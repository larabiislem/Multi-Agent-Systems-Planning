package blocks;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Immutable action with STRIPS-style preconditions and effects.
 */
public final class Action implements Serializable {
    private static final long serialVersionUID = 1L;

    private final ActionType type;
    private final String block;
    private final String target;

    private Action(ActionType type, String block, String target) {
        this.type = type;
        this.block = block;
        this.target = target;
    }

    public static Action pickup(String block) {
        return new Action(ActionType.PICKUP, block, null);
    }

    public static Action putdown(String block) {
        return new Action(ActionType.PUTDOWN, block, null);
    }

    public static Action stack(String block, String target) {
        return new Action(ActionType.STACK, block, target);
    }

    public static Action unstack(String block, String target) {
        return new Action(ActionType.UNSTACK, block, target);
    }

    public ActionType getType() {
        return type;
    }

    public String getBlock() {
        return block;
    }

    public String getTarget() {
        return target;
    }

    public Set<Predicate> getPreconditions() {
        switch (type) {
            case PICKUP:
                return setOf(
                        Predicate.handEmpty(),
                        Predicate.onTable(block),
                        Predicate.clear(block)
                );
            case PUTDOWN:
                return setOf(Predicate.holding(block));
            case STACK:
                return setOf(
                        Predicate.holding(block),
                        Predicate.clear(target)
                );
            case UNSTACK:
                return setOf(
                        Predicate.handEmpty(),
                        Predicate.on(block, target),
                        Predicate.clear(block)
                );
            default:
                return Collections.emptySet();
        }
    }

    public Set<Predicate> getAddEffects() {
        switch (type) {
            case PICKUP:
                return setOf(Predicate.holding(block));
            case PUTDOWN:
                return setOf(
                        Predicate.onTable(block),
                        Predicate.clear(block),
                        Predicate.handEmpty()
                );
            case STACK:
                return setOf(
                        Predicate.handEmpty(),
                        Predicate.on(block, target),
                        Predicate.clear(block)
                );
            case UNSTACK:
                return setOf(
                        Predicate.holding(block),
                        Predicate.clear(target)
                );
            default:
                return Collections.emptySet();
        }
    }

    public Set<Predicate> getDeleteEffects() {
        switch (type) {
            case PICKUP:
                return setOf(
                        Predicate.handEmpty(),
                        Predicate.onTable(block),
                        Predicate.clear(block)
                );
            case PUTDOWN:
                return setOf(Predicate.holding(block));
            case STACK:
                return setOf(
                        Predicate.holding(block),
                        Predicate.clear(target)
                );
            case UNSTACK:
                return setOf(
                        Predicate.handEmpty(),
                        Predicate.on(block, target),
                        Predicate.clear(block)
                );
            default:
                return Collections.emptySet();
        }
    }

    public boolean isApplicable(State state) {
        return state.containsAll(getPreconditions());
    }

    public State apply(State state) {
        return state.withChanges(getAddEffects(), getDeleteEffects());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Action other = (Action) obj;
        return type == other.type
                && Objects.equals(block, other.block)
                && Objects.equals(target, other.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, block, target);
    }

    @Override
    public String toString() {
        switch (type) {
            case PICKUP:
                return "Pickup(" + block + ")";
            case PUTDOWN:
                return "Putdown(" + block + ")";
            case STACK:
                return "Stack(" + block + "," + target + ")";
            case UNSTACK:
                return "Unstack(" + block + "," + target + ")";
            default:
                return type.name();
        }
    }

    private static Set<Predicate> setOf(Predicate... predicates) {
        Set<Predicate> set = new LinkedHashSet<>();
        Collections.addAll(set, predicates);
        return Collections.unmodifiableSet(set);
    }
}
