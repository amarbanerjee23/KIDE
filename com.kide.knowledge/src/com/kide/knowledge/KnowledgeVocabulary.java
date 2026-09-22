package com.kide.knowledge;

public final class KnowledgeVocabulary {
    public static final String KIDE = "https://kide.dev/ontology/v1#";
    public static final String RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    public static final String RDFS = "http://www.w3.org/2000/01/rdf-schema#";
    public static final String SH = "http://www.w3.org/ns/shacl#";

    public static final String RDF_TYPE = RDF + "type";
    public static final String LABEL = RDFS + "label";

    public static final String CAPABILITY = KIDE + "Capability";
    public static final String INTERFACE = KIDE + "Interface";
    public static final String BEHAVIOR = KIDE + "Behavior";
    public static final String INTERACTION = KIDE + "Interaction";
    public static final String WORKFLOW = KIDE + "Workflow";
    public static final String DEVICE = KIDE + "Device";

    public static final String SH_NODE_SHAPE = SH + "NodeShape";
    public static final String SH_TARGET_CLASS = SH + "targetClass";
    public static final String SH_PROPERTY = SH + "property";
    public static final String SH_PATH = SH + "path";
    public static final String SH_MIN_COUNT = SH + "minCount";
    public static final String SH_MAX_COUNT = SH + "maxCount";

    private KnowledgeVocabulary() { }
}
