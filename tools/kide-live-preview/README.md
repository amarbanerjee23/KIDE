# KIDE live preview

See the diagram while you write the model, without starting Eclipse.

```bash
node tools/kide-live-preview/server.js demo/example-workspace
# then open http://localhost:7654
```

Point it at any folder holding `.cap` and `.activity` files. Keep the browser on
one half of the screen and the editor on the other: every save redraws the
picture and refreshes the review panel.

## What it shows

- Activity diagrams on the left: one card per activity, with the capability it
  requires, how long it takes, and arrows for hand-overs and outcome branches.
- Capabilities on the right: the commands, events, alarms, data points and
  outcomes each one offers.
- A review panel listing anything that cannot hold — an activity handing over to
  a name that does not exist, an activity with no capability, a required
  capability that is nowhere in the folder.

Colours match the Sirius diagrams: blue for capabilities, amber for activities,
green for data.

## Notes

- The reader here is deliberately tolerant, not the Xtext parser. It keeps
  drawing while a model is half-typed, so it will not catch everything the
  Eclipse validators catch. It is a companion to them, not a replacement.
- Plain Node.js, no dependencies to install. Set `PORT` to use another port.
