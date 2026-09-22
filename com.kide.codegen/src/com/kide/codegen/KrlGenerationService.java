package com.kide.codegen;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.kide.krl.dsl.krl.Binding;
import com.kide.krl.dsl.krl.KnowledgeModel;
import com.kide.krl.dsl.krl.Parameter;
import com.kide.krl.dsl.krl.QueryValue;
import com.kide.krl.dsl.krl.Target;
import com.kide.krl.dsl.krl.Template;
import com.kide.knowledge.KnowledgeDataset;

public final class KrlGenerationService {
    private static final int MAX_TARGETS = 1_000;

    private final GenerationTargetRegistry targets;
    private final KrlQueryEngine queries = new KrlQueryEngine();
    private final KrlTemplateRenderer renderer = new KrlTemplateRenderer();

    public KrlGenerationService(GenerationTargetRegistry targets) {
        this.targets = java.util.Objects.requireNonNull(targets, "targets");
    }

    public GenerationOutput generate(
            KnowledgeModel model,
            KnowledgeDataset knowledge,
            GenerationContext context) {
        java.util.Objects.requireNonNull(model, "model");
        java.util.Objects.requireNonNull(knowledge, "knowledge");
        java.util.Objects.requireNonNull(context, "context");

        List<Target> declarations = model.getDeclarations().stream()
                .filter(Target.class::isInstance)
                .map(Target.class::cast)
                .sorted(Comparator.comparing(Target::getOutputPath)
                        .thenComparing(Target::getName))
                .toList();
        if (declarations.size() > MAX_TARGETS) {
            throw new GenerationException("KRL model exceeds " + MAX_TARGETS + " generation targets");
        }

        List<GeneratedArtifact> artifacts = new ArrayList<>();
        Set<String> paths = new java.util.HashSet<>();
        for (Target declaration : declarations) {
            Template template = declaration.getTemplate();
            if (template == null) {
                throw new GenerationException(
                        "generation target " + declaration.getName() + " has no resolved template");
            }
            if (!template.getTargetType().equals(declaration.getTargetType())) {
                throw new GenerationException(
                        "generation target type does not match template for " + declaration.getName());
            }

            Map<String, Parameter> parameters = parameters(template);
            Map<String, Binding> bindings = bindings(declaration);
            if (!bindings.keySet().equals(parameters.keySet())) {
                throw new GenerationException(
                        "target " + declaration.getName()
                                + " must bind every template parameter exactly once");
            }

            Map<String, SemanticValue> values = new LinkedHashMap<>(context.reservedValues());
            for (var entry : parameters.entrySet()) {
                Parameter parameter = entry.getValue();
                Binding binding = bindings.get(entry.getKey());
                if (!binding.getType().getLiteral().equals(parameter.getType().getLiteral())) {
                    throw new GenerationException(
                            "binding type does not match template parameter " + parameter.getName());
                }
                values.put(parameter.getName(),
                        resolveBinding(model, knowledge, binding, parameter));
            }

            String rendered = renderer.render(template.getBody(), Map.copyOf(values));
            GenerationTarget target = targets.require(declaration.getTargetType());
            GeneratedArtifact artifact = target.generate(
                    declaration.getName(),
                    template.getName(),
                    declaration.getOutputPath(),
                    rendered);
            if (!paths.add(artifact.path())) {
                throw new GenerationException("duplicate generated output path " + artifact.path());
            }
            artifacts.add(artifact);
        }

        artifacts.sort(Comparator.comparing(GeneratedArtifact::path));
        GenerationManifest manifest = GenerationManifest.create(
                context, model.getName(), targets.versions(), artifacts);
        return new GenerationOutput(artifacts, manifest);
    }

    private SemanticValue resolveBinding(
            KnowledgeModel model,
            KnowledgeDataset knowledge,
            Binding binding,
            Parameter parameter) {
        if (binding.getLiteral() != null) {
            if (!KrlValues.directMatches(binding.getLiteral(), parameter.getType())) {
                throw new GenerationException(
                        "literal binding type does not match " + parameter.getName());
            }
            return KrlValues.requireType(
                    KrlValues.direct(binding.getLiteral()),
                    parameter.getType(),
                    "binding " + parameter.getName());
        }

        QueryValue query = binding.getQuery();
        if (query == null || query.getQuery() == null) {
            throw new GenerationException(
                    "binding " + parameter.getName() + " has no resolved value");
        }
        List<Map<String, SemanticValue>> rows =
                queries.execute(model, query.getQuery(), query.getArguments(), knowledge);
        if (rows.size() != 1) {
            throw new GenerationException(
                    "query binding " + parameter.getName()
                            + " must resolve to exactly one row; got " + rows.size());
        }
        SemanticValue value = rows.get(0).get(query.getVariable());
        if (value == null) {
            throw new GenerationException(
                    "query binding selects an unknown variable ?" + query.getVariable());
        }
        return KrlValues.requireType(
                value, parameter.getType(), "query binding " + parameter.getName());
    }

    private static Map<String, Parameter> parameters(Template template) {
        Map<String, Parameter> result = new LinkedHashMap<>();
        for (Parameter parameter : template.getParameters()) {
            if (result.put(parameter.getName(), parameter) != null) {
                throw new GenerationException(
                        "duplicate template parameter " + parameter.getName());
            }
        }
        return result;
    }

    private static Map<String, Binding> bindings(Target target) {
        Map<String, Binding> result = new LinkedHashMap<>();
        for (Binding binding : target.getBindings()) {
            if (result.put(binding.getName(), binding) != null) {
                throw new GenerationException(
                        "duplicate target binding " + binding.getName());
            }
        }
        return result;
    }
}
