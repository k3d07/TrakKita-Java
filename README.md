# TrakKita (Java Edition)

A desktop app for PUV (jeepney) drivers: daily profit/loss, fuel log, route recommender,
break-even simulator and a quick-tap fare pad. Java 17+, Swing, JDK only (no libraries).

## Run (Windows)
1. Install JDK 17 or newer.
2. Double-click `build.bat` (prints BUILD OK), then `run.bat`.
3. Always run from this folder: the app reads `fonts/` and writes `data/puv_records.csv` relative to it.

## Folders
- `src/trakkita/model` entities (PUV hierarchy, LogEntry hierarchy, Route)
- `src/trakkita/service` calculators, repositories, AppContext
- `src/trakkita/storage` CSV data store
- `src/trakkita/gui` screens and shell; `gui/widget` custom-painted animated widgets
- `fonts/` bundled open-licensed fonts (SIL OFL)
- `docs/screenshots/` how each screen should look

## TODO before the demo
Replace the placeholders: the 8 routes in `RouteRecommender`, the PUV numbers, the default diesel price in `AppContext`.
