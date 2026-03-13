import java.util.*;

public class ParkingLotSystem {

    static final int SIZE = 500;

    enum Status {
        EMPTY, OCCUPIED, DELETED
    }

    static class ParkingSpot {
        String licensePlate;
        long entryTime;
        Status status;

        ParkingSpot() {
            status = Status.EMPTY;
        }
    }

    static ParkingSpot[] table = new ParkingSpot[SIZE];

    static int occupiedSpots = 0;
    static int totalProbes = 0;
    static int totalParkOperations = 0;

    static double RATE_PER_HOUR = 5.0;

    static {
        for (int i = 0; i < SIZE; i++) {
            table[i] = new ParkingSpot();
        }
    }

    // Hash function
    static int hash(String plate) {
        return Math.abs(plate.hashCode()) % SIZE;
    }

    // Park vehicle
    static void parkVehicle(String plate) {

        int index = hash(plate);
        int probes = 0;

        while (table[index].status == Status.OCCUPIED) {
            index = (index + 1) % SIZE;
            probes++;
        }

        table[index].licensePlate = plate;
        table[index].entryTime = System.currentTimeMillis();
        table[index].status = Status.OCCUPIED;

        occupiedSpots++;
        totalProbes += probes;
        totalParkOperations++;

        System.out.println("parkVehicle(\"" + plate + "\") → Assigned spot #" +
                index + " (" + probes + " probes)");
    }

    // Exit vehicle
    static void exitVehicle(String plate) {

        int index = hash(plate);
        int probes = 0;

        while (table[index].status != Status.EMPTY) {

            if (table[index].status == Status.OCCUPIED &&
                    table[index].licensePlate.equals(plate)) {

                long exitTime = System.currentTimeMillis();

                long durationMillis = exitTime - table[index].entryTime;

                double hours = durationMillis / (1000.0 * 60 * 60);

                if (hours < 1)
                    hours = 1;

                double fee = hours * RATE_PER_HOUR;

                table[index].status = Status.DELETED;

                occupiedSpots--;

                System.out.println(
                        "exitVehicle(\"" + plate + "\") → Spot #" + index +
                                " freed, Duration: " + String.format("%.2f", hours) +
                                "h, Fee: $" + String.format("%.2f", fee)
                );

                return;
            }

            index = (index + 1) % SIZE;
            probes++;

            if (probes >= SIZE)
                break;
        }

        System.out.println("Vehicle not found.");
    }

    // Find nearest available spot
    static int findNearestSpot() {

        for (int i = 0; i < SIZE; i++) {
            if (table[i].status != Status.OCCUPIED)
                return i;
        }

        return -1;
    }

    // Parking statistics
    static void getStatistics() {

        double occupancy = (occupiedSpots * 100.0) / SIZE;

        double avgProbes = totalParkOperations == 0
                ? 0
                : (double) totalProbes / totalParkOperations;

        System.out.println("\nParking Statistics");
        System.out.println("-------------------");
        System.out.println("Total Spots: " + SIZE);
        System.out.println("Occupied Spots: " + occupiedSpots);
        System.out.println("Occupancy: " + String.format("%.2f", occupancy) + "%");
        System.out.println("Average Probes: " + String.format("%.2f", avgProbes));
        System.out.println("Nearest Available Spot: #" + findNearestSpot());
    }

    public static void main(String[] args) throws Exception {

        parkVehicle("ABC-1234");
        parkVehicle("ABC-1235");
        parkVehicle("XYZ-9999");

        Thread.sleep(2000);

        exitVehicle("ABC-1234");

        getStatistics();
    }
}