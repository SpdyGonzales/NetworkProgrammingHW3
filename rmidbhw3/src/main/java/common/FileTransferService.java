package common;

import client.view.OutputHandler;
import server.model.UserAccount;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface FileTransferService extends Remote {
    public static final String FILETRANSFER_NAME_IN_REGISTRY = "filetransferservice";
    void createAccount(String user, String pass) throws RemoteException;
    UserAccountDTO loginAccount(String user, String pass, NotificationSystem notifyOutput) throws RemoteException;
    void logoutAccount(String user) throws RemoteException;
    List<? extends FilesDTO> listFiles() throws RemoteException;
    FilesDTO fetchFile(String fileName, String user) throws RemoteException;
    void removeFile(String userFile, String user) throws RemoteException;
    void addFile(String fileName, String owner, int size, String readPerm, String writePerm) throws RemoteException;

}
