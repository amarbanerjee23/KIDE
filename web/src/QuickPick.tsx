import { useEffect, useMemo, useRef, useState } from "react";

export interface QuickPickItem {
  id: string;
  label: string;
  description?: string;
  shortcut?: string;
  keywords?: string[];
}

interface Props {
  open: boolean;
  title: string;
  placeholder: string;
  items: QuickPickItem[];
  onSelect(item: QuickPickItem): void;
  onClose(): void;
}

export function QuickPick({
  open,
  title,
  placeholder,
  items,
  onSelect,
  onClose
}: Props) {
  const [query, setQuery] = useState("");
  const [activeIndex, setActiveIndex] = useState(0);
  const input = useRef<HTMLInputElement>(null);

  const filtered = useMemo(() => {
    const needle = query.trim().toLowerCase();
    if (!needle) return items.slice(0, 80);
    return items
      .map((item) => {
        const haystack = [
          item.label,
          item.description ?? "",
          ...(item.keywords ?? [])
        ].join(" ").toLowerCase();
        const label = item.label.toLowerCase();
        let score = 0;
        if (label === needle) score = 1000;
        else if (label.startsWith(needle)) score = 700;
        else if (label.includes(needle)) score = 500;
        else if (haystack.includes(needle)) score = 300;
        else {
          let cursor = 0;
          for (const char of needle) {
            cursor = haystack.indexOf(char, cursor);
            if (cursor < 0) return { item, score: -1 };
            cursor += 1;
          }
          score = 100;
        }
        return { item, score };
      })
      .filter((entry) => entry.score >= 0)
      .sort((a, b) => b.score - a.score || a.item.label.localeCompare(b.item.label))
      .slice(0, 80)
      .map((entry) => entry.item);
  }, [items, query]);

  useEffect(() => {
    if (!open) return;
    setQuery("");
    setActiveIndex(0);
    queueMicrotask(() => input.current?.focus());
  }, [open]);

  useEffect(() => {
    if (activeIndex >= filtered.length) setActiveIndex(0);
  }, [activeIndex, filtered.length]);

  if (!open) return null;

  function choose(item: QuickPickItem | undefined) {
    if (!item) return;
    onSelect(item);
    onClose();
  }

  return (
    <div
      className="quick-pick-backdrop"
      role="presentation"
      onMouseDown={(event) => {
        if (event.target === event.currentTarget) onClose();
      }}
    >
      <section
        className="quick-pick"
        role="dialog"
        aria-modal="true"
        aria-label={title}
      >
        <div className="quick-pick-title">{title}</div>
        <input
          ref={input}
          className="quick-pick-input"
          aria-label={title}
          value={query}
          placeholder={placeholder}
          onChange={(event) => {
            setQuery(event.target.value);
            setActiveIndex(0);
          }}
          onKeyDown={(event) => {
            if (event.key === "Escape") {
              event.preventDefault();
              onClose();
            } else if (event.key === "ArrowDown") {
              event.preventDefault();
              setActiveIndex((index) =>
                Math.min(index + 1, Math.max(0, filtered.length - 1))
              );
            } else if (event.key === "ArrowUp") {
              event.preventDefault();
              setActiveIndex((index) => Math.max(0, index - 1));
            } else if (event.key === "Enter") {
              event.preventDefault();
              choose(filtered[activeIndex]);
            }
          }}
        />
        <div className="quick-pick-list" role="listbox">
          {filtered.length ? (
            filtered.map((item, index) => (
              <button
                type="button"
                role="option"
                aria-selected={index === activeIndex}
                className={index === activeIndex ? "active" : ""}
                key={item.id}
                onMouseEnter={() => setActiveIndex(index)}
                onClick={() => choose(item)}
              >
                <span className="quick-pick-copy">
                  <strong>{item.label}</strong>
                  {item.description && <small>{item.description}</small>}
                </span>
                {item.shortcut && (
                  <kbd className="quick-pick-shortcut">{item.shortcut}</kbd>
                )}
              </button>
            ))
          ) : (
            <p className="quick-pick-empty">No matching commands or files.</p>
          )}
        </div>
      </section>
    </div>
  );
}
