package ar.edu.itba.pod.server.exceptions;

public class NoPatientsInWaitingRoomException extends RuntimeException {
    public NoPatientsInWaitingRoomException() {
        super("There are no patients in the waiting room.");
    }
}
