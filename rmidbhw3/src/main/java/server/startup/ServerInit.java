package server.startup;

import common.FileTransferService;
import server.controller.Controller;
import server.integration.FileTransferException;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerInit {
    private static final String USAGE = "java fileTransfer.Server" + "";
    private String datasource = "filetransferservice";
    private String dbms = "mysql";
    private String fileTransferName = FileTransferService.FILETRANSFER_NAME_IN_REGISTRY;

    public static void main(String[] args) throws FileTransferException {
        try {
            ServerInit server = new ServerInit();
            server.startRMIServant();
        }catch(RemoteException | MalformedURLException e) {
            System.out.println("Server startup failed");
        }
    }
    private void startRMIServant() throws RemoteException, MalformedURLException, FileTransferException {
        try {
            LocateRegistry.getRegistry().list();
        } catch (RemoteException noRegistryRunning) {
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        }
        Controller contr = new Controller(datasource, dbms);
        Naming.rebind(fileTransferName, contr);
    }
}
