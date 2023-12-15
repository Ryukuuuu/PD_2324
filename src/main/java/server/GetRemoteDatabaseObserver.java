package server;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GetRemoteDatabaseObserver extends Remote {
    void notifyDatabaseUpdate() throws RemoteException;
}