package com.kide.knowledge;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;

public final class DefaultKnowledgeShapes {
    private DefaultKnowledgeShapes() { }

    public static KnowledgeDataset create(String scopeId, Clock clock) {
        List<KnowledgeTriple> triples = new ArrayList<>();
        for (String target : List.of(
                KnowledgeVocabulary.CAPABILITY,
                KnowledgeVocabulary.INTERFACE,
                KnowledgeVocabulary.BEHAVIOR,
                KnowledgeVocabulary.INTERACTION,
                KnowledgeVocabulary.WORKFLOW,
                KnowledgeVocabulary.DEVICE)) {
            String token = target.substring(target.lastIndexOf('#') + 1);
            String shape = "urn:kide:shape:" + token;
            String property = "_:label-" + token;
            triples.add(new KnowledgeTriple(shape, KnowledgeVocabulary.RDF_TYPE,
                    KnowledgeTerm.iri(KnowledgeVocabulary.SH_NODE_SHAPE)));
            triples.add(new KnowledgeTriple(shape, KnowledgeVocabulary.SH_TARGET_CLASS,
                    KnowledgeTerm.iri(target)));
            triples.add(new KnowledgeTriple(shape, KnowledgeVocabulary.SH_PROPERTY,
                    KnowledgeTerm.iri(property)));
            triples.add(new KnowledgeTriple(property, KnowledgeVocabulary.SH_PATH,
                    KnowledgeTerm.iri(KnowledgeVocabulary.LABEL)));
            triples.add(new KnowledgeTriple(property, KnowledgeVocabulary.SH_MIN_COUNT,
                    KnowledgeTerm.literal("1")));
            triples.add(new KnowledgeTriple(property, KnowledgeVocabulary.SH_MAX_COUNT,
                    KnowledgeTerm.literal("1")));
        }
        return new KnowledgeDataset(
                KnowledgeDataset.CURRENT_SCHEMA,
                "kide-default-shapes",
                "PROJECT",
                scopeId,
                new KnowledgeProvenance(
                        "urn:kide:built-in:shapes",
                        "KIDE",
                        "system",
                        clock.millis(),
                        "SHACL"),
                triples);
    }
}
