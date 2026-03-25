import java.util.*;

/**
 * Hotel Booking Management System - Use Case 4
 *
 * Room Search & Availability Check
 *
 * This class demonstrates:
 * - Read-only access to inventory and room data
 * - Defensive programming with validation checks
 * - Separation of concerns (search vs. booking)
 * - Filtering available rooms
 * - Safe data access patterns
 * - Room search service with guest interactions
 *
 * @author sumanth-batna29
 * @version 4.1
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

        /**
         * Get number of beds
         * @return number of beds
         */
        public int getNumberOfBeds() {
            return numberOfBeds;
        }

        /**
         * Get room size
         * @return room size in square feet
         */
        public int getRoomSize() {
            return roomSize;
        }

        /**
         * Get amenities
         * @return amenities string
         */
        public String getAmenities() {
            return amenities;
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
    // ROOM INVENTORY CLASS
    // ============================================

    /**
     * RoomInventory class - Centralized inventory management
     *
     * Encapsulates all inventory-related operations using HashMap.
     * This class maintains a single source of truth for room availability.
     * Provides controlled access and updates to room inventory.
     */
    static class RoomInventory {

        // HashMap for centralized room availability
        private HashMap<String, Integer> inventoryMap;

        // Total room counts (immutable reference)
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
         * Initialize inventory with all room types
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
         * UC4: Get available rooms for a specific room type (READ-ONLY)
         * Time Complexity: O(1) - HashMap get operation
         *
         * @param roomType Type of room
         * @return Number of available rooms, or 0 if room type not found
         */
        public int getAvailableRooms(String roomType) {
            return inventoryMap.getOrDefault(roomType, 0);
        }

        /**
         * Get total rooms for a specific room type
         *
         * @param roomType Type of room
         * @return Total number of rooms of this type
         */
        public int getTotalRooms(String roomType) {
            return totalRoomsMap.getOrDefault(roomType, 0);
        }

        /**
         * Check if room type exists in inventory
         * Time Complexity: O(1)
         *
         * @param roomType Type of room
         * @return true if room type exists, false otherwise
         */
        public boolean roomTypeExists(String roomType) {
            return inventoryMap.containsKey(roomType);
        }

        /**
         * Book a room (decrement availability)
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
         * Cancel a booking (increment availability)
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
         * Display all room types and their availability
         * Uses entrySet() for efficient iteration over HashMap
         * Time Complexity: O(n) where n = number of room types
         */
        public void displayInventory() {
            System.out.println("\n========================================");
            System.out.println("    CENTRALIZED ROOM INVENTORY (HashMap) ");
            System.out.println("========================================");
            System.out.println("\nRoom Type Availability:\n");

            // Iterate using entrySet() for key-value pairs
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
         * Display inventory as a visual bar
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
         * Get occupancy statistics
         * Time Complexity: O(n) where n = number of room types
         */
        public void displayOccupancyStats() {
            System.out.println("\n========================================");
            System.out.println("    OCCUPANCY STATISTICS                ");
            System.out.println("========================================");

            int totalBooked = 0;
            int totalAvailable = 0;
            int totalRooms = 0;

            // Iterate using keySet()
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
    }

    // ============================================
    // UC4: ROOM SEARCH SERVICE CLASS (NEW)
    // ============================================

    /**
     * UC4: RoomSearchService class - Read-only search and availability check
     *
     * Provides guests with the ability to search for available rooms
     * without modifying system state. Implements defensive programming
     * with validation checks and safe data access patterns.
     */
    static class RoomSearchService {

        // Reference to inventory (read-only access)
        private RoomInventory inventory;

        // Room objects for domain information
        private Map<String, Room> roomCatalog;

        /**
         * UC4: Constructor - Initialize search service
         *
         * @param inventory Reference to centralized inventory
         */
        public RoomSearchService(RoomInventory inventory) {
            this.inventory = inventory;
            this.roomCatalog = new HashMap<>();
            initializeRoomCatalog();
        }

        /**
         * UC4: Initialize room catalog with all room types
         * Separates room domain information from inventory state
         */
        private void initializeRoomCatalog() {
            roomCatalog.put("Single Room", new SingleRoom());
            roomCatalog.put("Double Room", new DoubleRoom());
            roomCatalog.put("Suite Room", new SuiteRoom());
        }

        /**
         * UC4: Search for available rooms
         * READ-ONLY operation - does not modify inventory
         * Filters rooms to show only those with availability > 0
         *
         * @return List of available room types
         */
        public List<String> searchAvailableRooms() {
            List<String> availableRooms = new ArrayList<>();

            // UC4: Iterate through room catalog
            for (String roomType : roomCatalog.keySet()) {
                // UC4: Defensive check - verify availability before adding
                int available = inventory.getAvailableRooms(roomType);

                // UC4: Validation logic - include only rooms with availability > 0
                if (available > 0) {
                    availableRooms.add(roomType);
                }
            }

            return availableRooms;
        }

        /**
         * UC4: Check availability of a specific room type
         * READ-ONLY operation
         *
         * @param roomType Type of room to check
         * @return true if room is available, false otherwise
         */
        public boolean isRoomAvailable(String roomType) {
            // UC4: Defensive programming - verify room exists first
            if (!inventory.roomTypeExists(roomType)) {
                return false;
            }

            // UC4: Check if availability is greater than 0
            return inventory.getAvailableRooms(roomType) > 0;
        }

        /**
         * UC4: Get available count for a room type
         * READ-ONLY operation
         *
         * @param roomType Type of room
         * @return Number of available rooms
         */
        public int getAvailabilityCount(String roomType) {
            return inventory.getAvailableRooms(roomType);
        }

        /**
         * UC4: Get room details by type
         * READ-ONLY operation - returns room information without state changes
         *
         * @param roomType Type of room
         * @return Room object with details, or null if not found
         */
        public Room getRoomDetails(String roomType) {
            return roomCatalog.get(roomType);
        }

        /**
         * UC4: Display detailed search results
         * Shows available rooms with full details and pricing
         * READ-ONLY operation
         */
        public void displaySearchResults() {
            System.out.println("\n========================================");
            System.out.println("    AVAILABLE ROOMS - SEARCH RESULTS    ");
            System.out.println("========================================\n");

            // UC4: Get list of available rooms
            List<String> availableRooms = searchAvailableRooms();

            // UC4: Defensive check - handle case when no rooms are available
            if (availableRooms.isEmpty()) {
                System.out.println("✗ Sorry! No rooms are currently available.");
                System.out.println("Please try again later.\n");
                return;
            }

            System.out.println("Found " + availableRooms.size() + " available room type(s):\n");

            // UC4: Display each available room with full details
            int serialNo = 1;
            for (String roomType : availableRooms) {
                Room room = getRoomDetails(roomType);
                int available = getAvailabilityCount(roomType);

                // UC4: Defensive check - ensure room object exists
                if (room != null) {
                    System.out.println("  " + serialNo + ". " + roomType);
                    System.out.println("     Price: ₹" + room.getPricePerNight() + " per night");
                    System.out.println("     Beds: " + room.getNumberOfBeds());
                    System.out.println("     Size: " + room.getRoomSize() + " sq ft");
                    System.out.println("     Amenities: " + room.getAmenities());
                    System.out.println("     Available: " + available + " room(s)");
                    System.out.println();

                    serialNo++;
                }
            }

            System.out.println("========================================");
        }

        /**
         * UC4: Filter rooms by price range
         * READ-ONLY operation - returns filtered list
         *
         * @param minPrice Minimum price
         * @param maxPrice Maximum price
         * @return List of room types within price range and available
         */
        public List<String> filterByPrice(double minPrice, double maxPrice) {
            List<String> filteredRooms = new ArrayList<>();

            // UC4: Search available rooms first
            List<String> availableRooms = searchAvailableRooms();

            // UC4: Filter by price range
            for (String roomType : availableRooms) {
                Room room = getRoomDetails(roomType);

                if (room != null && room.getPricePerNight() >= minPrice &&
                        room.getPricePerNight() <= maxPrice) {
                    filteredRooms.add(roomType);
                }
            }

            return filteredRooms;
        }

        /**
         * UC4: Filter rooms by number of beds
         * READ-ONLY operation
         *
         * @param numberOfBeds Number of beds to filter by
         * @return List of room types with specified bed count and available
         */
        public List<String> filterByBeds(int numberOfBeds) {
            List<String> filteredRooms = new ArrayList<>();

            List<String> availableRooms = searchAvailableRooms();

            for (String roomType : availableRooms) {
                Room room = getRoomDetails(roomType);

                if (room != null && room.getNumberOfBeds() == numberOfBeds) {
                    filteredRooms.add(roomType);
                }
            }

            return filteredRooms;
        }

        /**
         * UC4: Display search statistics
         * Provides insights about available inventory
         * READ-ONLY operation
         */
        public void displaySearchStatistics() {
            System.out.println("\n========================================");
            System.out.println("    SEARCH STATISTICS                   ");
            System.out.println("========================================");

            List<String> availableRooms = searchAvailableRooms();

            System.out.println("\nAvailable Room Types: " + availableRooms.size());
            System.out.println("Available Rooms List: " + availableRooms);

            int totalAvailableCount = 0;
            for (String roomType : availableRooms) {
                totalAvailableCount += getAvailabilityCount(roomType);
            }

            System.out.println("Total Available Rooms: " + totalAvailableCount);

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
        System.out.println("Version: 4.1");
        System.out.println("Use Case 4: Room Search & Availability Check");
        System.out.println("========================================\n");
    }

    /**
     * UC4: Demonstrate guest search scenarios
     *
     * @param searchService RoomSearchService instance
     */
    public static void demonstrateGuestSearches(RoomSearchService searchService) {
        System.out.println("\n========================================");
        System.out.println("    GUEST SEARCH SCENARIOS               ");
        System.out.println("========================================");

        // UC4: Scenario 1 - Search all available rooms
        System.out.println("\n--- SCENARIO 1: Guest searches for all available rooms ---");
        searchService.displaySearchResults();

        // UC4: Scenario 2 - Search statistics
        System.out.println("\n--- SCENARIO 2: View search statistics ---");
        searchService.displaySearchStatistics();

        // UC4: Scenario 3 - Filter by price range
        System.out.println("\n--- SCENARIO 3: Filter rooms by price (₹2000 - ₹4000) ---");
        List<String> priceFiltered = searchService.filterByPrice(2000, 4000);
        System.out.println("Rooms within budget: " + priceFiltered);

        // UC4: Scenario 4 - Filter by number of beds
        System.out.println("\n--- SCENARIO 4: Filter rooms with 2 beds ---");
        List<String> bedFiltered = searchService.filterByBeds(2);
        System.out.println("Rooms with 2 beds: " + bedFiltered);

        // UC4: Scenario 5 - Check specific room availability
        System.out.println("\n--- SCENARIO 5: Check specific room availability ---");
        System.out.println("Is Single Room available? " +
                searchService.isRoomAvailable("Single Room"));
        System.out.println("Is Suite Room available? " +
                searchService.isRoomAvailable("Suite Room"));
        System.out.println("Single Room availability: " +
                searchService.getAvailabilityCount("Single Room"));
    }

    // ============================================
    // MAIN METHOD
    // ============================================

    /**
     * Main method - Entry point of the application
     * Demonstrates UC4: Room search and availability checking
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        // Display welcome message
        displayWelcomeMessage();

        // Create and initialize centralized inventory
        System.out.println("--- STEP 1: Initialize System ---");
        RoomInventory inventory = new RoomInventory();

        // Display initial inventory
        inventory.displayInventory();

        // Perform some bookings to change inventory state
        System.out.println("\n--- STEP 2: Simulate Some Bookings ---");
        inventory.bookRoom("Single Room");
        inventory.bookRoom("Single Room");
        inventory.bookRoom("Double Room");
        inventory.bookRoom("Double Room");
        inventory.bookRoom("Suite Room");

        // Display updated inventory after bookings
        inventory.displayInventory();
        inventory.displayOccupancyStats();

        // UC4: Create room search service (NEW)
        System.out.println("\n--- STEP 3: Initialize Room Search Service ---");
        RoomSearchService searchService = new RoomSearchService(inventory);
        System.out.println("✓ Search service initialized successfully!");

        // UC4: Demonstrate guest search scenarios (NEW)
        System.out.println("\n--- STEP 4: Demonstrate Guest Search Operations ---");
        demonstrateGuestSearches(searchService);

        // Verify inventory has not been modified by search operations
        System.out.println("\n--- STEP 5: Verify Inventory Unchanged After Searches ---");
        System.out.println("✓ Search operations are READ-ONLY");
        System.out.println("✓ Inventory state has NOT been modified");
        inventory.displayInventory();

        // Final status message
        System.out.println("\n========================================");
        System.out.println("UC4 Demonstration Complete!");
        System.out.println("Room search functionality established.");
        System.out.println("Separation of read and write operations confirmed.");
        System.out.println("Ready for booking queue management in UC5...");
        System.out.println("========================================\n");

        // Display UC4 advantages
        System.out.println("========================================");
        System.out.println("    UC4 ADVANTAGES - SAFE SEARCH ACCESS ");
        System.out.println("========================================");
        System.out.println("\n✓ Read-only search operations");
        System.out.println("✓ Defensive programming with validation checks");
        System.out.println("✓ Separation of search from booking logic");
        System.out.println("✓ Inventory remains consistent and safe");
        System.out.println("✓ Clear separation of concerns");
        System.out.println("✓ Filtering capabilities for guest preferences");
        System.out.println("✓ No unintended side effects");
        System.out.println("\n========================================\n");
    }
}