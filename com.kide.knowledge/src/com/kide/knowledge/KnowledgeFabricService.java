package com.kide.knowledge;

import java.time.Clock;
import java.util.Objects;

public final class KnowledgeFabricService {
    private final KnowledgeRepository repository;
    private final KnowledgeInterchange interchange;
    private final KnowledgeShaclValidator validator;
    private final KnowledgeDataset shapes;
    private final Clock clock;

    public KnowledgeFabricService(
            KnowledgeRepository repository,
            KnowledgeInterchange interchange,
            KnowledgeShaclValidator validator,
            KnowledgeDataset shapes,
            Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.interchange = Objects.requireNonNull(interchange, "interchange");
        this.validator = Objects.requireNonNull(validator, "validator");
        this.shapes = Objects.requireNonNull(shapes, "shapes");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public KnowledgeSnapshot importDocument(
            String document,
            KnowledgeFormat format,
            String datasetId,
            String scopeType,
            String scopeId,
            String source,
            String authority,
            String importedBy,
            String expectedEtag) {
        KnowledgeDataset dataset = interchange.parse(
                document,
                format,
                datasetId,
                scopeType,
                scopeId,
                new KnowledgeProvenance(
                        source, authority, importedBy, clock.millis(), format.name()));
        KnowledgeValidationReport report = validator.validate(dataset, shapes);
        if (!report.conforms()) {
            throw new IllegalArgumentException(
                    "knowledge import violates SHACL constraints: " + report.issues().get(0).message());
        }
        return repository.replace(dataset, expectedEtag);
    }

    public String exportCurrent(KnowledgeFormat format) {
        KnowledgeSnapshot snapshot = repository.snapshot()
                .orElseThrow(() -> new KnowledgeRepositoryException("knowledge repository is empty"));
        return interchange.write(snapshot.dataset(), format);
    }

    public KnowledgeValidationReport validateCurrent() {
        KnowledgeSnapshot snapshot = repository.snapshot()
                .orElseThrow(() -> new KnowledgeRepositoryException("knowledge repository is empty"));
        return validator.validate(snapshot.dataset(), shapes);
    }
}
