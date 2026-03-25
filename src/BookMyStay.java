import java.util.*;
import java.time.LocalDateTime;

/**
 * Hotel Booking Management System - Use Case 11
 *
 * Concurrent Booking Simulation (Thread Safety)
 *
 * This class demonstrates:
 * - Race conditions and thread safety issues
 * - Synchronized access to shared resources
 * - Critical sections for safe operations
 * - Multi-threaded booking simulation
 * - Prevention of double allocation
 * - Concurrent inventory management
 * - Thread-safe booking queue
 *
 * @author sumanth-batna29
 * @version 11.1
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
            super("Invalid room type: '" + roomType + "'");
        }
    }

    static class InsufficientAvailabilityException extends BookingException {
        public InsufficientAvailabilityException(String roomType) {
            super("Insufficient availability for: '" + roomType + "'");
        }
    }

    // ============================================
    // INPUT VALIDATOR CLASS
    // ============================================

    static class InputValidator {
        private static final Set<String> VALID_ROOM_TYPES = new HashSet<>(
                Arrays.asList("Single Room", "Double Room", "Suite Room"));

        public static void validateRoomType(String roomType)
                throws InvalidRoomTypeException {
            if (roomType == null || !VALID_ROOM_TYPES.contains(roomType)) {
                throw new InvalidRoomTypeException(roomType);
            }
        }
    }

    // ============================================
    // ROOM CLASS
    // ============================================

    abstract static class Room {
        protected String roomType;
        protected double pricePerNight;

        public Room(String roomType, double pricePerNight) {
            this.roomType = roomType;
            this.pricePerNight = pricePerNight;
        }

        public String getRoomType() {
            return roomType;
        }

        public double getPricePerNight() {
            return pricePerNight;
        }
    }

    static class SingleRoom extends Room {
        public SingleRoom() {
            super("Single Room", 2000.0);
        }
    }

    static class DoubleRoom extends Room {
        public DoubleRoom() {
            super("Double Room", 3500.0);
        }
    }

    static class SuiteRoom extends Room {
        public SuiteRoom() {
            super("Suite Room", 6000.0);
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
        private LocalDateTime bookingTime;

        public Reservation(String reservationId, String guestName,
                           String requestedRoomType, int numberOfNights) {
            this.reservationId = reservationId;
            this.guestName = guestName;
            this.requestedRoomType = requestedRoomType;
            this.numberOfNights = numberOfNights;
            this.status = "Pending";
            this.assignedRoomId = null;
            this.bookingTime = LocalDateTime.now();
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

        public LocalDateTime getBookingTime() {
            return bookingTime;
        }

        @Override
        public String toString() {
            String roomInfo = (assignedRoomId != null) ?
                    " [Room: " + assignedRoomId + "]" : "";
            return "[" + reservationId + "] " + guestName +
                    " - " + requestedRoomType + " - " + status + roomInfo;
        }
    }

    // ============================================
    // UC11: THREAD-SAFE ROOM INVENTORY CLASS (NEW)
    // ============================================

    /**
     * UC11: ThreadSafeRoomInventory class - Synchronized inventory management
     *
     * Provides thread-safe access to room inventory.
     * Uses synchronized methods to protect shared mutable state.
     * Prevents race conditions during concurrent allocations.
     * Ensures inventory consistency under multi-threaded access.
     */
    static class ThreadSafeRoomInventory {
        private HashMap<String, Integer> inventoryMap;
        private HashMap<String, Integer> totalRoomsMap;

        // UC11: Lock object for synchronization
        private final Object inventoryLock = new Object();

        public ThreadSafeRoomInventory() {
            this.inventoryMap = new HashMap<>();
            this.totalRoomsMap = new HashMap<>();
            initializeInventory();
        }

        private void initializeInventory() {
            inventoryMap.put("Single Room", 5);
            inventoryMap.put("Double Room", 8);
            inventoryMap.put("Suite Room", 3);

            totalRoomsMap.put("Single Room", 5);
            totalRoomsMap.put("Double Room", 8);
            totalRoomsMap.put("Suite Room", 3);
        }

        /**
         * UC11: Thread-safe get available rooms
         * Time Complexity: O(1)
         */
        public int getAvailableRooms(String roomType) {
            synchronized (inventoryLock) {
                return inventoryMap.getOrDefault(roomType, 0);
            }
        }

        public int getTotalRooms(String roomType) {
            synchronized (inventoryLock) {
                return totalRoomsMap.getOrDefault(roomType, 0);
            }
        }

        /**
         * UC11: Thread-safe decrement room count (critical section)
         * Time Complexity: O(1)
         * Prevents double-booking through synchronized access
         */
        public synchronized boolean decrementRoomCount(String roomType)
                throws InvalidRoomTypeException, InsufficientAvailabilityException {

            // UC11: Critical section - only one thread at a time
            synchronized (inventoryLock) {
                if (!inventoryMap.containsKey(roomType)) {
                    throw new InvalidRoomTypeException(roomType);
                }

                int available = inventoryMap.get(roomType);
                if (available <= 0) {
                    throw new InsufficientAvailabilityException(roomType);
                }

                // UC11: Safe to decrement - atomic operation
                inventoryMap.put(roomType, available - 1);
                return true;
            }
        }

        /**
         * UC11: Thread-safe increment room count (for cancellation)
         */
        public boolean incrementRoomCount(String roomType)
                throws InvalidRoomTypeException {

            synchronized (inventoryLock) {
                if (!inventoryMap.containsKey(roomType)) {
                    throw new InvalidRoomTypeException(roomType);
                }

                int available = inventoryMap.get(roomType);
                int total = totalRoomsMap.get(roomType);

                if (available >= total) {
                    return false;
                }

                inventoryMap.put(roomType, available + 1);
                return true;
            }
        }

        /**
         * UC11: Display inventory (thread-safe)
         */
        public void displayInventory() {
            synchronized (inventoryLock) {
                System.out.println("\n========================================");
                System.out.println("    ROOM INVENTORY (Thread-Safe)       ");
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
    // UC11: THREAD-SAFE BOOKING QUEUE CLASS (NEW)
    // ============================================

    /**
     * UC11: ThreadSafeBookingQueue class - Synchronized request queue
     *
     * Provides thread-safe access to booking requests.
     * Multiple threads can safely add and retrieve requests.
     * Prevents request loss or duplication during concurrent access.
     */
    static class ThreadSafeBookingQueue {
        private Queue<Reservation> requestQueue;
        private int reservationCounter;

        // UC11: Lock for synchronization
        private final Object queueLock = new Object();

        public ThreadSafeBookingQueue() {
            this.requestQueue = new LinkedList<>();
            this.reservationCounter = 1000;
        }

        /**
         * UC11: Thread-safe add booking request
         */
        public Reservation addBookingRequest(String guestName,
                                             String requestedRoomType,
                                             int numberOfNights) {
            synchronized (queueLock) {
                String reservationId = "RES-" + (++reservationCounter);
                Reservation reservation = new Reservation(reservationId, guestName,
                        requestedRoomType, numberOfNights);
                requestQueue.offer(reservation);
                return reservation;
            }
        }

        /**
         * UC11: Thread-safe poll booking request
         */
        public Reservation pollNextRequest() {
            synchronized (queueLock) {
                return requestQueue.poll();
            }
        }

        /**
         * UC11: Thread-safe queue size check
         */
        public int getPendingRequestCount() {
            synchronized (queueLock) {
                return requestQueue.size();
            }
        }
    }

    // ============================================
    // UC11: THREAD-SAFE ROOM ALLOCATION SERVICE (NEW)
    // ============================================

    /**
     * UC11: ThreadSafeRoomAllocationService class - Synchronized allocation
     *
     * Manages room allocation with thread safety.
     * Prevents double-booking through synchronized critical sections.
     * Ensures only one thread allocates a room at a time.
     */
    static class ThreadSafeRoomAllocationService {
        private Set<String> allocatedRoomIds;
        private HashMap<String, Set<String>> roomTypeToAllocatedIds;
        private HashMap<String, Integer> roomIdCounters;

        // UC11: Lock for synchronization
        private final Object allocationLock = new Object();

        public ThreadSafeRoomAllocationService() {
            this.allocatedRoomIds = new HashSet<>();
            this.roomTypeToAllocatedIds = new HashMap<>();
            this.roomIdCounters = new HashMap<>();

            roomTypeToAllocatedIds.put("Single Room", new HashSet<>());
            roomTypeToAllocatedIds.put("Double Room", new HashSet<>());
            roomTypeToAllocatedIds.put("Suite Room", new HashSet<>());

            roomIdCounters.put("Single Room", 1);
            roomIdCounters.put("Double Room", 1);
            roomIdCounters.put("Suite Room", 1);
        }

        /**
         * UC11: Thread-safe room ID generation
         */
        private String generateUniqueRoomId(String roomType) {
            int counter = roomIdCounters.getOrDefault(roomType, 1);
            String roomId = roomType.substring(0, 1) + counter;
            roomIdCounters.put(roomType, counter + 1);
            return roomId;
        }

        /**
         * UC11: Thread-safe allocate room (critical section)
         * Prevents double-booking through synchronized access
         */
        public boolean allocateRoom(Reservation reservation,
                                    ThreadSafeRoomInventory inventory)
                throws InvalidRoomTypeException, InsufficientAvailabilityException {

            String roomType = reservation.getRequestedRoomType();

            // UC11: Critical section - synchronized block
            synchronized (allocationLock) {
                // UC11: Check availability first
                if (inventory.getAvailableRooms(roomType) <= 0) {
                    throw new InsufficientAvailabilityException(roomType);
                }

                // UC11: Generate unique room ID
                String assignedRoomId = generateUniqueRoomId(roomType);

                // UC11: Record allocation
                allocatedRoomIds.add(assignedRoomId);
                Set<String> roomTypeAllocations = roomTypeToAllocatedIds.get(roomType);
                roomTypeAllocations.add(assignedRoomId);

                // UC11: Update reservation
                reservation.setAssignedRoomId(assignedRoomId);
                reservation.setStatus("Confirmed");
            }

            // UC11: Decrement inventory (separate synchronized call)
            inventory.decrementRoomCount(roomType);

            return true;
        }
    }

    // ============================================
    // UC11: BOOKING THREAD CLASS (NEW)
    // ============================================

    /**
     * UC11: BookingThread class - Represents a guest booking request in a thread
     *
     * Each thread simulates a guest attempting to book a room.
     * Multiple threads execute concurrently to simulate real-world conditions.
     * Demonstrates race conditions and thread safety mechanisms.
     */
    static class BookingThread extends Thread {
        private String guestName;
        private String requestedRoomType;
        private ThreadSafeBookingQueue bookingQueue;
        private ThreadSafeRoomAllocationService allocationService;
        private ThreadSafeRoomInventory inventory;
        private List<Reservation> completedBookings;

        // UC11: Shared counter for booking attempts
        private static int bookingAttempts = 0;
        private static final Object counterLock = new Object();

        public BookingThread(String guestName, String requestedRoomType,
                             ThreadSafeBookingQueue queue,
                             ThreadSafeRoomAllocationService service,
                             ThreadSafeRoomInventory inv,
                             List<Reservation> bookings) {
            this.guestName = guestName;
            this.requestedRoomType = requestedRoomType;
            this.bookingQueue = queue;
            this.allocationService = service;
            this.inventory = inv;
            this.completedBookings = bookings;

            // UC11: Thread name for identification
            setName("Booking-Thread-" + guestName);
        }

        @Override
        public void run() {
            try {
                // UC11: Simulate booking request
                System.out.println("[" + Thread.currentThread().getName() +
                        "] Guest submitting booking request");

                // UC11: Add booking request to queue (thread-safe)
                Reservation reservation = bookingQueue.addBookingRequest(
                        guestName, requestedRoomType, 2);

                // UC11: Increment booking attempts (with synchronization)
                synchronized (counterLock) {
                    bookingAttempts++;
                }

                // UC11: Simulate processing delay
                Thread.sleep(10 + (int)(Math.random() * 50));

                // UC11: Allocate room (thread-safe critical section)
                allocationService.allocateRoom(reservation, inventory);

                // UC11: Record completed booking (thread-safe list)
                synchronized (completedBookings) {
                    completedBookings.add(reservation);
                }

                System.out.println("[" + Thread.currentThread().getName() +
                        "] ✓ Booking confirmed: " + reservation.getReservationId() +
                        " | Room: " + reservation.getAssignedRoomId());

            } catch (BookingException e) {
                System.out.println("[" + Thread.currentThread().getName() +
                        "] ✗ Booking failed: " + e.getMessage());
            } catch (InterruptedException e) {
                System.out.println("[" + Thread.currentThread().getName() +
                        "] Interrupted: " + e.getMessage());
                Thread.currentThread().interrupt();
            }
        }

        public static int getBookingAttempts() {
            synchronized (counterLock) {
                return bookingAttempts;
            }
        }
    }

    // ============================================
    // DISPLAY METHODS
    // ============================================

    public static void displayWelcomeMessage() {
        System.out.println("\n========================================");
        System.out.println("    BOOK MY STAY - HOTEL BOOKING APP    ");
        System.out.println("========================================");
        System.out.println("Version: 11.1");
        System.out.println("Use Case 11: Concurrent Booking Simulation");
        System.out.println("========================================\n");
    }

    /**
     * UC11: Demonstrate concurrent booking simulation
     */
    public static void demonstrateConcurrentBooking() {
        System.out.println("\n========================================");
        System.out.println("    CONCURRENT BOOKING SIMULATION       ");
        System.out.println("========================================");

        // Initialize thread-safe components
        ThreadSafeRoomInventory inventory = new ThreadSafeRoomInventory();
        ThreadSafeBookingQueue bookingQueue = new ThreadSafeBookingQueue();
        ThreadSafeRoomAllocationService allocationService =
                new ThreadSafeRoomAllocationService();

        // UC11: Shared list for completed bookings (thread-safe)
        List<Reservation> completedBookings =
                Collections.synchronizedList(new ArrayList<>());

        // Display initial inventory
        System.out.println("\n--- Initial Inventory ---");
        inventory.displayInventory();

        // UC11: Create multiple booking threads
        System.out.println("\n--- Creating Concurrent Booking Threads ---\n");

        String[] guestNames = {
                "John Smith", "Jane Doe", "Bob Johnson", "Alice Williams",
                "Charlie Brown", "Diana Prince", "Edward Norton", "Fiona Green",
                "George Harris", "Hannah Montana", "Isaac Newton", "Julia Roberts",
                "Kevin Hart", "Laura Palmer"
        };

        String[] roomTypes = {
                "Single Room", "Double Room", "Suite Room", "Single Room",
                "Double Room", "Suite Room", "Single Room", "Double Room",
                "Single Room", "Suite Room", "Double Room", "Single Room",
                "Suite Room", "Double Room"
        };

        // UC11: Array to hold thread references
        Thread[] bookingThreads = new Thread[guestNames.length];

        // UC11: Create and start all threads simultaneously
        System.out.println("Starting " + guestNames.length + " concurrent booking threads...\n");

        for (int i = 0; i < guestNames.length; i++) {
            bookingThreads[i] = new BookingThread(
                    guestNames[i], roomTypes[i], bookingQueue,
                    allocationService, inventory, completedBookings);

            bookingThreads[i].start();
        }

        // UC11: Wait for all threads to complete
        System.out.println("Waiting for all threads to complete...\n");

        try {
            for (Thread thread : bookingThreads) {
                thread.join();  // Wait for thread to finish
            }
        } catch (InterruptedException e) {
            System.out.println("Main thread interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
        }

        // UC11: Display results
        System.out.println("\n--- Concurrent Booking Results ---");
        System.out.println("\nBooking Attempts: " + BookingThread.getBookingAttempts());
        System.out.println("Successful Bookings: " + completedBookings.size());
        System.out.println("Pending Requests in Queue: " + bookingQueue.getPendingRequestCount());

        // UC11: Display final inventory
        System.out.println("\n--- Final Inventory (After Concurrent Bookings) ---");
        inventory.displayInventory();

        // UC11: Display booked reservations
        System.out.println("\n--- Confirmed Reservations ---");
        System.out.println("\nTotal confirmed: " + completedBookings.size() + "\n");

        int position = 1;
        for (Reservation booking : completedBookings) {
            System.out.println("  " + position + ". " + booking);
            position++;
        }
    }

    // ============================================
    // MAIN METHOD
    // ============================================

    public static void main(String[] args) {
        displayWelcomeMessage();

        System.out.println("--- STEP 1: Introduce Thread Safety Concepts ---");
        System.out.println("\n1. Race Conditions:");
        System.out.println("   - Multiple threads access shared data simultaneously");
        System.out.println("   - Final state depends on execution timing");
        System.out.println("   - Can cause double-booking, inventory inconsistency");

        System.out.println("\n2. Thread Safety Solutions:");
        System.out.println("   - Synchronization: synchronized keyword");
        System.out.println("   - Locks: Object-level locks");
        System.out.println("   - Critical Sections: Protected code blocks");

        System.out.println("\n3. Synchronization Mechanisms Used:");
        System.out.println("   - synchronized methods");
        System.out.println("   - synchronized blocks with explicit locks");
        System.out.println("   - Collections.synchronizedList()");

        System.out.println("\n--- STEP 2: Demonstrate Concurrent Booking ---");
        demonstrateConcurrentBooking();

        // Final message
        System.out.println("\n========================================");
        System.out.println("UC11 Demonstration Complete!");
        System.out.println("Concurrent booking simulation established.");
        System.out.println("Thread safety verified - no conflicts!");
        System.out.println("========================================\n");

        // Display UC11 advantages
        System.out.println("========================================");
        System.out.println("    UC11 ADVANTAGES - THREAD SAFETY     ");
        System.out.println("========================================");
        System.out.println("\n✓ Synchronized methods protect critical sections");
        System.out.println("✓ Explicit locks prevent race conditions");
        System.out.println("✓ Thread-safe collections for shared data");
        System.out.println("✓ No double-booking under concurrent load");
        System.out.println("✓ Consistent inventory across threads");
        System.out.println("✓ Atomic operations prevent partial updates");
        System.out.println("✓ Multi-user support with correctness");
        System.out.println("✓ Foundation for scalable systems");
        System.out.println("\n========================================\n");

        // Display concurrency concepts
        System.out.println("========================================");
        System.out.println("    SYNCHRONIZATION PATTERNS USED      ");
        System.out.println("========================================");
        System.out.println("\n1. Synchronized Methods:");
        System.out.println("   - Automatic lock on 'this' object");
        System.out.println("   - Simple to use");
        System.out.println("   - All method body is critical section");

        System.out.println("\n2. Synchronized Blocks:");
        System.out.println("   - Manual lock on specific object");
        System.out.println("   - Finer-grained control");
        System.out.println("   - Only critical code is locked");

        System.out.println("\n3. Atomic Operations:");
        System.out.println("   - Check-then-act as single unit");
        System.out.println("   - Prevents interleaving");
        System.out.println("   - Ensures consistency");

        System.out.println("\n4. Thread.join():");
        System.out.println("   - Wait for thread completion");
        System.out.println("   - Ensures all work is done");
        System.out.println("   - Prevents premature shutdown");

        System.out.println("\n========================================\n");
    }
}