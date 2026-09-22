knowledge GoldenKrl {
  namespace kide = "https://kide.dev/ontology/v1#";

  fact kide:Camera kide:providesCapability iri "urn:kide:capability:Observe";

  query FindObserve(sensor: iri) {
    match ?device kide:providesCapability ?cap;
    where ?cap == iri "urn:kide:capability:Observe";
    select ?device;
  }

  template JavaBinding(name: string, resource: iri) for java
    body "public final class ${name} { public static final String RESOURCE = \"${resource}\"; }";

  target ObserveBinding type java {
    template JavaBinding;
    output "generated/ObserveBinding.java";
    bind name: string = "ObserveBinding";
    bind resource: iri = iri "urn:kide:device:camera";
  }
}
