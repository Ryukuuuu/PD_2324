package server;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GetRemoteDatabaseObserver extends Remote {

    void writeFileChunk(byte [] fileChunk, int nbytes) throws RemoteException, IOException;

    void notifyDatabaseUpdate() throws RemoteException;

}