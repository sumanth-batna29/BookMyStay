import java.util.*;
import java.time.LocalDateTime;

/**
 * Hotel Booking Management System - Use Case 9
 *
 * Error Handling & Validation
 *
 * This class demonstrates:
 * - Custom exception classes for domain-specific errors
 * - Input validation before processing bookings
 * - Inventory state guarding
 * - Fail-fast design principle
 * - Graceful error handling and recovery
 * - Early detection of invalid conditions
 * - Clear and informative error messages
 *
 * @author sumanth-batna29
 * @version 9.1
 * @since 2026-03-25
 */
public class BookMyStay {

    // ============================================
    // UC9: CUSTOM EXCEPTION CLASSES (NEW)
    // ============================================

    /**
     * UC9: Base custom exception for booking system
     */
    static class BookingException extends Exception {
        public BookingException(String message) {
            super(message);
        }

        public BookingException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * UC9: Exception for invalid room type
     */
    static class InvalidRoomTypeException extends BookingException {
        public InvalidRoomTypeException(String roomType) {
            super("Invalid room type: '" + roomType + "'. " +
                    "Allowed types: Single Room, Double Room, Suite Room");
        }
    }

    /**
     * UC9: Exception for invalid guest name
     */
    static class InvalidGuestNameException extends BookingException {
        public InvalidGuestNameException(String guestName) {
            super("Invalid guest name: '" + guestName + "'. " +
                    "Guest name must be non-empty and contain only letters and spaces");
        }
    }

    /**
     * UC9: Exception for invalid number of nights
     */
    static class InvalidNumberOfNightsException extends BookingException {
        public InvalidNumberOfNightsException(int nights) {
            super("Invalid number of nights: " + nights + ". " +
                    "Number of nights must be between 1 and 365");
        }
    }

    /**
     * UC9: Exception for insufficient room availability
     */
    static class InsufficientAvailabilityException extends BookingException {
        public InsufficientAvailabilityException(String roomType) {
            super("Insufficient availability for room type: '" + roomType + "'. " +
                    "No rooms of this type are currently available");
        }
    }

    /**
     * UC9: Exception for invalid service selection
     */
    static class InvalidServiceException extends BookingException {
        public InvalidServiceException(String serviceId) {
            super("Invalid service ID: '" + serviceId + "'. " +
                    "The requested service is not available");
        }
    }

    /**
     * UC9: Exception for invalid reservation status
     */
    static class InvalidReservationStatusException extends BookingException {
        public InvalidReservationStatusException(String status) {
            super("Invalid reservation status: '" + status + "'. " +
                    "Allowed statuses: Pending, Confirmed, Rejected");
        }
    }

    /**
     * UC9: Exception for duplicate service selection
     */
    static class DuplicateServiceException extends BookingException {
        public DuplicateServiceException(String serviceId) {
            super("Service already added: '" + serviceId + "'. " +
                    "Cannot add the same service twice to a reservation");
        }
    }

    /**
     * UC9: Exception for booking not found
     */
    static class BookingNotFoundException extends BookingException {
        public BookingNotFoundException(String reservationId) {
            super("Booking not found: '" + reservationId + "'. " +
                    "No booking exists with this reservation ID");
        }
    }

    // ============================================
    // UC9: INPUT VALIDATOR CLASS (NEW)
    // ============================================

    /**
     * UC9: InputValidator class - Validates all booking inputs
     *
     * Implements fail-fast design by validating inputs before processing.
     * Throws custom exceptions with clear error messages.
     */
    static class InputValidator {

        // Valid room types
        private static final Set<String> VALID_ROOM_TYPES = new HashSet<>(
                Arrays.asList("Single Room", "Double Room", "Suite Room"));

        // Valid reservation statuses
        private static final Set<String> VALID_STATUSES = new HashSet<>(
                Arrays.asList("Pending", "Confirmed", "Rejected"));

        // Constraints
        private static final int MIN_NIGHTS = 1;
        private static final int MAX_NIGHTS = 365;

