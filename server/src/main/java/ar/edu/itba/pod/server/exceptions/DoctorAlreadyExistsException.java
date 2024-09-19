package ar.edu.itba.pod.server.exceptions;

public class DoctorAlreadyExistsException extends RuntimeException {
    public DoctorAlreadyExistsException(final String message) {
        super("Doctor with name " + message + " already exists");
    }
}
