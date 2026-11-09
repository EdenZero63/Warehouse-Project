# Test Evidence

Run on 2026-11-08 against commit `3165fc0` with Java 26.0.2. The results below are copied from real
runs of the program.

## 1. Automated checks

`TestRunner` result: **45 passed, 0 failed**

| Area | Checks | What they cover |
|---|---|---|
| `ui` | 21 | Padding, boxes, battery bar including a negative level, colors, input parsing, bad numbers |
| `warehouse` | 18 | Map size, bounds, racks not walkable, label lookup, unknown label and off-grid errors |
| `robot` | 3 | A new robot starts idle and carries nothing |
| `charging` | 1 | Battery starting level |
| `task` | 1 | A new task is `PENDING` |
| `sim` | 1 | A new simulation starts at tick 0 |
| `navigation` | 0 | Not written yet, see section 4 |

## 2. Edge cases in the running program

Typed into the console app. The program printed each message and kept running. It exited normally,
with no stack traces.

| Input | Case | Program output |
|---|---|---|
| `xyz` | Unknown command | `Invalid command: "xyz" isn't a command, see the list under the map` |
| `i` | Missing argument | `Invalid command: use i <robot>, e.g. i 2` |
| `i 9` | Robot doesn't exist | `Robot problem: No robot found with ID: R9` |
| `c`, `R1` | Servicing a robot that isn't stranded | `Robot problem: Robot R1 is not stranded (current state: IDLE).` |
| `a`, `P9`, `D1` | Unknown pickup | `Can't add that task: Invalid Position: there's no P9 on the map` |
| `a`, `D1`, `P1` | Drop-off used as pickup | `Can't add that task: D1 isn't a pickup point, use P1, P2...` |
| `a`, `P1`, `P2` | Pickup used as drop-off | `Can't add that task: P2 isn't a drop-off point, use D1, D2...` |
| `r`, `n` | Reset declined | `reset cancelled` |
| `r`, `y` | Reset after a run | `reset to tick 0` |

## 3. End-to-end run

Default scenario, starting batteries R1 100%, R2 90%, R3 45%. Five valid tasks were added, and all
five reached `COMPLETED`.

**Task #1, P1 to D1:**

```
T1  R1 assigned task #1
T2  R1 picked up at P1
T6  R3 charging at C3
T9  R1 delivered to D1
T11 R3 done charging at 100%
```

Battery use matches the rules. R1 moved 9 cells at 5% per cell, and inspecting it afterward showed
`R1 (3,8) 55% IDLE`. R3 started at 45% and recharged by itself without any command.

**Tasks #2 to #5, added together:**

```
T17 R1 moving to C1 for task #2
T27 R1 charging at C1
T30 R1 done charging at 100%
T31 R1 assigned task #2
T32 R2 assigned task #3
T33 R3 assigned task #4
T34 R3 picked up at P2
T35 R1 picked up at P3
T35 R2 picked up at P4
T42 R3 delivered to D2
T47 R2 delivered to D1
T49 R1 delivered to D2
T57 R1 moving to C1 for task #5
T70 R1 charging at C1
T74 R1 done charging at 100%
T75 R1 assigned task #5
T76 R1 picked up at P1
T86 R1 delivered to D2
```

Task #2 shows the battery rule working: no robot could accept it at first, so R1 went to the charger
nearest the pickup and took the task once it was full.

## 4. Not tested yet

These behaviors are implemented, but neither the automated checks nor the run above exercised them:

- BFS path checks, including a destination walled off by obstacles
- A task too long for a full battery
- A robot reaching 0 battery, the `STRANDED` state, and the `c` service command
- Rule B, turning back mid-task and resuming after charging
- Two robots queuing for one charger
- Two robots blocking each other
