# Warehouse Robot Fleet: Architecture and Design Decisions

The UML class diagram is in this folder. Build and run instructions are in the README.

Code comments refer to sections 4, 5 and 6 of this document.

## 1. Overview

This is a console simulation of a 10x10 warehouse. Three robots carry items from pickup points to
drop-off points. Time moves in discrete ticks, and the map redraws after each one. Moving uses
battery, and robots recharge themselves before they run out.

The default map has 4 pickups (P1 to P4), 2 drop-offs (D1, D2), 4 chargers (C1 to C4) and 2x2 racks
as obstacles.

## 2. Architecture

| Package | Classes | Responsibility |
|---|---|---|
| `app` | `Main` | Starts the program |
| `sim` | `Simulation`, `Scenario`, `EventLog` | Holds the whole world, runs ticks, builds and resets the map |
| `warehouse` | `Warehouse`, `Cell`, `Position` | The grid, bounds and obstacle checks |
| `robot` | `Robot`, abstract `RobotState`, 7 state classes | Robot data and per-tick behavior |
| `charging` | `Battery`, `ChargingStation`, `ChargingPolicy` | Battery level, charger queues, all battery rules |
| `navigation` | `PathFinder` interface, `BfsPathFinder` | Routes and distances |
| `task` | `Task`, `TaskManager`, `Dispatcher` | Task lifecycle and assignment |
| `ui` | `Menu`, `InputHelper`, `GridRenderer`, `StatusPanel`, `TextUtil`, `Colors` | Input and display only |
| `exception` | 5 custom checked exceptions | Domain errors |

`Simulation` is the center of the design. It owns the warehouse, robots, chargers, tasks, dispatcher
and log, and other classes reach shared state through it. `ui` only reads state and calls
`Simulation`, so no game rules live in the interface.

## 3. Design decisions

**Robot behavior uses the State pattern (`RobotState` with one subclass per state).**
The states are Idle, ToPickup, ToDropoff, ToCharger, WaitingForCharger, Charging and Stranded. Each
one really does behave differently on a tick, so `Robot.update()` just calls `state.act()`. This
avoids one long if/else chain, and a new state doesn't change the existing ones. This is why we use
an abstract class here.

**Routing is behind the `PathFinder` interface.**
Robots and battery rules only know the interface. A different algorithm could replace BFS without
changing them.

**Routes use BFS.**
Every move costs the same, so BFS gives the shortest path without needing anything more complex.
All battery rules use real path lengths, because straight-line distance is wrong when racks are in
the way.

**Robots are not subclassed.**
All robots behave the same, so subclasses like `HeavyRobot` would be inheritance with no purpose.

**Task status uses `String` constants instead of `enum`.**
We kept to Java covered in class.

**Battery numbers live only in `ChargingPolicy`.**
One place to read or tune them.

**Domain code throws checked exceptions and `Menu` catches them.**
The compiler forces every failure to be handled, and each one becomes a readable message instead of
a crash.

**Chargers sit in dead-end alcoves, and BFS only enters a charger if it is the destination.**
No route passes through a charger, so a charging robot can't block traffic.

**The tick order is fixed.**
The same input always produces the same run, which makes testing and the demo predictable.

## 4. Battery and charging rules

| Constant | Value | Meaning |
|---|---|---|
| `MAX_BATTERY` | 100 | Full charge |
| `MOVE_COST` | 2.5 | Battery used per cell moved |
| `WAIT_COST` | 0 | Waiting is free, so queues don't drain robots |
| `CHARGE_RATE` | 15 | Gained per tick on a charger |
| `RESERVE` | 6 | Safety margin kept on top of a trip's cost |
| `RELEASE_LEVEL` | 90 | A charging robot leaves at this level if another robot is waiting |

**Rule A, accept only what can be finished.** A robot takes a task only if its battery covers
robot to pickup, pickup to drop-off, drop-off to nearest charger, plus `RESERVE`. A task that can't
be done even from a full battery at a charger is rejected when it is created.

**Rule B, point of no return.** Before each move toward a pickup or drop-off, the robot checks that
it could still reach a charger after that step. If not, it goes to charge. It keeps its task, and
any item it is carrying, and resumes afterward.

**Rule C, charger queue.** Each charger holds one robot. Others wait in arrival order at no battery
cost. The occupant leaves at 100, or at `RELEASE_LEVEL` if someone is waiting. A robot with no task
stays parked on the charger afterward, and the status panel shows it as `R1 parked`. A parked robot
never blocks the charger, because a robot that needs it trades cells with the parked one.

**Rule D, safe recovery when the battery runs out.** The robot enters `StrandedState` and stops. If
it was carrying an item, its task is marked `FAILED`. Otherwise the task goes back to `PENDING`. The
robot stays stopped until the scenario is reset with `r`.

