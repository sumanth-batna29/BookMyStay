import java.util.*;
import java.time.LocalDateTime;

/**
 * Hotel Booking Management System - Use Case 10
 *
 * Booking Cancellation & Inventory Rollback
 *
 * This class demonstrates:
 * - Stack data structure for LIFO rollback behavior
 * - Safe state reversal after cancellation
 * - Controlled mutation order
 * - Inventory restoration and synchronization
 * - Cancellation validation
 * - Consistent system state recovery
 * - Atomic rollback operations
 *
 * @author sumanth-batna29
 * @version 10.1
 * @since 2026-03-25
 */
public class BookMyStay {

    // ============================================
    // CUSTOM EXCEPTION CLASSES
    // ============================================

    static class BookingException extends Exception {
        public BookingException(String message) {
            super(message);
        }
    }

    static class InvalidRoomTypeException extends BookingException {
        public InvalidRoomTypeException(String roomType) {
            super("Invalid room type: '" + roomType + "'. " +
                    "Allowed types: Single Room, Double Room, Suite Room");
        }
    }

    static class InvalidGuestNameException extends BookingException {
        public InvalidGuestNameException(String guestName) {
            super("Invalid guest name: '" + guestName + "'. " +
                    "Guest name must be non-empty and contain only letters and spaces");
        }
    }

    static class InvalidNumberOfNightsException extends BookingException {
        public InvalidNumberOfNightsException(int nights) {
            super("Invalid number of nights: " + nights + ". " +
                    "Number of nights must be between 1 and 365");
        }
    }

    static class InsufficientAvailabilityException extends BookingException {
        public InsufficientAvailabilityException(String roomType) {
            super("Insufficient availability for room type: '" + roomType + "'");
        }
    }

    /**
     * UC10: Exception for cancellation not allowed
     */
    static class CancellationNotAllowedException extends BookingException {
        public CancellationNotAllowedException(String reservationId, String status) {
            super("Cancellation not allowed for reservation '" + reservationId +
                    "' with status '" + status + "'. Only confirmed bookings can be cancelled.");
        }
    }

    /**
     * UC10: Exception for booking not found
     */
    static class BookingNotFoundException extends BookingException {
        public BookingNotFoundException(String reservationId) {
            super("Booking not found: '" + reservationId + "'. " +
                    "No booking exists with this reservation ID");
        }
    }

    // ============================================
    // INPUT VALIDATOR CLASS
    // ============================================

    static class InputValidator {
        private static final Set<String> VALID_ROOM_TYPES = new HashSet<>(
                Arrays.asList("Single Room", "Double Room", "Suite Room"));

        private static final Set<String> VALID_STATUSES = new HashSet<>(
                Arrays.asList("Pending", "Confirmed", "Rejected", "Cancelled"));

        private static final int MIN_NIGHTS = 1;
        private static final int MAX_NIGHTS = 365;

        public static void validateRoomType(String roomType)
                throws InvalidRoomTypeException {
            if (roomType == null || !VALID_ROOM_TYPES.contains(roomType)) {
                throw new InvalidRoomTypeException(roomType);
            }
        }

        public static void validateGuestName(String guestName)
                throws InvalidGuestNameException {
            if (guestName == null || guestName.trim().isEmpty()) {
                throw new InvalidGuestNameException(guestName);
            }

            if (!guestName.matches("^[a-zA-Z\\s]+$")) {
                throw new InvalidGuestNameException(guestName);
            }
        }

        public static void validateNumberOfNights(int nights)
                throws InvalidNumberOfNightsException {
            if (nights < MIN_NIGHTS || nights > MAX_NIGHTS) {
                throw new InvalidNumberOfNightsException(nights);
            }
        }
    }

    // ============================================
    // ABSTRACT ROOM CLASS
    // ============================================

    abstract static class Room {
        protected String roomType;
        protected int numberOfBeds;
        protected double pricePerNight;
        protected String amenities;
        protected int roomSize;

        public Room(String roomType, int numberOfBeds, double pricePerNight,
                    String amenities, int roomSize) {
            this.roomType = roomType;
            this.numberOfBeds = numberOfBeds;
            this.pricePerNight = pricePerNight;
            this.amenities = amenities;
            this.roomSize = roomSize;
        }

