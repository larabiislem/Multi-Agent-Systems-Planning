package blocks;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Thread-safe world model for execution and GUI updates.
 */
public final class WorldModel {
    private final List<String> blocks;
    private final Set<Predicate> goalPredicates;
    private final PropertyChangeSupport changes = new PropertyChangeSupport(this);

    private State currentState;

    public WorldModel(List<String> blocks, State initialState, Set<Predicate> goalPredicates) {
        this.blocks = Collections.unmodifiableList(new ArrayList<>(blocks));
        this.goalPredicates = Collections.unmodifiableSet(new LinkedHashSet<>(goalPredicates));
        this.currentState = initialState;
    }

    public synchronized State getCurrentState() {
        return currentState;
    }

    public synchronized boolean isGoalReached() {
        return currentState.containsAll(goalPredicates);
    }

    public List<String> getBlocks() {
        return blocks;
    }

    public Set<Predicate> getGoalPredicates() {
        return goalPredicates;
    }

    public void apply(Action action) {
        State oldState;
        State newState;
        synchronized (this) {
            if (!action.isApplicable(currentState)) {
                throw new IllegalStateException("Action not applicable: " + action + " in " + currentState);
            }
            oldState = currentState;
            newState = action.apply(currentState);
            currentState = newState;
        }
        changes.firePropertyChange("state", oldState, newState);
    }

    public void addChangeListener(PropertyChangeListener listener) {
        changes.addPropertyChangeListener(listener);
    }

    public Optional<String> getHeldBlock() {
        State snapshot = getCurrentState();
        for (Predicate predicate : snapshot.getPredicates()) {
            if (predicate.getType() == PredicateType.HOLDING) {
                return Optional.of(predicate.getFirst());
            }
        }
        return Optional.empty();
    }

    public List<List<String>> getStacks() {
        State snapshot = getCurrentState();
        Map<String, String> above = new HashMap<>();
        Set<String> onTable = new LinkedHashSet<>();
        for (Predicate predicate : snapshot.getPredicates()) {
            if (predicate.getType() == PredicateType.ON_TABLE) {
                onTable.add(predicate.getFirst());
            } else if (predicate.getType() == PredicateType.ON) {
                above.put(predicate.getSecond(), predicate.getFirst());
            }
        }

        List<List<String>> stacks = new ArrayList<>();
        for (String base : onTable) {
            List<String> stack = new ArrayList<>();
            String current = base;
            stack.add(current);
            while (above.containsKey(current)) {
                current = above.get(current);
                stack.add(current);
            }
            stacks.add(stack);
        }
        return stacks;
    }
}