        /**
         * UC9: Validate room type
         * Time Complexity: O(1)
         */
        public static void validateRoomType(String roomType)
                throws InvalidRoomTypeException {
            if (roomType == null || !VALID_ROOM_TYPES.contains(roomType)) {
                throw new InvalidRoomTypeException(roomType);
            }
        }

        /**
         * UC9: Validate guest name
         * Time Complexity: O(n) where n = length of name
         */
        public static void validateGuestName(String guestName)
                throws InvalidGuestNameException {
            if (guestName == null || guestName.trim().isEmpty()) {
                throw new InvalidGuestNameException(guestName);
            }

            // Check if name contains only letters and spaces
            if (!guestName.matches("^[a-zA-Z\\s]+$")) {
                throw new InvalidGuestNameException(guestName);
            }
        }

        /**
         * UC9: Validate number of nights
         * Time Complexity: O(1)
         */
        public static void validateNumberOfNights(int nights)
                throws InvalidNumberOfNightsException {
            if (nights < MIN_NIGHTS || nights > MAX_NIGHTS) {
                throw new InvalidNumberOfNightsException(nights);
            }
        }

        /**
         * UC9: Validate reservation status
         * Time Complexity: O(1)
         */
        public static void validateReservationStatus(String status)
                throws InvalidReservationStatusException {
            if (status == null || !VALID_STATUSES.contains(status)) {
                throw new InvalidReservationStatusException(status);
            }
        }

        /**
         * UC9: Validate service ID exists
         * Time Complexity: O(1)
         */
        public static void validateServiceId(String serviceId,
                                             Map<String, AddOnService> serviceCatalog)
                throws InvalidServiceException {
            if (!serviceCatalog.containsKey(serviceId)) {
                throw new InvalidServiceException(serviceId);
            }
        }

        /**
         * UC9: Display validation rules
         */
        public static void displayValidationRules() {
            System.out.println("\n========================================");
            System.out.println("    VALIDATION RULES                    ");
            System.out.println("========================================");

            System.out.println("\nGuest Name:");
            System.out.println("  - Must not be empty");
            System.out.println("  - Must contain only letters and spaces");
            System.out.println("  - Example: John Smith");

            System.out.println("\nNumber of Nights:");
            System.out.println("  - Minimum: " + MIN_NIGHTS + " night");
            System.out.println("  - Maximum: " + MAX_NIGHTS + " nights");
            System.out.println("  - Example: 3");

            System.out.println("\nRoom Types:");
            for (String roomType : VALID_ROOM_TYPES) {
                System.out.println("  - " + roomType);
            }

            System.out.println("\n========================================");
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

        public int getNumberOfBeds() {
            return numberOfBeds;
        }

        public int getRoomSize() {
            return roomSize;
        }

        public String getAmenities() {
            return amenities;
        }
    }

    // ============================================
    // CONCRETE ROOM CLASSES
    // ============================================

    static class SingleRoom extends Room {
        public SingleRoom() {
            super("Single Room", 1, 2000.0,
                    "WiFi, AC, Bed, Bathroom", 200);
        }

        @Override
        void displayRoomDetails() {
            System.out.println("\n--- SINGLE ROOM DETAILS ---");
            this.displayBasicInfo();
        }
    }

    static class DoubleRoom extends Room {
        public DoubleRoom() {
            super("Double Room", 2, 3500.0,
                    "WiFi, AC, Double Bed, Bathroom, TV", 300);
        }

        @Override
        void displayRoomDetails() {
            System.out.println("\n--- DOUBLE ROOM DETAILS ---");
            this.displayBasicInfo();
        }
    }

    static class SuiteRoom extends Room {
        public SuiteRoom() {
            super("Suite Room", 2, 6000.0,
                    "WiFi, AC, King Bed, Bathroom, TV, Mini Bar, Balcony", 500);
        }

        @Override
        void displayRoomDetails() {
            System.out.println("\n--- SUITE ROOM DETAILS ---");
            this.displayBasicInfo();
        }
    }

