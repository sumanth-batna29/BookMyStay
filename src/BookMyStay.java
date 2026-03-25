import java.util.*;

/**
 * Hotel Booking Management System - Use Case 7
 *
 * Add-On Service Selection
 *
 * This class demonstrates:
 * - Map and List combination for one-to-many relationships
 * - Add-on service attachment to reservations
 * - Composition over inheritance pattern
 * - Cost aggregation and calculation
 * - Separation of core and optional features
 * - Business extensibility without modifying core logic
 *
 * @author sumanth-batna29
 * @version 7.1
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
        protected int roomSize;

        /**
         * Constructor for Room class
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
         */
        public String getRoomType() {
            return roomType;
        }

        /**
         * Get price per night
         */
        public double getPricePerNight() {
            return pricePerNight;
        }

        /**
         * Get number of beds
         */
        public int getNumberOfBeds() {
            return numberOfBeds;
        }

        /**
         * Get room size
         */
        public int getRoomSize() {
            return roomSize;
        }

        /**
         * Get amenities
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
     */
    static class SingleRoom extends Room {
        public SingleRoom() {
            super("Single Room", 1, 2000.0,
                    "WiFi, AC, Bed, Bathroom", 200);
        }

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
     */
    static class DoubleRoom extends Room {
        public DoubleRoom() {
            super("Double Room", 2, 3500.0,
                    "WiFi, AC, Double Bed, Bathroom, TV", 300);
        }

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
     */
    static class SuiteRoom extends Room {
        public SuiteRoom() {
            super("Suite Room", 2, 6000.0,
                    "WiFi, AC, King Bed, Bathroom, TV, Mini Bar, Balcony", 500);
        }

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
    // UC7: ADD-ON SERVICE CLASS (NEW)
    // ============================================

    /**
     * UC7: AddOnService class - Represents an optional service offering
     *
     * Encapsulates information about optional add-on services like breakfast,
     * airport transfers, spa packages, etc.
     * Uses composition pattern to attach to reservations.
     */
    static class AddOnService {

        private String serviceId;
        private String serviceName;
        private String serviceDescription;
        private double servicePrice;
        private String serviceCategory; // "Meals", "Transport", "Spa", "Entertainment"

        /**
         * UC7: Constructor - Create an add-on service
         *
         * @param serviceId Unique service ID
         * @param serviceName Name of the service
         * @param serviceDescription Description of the service
         * @param servicePrice Price of the service
         * @param serviceCategory Category of the service
         */
        public AddOnService(String serviceId, String serviceName,
                            String serviceDescription, double servicePrice,
                            String serviceCategory) {
            this.serviceId = serviceId;
            this.serviceName = serviceName;
            this.serviceDescription = serviceDescription;
            this.servicePrice = servicePrice;
            this.serviceCategory = serviceCategory;
        }

        /**
         * UC7: Get service ID
         */
        public String getServiceId() {
            return serviceId;
        }

        /**
         * UC7: Get service name
         */
        public String getServiceName() {
            return serviceName;
        }

        /**
         * UC7: Get service description
         */
        public String getServiceDescription() {
            return serviceDescription;
        }

        /**
         * UC7: Get service price
         */
        public double getServicePrice() {
            return servicePrice;
        }

        /**
         * UC7: Get service category
         */
        public String getServiceCategory() {
            return serviceCategory;
        }

        /**
         * UC7: Display service details
         */
        public void displayDetails() {
            System.out.println("Service ID: " + serviceId);
            System.out.println("Service Name: " + serviceName);
            System.out.println("Description: " + serviceDescription);
            System.out.println("Price: ₹" + servicePrice);
            System.out.println("Category: " + serviceCategory);
        }

        @Override
        public String toString() {
            return "[" + serviceId + "] " + serviceName + " - ₹" + servicePrice;
        }
    }

    // ============================================
    // RESERVATION CLASS
    // ============================================

    /**
     * Reservation class - Represents a guest's booking intent
     */
    static class Reservation {

        private String reservationId;
        private String guestName;
        private String requestedRoomType;
        private int numberOfNights;
        private long requestTimestamp;
        private String status;
        private String assignedRoomId;

        /**
         * Constructor - Create a new reservation
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
         */
        public String getReservationId() {
            return reservationId;
        }

        /**
         * Get guest name
         */
        public String getGuestName() {
            return guestName;
        }

        /**
         * Get requested room type
         */
        public String getRequestedRoomType() {
            return requestedRoomType;
        }

        /**
         * Get number of nights
         */
        public int getNumberOfNights() {
            return numberOfNights;
        }

        /**
         * Get request timestamp
         */
        public long getRequestTimestamp() {
            return requestTimestamp;
        }

        /**
         * Get reservation status
         */
        public String getStatus() {
            return status;
        }

        /**
         * Set reservation status
         */
        public void setStatus(String status) {
            this.status = status;
        }

        /**
         * Get assigned room ID
         */
        public String getAssignedRoomId() {
            return assignedRoomId;
        }

        /**
         * Set assigned room ID
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
     */
    static class RoomInventory {

        private HashMap<String, Integer> inventoryMap;
        private HashMap<String, Integer> totalRoomsMap;

        /**
         * Constructor - Initialize inventory
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

            inventoryMap.put("Single Room", 5);
            inventoryMap.put("Double Room", 8);
            inventoryMap.put("Suite Room", 3);

            totalRoomsMap.put("Single Room", 5);
            totalRoomsMap.put("Double Room", 8);
            totalRoomsMap.put("Suite Room", 3);

            System.out.println("✓ Inventory initialized successfully!");
        }

        /**
         * Get available rooms for a specific room type
         */
        public int getAvailableRooms(String roomType) {
            return inventoryMap.getOrDefault(roomType, 0);
        }

        /**
         * Get total rooms for a specific room type
         */
        public int getTotalRooms(String roomType) {
            return totalRoomsMap.getOrDefault(roomType, 0);
        }

        /**
         * Check if room type exists in inventory
         */
        public boolean roomTypeExists(String roomType) {
            return inventoryMap.containsKey(roomType);
        }

        /**
         * Decrement room availability
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
         */
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

    /**
     * BookingRequestQueue class - FIFO booking request management
     */
    static class BookingRequestQueue {

        private Queue<Reservation> requestQueue;
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
         * Get the next booking request from queue
         */
        public Reservation peekNextRequest() {
            return requestQueue.peek();
        }

        /**
         * Remove and return the next booking request
         */
        public Reservation pollNextRequest() {
            return requestQueue.poll();
        }

        /**
         * Check if queue has pending requests
         */
        public boolean hasPendingRequests() {
            return !requestQueue.isEmpty();
        }

        /**
         * Get number of pending requests
         */
        public int getPendingRequestCount() {
            return requestQueue.size();
        }
    }

    // ============================================
    // ROOM ALLOCATION SERVICE CLASS
    // ============================================

    /**
     * RoomAllocationService class - Safe room allocation
     */
    static class RoomAllocationService {

        private Set<String> allocatedRoomIds;
        private HashMap<String, Set<String>> roomTypeToAllocatedIds;
        private HashMap<String, Integer> roomIdCounters;

        /**
         * Constructor - Initialize allocation service
         */
        public RoomAllocationService() {
            this.allocatedRoomIds = new HashSet<>();
            this.roomTypeToAllocatedIds = new HashMap<>();
            this.roomIdCounters = new HashMap<>();

            initializeAllocationService();
        }

        /**
         * Initialize allocation service with room types
         */
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

        /**
         * Generate unique room ID for a room type
         */
        private String generateUniqueRoomId(String roomType) {
            int counter = roomIdCounters.getOrDefault(roomType, 1);
            String roomId = roomType.substring(0, 1) + counter;

            roomIdCounters.put(roomType, counter + 1);

            return roomId;
        }

        /**
         * Check if room ID already allocated
         */
        private boolean isRoomIdAllocated(String roomId) {
            return allocatedRoomIds.contains(roomId);
        }

        /**
         * Allocate room to reservation
         */
        public boolean allocateRoom(Reservation reservation, RoomInventory inventory) {
            String roomType = reservation.getRequestedRoomType();

            if (!inventory.roomTypeExists(roomType)) {
                System.out.println("✗ ERROR: Room type '" + roomType + "' not found!");
                return false;
            }

            if (inventory.getAvailableRooms(roomType) <= 0) {
                System.out.println("✗ No " + roomType + " available!");
                return false;
            }

            String assignedRoomId = generateUniqueRoomId(roomType);

            if (isRoomIdAllocated(assignedRoomId)) {
                System.out.println("✗ ERROR: Room ID collision detected!");
                return false;
            }

            allocatedRoomIds.add(assignedRoomId);

            Set<String> roomTypeAllocations = roomTypeToAllocatedIds.get(roomType);
            roomTypeAllocations.add(assignedRoomId);

            if (!inventory.decrementRoomCount(roomType)) {
                allocatedRoomIds.remove(assignedRoomId);
                roomTypeAllocations.remove(assignedRoomId);
                System.out.println("✗ ERROR: Failed to update inventory!");
                return false;
            }

            reservation.setAssignedRoomId(assignedRoomId);
            reservation.setStatus("Confirmed");

            return true;
        }

        /**
         * Get allocated room IDs
         */
        public Set<String> getAllocatedRoomIds(String roomType) {
            return roomTypeToAllocatedIds.getOrDefault(roomType, new HashSet<>());
        }
    }

    // ============================================
    // UC7: ADD-ON SERVICE MANAGER CLASS (NEW)
    // ============================================

    /**
     * UC7: AddOnServiceManager class - Manages add-on service selection
     *
     * Handles the attachment of optional services to reservations.
     * Uses Map<String, List<AddOnService>> to store one-to-many relationship.
     * Calculates additional costs and maintains separation from core booking.
     */
    static class AddOnServiceManager {

        // UC7: Catalog of available add-on services
        private Map<String, AddOnService> serviceCatalog;

        // UC7: Map from reservation ID to list of selected services
        private Map<String, List<AddOnService>> reservationServices;

        /**
         * UC7: Constructor - Initialize service manager
         */
        public AddOnServiceManager() {
            this.serviceCatalog = new HashMap<>();
            this.reservationServices = new HashMap<>();

            initializeServiceCatalog();
        }

        /**
         * UC7: Initialize service catalog with available services
         */
        private void initializeServiceCatalog() {
            System.out.println("Initializing add-on service catalog...");

            // UC7: Meals category
            serviceCatalog.put("SVC-101", new AddOnService("SVC-101",
                    "Continental Breakfast", "Fresh breakfast for 2 guests", 500.0, "Meals"));
            serviceCatalog.put("SVC-102", new AddOnService("SVC-102",
                    "Evening Dinner", "3-course dinner for 2 guests", 1500.0, "Meals"));
            serviceCatalog.put("SVC-103", new AddOnService("SVC-103",
                    "In-Room Dining", "Premium in-room meal service", 800.0, "Meals"));

            // UC7: Transport category
            serviceCatalog.put("SVC-201", new AddOnService("SVC-201",
                    "Airport Transfer", "Pick-up from airport", 400.0, "Transport"));
            serviceCatalog.put("SVC-202", new AddOnService("SVC-202",
                    "City Tour", "Guided city tour (4 hours)", 1200.0, "Transport"));

            // UC7: Spa category
            serviceCatalog.put("SVC-301", new AddOnService("SVC-301",
                    "Spa Treatment", "Relaxation spa package (1 hour)", 2000.0, "Spa"));
            serviceCatalog.put("SVC-302", new AddOnService("SVC-302",
                    "Massage Therapy", "Professional massage (1 hour)", 1500.0, "Spa"));

            // UC7: Entertainment category
            serviceCatalog.put("SVC-401", new AddOnService("SVC-401",
                    "Late Checkout", "Checkout until 6 PM", 500.0, "Entertainment"));
            serviceCatalog.put("SVC-402", new AddOnService("SVC-402",
                    "Movie Night", "In-room movie package", 300.0, "Entertainment"));

            System.out.println("✓ Service catalog initialized with " +
                    serviceCatalog.size() + " services!");
        }

        /**
         * UC7: Get list of available services by category
         *
         * @param category Service category
         * @return List of services in that category
         */
        public List<AddOnService> getServicesByCategory(String category) {
            List<AddOnService> categoryServices = new ArrayList<>();

            for (AddOnService service : serviceCatalog.values()) {
                if (service.getServiceCategory().equals(category)) {
                    categoryServices.add(service);
                }
            }

            return categoryServices;
        }

        /**
         * UC7: Get service by ID
         *
         * @param serviceId Service ID
         * @return AddOnService object or null if not found
         */
        public AddOnService getServiceById(String serviceId) {
            return serviceCatalog.get(serviceId);
        }

        /**
         * UC7: Add service to reservation
         * Time Complexity: O(1) average for both get and add operations
         *
         * @param reservationId Reservation ID
         * @param serviceId Service ID
         * @return true if service added successfully, false otherwise
         */
        public boolean addServiceToReservation(String reservationId, String serviceId) {
            AddOnService service = serviceCatalog.get(serviceId);

            if (service == null) {
                System.out.println("✗ ERROR: Service '" + serviceId + "' not found!");
                return false;
            }

            // UC7: Get or create service list for this reservation
            List<AddOnService> services = reservationServices.computeIfAbsent(
                    reservationId, k -> new ArrayList<>());

            // UC7: Check if service already added to avoid duplicates
            for (AddOnService existingService : services) {
                if (existingService.getServiceId().equals(serviceId)) {
                    System.out.println("⚠ Service already added to reservation!");
                    return false;
                }
            }

            services.add(service);
            return true;
        }

        /**
         * UC7: Remove service from reservation
         *
         * @param reservationId Reservation ID
         * @param serviceId Service ID
         * @return true if service removed, false if not found
         */
        public boolean removeServiceFromReservation(String reservationId, String serviceId) {
            List<AddOnService> services = reservationServices.get(reservationId);

            if (services == null) {
                return false;
            }

            return services.removeIf(s -> s.getServiceId().equals(serviceId));
        }

        /**
         * UC7: Get all services for a reservation
         *
         * @param reservationId Reservation ID
         * @return List of services for the reservation
         */
        public List<AddOnService> getServicesForReservation(String reservationId) {
            return reservationServices.getOrDefault(reservationId, new ArrayList<>());
        }

        /**
         * UC7: Calculate total add-on cost for reservation
         * Time Complexity: O(n) where n = number of services
         *
         * @param reservationId Reservation ID
         * @return Total cost of all services
         */
        public double calculateAddOnCost(String reservationId) {
            List<AddOnService> services = getServicesForReservation(reservationId);

            double totalCost = 0.0;
            for (AddOnService service : services) {
                totalCost += service.getServicePrice();
            }

            return totalCost;
        }

        /**
         * UC7: Calculate total booking cost (room + services)
         *
         * @param roomPrice Price per night
         * @param numberOfNights Number of nights
         * @param reservationId Reservation ID
         * @return Total booking cost
         */
        public double calculateTotalBookingCost(double roomPrice, int numberOfNights,
                                                String reservationId) {
            double roomCost = roomPrice * numberOfNights;
            double addOnCost = calculateAddOnCost(reservationId);

            return roomCost + addOnCost;
        }

        /**
         * UC7: Display all available services
         */
        public void displayAllServices() {
            System.out.println("\n========================================");
            System.out.println("    AVAILABLE ADD-ON SERVICES           ");
            System.out.println("========================================");

            Map<String, List<AddOnService>> servicesByCategory = new HashMap<>();

            for (AddOnService service : serviceCatalog.values()) {
                String category = service.getServiceCategory();
                servicesByCategory.computeIfAbsent(category, k -> new ArrayList<>())
                        .add(service);
            }

            for (Map.Entry<String, List<AddOnService>> entry :
                    servicesByCategory.entrySet()) {
                System.out.println("\n" + entry.getKey() + ":");

                int position = 1;
                for (AddOnService service : entry.getValue()) {
                    System.out.println("  " + position + ". " + service);
                    System.out.println("     " + service.getServiceDescription());
                    position++;
                }
            }

            System.out.println("\n========================================");
        }

        /**
         * UC7: Display services for a specific reservation
         *
         * @param reservationId Reservation ID
         * @param guestName Guest name
         */
        public void displayReservationServices(String reservationId, String guestName) {
            List<AddOnService> services = getServicesForReservation(reservationId);

            System.out.println("\n--- Services for " + guestName + " ---");

            if (services.isEmpty()) {
                System.out.println("No add-on services selected");
                System.out.println("Add-on Cost: ₹0");
            } else {
                System.out.println("Selected Services (" + services.size() + "):");

                int position = 1;
                double totalCost = 0;

                for (AddOnService service : services) {
                    System.out.println("  " + position + ". " + service);
                    totalCost += service.getServicePrice();
                    position++;
                }

                System.out.println("Add-on Cost: ₹" + totalCost);
            }
        }

        /**
         * UC7: Display service statistics
         */
        public void displayServiceStatistics() {
            System.out.println("\n========================================");
            System.out.println("    SERVICE STATISTICS                  ");
            System.out.println("========================================");

            System.out.println("\nTotal Services in Catalog: " + serviceCatalog.size());
            System.out.println("Total Reservations with Services: " +
                    reservationServices.size());

            int totalServiceSelections = 0;
            for (List<AddOnService> services : reservationServices.values()) {
                totalServiceSelections += services.size();
            }

            System.out.println("Total Service Selections: " + totalServiceSelections);

            if (totalServiceSelections > 0) {
                double avgServicesPerReservation =
                        (double) totalServiceSelections / reservationServices.size();
                System.out.println("Avg Services per Reservation: " +
                        String.format("%.2f", avgServicesPerReservation));
            }

            System.out.println("\n========================================");
        }
    }

    // ============================================
    // DISPLAY METHODS
    // ============================================

    /**
     * Display welcome message
     */
    public static void displayWelcomeMessage() {
        System.out.println("\n========================================");
        System.out.println("    BOOK MY STAY - HOTEL BOOKING APP    ");
        System.out.println("========================================");
        System.out.println("Version: 7.1");
        System.out.println("Use Case 7: Add-On Service Selection");
        System.out.println("========================================\n");
    }

    /**
     * UC7: Demonstrate add-on service selection
     */
    public static void demonstrateAddOnServices(BookingRequestQueue requestQueue,
                                                RoomInventory inventory,
                                                RoomAllocationService allocationService,
                                                AddOnServiceManager serviceManager) {
        System.out.println("\n========================================");
        System.out.println("    ADD-ON SERVICE DEMONSTRATION        ");
        System.out.println("========================================");

        // Scenario 1: Display available services
        System.out.println("\n--- SCENARIO 1: Display available services ---");
        serviceManager.displayAllServices();

        // Scenario 2: Create and allocate reservations
        System.out.println("\n--- SCENARIO 2: Create and allocate reservations ---\n");

        Reservation res1 = requestQueue.addBookingRequest("Rajesh Kumar",
                "Single Room", 3);
        Reservation res2 = requestQueue.addBookingRequest("Priya Sharma",
                "Double Room", 2);
        Reservation res3 = requestQueue.addBookingRequest("Amit Patel",
                "Suite Room", 4);

        allocationService.allocateRoom(res1, inventory);
        allocationService.allocateRoom(res2, inventory);
        allocationService.allocateRoom(res3, inventory);

        System.out.println("✓ 3 reservations allocated\n");

        // Scenario 3: Add services to reservations
        System.out.println("--- SCENARIO 3: Add services to reservations ---\n");

        System.out.println("Adding services for Rajesh Kumar:");
        serviceManager.addServiceToReservation("RES-1001", "SVC-101");
        System.out.println("✓ Continental Breakfast added");
        serviceManager.addServiceToReservation("RES-1001", "SVC-201");
        System.out.println("✓ Airport Transfer added");

        System.out.println("\nAdding services for Priya Sharma:");
        serviceManager.addServiceToReservation("RES-1002", "SVC-102");
        System.out.println("✓ Evening Dinner added");
        serviceManager.addServiceToReservation("RES-1002", "SVC-301");
        System.out.println("✓ Spa Treatment added");
        serviceManager.addServiceToReservation("RES-1002", "SVC-401");
        System.out.println("✓ Late Checkout added");

        System.out.println("\nAdding services for Amit Patel:");
        serviceManager.addServiceToReservation("RES-1003", "SVC-202");
        System.out.println("✓ City Tour added");

        // Scenario 4: Display reservation details with services
        System.out.println("\n--- SCENARIO 4: Reservation details with services ---");

        serviceManager.displayReservationServices("RES-1001", "Rajesh Kumar");
        serviceManager.displayReservationServices("RES-1002", "Priya Sharma");
        serviceManager.displayReservationServices("RES-1003", "Amit Patel");

        // Scenario 5: Calculate costs
        System.out.println("\n--- SCENARIO 5: Cost calculation ---\n");

        double cost1 = serviceManager.calculateTotalBookingCost(2000, 3, "RES-1001");
        System.out.println("Total cost for Rajesh Kumar: ₹" + cost1);
        System.out.println("  (Room: ₹6000 + Services: ₹" +
                serviceManager.calculateAddOnCost("RES-1001") + ")");

        double cost2 = serviceManager.calculateTotalBookingCost(3500, 2, "RES-1002");
        System.out.println("\nTotal cost for Priya Sharma: ₹" + cost2);
        System.out.println("  (Room: ₹7000 + Services: ₹" +
                serviceManager.calculateAddOnCost("RES-1002") + ")");

        double cost3 = serviceManager.calculateTotalBookingCost(6000, 4, "RES-1003");
        System.out.println("\nTotal cost for Amit Patel: ₹" + cost3);
        System.out.println("  (Room: ₹24000 + Services: ₹" +
                serviceManager.calculateAddOnCost("RES-1003") + ")");

        // Scenario 6: Statistics
        System.out.println("\n--- SCENARIO 6: Service statistics ---");
        serviceManager.displayServiceStatistics();
    }

    // ============================================
    // MAIN METHOD
    // ============================================

    /**
     * Main method - Entry point of UC7
     */
    public static void main(String[] args) {
        displayWelcomeMessage();

        // Initialize system components
        System.out.println("--- STEP 1: Initialize System Components ---");
        RoomInventory inventory = new RoomInventory();
        inventory.displayInventory();

        BookingRequestQueue requestQueue = new BookingRequestQueue();
        System.out.println("\n✓ Booking request queue initialized");

        RoomAllocationService allocationService = new RoomAllocationService();

        // UC7: Initialize service manager (NEW)
        System.out.println("\n--- STEP 2: Initialize Add-On Service Manager ---");
        AddOnServiceManager serviceManager = new AddOnServiceManager();
        System.out.println("✓ Service manager initialized!");
        System.out.println("  Data Structure: Map<String, List<AddOnService>>");
        System.out.println("  Pattern: Composition over Inheritance");

        // UC7: Demonstrate add-on services
        System.out.println("\n--- STEP 3: Demonstrate Add-On Services ---");
        demonstrateAddOnServices(requestQueue, inventory, allocationService,
                serviceManager);

        // Final message
        System.out.println("\n========================================");
        System.out.println("UC7 Demonstration Complete!");
        System.out.println("Add-on service selection established.");
        System.out.println("Extensibility without modifying core logic verified.");
        System.out.println("Ready for advanced features in UC8+...");
        System.out.println("========================================\n");

        // Display UC7 advantages
        System.out.println("========================================");
        System.out.println("    UC7 ADVANTAGES - BUSINESS EXTENSION ");
        System.out.println("========================================");
        System.out.println("\n✓ Map<String, List<>> for one-to-many relationships");
        System.out.println("✓ Composition over inheritance pattern");
        System.out.println("✓ Separation of core and optional features");
        System.out.println("✓ Easy to add new services");
        System.out.println("✓ Core booking logic remains unchanged");
        System.out.println("✓ Independent service cost calculation");
        System.out.println("✓ Flexible feature attachment");
        System.out.println("✓ Business extensibility demonstrated");
        System.out.println("\n========================================\n");

        // Display data structure benefits
        System.out.println("========================================");
        System.out.println("    WHY MAP + LIST FOR SERVICES?       ");
        System.out.println("========================================");
        System.out.println("\nOne-to-Many Relationship Modeling:\n");
        System.out.println("Reservation ──────┬──── Service 1");
        System.out.println("                  ├──── Service 2");
        System.out.println("                  └──── Service 3");
        System.out.println("\nData Structure Benefits:");
        System.out.println("✓ Map: Fast reservation lookup - O(1)");
        System.out.println("✓ List: Preserves insertion order");
        System.out.println("✓ Combination: Perfect for this pattern");
        System.out.println("✓ Flexibility: Easy to extend with new services");
        System.out.println("✓ Isolation: Services don't affect core booking");
        System.out.println("\n========================================\n");
    }
}