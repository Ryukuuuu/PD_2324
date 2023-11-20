package database;
import data.ClientData;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DatabaseConnection {
    private String DATABASE_URL;
    private Connection conn;
    private long versionDB;
    public DatabaseConnection(String DATABASE_URL) {
        this.DATABASE_URL = DATABASE_URL;
        versionDB = 1L;
        try {
            conn = DriverManager.getConnection(DATABASE_URL);
        }catch (SQLException sqlE){
            System.out.println("<ERRO> Nao foi possivel estabelecer conexao com a DB!");
        }
    }
    public ClientData getClient(String email, String password) {
        return null;
    }
    public boolean addNewEntryToClients(ClientData clientData) {
        return false;
    }

    public ClientData editUserInfo(ClientData clientData) {
        return null;
    }

    public boolean checkIfCodeExists(long eventCode) {
        return false;
    }
}