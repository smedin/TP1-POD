package ar.edu.itba.pod.client.utils;

import ar.edu.itba.pod.grpc.query.RoomData;
import ar.edu.itba.pod.grpc.query.RoomList;

import java.io.FileWriter;
import java.io.IOException;

public class WriterUtils {

    public static void writeQuery1(String filePath, RoomList data) {
        try (FileWriter writer = new FileWriter(filePath)) {

            writer.write("Room,Status,Patient,Doctor\n");

            for (RoomData roomData : data.getRoomDataList()) {
                String line = roomData.getRoomNumber() + "," +
                        (roomData.getFree() ? "Free" : "Occupied") + "," +
                        roomData.getPatient().getName() + (roomData.getPatient().getLevel() == 0 ? "" : " (" + roomData.getPatient().getLevel() + ")") + "," +
                        roomData.getDoctor().getName() + (roomData.getDoctor().getLevel() == 0 ? "" : " (" + roomData.getDoctor().getLevel() + ")\n");
                writer.write(line);
            }

        } catch (IOException e) {
            System.out.println("Error while creating/writing to the file: " + e.getMessage());
        }
    }

}
