package ar.edu.itba.pod.server.exceptions;

public class NoRoomsRegisteredException extends Exception {
    public NoRoomsRegisteredException() {
        super("No rooms have been registered in the emergency system.");
    }
}
