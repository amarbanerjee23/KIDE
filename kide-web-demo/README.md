# KIDE browser demo

A single page that shows what KIDE does, with nothing to install: type a
KIDE-style model on the left, and the diagram builds itself on the right while
you type, with plain-language validation underneath.

```bash
npm install   # or bun install
npm run dev   # opens http://localhost:8080
```

Three sample models are one click away, including one that deliberately
contains a problem so you can see how the validation reads.

The parser here (`src/lib/kide-dsl.ts`) understands a small, readable subset of
the KIDE languages. It exists so that a reviewer can grasp the idea in a minute;
the Eclipse editors remain the real tooling.
