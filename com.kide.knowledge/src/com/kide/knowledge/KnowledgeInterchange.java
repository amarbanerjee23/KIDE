package com.kide.knowledge;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public final class KnowledgeInterchange {
    private static final Pattern TURTLE = Pattern.compile(
            "^\\s*<([^>]+)>\\s+<([^>]+)>\\s+(?:<([^>]+)>|\"((?:\\\\.|[^\"])*)\")\\s*\\.\\s*$");

    public KnowledgeDataset parse(
            String document,
            KnowledgeFormat format,
            String datasetId,
            String scopeType,
            String scopeId,
            KnowledgeProvenance provenance) {
        Objects.requireNonNull(document, "document");
        List<KnowledgeTriple> triples = switch (Objects.requireNonNull(format, "format")) {
            case TURTLE -> parseTurtle(document);
            case RDF_XML -> parseRdfXml(document);
            case JSON_LD -> parseJsonLd(document);
        };
        return new KnowledgeDataset(
                KnowledgeDataset.CURRENT_SCHEMA, datasetId, scopeType, scopeId, provenance, triples);
    }

    public String write(KnowledgeDataset dataset, KnowledgeFormat format) {
        return switch (Objects.requireNonNull(format, "format")) {
            case TURTLE -> writeTurtle(dataset);
            case RDF_XML -> writeRdfXml(dataset);
            case JSON_LD -> writeJsonLd(dataset);
        };
    }

    private static List<KnowledgeTriple> parseTurtle(String document) {
        List<KnowledgeTriple> result = new ArrayList<>();
        int lineNumber = 0;
        for (String line : document.split("\\R")) {
            lineNumber++;
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) continue;
            Matcher matcher = TURTLE.matcher(line);
            if (!matcher.matches()) {
                throw new IllegalArgumentException("unsupported Turtle statement at line " + lineNumber);
            }
            KnowledgeTerm object = matcher.group(3) != null
                    ? KnowledgeTerm.iri(matcher.group(3))
                    : KnowledgeTerm.literal(unescapeLiteral(matcher.group(4)));
            result.add(new KnowledgeTriple(matcher.group(1), matcher.group(2), object));
        }
        return result;
    }

    private static List<KnowledgeTriple> parseJsonLd(String document) {
        JsonElement parsed = JsonParser.parseString(document);
        JsonArray graph;
        if (parsed.isJsonObject() && parsed.getAsJsonObject().has("@graph")) {
            graph = parsed.getAsJsonObject().getAsJsonArray("@graph");
        } else if (parsed.isJsonArray()) {
            graph = parsed.getAsJsonArray();
        } else {
            throw new IllegalArgumentException("JSON-LD document requires @graph");
        }

        List<KnowledgeTriple> result = new ArrayList<>();
        for (JsonElement nodeElement : graph) {
            if (!nodeElement.isJsonObject()) throw new IllegalArgumentException("JSON-LD node must be an object");
            JsonObject node = nodeElement.getAsJsonObject();
            String subject = requiredJsonString(node, "@id");
            for (Map.Entry<String, JsonElement> entry : node.entrySet()) {
                if ("@id".equals(entry.getKey()) || "@context".equals(entry.getKey())) continue;
                if ("@type".equals(entry.getKey())) {
                    for (JsonElement value : values(entry.getValue())) {
                        result.add(new KnowledgeTriple(subject, KnowledgeVocabulary.RDF_TYPE,
                                KnowledgeTerm.iri(value.getAsString())));
                    }
                    continue;
                }
                String predicate = entry.getKey();
                if (!java.net.URI.create(predicate).isAbsolute()) {
                    throw new IllegalArgumentException("JSON-LD predicates must be expanded absolute IRIs");
                }
                for (JsonElement value : values(entry.getValue())) {
                    if (value.isJsonObject() && value.getAsJsonObject().has("@id")) {
                        result.add(new KnowledgeTriple(subject, predicate,
                                KnowledgeTerm.iri(requiredJsonString(value.getAsJsonObject(), "@id"))));
                    } else if (value.isJsonPrimitive()) {
                        result.add(new KnowledgeTriple(subject, predicate,
                                KnowledgeTerm.literal(value.getAsString())));
                    } else {
                        throw new IllegalArgumentException("unsupported JSON-LD value");
                    }
                }
            }
        }
        return result;
    }

    private static List<KnowledgeTriple> parseRdfXml(String document) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            var dom = factory.newDocumentBuilder().parse(new InputSource(new StringReader(document)));
            List<KnowledgeTriple> result = new ArrayList<>();
            var descriptions = dom.getElementsByTagNameNS(KnowledgeVocabulary.RDF, "Description");
            for (int i = 0; i < descriptions.getLength(); i++) {
                Element description = (Element) descriptions.item(i);
                String subject = description.getAttributeNS(KnowledgeVocabulary.RDF, "about");
                if (subject == null || subject.isBlank()) {
                    throw new IllegalArgumentException("rdf:Description requires rdf:about");
                }
                for (var child = description.getFirstChild(); child != null; child = child.getNextSibling()) {
                    if (!(child instanceof Element property)) continue;
                    String ns = property.getNamespaceURI();
                    String local = property.getLocalName();
                    if (ns == null || local == null) {
                        throw new IllegalArgumentException("RDF/XML property must be namespace-qualified");
                    }
                    String predicate = ns + local;
                    String resource = property.getAttributeNS(KnowledgeVocabulary.RDF, "resource");
                    result.add(new KnowledgeTriple(subject, predicate,
                            resource == null || resource.isBlank()
                                    ? KnowledgeTerm.literal(property.getTextContent())
                                    : KnowledgeTerm.iri(resource)));
                }
            }
            return result;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("RDF/XML document is invalid", e);
        }
    }

    private static String writeTurtle(KnowledgeDataset dataset) {
        StringBuilder out = new StringBuilder();
        out.append("# KIDE knowledge schema ").append(dataset.schemaVersion()).append('\n');
        for (KnowledgeTriple triple : dataset.triples()) {
            out.append('<').append(triple.subject()).append("> <")
                    .append(triple.predicate()).append("> ");
            if (triple.object().literal()) {
                out.append('"').append(escapeLiteral(triple.object().value())).append('"');
            } else {
                out.append('<').append(triple.object().value()).append('>');
            }
            out.append(" .\n");
        }
        return out.toString();
    }

    private static String writeJsonLd(KnowledgeDataset dataset) {
        Map<String, JsonObject> nodes = new LinkedHashMap<>();
        for (KnowledgeTriple triple : dataset.triples()) {
            JsonObject node = nodes.computeIfAbsent(triple.subject(), key -> {
                JsonObject created = new JsonObject();
                created.addProperty("@id", key);
                return created;
            });
            String key = KnowledgeVocabulary.RDF_TYPE.equals(triple.predicate())
                    ? "@type" : triple.predicate();
            JsonArray values = node.has(key) ? node.getAsJsonArray(key) : new JsonArray();
            if (!node.has(key)) node.add(key, values);
            if (triple.object().literal()) {
                values.add(triple.object().value());
            } else if ("@type".equals(key)) {
                values.add(triple.object().value());
            } else {
                JsonObject ref = new JsonObject();
                ref.addProperty("@id", triple.object().value());
                values.add(ref);
            }
        }
        JsonObject root = new JsonObject();
        JsonObject context = new JsonObject();
        context.addProperty("kide", KnowledgeVocabulary.KIDE);
        root.add("@context", context);
        JsonArray graph = new JsonArray();
        nodes.values().forEach(graph::add);
        root.add("@graph", graph);
        return KnowledgeCodec.GSON.toJson(root);
    }

    private static String writeRdfXml(KnowledgeDataset dataset) {
        StringBuilder out = new StringBuilder();
        out.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
                .append("<rdf:RDF xmlns:rdf=\"").append(KnowledgeVocabulary.RDF).append("\">\n");
        Map<String, List<KnowledgeTriple>> bySubject = new LinkedHashMap<>();
        for (KnowledgeTriple triple : dataset.triples()) {
            bySubject.computeIfAbsent(triple.subject(), ignored -> new ArrayList<>()).add(triple);
        }
        for (var entry : bySubject.entrySet()) {
            out.append("  <rdf:Description rdf:about=\"").append(xml(entry.getKey())).append("\">\n");
            int index = 0;
            for (KnowledgeTriple triple : entry.getValue()) {
                Name name = split(triple.predicate());
                String prefix = "p" + (++index);
                out.append("    <").append(prefix).append(':').append(name.local)
                        .append(" xmlns:").append(prefix).append("=\"").append(xml(name.namespace)).append("\"");
                if (triple.object().literal()) {
                    out.append('>').append(xml(triple.object().value())).append("</")
                            .append(prefix).append(':').append(name.local).append(">\n");
                } else {
                    out.append(" rdf:resource=\"").append(xml(triple.object().value())).append("\"/>\n");
                }
            }
            out.append("  </rdf:Description>\n");
        }
        out.append("</rdf:RDF>\n");
        return out.toString();
    }

    private static Name split(String iri) {
        int hash = iri.lastIndexOf('#');
        int slash = iri.lastIndexOf('/');
        int split = Math.max(hash, slash);
        if (split < 0 || split + 1 >= iri.length()) {
            throw new IllegalArgumentException("predicate IRI cannot be represented in RDF/XML");
        }
        String local = iri.substring(split + 1).replaceAll("[^A-Za-z0-9_.-]", "_");
        if (!Character.isLetter(local.charAt(0)) && local.charAt(0) != '_') local = "_" + local;
        return new Name(iri.substring(0, split + 1), local);
    }

    private static List<JsonElement> values(JsonElement value) {
        if (value.isJsonArray()) {
            List<JsonElement> result = new ArrayList<>();
            value.getAsJsonArray().forEach(result::add);
            return result;
        }
        return List.of(value);
    }

    private static String requiredJsonString(JsonObject object, String key) {
        if (!object.has(key) || !object.get(key).isJsonPrimitive()) {
            throw new IllegalArgumentException("JSON-LD field " + key + " is required");
        }
        String value = object.get(key).getAsString();
        if (value.isBlank()) throw new IllegalArgumentException("JSON-LD field " + key + " is blank");
        return value;
    }

    private static String escapeLiteral(String value) {
        return value.replace("\\", "\\\\").replace(""", "\\"")
                .replace("\n", "\\n").replace("\r", "\\r");
    }

    private static String unescapeLiteral(String value) {
        StringBuilder out = new StringBuilder();
        boolean escape = false;
        for (char c : value.toCharArray()) {
            if (escape) {
                out.append(c == 'n' ? '\n' : c == 'r' ? '\r' : c);
                escape = false;
            } else if (c == '\\') {
                escape = true;
            } else {
                out.append(c);
            }
        }
        if (escape) throw new IllegalArgumentException("unterminated Turtle escape");
        return out.toString();
    }

    private static String xml(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace(""", "&quot;");
    }

    private record Name(String namespace, String local) { }
}
