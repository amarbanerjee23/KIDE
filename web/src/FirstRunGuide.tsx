import { firstRunProgress, firstRunSteps, type FirstRunAction, type FirstRunState } from "./firstRun";

interface FirstRunGuideProps {
  state: FirstRunState;
  onAction(action: FirstRunAction): void;
}

/** First run is a reactive checklist, never a sign-in or role-grant bypass. */
export function FirstRunGuide({ state, onAction }: FirstRunGuideProps) {
  const steps = firstRunSteps(state);
  const done = firstRunProgress(state);
  const next = steps.find((step) => !step.completed);
  return (
    <section className="first-run-guide" aria-labelledby="first-run-title">
      <div className="first-run-heading">
        <div>
          <p className="eyebrow">GETTING STARTED</p>
          <h2 id="first-run-title">Your engineering setup</h2>
        </div>
        <p className="first-run-progress" aria-live="polite">
          {done} of {steps.length} ready
        </p>
      </div>
      <ol className="first-run-steps">
        {steps.map((step) => (
          <li className={step.completed ? "first-run-done" : ""} key={step.id}>
            <span className="first-run-mark" aria-hidden="true">
              {step.completed ? "✓" : steps.indexOf(step) + 1}
            </span>
            <div>
              <strong>{step.label}</strong>
              <p>{step.detail}</p>
              <span className="first-run-step-status">
                {step.completed ? "Ready" : step.id === next?.id ? "Next step" : "Not yet ready"}
              </span>
              {!step.completed && step.id === next?.id && step.action && (
                <button
                  className="first-run-action"
                  type="button"
                  onClick={() => onAction(step.action!)}
                >
                  {step.actionLabel}
                </button>
              )}
            </div>
          </li>
        ))}
      </ol>
    </section>
  );
}
