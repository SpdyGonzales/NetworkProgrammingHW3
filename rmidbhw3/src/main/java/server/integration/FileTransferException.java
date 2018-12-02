package server.integration;

public class FileTransferException extends Exception {

    public FileTransferException(String reason) {
        super(reason);
    }
    public FileTransferException(String reason, Throwable rootCause) {
        super(reason, rootCause);
    }
}
