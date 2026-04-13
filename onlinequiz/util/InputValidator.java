package util;

/**
 * InputValidator - Utility for validating user input.
 */
public class InputValidator {
    public static boolean isValidEmail(String email) {
        // Implement email validation
        return email != null && email.contains("@");
    }
}
