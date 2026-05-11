package auction;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Thread-safe model for auction state updates in the GUI.
 */
public final class AuctionModel {
    private final String item;
    private final PropertyChangeSupport changes = new PropertyChangeSupport(this);

    private int currentPrice;
    private int round;
    private String status;
    private String winner;
    private Map<String, Integer> lastBids;

    public AuctionModel(String item) {
        this.item = item;
        this.currentPrice = 0;
        this.round = 0;
        this.status = "Waiting for seller.";
        this.lastBids = new LinkedHashMap<>();
    }

    public void addChangeListener(PropertyChangeListener listener) {
        changes.addPropertyChangeListener(listener);
    }

    public synchronized Snapshot snapshot() {
        return snapshotLocked();
    }

    public void updateRound(int round, int currentPrice, Map<String, Integer> lastBids, String status) {
        Snapshot oldSnapshot;
        Snapshot newSnapshot;
        synchronized (this) {
            oldSnapshot = snapshotLocked();
            this.round = round;
            this.currentPrice = currentPrice;
            this.lastBids = new LinkedHashMap<>(lastBids);
            if (status != null) {
                this.status = status;
            }
            newSnapshot = snapshotLocked();
        }
        changes.firePropertyChange("state", oldSnapshot, newSnapshot);
    }

    public void finish(String status, String winner, int finalPrice, Map<String, Integer> lastBids) {
        Snapshot oldSnapshot;
        Snapshot newSnapshot;
        synchronized (this) {
            oldSnapshot = snapshotLocked();
            this.currentPrice = finalPrice;
            if (lastBids != null) {
                this.lastBids = new LinkedHashMap<>(lastBids);
            }
            this.status = status;
            this.winner = winner;
            newSnapshot = snapshotLocked();
        }
        changes.firePropertyChange("state", oldSnapshot, newSnapshot);
    }

    private Snapshot snapshotLocked() {
        return new Snapshot(item, currentPrice, round, status, winner,
                Collections.unmodifiableMap(new LinkedHashMap<>(lastBids)));
    }

    public static final class Snapshot {
        private final String item;
        private final int currentPrice;
        private final int round;
        private final String status;
        private final String winner;
        private final Map<String, Integer> lastBids;

        private Snapshot(String item, int currentPrice, int round, String status, String winner,
                         Map<String, Integer> lastBids) {
            this.item = item;
            this.currentPrice = currentPrice;
            this.round = round;
            this.status = status;
            this.winner = winner;
            this.lastBids = lastBids;
        }

        public String getItem() {
            return item;
        }

        public int getCurrentPrice() {
            return currentPrice;
        }

        public int getRound() {
            return round;
        }

        public String getStatus() {
            return status;
        }

        public String getWinner() {
            return winner;
        }

        public Map<String, Integer> getLastBids() {
            return lastBids;
        }
    }
}
