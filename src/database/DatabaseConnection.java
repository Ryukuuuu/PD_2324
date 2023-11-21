package database;
import data.ClientData;

import java.sql.*;

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
        versionDB = getDBVersion();
        if(versionDB == 0){
            Statement statement;
            try {
                statement = conn.createStatement();
                String insertFirstVersionStatement = "INSERT INTO DatabaseVersion (version) VALUES (0);";
                statement.executeUpdate(insertFirstVersionStatement);
            } catch (SQLException e) {
                System.out.println("Erro na insercao da versao a 0: ");
                e.printStackTrace();
            }
        }

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
            String createDatabaseVersionStatement = "CREATE TABLE IF NOT EXISTS DatabaseVersion (\n" +
                                                    "version INTEGER NOT NULL\n" +
                                                    ");";
            statement.execute(createDatabaseVersionStatement);
        } catch (SQLException e) {
            System.out.println("Erro em algum statement de criacao de tabelas: ");
            e.printStackTrace();
        }
    }

    public long getDBVersion(){
        Statement statement;
        try {
            statement = conn.createStatement();
            String selectVersionStatement = "SELECT version FROM DatabaseVersion";
            ResultSet result = statement.executeQuery(selectVersionStatement);

            if (result.next())
                return result.getLong("version");

        } catch (SQLException e) {
            System.out.println("Erro no statement de obtencao da versao da base de dados: ");
            e.printStackTrace();
        }
        return 0L;
    }

    private void updateDBVersion(){
        Statement statement;
        try {
            statement = conn.createStatement();
            String updateVersionStatement = "UPDATE DatabaseVersion SET version = " + ++versionDB + ";";
            statement.executeUpdate(updateVersionStatement);
        } catch (SQLException e) {
            System.out.println("Erro na atualizacao da versao da base dados " + (versionDB - 1) + " » " + versionDB);
            e.printStackTrace();
        }
    }

    public ClientData getClient(String email, String password) {
        Statement statement;
        ResultSet result;
        try {
            statement = conn.createStatement();
            String selectUser = "SELECT *\n" +
                                "FROM Users\n" +
                                "WHERE email='" + email + "' AND password='" + password + "'\n";
            result = statement.executeQuery(selectUser);
            if (result.next()){
                return new ClientData(
                        result.getString("name"),
                        result.getLong("clientID"),
                        result.getString("email"),
                        result.getString("password"),
                        result.getBoolean("administrator"));
            }
        } catch (SQLException e) {
            System.out.println("Erro em algum statement de criacao de tabelas: ");
            e.printStackTrace();
        }

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