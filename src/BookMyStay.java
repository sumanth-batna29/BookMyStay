import java.util.*;

/**
 * Hotel Booking Management System - Use Case 5
 *
 * Booking Request (First-Come-First-Served)
 *
 * This class demonstrates:
 * - Queue data structure for fair request ordering
 * - FIFO (First-Come-First-Served) principle
 * - Reservation model for booking intent
 * - Request intake without inventory mutation
 * - Fairness in booking allocation
 * - Decoupling request intake from allocation
 *
 * @author sumanth-batna29
 * @version 5.1
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
    // UC5: RESERVATION CLASS (NEW)
    // ============================================

    /**
     * UC5: Reservation class - Represents a guest's booking intent
     *
     * Encapsulates all information related to a booking request.
     * Contains guest details, room preference, and timestamps for ordering.
     */
    static class Reservation {

        private String reservationId;
        private String guestName;
        private String requestedRoomType;
        private int numberOfNights;
        private long requestTimestamp;
        private String status; // "Pending", "Approved", "Rejected"

        /**
         * UC5: Constructor - Create a new reservation
         *
         * @param reservationId Unique reservation ID
         * @param guestName Name of the guest
         * @param requestedRoomType Type of room requested
         * @param numberOfNights Number of nights for booking
         */
        public Reservation(String reservationId, String guestName,
                           String requestedRoomType, int numberOfNights) {
            this.reservationId = reservationId;
            this.guestName = guestName;
            this.requestedRoomType = requestedRoomType;
            this.numberOfNights = numberOfNights;
            this.requestTimestamp = System.currentTimeMillis();
            this.status = "Pending";
        }

        /**
         * UC5: Get reservation ID
         * @return reservation ID
         */
        public String getReservationId() {
            return reservationId;
        }

        /**
         * UC5: Get guest name
         * @return guest name
         */
        public String getGuestName() {
            return guestName;
        }

        /**
         * UC5: Get requested room type
         * @return room type
         */
        public String getRequestedRoomType() {
            return requestedRoomType;
        }

        /**
         * UC5: Get number of nights
         * @return number of nights
         */
        public int getNumberOfNights() {
            return numberOfNights;
        }

        /**
         * UC5: Get request timestamp
         * @return timestamp in milliseconds
         */
        public long getRequestTimestamp() {
            return requestTimestamp;
        }

        /**
         * UC5: Get reservation status
         * @return current status
         */
        public String getStatus() {
            return status;
        }

        /**
         * UC5: Set reservation status
         * @param status New status
         */
        public void setStatus(String status) {
            this.status = status;
        }

        /**
         * UC5: Display reservation details
         */
        public void displayDetails() {
            System.out.println("Reservation ID: " + reservationId);
            System.out.println("Guest Name: " + guestName);
            System.out.println("Room Type: " + requestedRoomType);
            System.out.println("Number of Nights: " + numberOfNights);
            System.out.println("Status: " + status);
        }

        @Override
        public String toString() {
            return "[" + reservationId + "] " + guestName +
                    " - " + requestedRoomType +
                    " (" + numberOfNights + " nights) - " + status;
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
         * Get available rooms for a specific room type
         * Time Complexity: O(1)
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
                return false;
            }

            int available = getAvailableRooms(roomType);
            if (available > 0) {
                inventoryMap.put(roomType, available - 1);
                return true;
            }
            return false;
        }

        /**
         * Display all room types and their availability
         */
        public void displayInventory() {
            System.out.println("\n========================================");
            System.out.println("    CENTRALIZED ROOM INVENTORY (HashMap) ");
            System.out.println("========================================");
            System.out.println("\nRoom Type Availability:\n");

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
        }
    }

    // ============================================
    // UC5: BOOKING REQUEST QUEUE CLASS (NEW)
    // ============================================

    /**
     * UC5: BookingRequestQueue class - FIFO booking request management
     *
     * Manages incoming booking requests using a Queue data structure.
     * Implements FIFO (First-Come-First-Served) principle for fairness.
     * Preserves arrival order of booking requests without inventory mutation.
     */
    static class BookingRequestQueue {

        // UC5: Queue for managing booking requests in FIFO order
        private Queue<Reservation> requestQueue;

        // Counter for generating unique reservation IDs
        private int reservationCounter;

        /**
         * UC5: Constructor - Initialize booking request queue
         */
        public BookingRequestQueue() {
            // UC5: LinkedList implements Queue interface
            this.requestQueue = new LinkedList<>();
            this.reservationCounter = 1000;
        }

        /**
         * UC5: Add a booking request to the queue
         * Time Complexity: O(1) - LinkedList offer operation
         * FIFO Principle: Request is added at the end
         *
         * @param guestName Name of the guest
         * @param requestedRoomType Type of room requested
         * @param numberOfNights Number of nights for booking
         * @return The created Reservation object
         */
        public Reservation addBookingRequest(String guestName,
                                             String requestedRoomType,
                                             int numberOfNights) {
            // UC5: Generate unique reservation ID
            String reservationId = "RES-" + (++reservationCounter);

            // UC5: Create new reservation
            Reservation reservation = new Reservation(reservationId, guestName,
                    requestedRoomType, numberOfNights);

            // UC5: Add to queue (FIFO - added at end)
            requestQueue.offer(reservation);  // or add()

            System.out.println("✓ Booking request added: " + reservationId);
            System.out.println("  Queue size: " + requestQueue.size());

            return reservation;
        }

        /**
         * UC5: Get the next booking request from queue (peek without removing)
         * Time Complexity: O(1)
         * FIFO Principle: Returns the first request (head of queue)
         *
         * @return Next Reservation to process, or null if queue is empty
         */
        public Reservation peekNextRequest() {
            return requestQueue.peek();
        }

        /**
         * UC5: Remove and return the next booking request from queue
         * Time Complexity: O(1)
         * FIFO Principle: Removes and returns the first request
         *
         * @return Next Reservation to process, or null if queue is empty
         */
        public Reservation pollNextRequest() {
            return requestQueue.poll();
        }

        /**
         * UC5: Check if queue has pending requests
         *
         * @return true if queue is not empty, false otherwise
         */
        public boolean hasPendingRequests() {
            return !requestQueue.isEmpty();
        }

        /**
         * UC5: Get number of pending requests in queue
         * Time Complexity: O(1)
         *
         * @return Number of requests waiting in queue
         */
        public int getPendingRequestCount() {
            return requestQueue.size();
        }

        /**
         * UC5: Display all pending booking requests in queue order
         * Time Complexity: O(n) where n = number of requests
         */
        public void displayPendingRequests() {
            System.out.println("\n========================================");
            System.out.println("    BOOKING REQUEST QUEUE (FIFO)        ");
            System.out.println("========================================");
            System.out.println("\nTotal Pending Requests: " + requestQueue.size());

            // UC5: Defensive check - handle empty queue
            if (requestQueue.isEmpty()) {
                System.out.println("\n✓ Queue is empty - no pending requests");
                System.out.println("\n========================================");
                return;
            }

            System.out.println("\nRequests in order (FIFO):\n");

            // UC5: Display requests in queue order
            // Note: We iterate through a copy to preserve queue
            Queue<Reservation> tempQueue = new LinkedList<>(requestQueue);
            int position = 1;

            while (!tempQueue.isEmpty()) {
                Reservation reservation = tempQueue.poll();
                System.out.println("  " + position + ". " + reservation);
                position++;
            }

            System.out.println("\n========================================");
        }

        /**
         * UC5: Display queue statistics
         */
        public void displayQueueStatistics() {
            System.out.println("\n========================================");
            System.out.println("    QUEUE STATISTICS                    ");
            System.out.println("========================================");

            System.out.println("\nTotal Requests in Queue: " + requestQueue.size());
            System.out.println("Queue Status: " +
                    (requestQueue.isEmpty() ? "Empty" : "Active"));

            // Count requests by room type
            Map<String, Integer> roomTypeCount = new HashMap<>();
            for (Reservation res : requestQueue) {
                String roomType = res.getRequestedRoomType();
                roomTypeCount.put(roomType, roomTypeCount.getOrDefault(roomType, 0) + 1);
            }

            System.out.println("\nRequests by Room Type:");
            for (Map.Entry<String, Integer> entry : roomTypeCount.entrySet()) {
                System.out.println("  " + entry.getKey() + ": " + entry.getValue());
            }

            System.out.println("\n========================================");
        }

        /**
         * UC5: Process first booking request from queue
         * Demonstrates FIFO allocation principle
         *
         * @param inventory RoomInventory for booking allocation
         * @return true if booking was successful, false otherwise
         */
        public boolean processNextBooking(RoomInventory inventory) {
            // UC5: Peek at next request without removing
            Reservation nextRequest = peekNextRequest();

            if (nextRequest == null) {
                System.out.println("✗ No requests to process - queue is empty");
                return false;
            }

            System.out.println("\nProcessing booking request: " + nextRequest.getReservationId());

            // UC5: Check if room is available
            String requestedRoom = nextRequest.getRequestedRoomType();
            if (inventory.getAvailableRooms(requestedRoom) > 0) {
                // UC5: Book the room
                if (inventory.bookRoom(requestedRoom)) {
                    // UC5: Update reservation status
                    nextRequest.setStatus("Approved");

                    // UC5: Remove from queue (FIFO processing)
                    pollNextRequest();

                    System.out.println("✓ Booking approved!");
                    System.out.println("  Reservation: " + nextRequest.getReservationId());
                    System.out.println("  Guest: " + nextRequest.getGuestName());
                    System.out.println("  Room: " + requestedRoom);
                    System.out.println("  Remaining queue: " + requestQueue.size());

                    return true;
                }
            } else {
                System.out.println("✗ No " + requestedRoom + " available");
                System.out.println("  Request remains in queue");
            }

            return false;
        }

        /**
         * UC5: Clear all pending requests from queue
         * Use with caution
         */
        public void clearQueue() {
            int clearedCount = requestQueue.size();
            requestQueue.clear();
            System.out.println("✓ Queue cleared - " + clearedCount + " requests removed");
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
        System.out.println("Version: 5.1");
        System.out.println("Use Case 5: Booking Request (First-Come-First-Served)");
        System.out.println("========================================\n");
    }

    /**
     * UC5: Demonstrate multiple guest booking requests
     *
     * @param requestQueue BookingRequestQueue instance
     * @param inventory RoomInventory instance
     */
    public static void demonstrateBookingRequests(BookingRequestQueue requestQueue,
                                                  RoomInventory inventory) {
        System.out.println("\n========================================");
        System.out.println("    GUEST BOOKING REQUESTS               ");
        System.out.println("========================================");

        // UC5: Scenario 1 - Multiple guests submit requests simultaneously
        System.out.println("\n--- SCENARIO 1: Multiple booking requests arrive ---");
        System.out.println("(Simulating peak demand - requests submitted in quick succession)\n");

        requestQueue.addBookingRequest("Rajesh Kumar", "Single Room", 3);
        requestQueue.addBookingRequest("Priya Sharma", "Double Room", 2);
        requestQueue.addBookingRequest("Amit Patel", "Suite Room", 4);
        requestQueue.addBookingRequest("Neha Singh", "Double Room", 1);
        requestQueue.addBookingRequest("Vikram Gupta", "Single Room", 5);
        requestQueue.addBookingRequest("Anjali Verma", "Suite Room", 2);

        // UC5: Display queue status
        System.out.println("\n--- SCENARIO 2: View pending requests (FIFO order) ---");
        requestQueue.displayPendingRequests();

        // UC5: Display queue statistics
        System.out.println("\n--- SCENARIO 3: Queue statistics ---");
        requestQueue.displayQueueStatistics();

        // UC5: Process requests one by one (FIFO principle)
        System.out.println("\n--- SCENARIO 4: Process booking requests (FIFO order) ---");
        System.out.println("(Processing in the exact order they were received)\n");

        int processedCount = 0;
        while (requestQueue.hasPendingRequests() && processedCount < 4) {
            requestQueue.processNextBooking(inventory);
            processedCount++;
        }

        // UC5: Display remaining queue
        System.out.println("\n--- SCENARIO 5: Remaining pending requests ---");
        requestQueue.displayPendingRequests();

        // UC5: Display current inventory
        System.out.println("\n--- SCENARIO 6: Current inventory after processing ---");
        inventory.displayInventory();
    }

    // ============================================
    // MAIN METHOD
    // ============================================

    /**
     * Main method - Entry point of the application
     * Demonstrates UC5: Booking request queue management with FIFO principle
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        // Display welcome message
        displayWelcomeMessage();

        // Create and initialize centralized inventory
        System.out.println("--- STEP 1: Initialize System ---");
        RoomInventory inventory = new RoomInventory();
        inventory.displayInventory();

        // UC5: Create booking request queue (NEW)
        System.out.println("\n--- STEP 2: Initialize Booking Request Queue ---");
        BookingRequestQueue requestQueue = new BookingRequestQueue();
        System.out.println("✓ Booking request queue initialized!");
        System.out.println("  Data Structure: Queue (LinkedList)");
        System.out.println("  Processing Model: FIFO (First-Come-First-Served)");

        // UC5: Demonstrate booking requests (NEW)
        System.out.println("\n--- STEP 3: Demonstrate Booking Requests ---");
        demonstrateBookingRequests(requestQueue, inventory);

        // Final status message
        System.out.println("\n========================================");
        System.out.println("UC5 Demonstration Complete!");
        System.out.println("Booking request queue established.");
        System.out.println("FIFO fairness principle demonstrated.");
        System.out.println("Ready for advanced queue management in UC6...");
        System.out.println("========================================\n");

        // Display UC5 advantages
        System.out.println("========================================");
        System.out.println("    UC5 ADVANTAGES - FAIR FIFO BOOKING  ");
        System.out.println("========================================");
        System.out.println("\n✓ Queue data structure ensures FIFO order");
        System.out.println("✓ Fair allocation - first come, first served");
        System.out.println("✓ O(1) average-time insertion and removal");
        System.out.println("✓ Request ordering preserved automatically");
        System.out.println("✓ Decoupled request intake from allocation");
        System.out.println("✓ Handles peak demand fairly");
        System.out.println("✓ Scales well with multiple simultaneous requests");
        System.out.println("✓ Eliminates unfair booking advantage");
        System.out.println("\n========================================\n");

        // Display Queue vs Other Structures
        System.out.println("========================================");
        System.out.println("    WHY QUEUE FOR BOOKING REQUESTS?     ");
        System.out.println("========================================");
        System.out.println("\nComparison with other data structures:\n");
        System.out.println("Stack:");
        System.out.println("  ✗ LIFO (Last-In-First-Out)");
        System.out.println("  ✗ Unfair - latest requests processed first");
        System.out.println("  ✗ Not suitable for booking fairness\n");

        System.out.println("List/ArrayList:");
        System.out.println("  ? Can maintain order but no inherent FIFO");
        System.out.println("  ✗ Requires manual index management");
        System.out.println("  ✗ Less efficient for queue operations\n");

        System.out.println("Queue/LinkedList:");
        System.out.println("  ✓ FIFO (First-In-First-Out)");
        System.out.println("  ✓ Natural fairness - first request processed first");
        System.out.println("  ✓ O(1) add and remove operations");
        System.out.println("  ✓ Perfect for booking systems");
        System.out.println("\n========================================\n");
    }
}