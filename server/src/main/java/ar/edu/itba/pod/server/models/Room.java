package ar.edu.itba.pod.server.models;

public class Room {

    private final int id;
    private String state; // "free" or "occupied"
    private String doctorName;
    private String patientName;

    public Room(int id) {
        this.id = id;
        this.state = "free";
        this.doctorName = "";
        this.patientName = "";
    }

    public int getName() {
        return id;
    }

    public String getState() {
        return state;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public void free() {
        this.state = "free";
        this.doctorName = "";
        this.patientName = "";
    }

    public void occupy(String doctorName, String patientName) {
        this.state = "occupied";
        this.doctorName = doctorName;
        this.patientName = patientName;
    }

    public boolean isFree() {
        return state.equals("free");
    }

    public boolean isOccupied() {
        return state.equals("occupied");
    }

}
