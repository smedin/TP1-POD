package ar.edu.itba.pod.server.exceptions;

public class InvalidLevelException extends RuntimeException {
    public InvalidLevelException(final int level) {
        super("Level " + level + " is not a valid level");
    }
}
