package client.startup;

import client.view.Interpreter;
import common.FileTransferService;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class MainStartup {
    public static void main(String[] args) {
        try {
            FileTransferService fts = (FileTransferService) Naming.lookup(FileTransferService.FILETRANSFER_NAME_IN_REGISTRY);
            new Interpreter().start(fts);
        } catch (NotBoundException | MalformedURLException | RemoteException ex) {
            System.out.println("Could not start file transfer client.");
        }
    }
}
