package ar.edu.itba.pod.server;

import ar.edu.itba.pod.server.models.NotificationManager;
import ar.edu.itba.pod.server.servants.*;
import ar.edu.itba.pod.server.utils.GlobalExceptionHandlerInterceptor;
import ar.edu.itba.pod.server.models.Hospital;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) throws InterruptedException, IOException {
        logger.info(" Server Starting ...");

        int port = 50051;
        Hospital hospital = new Hospital();
        NotificationManager notificationManager = new NotificationManager();
        io.grpc.Server server = ServerBuilder.forPort(port)
                .addService(new EmergencyServant(hospital, notificationManager))
                .addService(new AdminServant(hospital, notificationManager))
                .addService(new WaitingRoomServant(hospital))
                .addService(new NotificationServant(hospital, notificationManager))
                .addService(new QueryServant(hospital))
                .intercept(new GlobalExceptionHandlerInterceptor())
                .build();
        server.start();
        logger.info("Server started, listening on " + port);
        server.awaitTermination();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down gRPC server since JVM is shutting down");
            server.shutdown();
            logger.info("Server shut down");
        }));
    }
}
