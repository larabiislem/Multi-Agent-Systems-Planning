# JADE Blocks World Planner (Repo 4)

Centralized planner with distributed execution for the classic blocks-world domain. A planner agent builds a plan and a coordinator dispatches each step to block agents while a Swing GUI visualizes the world state.

## Project structure

```text
repo 4/
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
mkdir -p "repo 4"/out
javac -cp "jade/lib/jade.jar" -d "repo 4"/out "repo 4"/src/blocks/*.java
```

## Run

```bash
java -cp "repo 4/out:jade/lib/jade.jar" blocks.BlocksWorldMain
```

> On Windows, replace `repo 4/out:jade/lib/jade.jar` with `repo 4/out;jade/lib/jade.jar`.

## Notes

- The JADE RMA GUI opens automatically alongside the blocks-world GUI.
- Edit `BlocksWorldMain.java` to change the initial state, goal state, or block set.
