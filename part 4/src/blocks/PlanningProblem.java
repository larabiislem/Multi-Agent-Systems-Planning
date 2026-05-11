package blocks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Planning problem definition: initial state, goal predicates, and blocks list.
 */
public final class PlanningProblem implements Serializable {
    private static final long serialVersionUID = 1L;

    private final State initialState;
    private final Set<Predicate> goalPredicates;
    private final List<String> blocks;

    public PlanningProblem(State initialState, Set<Predicate> goalPredicates, List<String> blocks) {
        this.initialState = initialState;
        this.goalPredicates = Collections.unmodifiableSet(new LinkedHashSet<>(goalPredicates));
        this.blocks = Collections.unmodifiableList(new ArrayList<>(blocks));
    }

    public State getInitialState() {
        return initialState;
    }

    public Set<Predicate> getGoalPredicates() {
        return goalPredicates;
    }

    public List<String> getBlocks() {
        return blocks;
    }

    public boolean isGoal(State state) {
        return state.containsAll(goalPredicates);
    }
}
