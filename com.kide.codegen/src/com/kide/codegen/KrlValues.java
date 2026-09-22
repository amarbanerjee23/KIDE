package com.kide.codegen;

import java.math.BigDecimal;

import com.kide.krl.dsl.krl.KrlPackage;
import com.kide.krl.dsl.krl.Value;
import com.kide.krl.dsl.krl.ValueType;
import com.kide.knowledge.KnowledgeTerm;

final class KrlValues {
    private KrlValues() { }

    static SemanticValue direct(Value value) {
        if (value == null) throw new GenerationException("KRL value is required");
        if (value.eIsSet(KrlPackage.Literals.VALUE__STRING_VALUE)) {
            return new SemanticValue(
                    String.valueOf(value.eGet(KrlPackage.Literals.VALUE__STRING_VALUE)),
                    false, SemanticValue.Kind.STRING);
        }
        if (value.eIsSet(KrlPackage.Literals.VALUE__INTEGER_VALUE)) {
            return new SemanticValue(
                    String.valueOf(value.eGet(KrlPackage.Literals.VALUE__INTEGER_VALUE)),
                    false, SemanticValue.Kind.INTEGER);
        }
        if (value.eIsSet(KrlPackage.Literals.VALUE__DECIMAL_VALUE)) {
            Object raw = value.eGet(KrlPackage.Literals.VALUE__DECIMAL_VALUE);
            BigDecimal decimal = BigDecimal.valueOf(((Number) raw).doubleValue()).stripTrailingZeros();
            return new SemanticValue(decimal.toPlainString(), false, SemanticValue.Kind.DECIMAL);
        }
        if (value.eIsSet(KrlPackage.Literals.VALUE__BOOLEAN_VALUE)) {
            return new SemanticValue(
                    String.valueOf(value.eGet(KrlPackage.Literals.VALUE__BOOLEAN_VALUE)),
                    false, SemanticValue.Kind.BOOLEAN);
        }
        if (value.eIsSet(KrlPackage.Literals.VALUE__IRI_VALUE)) {
            return SemanticValue.iri(
                    String.valueOf(value.eGet(KrlPackage.Literals.VALUE__IRI_VALUE)));
        }
        throw new GenerationException("KRL value has no typed literal");
    }

    static SemanticValue fromKnowledge(KnowledgeTerm term) {
        return term.literal()
                ? SemanticValue.literal(term.value())
                : SemanticValue.iri(term.value());
    }

    static KnowledgeTerm toKnowledge(Value value) {
        SemanticValue semantic = direct(value);
        return semantic.iri()
                ? KnowledgeTerm.iri(semantic.value())
                : KnowledgeTerm.literal(semantic.value());
    }

    static SemanticValue requireType(SemanticValue value, ValueType type, String field) {
        String expected = type.getLiteral();
        if ("iri".equals(expected)) {
            if (!value.iri()) throw new GenerationException(field + " must resolve to an IRI");
            return new SemanticValue(value.value(), true, SemanticValue.Kind.IRI);
        }
        if (value.iri()) {
            throw new GenerationException(field + " must resolve to a literal " + expected);
        }
        try {
            return switch (expected) {
                case "string" -> new SemanticValue(
                        value.value(), false, SemanticValue.Kind.STRING);
                case "integer" -> new SemanticValue(
                        Long.toString(Long.parseLong(value.value())),
                        false, SemanticValue.Kind.INTEGER);
                case "decimal" -> new SemanticValue(
                        new BigDecimal(value.value()).stripTrailingZeros().toPlainString(),
                        false, SemanticValue.Kind.DECIMAL);
                case "boolean" -> new SemanticValue(
                        Boolean.toString(parseBoolean(value.value())),
                        false, SemanticValue.Kind.BOOLEAN);
                default -> throw new GenerationException("unsupported KRL value type " + expected);
            };
        } catch (NumberFormatException e) {
            throw new GenerationException(field + " does not match declared type " + expected, e);
        }
    }

    static boolean directMatches(Value value, ValueType type) {
        SemanticValue actual = direct(value);
        return actual.kind().name().equalsIgnoreCase(type.getLiteral());
    }

    private static boolean parseBoolean(String value) {
        if ("true".equals(value)) return true;
        if ("false".equals(value)) return false;
        throw new GenerationException("boolean literal must be true or false");
    }
}
