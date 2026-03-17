import java.util.*;

class ParkingSpot {

    String licensePlate;
    long entryTime;
    boolean occupied;

    public ParkingSpot() {
        this.occupied = false;
    }
}

public class ParkingLotOpenAddressing {

    static final int TOTAL_SPOTS = 500;

    ParkingSpot[] table = new ParkingSpot[TOTAL_SPOTS];

    int totalProbes = 0;
    int totalParks = 0;

    public ParkingLotOpenAddressing() {
        for (int i = 0; i < TOTAL_SPOTS; i++) {
            table[i] = new ParkingSpot();
        }
    }

    // Custom hash function
    public int hash(String licensePlate) {
        return Math.abs(licensePlate.hashCode()) % TOTAL_SPOTS;
    }

    // Park vehicle
    public void parkVehicle(String plate) {

        int index = hash(plate);
        int probes = 0;

        while (table[index].occupied) {
            index = (index + 1) % TOTAL_SPOTS;
            probes++;
        }

        table[index].licensePlate = plate;
        table[index].entryTime = System.currentTimeMillis();
        table[index].occupied = true;

        totalProbes += probes;
        totalParks++;

        System.out.println("Assigned spot #" + index + " (" + probes + " probes)");
    }

    // Exit vehicle
    public void exitVehicle(String plate) {

        int index = hash(plate);
        int probes = 0;

        while (table[index].occupied) {

            if (plate.equals(table[index].licensePlate)) {

                long duration = System.currentTimeMillis() - table[index].entryTime;

                double hours = duration / (1000.0 * 60 * 60);
                double fee = hours * 5.5;

                table[index].occupied = false;

                System.out.println("Spot #" + index + " freed");
                System.out.printf("Duration: %.2f hours, Fee: $%.2f\n", hours, fee);

                return;
            }

            index = (index + 1) % TOTAL_SPOTS;
            probes++;

            if (probes >= TOTAL_SPOTS)
                break;
        }

        System.out.println("Vehicle not found");
    }

    // Find nearest available spot
    public int findNearestSpot() {

        for (int i = 0; i < TOTAL_SPOTS; i++) {

            if (!table[i].occupied) {
                return i;
            }
        }

        return -1;
    }

    // Generate statistics
    public void getStatistics() {

        int occupied = 0;

        for (ParkingSpot spot : table) {

            if (spot.occupied)
                occupied++;
        }

        double occupancyRate = (occupied * 100.0) / TOTAL_SPOTS;

        double avgProbes = totalParks == 0 ? 0 : (double) totalProbes / totalParks;

        System.out.println("\nParking Statistics");
        System.out.println("Occupancy: " + String.format("%.2f", occupancyRate) + "%");
        System.out.println("Average Probes: " + String.format("%.2f", avgProbes));
        System.out.println("Nearest Available Spot: " + findNearestSpot());
    }

    public static void main(String[] args) throws Exception {

        ParkingLotOpenAddressing lot = new ParkingLotOpenAddressing();

        lot.parkVehicle("ABC-1234");
        lot.parkVehicle("ABC-1235");
        lot.parkVehicle("XYZ-9999");

        Thread.sleep(2000);

        lot.exitVehicle("ABC-1234");

        lot.getStatistics();
    }
}