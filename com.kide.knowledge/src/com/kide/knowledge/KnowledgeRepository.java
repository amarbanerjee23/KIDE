package com.kide.knowledge;

import java.util.Optional;

public interface KnowledgeRepository {
    String MISSING_ETAG = "0".repeat(64);

    Optional<KnowledgeSnapshot> snapshot();

    KnowledgeSnapshot replace(KnowledgeDataset dataset, String expectedEtag);
}
