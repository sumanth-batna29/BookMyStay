import java.util.*;

/**
 * Hotel Booking Management System - Use Case 3
 *
 * Centralized Room Inventory Management
 *
 * This class demonstrates:
 * - Problem of scattered state (from UC2)
 * - Centralized inventory management using HashMap
 * - O(1) lookup and update operations
 * - Single source of truth for room availability
 * - Encapsulation of inventory logic
 * - Separation of concerns between Room and Inventory
 *
 * @author sumanth-batna29
 * @version 3.1
 * @since 2026-03-25
 */
public class BookMyStay {

    // ============================================
    // ABSTRACT ROOM CLASS
    // ============================================

    /**
     * Abstract Room class representing a generalized hotel room concept.
     * All concrete room types inherit from this class.
     */
    abstract static class Room {
        protected String roomType;
        protected int numberOfBeds;
        protected double pricePerNight;
        protected String amenities;
        protected int roomSize; // in square feet

        /**
         * Constructor for Room class
         *
         * @param roomType Type of room (Single, Double, Suite)
         * @param numberOfBeds Number of beds in the room
         * @param pricePerNight Price per night
         * @param amenities Amenities available in the room
         * @param roomSize Size of room in square feet
         */
        public Room(String roomType, int numberOfBeds, double pricePerNight,
                    String amenities, int roomSize) {
            this.roomType = roomType;
            this.numberOfBeds = numberOfBeds;
            this.pricePerNight = pricePerNight;
            this.amenities = amenities;
            this.roomSize = roomSize;
        }

        /**
         * Abstract method to display room details
         * Must be implemented by concrete subclasses
         */
        abstract void displayRoomDetails();

        /**
         * Display basic room information
         */
        public void displayBasicInfo() {
            System.out.println("Room Type: " + roomType);
            System.out.println("Number of Beds: " + numberOfBeds);
            System.out.println("Price per Night: ₹" + pricePerNight);
            System.out.println("Room Size: " + roomSize + " sq ft");
            System.out.println("Amenities: " + amenities);
        }

        /**
         * Get room type
         * @return room type string
         */
        public String getRoomType() {
            return roomType;
        }

        /**
         * Get price per night
         * @return price per night
         */
        public double getPricePerNight() {
            return pricePerNight;
        }
    }

    // ============================================
    // CONCRETE ROOM CLASSES
    // ============================================

    /**
     * SingleRoom class - Concrete implementation of Room
     * Represents a single occupancy room
     */
    static class SingleRoom extends Room {

        /**
         * Constructor for SingleRoom
         */
        public SingleRoom() {
            super("Single Room", 1, 2000.0,
                    "WiFi, AC, Bed, Bathroom", 200);
        }

        /**
         * Display details specific to SingleRoom
         */
        @Override
        void displayRoomDetails() {
            System.out.println("\n--- SINGLE ROOM DETAILS ---");
            this.displayBasicInfo();
            System.out.println("Perfect for: Solo travelers");
            System.out.println("Bed Type: Single");
        }
    }

    /**
     * DoubleRoom class - Concrete implementation of Room
     * Represents a double occupancy room
     */
    static class DoubleRoom extends Room {

        /**
         * Constructor for DoubleRoom
         */
        public DoubleRoom() {
            super("Double Room", 2, 3500.0,
                    "WiFi, AC, Double Bed, Bathroom, TV", 300);
        }

        /**
         * Display details specific to DoubleRoom
         */
        @Override
        void displayRoomDetails() {
            System.out.println("\n--- DOUBLE ROOM DETAILS ---");
            this.displayBasicInfo();
            System.out.println("Perfect for: Couples, Friends");
            System.out.println("Bed Type: Double");
        }
    }

    /**
     * SuiteRoom class - Concrete implementation of Room
     * Represents a premium suite room
     */
    static class SuiteRoom extends Room {

