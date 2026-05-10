package blocks;

import java.io.Serializable;
import java.util.Objects;

/**
 * Immutable predicate instance for the blocks world.
 */
public final class Predicate implements Serializable {
    private static final long serialVersionUID = 1L;

    private final PredicateType type;
    private final String first;
    private final String second;

    private Predicate(PredicateType type, String first, String second) {
        this.type = type;
        this.first = first;
        this.second = second;
    }

    public static Predicate handEmpty() {
        return new Predicate(PredicateType.HAND_EMPTY, null, null);
    }

    public static Predicate holding(String block) {
        return new Predicate(PredicateType.HOLDING, block, null);
    }

    public static Predicate clear(String block) {
        return new Predicate(PredicateType.CLEAR, block, null);
    }

    public static Predicate onTable(String block) {
        return new Predicate(PredicateType.ON_TABLE, block, null);
    }

    public static Predicate on(String top, String bottom) {
        return new Predicate(PredicateType.ON, top, bottom);
    }

    public PredicateType getType() {
        return type;
    }

    public String getFirst() {
        return first;
    }

    public String getSecond() {
        return second;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Predicate other = (Predicate) obj;
        return type == other.type
                && Objects.equals(first, other.first)
                && Objects.equals(second, other.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, first, second);
    }

    @Override
    public String toString() {
        switch (type) {
            case HAND_EMPTY:
                return "handempty";
            case HOLDING:
                return "holding(" + first + ")";
            case CLEAR:
                return "clear(" + first + ")";
            case ON_TABLE:
                return "ontable(" + first + ")";
            case ON:
                return "on(" + first + "," + second + ")";
            default:
                return type.name();
        }
    }
}
