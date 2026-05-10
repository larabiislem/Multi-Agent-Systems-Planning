package blocks;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Breadth-first planner for the blocks world domain.
 */
public final class Planner {

    public Optional<Plan> computePlan(PlanningProblem problem) {
        State initial = problem.getInitialState();
        if (problem.isGoal(initial)) {
            return Optional.of(new Plan(new ArrayList<Action>()));
        }

        List<Action> actionLibrary = buildActionLibrary(problem.getBlocks());
        Deque<State> frontier = new ArrayDeque<>();
        Map<State, State> parents = new HashMap<>();
        Map<State, Action> parentActions = new HashMap<>();
        Set<State> visited = new HashSet<>();

        frontier.add(initial);
        visited.add(initial);

        while (!frontier.isEmpty()) {
            State current = frontier.removeFirst();
            for (Action action : actionLibrary) {
                if (!action.isApplicable(current)) {
                    continue;
                }
                State next = action.apply(current);
                if (visited.contains(next)) {
                    continue;
                }
                visited.add(next);
                parents.put(next, current);
                parentActions.put(next, action);
                if (problem.isGoal(next)) {
                    return Optional.of(reconstructPlan(next, parents, parentActions));
                }
                frontier.addLast(next);
            }
        }

        return Optional.empty();
    }

    private Plan reconstructPlan(State goal, Map<State, State> parents, Map<State, Action> actions) {
        List<Action> steps = new ArrayList<>();
        State current = goal;
        while (parents.containsKey(current)) {
            Action action = actions.get(current);
            steps.add(action);
            current = parents.get(current);
        }
        List<Action> ordered = new ArrayList<>();
        for (int i = steps.size() - 1; i >= 0; i--) {
            ordered.add(steps.get(i));
        }
        return new Plan(ordered);
    }

    private List<Action> buildActionLibrary(List<String> blocks) {
        List<Action> actions = new ArrayList<>();
        for (String block : blocks) {
            actions.add(Action.pickup(block));
            actions.add(Action.putdown(block));
        }
        for (String block : blocks) {
            for (String target : blocks) {
                if (block.equals(target)) {
                    continue;
                }
                actions.add(Action.stack(block, target));
                actions.add(Action.unstack(block, target));
            }
        }
        return actions;
    }
}
