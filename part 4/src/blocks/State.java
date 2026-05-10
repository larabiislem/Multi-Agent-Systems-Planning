package blocks;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Immutable set of predicates representing a world state.
 */
public final class State implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Set<Predicate> predicates;

    public State(Collection<Predicate> predicates) {
        this.predicates = Collections.unmodifiableSet(new LinkedHashSet<>(predicates));
    }

    public static State of(Predicate... predicates) {
        return new State(Arrays.asList(predicates));
    }

    public Set<Predicate> getPredicates() {
        return predicates;
    }

    public boolean has(Predicate predicate) {
        return predicates.contains(predicate);
    }

    public boolean containsAll(Collection<Predicate> required) {
        return predicates.containsAll(required);
    }

    public State withChanges(Collection<Predicate> toAdd, Collection<Predicate> toRemove) {
        Set<Predicate> next = new LinkedHashSet<>(predicates);
        next.removeAll(toRemove);
        next.addAll(toAdd);
        return new State(next);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        State other = (State) obj;
        return Objects.equals(predicates, other.predicates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(predicates);
    }

    @Override
    public String toString() {
        return predicates.stream()
                .map(Predicate::toString)
                .sorted()
                .collect(Collectors.joining(", "));
    }
}
