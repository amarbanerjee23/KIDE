package com.kide.codegen;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.validation.CheckMode;
import org.eclipse.xtext.validation.IResourceValidator;
import org.eclipse.xtext.validation.Issue;

import com.google.inject.Injector;
import com.kide.krl.dsl.KrlStandaloneSetup;
import com.kide.krl.dsl.krl.KnowledgeModel;
import com.kide.knowledge.KnowledgeDataset;

public final class ProjectKrlGenerationEngine {
    private static volatile Injector injector;

    public GenerationOutput generate(
            Path projectRoot,
            String krlPath,
            KnowledgeDataset knowledge,
            GenerationContext context) {
        Path root = safeRoot(projectRoot);
        Path source = safeKrl(root, krlPath);

        Injector runtime = injector();
        XtextResourceSet resources = runtime.getInstance(XtextResourceSet.class);
        Resource resource = resources.getResource(
                URI.createFileURI(source.toAbsolutePath().normalize().toString()), true);

        if (!resource.getErrors().isEmpty()) {
            Resource.Diagnostic first = resource.getErrors().get(0);
            throw new GenerationException(
                    "KRL parse/link error at line " + first.getLine() + ": " + first.getMessage());
        }

        IResourceValidator validator = runtime.getInstance(IResourceValidator.class);
        List<Issue> issues = validator.validate(resource, CheckMode.ALL, CancelIndicator.NullImpl);
        Issue error = issues.stream()
                .filter(issue -> issue.getSeverity() == org.eclipse.xtext.diagnostics.Severity.ERROR)
                .findFirst()
                .orElse(null);
        if (error != null) {
            throw new GenerationException(
                    "KRL validation failed at line " + error.getLineNumber()
                            + ": " + error.getMessage());
        }

        if (resource.getContents().size() != 1
                || !(resource.getContents().get(0) instanceof KnowledgeModel model)) {
            throw new GenerationException("KRL source must contain exactly one knowledge model");
        }
        return new KrlGenerationService(GenerationTargetRegistry.defaults())
                .generate(model, knowledge, context);
    }

    private static Injector injector() {
        Injector current = injector;
        if (current != null) return current;
        synchronized (ProjectKrlGenerationEngine.class) {
            current = injector;
            if (current == null) {
                current = new KrlStandaloneSetup().createInjectorAndDoEMFRegistration();
                injector = current;
            }
            return current;
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

    private static Path safeKrl(Path root, String relative) {
        if (relative == null || relative.isBlank()) {
            throw new IllegalArgumentException("krlPath is required");
        }
        String safe = GenerationPaths.requireSafeRelative(relative);
        if (!safe.endsWith(".krl")) {
            throw new IllegalArgumentException("generation source must be a .krl file");
        }
        Path source = root.resolve(safe).normalize();
        if (!source.startsWith(root)
                || !Files.isRegularFile(source, LinkOption.NOFOLLOW_LINKS)
                || Files.isSymbolicLink(source)) {
            throw new IllegalArgumentException("krlPath is not a safe project file");
        }
        return source;
    }
}