        /**
         * Constructor for SuiteRoom
         */
        public SuiteRoom() {
            super("Suite Room", 2, 6000.0,
                    "WiFi, AC, King Bed, Bathroom, TV, Mini Bar, Balcony", 500);
        }

        /**
         * Display details specific to SuiteRoom
         */
        @Override
        void displayRoomDetails() {
            System.out.println("\n--- SUITE ROOM DETAILS ---");
            this.displayBasicInfo();
            System.out.println("Perfect for: Premium guests, Business travelers");
            System.out.println("Bed Type: King Size");
            System.out.println("Extra Features: Mini Bar, Balcony, Premium Amenities");
        }
    }

    // ============================================
    // ROOM INVENTORY CLASS (UC3 - NEW)
    // ============================================

    /**
     * UC3: RoomInventory class - Centralized inventory management
     *
     * Encapsulates all inventory-related operations using HashMap.
     * This class maintains a single source of truth for room availability.
     * Provides controlled access and updates to room inventory.
     */
    static class RoomInventory {

        // UC3: HashMap for centralized room availability
        private HashMap<String, Integer> inventoryMap;

        // UC3: Total room counts (immutable reference)
        private HashMap<String, Integer> totalRoomsMap;

        /**
         * Constructor - Initialize inventory with room types and availability
         */
        public RoomInventory() {
            this.inventoryMap = new HashMap<>();
            this.totalRoomsMap = new HashMap<>();
            initializeInventory();
        }

        /**
         * UC3: Initialize inventory with all room types
         * This method demonstrates centralized initialization in one place.
         */
        private void initializeInventory() {
            System.out.println("Initializing centralized room inventory...");

            // Initialize available rooms
            inventoryMap.put("Single Room", 5);
            inventoryMap.put("Double Room", 8);
            inventoryMap.put("Suite Room", 3);

            // Store total room counts
            totalRoomsMap.put("Single Room", 5);
            totalRoomsMap.put("Double Room", 8);
            totalRoomsMap.put("Suite Room", 3);

            System.out.println("✓ Inventory initialized successfully!");
        }

        /**
         * UC3: Get available rooms for a specific room type
         * Time Complexity: O(1) - HashMap get operation
         *
         * @param roomType Type of room
         * @return Number of available rooms, or 0 if room type not found
         */
        public int getAvailableRooms(String roomType) {
            return inventoryMap.getOrDefault(roomType, 0);
        }

        /**
         * UC3: Get total rooms for a specific room type
         *
         * @param roomType Type of room
         * @return Total number of rooms of this type
         */
        public int getTotalRooms(String roomType) {
            return totalRoomsMap.getOrDefault(roomType, 0);
        }

        /**
         * UC3: Check if room type exists in inventory
         * Time Complexity: O(1)
         *
         * @param roomType Type of room
         * @return true if room type exists, false otherwise
         */
        public boolean roomTypeExists(String roomType) {
            return inventoryMap.containsKey(roomType);
        }

        /**
         * UC3: Book a room (decrement availability)
         * Time Complexity: O(1)
         *
         * @param roomType Type of room to book
         * @return true if booking successful, false if no rooms available
         */
        public boolean bookRoom(String roomType) {
            if (!roomTypeExists(roomType)) {
                System.out.println("✗ ERROR: Room type '" + roomType + "' not found!");
                return false;
            }

            int available = getAvailableRooms(roomType);
            if (available > 0) {
                inventoryMap.put(roomType, available - 1);
                System.out.println("✓ Room booked successfully! Remaining: " +
                        getAvailableRooms(roomType));
                return true;
            } else {
                System.out.println("✗ No rooms available for: " + roomType);
                return false;
            }
        }

