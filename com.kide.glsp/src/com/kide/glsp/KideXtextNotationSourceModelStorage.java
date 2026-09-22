package com.kide.glsp;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.glsp.server.actions.SaveModelAction;
import org.eclipse.glsp.server.emf.model.notation.Diagram;
import org.eclipse.glsp.server.emf.model.notation.NotationFactory;
import org.eclipse.glsp.server.emf.model.notation.NotationPackage;
import org.eclipse.glsp.server.emf.model.notation.SemanticElementReference;
import org.eclipse.glsp.server.emf.notation.EMFNotationSourceModelStorage;
import org.eclipse.glsp.server.features.core.model.RequestModelAction;
import org.eclipse.glsp.server.types.GLSPServerException;
import org.eclipse.glsp.server.utils.ClientOptionsUtil;

import com.google.inject.Inject;
import com.kide.enterprise.modelrepo.ModelPath;
import com.kide.enterprise.modelrepo.ModelSnapshot;
import com.kide.enterprise.modelrepo.ModelTransaction;
import com.kide.languageserver.KideResourceServiceProviderRegistryProvider;

public final class KideXtextNotationSourceModelStorage extends EMFNotationSourceModelStorage {
    private static final String MISSING_ETAG = "0".repeat(64);

    @Inject
    protected KideGlspWorkspace workspace;

    // Optimistic revisions are repository identities, not EMF URI spellings.
    private final Map<ModelPath, String> loadedEtags = new HashMap<>();

    @Override
    protected ResourceSet setupResourceSet(ResourceSet resourceSet) {
        new KideResourceServiceProviderRegistryProvider().get();
        activityDiagramModel.ActivityDiagramModelPackage.eINSTANCE.eClass();
        mncModel.MncModelPackage.eINSTANCE.eClass();
        NotationPackage.eINSTANCE.eClass();

        ResourceSet result = super.setupResourceSet(resourceSet);
        copyGlobalFactory(result, "activity");
        copyGlobalFactory(result, "mncspec");
        result.getResourceFactoryRegistry().getExtensionToFactoryMap()
                .put("notation", new XMIResourceFactoryImpl());
        return result;
    }

    private static void copyGlobalFactory(ResourceSet resourceSet, String extension) {
        Object factory = Resource.Factory.Registry.INSTANCE
                .getExtensionToFactoryMap().get(extension);
        if (factory == null) {
            throw new GLSPServerException(
                    "No production Xtext resource factory for ." + extension);
        }
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
                .put(extension, factory);
    }

    @Override
    protected void loadSemanticModel(
            ResourceSet resourceSet, URI sourceURI, RequestModelAction action) {
        Path source = workspace.requireProjectPath(sourceURI.toFileString());
        ModelPath modelPath = workspace.modelPath(source);
        ModelSnapshot snapshot = workspace.repository().read(modelPath)
                .orElseThrow(() -> new GLSPServerException(
                        "Graphical source model does not exist: " + modelPath.value()));
        loadedEtags.put(modelPath, snapshot.revision().etag());
        super.loadSemanticModel(resourceSet, sourceURI, action);
    }

    @Override
    protected void loadNotationModel(
            ResourceSet resourceSet, URI sourceURI, RequestModelAction action) {
        URI notationURI = deriveNotationModelURI(sourceURI);
        Path notationPath = workspace.requireProjectPath(notationURI.toFileString());
        ModelPath notationModelPath = workspace.modelPath(notationPath);
        if (Files.exists(notationPath)) {
            ModelSnapshot snapshot = workspace.repository().read(notationModelPath)
                    .orElseThrow(() -> new GLSPServerException(
                            "Graphical notation model could not be read: "
                            + notationModelPath.value()));
            loadedEtags.put(notationModelPath, snapshot.revision().etag());
            super.loadNotationModel(resourceSet, sourceURI, action);
            return;
        }

        Resource resource = resourceSet.createResource(notationURI);
        Diagram diagram = NotationFactory.eINSTANCE.createDiagram();
        diagram.setDiagramType(
                ClientOptionsUtil.getDiagramType(action.getOptions()).orElse(""));
        EObject semantic = modelState.getSemanticModel();
        SemanticElementReference reference =
                NotationFactory.eINSTANCE.createSemanticElementReference();
        reference.setResolvedSemanticElement(semantic);
        reference.setElementId(EcoreUtil.getURI(semantic).fragment());
        diagram.setSemanticElement(reference);
        resource.getContents().add(diagram);
        modelState.setNotationModel(diagram);
        loadedEtags.put(notationModelPath, MISSING_ETAG);
    }

    @Override
    public void saveSourceModel(SaveModelAction action) {
        ResourceSet resourceSet = modelState.getResourceSet();
        if (resourceSet == null) {
            throw new GLSPServerException("No graphical source model is loaded");
        }

        if (action.getFileUri().isPresent()) {
            throw new GLSPServerException(
                    "Graphical Save As is not supported; save the active canonical model instead");
        }

        Resource semanticResource = modelState.getSemanticModel().eResource();
        Resource notationResource = modelState.getNotationModel().eResource();
        if (semanticResource == null || notationResource == null) {
            throw new GLSPServerException(
                    "Graphical semantic and notation resources must both be loaded");
        }

        try (ModelTransaction tx = workspace.repository().beginTransaction()) {
            Map<URI, ModelPath> savedPaths = new HashMap<>();
            for (Resource resource : java.util.List.of(
                    semanticResource, notationResource)) {
                URI uri = resource.getURI();
                if (uri == null || !uri.isFile()) {
                    throw new GLSPServerException(
                            "Graphical model resources must use authorized project files");
                }
                Path path = workspace.requireProjectPath(uri.toFileString());
                ModelPath modelPath = workspace.modelPath(path);
                String expectedEtag = loadedEtags.get(modelPath);
                if (expectedEtag == null) {
                    throw new GLSPServerException(
                            "Graphical resource revision was not captured at load: "
                            + modelPath.value());
                }

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                resource.save(out, Map.of());
                tx.write(modelPath, out.toByteArray(), expectedEtag);
                savedPaths.put(uri, modelPath);
            }
            tx.commit();

            for (Map.Entry<URI, ModelPath> entry : savedPaths.entrySet()) {
                ModelSnapshot snapshot = workspace.repository()
                        .read(entry.getValue()).orElseThrow();
                loadedEtags.put(entry.getValue(), snapshot.revision().etag());
                Resource resource = resourceSet.getResource(entry.getKey(), false);
                if (resource != null) resource.setModified(false);
            }
        } catch (com.kide.enterprise.modelrepo.RevisionConflictException conflict) {
            throw new GLSPServerException(
                    "Graphical save rejected because the project revision changed",
                    conflict);
        } catch (Exception failure) {
            if (failure instanceof GLSPServerException glsp) throw glsp;
            throw new GLSPServerException(
                    "Could not persist graphical model transaction", failure);
        }
    }
}
