# Using KIDE

A tour of the studio, in the order you meet it.

## The workbench

The **KIDE Modelling** perspective opens by default.

| Area | What lives there |
| --- | --- |
| Left | Project Explorer, and the Sirius Model Explorer once viewpoints are enabled |
| Middle | The language editors |
| Right | Outline of the model you are editing, and the guided tours |
| Bottom | Problems, Properties, Sirius validation, Console |

Switch the theme under **Window > Preferences > General > Appearance**:
*KIDE Light* (default) or *KIDE Dark*.

## The four languages

| Extension | Language | What you describe |
| --- | --- | --- |
| `.dml` | Data | the primitive values the system exchanges |
| `.mncspec` | MNC specification | the component interface: commands, events, alarms, responses |
| `.cap` | Capability | what a component can do, bound to an interface |
| `.activity` | Activity | the flow of work that uses those capabilities |

## Editing

- **`Ctrl+Space`** offers whole blocks, not just words: a complete `Capability`
  skeleton, a `providesControlCapabilities` block, a full `Activity`.
- **Colour has meaning.** Capability names blue, activity names amber, data
  green, structural keywords slate — the same palette as the diagrams.
- **Hover** over any element for a sentence explaining what it is.
- **`Ctrl+1`** on a problem offers a fix. KIDE offers only fixes it can make
  honestly; where the right repair depends on your intent it says so rather than
  guessing.
- Validation runs as you type, and each message says what is wrong *and* what to
  do about it.

## Diagrams

1. Right-click the project > **Viewpoints Selection**.
2. Tick the KIDE viewpoints and confirm.
3. Expand the model in the **Model Explorer** and double-click a representation.

Diagram colours follow the same language: blue capabilities, amber activities,
green data, slate structure, grey relations.

## From activities to MNC

With a validated `.activity` model selected, run the activity-to-MNC
transformation from the context menu. It produces the MNC model the MNC diagrams
and downstream tooling consume. Fix validation problems first — the
transformation trusts the model it is given.

## Guided tours

**Help > Welcome** has three cheat sheets that work on the files the project
wizard created:

1. Write your first capability model.
2. From activities to an MNC model.
3. Open and read the Sirius diagrams.

Each step is ticked off as you complete it, and the tours stay available in the
**Cheat Sheets** view on the right.

## Shortcuts worth learning

| Shortcut | Action |
| --- | --- |
| `Ctrl+Space` | Content assist and templates |
| `Ctrl+1` | Quick fix |
| `Ctrl+Shift+F` | Format the model |
| `F3` | Jump to the declaration under the cursor |
| `Ctrl+Shift+G` | Find every reference to it |
| `Ctrl+O` | Outline of the current file |
| `Ctrl+3` | Find any command, view or preference by name |
