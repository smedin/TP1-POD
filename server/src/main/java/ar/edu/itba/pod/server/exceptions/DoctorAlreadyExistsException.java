package ar.edu.itba.pod.server.exceptions;

public class DoctorAlreadyExistsException extends RuntimeException {
    public DoctorAlreadyExistsException(final String doctorName) {
        super(String.format("Doctor %s already exists", doctorName));
    }
}
