package ar.edu.itba.pod.server.models;

public enum Availability {
    AVAILABLE("available"),
    UNAVAILABLE("unavailable"),
    ATTENDING("attending");

    private final String state;

    // Constructor to associate each enum value with a string
    Availability(String state) {
        this.state = state;
    }

    // Getter method to retrieve the string value of the enum
    public String getState() {
        return state.substring(0, 1).toUpperCase() + state.substring(1);
    }

    // Method to compare with a string
    public boolean equalsString(String otherState) {
        return this.state.equalsIgnoreCase(otherState);
    }

    // Override toString for easy printing
    @Override
    public String toString() {
        return getState();
    }
}
