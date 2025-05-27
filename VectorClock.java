import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Vector Clock implementation - useful for understanding causality
 */
public class VectorClock implements Comparable<VectorClock> {
    private final Map<String, Long> clock;

    public VectorClock() {
        this.clock = new ConcurrentHashMap<>();
    }

    public void increment(String nodeId) {
        this.clock.put(nodeId, this.clock.getOrDefault(nodeId, 0L) + 1);
    }

    public boolean happensBefore(VectorClock other) {
        boolean strictlyLess = false;

        for (Map.Entry<String, Long> entry : this.clock.entrySet()) {
            String nodeId = entry.getKey();
            Long thisValue = entry.getValue();
            Long otherValue = other.clock.getOrDefault(nodeId, 0L);

            if (thisValue > otherValue) {
                return false;
            }
            if (thisValue < otherValue) {
                strictlyLess = true;
            }
        }

        for (Map.Entry<String, Long> entry : other.clock.entrySet()) {
            String nodeId = entry.getKey();
            if (!this.clock.containsKey(nodeId) && entry.getValue() > 0) {
                strictlyLess = true;
            }
        }

        return strictlyLess;
    }

    public boolean isConcurrent(VectorClock other) {
        return !this.happensBefore(other) && !other.happensBefore(this) && !this.equals(other);
    }

    @Override
    public int compareTo(VectorClock other) {
        if (this.equals(other)) return 0;
        if (this.happensBefore(other)) return -1;
        if (other.happensBefore(this)) return 1;
        return Integer.compare(this.hashCode(), other.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        VectorClock other = (VectorClock) obj;
        Set<String> allNodes = new HashSet<>(this.clock.keySet());
        allNodes.addAll(other.clock.keySet());

        for (String nodeId : allNodes) {
            long thisValue = this.clock.getOrDefault(nodeId, 0L);
            long otherValue = other.clock.getOrDefault(nodeId, 0L);
            if (thisValue != otherValue) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(clock);
    }

    @Override
    public String toString() {
        return "VectorClock" + clock;
    }
}