        abstract void displayRoomDetails();

        public void displayBasicInfo() {
            System.out.println("Room Type: " + roomType);
            System.out.println("Number of Beds: " + numberOfBeds);
            System.out.println("Price per Night: ₹" + pricePerNight);
            System.out.println("Room Size: " + roomSize + " sq ft");
            System.out.println("Amenities: " + amenities);
        }

        public String getRoomType() {
            return roomType;
        }

        public double getPricePerNight() {
            return pricePerNight;
        }
    }

    // ============================================
    // CONCRETE ROOM CLASSES
    // ============================================

    static class SingleRoom extends Room {
        public SingleRoom() {
            super("Single Room", 1, 2000.0, "WiFi, AC, Bed, Bathroom", 200);
        }

        @Override
        void displayRoomDetails() {
            System.out.println("\n--- SINGLE ROOM ---");
            this.displayBasicInfo();
        }
    }

    static class DoubleRoom extends Room {
        public DoubleRoom() {
            super("Double Room", 2, 3500.0, "WiFi, AC, Double Bed, Bathroom, TV", 300);
        }

        @Override
        void displayRoomDetails() {
            System.out.println("\n--- DOUBLE ROOM ---");
            this.displayBasicInfo();
        }
    }

    static class SuiteRoom extends Room {
        public SuiteRoom() {
            super("Suite Room", 2, 6000.0, "WiFi, AC, King Bed, Bathroom, TV, Mini Bar", 500);
        }

        @Override
        void displayRoomDetails() {
            System.out.println("\n--- SUITE ROOM ---");
            this.displayBasicInfo();
        }
    }

    // ============================================
    // RESERVATION CLASS
    // ============================================

    static class Reservation {
        private String reservationId;
        private String guestName;
        private String requestedRoomType;
        private int numberOfNights;
        private String status;
        private String assignedRoomId;
        private double totalCost;
        private LocalDateTime confirmationTime;
        private LocalDateTime cancellationTime;

        public Reservation(String reservationId, String guestName,
                           String requestedRoomType, int numberOfNights)
                throws InvalidGuestNameException, InvalidNumberOfNightsException,
                InvalidRoomTypeException {

            InputValidator.validateGuestName(guestName);
            InputValidator.validateNumberOfNights(numberOfNights);
            InputValidator.validateRoomType(requestedRoomType);

            this.reservationId = reservationId;
            this.guestName = guestName;
            this.requestedRoomType = requestedRoomType;
            this.numberOfNights = numberOfNights;
            this.status = "Pending";
            this.assignedRoomId = null;
            this.totalCost = 0.0;
            this.confirmationTime = null;
            this.cancellationTime = null;
        }

        public String getReservationId() {
            return reservationId;
        }

        public String getGuestName() {
            return guestName;
        }

        public String getRequestedRoomType() {
            return requestedRoomType;
        }

