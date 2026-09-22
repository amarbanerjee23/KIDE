package com.kide.codegen;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.kide.krl.dsl.krl.Condition;
import com.kide.krl.dsl.krl.Fact;
import com.kide.krl.dsl.krl.KnowledgeModel;
import com.kide.krl.dsl.krl.Parameter;
import com.kide.krl.dsl.krl.Pattern;
import com.kide.krl.dsl.krl.Query;
import com.kide.krl.dsl.krl.Term;
import com.kide.krl.dsl.krl.Value;
import com.kide.knowledge.KnowledgeDataset;
import com.kide.knowledge.KnowledgeTriple;

public final class KrlQueryEngine {
    private static final int MAX_PATTERNS = 64;
    private static final int MAX_RESULTS = 10_000;

    public List<Map<String, SemanticValue>> execute(
            KnowledgeModel model,
            Query query,
            List<Value> arguments,
            KnowledgeDataset knowledge) {
        if (query.getPatterns().isEmpty() || query.getPatterns().size() > MAX_PATTERNS) {
            throw new GenerationException("KRL query must contain 1.." + MAX_PATTERNS + " patterns");
        }
        if (arguments.size() != query.getParameters().size()) {
            throw new GenerationException("KRL query argument count does not match parameters");
        }

        KrlNamespaceResolver namespaces = new KrlNamespaceResolver(model);
        List<KnowledgeTriple> collectedTriples = new ArrayList<>(knowledge.triples());
        model.getDeclarations().stream()
                .filter(Fact.class::isInstance)
                .map(Fact.class::cast)
                .forEach(fact -> collectedTriples.add(new KnowledgeTriple(
                        namespaces.resolve(fact.getSubject()),
                        namespaces.resolve(fact.getPredicate()),
                        KrlValues.toKnowledge(fact.getObject()))));
        List<KnowledgeTriple> triples =
                collectedTriples.stream().distinct().sorted().toList();

        Map<String, SemanticValue> initial = new LinkedHashMap<>();
        for (int i = 0; i < query.getParameters().size(); i++) {
            Parameter parameter = query.getParameters().get(i);
            SemanticValue argument = KrlValues.requireType(
                    KrlValues.direct(arguments.get(i)),
                    parameter.getType(),
                    "query parameter " + parameter.getName());
            initial.put(parameter.getName(), argument);
        }

        List<Map<String, SemanticValue>> rows = List.of(Map.copyOf(initial));
        for (Pattern pattern : query.getPatterns()) {
            List<Map<String, SemanticValue>> next = new ArrayList<>();
            for (Map<String, SemanticValue> row : rows) {
                for (KnowledgeTriple triple : triples) {
                    Map<String, SemanticValue> candidate = new LinkedHashMap<>(row);
                    if (match(pattern.getSubject(), SemanticValue.iri(triple.subject()),
                            candidate, namespaces, false)
                            && match(pattern.getPredicate(), SemanticValue.iri(triple.predicate()),
                            candidate, namespaces, false)
                            && match(pattern.getObject(), KrlValues.fromKnowledge(triple.object()),
                            candidate, namespaces, true)) {
                        next.add(Map.copyOf(candidate));
                        if (next.size() > MAX_RESULTS) {
                            throw new GenerationException(
                                    "KRL query exceeds " + MAX_RESULTS + " intermediate results");
                        }
                    }
                }
            }
            rows = next;
            if (rows.isEmpty()) break;
        }

        rows = rows.stream()
                .filter(row -> query.getConditions().stream().allMatch(c -> test(c, row)))
                .map(row -> selected(query, row))
                .distinct()
                .sorted(Comparator.comparing(KrlQueryEngine::canonical))
                .limit(MAX_RESULTS)
                .toList();
        return List.copyOf(rows);
    }

    private static boolean match(
            Term term,
            SemanticValue actual,
            Map<String, SemanticValue> row,
            KrlNamespaceResolver namespaces,
            boolean objectPosition) {
        if (term.getVariable() != null) {
            SemanticValue existing = row.get(term.getVariable());
            if (existing == null) {
                row.put(term.getVariable(), actual);
                return true;
            }
            return same(existing, actual);
        }
        if (term.getIri() != null) {
            return same(SemanticValue.iri(term.getIri()), actual);
        }
        if (term.getLiteral() != null) {
            if (!objectPosition) return false;
            return same(SemanticValue.literal(term.getLiteral()), actual);
        }
        if (term.getName() != null) {
            return same(SemanticValue.iri(namespaces.resolve(term.getName())), actual);
        }
        return false;
    }

    private static boolean test(Condition condition, Map<String, SemanticValue> row) {
        SemanticValue left = row.get(condition.getLeft().getName());
        if (left == null) return false;
        SemanticValue right = KrlValues.direct(condition.getRight());
        String operator = condition.getOperator().getLiteral();
        if ("==".equals(operator)) return same(left, right);
        if ("!=".equals(operator)) return !same(left, right);
        if (left.iri() || right.iri()) return false;
        try {
            BigDecimal a = new BigDecimal(left.value());
            BigDecimal b = new BigDecimal(right.value());
            int comparison = a.compareTo(b);
            return switch (operator) {
                case "<" -> comparison < 0;
                case "<=" -> comparison <= 0;
                case ">" -> comparison > 0;
                case ">=" -> comparison >= 0;
                default -> false;
            };
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static Map<String, SemanticValue> selected(
            Query query, Map<String, SemanticValue> row) {
        Map<String, SemanticValue> selected = new LinkedHashMap<>();
        query.getSelections().forEach(variable -> {
            SemanticValue value = row.get(variable.getName());
            if (value == null) {
                throw new GenerationException(
                        "KRL query selects unbound variable ?" + variable.getName());
            }
            selected.put(variable.getName(), value);
        });
        return Map.copyOf(selected);
    }

    private static boolean same(SemanticValue a, SemanticValue b) {
        return a.iri() == b.iri() && a.value().equals(b.value());
    }

    private static String canonical(Map<String, SemanticValue> row) {
        StringBuilder value = new StringBuilder();
        new java.util.TreeMap<>(row).forEach((key, term) ->
                value.append(key).append('=').append(term.iri() ? 'I' : 'L')
                        .append(':').append(term.value()).append('\n'));
        return value.toString();
    }
}
