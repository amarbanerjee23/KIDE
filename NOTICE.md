# Notices

KIDE Modelling Studio
Copyright (c) 2026 KIDE contributors.

This software is published under the Eclipse Public License 2.0
(SPDX-License-Identifier: `EPL-2.0`). See [LICENSE](LICENSE).

## Third-party components

KIDE is distributed with, or depends on, the following components, each under
its own licence.

| Component | Licence |
| --- | --- |
| Eclipse Platform, EMF, EMF Compare, GMF, OCL, Acceleo, UML2 | EPL-2.0 |
| Eclipse Xtext and Xtend | EPL-2.0 |
| Eclipse Sirius | EPL-2.0 |
| ANTLR 3 runtime | BSD-3-Clause |
| Google Guava, Google Gson, Google Guice | Apache-2.0 |
| Apache Commons IO | Apache-2.0 |
| Apache Log4j 1.x (via the generated Xtext UI bundles) | Apache-2.0 |

Exact versions are fixed by
`releng/com.kide.target/com.kide.target.target` and reported in
**Help > About > Installation Details** in a built product.

## Excluded from the distribution

`com.system.knowledge.plugin` depends on NeoEMF (`fr.inria.atlanmod.*`), which is
not part of the target platform. It remains in the source repository as research
code and is not built or shipped.

## Trademarks

Eclipse and the Eclipse logo are trademarks of the Eclipse Foundation. Other
names may be trademarks of their respective owners. KIDE is not affiliated with
or endorsed by the Eclipse Foundation.