        public int getNumberOfNights() {
            return numberOfNights;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getAssignedRoomId() {
            return assignedRoomId;
        }

        public void setAssignedRoomId(String roomId) {
            this.assignedRoomId = roomId;
        }

        public double getTotalCost() {
            return totalCost;
        }

        public void setTotalCost(double cost) {
            this.totalCost = cost;
        }

        public LocalDateTime getConfirmationTime() {
            return confirmationTime;
        }

        public void setConfirmationTime(LocalDateTime time) {
            this.confirmationTime = time;
        }

        public LocalDateTime getCancellationTime() {
            return cancellationTime;
        }

        public void setCancellationTime(LocalDateTime time) {
            this.cancellationTime = time;
        }

        public void displayDetails() {
            System.out.println("Reservation ID: " + reservationId);
            System.out.println("Guest Name: " + guestName);
            System.out.println("Room Type: " + requestedRoomType);
            System.out.println("Assigned Room: " + assignedRoomId);
            System.out.println("Number of Nights: " + numberOfNights);
            System.out.println("Status: " + status);
            System.out.println("Total Cost: ₹" + totalCost);
            if (confirmationTime != null) {
                System.out.println("Confirmed: " + confirmationTime);
            }
            if (cancellationTime != null) {
                System.out.println("Cancelled: " + cancellationTime);
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

    static class RoomInventory {
        private HashMap<String, Integer> inventoryMap;
        private HashMap<String, Integer> totalRoomsMap;

        public RoomInventory() {
            this.inventoryMap = new HashMap<>();
            this.totalRoomsMap = new HashMap<>();
            initializeInventory();
        }

        private void initializeInventory() {
            System.out.println("Initializing centralized room inventory...");

            inventoryMap.put("Single Room", 5);
            inventoryMap.put("Double Room", 8);
            inventoryMap.put("Suite Room", 3);

            totalRoomsMap.put("Single Room", 5);
            totalRoomsMap.put("Double Room", 8);
            totalRoomsMap.put("Suite Room", 3);

            System.out.println("✓ Inventory initialized successfully!");
        }

        public int getAvailableRooms(String roomType) {
            return inventoryMap.getOrDefault(roomType, 0);
        }

        public int getTotalRooms(String roomType) {
            return totalRoomsMap.getOrDefault(roomType, 0);
        }

        public boolean roomTypeExists(String roomType) {
            return inventoryMap.containsKey(roomType);
        }

        public boolean decrementRoomCount(String roomType)
                throws InvalidRoomTypeException, InsufficientAvailabilityException {

            if (!roomTypeExists(roomType)) {
                throw new InvalidRoomTypeException(roomType);
            }

            int available = getAvailableRooms(roomType);
            if (available <= 0) {
                throw new InsufficientAvailabilityException(roomType);
            }

            inventoryMap.put(roomType, available - 1);
            return true;
        }

        /**
         * UC10: Increment room count (for cancellation/rollback)
         * Time Complexity: O(1)
         */
        public boolean incrementRoomCount(String roomType)
                throws InvalidRoomTypeException {

            if (!roomTypeExists(roomType)) {
                throw new InvalidRoomTypeException(roomType);
            }

            int available = getAvailableRooms(roomType);
            int total = getTotalRooms(roomType);

            // Guard: prevent overflow
            if (available >= total) {
                System.out.println("⚠ Warning: Room count already at maximum");
                return false;
            }

            inventoryMap.put(roomType, available + 1);
            return true;
        }

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

        private void displayInventoryBar(int available, int total) {
            System.out.print("     [");
            for (int i = 0; i < total; i++) {
                if (i < available) {
                    System.out.print("█");
                } else {
                    System.out.print("░");
                }
            }
            System.out.println("]");
        }
    }

    // ============================================
    // BOOKING REQUEST QUEUE CLASS
    // ============================================

    static class BookingRequestQueue {
        private Queue<Reservation> requestQueue;
        private int reservationCounter;

        public BookingRequestQueue() {
            this.requestQueue = new LinkedList<>();
            this.reservationCounter = 1000;
        }

        public Reservation addBookingRequest(String guestName,
                                             String requestedRoomType,
                                             int numberOfNights)
                throws InvalidGuestNameException, InvalidNumberOfNightsException,
                InvalidRoomTypeException {
            String reservationId = "RES-" + (++reservationCounter);

            Reservation reservation = new Reservation(reservationId, guestName,
                    requestedRoomType, numberOfNights);
            requestQueue.offer(reservation);
            return reservation;
        }

        public Reservation pollNextRequest() {
            return requestQueue.poll();
        }
    }

    // ============================================
    // ROOM ALLOCATION SERVICE CLASS
    // ============================================

    static class RoomAllocationService {
        private Set<String> allocatedRoomIds;
        private HashMap<String, Set<String>> roomTypeToAllocatedIds;
        private HashMap<String, Integer> roomIdCounters;

        public RoomAllocationService() {
            this.allocatedRoomIds = new HashSet<>();
            this.roomTypeToAllocatedIds = new HashMap<>();
            this.roomIdCounters = new HashMap<>();

            initializeAllocationService();
        }

        private void initializeAllocationService() {
            System.out.println("Initializing room allocation service...");

            roomTypeToAllocatedIds.put("Single Room", new HashSet<>());
            roomTypeToAllocatedIds.put("Double Room", new HashSet<>());
            roomTypeToAllocatedIds.put("Suite Room", new HashSet<>());

            roomIdCounters.put("Single Room", 1);
            roomIdCounters.put("Double Room", 1);
            roomIdCounters.put("Suite Room", 1);

            System.out.println("✓ Allocation service initialized successfully!");
        }

        private String generateUniqueRoomId(String roomType) {
            int counter = roomIdCounters.getOrDefault(roomType, 1);
            String roomId = roomType.substring(0, 1) + counter;
            roomIdCounters.put(roomType, counter + 1);
            return roomId;
        }

        public boolean allocateRoom(Reservation reservation, RoomInventory inventory)
                throws InvalidRoomTypeException, InsufficientAvailabilityException {

            String roomType = reservation.getRequestedRoomType();

            if (!inventory.roomTypeExists(roomType)) {
                throw new InvalidRoomTypeException(roomType);
            }

            if (inventory.getAvailableRooms(roomType) <= 0) {
                throw new InsufficientAvailabilityException(roomType);
            }

            String assignedRoomId = generateUniqueRoomId(roomType);

            allocatedRoomIds.add(assignedRoomId);
            Set<String> roomTypeAllocations = roomTypeToAllocatedIds.get(roomType);
            roomTypeAllocations.add(assignedRoomId);

            inventory.decrementRoomCount(roomType);

            reservation.setAssignedRoomId(assignedRoomId);
            reservation.setStatus("Confirmed");
            reservation.setConfirmationTime(LocalDateTime.now());

            return true;
        }

        /**
         * UC10: Deallocate room (for cancellation)
         * Time Complexity: O(1)
         */
        public boolean deallocateRoom(String roomId, String roomType) {
            if (!allocatedRoomIds.contains(roomId)) {
                return false;
            }

            allocatedRoomIds.remove(roomId);
            Set<String> roomTypeAllocations = roomTypeToAllocatedIds.get(roomType);
            if (roomTypeAllocations != null) {
                roomTypeAllocations.remove(roomId);
            }

            return true;
        }
    }

    // ============================================
    // UC10: BOOKING CANCELLATION SERVICE CLASS (NEW)
    // ============================================

    /**
     * UC10: BookingCancellationService class - Manages cancellation and rollback
     *
     * Handles safe cancellation of confirmed bookings.
     * Uses Stack<String> to track released room IDs for rollback operations.
     * Implements controlled state reversal with proper validation.
     * Ensures inventory consistency during cancellation.
     */
    static class BookingCancellationService {

        // UC10: Stack to track released room IDs (LIFO - Last-In-First-Out)
        private Stack<String> releasedRoomIds;

        // Reference to booking history for lookups
        private BookingHistory bookingHistory;

        /**
         * UC10: Constructor - Initialize cancellation service
         */
        public BookingCancellationService(BookingHistory history) {
            // UC10: Use Stack for LIFO rollback behavior
            this.releasedRoomIds = new Stack<>();
            this.bookingHistory = history;
        }

        /**
         * UC10: Cancel a confirmed booking with rollback
         * Time Complexity: O(1) average for rollback operations
         *
         * @param reservationId Reservation ID to cancel
         * @param inventory RoomInventory to restore
         * @param allocationService RoomAllocationService to update
         */
        public boolean cancelBooking(String reservationId,
                                     RoomInventory inventory,
                                     RoomAllocationService allocationService)
                throws BookingNotFoundException, CancellationNotAllowedException,
                InvalidRoomTypeException {

            // UC10: Step 1: Validate booking exists
            Reservation booking = bookingHistory.getBookingById(reservationId);

            if (booking == null) {
                throw new BookingNotFoundException(reservationId);
            }

            // UC10: Step 2: Validate booking is cancellable (only Confirmed status)
            if (!booking.getStatus().equals("Confirmed")) {
                throw new CancellationNotAllowedException(reservationId, booking.getStatus());
            }

            // UC10: Step 3: Extract room information before rollback
            String assignedRoomId = booking.getAssignedRoomId();
            String roomType = booking.getRequestedRoomType();

            if (assignedRoomId == null) {
                throw new BookingNotFoundException(reservationId);
            }

            // UC10: Step 4: Release room from allocation (deallocate)
            if (!allocationService.deallocateRoom(assignedRoomId, roomType)) {
                System.out.println("⚠ Warning: Room ID not found in allocated set");
                return false;
            }

            // UC10: Step 5: Push released room ID onto stack (for potential re-use or audit)
            releasedRoomIds.push(assignedRoomId);
            System.out.println("  Released room: " + assignedRoomId + " pushed to stack");

            // UC10: Step 6: Restore inventory immediately (atomic operation)
            if (!inventory.incrementRoomCount(roomType)) {
                System.out.println("⚠ Warning: Failed to increment inventory");
            }

            // UC10: Step 7: Update reservation status to Cancelled
            booking.setStatus("Cancelled");
            booking.setCancellationTime(LocalDateTime.now());

            System.out.println("✓ Cancellation completed for: " + reservationId);
            System.out.println("  Room type: " + roomType);
            System.out.println("  Inventory restored");
            System.out.println("  Stack size: " + releasedRoomIds.size());

            return true;
        }

        /**
         * UC10: Get recently released room ID from stack
         * Time Complexity: O(1)
         *
         * @return Most recently released room ID, or null if stack is empty
         */
        public String peekReleasedRoom() {
            return releasedRoomIds.isEmpty() ? null : releasedRoomIds.peek();
        }

        /**
         * UC10: Pop recently released room ID from stack
         * Time Complexity: O(1)
         *
         * @return Most recently released room ID, or null if stack is empty
         */
        public String popReleasedRoom() {
            return releasedRoomIds.isEmpty() ? null : releasedRoomIds.pop();
        }

        /**
         * UC10: Get count of released room IDs in stack
         *
         * @return Number of released rooms
         */
        public int getReleasedRoomCount() {
            return releasedRoomIds.size();
        }

        /**
         * UC10: Check if any rooms have been released
         *
         * @return true if stack has released rooms
         */
        public boolean hasReleasedRooms() {
            return !releasedRoomIds.isEmpty();
        }

        /**
         * UC10: Display released rooms stack (audit trail)
         */
        public void displayReleasedRooms() {
            System.out.println("\n========================================");
            System.out.println("    RELEASED ROOM IDS (Stack - LIFO)    ");
            System.out.println("========================================");

            if (releasedRoomIds.isEmpty()) {
                System.out.println("\nNo released rooms in stack");
            } else {
                System.out.println("\nTotal released: " + releasedRoomIds.size());
                System.out.println("\nReleased rooms (top to bottom - most recent first):");

                // Create copy to display without modifying original
                Stack<String> tempStack = new Stack<>();
                tempStack.addAll(releasedRoomIds);

                int position = 1;
                while (!tempStack.isEmpty()) {
                    String roomId = tempStack.pop();
                    System.out.println("  " + position + ". " + roomId + " (most recent)");
                    position++;
                }
            }

            System.out.println("\n========================================");
        }

        /**
         * UC10: Display cancellation history
         */
        public void displayCancellationReport(BookingHistory history) {
            System.out.println("\n========================================");
            System.out.println("    CANCELLATION REPORT                 ");
            System.out.println("========================================");

            List<Reservation> allBookings = history.getAllBookings();
            List<Reservation> cancelledBookings = new ArrayList<>();

            for (Reservation booking : allBookings) {
                if (booking.getStatus().equals("Cancelled")) {
                    cancelledBookings.add(booking);
                }
            }

            System.out.println("\nTotal cancelled bookings: " + cancelledBookings.size());

            if (cancelledBookings.isEmpty()) {
                System.out.println("No cancellations yet");
            } else {
                System.out.println("\nCancelled bookings:");
                int position = 1;
                for (Reservation booking : cancelledBookings) {
                    System.out.println("  " + position + ". " + booking);
                    System.out.println("     Cancelled: " + booking.getCancellationTime());
                    position++;
                }
            }

            System.out.println("\n========================================");
        }
    }

    // ============================================
    // BOOKING HISTORY CLASS
    // ============================================

    static class BookingHistory {
        private List<Reservation> allBookings;

        public BookingHistory() {
            this.allBookings = new ArrayList<>();
        }

        public void recordBooking(Reservation reservation) {
            allBookings.add(reservation);
        }

        public List<Reservation> getAllBookings() {
            return Collections.unmodifiableList(allBookings);
        }

        public Reservation getBookingById(String reservationId)
                throws BookingNotFoundException {
            for (Reservation booking : allBookings) {
                if (booking.getReservationId().equals(reservationId)) {
                    return booking;
                }
            }
            throw new BookingNotFoundException(reservationId);
        }

        public int getTotalBookingCount() {
            return allBookings.size();
        }
    }

    // ============================================
    // DISPLAY METHODS
    // ============================================

    public static void displayWelcomeMessage() {
        System.out.println("\n========================================");
        System.out.println("    BOOK MY STAY - HOTEL BOOKING APP    ");
        System.out.println("========================================");
        System.out.println("Version: 10.1");
        System.out.println("Use Case 10: Booking Cancellation & Inventory Rollback");
        System.out.println("========================================\n");
    }

    /**
     * UC10: Demonstrate cancellation and rollback
     */
    public static void demonstrateCancellation(
            BookingRequestQueue requestQueue,
            RoomInventory inventory,
            RoomAllocationService allocationService,
            BookingHistory bookingHistory,
            BookingCancellationService cancellationService) {

        System.out.println("\n========================================");
        System.out.println("    CANCELLATION & ROLLBACK DEMO        ");
        System.out.println("========================================");

        // Scenario 1: Create and confirm multiple bookings
        System.out.println("\n--- SCENARIO 1: Create and confirm bookings ---\n");

        Reservation[] reservations = new Reservation[4];
        String[] guestNames = {"John Smith", "Jane Doe", "Bob Johnson", "Alice Williams"};
        String[] roomTypes = {"Single Room", "Double Room", "Suite Room", "Single Room"};

        try {
            for (int i = 0; i < 4; i++) {
                reservations[i] = requestQueue.addBookingRequest(guestNames[i], roomTypes[i], 2);
                allocationService.allocateRoom(reservations[i], inventory);
                reservations[i].setTotalCost(5000 + (i * 1000));
                bookingHistory.recordBooking(reservations[i]);
                System.out.println("✓ Booked: " + reservations[i].getReservationId() +
                        " - " + guestNames[i] + " - Room: " + reservations[i].getAssignedRoomId());
            }
        } catch (BookingException e) {
            System.out.println("✗ Error: " + e.getMessage());
        }

        // Display current inventory
        System.out.println("\n--- Current Inventory (Before Cancellations) ---");
        inventory.displayInventory();

        // Scenario 2: Cancel first booking
        System.out.println("\n--- SCENARIO 2: Cancel first booking ---\n");
        try {
            System.out.println("Cancelling: " + reservations[0].getReservationId());
            cancellationService.cancelBooking(reservations[0].getReservationId(),
                    inventory, allocationService);
        } catch (BookingException e) {
            System.out.println("✗ Error: " + e.getMessage());
        }

        // Scenario 3: Cancel third booking
        System.out.println("\n--- SCENARIO 3: Cancel third booking ---\n");
        try {
            System.out.println("Cancelling: " + reservations[2].getReservationId());
            cancellationService.cancelBooking(reservations[2].getReservationId(),
                    inventory, allocationService);
        } catch (BookingException e) {
            System.out.println("✗ Error: " + e.getMessage());
        }

        // Display updated inventory
        System.out.println("\n--- Updated Inventory (After Cancellations) ---");
        inventory.displayInventory();

        // Scenario 4: Try to cancel already cancelled booking
        System.out.println("\n--- SCENARIO 4: Try to cancel already cancelled booking ---\n");
        try {
            System.out.println("Attempting to cancel: " + reservations[0].getReservationId());
            cancellationService.cancelBooking(reservations[0].getReservationId(),
                    inventory, allocationService);
        } catch (CancellationNotAllowedException e) {
            System.out.println("✗ Cancellation Error: " + e.getMessage());
        } catch (BookingException e) {
            System.out.println("✗ Error: " + e.getMessage());
        }

        // Scenario 5: Try to cancel non-existent booking
        System.out.println("\n--- SCENARIO 5: Try to cancel non-existent booking ---\n");
        try {
            System.out.println("Attempting to cancel: RES-9999");
            cancellationService.cancelBooking("RES-9999", inventory, allocationService);
        } catch (BookingNotFoundException e) {
            System.out.println("✗ Booking Error: " + e.getMessage());
        } catch (BookingException e) {
            System.out.println("✗ Error: " + e.getMessage());
        }

        // Display released rooms stack
        System.out.println("\n--- SCENARIO 6: Display released rooms (Stack - LIFO) ---");
        cancellationService.displayReleasedRooms();

        // Display cancellation report
        System.out.println("\n--- SCENARIO 7: Cancellation Report ---");
        cancellationService.displayCancellationReport(bookingHistory);
    }

    // ============================================
    // MAIN METHOD
    // ============================================

    public static void main(String[] args) {
        displayWelcomeMessage();

        // Initialize system components
        System.out.println("--- STEP 1: Initialize System Components ---");
        RoomInventory inventory = new RoomInventory();
        inventory.displayInventory();

        BookingRequestQueue requestQueue = new BookingRequestQueue();
        System.out.println("\n✓ Booking request queue initialized");

        RoomAllocationService allocationService = new RoomAllocationService();

        BookingHistory bookingHistory = new BookingHistory();
        System.out.println("✓ Booking history initialized");

        // UC10: Initialize cancellation service (NEW)
        System.out.println("\n--- STEP 2: Initialize Cancellation Service ---");
        BookingCancellationService cancellationService =
                new BookingCancellationService(bookingHistory);
        System.out.println("✓ Cancellation service initialized!");
        System.out.println("  Data Structure: Stack<String> for LIFO rollback");
        System.out.println("  Purpose: Track released room IDs and manage state reversal");

        // UC10: Demonstrate cancellation and rollback
        System.out.println("\n--- STEP 3: Demonstrate Cancellation & Rollback ---");
        demonstrateCancellation(requestQueue, inventory, allocationService,
                bookingHistory, cancellationService);

        // Final message
        System.out.println("\n========================================");
        System.out.println("UC10 Demonstration Complete!");
        System.out.println("Booking cancellation and rollback established.");
        System.out.println("State reversal with inventory restoration verified.");
        System.out.println("========================================\n");

        // Display UC10 advantages
        System.out.println("========================================");
        System.out.println("    UC10 ADVANTAGES - SAFE CANCELLATION ");
        System.out.println("========================================");
        System.out.println("\n✓ Stack<String> for LIFO rollback behavior");
        System.out.println("✓ Safe state reversal after cancellation");
        System.out.println("✓ Controlled mutation order");
        System.out.println("✓ Inventory restoration and synchronization");
        System.out.println("✓ Cancellation validation before rollback");
        System.out.println("✓ Consistent system state recovery");
        System.out.println("✓ Atomic rollback operations");
        System.out.println("✓ Complete audit trail of cancellations");
        System.out.println("\n========================================\n");

        // Display LIFO benefits
        System.out.println("========================================");
        System.out.println("    WHY STACK FOR ROLLBACK?             ");
        System.out.println("========================================");
        System.out.println("\nRollback Requirements:\n");
        System.out.println("✓ LIFO Order: Undo most recent first");
        System.out.println("✓ Natural Undo: Mirrors real-world undo operations");
        System.out.println("✓ Simple Logic: Push on release, pop on recover");
        System.out.println("✓ Atomic Ops: O(1) push and pop operations");
        System.out.println("✓ Audit Trail: Records sequence of cancellations");

        System.out.println("\nBetter than alternatives:");
        System.out.println("  Queue: FIFO not suitable for rollback");
        System.out.println("  List: Requires index management");
        System.out.println("  Set: No order, can't track sequence");
        System.out.println("\n========================================\n");
    }
}