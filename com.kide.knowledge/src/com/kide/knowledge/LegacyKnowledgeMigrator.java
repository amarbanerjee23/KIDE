package com.kide.knowledge;

import java.io.StringReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;

public final class LegacyKnowledgeMigrator {
    private static final Map<String, String> TYPES = Map.of(
            "capability", KnowledgeVocabulary.CAPABILITY,
            "interface", KnowledgeVocabulary.INTERFACE,
            "behavior", KnowledgeVocabulary.BEHAVIOR,
            "interaction", KnowledgeVocabulary.INTERACTION,
            "workflow", KnowledgeVocabulary.WORKFLOW,
            "device", KnowledgeVocabulary.DEVICE);

    public KnowledgeDataset migrateXmi(
            String xml,
            String datasetId,
            String scopeType,
            String scopeId,
            String source,
            String authority,
            String importedBy,
            Clock clock) {
        try {
            XMLInputFactory factory = XMLInputFactory.newFactory();
            trySet(factory, XMLInputFactory.SUPPORT_DTD, false);
            trySet(factory, "javax.xml.stream.isSupportingExternalEntities", false);
            var reader = factory.createXMLStreamReader(new StringReader(xml));
            List<KnowledgeTriple> triples = new ArrayList<>();
            int sequence = 0;
            while (reader.hasNext()) {
                if (reader.next() != XMLStreamConstants.START_ELEMENT) continue;
                String local = reader.getLocalName().toLowerCase(Locale.ROOT);
                String matched = TYPES.keySet().stream()
                        .filter(local::contains).findFirst().orElse(null);
                if (matched == null) continue;
                String name = attribute(reader, "name");
                if (name == null || name.isBlank()) name = attribute(reader, "id");
                if (name == null || name.isBlank()) name = matched + "-" + (++sequence);
                String subject = "urn:kide:legacy:" + matched + ":"
                        + URLEncoder.encode(name, StandardCharsets.UTF_8).replace("+", "%20");
                triples.add(new KnowledgeTriple(subject, KnowledgeVocabulary.RDF_TYPE,
                        KnowledgeTerm.iri(TYPES.get(matched))));
                triples.add(new KnowledgeTriple(subject, KnowledgeVocabulary.LABEL,
                        KnowledgeTerm.literal(name)));
            }
            return new KnowledgeDataset(
                    KnowledgeDataset.CURRENT_SCHEMA,
                    datasetId,
                    scopeType,
                    scopeId,
                    new KnowledgeProvenance(
                            source, authority, importedBy, clock.millis(), "LEGACY_XMI"),
                    triples);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("legacy knowledge XMI is malformed", e);
        }
    }

    private static String attribute(javax.xml.stream.XMLStreamReader reader, String localName) {
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            if (localName.equalsIgnoreCase(reader.getAttributeLocalName(i))) {
                return reader.getAttributeValue(i);
            }
        }
        return null;
    }

    private static void trySet(XMLInputFactory factory, String property, Object value) {
        try { factory.setProperty(property, value); } catch (IllegalArgumentException ignored) { }
    }
}
