import type {
  GenerationArtifact,
  GenerationResult,
  ReconfigurationCause,
  ReconfigurationResult,
  SynthesisResult
} from "./types";

interface Props {
  projectName: string;
  selectedPath?: string;
  activityModels: string[];
  mncModels: string[];
  synthesisResult?: SynthesisResult;
  reconfigurationResult?: ReconfigurationResult;
  generationResult?: GenerationResult;
  canSynthesize: boolean;
  canReconfigure: boolean;
  canGenerate: boolean;
  reconfigurationCause: ReconfigurationCause;
  generationKrlModelId: string;
  onReconfigurationCause(value: ReconfigurationCause): void;
  onGenerationKrlModelId(value: string): void;
  onSynthesize(): void;
  onReconfigure(): void;
  onGenerate(): void;
  onOpenActivityDiagram(path: string): void;
  onOpenMncDiagram(path: string): void;
  onOpenEditor(): void;
}

export function EngineeringCockpit({
  projectName,
  selectedPath,
  activityModels,
  mncModels,
  synthesisResult,
  reconfigurationResult,
  generationResult,
  canSynthesize,
  canReconfigure,
  canGenerate,
  reconfigurationCause,
  generationKrlModelId,
  onReconfigurationCause,
  onGenerationKrlModelId,
  onSynthesize,
  onReconfigure,
  onGenerate,
  onOpenActivityDiagram,
  onOpenMncDiagram,
  onOpenEditor
}: Props) {
  const activeActivity =
    selectedPath?.toLowerCase().endsWith(".activity")
      ? selectedPath
      : activityModels[0];

  return (
    <section className="engineering-cockpit" aria-label="Engineering cockpit">
      <header className="engineering-cockpit-header">
        <div>
          <p className="eyebrow">ENGINEERING FLOW</p>
          <h2>{projectName}</h2>
          <p>
            Activity → capability/resource synthesis → MNC control model → KRL code generation.
          </p>
        </div>
        <button type="button" onClick={onOpenEditor}>
          Return to editor
        </button>
      </header>

      <div className="engineering-action-grid">
        <article className="engineering-action-card">
          <span className="engineering-step">1</span>
          <div>
            <strong>Activity flow</strong>
            <p>Inspect the executable workflow with the Eclipse GLSP graphical service.</p>
          </div>
          <button
            type="button"
            disabled={!activeActivity}
            onClick={() => activeActivity && onOpenActivityDiagram(activeActivity)}
          >
            Visualize flow
          </button>
        </article>

        <article className="engineering-action-card">
          <span className="engineering-step">2</span>
          <div>
            <strong>Deterministic synthesis</strong>
            <p>Run shared Eclipse/Xtext synthesis and capability-resource selection.</p>
          </div>
          <button type="button" className="primary-action" disabled={!canSynthesize} onClick={onSynthesize}>
            Synthesize
          </button>
        </article>

        <article className="engineering-action-card">
          <span className="engineering-step">3</span>
          <div>
            <strong>State machine</strong>
            <p>Open an MNC control model through the Eclipse GLSP graphical runtime.</p>
          </div>
          <button
            type="button"
            disabled={!mncModels.length}
            onClick={() => mncModels[0] && onOpenMncDiagram(mncModels[0])}
          >
            Visualize state machine
          </button>
        </article>

        <article className="engineering-action-card">
          <span className="engineering-step">4</span>
          <div>
            <strong>Generate code</strong>
            <p>Generate deterministic target artifacts from the successful synthesis and KRL model.</p>
          </div>
          <button type="button" className="primary-action" disabled={!canGenerate} onClick={onGenerate}>
            Generate code
          </button>
        </article>
      </div>

      <section className="engineering-flow-map" aria-label="Engineering flow visualizer">
        <h3>System engineering flow</h3>
        <div className="engineering-flow-track">
          <FlowNode title="Activity model" detail={activeActivity ?? "No Activity model"} />
          <FlowArrow />
          <FlowNode
            title="Synthesis"
            detail={
              synthesisResult
                ? `${synthesisResult.status} · ${synthesisResult.selections.length} binding(s)`
                : "Not run"
            }
          />
          <FlowArrow />
          <FlowNode
            title="MNC control"
            detail={
              synthesisResult?.generatedMnc
                ? "Synthesized MNC available"
                : mncModels[0] ?? "No MNC model"
            }
          />
          <FlowArrow />
          <FlowNode
            title="Generated artifacts"
            detail={
              generationResult
                ? `${generationResult.artifacts.length} artifact(s)`
                : "Not generated"
            }
          />
        </div>

        {synthesisResult?.selections.length ? (
          <div className="engineering-bindings">
            {synthesisResult.selections.map((selection) => (
              <div className="engineering-binding" key={selection.requirementId}>
                <span>{selection.activityName}</span>
                <b aria-hidden="true">→</b>
                <span>{selection.capabilityName}</span>
                <b aria-hidden="true">→</b>
                <strong>{selection.resourceId}</strong>
              </div>
            ))}
          </div>
        ) : (
          <p className="muted">
            Run synthesis to visualize capability-to-resource bindings.
          </p>
        )}
      </section>

      <div className="engineering-results-grid">
        <section className="engineering-result-card" aria-label="Synthesis result">
          <div className="panel-heading">
            <h3>Synthesis & reconfiguration</h3>
            <button type="button" disabled={!canReconfigure} onClick={onReconfigure}>
              Reconfigure
            </button>
          </div>
          <label className="engineering-inline-field">
            Reconfiguration cause
            <select
              aria-label="Reconfiguration cause"
              value={reconfigurationCause}
              onChange={(event) =>
                onReconfigurationCause(event.target.value as ReconfigurationCause)
              }
            >
              <option value="AVAILABILITY_CHANGE">Availability change</option>
              <option value="RESOURCE_LOSS">Resource loss</option>
              <option value="RESOURCE_REPLACEMENT">Resource replacement</option>
              <option value="CAPABILITY_CHANGE">Capability change</option>
              <option value="REQUIREMENT_CHANGE">Requirement change</option>
              <option value="MANUAL_REPLAN">Manual re-plan</option>
            </select>
          </label>
          {synthesisResult ? (
            <>
              <strong>{synthesisResult.status}</strong>
              <p className="muted">
                fingerprint {synthesisResult.fingerprint.slice(0, 12)} · knowledge revision {synthesisResult.knowledgeRevision}
              </p>
              {synthesisResult.diagnostics.map((diagnostic, index) => (
                <div className="engineering-diagnostic" key={diagnostic.code + index}>
                  <strong>{diagnostic.code}</strong>
                  <span>{diagnostic.message}</span>
                </div>
              ))}
              {synthesisResult.generatedMnc && (
                <details open>
                  <summary>Synthesized MNC model</summary>
                  <pre>{synthesisResult.generatedMnc}</pre>
                </details>
              )}
            </>
          ) : (
            <p className="muted">Synthesis has not been run for the selected Activity model.</p>
          )}

          {reconfigurationResult && (
            <div className="engineering-reconfiguration" aria-label="Reconfiguration result">
              <strong>{reconfigurationResult.status}</strong>
              {reconfigurationResult.migrations.map((migration) => (
                <p key={migration.requirementId}>
                  {migration.policy}: {migration.fromResourceId || "unbound"} → {migration.toResourceId || "unbound"}
                </p>
              ))}
            </div>
          )}
        </section>

        <section className="engineering-result-card" aria-label="State machine models">
          <h3>State machines</h3>
          {mncModels.length ? (
            <ul className="engineering-model-list">
              {mncModels.map((path) => (
                <li key={path}>
                  <span>{path}</span>
                  <button type="button" onClick={() => onOpenMncDiagram(path)}>
                    Open diagram
                  </button>
                </li>
              ))}
            </ul>
          ) : (
            <p className="muted">
              No persisted .mncspec model is present. The synthesized MNC preview remains available in the synthesis result.
            </p>
          )}
        </section>

        <section className="engineering-result-card engineering-code-card" aria-label="Generation result">
          <div className="panel-heading">
            <h3>Generated code</h3>
            <button type="button" className="primary-action" disabled={!canGenerate} onClick={onGenerate}>
              Generate code
            </button>
          </div>
          <label className="engineering-inline-field">
            KRL model
            <input
              aria-label="KRL model ID"
              value={generationKrlModelId}
              onChange={(event) => onGenerationKrlModelId(event.target.value)}
              placeholder="bindings.krl"
            />
          </label>
          {generationResult?.artifacts.length ? (
            generationResult.artifacts.map((artifact) => (
              <GeneratedArtifactView artifact={artifact} key={artifact.path} />
            ))
          ) : (
            <p className="muted">
              Run synthesis first, then generate code to inspect deterministic output artifacts here.
            </p>
          )}
        </section>
      </div>
    </section>
  );
}

function FlowNode({ title, detail }: { title: string; detail: string }) {
  return (
    <div className="engineering-flow-node">
      <strong>{title}</strong>
      <span>{detail}</span>
    </div>
  );
}

function FlowArrow() {
  return <span className="engineering-flow-arrow" aria-hidden="true">→</span>;
}

function GeneratedArtifactView({ artifact }: { artifact: GenerationArtifact }) {
  const preview = decodeTextArtifact(artifact);
  return (
    <details className="generated-artifact" open>
      <summary>
        <span>{artifact.path}</span>
        <small>{artifact.targetId}@{artifact.targetVersion}</small>
      </summary>
      <pre>{preview}</pre>
    </details>
  );
}

function decodeTextArtifact(artifact: GenerationArtifact): string {
  try {
    const binary = atob(artifact.contentBase64);
    const bytes = Uint8Array.from(binary, (character) => character.charCodeAt(0));
    return new TextDecoder().decode(bytes);
  } catch {
    return "Generated artifact is not displayable as UTF-8 text.";
  }
}
