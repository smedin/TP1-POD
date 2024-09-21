package ar.edu.itba.pod.server.models;

public class Patient implements Comparable<Patient> {

    private final String name;
    private int emergencyLevel;

    public Patient(String name, int emergencyLevel) {
        this.name = name;
        this.emergencyLevel = emergencyLevel;
    }

    public String getName() {
        return name;
    }

    public int getEmergencyLevel() {
        return emergencyLevel;
    }

    public void setEmergencyLevel(int emergencyLevel) {
        this.emergencyLevel = emergencyLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Patient patient = (Patient) o;

        if (emergencyLevel != patient.emergencyLevel) return false;
        return name.equals(patient.name);
    }

    @Override
    public int compareTo(Patient o) {
        int compare = Integer.compare(o.emergencyLevel, this.emergencyLevel);
        if (compare == 0) {
            return this.name.compareTo(o.name);
        }
        return compare;
    }
}
