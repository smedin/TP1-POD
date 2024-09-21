package ar.edu.itba.pod.server.exceptions;

public class DoctorNotInRoomException extends RuntimeException {
    public DoctorNotInRoomException(final String doctorName, final int roomId) {
        super(String.format("Doctor %s is not in room %d", doctorName, roomId));
    }
}
