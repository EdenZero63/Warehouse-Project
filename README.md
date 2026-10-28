# Warehouse-Project

Warehouse Robot Fleet Simulation: a Java console app for our 4-week team project.

Robots pick up items and deliver them to drop-off points in a small grid warehouse. The
simulation runs one tick at a time and redraws the map after each tick, so you can watch
the robots move. Moving uses battery, and robots drive themselves to a charging station
before they run out.

> **Status: Week 1 skeleton.** Every class exists and compiles, but most methods are still
> `TODO (Role N)` and throw `UnsupportedOperationException` if called. Running the app
> currently prints one line and exits.

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

## Map legend

| Symbol | Meaning |
|---|---|
| `.` | Floor |
| `#` | Obstacle |
| `P1`, `P2` | Pickup points |
| `D1`, `D2` | Drop-off points |
| `C1`, `C2` | Charging stations |
| `R1` | Robot |
| `R1*` | Robot carrying an item |
| `R1!` | Robot stranded (battery empty) |

## Controls (planned)

| Key | Action |
|---|---|
| Enter | Next tick |
| `n <k>` | Run k ticks |
| `a` | Add a task (pickup and drop-off) |
| `i <n>` | Inspect robot n |
| `c` | Service a stranded robot |
| `r` | Reset the scenario (asks to confirm) |
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
  ui/           Menu, InputHelper, GridRenderer, StatusPanel
  exception/    Custom checked exceptions
docs/           UML diagram and design document
```

## Team roles

Every file starts with an `//owner: Role N` comment, and each TODO names its role.

| Role | Area | Packages | Member |
|---|---|---|---|
| 1 | Integration lead | `app`, `sim` | |
| 2 | Warehouse map | `warehouse` | |
| 3 | Robot behavior | `robot` | |
| 4 | Charging | `charging` | |
| 5 | Navigation | `navigation` | |
| 6 | Tasks and dispatch | `task` | |
| 7 | Console UI | `ui` | |
| 8 | QA and errors | `exception` | |

Everyone presents their part in the demo.

## Git workflow

1. Never commit straight to `main`. Make your own branch: `git checkout -b dev-<yourname>`.
2. Commit small, working steps with a clear message, e.g. `feat: add BFS pathfinding`.
3. Before opening a pull request, make sure the project compiles and runs.
4. Open a pull request into `main`. At least one teammate reviews it before it's merged.
5. Pull `main` often so your branch doesn't drift.
