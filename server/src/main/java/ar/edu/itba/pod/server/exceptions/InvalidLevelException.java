package ar.edu.itba.pod.server.exceptions;

public class InvalidLevelException extends RuntimeException {
    public InvalidLevelException(final int level) {
        super(String.format("Level %d is not a valid level", level));
    }
}
