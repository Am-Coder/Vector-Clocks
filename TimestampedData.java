/**
 * Timestamp-based versioned data - how Cassandra actually handles conflicts
 * Uses Last-Write-Wins (LWW) conflict resolution
 */
public class TimestampedData<T> {
    private final T data;
    private final long timestamp;
    private final String nodeId; // For tie-breaking
    private final VectorClock clock;

    public TimestampedData(T data, long timestamp, String nodeId, VectorClock vectorClock) {
        this.data = data;
        this.timestamp = timestamp;
        this.nodeId = nodeId;
        this.clock = vectorClock;
    }

    public T getData() {
        return data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getNodeId() {
        return nodeId;
    }

    public VectorClock getClock() {
        return clock;
    }

    /**
     * Cassandra-style conflict resolution using timestamps
     * Last Write Wins (LWW) with node ID tie-breaking
     */
    public static <T> TimestampedData<T> resolveConflict(
            TimestampedData<T> data1, TimestampedData<T> data2) {

        // Primary: Compare timestamps (later timestamp wins)
        if (data1.timestamp > data2.timestamp) {
            return data1;
        } else if (data2.timestamp > data1.timestamp) {
            return data2;
        }

        // Tie-breaker: Compare node IDs lexicographically
        // This ensures deterministic conflict resolution across all nodes
        if (data1.nodeId.compareTo(data2.nodeId) > 0) {
            return data1;
        } else {
            return data2;
        }
    }

    @Override
    public String toString() {
        return "TimestampedData{data=" + data +
                ", timestamp=" + timestamp +
                ", nodeId='" + nodeId +
                ", clock='" + clock.toString() + "'}";
    }
}
