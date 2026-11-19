# TrakKita UX and Completion Guide

This is the source-traced edition of the supplied UX/fix guide. It describes the Java application in this workspace. The original attachment remains unchanged because it is outside the writable project folder.

## Build and launch

From `TrakKita-Java`, run `build.bat` and then `run.bat`. The scripts compile `src/` into `out/` and start `trakkita.Main`. Run from the project directory so the application finds the bundled fonts and local `data/` files.

## Current screen behavior

### Profit / Loss

- The entry form contains date, fares, fuel cost, and boundary fee.
- Date search is displayed above the history table.
- Date stays left aligned; monetary columns and their headers are right aligned.
- Save and export outcomes appear briefly in the screen's inline status line.
- Empty tables explain that no records are available; update/delete actions become enabled only when a row is selected.
- Adding a second record for the same date asks whether to replace the existing values.
- Selecting a history row fills the entry form. Delete asks for confirmation.
- Export CSV writes date and numeric PHP values using a period decimal separator.
- When a date has fuel purchases recorded in Fuel Log, an empty fuel-cost field can be filled with that day's total.
- A saved Quick-Tap session can populate the date and fare fields.

### Fuel Log

- The form supports add, update, delete, and clear.
- Date search sits above the history table.
- The summary shows the latest seven entries.
- Table text and numeric columns use the same shared alignment helper.

### Routes and route data

- The eight default routes and their values are copied from the original C `route.c` seed table.
- Ranking uses estimated net income divided by estimated fuel cost.
- The fuel-use presets for Traditional Jeepney, Modern Jeepney, UV Express, and Tricycle use the C values: 0.22, 0.11, 0.08, and 0.03 liters/km. Java stores these as km/liter for the break-even screen.
- Diesel starts at PHP 65/liter, matching `include/route.h`; the default boundary is PHP 500/day, matching `route.c`.
- Route ownership can set the boundary to zero.
- Manage Routes supports add, edit, and delete. Saving writes `data/routes.csv`. A missing file loads the built-in routes; malformed lines are skipped with startup warnings.
- Provincial Bus is a Java-only catalog item. The C route source has no matching fuel preset, so its estimates are not confirmed as a port of that source.

### Break-Even

- Vehicle selection fills fuel efficiency, boundary, and maintenance assumptions from the selected Java PUV class.
- Its formulas are implemented by `BreakEvenCalculator` and operate on the values entered in the form.
- The selected vehicle is shared with route recommendations. The app does not yet derive break-even inputs from a driver's past seven days of entries.

### Quick-Tap

- Click the two fare buttons or use keyboard keys `1` and `2`.
- Undo removes the most recent tap from the current session.
- End and Save stores a session and offers to send its fare total to Profit / Loss.
- Saved sessions appear in the history table and can be deleted after confirmation.

### Dashboard

- Shows the most recent daily entry, the latest seven net-income entries, and the current best route.
- Includes shortcuts to Profit / Loss, Quick-Tap, and report export.

## Data and recovery

- Earnings, fuel entries, and saved tap sessions are persisted in `data/puv_records.csv`.
- Missing record files are treated as a first run. Damaged rows are skipped and reported at launch.
- Route definitions are stored separately in `data/routes.csv`.
- User data folders are ignored by Git through `.gitignore`.

## Source map

- `gui/ProfitPanel.java`, `gui/FuelPanel.java`, `gui/QuickTapPanel.java`, `gui/RoutePanel.java`, and `gui/RouteManagerPanel.java` implement the main data-entry flows.
- `gui/TableStyle.java` aligns numeric cells and headers consistently.
- `service/AppContext.java` owns the shared application state and repositories.
- `service/RouteRecommender.java` contains the built-in routes and ranking calculation.
- `service/RouteCsvLoader.java` reads and writes `data/routes.csv`.
- `storage/CsvDataStore.java` persists earning, fuel, and tap-session rows.
- `docs/requirements.md` contains the requirements analysis.
- `docs/class-diagram.puml` is the source-traced class diagram.

## Final review checklist

Code and documentation work in this checkout:

- [x] Move Profit / Loss search above its table and align numeric columns.
- [x] Use the eight route definitions from the original C implementation.
- [x] Add route CSV loading, fallback behavior, and a route management screen.
- [x] Add Fuel Log date search and table alignment.
- [x] Add Quick-Tap history, deletion confirmation, and transfer to Profit / Loss.
- [x] Add duplicate-date replacement and Profit / Loss CSV export.
- [x] Add Enter-to-submit, Escape-to-clear on entry forms, and inline save feedback.
- [x] Add empty-table guidance and selection-based update/delete states.
- [x] Add owner-driver boundary handling and synchronize the selected vehicle.
- [x] Replace the README TODO with project-specific documentation.
- [x] Add requirement analysis and PlantUML diagram.

Still requiring a real desktop session before submission:

- [ ] Build from a clean checkout with JDK 17+ and launch the window.
- [ ] Click through CRUD, filtering, duplicate replacement, export, persistence, and malformed-file recovery.
- [ ] Resize each screen and inspect the bundled fonts, sidebar text, and table spacing at small and full-screen sizes.
- [ ] Confirm the project team's chosen Provincial Bus defaults and any other vehicle values not present in the C source.
- [ ] Add the planned break-even defaults derived from at least three of the driver's recent daily entries, if that behavior is still required.
- [ ] Add screenshots of the Manage Routes screen if the team wants that screen represented in the README.

The first three items require interactive desktop review; do not mark them complete based only on source inspection or compilation.
