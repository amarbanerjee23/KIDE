package com.kide.glsp;

import org.eclipse.glsp.server.diagram.DiagramConfiguration;
import org.eclipse.glsp.server.di.MultiBinding;
import org.eclipse.glsp.server.emf.EMFIdGenerator;
import org.eclipse.glsp.server.emf.EMFSourceModelStorage;
import org.eclipse.glsp.server.emf.idgen.FragmentIdGenerator;
import org.eclipse.glsp.server.emf.notation.EMFSemanticIdConverter;
import org.eclipse.glsp.server.emf.notation.EMFNotationDiagramModule;
import org.eclipse.glsp.server.emf.notation.SemanticFragmentIdConverter;
import org.eclipse.glsp.server.features.core.model.GModelFactory;
import org.eclipse.glsp.server.features.toolpalette.ToolPaletteItemProvider;
import org.eclipse.glsp.server.features.validation.ModelValidator;
import org.eclipse.glsp.server.operations.OperationHandler;

public final class MncDiagramModule extends EMFNotationDiagramModule {
    @Override protected Class<? extends DiagramConfiguration> bindDiagramConfiguration() {
        return MncDiagramConfiguration.class;
    }
    @Override protected Class<? extends EMFSourceModelStorage> bindSourceModelStorage() {
        return KideXtextNotationSourceModelStorage.class;
    }
    @Override protected Class<? extends GModelFactory> bindGModelFactory() {
        return MncGModelFactory.class;
    }
    @Override protected Class<? extends EMFIdGenerator> bindEMFIdGenerator() {
        return FragmentIdGenerator.class;
    }
    @Override protected Class<? extends EMFSemanticIdConverter> bindEMFSemanticIdConverter() {
        return SemanticFragmentIdConverter.class;
    }
    @Override protected Class<? extends ToolPaletteItemProvider> bindToolPaletteItemProvider() {
        return KideToolPaletteItemProvider.class;
    }
    @Override protected Class<? extends ModelValidator> bindModelValidator() {
        return KideGlspModelValidator.class;
    }
    @Override protected void configureOperationHandlers(MultiBinding<OperationHandler<?>> binding) {
        super.configureOperationHandlers(binding);
        binding.add(CreateMncInterfaceHandler.class);
        binding.add(CreateMncControlNodeHandler.class);
        binding.add(KideCreateEdgeHandler.class);
        binding.add(KideDeleteHandler.class);
        binding.add(KideLabelEditHandler.class);
    }
    @Override public String getDiagramType() { return KideDiagramTypes.MNC_DIAGRAM; }
}
