package ar.edu.itba.pod.server.utils;

import ar.edu.itba.pod.server.exceptions.*;
import com.google.rpc.Code;
import io.grpc.*;
import io.grpc.protobuf.StatusProto;

import java.util.HashMap;
import java.util.Map;

import static com.google.rpc.Code.*;


public class GlobalExceptionHandlerInterceptor implements ServerInterceptor {

    @Override
    public <T, R> ServerCall.Listener<T> interceptCall(
            ServerCall<T, R> serverCall, Metadata headers, ServerCallHandler<T, R> serverCallHandler) {
        ServerCall.Listener<T> delegate = serverCallHandler.startCall(serverCall, headers);
        return new ExceptionHandler<>(delegate, serverCall, headers);
    }

    private static class ExceptionHandler<T, R> extends ForwardingServerCallListener.SimpleForwardingServerCallListener<T> {

        private final ServerCall<T, R> delegate;
        private final Metadata headers;
        private final Map<Class<? extends Throwable>, Code> errorCodesByException;

        ExceptionHandler(ServerCall.Listener<T> listener, ServerCall<T, R> serverCall, Metadata headers) {
            super(listener);
            this.delegate = serverCall;
            this.headers = headers;
            this.errorCodesByException = createExceptionsMap();
        }

        private Map<Class<? extends Throwable>, Code> createExceptionsMap() {
            Map<Class<? extends Throwable>, Code> map = new HashMap<>();
            map.put(PatientAlreadyInWaitingRoomException.class, ALREADY_EXISTS);
            map.put(DoctorAlreadyExistsException.class, ALREADY_EXISTS);
            map.put(DoctorIsAttendingException.class, FAILED_PRECONDITION);
            map.put(InvalidLevelException.class, INVALID_ARGUMENT);
            map.put(DoctorNotFoundException.class, NOT_FOUND);
            map.put(RoomNotFoundException.class, NOT_FOUND);
            map.put(UnableToStartEmergencyException.class, FAILED_PRECONDITION);
            map.put(RoomNotFreeException.class, FAILED_PRECONDITION);
            map.put(RoomFreeException.class, FAILED_PRECONDITION);
            map.put(PatientNotFoundException.class, NOT_FOUND);
            map.put(DoctorNotInRoomException.class, FAILED_PRECONDITION);
            return map;
        }

        @Override
        public void onHalfClose() {
            try {
                super.onHalfClose();
            } catch (RuntimeException ex) {
                handleException(ex, delegate, headers);
            }
        }


        private void handleException(RuntimeException exception, ServerCall<T, R> serverCall, Metadata headers) {
            Throwable error = exception;
            if (!errorCodesByException.containsKey(error.getClass())) {
                // Si la excepción vino "wrappeada" entonces necesitamos preguntar por la causa.
                error = error.getCause();
                if (error == null || !errorCodesByException.containsKey(error.getClass())) {
                    // Una excepción NO esperada.
                    serverCall.close(Status.UNKNOWN, headers);
                    return;
                }
            }
            // Una excepción esperada.
            com.google.rpc.Status rpcStatus = com.google.rpc.Status.newBuilder()
                    .setCode(errorCodesByException.get(error.getClass()).getNumber())
                    .setMessage(error.getMessage())
                    .build();
            StatusRuntimeException statusRuntimeException = StatusProto.toStatusRuntimeException(rpcStatus);
            Status newStatus = Status.fromThrowable(statusRuntimeException);
            serverCall.close(newStatus, headers);
        }
    }

}