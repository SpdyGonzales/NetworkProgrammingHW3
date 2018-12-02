package server.controller;

import common.FileTransferService;
import common.FilesDTO;
import common.NotificationSystem;
import common.UserAccountDTO;
import org.omg.CORBA.portable.RemarshalException;
import server.integration.FileTransferDAO;
import server.integration.FileTransferException;
import server.model.File;
import server.model.UserAccount;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;

public class Controller extends UnicastRemoteObject implements FileTransferService {
    private FileTransferDAO fileDb;
    private HashMap<String, NotificationSystem> onlineUsers = new HashMap();

    public Controller(String datasource, String dbms) throws RemoteException, FileTransferException {
        super();
        fileDb = new FileTransferDAO(dbms, datasource);
    }

    public synchronized void createAccount(String user, String pass) throws RemoteException {
        try {
            if (fileDb.checkAccount(user) == null) {
                fileDb.createAccount(new UserAccount(user, pass));
            }else{
                throw new RemoteException(user + "' is already an existing account");
            }
        } catch (Exception e) {
            throw new RemoteException("Can not create new user. Try again later");
        }
    }

    public synchronized void logoutAccount(String user) throws RemoteException {
        onlineUsers.remove(user);
    }

    public synchronized void addFile(String fileName, String owner, int size, String readPerm, String writePerm) throws RemoteException{
        boolean readBool = false;
        boolean writeBool = false;
        if(readPerm.toLowerCase().equals("read")){
            readBool = true;
        }
        if(writePerm.toLowerCase().equals("write")){
            writeBool = true;
        }
        try{
            if(fileDb.fetchFile(fileName) == null){
                fileDb.addFile(fileName, owner, size, readBool,writeBool);
            }else{
                throw new RemoteException(fileName + "' is already an existing file");
            }
        }catch (Exception e) {
            throw new RemoteException("Can not add new file. Try again later");
        }
    }

    public synchronized UserAccountDTO loginAccount(String user, String pass, NotificationSystem notifyOutput) throws RemoteException {
        UserAccount userAcc = fileDb.checkAccount(user);
        if (userAcc != null && pass.equals(userAcc.getPassword())) {
            onlineUsers.put(user, notifyOutput);
            return userAcc;
        } else {
            throw new RemoteException("Password or username incorrect!");
        }
    }

    public synchronized List<? extends File> listFiles() throws RemoteException {
        try {
            return fileDb.findAllFiles();
        } catch (Exception e) {
            throw new RemoteException("Unable to list files.", e);
        }
    }

    public synchronized File fetchFile(String fileName, String user) throws RemoteException {
        File fetchedFile;
        try {
            fetchedFile = fileDb.fetchFile(fileName);
        } catch (FileTransferException e) {
            throw new RemoteException("Unable to fetch file", e);
        }
        if(fetchedFile.getOwner() == user || fetchedFile.getReadPermission()){
            return fetchedFile;
        }else{
            throw new RemoteException("Permission Denied to read file");
        }


    }
    public void notifyOwner(FilesDTO file, String user, String change) throws RemoteException {
        NotificationSystem owner = onlineUsers.get(file.getOwner());
        if(owner != null){
            owner.notify(user + " " + change + " your file " + file.getFileName() + " from the File Transfer Service");
        }
    }

    @Override
    public void removeFile(String fileName, String user) throws RemoteException {
        File fetchedFile;
        try {
            fetchedFile = fileDb.fetchFile(fileName);
        } catch (FileTransferException e) {
            throw new RemoteException("Unable to fetch file", e);
        }
        if(fetchedFile.getOwner() == user || fetchedFile.getWritePermission()){
            try {
                fileDb.removeFile(fileName);
                notifyOwner(fetchedFile, user, "removed");
            } catch (FileTransferException e) {
                throw new RemoteException("Unable to remove file");
            }
        }else{
            throw new RemoteException("Permission Denied to delete file");
        }
    }
}
