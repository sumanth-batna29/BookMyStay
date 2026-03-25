public class BookMyStay {

    // Application metadata constants
    private static final String APP_NAME = "Book My Stay";
    private static final String APP_VERSION = "1.0";
    private static final String APP_DESCRIPTION = "Hotel Booking Management System";

    /**
     * Main method - Entry point of the Hotel Booking Application
     *
     * The JVM invokes this method to start the application execution.
     * This method is declared static so it can be executed without creating
     * an instance of the UseCase1HotelBookingApp class.
     *
     * @param args Command line arguments (not used in this use case)
     */
    public static void main(String[] args) {
        // Welcome message banner
        System.out.println("========================================");
        System.out.println("    WELCOME TO HOTEL BOOKING SYSTEM     ");
        System.out.println("========================================");
        System.out.println();

        // Application information
        System.out.println("Application Name: " + APP_NAME);
        System.out.println("Version: " + APP_VERSION);
        System.out.println("Description: " + APP_DESCRIPTION);
        System.out.println();

        // System status
        System.out.println("========================================");
        System.out.println("Application started successfully!");
        System.out.println("Ready to accept hotel booking requests.");
        System.out.println("========================================");
        System.out.println();

        // Additional information
        System.out.println("Features:");
        System.out.println("✓ Room availability tracking");
        System.out.println("✓ Fair booking request handling");
        System.out.println("✓ Prevention of double-booking");
        System.out.println("✓ Inventory consistency management");
        System.out.println();

        // Footer message
        System.out.println("For more information, visit our system documentation.");
        System.out.println();
        System.out.println("Application ready. Waiting for user input...");
    }
}
