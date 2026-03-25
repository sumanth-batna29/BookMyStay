import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Hotel Booking Management System - Use Case 8
 *
 * Booking History & Reporting
 *
 * This class demonstrates:
 * - List data structure for ordered historical records
 * - Audit trail creation from confirmed bookings
 * - Separation of data storage and reporting
 * - Historical tracking and persistence mindset
 * - Comprehensive reporting and analysis
 * - Operational visibility and administrative oversight
 *
 * @author sumanth-batna29
 * @version 8.1
 * @since 2026-03-25
 */
public class BookMyStay {

    // ============================================
    // ABSTRACT ROOM CLASS
    // ============================================

    /**
     * Abstract Room class representing a generalized hotel room concept.
     */
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
            System.out.println("Perfect for: Solo travelers");
            System.out.println("Bed Type: Single");
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
            System.out.println("Perfect for: Couples, Friends");
            System.out.println("Bed Type: Double");
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
            System.out.println("Perfect for: Premium guests, Business travelers");
            System.out.println("Bed Type: King Size");
            System.out.println("Extra Features: Mini Bar, Balcony, Premium Amenities");
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
                           String requestedRoomType, int numberOfNights) {
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

        public void setStatus(String status) {
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

        public void addService(AddOnService service) {
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
            if (confirmationTime != null) {
                System.out.println("Confirmation Time: " + confirmationTime);
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
                                             int numberOfNights) {
            String reservationId = "RES-" + (++reservationCounter);
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

        public int getPendingRequestCount() {
            return requestQueue.size();
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
            reservation.setConfirmationTime(LocalDateTime.now());

            return true;
        }
    }

    // ============================================
    // ADD-ON SERVICE MANAGER CLASS
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
            serviceCatalog.put("SVC-401", new AddOnService("SVC-401",
                    "Late Checkout", "Checkout until 6 PM", 500.0, "Entertainment"));

            System.out.println("✓ Service catalog initialized!");
        }

        public AddOnService getServiceById(String serviceId) {
            return serviceCatalog.get(serviceId);
        }
    }

    // ============================================
    // UC8: BOOKING HISTORY CLASS (NEW)
    // ============================================

    /**
     * UC8: BookingHistory class - Maintains chronological record of confirmed bookings
     *
     * Uses List<Reservation> to store confirmed bookings in insertion order.
     * Serves as audit trail for operational visibility and historical tracking.
     * Enables administrators to review completed transactions.
     */
    static class BookingHistory {

        // UC8: List maintains bookings in confirmation order (ordered storage)
        private List<Reservation> confirmedBookings;

        /**
         * UC8: Constructor - Initialize booking history
         */
        public BookingHistory() {
            // UC8: LinkedList for efficient insertion and preserves order
            this.confirmedBookings = new ArrayList<>();
        }

        /**
         * UC8: Add confirmed booking to history
         * Time Complexity: O(1) for ArrayList append operation
         *
         * @param reservation Confirmed reservation to record
         */
        public void recordConfirmedBooking(Reservation reservation) {
            if (reservation.getStatus().equals("Confirmed")) {
                confirmedBookings.add(reservation);
                System.out.println("✓ Booking recorded in history: " +
                        reservation.getReservationId());
            } else {
                System.out.println("✗ Only confirmed bookings can be recorded!");
            }
        }

        /**
         * UC8: Get all confirmed bookings (read-only view)
         *
         * @return Unmodifiable list of confirmed bookings
         */
        public List<Reservation> getAllBookings() {
            return Collections.unmodifiableList(confirmedBookings);
        }

        /**
         * UC8: Get booking by reservation ID
         * Time Complexity: O(n) for linear search
         *
         * @param reservationId Reservation ID to search
         * @return Reservation if found, null otherwise
         */
        public Reservation getBookingById(String reservationId) {
            for (Reservation booking : confirmedBookings) {
                if (booking.getReservationId().equals(reservationId)) {
                    return booking;
                }
            }
            return null;
        }

        /**
         * UC8: Get total number of confirmed bookings
         *
         * @return Count of bookings
         */
        public int getTotalBookingCount() {
            return confirmedBookings.size();
        }

        /**
         * UC8: Display complete booking history
         * Time Complexity: O(n)
         */
        public void displayCompleteHistory() {
            System.out.println("\n========================================");
            System.out.println("    COMPLETE BOOKING HISTORY            ");
            System.out.println("========================================");
            System.out.println("\nTotal Confirmed Bookings: " + confirmedBookings.size());

            if (confirmedBookings.isEmpty()) {
                System.out.println("No bookings in history yet.");
                System.out.println("\n========================================");
                return;
            }

            System.out.println("\nBooking Records (Insertion Order):\n");

            int position = 1;
            for (Reservation booking : confirmedBookings) {
                System.out.println("  " + position + ". " + booking);
                position++;
            }

            System.out.println("\n========================================");
        }

        /**
         * UC8: Display booking details by ID
         *
         * @param reservationId Reservation ID
         */
        public void displayBookingDetails(String reservationId) {
            Reservation booking = getBookingById(reservationId);

            if (booking == null) {
                System.out.println("✗ Booking not found: " + reservationId);
                return;
            }

            System.out.println("\n========================================");
            System.out.println("    BOOKING DETAILS                     ");
            System.out.println("========================================");
            booking.displayDetails();
            System.out.println("========================================");
        }
    }

    // ============================================
    // UC8: BOOKING REPORT SERVICE CLASS (NEW)
    // ============================================

    /**
     * UC8: BookingReportService class - Generates reports from booking history
     *
     * Provides read-only analysis and reporting capabilities.
     * Separates reporting logic from data storage.
     * Enables operational visibility without modifying historical data.
     */
    static class BookingReportService {

        private BookingHistory bookingHistory;
        private RoomInventory inventory;
        private Map<String, Room> roomCatalog;

        /**
         * UC8: Constructor - Initialize report service
         *
         * @param history BookingHistory instance
         * @param inv RoomInventory instance
         */
        public BookingReportService(BookingHistory history, RoomInventory inv) {
            this.bookingHistory = history;
            this.inventory = inv;
            this.roomCatalog = new HashMap<>();

            // Initialize room catalog
            roomCatalog.put("Single Room", new SingleRoom());
            roomCatalog.put("Double Room", new DoubleRoom());
            roomCatalog.put("Suite Room", new SuiteRoom());
        }

        /**
         * UC8: Generate summary report
         * Time Complexity: O(n)
         */
        public void generateSummaryReport() {
            System.out.println("\n========================================");
            System.out.println("    BOOKING SUMMARY REPORT              ");
            System.out.println("========================================");

            List<Reservation> bookings = bookingHistory.getAllBookings();

            if (bookings.isEmpty()) {
                System.out.println("\nNo bookings to report.");
                System.out.println("\n========================================");
                return;
            }

            double totalRevenue = 0;
            int totalNights = 0;
            int totalGuests = bookings.size();

            Map<String, Integer> bookingsByType = new HashMap<>();

            for (Reservation booking : bookings) {
                totalRevenue += booking.getTotalCost();
                totalNights += booking.getNumberOfNights();

                String roomType = booking.getRequestedRoomType();
                bookingsByType.put(roomType, bookingsByType.getOrDefault(roomType, 0) + 1);
            }

            System.out.println("\nBooking Overview:");
            System.out.println("  Total Bookings: " + totalGuests);
            System.out.println("  Total Revenue: ₹" + String.format("%.2f", totalRevenue));
            System.out.println("  Total Nights: " + totalNights);
            System.out.println("  Average Revenue per Booking: ₹" +
                    String.format("%.2f", totalRevenue / totalGuests));

            System.out.println("\nBookings by Room Type:");
            for (Map.Entry<String, Integer> entry : bookingsByType.entrySet()) {
                System.out.println("  " + entry.getKey() + ": " + entry.getValue());
            }

            System.out.println("\n========================================");
        }

        /**
         * UC8: Generate revenue report
         */
        public void generateRevenueReport() {
            System.out.println("\n========================================");
            System.out.println("    REVENUE ANALYSIS REPORT             ");
            System.out.println("========================================");

            List<Reservation> bookings = bookingHistory.getAllBookings();

            double totalRevenue = 0;
            double maxRevenue = 0;
            double minRevenue = Double.MAX_VALUE;

            Map<String, Double> revenueByType = new HashMap<>();

            for (Reservation booking : bookings) {
                double cost = booking.getTotalCost();
                totalRevenue += cost;
                maxRevenue = Math.max(maxRevenue, cost);
                minRevenue = Math.min(minRevenue, cost);

                String roomType = booking.getRequestedRoomType();
                revenueByType.put(roomType,
                        revenueByType.getOrDefault(roomType, 0.0) + cost);
            }

            System.out.println("\nRevenue Summary:");
            System.out.println("  Total Revenue: ₹" + String.format("%.2f", totalRevenue));
            System.out.println("  Highest Booking: ₹" + String.format("%.2f", maxRevenue));
            System.out.println("  Lowest Booking: ₹" + String.format("%.2f", minRevenue));

            if (!bookings.isEmpty()) {
                System.out.println("  Average Booking: ₹" +
                        String.format("%.2f", totalRevenue / bookings.size()));
            }

            System.out.println("\nRevenue by Room Type:");
            for (Map.Entry<String, Double> entry : revenueByType.entrySet()) {
                System.out.println("  " + entry.getKey() + ": ₹" +
                        String.format("%.2f", entry.getValue()));
            }

            System.out.println("\n========================================");
        }

        /**
         * UC8: Generate occupancy report
         */
        public void generateOccupancyReport() {
            System.out.println("\n========================================");
            System.out.println("    OCCUPANCY REPORT                    ");
            System.out.println("========================================");

            System.out.println("\nCurrent Room Status:");

            for (String roomType : new String[]{"Single Room", "Double Room", "Suite Room"}) {
                int available = inventory.getAvailableRooms(roomType);
                int total = inventory.getTotalRooms(roomType);
                int booked = total - available;

                double occupancyRate = (booked * 100.0) / total;

                System.out.println("\n" + roomType + ":");
                System.out.println("  Available: " + available + " / " + total);
                System.out.println("  Booked: " + booked);
                System.out.println("  Occupancy Rate: " +
                        String.format("%.1f%%", occupancyRate));
            }

            System.out.println("\n========================================");
        }

        /**
         * UC8: Generate guest list report
         */
        public void generateGuestListReport() {
            System.out.println("\n========================================");
            System.out.println("    GUEST LIST REPORT                   ");
            System.out.println("========================================");

            List<Reservation> bookings = bookingHistory.getAllBookings();

            System.out.println("\nGuest Bookings:\n");

            int position = 1;
            for (Reservation booking : bookings) {
                System.out.println("  " + position + ". " + booking.getGuestName());
                System.out.println("     Reservation: " + booking.getReservationId());
                System.out.println("     Room: " + booking.getAssignedRoomId() +
                        " (" + booking.getRequestedRoomType() + ")");
                System.out.println("     Nights: " + booking.getNumberOfNights());
                System.out.println("     Cost: ₹" + booking.getTotalCost());
                System.out.println();

                position++;
            }

            System.out.println("========================================");
        }
    }

    // ============================================
    // DISPLAY METHODS
    // ============================================

    public static void displayWelcomeMessage() {
        System.out.println("\n========================================");
        System.out.println("    BOOK MY STAY - HOTEL BOOKING APP    ");
        System.out.println("========================================");
        System.out.println("Version: 8.1");
        System.out.println("Use Case 8: Booking History & Reporting");
        System.out.println("========================================\n");
    }

    /**
     * UC8: Demonstrate booking history and reporting
     */
    public static void demonstrateHistoryAndReporting(
            BookingRequestQueue requestQueue,
            RoomInventory inventory,
            RoomAllocationService allocationService,
            AddOnServiceManager serviceManager,
            BookingHistory bookingHistory,
            BookingReportService reportService) {

        System.out.println("\n========================================");
        System.out.println("    HISTORY & REPORTING DEMONSTRATION   ");
        System.out.println("========================================");

        // Scenario 1: Create and confirm bookings
        System.out.println("\n--- SCENARIO 1: Create and confirm multiple bookings ---\n");

        Reservation res1 = requestQueue.addBookingRequest("Rajesh Kumar",
                "Single Room", 3);
        Reservation res2 = requestQueue.addBookingRequest("Priya Sharma",
                "Double Room", 2);
        Reservation res3 = requestQueue.addBookingRequest("Amit Patel",
                "Suite Room", 4);
        Reservation res4 = requestQueue.addBookingRequest("Neha Singh",
                "Single Room", 2);

        // Allocate rooms
        allocationService.allocateRoom(res1, inventory);
        allocationService.allocateRoom(res2, inventory);
        allocationService.allocateRoom(res3, inventory);
        allocationService.allocateRoom(res4, inventory);

        // Calculate and set costs
        res1.setTotalCost(2000 * 3 + 500 + 400); // Room + Breakfast + Airport
        res2.setTotalCost(3500 * 2 + 1500 + 2000); // Room + Dinner + Spa
        res3.setTotalCost(6000 * 4 + 500); // Room + Late Checkout
        res4.setTotalCost(2000 * 2); // Room only

        // UC8: Record bookings in history (NEW)
        System.out.println("\n--- SCENARIO 2: Record confirmed bookings in history ---\n");

        bookingHistory.recordConfirmedBooking(res1);
        bookingHistory.recordConfirmedBooking(res2);
        bookingHistory.recordConfirmedBooking(res3);
        bookingHistory.recordConfirmedBooking(res4);

        System.out.println("\n✓ " + bookingHistory.getTotalBookingCount() +
                " bookings recorded in history");

        // UC8: Display complete history
        System.out.println("\n--- SCENARIO 3: Display complete booking history ---");
        bookingHistory.displayCompleteHistory();

        // UC8: Display specific booking details
        System.out.println("\n--- SCENARIO 4: Look up specific booking ---");
        bookingHistory.displayBookingDetails("RES-1001");

        // UC8: Generate reports
        System.out.println("\n--- SCENARIO 5: Generate reports ---");
        reportService.generateSummaryReport();
        reportService.generateRevenueReport();
        reportService.generateOccupancyReport();
        reportService.generateGuestListReport();
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

        AddOnServiceManager serviceManager = new AddOnServiceManager();

        // UC8: Initialize booking history and report service (NEW)
        System.out.println("\n--- STEP 2: Initialize Booking History & Reporting ---");
        BookingHistory bookingHistory = new BookingHistory();
        System.out.println("✓ Booking history initialized!");
        System.out.println("  Data Structure: List<Reservation>");
        System.out.println("  Maintains: Insertion order (chronological)");

        BookingReportService reportService = new BookingReportService(inventory,
                bookingHistory);
        System.out.println("✓ Report service initialized!");
        System.out.println("  Reports Available: Summary, Revenue, Occupancy, Guest List");

        // UC8: Demonstrate history and reporting
        System.out.println("\n--- STEP 3: Demonstrate History & Reporting ---");
        demonstrateHistoryAndReporting(requestQueue, inventory, allocationService,
                serviceManager, bookingHistory, reportService);

        // Final message
        System.out.println("\n========================================");
        System.out.println("UC8 Demonstration Complete!");
        System.out.println("Booking history and reporting established.");
        System.out.println("Operational visibility and audit trail verified.");
        System.out.println("Ready for advanced features and persistence in UC9+...");
        System.out.println("========================================\n");

        // Display UC8 advantages
        System.out.println("========================================");
        System.out.println("    UC8 ADVANTAGES - HISTORY & REPORTS  ");
        System.out.println("========================================");
        System.out.println("\n✓ List<Reservation> maintains chronological order");
        System.out.println("✓ O(1) append for recording confirmations");
        System.out.println("✓ Read-only audit trail");
        System.out.println("✓ Separated reporting from storage");
        System.out.println("✓ Operational visibility enabled");
        System.out.println("✓ Historical tracking for compliance");
        System.out.println("✓ Admin oversight and analysis");
        System.out.println("✓ Persistence mindset established");
        System.out.println("\n========================================\n");

        // Display data structure benefits
        System.out.println("========================================");
        System.out.println("    WHY LIST FOR BOOKING HISTORY?      ");
        System.out.println("========================================");
        System.out.println("\nHistorical Record Requirements:\n");
        System.out.println("✓ Ordered Storage: Chronological record");
        System.out.println("✓ Fast Insertion: O(1) append operation");
        System.out.println("✓ Sequential Access: Natural reporting");
        System.out.println("✓ Audit Trail: Complete transaction history");
        System.out.println("✓ Scalability: Grows with bookings");
        System.out.println("✓ Immutability: Read-only for reporting");
        System.out.println("\nBetter than alternatives:");
        System.out.println("  Queue: Would lose old entries");
        System.out.println("  Set: No order or duplicates");
        System.out.println("  Map: Not suitable for sequences");
        System.out.println("\n========================================\n");
    }
}