package ar.edu.itba.pod.server.exceptions;

public class DoctorAlreadyExistsException extends RuntimeException {
    public DoctorAlreadyExistsException(final String doctorName) {
        super("Doctor " + doctorName + " already exists");
    }
}
