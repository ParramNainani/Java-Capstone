package util;

/**
 * ExceptionHandler - Basic utility for wrapping or logging custom exceptions.
 */
public class ExceptionHandler {

    public static void logError(String context, Exception e) {
        System.err.println("[ERROR] " + context + ": " + e.getMessage());
        // In a real application, integration with Log4j or SLF4J goes here.
    }

    public static void handleValidationException(String message) {
        System.err.println("[VALIDATION FAILED] " + message);
        // Could throw a custom RuntimeException
    }
}
