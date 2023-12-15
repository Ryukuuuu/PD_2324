package server;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GetRemoteDatabaseService extends Remote {
    byte[] getDatabaseCopy(long offset) throws RemoteException, IOException;
    void addObserver(GetRemoteDatabaseObserver observer) throws RemoteException;
    void removeObserver(GetRemoteDatabaseObserver observer) throws RemoteException;
}