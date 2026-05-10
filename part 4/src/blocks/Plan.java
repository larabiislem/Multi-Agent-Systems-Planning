package blocks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Immutable plan containing ordered actions.
 */
public final class Plan implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<Action> steps;

    public Plan(List<Action> steps) {
        this.steps = Collections.unmodifiableList(new ArrayList<>(steps));
    }

    public List<Action> getSteps() {
        return steps;
    }

    public boolean isEmpty() {
        return steps.isEmpty();
    }
}
