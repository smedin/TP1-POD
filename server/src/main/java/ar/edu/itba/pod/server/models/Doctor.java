package ar.edu.itba.pod.server.models;

import java.util.Objects;

public class Doctor implements Comparable<Doctor> {

    private final String name;
    private final int maxLevel;
    private Availability availability;

    public Doctor(String name, int maxLevel) {
        this.name = name;
        this.maxLevel = maxLevel;
        this.availability = Availability.UNAVAILABLE;
    }

    public String getName() {
        return name;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public Availability getAvailability() {
        return availability;
    }

    public void setAvailability(Availability availability) {
        this.availability = availability;
    }

    public boolean isAvailable() {
        return availability == Availability.AVAILABLE;
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
        return Objects.hash(name);
    }

    @Override
    public int compareTo(Doctor o) {
        int compare = Integer.compare(this.maxLevel, o.maxLevel);
        if (compare == 0) {
            return this.name.compareTo(o.name);
        }
        return compare;
    }
}
