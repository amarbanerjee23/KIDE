package com.kide.synthesis;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.serializer.ISerializer;

import com.capability.CapabilityStandaloneSetup;
import com.dml.dsl.DmlStandaloneSetup;
import com.google.inject.Injector;
import com.kide.knowledge.KnowledgeDataset;
import com.mncml.dsl.MncStandaloneSetup;
import com.operation.dsl.OperationStandaloneSetup;
import com.smr.activity.dsl.ActivityDiagramStandaloneSetup;

import activityDiagramModel.ActivityDiagram;

public final class ProjectSynthesisEngine {
    private static final Set<String> SUPPORTED_EXTENSIONS =
            Set.of("dml", "op", "mncspec", "cap", "activity");
    private static volatile Runtime runtime;

    public ProjectSynthesisOutput synthesize(
            Path projectRoot,
            String activityModelPath,
            KnowledgeDataset knowledge) {
        PreparedProject prepared = prepare(projectRoot, activityModelPath);
        SynthesisResult result =
                new DeterministicSynthesisService().synthesize(prepared.diagram(), knowledge);
        String generated = "";
        if (result.status() == SynthesisStatus.SUCCESS) {
            generated = prepared.runtime().mncSerializer.serialize(result.controllerModel());
            if (generated == null || generated.isBlank()) {
                throw new ProjectSynthesisException("generated MNC serialization is empty");
            }
        }
        return new ProjectSynthesisOutput(result, generated);
    }

    public ReconfigurationResult reconfigure(
            Path projectRoot,
            String activityModelPath,
            KnowledgeDataset knowledge,
            SynthesisPlan previousPlan,
            ReconfigurationCause cause) {
        PreparedProject prepared = prepare(projectRoot, activityModelPath);
        return new DeterministicReconfigurationService().reconfigure(
                prepared.diagram(), knowledge, previousPlan, cause);
    }

    private static PreparedProject prepare(
            Path projectRoot,
            String activityModelPath) {
        Path root = safeRoot(projectRoot);
        Path target = safeModel(root, activityModelPath);
        if (!target.getFileName().toString().endsWith(".activity")) {
            throw new IllegalArgumentException("synthesis source must be an .activity model");
        }

        Runtime rt = runtime();
        XtextResourceSet resourceSet = rt.activityInjector.getInstance(XtextResourceSet.class);
        resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);

        try {
            List<Path> projectFiles;
            try (var stream = Files.walk(root)) {
                projectFiles = stream
                        .filter(path -> Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS))
                        .filter(path -> !Files.isSymbolicLink(path))
                        .filter(ProjectSynthesisEngine::supported)
                        .sorted(Comparator.comparing(path -> root.relativize(path).toString()))
                        .toList();
            }
            for (Path path : projectFiles) {
                if (!path.equals(target)) load(resourceSet, path);
            }
            Resource source = load(resourceSet, target);
            EcoreUtil.resolveAll(resourceSet);
            rejectRelevantResourceErrors(source);

            if (source.getContents().size() != 1
                    || !(source.getContents().get(0) instanceof ActivityDiagram diagram)) {
                throw new ProjectSynthesisException(
                        "Activity model does not contain exactly one ActivityDiagram root");
            }
            return new PreparedProject(rt, diagram);
        } catch (IOException e) {
            throw new ProjectSynthesisException("project synthesis input could not be read", e);
        }
    }

    private static Resource load(XtextResourceSet set, Path path) {
        URI uri = URI.createFileURI(path.toAbsolutePath().normalize().toString());
        return set.getResource(uri, true);
    }

    private static void rejectRelevantResourceErrors(Resource source) {
        Set<Resource> relevant =
                Collections.newSetFromMap(new IdentityHashMap<>());
        ArrayDeque<Resource> queue = new ArrayDeque<>();
        relevant.add(source);
        queue.add(source);

        while (!queue.isEmpty()) {
            Resource resource = queue.removeFirst();
            if (!resource.getErrors().isEmpty()) {
                Resource.Diagnostic first = resource.getErrors().get(0);
                String location = resource.getURI() == null
                        ? "project model" : resource.getURI().lastSegment();
                throw new ProjectSynthesisException(
                        "Project model validation failed in " + location
                                + " at line " + first.getLine());
            }
            for (EObject root : resource.getContents()) {
                enqueueReferencedResources(root, relevant, queue);
                for (var iterator = root.eAllContents(); iterator.hasNext();) {
                    enqueueReferencedResources(iterator.next(), relevant, queue);
                }
            }
        }
    }

    private static void enqueueReferencedResources(
            EObject object,
            Set<Resource> relevant,
            ArrayDeque<Resource> queue) {
        for (EObject referenced : object.eCrossReferences()) {
            Resource resource = referenced.eResource();
            if (resource != null && relevant.add(resource)) {
                queue.addLast(resource);
            }
        }
    }

    private static Path safeRoot(Path projectRoot) {
        if (projectRoot == null) throw new IllegalArgumentException("projectRoot is required");
        Path root = projectRoot.toAbsolutePath().normalize();
        if (!Files.isDirectory(root, LinkOption.NOFOLLOW_LINKS)
                || Files.isSymbolicLink(root)) {
            throw new IllegalArgumentException("projectRoot must be a real directory");
        }
        return root;
    }

    private static Path safeModel(Path root, String relative) {
        if (relative == null || relative.isBlank()) {
            throw new IllegalArgumentException("activityModelPath is required");
        }
        String value = relative.replace('\\', '/').trim();
        if (value.startsWith("/") || value.startsWith(".kide/")
                || value.equals(".kide") || value.contains("../")
                || value.equals("..") || value.contains("/./") || value.endsWith("/..")) {
            throw new IllegalArgumentException("activityModelPath escapes project root");
        }
        Path resolved = root.resolve(value).normalize();
        if (!resolved.startsWith(root)
                || !Files.isRegularFile(resolved, LinkOption.NOFOLLOW_LINKS)
                || Files.isSymbolicLink(resolved)) {
            throw new IllegalArgumentException("activityModelPath is not a safe project file");
        }
        return resolved;
    }

    private static boolean supported(Path path) {
        String name = path.getFileName().toString();
        int dot = name.lastIndexOf('.');
        return dot >= 0 && dot + 1 < name.length()
                && SUPPORTED_EXTENSIONS.contains(name.substring(dot + 1));
    }

    private static Runtime runtime() {
        Runtime current = runtime;
        if (current != null) return current;
        synchronized (ProjectSynthesisEngine.class) {
            current = runtime;
            if (current != null) return current;

            DmlStandaloneSetup.doSetup();
            new OperationStandaloneSetup().createInjectorAndDoEMFRegistration();
            Injector mnc = new MncStandaloneSetup().createInjectorAndDoEMFRegistration();
            new CapabilityStandaloneSetup().createInjectorAndDoEMFRegistration();
            Injector activity =
                    new ActivityDiagramStandaloneSetup().createInjectorAndDoEMFRegistration();

            current = new Runtime(
                    activity,
                    mnc.getInstance(ISerializer.class));
            runtime = current;
            return current;
        }
    }

    private record Runtime(Injector activityInjector, ISerializer mncSerializer) { }
    private record PreparedProject(Runtime runtime, ActivityDiagram diagram) { }
}
