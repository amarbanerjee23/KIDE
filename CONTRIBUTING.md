# Contributing to KIDE

## Setting up

1. JDK 17 and Maven 3.9 or newer.
2. Eclipse **IDE for Java and DSL Developers** 2021-12 or newer.
3. Import every project in this repository.
4. Open `releng/com.kide.target/com.kide.target.target` and click **Set as
   Active Target Platform**. Wait for it to resolve — nothing compiles until it
   has.
5. Launch from `releng/com.kide.repository/kide.product` > **Launch an Eclipse
   application**.

A full command-line build is `mvn clean verify`.

## Ground rules

- **The target platform is pinned.** Version bumps are their own pull request
  with a full smoke test; see [docs/release-process.md](docs/release-process.md).
- **Generated code stays generated.** Change the grammar or the Ecore model and
  regenerate; do not hand-edit generated sources.
- **Every new plug-in** needs an entry in `<modules>` in the root `pom.xml` *and*
  in a feature, or it will not ship.
- **User-facing text is plain language.** Validation messages say what is wrong
  and what to do about it.
- **Palette consistency.** `tools/apply_visual_language.py` is the source of
  truth for diagram colours; re-run it rather than picking colours by hand.

## Pull requests

- One logical change per commit, with a message that says why.
- Reference the issue it closes.
- The `Build KIDE` workflow must be green.
- Add a line to `CHANGELOG.md` under *Unreleased*, written for a user.

## Code style

- Java and Xtend: tabs for indentation, 120-column limit.
- Externalised strings carry `//$NON-NLS-1$`.
- Javadoc explains *why* something exists, not what the next line does.
