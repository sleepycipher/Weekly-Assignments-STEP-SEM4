import java.util.*;

public class Problem8_ParkingLot {

    static final int CAPACITY = 20; // Using 20 spots for demo (scale to 500 as needed)

    // Slot states
    static final int EMPTY = 0;
    static final int OCCUPIED = 1;
    static final int DELETED = 2; // for lazy deletion (to not break probe chains)

    // Arrays for open addressing
    int[] slotStatus = new int[CAPACITY];
    String[] licensePlates = new String[CAPACITY];
    long[] entryTimes = new long[CAPACITY];

    // Statistics
    int totalProbes = 0;
    int totalParkings = 0;
    HashMap<Integer, Integer> peakHourCount = new HashMap<>();

    // Hash function: maps license plate to a slot index
    private int hash(String licensePlate) {
        int hash = 0;
        for (char c : licensePlate.toCharArray()) {
            hash = (hash * 31 + c) % CAPACITY;
        }
        return Math.abs(hash);
    }

    // Park a vehicle using linear probing
    public String parkVehicle(String licensePlate) {
        int preferredSpot = hash(licensePlate);
        int probes = 0;
        int spot = preferredSpot;

        // Linear probing to find an empty or deleted slot
        while (slotStatus[spot] == OCCUPIED) {
            probes++;
            spot = (spot + 1) % CAPACITY;
            if (spot == preferredSpot) {
                return "PARKING FULL: Cannot park " + licensePlate;
            }
        }

        slotStatus[spot] = OCCUPIED;
        licensePlates[spot] = licensePlate;
        entryTimes[spot] = System.currentTimeMillis();

        totalProbes += probes;
        totalParkings++;

        // Track peak hour
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        peakHourCount.put(hour, peakHourCount.getOrDefault(hour, 0) + 1);

        return String.format("Parked \"%s\" at spot #%d (%d probe%s)",
                             licensePlate, spot, probes, probes == 1 ? "" : "s");
    }

    // Find which spot a vehicle is in
    private int findSpot(String licensePlate) {
        int preferredSpot = hash(licensePlate);
        int spot = preferredSpot;

        while (slotStatus[spot] != EMPTY) {
            if (slotStatus[spot] == OCCUPIED && licensePlate.equals(licensePlates[spot])) {
                return spot;
            }
            spot = (spot + 1) % CAPACITY;
            if (spot == preferredSpot) break;
        }
        return -1;
    }

    // Exit a vehicle and calculate fee
    public String exitVehicle(String licensePlate) {
        int spot = findSpot(licensePlate);
        if (spot == -1) return "Vehicle not found: " + licensePlate;

        long durationMs = System.currentTimeMillis() - entryTimes[spot];
        double durationHours = Math.max(durationMs / 3600000.0, 0.042); // min ~2.5 min for demo
        double fee = Math.round(durationHours * 5.0 * 100.0) / 100.0;  // $5/hr

        // Lazy deletion
        slotStatus[spot] = DELETED;
        licensePlates[spot] = null;

        long mins = (durationMs / 1000) / 60;
        long secs = (durationMs / 1000) % 60;

        return String.format("Exit \"%s\" from spot #%d | Duration: %dm %ds | Fee: $%.2f",
                             licensePlate, spot, mins, secs, fee);
    }

    // Check current occupancy
    public int getOccupancy() {
        int count = 0;
        for (int s : slotStatus) if (s == OCCUPIED) count++;
        return count;
    }

    // Get peak hour
    public int getPeakHour() {
        return peakHourCount.entrySet().stream()
                            .max(Map.Entry.comparingByValue())
                            .map(Map.Entry::getKey).orElse(-1);
    }

    // Print statistics
    public void getStatistics() {
        int occupancy = getOccupancy();
        double occupancyPct = occupancy * 100.0 / CAPACITY;
        double avgProbes = totalParkings > 0 ? (double) totalProbes / totalParkings : 0;
        System.out.println("\n=== Parking Statistics ===");
        System.out.printf("Occupancy   : %d/%d (%.1f%%)%n", occupancy, CAPACITY, occupancyPct);
        System.out.printf("Avg Probes  : %.2f%n", avgProbes);
        System.out.println("Peak Hour   : " + getPeakHour() + ":00");
    }

    // Display lot
    public void printLot() {
        System.out.println("\n--- Parking Lot Layout ---");
        for (int i = 0; i < CAPACITY; i++) {
            String state = slotStatus[i] == OCCUPIED ? "[" + licensePlates[i] + "]"
                         : slotStatus[i] == DELETED  ? "[DELETED]"
                         : "[EMPTY  ]";
            System.out.printf("Spot %2d: %s%n", i, state);
        }
    }

    public static void main(String[] args) {
        Problem8_ParkingLot lot = new Problem8_ParkingLot();

        System.out.println("=== Problem 8: Parking Lot Management with Open Addressing ===\n");

        System.out.println(lot.parkVehicle("ABC-1234"));
        System.out.println(lot.parkVehicle("ABC-1235"));
        System.out.println(lot.parkVehicle("XYZ-9999"));
        System.out.println(lot.parkVehicle("DEF-5678"));
        System.out.println(lot.parkVehicle("GHI-1111"));
        System.out.println(lot.parkVehicle("JKL-2222"));

        lot.printLot();

        System.out.println("\n--- Vehicles Exiting ---");
        System.out.println(lot.exitVehicle("ABC-1234"));
        System.out.println(lot.exitVehicle("XYZ-9999"));
        System.out.println(lot.exitVehicle("NOTHERE-000"));

        System.out.println("\n--- Park again after exits ---");
        System.out.println(lot.parkVehicle("NEW-0001"));

        lot.getStatistics();
    }
}