        /**
         * UC3: Cancel a booking (increment availability)
         * Time Complexity: O(1)
         *
         * @param roomType Type of room to cancel
         * @return true if cancellation successful, false if already at max
         */
        public boolean cancelBooking(String roomType) {
            if (!roomTypeExists(roomType)) {
                System.out.println("✗ ERROR: Room type '" + roomType + "' not found!");
                return false;
            }

            int available = getAvailableRooms(roomType);
            int total = getTotalRooms(roomType);

            if (available < total) {
                inventoryMap.put(roomType, available + 1);
                System.out.println("✓ Booking cancelled successfully! Available: " +
                        getAvailableRooms(roomType));
                return true;
            } else {
                System.out.println("✗ All rooms already available!");
                return false;
            }
        }

        /**
         * UC3: Display all room types and their availability
         * Uses entrySet() for efficient iteration over HashMap
         * Time Complexity: O(n) where n = number of room types
         */
        public void displayInventory() {
            System.out.println("\n========================================");
            System.out.println("    CENTRALIZED ROOM INVENTORY (HashMap) ");
            System.out.println("========================================");
            System.out.println("\nRoom Type Availability:\n");

            // UC3: Iterate using entrySet() for key-value pairs
            int serialNo = 1;
            for (Map.Entry<String, Integer> entry : inventoryMap.entrySet()) {
                String roomType = entry.getKey();
                Integer available = entry.getValue();
                Integer total = totalRoomsMap.get(roomType);

                System.out.println("  " + serialNo + ". " + roomType);
                System.out.println("     Available: " + available + " / " + total);
                displayInventoryBar(available, total);
                System.out.println();

                serialNo++;
            }

            System.out.println("========================================");
        }

        /**
         * UC3: Display inventory as a visual bar
         *
         * @param available Number of available rooms
         * @param total Total number of rooms
         */
        private void displayInventoryBar(int available, int total) {
            System.out.print("     [");
            for (int i = 0; i < total; i++) {
                if (i < available) {
                    System.out.print("█");  // Available
                } else {
                    System.out.print("░");  // Booked
                }
            }
            System.out.println("]");
            System.out.println("     (█ = Available, ░ = Booked)");
        }

        /**
         * UC3: Get occupancy statistics
         * Time Complexity: O(n) where n = number of room types
         */
        public void displayOccupancyStats() {
            System.out.println("\n========================================");
            System.out.println("    OCCUPANCY STATISTICS                ");
            System.out.println("========================================");

            int totalBooked = 0;
            int totalAvailable = 0;
            int totalRooms = 0;

            // UC3: Iterate using keySet()
            for (String roomType : inventoryMap.keySet()) {
                int available = getAvailableRooms(roomType);
                int total = getTotalRooms(roomType);
                int booked = total - available;

                totalAvailable += available;
                totalBooked += booked;
                totalRooms += total;
            }

            System.out.println("\nTotal Rooms: " + totalRooms);
            System.out.println("Booked Rooms: " + totalBooked);
            System.out.println("Available Rooms: " + totalAvailable);

            if (totalRooms > 0) {
                System.out.println("Occupancy Rate: " +
                        String.format("%.2f%%", (totalBooked * 100.0) / totalRooms));
            }

            System.out.println("\n========================================");
        }

        /**
         * UC3: Display detailed inventory report
         */
        public void displayDetailedReport() {
            System.out.println("\n========================================");
            System.out.println("    DETAILED INVENTORY REPORT           ");
            System.out.println("========================================");

            System.out.println("\nUsing values() to get all availability counts:");
            System.out.print("Availability Counts: ");
            System.out.println(inventoryMap.values());

            System.out.println("\nUsing keySet() to get all room types:");
            System.out.println("Room Types: " + inventoryMap.keySet());

            System.out.println("\nUsing entrySet() for key-value pairs:");
            for (Map.Entry<String, Integer> entry : inventoryMap.entrySet()) {
                System.out.println("  " + entry.getKey() + " → " + entry.getValue() + " available");
            }

            System.out.println("\n========================================");
        }
    }

    // ============================================
    // DISPLAY METHODS
    // ============================================

