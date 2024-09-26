package ar.edu.itba.pod.server.models;

import ar.edu.itba.pod.server.exceptions.InvalidLevelException;

public class Patient implements Comparable<Patient> {

    private final String name;
    private int emergencyLevel;

    public Patient(String name, int emergencyLevel) {
        this.name = name;
        if( emergencyLevel < 1 || emergencyLevel > 5) {
            throw new InvalidLevelException(emergencyLevel);
        }
        this.emergencyLevel = emergencyLevel;
    }

    public String getName() {
        return name;
    }

    public int getEmergencyLevel() {
        return emergencyLevel;
    }

    public void setEmergencyLevel(int emergencyLevel) {
        if( emergencyLevel < 1 || emergencyLevel > 5) {
            throw new InvalidLevelException(emergencyLevel);
        }
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
        return Integer.compare(o.emergencyLevel, this.emergencyLevel);
    }

    @Override
    public String toString() {
        return "Patient{" +
                "name='" + name + '\'' +
                ", emergencyLevel=" + emergencyLevel +
                '}';
    }
}
