import { useMemo, useRef } from "react";
import type { Diagnostic } from "@/lib/kide-dsl";

const KEYWORDS = ["capability", "activity", "requires", "produces"];

function highlight(line: string) {
  const commentIndex = line.indexOf("//");
  const code = commentIndex >= 0 ? line.slice(0, commentIndex) : line;
  const comment = commentIndex >= 0 ? line.slice(commentIndex) : "";

  const parts = code.split(/("[^"]*"|\s+)/).filter((p) => p !== "");

  return (
    <>
      {parts.map((part, i) => {
        if (/^"/.test(part)) {
          return (
            <span key={i} className="text-resource">
              {part}
            </span>
          );
        }
        if (KEYWORDS.includes(part)) {
          return (
            <span key={i} className="font-semibold text-activity">
              {part}
            </span>
          );
        }
        if (part === "{" || part === "}") {
          return (
            <span key={i} className="text-muted-foreground">
              {part}
            </span>
          );
        }
        if (/^[A-Za-z_]\w*$/.test(part)) {
          return (
            <span key={i} className="text-capability">
              {part}
            </span>
          );
        }
        return <span key={i}>{part}</span>;
      })}
      {comment && <span className="text-muted-foreground italic">{comment}</span>}
    </>
  );
}

export function ModelEditor({
  value,
  onChange,
  diagnostics,
}: {
  value: string;
  onChange: (next: string) => void;
  diagnostics: Diagnostic[];
}) {
  const scrollRef = useRef<HTMLDivElement>(null);
  const lines = useMemo(() => value.split("\n"), [value]);

  const marks = useMemo(() => {
    const map = new Map<number, "error" | "warning">();
    for (const d of diagnostics) {
      if (d.severity === "error" || !map.has(d.line)) map.set(d.line, d.severity);
    }
    return map;
  }, [diagnostics]);

  return (
    <div className="relative h-full overflow-hidden rounded-lg border border-border bg-background/60">
      <div ref={scrollRef} className="h-full overflow-auto">
        <div className="relative min-h-full w-max min-w-full font-mono text-[12.5px] leading-6">
          <pre aria-hidden className="m-0 block p-3 pl-14">
            {lines.map((line, i) => (
              <div key={i} className="whitespace-pre">
                {highlight(line) || " "}
              </div>
            ))}
          </pre>

          <div className="pointer-events-none absolute inset-y-0 left-0 w-11 border-r border-border/70 bg-secondary/30 py-3 text-right">
            {lines.map((_, i) => {
              const mark = marks.get(i + 1);
              return (
                <div
                  key={i}
                  className={
                    mark === "error"
                      ? "pr-2 text-destructive"
                      : mark === "warning"
                        ? "pr-2 text-warning"
                        : "pr-2 text-muted-foreground/60"
                  }
                >
                  {mark ? "●" : i + 1}
                </div>
              );
            })}
          </div>

          <textarea
            value={value}
            onChange={(e) => onChange(e.target.value)}
            spellCheck={false}
            aria-label="KIDE model source"
            className="absolute inset-0 h-full w-full resize-none overflow-hidden bg-transparent p-3 pl-14 font-mono text-[12.5px] leading-6 text-transparent caret-primary outline-none"
          />
        </div>
      </div>
    </div>
  );
}
