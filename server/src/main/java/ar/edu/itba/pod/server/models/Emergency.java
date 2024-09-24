package ar.edu.itba.pod.server.models;

public class Emergency {

    private final int roomNumber;
    private final Doctor doctor;
    private final Patient patient;

    public Emergency(int roomNumber, Doctor doctor, Patient patient) {
        this.roomNumber = roomNumber;
        this.doctor = doctor;
        this.patient = patient;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public Patient getPatient() {
        return patient;
    }
}
