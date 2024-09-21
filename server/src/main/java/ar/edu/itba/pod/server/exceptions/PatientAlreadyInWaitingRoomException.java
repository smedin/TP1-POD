package ar.edu.itba.pod.server.exceptions;

public class PatientAlreadyInWaitingRoomException extends RuntimeException {
    public PatientAlreadyInWaitingRoomException(final String patientName) {
        super("Patient " + patientName + " already exists in waiting room");
    }
}