## 5. One tick, in order

1. The tick counter increases.
2. **Dispatch:** the oldest pending task goes to the nearest idle robot that passes Rule A, with ties
   going to the lower robot ID. One task is assigned per tick. If no robot qualifies, the task stays
   `PENDING`, and a robot is sent to the charger nearest the pickup, unless one is already charging.
3. **Move:** robots act in ID order, and each moves at most one cell.
4. **Blocking:** a robot waits if the next cell is taken. An idle robot in the way trades cells
   instead. After 3 waits in a row, the robot takes a route around the others if a different route
   exists and its battery covers it. Two robots stuck head-on with no way around swap cells, and
   both pay for the move.
5. **Charge:** each charger charges its occupant and admits the next robot in line when it is free.
6. **Log:** events are written to `EventLog` as they happen during the steps above.
7. **Render:** `Menu` redraws the map and status panel.

The menu runs ticks automatically while robots have work, and waits for a command when they don't.
The `n` command advances the simulation by hand, one tick or a chosen number at a time.

## 6. Errors and edge cases

| Case | Behavior | Exception |
|---|---|---|
| Unknown or incomplete command | Message, then back to the prompt | `InvalidCommandException` |
| Unknown label such as P9 | Task rejected | `InvalidTaskException` |
| Pickup that isn't a P, or drop-off that isn't a D | Task rejected | `InvalidTaskException` |
| Destination walled off | Task rejected when created | `UnreachableDestinationException` |
| Task too long for a full battery | Task rejected (Rule A) | `InvalidTaskException` |
| No idle robot can take a task | Task stays `PENDING` | `RobotUnavailableException`, caught by `Dispatcher` |
| Charger busy | Robot waits in line (Rule C) | None |
| Next cell occupied | Wait, then re-route (section 5, step 4) | None |
| Battery reaches 0 | `STRANDED` and recovery (Rule D) | None |
| Reset during a run | Back to the scenario start at tick 0, after confirming | None |

## 7. Scope limits

The simulation is console-only with one predefined map. Traffic handling uses the simple rules in
section 5 rather than coordinated multi-robot planning, which the project scope allows.

## 8. Warehouse map

The `warehouse` package is only the map and doesn't know anything about the robots, tasks, or batteries. It just knows what is supposed to be on each square.

**The default layout** is built by `Scenario.createDefault()` from one text row per map row:

```
   0123456789
0  ..........
1  .##.##.##.
2  C##P##.##C
3  .P......D.
4  ....##....
5  ....##....
6  .P......D.
7  C##P##.##C
8  .##.##.##.
9  ..........
```

`.` is floor, `#` is a rack, `P` is a pickup point, `D` is a drop-off point and `C` is a charger.
A position is written `(row, col)`, and `(0,0)` is the top-left corner.

| Label | Position | Label | Position |
|---|---|---|---|
| P1 | (3,1) | D1 | (3,8) |
| P2 | (6,1) | D2 | (6,8) |
| P3 | (2,3) | C1 | (2,0) |
| P4 | (7,3) | C2 | (2,9) |
| | | C3 | (7,0) |
| | | C4 | (7,9) |

P and D are numbered column by column, left to right and top to bottom inside a column. Chargers are
numbered in reading order. The 2x2 racks leave aisles between them, so most trips have to go around
at least one rack. Chargers are on the left and right edges, and BFS never routes through one unless
it is the destination.

**Classes**

| Class | Job |
|---|---|
| `Position` | One `(row, col)` spot. It can't be changed once made, and `move(dRow, dCol)` returns a new neighbouring one |
| `Cell` | What is on one square: its type (`FLOOR`, `OBSTACLE`, `PICKUP`, `DROPOFF`, `CHARGER`) and a label such as `"P1"` |
| `Warehouse` | The grid, a private `Cell[][]`, with bounds checks, walkability checks and label lookup |

**Design decisions**

- **The grid is private.** Other classes go through `isInBounds`, `isWalkable`, `getCell`, `setCell`
  and `findLabel`, so every read and write goes through a bounds check first.
- **`isWalkable` and `isInBounds` are overloaded.** Each takes either a `Position` or a `row, col`
  pair, whichever the caller already has.
- **`Position` overrides `equals` and `hashCode`.** BFS keeps a `HashMap<Position, Position>` of
  visited cells, and robots compare positions to see if a cell is taken. Without the overrides, two
  `Position` objects with the same row and column would count as different.
- **`isWalkable` returns `false` off the grid, but `getCell` throws.** BFS looks past the edge all
  the time, so "not walkable" is a normal answer there. Asking `getCell` or `setCell` for a square
  that doesn't exist is a real mistake, so it throws `InvalidPositionException`.
- **The layout is plain text in `Scenario`.** A new map is just new strings, and
  `buildWarehouse()` checks that every row has the same length.
