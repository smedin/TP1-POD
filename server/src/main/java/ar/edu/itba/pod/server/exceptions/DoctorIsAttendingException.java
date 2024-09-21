package ar.edu.itba.pod.server.exceptions;

public class DoctorIsAttendingException extends RuntimeException {
    public DoctorIsAttendingException(final String doctorName) {
        super("Doctor " + doctorName + " is attending, cannot change availability right now");
    }
}
