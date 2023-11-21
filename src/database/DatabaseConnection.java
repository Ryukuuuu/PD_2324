package database;
import data.ClientData;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private String DATABASE_URL;
    private Connection conn;
    private long versionDB;
    public DatabaseConnection(String DATABASE_URL) {
        this.DATABASE_URL = DATABASE_URL;
        versionDB = 0L;
        try {
            conn = DriverManager.getConnection(DATABASE_URL);
        }catch (SQLException sqlE){
            System.out.println("<ERRO> Nao foi possivel estabelecer conexao com a DB!");
        }

        createTables();
    }

    private void createTables() {
        Statement statement;
        try {
            statement = conn.createStatement();
            String createUsersTableStatement = "CREATE TABLE IF NOT EXISTS Users (\n" +
                                        "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                                        "name TEXT NOT NULL,\n" +
                                        "clientID INTEGER UNIQUE NOT NULL,\n" +
                                        "email TEXT UNIQUE NOT NULL,\n" +
                                        "password TEXT NOT NULL,\n" +
                                        "administrator BOOLEAN NOT NULL DEFAULT(0)\n" +
                                        ");";
            statement.execute(createUsersTableStatement);
            String createEventsTableStatement = "CREATE TABLE IF NOT EXISTS Events (\n" +
                                        "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                                        "name TEXT NOT NULL,\n" +
                                        "local TEXT NOT NULL,\n" +
                                        "date DATE NOT NULL,\n" +
                                        "activeCode INTEGER UNIQUE NOT NULL,\n" +
                                        "codeValidityEnding TIME NOT NULL,\n" +
                                        "startingTime TIME NOT NULL,\n" +
                                        "endingTime TIME NOT NULL\n" +
                                        ");";
            statement.execute(createEventsTableStatement);
            String createUsersEventsTableStatement = "CREATE TABLE IF NOT EXISTS UsersEvents (\n" +
                                        "userID INTEGER NOT NULL,\n" +
                                        "eventID INTEGER NOT NULL,\n" +
                                        "PRIMARY KEY (user_id, event_id),\n" +
                                        "FOREIGN KEY (user_id) REFERENCES Users(id),\n" +
                                        "FOREIGN KEY (event_id) REFERENCES Events(id)\n" +
                                        ");";
            statement.execute(createUsersEventsTableStatement);
        } catch (SQLException e) {
            System.out.println("Erro em algum statement: ");
            e.printStackTrace();
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