# KIDE modelling project

This project was created by **File > New > KIDE Modelling Project**. Everything
in it already validates, so you can change one thing at a time and watch what
happens.

| File | What it is |
| --- | --- |
| `models/Ecre.dml` | the data values the system exchanges |
| `models/Ecre.mncspec` | the component interface: commands, events, alarms, responses |
| `models/Loading.cap` | a loading capability built on that interface |
| `models/MissionPlanning.activity` | an activity flow that uses the capability |
| `diagrams/` | where the Sirius diagrams are stored |

## Try this first

1. Open `models/Loading.cap`. Press `Ctrl+Space` — the editor offers complete
   blocks, not just words.
2. Misspell a command name. The Problems view explains what is wrong and what to
   do; `Ctrl+1` offers a fix.
3. Right-click the project > **Viewpoints Selection**, tick the KIDE viewpoints,
   then open a diagram from the Model Explorer.

**Help > Welcome** has three guided tours that work on exactly these files.
