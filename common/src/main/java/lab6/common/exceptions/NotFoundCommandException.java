package lab6.common.exceptions;

public class NotFoundCommandException extends RuntimeException {
    public NotFoundCommandException(String commandName) {
        super("Command " + commandName + " is not found.");
    }
}
