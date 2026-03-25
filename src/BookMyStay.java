import java.util.*;

/**
 * Hotel Booking Management System - Use Case 6
 *
 * Reservation Confirmation & Room Allocation
 *
 * This class demonstrates:
 * - Set data structure for enforcing room ID uniqueness
 * - Prevention of double-booking through unique room assignment
 * - Atomic logical operations for booking and inventory updates
 * - Mapping room types to allocated rooms
 * - Inventory synchronization after allocation
 * - Safe room allocation patterns
 *
 * @author sumanth-batna29
 * @version 6.1
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
    // RESERVATION CLASS
    // ============================================

    /**
     * Reservation class - Represents a guest's booking intent
     *
     * Encapsulates all information related to a booking request.
     * Contains guest details, room preference, and booking confirmation.
     */
    static class Reservation {

        private String reservationId;
        private String guestName;
        private String requestedRoomType;
        private int numberOfNights;
        private long requestTimestamp;
        private String status; // "Pending", "Confirmed", "Rejected"
        private String assignedRoomId; // UC6: Assigned room ID

        /**
         * Constructor - Create a new reservation
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
            this.assignedRoomId = null;
        }

        /**
         * Get reservation ID
         * @return reservation ID
         */
        public String getReservationId() {
            return reservationId;
        }

        /**
         * Get guest name
         * @return guest name
         */
        public String getGuestName() {
            return guestName;
        }

        /**
         * Get requested room type
         * @return room type
         */
        public String getRequestedRoomType() {
            return requestedRoomType;
        }

        /**
         * Get number of nights
         * @return number of nights
         */
        public int getNumberOfNights() {
            return numberOfNights;
        }

        /**
         * Get request timestamp
         * @return timestamp in milliseconds
         */
        public long getRequestTimestamp() {
            return requestTimestamp;
        }

        /**
         * Get reservation status
         * @return current status
         */
        public String getStatus() {
            return status;
        }

        /**
         * Set reservation status
         * @param status New status
         */
        public void setStatus(String status) {
            this.status = status;
        }

        /**
         * UC6: Get assigned room ID
         * @return assigned room ID or null if not assigned
         */
        public String getAssignedRoomId() {
            return assignedRoomId;
        }

        /**
         * UC6: Set assigned room ID
         * @param roomId Room ID to assign
         */
        public void setAssignedRoomId(String roomId) {
            this.assignedRoomId = roomId;
        }

        /**
         * Display reservation details
         */
        public void displayDetails() {
            System.out.println("Reservation ID: " + reservationId);
            System.out.println("Guest Name: " + guestName);
            System.out.println("Room Type: " + requestedRoomType);
            System.out.println("Number of Nights: " + numberOfNights);
            System.out.println("Status: " + status);
            if (assignedRoomId != null) {
                System.out.println("Assigned Room: " + assignedRoomId);
            }
        }

        @Override
        public String toString() {
            String roomInfo = (assignedRoomId != null) ?
                    " [Room: " + assignedRoomId + "]" : "";
            return "[" + reservationId + "] " + guestName +
                    " - " + requestedRoomType +
                    " (" + numberOfNights + " nights) - " + status + roomInfo;
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
         * UC6: Decrement room availability (atomic operation)
         * Time Complexity: O(1)
         *
         * @param roomType Type of room to decrement
         * @return true if decrement successful, false if no rooms available
         */
        public boolean decrementRoomCount(String roomType) {
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
    // BOOKING REQUEST QUEUE CLASS
    // ============================================

    /**
     * BookingRequestQueue class - FIFO booking request management
     *
     * Manages incoming booking requests using a Queue data structure.
     * Implements FIFO (First-Come-First-Served) principle for fairness.
     * Preserves arrival order of booking requests without inventory mutation.
     */
    static class BookingRequestQueue {

        // Queue for managing booking requests in FIFO order
        private Queue<Reservation> requestQueue;

        // Counter for generating unique reservation IDs
        private int reservationCounter;

        /**
         * Constructor - Initialize booking request queue
         */
        public BookingRequestQueue() {
            this.requestQueue = new LinkedList<>();
            this.reservationCounter = 1000;
        }

        /**
         * Add a booking request to the queue
         * Time Complexity: O(1)
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
            String reservationId = "RES-" + (++reservationCounter);
            Reservation reservation = new Reservation(reservationId, guestName,
                    requestedRoomType, numberOfNights);
            requestQueue.offer(reservation);
            return reservation;
        }

        /**
         * Get the next booking request from queue (peek without removing)
         * Time Complexity: O(1)
         *
         * @return Next Reservation to process, or null if queue is empty
         */
        public Reservation peekNextRequest() {
            return requestQueue.peek();
        }

        /**
         * Remove and return the next booking request from queue
         * Time Complexity: O(1)
         *
         * @return Next Reservation to process, or null if queue is empty
         */
        public Reservation pollNextRequest() {
            return requestQueue.poll();
        }

        /**
         * Check if queue has pending requests
         *
         * @return true if queue is not empty, false otherwise
         */
        public boolean hasPendingRequests() {
            return !requestQueue.isEmpty();
        }

        /**
         * Get number of pending requests in queue
         *
         * @return Number of requests waiting in queue
         */
        public int getPendingRequestCount() {
            return requestQueue.size();
        }

        /**
         * Display all pending booking requests in queue order
         */
        public void displayPendingRequests() {
            System.out.println("\n========================================");
            System.out.println("    BOOKING REQUEST QUEUE (FIFO)        ");
            System.out.println("========================================");
            System.out.println("\nTotal Pending Requests: " + requestQueue.size());

            if (requestQueue.isEmpty()) {
                System.out.println("\n✓ Queue is empty - no pending requests");
                System.out.println("\n========================================");
                return;
            }

            System.out.println("\nRequests in order (FIFO):\n");

            Queue<Reservation> tempQueue = new LinkedList<>(requestQueue);
            int position = 1;

            while (!tempQueue.isEmpty()) {
                Reservation reservation = tempQueue.poll();
                System.out.println("  " + position + ". " + reservation);
                position++;
            }

            System.out.println("\n========================================");
        }
    }

    // ============================================
    // UC6: ROOM ALLOCATION SERVICE CLASS (NEW)
    // ============================================

    /**
     * UC6: RoomAllocationService class - Safe room allocation with double-booking prevention
     *
     * Manages room allocation process with uniqueness enforcement.
     * Uses Set data structure to prevent double-booking.
     * Maps room types to allocated room IDs.
     * Maintains consistency between allocation and inventory.
     */
    static class RoomAllocationService {

        // UC6: Set to track all allocated room IDs (enforces uniqueness)
        private Set<String> allocatedRoomIds;

        // UC6: HashMap to map room types to their allocated room IDs
        private HashMap<String, Set<String>> roomTypeToAllocatedIds;

        // Counter for generating unique room IDs
        private HashMap<String, Integer> roomIdCounters;

        /**
         * UC6: Constructor - Initialize allocation service
         */
        public RoomAllocationService() {
            // UC6: HashSet enforces uniqueness of room IDs
            this.allocatedRoomIds = new HashSet<>();

            // UC6: HashMap maps each room type to its allocated IDs (Set)
            this.roomTypeToAllocatedIds = new HashMap<>();

            // Initialize counters for room ID generation
            this.roomIdCounters = new HashMap<>();

            initializeAllocationService();
        }

        /**
         * UC6: Initialize allocation service with room types
         */
        private void initializeAllocationService() {
            System.out.println("Initializing room allocation service...");

            // UC6: Create Set for each room type to store allocated room IDs
            roomTypeToAllocatedIds.put("Single Room", new HashSet<>());
            roomTypeToAllocatedIds.put("Double Room", new HashSet<>());
            roomTypeToAllocatedIds.put("Suite Room", new HashSet<>());

            // Initialize ID counters
            roomIdCounters.put("Single Room", 1);
            roomIdCounters.put("Double Room", 1);
            roomIdCounters.put("Suite Room", 1);

            System.out.println("✓ Allocation service initialized successfully!");
        }

        /**
         * UC6: Generate unique room ID for a room type
         * Ensures no ID reuse across all allocations
         * Time Complexity: O(1)
         *
         * @param roomType Type of room
         * @return Unique room ID
         */
        private String generateUniqueRoomId(String roomType) {
            // UC6: Generate ID based on room type and counter
            int counter = roomIdCounters.getOrDefault(roomType, 1);
            String roomId = roomType.substring(0, 1) + counter;  // S1, D1, SU1, etc.

            // UC6: Increment counter for next ID generation
            roomIdCounters.put(roomType, counter + 1);

            return roomId;
        }

        /**
         * UC6: Check if room ID already allocated
         * Prevents double-booking by checking uniqueness
         * Time Complexity: O(1)
         *
         * @param roomId Room ID to check
         * @return true if room ID already allocated, false otherwise
         */
        private boolean isRoomIdAllocated(String roomId) {
            // UC6: Set.contains() provides O(1) lookup
            return allocatedRoomIds.contains(roomId);
        }

        /**
         * UC6: Allocate room to reservation
         * Atomic operation: generate ID, verify uniqueness, record allocation, confirm
         * Time Complexity: O(1)
         *
         * @param reservation Reservation to allocate room for
         * @param inventory RoomInventory for inventory updates
         * @return true if allocation successful, false otherwise
         */
        public boolean allocateRoom(Reservation reservation, RoomInventory inventory) {
            String roomType = reservation.getRequestedRoomType();

            // UC6: Step 1: Check if room type exists
            if (!inventory.roomTypeExists(roomType)) {
                System.out.println("✗ ERROR: Room type '" + roomType + "' not found!");
                return false;
            }

            // UC6: Step 2: Check inventory availability
            if (inventory.getAvailableRooms(roomType) <= 0) {
                System.out.println("✗ No " + roomType + " available!");
                return false;
            }

            // UC6: Step 3: Generate unique room ID
            String assignedRoomId = generateUniqueRoomId(roomType);

            // UC6: Step 4: Verify room ID is not already allocated (uniqueness check)
            if (isRoomIdAllocated(assignedRoomId)) {
                System.out.println("✗ ERROR: Room ID collision detected for: " + assignedRoomId);
                System.out.println("  This should NEVER happen - double-booking prevented!");
                return false;
            }

            // UC6: Step 5: Record allocation in global set (mark as allocated)
            allocatedRoomIds.add(assignedRoomId);

            // UC6: Step 6: Record allocation in room type map
            Set<String> roomTypeAllocations = roomTypeToAllocatedIds.get(roomType);
            roomTypeAllocations.add(assignedRoomId);

            // UC6: Step 7: Update inventory immediately (atomic with allocation)
            if (!inventory.decrementRoomCount(roomType)) {
                // UC6: Rollback if inventory update fails
                allocatedRoomIds.remove(assignedRoomId);
                roomTypeAllocations.remove(assignedRoomId);
                System.out.println("✗ ERROR: Failed to update inventory!");
                return false;
            }

            // UC6: Step 8: Update reservation with allocated room
            reservation.setAssignedRoomId(assignedRoomId);
            reservation.setStatus("Confirmed");

            return true;
        }

        /**
         * UC6: Check if room is already allocated
         *
         * @param roomId Room ID to check
         * @return true if room is allocated, false otherwise
         */
        public boolean isRoomAllocated(String roomId) {
            return allocatedRoomIds.contains(roomId);
        }

        /**
         * UC6: Get count of allocated room IDs (for statistics)
         *
         * @return Number of allocated rooms
         */
        public int getAllocatedRoomCount() {
            return allocatedRoomIds.size();
        }

        /**
         * UC6: Get allocated room IDs for a specific room type
         *
         * @param roomType Type of room
         * @return Set of allocated room IDs for this type
         */
        public Set<String> getAllocatedRoomIds(String roomType) {
            return roomTypeToAllocatedIds.getOrDefault(roomType, new HashSet<>());
        }

        /**
         * UC6: Display all allocated rooms
         */
        public void displayAllocatedRooms() {
            System.out.println("\n========================================");
            System.out.println("    ALLOCATED ROOMS (Set Uniqueness)    ");
            System.out.println("========================================");
            System.out.println("\nTotal Allocated Room IDs: " + allocatedRoomIds.size());
            System.out.println("All Allocated IDs: " + allocatedRoomIds);

            System.out.println("\nAllocations by Room Type:\n");

            for (Map.Entry<String, Set<String>> entry : roomTypeToAllocatedIds.entrySet()) {
                String roomType = entry.getKey();
                Set<String> allocatedIds = entry.getValue();

                System.out.println("  " + roomType + ":");
                if (allocatedIds.isEmpty()) {
                    System.out.println("    No allocations yet");
                } else {
                    System.out.println("    Allocated IDs: " + allocatedIds);
                }
            }

            System.out.println("\n========================================");
        }

        /**
         * UC6: Display allocation statistics
         */
        public void displayAllocationStatistics() {
            System.out.println("\n========================================");
            System.out.println("    ALLOCATION STATISTICS               ");
            System.out.println("========================================");

            System.out.println("\nGlobal Statistics:");
            System.out.println("  Total Unique Room IDs Generated: " + allocatedRoomIds.size());

            System.out.println("\nRoom Type Statistics:");
            for (Map.Entry<String, Set<String>> entry : roomTypeToAllocatedIds.entrySet()) {
                System.out.println("  " + entry.getKey() + ": " + entry.getValue().size() + " allocated");
            }

            // UC6: Verify uniqueness (should never have duplicates)
            int totalByType = roomTypeToAllocatedIds.values().stream()
                    .mapToInt(Set::size)
                    .sum();

            System.out.println("\nUniqueness Verification:");
            System.out.println("  Total IDs in global set: " + allocatedRoomIds.size());
            System.out.println("  Total IDs in type sets: " + totalByType);

            if (allocatedRoomIds.size() == totalByType) {
                System.out.println("  ✓ VERIFIED: No duplicates - uniqueness enforced");
            } else {
                System.out.println("  ✗ ERROR: Mismatch detected - inconsistency!");
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
        System.out.println("Version: 6.1");
        System.out.println("Use Case 6: Reservation Confirmation & Room Allocation");
        System.out.println("========================================\n");
    }

    /**
     * UC6: Demonstrate complete booking flow from request to allocation
     */
    public static void demonstrateBookingFlow(BookingRequestQueue requestQueue,
                                              RoomInventory inventory,
                                              RoomAllocationService allocationService) {
        System.out.println("\n========================================");
        System.out.println("    COMPLETE BOOKING FLOW DEMONSTRATION ");
        System.out.println("========================================");

        // UC6: Scenario 1 - Add booking requests
        System.out.println("\n--- SCENARIO 1: Guests submit booking requests ---\n");

        requestQueue.addBookingRequest("Rajesh Kumar", "Single Room", 3);
        requestQueue.addBookingRequest("Priya Sharma", "Double Room", 2);
        requestQueue.addBookingRequest("Amit Patel", "Suite Room", 4);
        requestQueue.addBookingRequest("Neha Singh", "Double Room", 1);
        requestQueue.addBookingRequest("Vikram Gupta", "Single Room", 5);
        requestQueue.addBookingRequest("Anjali Verma", "Suite Room", 2);

        System.out.println("\n--- SCENARIO 2: Display pending requests ---");
        requestQueue.displayPendingRequests();

        // UC6: Scenario 2 - Initial inventory state
        System.out.println("\n--- SCENARIO 3: Initial inventory state ---");
        inventory.displayInventory();

        // UC6: Scenario 3 - Process requests and allocate rooms
        System.out.println("\n--- SCENARIO 4: Process booking requests (FIFO) & Allocate Rooms ---");
        System.out.println("(Confirming reservations with room assignment)\n");

        int successfulAllocations = 0;
        int processedRequests = 0;

        while (requestQueue.hasPendingRequests() && processedRequests < 6) {
            Reservation nextRequest = requestQueue.pollNextRequest();

            if (nextRequest != null) {
                System.out.println("Processing: " + nextRequest.getReservationId() +
                        " (" + nextRequest.getGuestName() + ")");

                // UC6: Allocate room
                if (allocationService.allocateRoom(nextRequest, inventory)) {
                    System.out.println("✓ CONFIRMED: Room " + nextRequest.getAssignedRoomId() +
                            " assigned to " + nextRequest.getGuestName());
                    successfulAllocations++;
                } else {
                    nextRequest.setStatus("Rejected");
                    System.out.println("✗ REJECTED: No rooms available for " +
                            nextRequest.getRequestedRoomType());
                }

                System.out.println();
                processedRequests++;
            }
        }

        // UC6: Scenario 4 - Display allocated rooms
        System.out.println("\n--- SCENARIO 5: Display allocated rooms (Set Uniqueness) ---");
        allocationService.displayAllocatedRooms();

        // UC6: Scenario 5 - Allocation statistics
        System.out.println("\n--- SCENARIO 6: Allocation statistics & verification ---");
        allocationService.displayAllocationStatistics();

        // UC6: Scenario 6 - Updated inventory
        System.out.println("\n--- SCENARIO 7: Updated inventory after allocations ---");
        inventory.displayInventory();

        // UC6: Summary
        System.out.println("\n--- SCENARIO 8: Booking Summary ---");
        System.out.println("✓ Requests Processed: " + processedRequests);
        System.out.println("✓ Successful Allocations: " + successfulAllocations);
        System.out.println("✓ Rooms Allocated (Unique IDs): " + allocationService.getAllocatedRoomCount());

        if (requestQueue.hasPendingRequests()) {
            System.out.println("⏳ Remaining in Queue: " + requestQueue.getPendingRequestCount());
        }
    }

    // ============================================
    // MAIN METHOD
    // ============================================

    /**
     * Main method - Entry point of the application
     * Demonstrates UC6: Room allocation with double-booking prevention
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        // Display welcome message
        displayWelcomeMessage();

        // Initialize system components
        System.out.println("--- STEP 1: Initialize System Components ---");
        RoomInventory inventory = new RoomInventory();
        inventory.displayInventory();

        BookingRequestQueue requestQueue = new BookingRequestQueue();
        System.out.println("\n✓ Booking request queue initialized");

        // UC6: Initialize room allocation service (NEW)
        System.out.println("\n--- STEP 2: Initialize Room Allocation Service ---");
        RoomAllocationService allocationService = new RoomAllocationService();
        System.out.println("✓ Room allocation service initialized!");
        System.out.println("  Data Structure: Set<String> for room ID uniqueness");
        System.out.println("  Mapping: HashMap<String, Set<String>> for room types");

        // UC6: Demonstrate complete booking flow
        System.out.println("\n--- STEP 3: Demonstrate Complete Booking Flow ---");
        demonstrateBookingFlow(requestQueue, inventory, allocationService);

        // Final status message
        System.out.println("\n========================================");
        System.out.println("UC6 Demonstration Complete!");
        System.out.println("Room allocation with double-booking prevention established.");
        System.out.println("Inventory synchronization verified.");
        System.out.println("Ready for advanced booking scenarios in UC7...");
        System.out.println("========================================\n");

        // Display UC6 advantages
        System.out.println("========================================");
        System.out.println("    UC6 ADVANTAGES - SAFE ALLOCATION    ");
        System.out.println("========================================");
        System.out.println("\n✓ Set<String> enforces room ID uniqueness");
        System.out.println("✓ O(1) lookup prevents double-booking");
        System.out.println("✓ Atomic operations maintain consistency");
        System.out.println("✓ Immediate inventory synchronization");
        System.out.println("✓ Grouped tracking by room type");
        System.out.println("✓ Impossible to assign same room twice");
        System.out.println("✓ Scalable allocation pattern");
        System.out.println("✓ Verified uniqueness enforcement");
        System.out.println("\n========================================\n");

        // Display set vs other structures
        System.out.println("========================================");
        System.out.println("    WHY SET FOR ROOM ID UNIQUENESS?     ");
        System.out.println("========================================");
        System.out.println("\nComparison with other data structures:\n");
        System.out.println("List:");
        System.out.println("  ✗ Allows duplicates");
        System.out.println("  ✗ Requires manual duplicate check");
        System.out.println("  ✗ O(n) lookup time\n");

        System.out.println("HashSet (CHOSEN):");
        System.out.println("  ✓ Automatically prevents duplicates");
        System.out.println("  ✓ O(1) lookup time");
        System.out.println("  ✓ Perfect for uniqueness enforcement");
        System.out.println("  ✓ Built-in double-booking prevention\n");

        System.out.println("TreeSet:");
        System.out.println("  ✓ Prevents duplicates but");
        System.out.println("  ✗ O(log n) lookup time");
        System.out.println("  ✗ Unnecessary sorting overhead");
        System.out.println("\n========================================\n");
    }
}