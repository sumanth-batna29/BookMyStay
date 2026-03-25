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
    // STATIC AVAILABILITY VARIABLES
    // ============================================

    // Room availability counters (static representation)
    private static int singleRoomsAvailable = 5;
    private static int doubleRoomsAvailable = 8;
    private static int suiteRoomsAvailable = 3;

    // Total room counts
    private static final int TOTAL_SINGLE_ROOMS = 5;
    private static final int TOTAL_DOUBLE_ROOMS = 8;
    private static final int TOTAL_SUITE_ROOMS = 3;

    // ============================================
    // DISPLAY METHODS
    // ============================================

    /**
     * Display welcome message and application header
     */
    public static void displayWelcomeMessage() {
        System.out.println("========================================");
        System.out.println("    BOOK MY STAY - HOTEL BOOKING APP    ");
        System.out.println("========================================");
        System.out.println("Version: 2.1");
        System.out.println("Use Case 2: Basic Room Types & Static Availability");
        System.out.println("========================================\n");
    }

    /**
     * Display availability information for all room types
     */
    public static void displayAvailability() {
        System.out.println("\n========================================");
        System.out.println("    ROOM AVAILABILITY STATUS            ");
        System.out.println("========================================");

        System.out.println("\nSingle Rooms:");
        System.out.println("  Available: " + singleRoomsAvailable + " / " + TOTAL_SINGLE_ROOMS);
        displayAvailabilityBar(singleRoomsAvailable, TOTAL_SINGLE_ROOMS);

        System.out.println("\nDouble Rooms:");
        System.out.println("  Available: " + doubleRoomsAvailable + " / " + TOTAL_DOUBLE_ROOMS);
        displayAvailabilityBar(doubleRoomsAvailable, TOTAL_DOUBLE_ROOMS);

        System.out.println("\nSuite Rooms:");
        System.out.println("  Available: " + suiteRoomsAvailable + " / " + TOTAL_SUITE_ROOMS);
        displayAvailabilityBar(suiteRoomsAvailable, TOTAL_SUITE_ROOMS);

        System.out.println("\n========================================");
    }

    /**
     * Display availability as a visual bar
     *
     * @param available Number of available rooms
     * @param total Total number of rooms
     */
    public static void displayAvailabilityBar(int available, int total) {
        System.out.print("  [");
        for (int i = 0; i < total; i++) {
            if (i < available) {
                System.out.print("█");  // Available
            } else {
                System.out.print("░");  // Booked
            }
        }
        System.out.println("]");
        System.out.println("  (█ = Available, ░ = Booked)");
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
     * Display room occupancy statistics
     */
    public static void displayOccupancyStats() {
        System.out.println("\n========================================");
        System.out.println("    OCCUPANCY STATISTICS                ");
        System.out.println("========================================");

        int totalBooked = (TOTAL_SINGLE_ROOMS - singleRoomsAvailable) +
                (TOTAL_DOUBLE_ROOMS - doubleRoomsAvailable) +
                (TOTAL_SUITE_ROOMS - suiteRoomsAvailable);

        int totalAvailable = singleRoomsAvailable + doubleRoomsAvailable + suiteRoomsAvailable;
        int totalRooms = TOTAL_SINGLE_ROOMS + TOTAL_DOUBLE_ROOMS + TOTAL_SUITE_ROOMS;

        System.out.println("\nTotal Rooms: " + totalRooms);
        System.out.println("Booked Rooms: " + totalBooked);
        System.out.println("Available Rooms: " + totalAvailable);
        System.out.println("Occupancy Rate: " +
                String.format("%.2f%%", (totalBooked * 100.0) / totalRooms));

        System.out.println("\n========================================");
    }

    /**
     * Display summary of room pricing
     */
    public static void displayPricingSummary() {
        System.out.println("\n========================================");
        System.out.println("    PRICING SUMMARY                     ");
        System.out.println("========================================");

        System.out.println("\nSingle Room: ₹2000 per night");
        System.out.println("Double Room: ₹3500 per night");
        System.out.println("Suite Room: ₹6000 per night");

        System.out.println("\nNote: Prices shown are for one night stay");
        System.out.println("Additional taxes may apply");

        System.out.println("\n========================================");
    }

    // ============================================
    // MAIN METHOD
    // ============================================

    /**
     * Main method - Entry point of the application
     * Demonstrates room initialization and static availability
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        // Display welcome message
        displayWelcomeMessage();

        // Display all room types with details
        displayAllRoomTypes();

        // Display current availability
        displayAvailability();

        // Display pricing information
        displayPricingSummary();

        // Display occupancy statistics
        displayOccupancyStats();

        // Final status message
        System.out.println("\n========================================");
        System.out.println("UC2 Demonstration Complete!");
        System.out.println("Ready for booking requests in UC3...");
        System.out.println("========================================\n");
    }
}