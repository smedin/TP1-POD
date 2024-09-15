package ar.edu.itba.pod.server.models;

import java.util.Objects;

public class Doctor {

    private final String name;
    private final int maxLevel;
    private String availability;

    public Doctor(String name, int maxLevel) {
        this.name = name;
        this.maxLevel = maxLevel;
        this.availability = "unavailable";
    }

    public String getName() {
        return name;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Doctor doctor = (Doctor) o;
        return name.equals(doctor.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name); // Usa solo el nombre para el hash
    }

}
