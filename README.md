# Warehouse-Project

Warehouse Robot Fleet Simulation: a Java console app for our 4-week team project.

Robots pick up items and deliver them to drop-off points in a small grid warehouse. The
simulation runs one tick at a time and redraws the map after each tick, so you can watch
the robots move. Moving uses battery, and robots drive themselves to a charging station
before they run out.

## Demo video

Watch it run: https://youtu.be/-1Mhw2NFFrQ

## Requirements

- A JDK (developed on JDK 26). Check with `java -version` and `javac -version`.
- No libraries and no build tool. Just `javac` and `java`.

## Build and run

**Windows (PowerShell)**
```powershell
javac -d out (Get-ChildItem -Path src -Recurse -Filter *.java).FullName
java -cp out app.Main
```

**macOS / Linux**
```bash
javac -d out $(find src -name "*.java")
java -cp out app.Main
```

**IntelliJ:** mark `src` as *Sources Root*, then run `app.Main`.

`out/` is gitignored. Never commit compiled `.class` files.

## Running the tests

Tests are in `test/` and use a small `Check` helper instead of JUnit. `TestRunner` prints the totals and exits with code 1 if anything fails.

**Windows (PowerShell)**
```powershell
javac -d out-test (Get-ChildItem -Path src,test -Recurse -Filter *.java).FullName
java -cp out-test TestRunner
```

**macOS / Linux**
```bash
javac -d out-test $(find src test -name "*.java")
java -cp out-test TestRunner
```

## Map legend

| Symbol | Meaning |
|---|---|
| Blank | Floor |
| `####` | Rack (obstacle) |
| `(P1)` to `(P4)` | Pickup points |
| `(D1)`, `(D2)` | Drop-off points |
| `[C1]` to `[C4]` | Charging stations |
| `R1` | Robot |
| `R1*` | Robot carrying an item |
| `R1!` | Robot stranded (battery empty) |
| `..` | Where a robot is going next |

## Controls

Ticks run automatically while robots have work. To see a delivery, press `a`, then enter `P1` and `D1`.

| Key | Action |
|---|---|
| `a` | Add a task (pickup and drop-off) |
| `n`, `n 5` | Advance one tick, or a chosen number of ticks |
| `r` | Reset the scenario (asks to confirm) |
| `k` | Turn colors on or off |
| `q` | Quit |

## Project structure

```
src/
  app/          Main: starts the program
  sim/          Simulation (tick loop), Scenario (starting map), EventLog
  warehouse/    Warehouse grid, Cell, Position
  robot/        Robot and its states (idle, to pickup, to drop-off, to charger, charging, ...)
  charging/     Battery, ChargingStation, ChargingPolicy (all battery rules)
  navigation/   PathFinder interface, BfsPathFinder
  task/         Task, TaskManager, Dispatcher
  ui/           Menu, InputHelper, GridRenderer, StatusPanel, TextUtil, Colors
  exception/    Custom checked exceptions
test/           TestRunner and one test class per package
docs/           UML diagram and design document
```

## Team roles

Every file starts with an `//owner: Role N` comment, and each TODO names its role.

| Role | Area | Packages | Member |
|---|---|---|---|
| 1 | Integration lead | `app`, `sim` | [Alexander Nguyen](https://github.com/acnguyen1259), [Sierra Hunt](https://github.com/darkgrapeman) |
| 2 | Warehouse map | `warehouse` | [Kerem Eren](https://github.com/qeremeren) |
| 3 | Robot behavior | `robot` | [Andrew Goodman](https://github.com/droobix) |
| 4 | Charging | `charging` | [Sierra Hunt](https://github.com/darkgrapeman) |
| 5 | Navigation | `navigation` | [Marcelo Soria](https://github.com/EdenZero63) |
| 6 | Tasks and dispatch | `task` | [James Whiting](https://github.com/James-saddleback) |
| 7 | Console UI | `ui` | [Valance Fernando](https://github.com/PowerisTsutsun) |
| 8 | QA and errors | `exception` | [Erik Hernandez](https://github.com/PondBits) |

Everyone presents their part in the demo.

## Git workflow

1. Never commit straight to `main`. Make your own branch: `git checkout -b dev-<yourname>`.
2. Commit small, working steps with a clear message, e.g. `feat: add BFS pathfinding`.
3. Before opening a pull request, make sure the project compiles and runs.
4. Open a pull request into `main`. At least one teammate reviews it before it's merged.
5. Pull `main` often so your branch doesn't drift.
