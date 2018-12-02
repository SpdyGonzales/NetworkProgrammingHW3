package common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NotificationSystem extends Remote {

    void notify(String notification) throws RemoteException;
}
