package ar.edu.itba.pod.server.exceptions;

public class DoctorAlreadyRegisteredForNotificationsException extends RuntimeException {
    public DoctorAlreadyRegisteredForNotificationsException(final String doctorName) {
        super(String.format("Doctor %s already registered for emergency notifications", doctorName));
    }
}
