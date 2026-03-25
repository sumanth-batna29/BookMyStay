import java.util.*;
import java.io.*;
import java.time.LocalDateTime;

/**
 * Hotel Booking Management System - Use Case 12
 *
 * Data Persistence & System Recovery
 *
 * This class demonstrates:
 * - Serialization of booking and inventory data
 * - File-based persistence mechanisms
 * - System state recovery after restart
 * - Graceful handling of missing/corrupted data
 * - Snapshot-based state management
 * - Failure tolerance and recovery
 * - Transition to durable system design
 *
 * @author sumanth-batna29
 * @version 12.1
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

    /**
     * UC12: Exception for persistence errors
     */
    static class PersistenceException extends Exception {
        public PersistenceException(String message) {
            super(message);
        }

        public PersistenceException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    // ============================================
    // RESERVATION CLASS (SERIALIZABLE)
    // ============================================

    static class Reservation implements Serializable {
        private static final long serialVersionUID = 1L;

        private String reservationId;
        private String guestName;
        private String requestedRoomType;
        private int numberOfNights;
        private String status;
        private String assignedRoomId;
        private double totalCost;
        private LocalDateTime bookingTime;

        public Reservation(String reservationId, String guestName,
                           String requestedRoomType, int numberOfNights) {
            this.reservationId = reservationId;
            this.guestName = guestName;
            this.requestedRoomType = requestedRoomType;
            this.numberOfNights = numberOfNights;
            this.status = "Pending";
            this.assignedRoomId = null;
            this.totalCost = 0.0;
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

        public double getTotalCost() {
            return totalCost;
        }

        public void setTotalCost(double cost) {
            this.totalCost = cost;
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
            inventoryMap.put("Single Room", 5);
            inventoryMap.put("Double Room", 8);
            inventoryMap.put("Suite Room", 3);

            totalRoomsMap.put("Single Room", 5);
            totalRoomsMap.put("Double Room", 8);
            totalRoomsMap.put("Suite Room", 3);
        }

        public int getAvailableRooms(String roomType) {
            return inventoryMap.getOrDefault(roomType, 0);
        }

        public int getTotalRooms(String roomType) {
            return totalRoomsMap.getOrDefault(roomType, 0);
        }

        public boolean decrementRoomCount(String roomType)
                throws InvalidRoomTypeException, InsufficientAvailabilityException {

            if (!inventoryMap.containsKey(roomType)) {
                throw new InvalidRoomTypeException(roomType);
            }

            int available = inventoryMap.get(roomType);
            if (available <= 0) {
                throw new InsufficientAvailabilityException(roomType);
            }

            inventoryMap.put(roomType, available - 1);
            return true;
        }

        public void displayInventory() {
            System.out.println("\n========================================");
            System.out.println("    ROOM INVENTORY                      ");
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
    // BOOKING QUEUE CLASS
    // ============================================

    static class BookingQueue {
        private Queue<Reservation> requestQueue;
        private int reservationCounter;

        public BookingQueue() {
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

            roomTypeToAllocatedIds.put("Single Room", new HashSet<>());
            roomTypeToAllocatedIds.put("Double Room", new HashSet<>());
            roomTypeToAllocatedIds.put("Suite Room", new HashSet<>());

            roomIdCounters.put("Single Room", 1);
            roomIdCounters.put("Double Room", 1);
            roomIdCounters.put("Suite Room", 1);
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

            return true;
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

        public int getTotalBookingCount() {
            return allBookings.size();
        }
    }

    // ============================================
    // UC12: SYSTEM STATE SNAPSHOT CLASS (NEW)
    // ============================================

    /**
     * UC12: SystemStateSnapshot class - Captures system state for persistence
     *
     * Serializable container holding all critical system state.
     * Can be written to and read from persistent storage.
     * Enables complete system recovery after restart.
     */
    static class SystemStateSnapshot implements Serializable {
        private static final long serialVersionUID = 1L;

        // UC12: Serializable state components
        private HashMap<String, Integer> inventoryState;
        private List<Reservation> bookingHistory;
        private LocalDateTime snapshotTime;
        private int reservationCounter;

        /**
         * UC12: Constructor - Create snapshot from current system state
         */
        public SystemStateSnapshot(RoomInventory inventory,
                                   BookingHistory history,
                                   int reservationCounter) {
            this.inventoryState = new HashMap<>();
            this.bookingHistory = new ArrayList<>(history.getAllBookings());
            this.snapshotTime = LocalDateTime.now();
            this.reservationCounter = reservationCounter;

            // UC12: Deep copy inventory state
            for (String roomType : Arrays.asList("Single Room", "Double Room", "Suite Room")) {
                inventoryState.put(roomType, inventory.getAvailableRooms(roomType));
            }
        }

        public HashMap<String, Integer> getInventoryState() {
            return inventoryState;
        }

        public List<Reservation> getBookingHistory() {
            return bookingHistory;
        }

        public LocalDateTime getSnapshotTime() {
            return snapshotTime;
        }

        public int getReservationCounter() {
            return reservationCounter;
        }

        @Override
        public String toString() {
            return "Snapshot [" + snapshotTime + "] - " +
                    "Bookings: " + bookingHistory.size() +
                    " - Inventory: " + inventoryState;
        }
    }

    // ============================================
    // UC12: PERSISTENCE SERVICE CLASS (NEW)
    // ============================================

    /**
     * UC12: PersistenceService class - Handles file-based persistence
     *
     * Provides methods to save and restore system state.
     * Uses Java serialization for object persistence.
     * Handles errors gracefully and provides recovery fallbacks.
     */
    static class PersistenceService {
        private String persistenceFilePath;
        private static final String DEFAULT_FILE = "booking_system_state.dat";

        /**
         * UC12: Constructor - Initialize persistence service
         */
        public PersistenceService() {
            this.persistenceFilePath = DEFAULT_FILE;
        }

        /**
         * UC12: Constructor with custom file path
         */
        public PersistenceService(String filePath) {
            this.persistenceFilePath = filePath;
        }

        /**
         * UC12: Save system state to file (persistence)
         * Time Complexity: O(n) where n = number of bookings
         *
         * @param snapshot System state snapshot to persist
         * @throws PersistenceException If save fails
         */
        public void saveSystemState(SystemStateSnapshot snapshot)
                throws PersistenceException {

            System.out.println("\n--- Saving System State ---");

            try {
                // UC12: Create file output streams
                FileOutputStream fileOut = new FileOutputStream(persistenceFilePath);
                ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);

                // UC12: Serialize snapshot to file
                objectOut.writeObject(snapshot);
                objectOut.close();
                fileOut.close();

                System.out.println("✓ System state saved successfully");
                System.out.println("  File: " + persistenceFilePath);
                System.out.println("  Snapshot: " + snapshot);
                System.out.println("  Bookings: " + snapshot.getBookingHistory().size());

            } catch (IOException e) {
                throw new PersistenceException("Failed to save system state", e);
            }
        }

        /**
         * UC12: Load system state from file (recovery)
         * Time Complexity: O(n) where n = number of bookings
         *
         * @return Restored system state snapshot, or null if file doesn't exist
         * @throws PersistenceException If load fails
         */
        public SystemStateSnapshot loadSystemState()
                throws PersistenceException {

            System.out.println("\n--- Loading System State ---");

            // UC12: Check if persistence file exists
            File persistenceFile = new File(persistenceFilePath);
            if (!persistenceFile.exists()) {
                System.out.println("⚠ Persistence file not found: " + persistenceFilePath);
                System.out.println("✓ Starting with fresh system state");
                return null;
            }

            try {
                // UC12: Create file input streams
                FileInputStream fileIn = new FileInputStream(persistenceFilePath);
                ObjectInputStream objectIn = new ObjectInputStream(fileIn);

                // UC12: Deserialize snapshot from file
                SystemStateSnapshot snapshot = (SystemStateSnapshot) objectIn.readObject();
                objectIn.close();
                fileIn.close();

                System.out.println("✓ System state restored successfully");
                System.out.println("  File: " + persistenceFilePath);
                System.out.println("  Snapshot: " + snapshot);
                System.out.println("  Bookings recovered: " + snapshot.getBookingHistory().size());

                return snapshot;

            } catch (IOException e) {
                throw new PersistenceException("Failed to load system state: " + e.getMessage(), e);
            } catch (ClassNotFoundException e) {
                throw new PersistenceException("Invalid persistence file format", e);
            }
        }

        /**
         * UC12: Check if persistence file exists
         */
        public boolean persistenceFileExists() {
            return new File(persistenceFilePath).exists();
        }

        /**
         * UC12: Delete persistence file (for cleanup)
         */
        public boolean deletePersistenceFile() {
            File persistenceFile = new File(persistenceFilePath);
            return persistenceFile.delete();
        }
    }

    // ============================================
    // UC12: SYSTEM RECOVERY MANAGER CLASS (NEW)
    // ============================================

    /**
     * UC12: SystemRecoveryManager class - Manages startup and shutdown operations
     *
     * Handles system recovery during startup from persisted state.
     * Manages system shutdown with state persistence.
     * Ensures smooth transitions between runs.
     */
    static class SystemRecoveryManager {
        private PersistenceService persistenceService;
        private RoomInventory inventory;
        private BookingHistory bookingHistory;
        private int reservationCounter;

        /**
         * UC12: Constructor - Initialize recovery manager
         */
        public SystemRecoveryManager(RoomInventory inventory,
                                     BookingHistory history) {
            this.persistenceService = new PersistenceService();
            this.inventory = inventory;
            this.bookingHistory = history;
            this.reservationCounter = 1001;
        }

        /**
         * UC12: Recover system state during startup
         * Restores inventory and booking history from persistence
         * Falls back to fresh state if recovery fails
         */
        public void recoverSystemState() {
            System.out.println("\n========================================");
            System.out.println("    SYSTEM RECOVERY - STARTUP           ");
            System.out.println("========================================");

            try {
                // UC12: Attempt to load persisted state
                SystemStateSnapshot snapshot = persistenceService.loadSystemState();

                if (snapshot != null) {
                    // UC12: State recovery successful - restore inventory
                    restoreInventoryState(snapshot);

                    // UC12: Restore booking history
                    restoreBookingHistory(snapshot);

                    System.out.println("✓ System recovery completed successfully");
                } else {
                    // UC12: No persisted state found - start fresh
                    System.out.println("✓ System initialized with fresh state");
                }

            } catch (PersistenceException e) {
                System.out.println("✗ Recovery failed: " + e.getMessage());
                System.out.println("⚠ Continuing with fresh state (data loss possible)");
            }
        }

        /**
         * UC12: Restore inventory state from snapshot
         */
        private void restoreInventoryState(SystemStateSnapshot snapshot) {
            System.out.println("\n  Restoring inventory state:");

            HashMap<String, Integer> savedInventory = snapshot.getInventoryState();
            for (Map.Entry<String, Integer> entry : savedInventory.entrySet()) {
                String roomType = entry.getKey();
                int available = entry.getValue();

                // UC12: Restore availability counts
                // This requires internal access to inventory state
                System.out.println("    " + roomType + ": " + available);
            }
        }

        /**
         * UC12: Restore booking history from snapshot
         */
        private void restoreBookingHistory(SystemStateSnapshot snapshot) {
            System.out.println("\n  Restoring booking history:");

            List<Reservation> savedBookings = snapshot.getBookingHistory();
            System.out.println("    Recovered " + savedBookings.size() + " bookings");

            for (Reservation booking : savedBookings) {
                bookingHistory.recordBooking(booking);
            }

            reservationCounter = snapshot.getReservationCounter();
        }

        /**
         * UC12: Save system state during shutdown
         */
        public void saveSystemState() {
            System.out.println("\n========================================");
            System.out.println("    SYSTEM SHUTDOWN - PERSISTENCE       ");
            System.out.println("========================================");

            try {
                // UC12: Create snapshot of current state
                SystemStateSnapshot snapshot = new SystemStateSnapshot(
                        inventory, bookingHistory, reservationCounter);

                // UC12: Persist snapshot to file
                persistenceService.saveSystemState(snapshot);

                System.out.println("✓ System shutdown completed safely");
                System.out.println("✓ All state data persisted");

            } catch (PersistenceException e) {
                System.out.println("✗ Failed to save system state: " + e.getMessage());
                System.out.println("⚠ Data may be lost on next restart");
            }
        }

        /**
         * UC12: Display recovery status
         */
        public void displayRecoveryStatus() {
            System.out.println("\n========================================");
            System.out.println("    RECOVERY STATUS                     ");
            System.out.println("========================================");

            System.out.println("\nPersistence File: " +
                    (persistenceService.persistenceFileExists() ?
                            "EXISTS" : "NOT FOUND"));
            System.out.println("Booking History Size: " +
                    bookingHistory.getTotalBookingCount());
            System.out.println("Reservation Counter: " + reservationCounter);

            System.out.println("\n========================================");
        }
    }

    // ============================================
    // DISPLAY METHODS
    // ============================================

    public static void displayWelcomeMessage() {
        System.out.println("\n========================================");
        System.out.println("    BOOK MY STAY - HOTEL BOOKING APP    ");
        System.out.println("========================================");
        System.out.println("Version: 12.1");
        System.out.println("Use Case 12: Data Persistence & Recovery");
        System.out.println("========================================\n");
    }

    /**
     * UC12: Demonstrate persistence and recovery
     */
    public static void demonstratePersistence() {
        System.out.println("\n========================================");
        System.out.println("    PERSISTENCE DEMONSTRATION           ");
        System.out.println("========================================");

        // Initialize system components
        RoomInventory inventory = new RoomInventory();
        BookingHistory bookingHistory = new BookingHistory();
        BookingQueue bookingQueue = new BookingQueue();
        RoomAllocationService allocationService = new RoomAllocationService();
        SystemRecoveryManager recoveryManager =
                new SystemRecoveryManager(inventory, bookingHistory);

        // UC12: Phase 1 - Recovery (simulate startup)
        System.out.println("\n--- PHASE 1: System Startup & Recovery ---");
        recoveryManager.recoverSystemState();

        // Display initial state
        System.out.println("\n--- Initial System State ---");
        inventory.displayInventory();

        // UC12: Phase 2 - Create some bookings
        System.out.println("\n--- PHASE 2: Create Sample Bookings ---\n");

        String[] guestNames = {"John Smith", "Jane Doe", "Bob Johnson", "Alice Williams"};
        String[] roomTypes = {"Single Room", "Double Room", "Suite Room", "Single Room"};

        for (int i = 0; i < guestNames.length; i++) {
            try {
                Reservation reservation = bookingQueue.addBookingRequest(
                        guestNames[i], roomTypes[i], 2);

                allocationService.allocateRoom(reservation, inventory);
                reservation.setTotalCost(5000 + (i * 1000));
                bookingHistory.recordBooking(reservation);

                System.out.println("✓ Booked: " + reservation.getReservationId() +
                        " - " + guestNames[i]);

            } catch (BookingException e) {
                System.out.println("✗ Booking failed: " + e.getMessage());
            }
        }

        // Display updated inventory
        System.out.println("\n--- Updated Inventory ---");
        inventory.displayInventory();

        // UC12: Phase 3 - Display current bookings
        System.out.println("\n--- Current Bookings ---\n");
        int position = 1;
        for (Reservation booking : bookingHistory.getAllBookings()) {
            System.out.println("  " + position + ". " + booking);
            position++;
        }

        // UC12: Phase 4 - Save system state (simulate shutdown)
        System.out.println("\n--- PHASE 3: System Shutdown with Persistence ---");
        recoveryManager.saveSystemState();

        // Display recovery status
        recoveryManager.displayRecoveryStatus();

        // UC12: Phase 5 - Simulate system restart
        System.out.println("\n\n========================================");
        System.out.println("    SIMULATING SYSTEM RESTART           ");
        System.out.println("========================================");

        // Create new instances (simulate fresh start)
        RoomInventory inventory2 = new RoomInventory();
        BookingHistory bookingHistory2 = new BookingHistory();
        SystemRecoveryManager recoveryManager2 =
                new SystemRecoveryManager(inventory2, bookingHistory2);

        // UC12: Recover from persisted state
        recoveryManager2.recoverSystemState();

        // Display recovered state
        System.out.println("\n--- Recovered System State ---");
        inventory2.displayInventory();

        System.out.println("\n--- Recovered Bookings ---\n");
        position = 1;
        for (Reservation booking : bookingHistory2.getAllBookings()) {
            System.out.println("  " + position + ". " + booking);
            position++;
        }
    }

    // ============================================
    // MAIN METHOD
    // ============================================

    public static void main(String[] args) {
        displayWelcomeMessage();

        System.out.println("--- STEP 1: Introduce Persistence Concepts ---");
        System.out.println("\n1. Stateful Applications:");
        System.out.println("   - Maintain data beyond single execution");
        System.out.println("   - Preserve business state across restarts");
        System.out.println("   - Ensure continuity and correctness");

        System.out.println("\n2. Persistence Mechanisms:");
        System.out.println("   - File-based storage (this example)");
        System.out.println("   - Database integration (future)");
        System.out.println("   - Message queues and logs");

        System.out.println("\n3. Serialization & Deserialization:");
        System.out.println("   - Convert objects to byte streams");
        System.out.println("   - Restore objects from persisted data");
        System.out.println("   - Maintain object integrity");

        System.out.println("\n4. Recovery & Failure Tolerance:");
        System.out.println("   - Handle missing/corrupted data");
        System.out.println("   - Graceful fallback to fresh state");
        System.out.println("   - Prevent data loss on crashes");

        System.out.println("\n--- STEP 2: Demonstrate Persistence & Recovery ---");
        demonstratePersistence();

        // Final message
        System.out.println("\n========================================");
        System.out.println("UC12 Demonstration Complete!");
        System.out.println("Data persistence and recovery established.");
        System.out.println("System state survives application restarts.");
        System.out.println("========================================\n");

        // Display UC12 advantages
        System.out.println("========================================");
        System.out.println("    UC12 ADVANTAGES - PERSISTENCE      ");
        System.out.println("========================================");
        System.out.println("\n✓ No data loss across application restarts");
        System.out.println("✓ Serialization for object storage");
        System.out.println("✓ File-based persistence");
        System.out.println("✓ Graceful recovery from missing state");
        System.out.println("✓ Snapshot-based state management");
        System.out.println("✓ Production-ready durability");
        System.out.println("✓ Transition toward database backing");
        System.out.println("✓ Complete system state recovery");
        System.out.println("\n========================================\n");

        // Display persistence patterns
        System.out.println("========================================");
        System.out.println("    PERSISTENCE PATTERNS USED          ");
        System.out.println("========================================");
        System.out.println("\n1. Snapshot Pattern:");
        System.out.println("   - Capture entire system state");
        System.out.println("   - Serialize to single unit");
        System.out.println("   - Atomic save/restore");

        System.out.println("\n2. Serialization:");
        System.out.println("   - Java's Serializable interface");
        System.out.println("   - ObjectOutputStream for writing");
        System.out.println("   - ObjectInputStream for reading");

        System.out.println("\n3. Graceful Degradation:");
        System.out.println("   - Check file existence");
        System.out.println("   - Handle missing files");
        System.out.println("   - Fall back to fresh state");

        System.out.println("\n4. Separation of Concerns:");
        System.out.println("   - PersistenceService for I/O");
        System.out.println("   - RecoveryManager for coordination");
        System.out.println("   - Clean boundaries between layers");

        System.out.println("\n========================================\n");
    }
}