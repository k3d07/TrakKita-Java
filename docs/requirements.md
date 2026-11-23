# TrakKita Requirements Analysis

## Problem

PUV drivers receive fares throughout the day and pay operating costs such as fuel and boundary fees. If those numbers are kept only on paper or in memory, it is difficult to see whether a day was profitable, compare recent performance, or estimate how many passengers are needed to cover costs. Route choice also involves tradeoffs between distance, traffic, fare income, and fuel use. TrakKita puts these calculations and records in one offline desktop application.

## Users

The main users are jeepney, UV Express, bus, and tricycle drivers or operators who use a Windows computer. They need quick entry controls, readable totals, and a record that remains available after closing the program. Drivers may use the app between trips, so common actions should require few steps and input errors should be explained in plain language.

## Functional requirements

1. The system shall let a user add, update, filter, and delete daily fare and cost records.
2. The system shall ask before replacing a daily earning with the same date, reject updates that would create a duplicate date, and keep only the first duplicate daily earning row when loading a file.
3. The system shall calculate net profit as total fares minus fuel cost and boundary fee, and identify negative results.
4. The system shall let a user record fuel volume and price, calculate each fill-up cost, and summarize recent fuel entries.
5. The system shall estimate route fuel cost and net income from route distance, traffic factor, trips per day, vehicle fuel use, fuel price, and boundary fee.
6. The system shall rank available routes by estimated net income divided by estimated fuel cost.
7. The system shall let a user add, edit, and remove route rows and save the route list in `data/routes.csv`.
8. The system shall estimate break-even passengers and daily net profit from the entered operating assumptions.
9. The system shall count regular and discounted fares in a Quick-Tap session, undo the most recent tap, and save completed sessions.
10. The system shall offer a single choice to save a Quick-Tap session and either transfer its fare total to Profit / Loss or save it without transfer.
11. The system shall export the visible Profit / Loss records to CSV.
12. The system shall show recent totals and route recommendations on the Dashboard.
13. The system shall reload saved data when it starts and write changes when a record is changed.

## Non-functional requirements

- The application shall work offline and use Java Swing and the JDK without third-party runtime dependencies.
- The application shall store user records in readable CSV files beneath the project `data/` folder.
- Invalid user input shall produce a clear message and shall not terminate the application.
- Numeric validation shall reject `NaN` and positive or negative infinity.
- A missing records file shall be treated as a first run. Malformed rows shall be skipped with a warning so valid rows can still load.
- A missing or unusable route file shall fall back to the built-in route seeds.
- Screens shall share a consistent visual theme and present monetary values with two decimal places.

## Constraints and assumptions

- The target environment is Windows with JDK 17 or newer.
- The UI uses Swing and the project builds with the included `build.bat` script.
- Dates use ISO format (`yyyy-MM-dd`) and money values are Philippine pesos.
- Persistent record data is local CSV; no database or network connection is required.
- Route seed names and values, plus four fuel-use presets, are copied from the original C route recommender. The Java code includes a Provincial Bus preset without a corresponding preset in that C source; its assumptions must be treated as Java-specific until the project team validates them.
- The current data model does not persist vehicle selection or route settings between launches.
