import { createFileRoute } from "@tanstack/react-router";
import { useMemo, useState } from "react";
import { KideDiagram } from "@/components/kide/KideDiagram";
import { ModelEditor } from "@/components/kide/ModelEditor";
import { parseModel, SAMPLES } from "@/lib/kide-dsl";

const title = "KIDE — model knowledge-integrated systems in your browser";
const description =
  "A live demonstrator for KIDE: write capabilities and activities in the DSL and watch the model diagram build itself, with plain-language validation.";

export const Route = createFileRoute("/")({
  head: () => ({
    meta: [
      { title },
      { name: "description", content: description },
      { property: "og:title", content: title },
      { property: "og:description", content: description },
      { property: "og:type", content: "website" },
      { name: "twitter:card", content: "summary_large_image" },
    ],
  }),
  component: Index,
});

function Index() {
  const [sample, setSample] = useState(0);
  const [source, setSource] = useState(SAMPLES[0]!.source);
  const model = useMemo(() => parseModel(source), [source]);

  const errors = model.diagnostics.filter((d) => d.severity === "error");
  const warnings = model.diagnostics.filter((d) => d.severity === "warning");
  const activityCount = model.capabilities.reduce((n, c) => n + c.activities.length, 0);

  return (
    <main className="mx-auto flex min-h-screen w-full max-w-6xl flex-col gap-6 px-4 py-8 md:px-8">
      <header className="flex flex-col gap-4">
        <div className="flex flex-wrap items-center gap-3">
          <span className="rounded-full border border-primary/40 bg-primary/10 px-3 py-1 font-mono text-[11px] tracking-widest text-primary uppercase">
            Live demonstrator
          </span>
          <span className="font-mono text-[11px] text-muted-foreground">
            no install · no Eclipse · runs in this tab
          </span>
        </div>
        <h1 className="text-3xl leading-tight font-semibold tracking-tight md:text-5xl">
          KIDE, without the{" "}
          <span className="text-primary">thirty-minute setup</span>
        </h1>
        <p className="max-w-2xl text-sm leading-relaxed text-muted-foreground md:text-base">
          Write capabilities and the activities they own. The artefacts flowing between them
          are resolved as you type, the diagram redraws instantly, and anything broken is
          explained in words rather than parser output.
        </p>
      </header>

      <div className="flex flex-wrap gap-2">
        {SAMPLES.map((s, i) => (
          <button
            key={s.name}
            onClick={() => {
              setSample(i);
              setSource(s.source);
            }}
            className={
              sample === i
                ? "glow-ring rounded-full bg-primary px-4 py-2 text-xs font-medium text-primary-foreground transition"
                : "rounded-full border border-border bg-card/60 px-4 py-2 text-xs font-medium text-muted-foreground transition hover:border-primary/50 hover:text-foreground"
            }
          >
            {s.name}
            <span className="ml-2 hidden text-[11px] opacity-70 sm:inline">{s.blurb}</span>
          </button>
        ))}
      </div>

      <section className="grid gap-5 lg:grid-cols-2">
        <div className="panel flex min-h-[26rem] flex-col gap-3 p-4">
          <PanelHeading label="Model source" hint=".kide" />
          <div className="min-h-0 flex-1">
            <ModelEditor
              value={source}
              onChange={setSource}
              diagnostics={model.diagnostics}
            />
          </div>
        </div>

        <div className="panel flex min-h-[26rem] flex-col gap-3 p-4">
          <PanelHeading label="Generated diagram" hint="auto-layout" />
          <div className="min-h-0 flex-1 overflow-auto rounded-lg border border-border bg-background/40 p-2">
            <KideDiagram model={model} />
          </div>
          <div className="flex flex-wrap gap-4 font-mono text-[11px] text-muted-foreground">
            <Legend color="bg-capability" label="capability" />
            <Legend color="bg-activity" label="activity" />
            <Legend color="bg-resource" label="artefact" />
          </div>
        </div>
      </section>

      <section className="panel flex flex-col gap-4 p-4">
        <div className="flex flex-wrap items-center justify-between gap-3">
          <PanelHeading label="Validation" hint="live" />
          <div className="flex gap-2 font-mono text-[11px]">
            <Stat value={model.capabilities.length} label="capabilities" />
            <Stat value={activityCount} label="activities" />
            <Stat value={model.resources.length} label="artefacts" />
          </div>
        </div>

        {model.diagnostics.length === 0 ? (
          <p className="rounded-lg border border-primary/30 bg-primary/5 px-4 py-3 text-sm text-primary">
            Model is consistent — every artefact an activity needs is produced somewhere.
          </p>
        ) : (
          <ul className="flex flex-col gap-2">
            {[...errors, ...warnings].map((d, i) => (
              <li
                key={`${d.line}-${i}`}
                className={
                  d.severity === "error"
                    ? "flex gap-3 rounded-lg border border-destructive/40 bg-destructive/10 px-4 py-3 text-sm"
                    : "flex gap-3 rounded-lg border border-warning/40 bg-warning/10 px-4 py-3 text-sm"
                }
              >
                <span
                  className={
                    d.severity === "error"
                      ? "font-mono text-xs text-destructive"
                      : "font-mono text-xs text-warning"
                  }
                >
                  line {d.line}
                </span>
                <span className="text-foreground/90">{d.message}</span>
              </li>
            ))}
          </ul>
        )}
      </section>

      <footer className="pb-4 text-xs text-muted-foreground">
        A browser demonstrator of the KIDE workspace — the same concepts as the Xtext and
        Sirius tooling, shareable with a link.
      </footer>
    </main>
  );
}

function PanelHeading({ label, hint }: { label: string; hint: string }) {
  return (
    <div className="flex items-baseline gap-3">
      <h2 className="text-sm font-semibold tracking-wide">{label}</h2>
      <span className="font-mono text-[11px] text-muted-foreground">{hint}</span>
    </div>
  );
}

function Legend({ color, label }: { color: string; label: string }) {
  return (
    <span className="flex items-center gap-2">
      <span className={`inline-block size-2.5 rounded-full ${color}`} />
      {label}
    </span>
  );
}

function Stat({ value, label }: { value: number; label: string }) {
  return (
    <span className="rounded-md border border-border bg-secondary/40 px-2.5 py-1">
      <span className="text-foreground">{value}</span>{" "}
      <span className="text-muted-foreground">{label}</span>
    </span>
  );
}
