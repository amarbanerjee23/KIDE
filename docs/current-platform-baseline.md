# KIDE current platform baseline

PR16 moves the supported Eclipse product and headless language server to the current Java 21 generation.

## Pinned platform

- Java: 21
- Tycho: 5.0.4
- Eclipse Platform / SimRel: 2026-09 (Eclipse 4.41)
- Xtext/Xtend: 2.44.0
- Sirius Desktop: 7.6.1

The Xtext and Sirius release-specific p2 repositories are retained in the target definition so the language and graphical stacks cannot silently move to another release. Common Eclipse, EMF and third-party IUs resolve from the 2026-09 simultaneous-release repository.

## Compatibility boundary

PR16 removes the legacy Xtext 2.25 LSP4J compatibility repository and the 2021 Orbit repository. Every KIDE bundle with a BREE now declares JavaSE-21. Build and trusted-release workflows both use JDK 21.

The PR12 golden corpus, PR13 LSP parity contract, PR14 schema migration qualification and PR15 generated-source ownership/baseline checks remain mandatory. The platform change is accepted only if the same packaged desktop and language-server qualifications pass.

## Deliberate non-goals

This PR does not change KIDE grammar/model semantics, project schema version, enterprise stable-ID rules, or the Activity-to-MNC transformation contract.
