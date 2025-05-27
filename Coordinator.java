public class Coordinator {
    public static void main(String[] args) {
        // Create mock Nodes and Clocks
        VectorClock clock1 = new VectorClock();
        VectorClock clock2 = new VectorClock();
        String node1 = "n1";
        String node2 = "n2";

        clock1.increment(node1);
        clock1.increment(node1);
        clock1.increment(node2);

        clock2.increment(node2);
        clock2.increment(node2);
        clock2.increment(node1);

        // Create Data points
        TimestampedData<Integer> data1 = new TimestampedData<>(1,
                1000L, node1, clock1);
        TimestampedData<Integer> data2 = new TimestampedData<>(1,
                2000L, node2, clock2);

        System.out.println(data1.getClock());
        System.out.println(data2.getClock());

        // Check for conflicts and resolve it,
        // Usually the coordinator performs this at READ time based on your Quorum setup
        if(data1.getData().equals(data2.getData())) {
            if(data1.getClock().isConcurrent(data2.getClock())) {
                TimestampedData<Integer> resolved = TimestampedData.resolveConflict(data1, data2);
                System.out.println(resolved);
            }
        }
    }
}