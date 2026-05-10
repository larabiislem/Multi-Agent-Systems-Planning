# JADE Blocks World Planner (Part 4)

Centralized planner with distributed execution for the classic blocks-world domain. A planner agent builds a plan and a coordinator dispatches each step to block agents while a Swing GUI visualizes the world state.

## Project structure

```text
part 4/
├─ src/
│  └─ blocks/
│     ├─ BlocksWorldMain.java
│     ├─ PlannerAgent.java
│     ├─ ExecutionCoordinatorAgent.java
│     ├─ BlockAgent.java
│     ├─ Planner.java
│     ├─ PlanningProblem.java
│     ├─ WorldModel.java
│     ├─ Action.java
│     ├─ ActionType.java
│     ├─ Predicate.java
│     ├─ PredicateType.java
│     ├─ Plan.java
│     ├─ BlocksWorldFrame.java
│     ├─ BlocksWorldPanel.java
│     ├─ BlocksWorldLogger.java
│     └─ AgentMessages.java
└─ README.md
```

## Requirements

- Java (JDK 8+)
- JADE library (`jade/lib/jade.jar`)

## Compile

From the repository root:

```bash
mkdir -p "part 4"/out
javac -cp "jade/lib/jade.jar" -d "part 4"/out "part 4"/src/blocks/*.java
```

## Run

```bash
java -cp "part 4/out:jade/lib/jade.jar" blocks.BlocksWorldMain
```

> On Windows, replace `part 4/out:jade/lib/jade.jar` with `part 4/out;jade/lib/jade.jar`.

## Notes

- The JADE RMA GUI opens automatically alongside the blocks-world GUI.
- Edit `BlocksWorldMain.java` to change the initial state, goal state, or block set.
