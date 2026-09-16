# Support

## Where to ask

| Need | Where |
| --- | --- |
| A question about modelling with KIDE | GitHub Discussions |
| A defect or a feature request | GitHub Issues |
| A security problem | See [SECURITY.md](SECURITY.md) — not a public issue |
| Commercial or research collaboration | Contact the maintainer, Amar Banerjee, through the repository profile |

## Reporting a defect well

Include:

1. What you expected and what happened instead.
2. The version and build id from **Help > About**.
3. Your platform and `java -version`.
4. The smallest model that shows the problem.
5. The relevant part of `<workspace>/.metadata/.log`.

## Self-diagnosis first

| Symptom | Check |
| --- | --- |
| The studio will not start | Java 11 or newer on the path, or set `-vm` in `kide.ini` |
| Diagrams do not appear | Right-click the project > **Viewpoints Selection** and tick the KIDE viewpoints |
| Stale errors after editing | **Project > Clean** |
| Editors behave like plain text | The project needs the Xtext nature; projects made by the KIDE wizard have it |
| Odd behaviour after installing plug-ins | Start once with `kide -clean` |

## Expectations

KIDE is a research-led product. Issues are triaged roughly weekly; there is no
contractual response time unless one has been agreed separately.
