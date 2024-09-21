package ar.edu.itba.pod.server.models;

public class Room {

    private final int id;
    private boolean free;
    private String doctorName;
    private String patientName;

    public Room(int id) {
        this.id = id;
        this.free = true;
        this.doctorName = "";
        this.patientName = "";
    }

    public int getId() {
        return id;
    }

    public boolean isFree() {
        return free;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setFree(boolean free) {
        this.free = free;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }
}
