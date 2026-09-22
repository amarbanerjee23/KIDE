package com.kide.synthesis;

public final class SynthesisVocabulary {
    public static final String KIDE = "https://kide.dev/ontology/v1#";
    public static final String PROVIDES_CAPABILITY = KIDE + "providesCapability";
    public static final String PROVIDES_INTERFACE = KIDE + "providesInterface";
    public static final String PROVIDES_OPERATION = KIDE + "providesOperation";
    public static final String CONFLICTS_WITH = KIDE + "conflictsWith";
    public static final String PRIORITY = KIDE + "priority";
    public static final String LATENCY_MILLIS = KIDE + "latencyMillis";
    public static final String ENERGY_MILLIJOULES = KIDE + "energyMilliJoules";
    public static final String MAX_BINDINGS = KIDE + "maxBindings";
    public static final String AVAILABLE = KIDE + "available";

    private SynthesisVocabulary() { }
}
