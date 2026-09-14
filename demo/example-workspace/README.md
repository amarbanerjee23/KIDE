# KIDE example workspace

Everything an evaluator needs to see the toolchain working, in four small files.
Open them in the KIDE Modelling Studio (or in an Eclipse workspace with the KIDE
plug-ins installed) in this order:

| File | What it shows |
| --- | --- |
| `Ecre.dml` | the data model used by the other files |
| `Ecre.mncspec` | the MNC component and its interface |
| `Loading.cap` | a capability bound to that interface |
| `MissionPlanning.activity` | an activity flow that consumes the capability |

The three guided tours on the Welcome page walk through exactly these files.
