package wisoft.tddstart.commerce;

public class UserPropertyValidator {
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
    public static final String USERNAME_REGEX = "[a-zA-Z0-9_-]{3,}$";

    public static boolean isEmailValid(final String email) {
        return email != null && email.matches(EMAIL_REGEX);
    }

    public static boolean isUserNameValid(final String username) {
        return username != null && username.matches(USERNAME_REGEX);
    }

    public static boolean isPasswordValid(final String password) {
        return password != null && password.length() >= 8;
    }
}