    /**
     * Display welcome message and application header
     */
    public static void displayWelcomeMessage() {
        System.out.println("\n========================================");
        System.out.println("    BOOK MY STAY - HOTEL BOOKING APP    ");
        System.out.println("========================================");
        System.out.println("Version: 3.1");
        System.out.println("Use Case 3: Centralized Room Inventory Management");
        System.out.println("========================================\n");
    }

    /**
     * Display all room types and their details using polymorphism
     */
    public static void displayAllRoomTypes() {
        System.out.println("\n========================================");
        System.out.println("    AVAILABLE ROOM TYPES                ");
        System.out.println("========================================");

        // Create room objects (polymorphic references)
        Room[] rooms = new Room[3];
        rooms[0] = new SingleRoom();
        rooms[1] = new DoubleRoom();
        rooms[2] = new SuiteRoom();

        // Display details for all room types using polymorphism
        for (Room room : rooms) {
            room.displayRoomDetails();
        }
    }

    /**
     * UC3: Demonstrate booking and cancellation operations
     *
     * @param inventory RoomInventory instance
     */
    public static void demonstrateBookingOperations(RoomInventory inventory) {
        System.out.println("\n========================================");
        System.out.println("    BOOKING OPERATIONS DEMONSTRATION    ");
        System.out.println("========================================");

        System.out.println("\n--- STEP 1: Book Single Rooms ---");
        inventory.bookRoom("Single Room");
        inventory.bookRoom("Single Room");

        System.out.println("\n--- STEP 2: Book Double Rooms ---");
        inventory.bookRoom("Double Room");
        inventory.bookRoom("Double Room");
        inventory.bookRoom("Double Room");

        System.out.println("\n--- STEP 3: Attempt to book Suite Rooms ---");
        inventory.bookRoom("Suite Room");
        inventory.bookRoom("Suite Room");
        inventory.bookRoom("Suite Room");
        inventory.bookRoom("Suite Room");  // This should fail

        System.out.println("\n--- STEP 4: Cancel a booking ---");
        inventory.cancelBooking("Suite Room");

        System.out.println("\n--- STEP 5: Try to cancel when no bookings exist ---");
        inventory.cancelBooking("Single Room");

        System.out.println("\n========================================");
    }

    // ============================================
    // MAIN METHOD
    // ============================================

    /**
     * Main method - Entry point of the application
     * Demonstrates UC3: Centralized inventory management using HashMap
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        // Display welcome message
        displayWelcomeMessage();

        // Display all room types with details
        displayAllRoomTypes();

        // UC3: Create and initialize centralized inventory
        System.out.println("\n--- STEP 1: Initialize Centralized Inventory ---");
        RoomInventory inventory = new RoomInventory();

        // UC3: Display current inventory state
        inventory.displayInventory();

        // UC3: Display detailed report
        inventory.displayDetailedReport();

        // UC3: Display occupancy statistics
        inventory.displayOccupancyStats();

        // UC3: Demonstrate booking and cancellation operations
        demonstrateBookingOperations(inventory);

        // UC3: Display updated inventory after operations
        System.out.println("\n--- STEP 6: Display Updated Inventory ---");
        inventory.displayInventory();
        inventory.displayOccupancyStats();

        // Final status message
        System.out.println("\n========================================");
        System.out.println("UC3 Demonstration Complete!");
        System.out.println("Centralized inventory management established.");
        System.out.println("Ready for booking queue management in UC4...");
        System.out.println("========================================\n");

        // Display HashMap advantages
        System.out.println("========================================");
        System.out.println("    HASHMAP ADVANTAGES IN UC3           ");
        System.out.println("========================================");
        System.out.println("\n✓ O(1) average-time lookup and updates");
        System.out.println("✓ Single source of truth for availability");
        System.out.println("✓ Scalable: easy to add new room types");
        System.out.println("✓ Encapsulated: controlled access via methods");
        System.out.println("✓ Flexible: supports dynamic room management");
        System.out.println("✓ Consistent: eliminates scattered state");
        System.out.println("\n========================================\n");
    }
}