    // ============================================
    // ADD-ON SERVICE CLASS
    // ============================================

    static class AddOnService {
        private String serviceId;
        private String serviceName;
        private String serviceDescription;
        private double servicePrice;
        private String serviceCategory;

        public AddOnService(String serviceId, String serviceName,
                            String serviceDescription, double servicePrice,
                            String serviceCategory) {
            this.serviceId = serviceId;
            this.serviceName = serviceName;
            this.serviceDescription = serviceDescription;
            this.servicePrice = servicePrice;
            this.serviceCategory = serviceCategory;
        }

        public String getServiceId() {
            return serviceId;
        }

        public String getServiceName() {
            return serviceName;
        }

        public double getServicePrice() {
            return servicePrice;
        }

        public String getServiceCategory() {
            return serviceCategory;
        }

        @Override
        public String toString() {
            return "[" + serviceId + "] " + serviceName + " - ₹" + servicePrice;
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
        private long requestTimestamp;
        private String status;
        private String assignedRoomId;
        private List<AddOnService> selectedServices;
        private double totalCost;
        private LocalDateTime confirmationTime;

        public Reservation(String reservationId, String guestName,
                           String requestedRoomType, int numberOfNights)
                throws InvalidGuestNameException, InvalidNumberOfNightsException,
                InvalidRoomTypeException {

            // UC9: Validate inputs before creating reservation (FAIL-FAST)
            InputValidator.validateGuestName(guestName);
            InputValidator.validateNumberOfNights(numberOfNights);
            InputValidator.validateRoomType(requestedRoomType);

            this.reservationId = reservationId;
            this.guestName = guestName;
            this.requestedRoomType = requestedRoomType;
            this.numberOfNights = numberOfNights;
            this.requestTimestamp = System.currentTimeMillis();
            this.status = "Pending";
            this.assignedRoomId = null;
            this.selectedServices = new ArrayList<>();
            this.totalCost = 0.0;
            this.confirmationTime = null;
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

        public void setStatus(String status)
                throws InvalidReservationStatusException {
            // UC9: Validate status before setting
            InputValidator.validateReservationStatus(status);
            this.status = status;
        }

        public String getAssignedRoomId() {
            return assignedRoomId;
        }

        public void setAssignedRoomId(String roomId) {
            this.assignedRoomId = roomId;
        }

        public List<AddOnService> getSelectedServices() {
            return selectedServices;
        }

        public void addService(AddOnService service)
                throws DuplicateServiceException {
            // UC9: Check for duplicate services
            for (AddOnService existing : selectedServices) {
                if (existing.getServiceId().equals(service.getServiceId())) {
                    throw new DuplicateServiceException(service.getServiceId());
                }
            }
            selectedServices.add(service);
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

        public void displayDetails() {
            System.out.println("Reservation ID: " + reservationId);
            System.out.println("Guest Name: " + guestName);
            System.out.println("Room Type: " + requestedRoomType);
            System.out.println("Assigned Room: " + assignedRoomId);
            System.out.println("Number of Nights: " + numberOfNights);
            System.out.println("Status: " + status);
            System.out.println("Total Cost: ₹" + totalCost);
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
    // ROOM INVENTORY CLASS (WITH VALIDATION)
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

        /**
         * UC9: Guarded decrement - validates state before update
         */
        public boolean decrementRoomCount(String roomType)
                throws InvalidRoomTypeException, InsufficientAvailabilityException {

            // UC9: Validate room type exists
            if (!roomTypeExists(roomType)) {
                throw new InvalidRoomTypeException(roomType);
            }

            // UC9: Guard: check availability before decrementing
            int available = getAvailableRooms(roomType);
            if (available <= 0) {
                throw new InsufficientAvailabilityException(roomType);
            }

            // UC9: Safe to update
            inventoryMap.put(roomType, available - 1);
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

        /**
         * UC9: Add booking with validation
         */
        public Reservation addBookingRequest(String guestName,
                                             String requestedRoomType,
                                             int numberOfNights)
                throws InvalidGuestNameException, InvalidNumberOfNightsException,
                InvalidRoomTypeException {
            String reservationId = "RES-" + (++reservationCounter);

            // UC9: Validation happens in Reservation constructor (FAIL-FAST)
            Reservation reservation = new Reservation(reservationId, guestName,
                    requestedRoomType, numberOfNights);
            requestQueue.offer(reservation);
            return reservation;
        }

        public Reservation peekNextRequest() {
            return requestQueue.peek();
        }

        public Reservation pollNextRequest() {
            return requestQueue.poll();
        }

        public boolean hasPendingRequests() {
            return !requestQueue.isEmpty();
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

        private boolean isRoomIdAllocated(String roomId) {
            return allocatedRoomIds.contains(roomId);
        }

        /**
         * UC9: Allocate with comprehensive validation
         */
        public boolean allocateRoom(Reservation reservation, RoomInventory inventory)
                throws InvalidRoomTypeException, InsufficientAvailabilityException {

            String roomType = reservation.getRequestedRoomType();

            // UC9: Guard: validate room type exists in inventory
            if (!inventory.roomTypeExists(roomType)) {
                throw new InvalidRoomTypeException(roomType);
            }

            // UC9: Guard: check availability (throws if insufficient)
            if (inventory.getAvailableRooms(roomType) <= 0) {
                throw new InsufficientAvailabilityException(roomType);
            }

            String assignedRoomId = generateUniqueRoomId(roomType);

            // UC9: Guard: prevent double-booking (collision detection)
            if (isRoomIdAllocated(assignedRoomId)) {
                System.out.println("✗ ERROR: Room ID collision detected!");
                return false;
            }

            allocatedRoomIds.add(assignedRoomId);
            Set<String> roomTypeAllocations = roomTypeToAllocatedIds.get(roomType);
            roomTypeAllocations.add(assignedRoomId);

            // UC9: Guard: update inventory (may throw exception)
            inventory.decrementRoomCount(roomType);

            reservation.setAssignedRoomId(assignedRoomId);
            reservation.setStatus("Confirmed");
            reservation.setConfirmationTime(LocalDateTime.now());

            return true;
        }
    }

    // ============================================
    // ADD-ON SERVICE MANAGER CLASS (WITH VALIDATION)
    // ============================================

    static class AddOnServiceManager {
        private Map<String, AddOnService> serviceCatalog;

        public AddOnServiceManager() {
            this.serviceCatalog = new HashMap<>();
            initializeServiceCatalog();
        }

        private void initializeServiceCatalog() {
            System.out.println("Initializing add-on service catalog...");

            serviceCatalog.put("SVC-101", new AddOnService("SVC-101",
                    "Continental Breakfast", "Fresh breakfast for 2 guests", 500.0, "Meals"));
            serviceCatalog.put("SVC-102", new AddOnService("SVC-102",
                    "Evening Dinner", "3-course dinner for 2 guests", 1500.0, "Meals"));
            serviceCatalog.put("SVC-201", new AddOnService("SVC-201",
                    "Airport Transfer", "Pick-up from airport", 400.0, "Transport"));
            serviceCatalog.put("SVC-301", new AddOnService("SVC-301",
                    "Spa Treatment", "Relaxation spa package (1 hour)", 2000.0, "Spa"));

            System.out.println("✓ Service catalog initialized!");
        }

        /**
         * UC9: Get service with validation
         */
        public AddOnService getServiceById(String serviceId)
                throws InvalidServiceException {
            if (!serviceCatalog.containsKey(serviceId)) {
                throw new InvalidServiceException(serviceId);
            }
            return serviceCatalog.get(serviceId);
        }

        public Map<String, AddOnService> getServiceCatalog() {
            return serviceCatalog;
        }
    }

    // ============================================
    // BOOKING HISTORY CLASS
    // ============================================

    static class BookingHistory {
        private List<Reservation> confirmedBookings;

        public BookingHistory() {
            this.confirmedBookings = new ArrayList<>();
        }

        /**
         * UC9: Record with validation
         */
        public void recordConfirmedBooking(Reservation reservation)
                throws InvalidReservationStatusException {
            if (!reservation.getStatus().equals("Confirmed")) {
                throw new InvalidReservationStatusException(reservation.getStatus());
            }
            confirmedBookings.add(reservation);
        }

        public List<Reservation> getAllBookings() {
            return Collections.unmodifiableList(confirmedBookings);
        }

        /**
         * UC9: Get booking with validation
         */
        public Reservation getBookingById(String reservationId)
                throws BookingNotFoundException {
            for (Reservation booking : confirmedBookings) {
                if (booking.getReservationId().equals(reservationId)) {
                    return booking;
                }
            }
            throw new BookingNotFoundException(reservationId);
        }

        public int getTotalBookingCount() {
            return confirmedBookings.size();
        }
    }

    // ============================================
    // DISPLAY METHODS
    // ============================================

    public static void displayWelcomeMessage() {
        System.out.println("\n========================================");
        System.out.println("    BOOK MY STAY - HOTEL BOOKING APP    ");
        System.out.println("========================================");
        System.out.println("Version: 9.1");
        System.out.println("Use Case 9: Error Handling & Validation");
        System.out.println("========================================\n");
    }

    /**
     * UC9: Demonstrate error handling and validation
     */
    public static void demonstrateErrorHandling(
            BookingRequestQueue requestQueue,
            RoomInventory inventory,
            RoomAllocationService allocationService,
            AddOnServiceManager serviceManager) {

        System.out.println("\n========================================");
        System.out.println("    ERROR HANDLING DEMONSTRATION        ");
        System.out.println("========================================");

        // Scenario 1: Valid booking
        System.out.println("\n--- SCENARIO 1: Valid booking (SUCCESS) ---\n");
        try {
            Reservation res1 = requestQueue.addBookingRequest("John Smith",
                    "Single Room", 3);
            System.out.println("✓ Booking created successfully");
            System.out.println("  Reservation: " + res1.getReservationId());

            allocationService.allocateRoom(res1, inventory);
            System.out.println("✓ Room allocated: " + res1.getAssignedRoomId());
        } catch (BookingException e) {
            System.out.println("✗ Error: " + e.getMessage());
        }

        // Scenario 2: Invalid guest name
        System.out.println("\n--- SCENARIO 2: Invalid guest name (FAIL-FAST) ---\n");
        try {
            Reservation res2 = requestQueue.addBookingRequest("John123",
                    "Double Room", 2);
            System.out.println("✓ Booking created");
        } catch (InvalidGuestNameException e) {
            System.out.println("✗ Validation Error: " + e.getMessage());
        } catch (BookingException e) {
            System.out.println("✗ Error: " + e.getMessage());
        }

        // Scenario 3: Invalid number of nights
        System.out.println("\n--- SCENARIO 3: Invalid number of nights (FAIL-FAST) ---\n");
        try {
            Reservation res3 = requestQueue.addBookingRequest("Jane Doe",
                    "Double Room", 400);
            System.out.println("✓ Booking created");
        } catch (InvalidNumberOfNightsException e) {
            System.out.println("✗ Validation Error: " + e.getMessage());
        } catch (BookingException e) {
            System.out.println("✗ Error: " + e.getMessage());
        }

        // Scenario 4: Invalid room type
        System.out.println("\n--- SCENARIO 4: Invalid room type (FAIL-FAST) ---\n");
        try {
            Reservation res4 = requestQueue.addBookingRequest("Bob Johnson",
                    "Luxury Suite", 2);
            System.out.println("✓ Booking created");
        } catch (InvalidRoomTypeException e) {
            System.out.println("✗ Validation Error: " + e.getMessage());
        } catch (BookingException e) {
            System.out.println("✗ Error: " + e.getMessage());
        }

        // Scenario 5: Insufficient availability
        System.out.println("\n--- SCENARIO 5: Insufficient availability (GUARDED) ---\n");
        try {
            // Try to allocate more rooms than available
            for (int i = 0; i < 10; i++) {
                Reservation res = requestQueue.addBookingRequest("Guest " + i,
                        "Suite Room", 1);
                allocationService.allocateRoom(res, inventory);
                System.out.println("✓ Allocated: " + res.getAssignedRoomId());
            }
        } catch (InsufficientAvailabilityException e) {
            System.out.println("✗ Allocation Error: " + e.getMessage());
        } catch (BookingException e) {
            System.out.println("✗ Error: " + e.getMessage());
        }

        // Scenario 6: Invalid service
        System.out.println("\n--- SCENARIO 6: Invalid service selection (VALIDATION) ---\n");
        try {
            AddOnService invalidService = serviceManager.getServiceById("SVC-999");
        } catch (InvalidServiceException e) {
            System.out.println("✗ Service Error: " + e.getMessage());
        } catch (BookingException e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    // ============================================
    // MAIN METHOD
    // ============================================

    public static void main(String[] args) {
        displayWelcomeMessage();

        // Display validation rules
        InputValidator.displayValidationRules();

        // Initialize system components
        System.out.println("\n--- STEP 1: Initialize System Components ---");
        RoomInventory inventory = new RoomInventory();
        inventory.displayInventory();

        BookingRequestQueue requestQueue = new BookingRequestQueue();
        System.out.println("\n✓ Booking request queue initialized");

        RoomAllocationService allocationService = new RoomAllocationService();

        AddOnServiceManager serviceManager = new AddOnServiceManager();

        BookingHistory bookingHistory = new BookingHistory();
        System.out.println("✓ Booking history initialized");

        // UC9: Demonstrate error handling
        System.out.println("\n--- STEP 2: Initialize Error Handling & Validation ---");
        System.out.println("✓ Custom exceptions configured");
        System.out.println("✓ Input validator enabled");
        System.out.println("✓ State guards activated");

        System.out.println("\n--- STEP 3: Demonstrate Error Handling ---");
        demonstrateErrorHandling(requestQueue, inventory, allocationService,
                serviceManager);

        // Final message
        System.out.println("\n========================================");
        System.out.println("UC9 Demonstration Complete!");
        System.out.println("Error handling and validation established.");
        System.out.println("Fail-fast design principle implemented.");
        System.out.println("System remains stable after errors.");
        System.out.println("========================================\n");

        // Display UC9 advantages
        System.out.println("========================================");
        System.out.println("    UC9 ADVANTAGES - ERROR SAFETY      ");
        System.out.println("========================================");
        System.out.println("\n✓ Custom exceptions for clear error causes");
        System.out.println("✓ Fail-fast design prevents cascading failures");
        System.out.println("✓ Guarded state transitions prevent corruption");
        System.out.println("✓ Early validation catches errors immediately");
        System.out.println("✓ Graceful error handling maintains stability");
        System.out.println("✓ Meaningful error messages aid debugging");
        System.out.println("✓ Input validation before processing");
        System.out.println("✓ Inventory state always remains consistent");
        System.out.println("\n========================================\n");

        // Display error handling benefits
        System.out.println("========================================");
        System.out.println("    ERROR HANDLING BEST PRACTICES      ");
        System.out.println("========================================");
        System.out.println("\n1. Custom Exceptions:");
        System.out.println("   - Domain-specific error types");
        System.out.println("   - Clear error messages");
        System.out.println("   - Exception hierarchy");

        System.out.println("\n2. Fail-Fast Design:");
        System.out.println("   - Detect errors early");
        System.out.println("   - Stop processing immediately");
        System.out.println("   - Prevent partial state changes");

        System.out.println("\n3. Guard Clauses:");
        System.out.println("   - Validate before mutation");
        System.out.println("   - Check preconditions");
        System.out.println("   - Ensure state consistency");

        System.out.println("\n4. Graceful Degradation:");
        System.out.println("   - Handle errors without crashing");
        System.out.println("   - Provide clear feedback");
        System.out.println("   - Allow system recovery");

        System.out.println("\n========================================\n");
    }
}