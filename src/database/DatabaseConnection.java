package database;
import data.ClientData;
import data.Event;

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
            String createClientsTableStatement = "CREATE TABLE IF NOT EXISTS Clients (\n" +
                                                    "name TEXT NOT NULL,\n" +
                                                    "clientID INTEGER NOT NULL,\n" +
                                                    "email TEXT PRIMARY KEY NOT NULL,\n" +
                                                    "password TEXT NOT NULL,\n" +
                                                    "admin BOOLEAN NOT NULL DEFAULT(0)\n" +
                                                    ");";
            statement.execute(createClientsTableStatement);
            String createEventsTableStatement = "CREATE TABLE IF NOT EXISTS Events (\n" +
                                                    "id INTEGER NOT NULL PRIMARY KEY,\n" +
                                                    "name TEXT NOT NULL,\n" +
                                                    "local TEXT NOT NULL,\n" +
                                                    "date DATE NOT NULL,\n" +
                                                    "activeCode INTEGER UNIQUE NOT NULL,\n" +
                                                    "codeValidityEnding TIME NOT NULL,\n" +
                                                    "startingTime TIME NOT NULL,\n" +
                                                    "endingTime TIME NOT NULL\n" +
                                                    ");";
            statement.execute(createEventsTableStatement);
            String createClientsEventsTableStatement = "CREATE TABLE IF NOT EXISTS ClientsEvents (\n" +
                                                    "clientEmail TEXT NOT NULL,\n" +
                                                    "eventID INTEGER NOT NULL,\n" +
                                                    "PRIMARY KEY (clientEmail, event_id),\n" +
                                                    "FOREIGN KEY (clientEmail) REFERENCES Clients(email),\n" +
                                                    "FOREIGN KEY (event_id) REFERENCES Events(id)\n" +
                                                    ");";
            statement.execute(createClientsEventsTableStatement);
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
            String updateVersionStatement = "UPDATE DatabaseVersion SET version=" + ++versionDB + ";";
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
            String selectClient = "SELECT *\n" +
                                "FROM Clients\n" +
                                "WHERE email='" + email + "' AND password='" + password + "';";
            result = statement.executeQuery(selectClient);
            if (result.next()){
                return new ClientData(
                        result.getString("name"),
                        result.getLong("clientID"),
                        result.getString("email"),
                        result.getString("password"),
                        result.getBoolean("admin"));
            }
        } catch (SQLException e) {
            System.out.println("Erro no statement de obtencao de Clients: ");
            e.printStackTrace();
        }

        return null;
    }
    public boolean addNewEntryToClients(ClientData clientData) {
        Statement statement;
        int result;
        synchronized (conn){
            try {
                statement = conn.createStatement();
                String insertClient = "INSERT INTO Clients (name,clientID,email,password,admin) VALUES\n" +
                        "('" + clientData.getName() + "'," + clientData.getId() + ",'" + clientData.getEmail() +
                        "','" + clientData.getPassword() + "'," + clientData.isAdmin() + ");";
                result = statement.executeUpdate(insertClient);

                if (result != 0) {
                    updateDBVersion();
                    return true;
                }
            } catch (SQLException e) {
                System.out.println("Erro no statement de insercao de um novo Cliente: ");
                e.printStackTrace();
            }
        }

        return false;
    }
    public ClientData editClientInfo(ClientData clientData) {
        Statement statement;
        int result;
        synchronized (conn){
            try {
                statement = conn.createStatement();
                String updateClientStatement = "UPDATE Clients SET name='" + clientData.getName() + "', clientID=" +
                        clientData.getId() + ", email='" + clientData.getEmail() + "', password='" +
                        clientData.getPassword() + "', admin=" + clientData.isAdmin() + "\n" +
                        "WHERE clientID=" + clientData.getId() + ";";
                result = statement.executeUpdate(updateClientStatement);

                if (result != 0){
                    updateDBVersion();
                    return clientData;
                }
            } catch (SQLException e) {
                System.out.println("Erro no statement de atualizacao de um Cliente: ");
                e.printStackTrace();
            }
        }

        return null;
    }

    public Event getEvent(int eventid){
        Statement statement;
        ResultSet result;
        try {
            statement = conn.createStatement();
            String selectEvent = "SELECT *\n" +
                                  "FROM Events\n" +
                                  "WHERE id=" + eventid + ";";
            result = statement.executeQuery(selectEvent);
            if (result.next()){
                return new Event(
                        result.getInt("id"),
                        result.getString("name"),
                        result.getString("local"),
                        result.getString("date"),
                        result.getLong("activeCode"),
                        result.getString("codeValidityEnding"),
                        result.getString("startingTime"),
                        result.getString("endingTime"));
            }
        } catch (SQLException e) {
            System.out.println("Erro no statement de obtencao de Events: ");
            e.printStackTrace();
        }

        return null;
    }

    public boolean addNewEntryToEvent(Event event){
        Statement statement;
        int result;
        synchronized (conn){
            try {
                statement = conn.createStatement();
                String insertEvent = "INSERT INTO Events (id,name,local,date,activeCode,codeValidityEnding,startingTime,endingTime) VALUES\n" +
                        "(" + event.getId() + ",'" + event.getName() + "','" + event.getLocal() + "','" + event.getDate() +
                        "'," + event.getActiveCode() + ",'" + event.getCodeValidityEnding() + "','" + event.getStartingTime() +
                        "','" + event.getEndingTime() + "');";
                result = statement.executeUpdate(insertEvent);

                if (result != 0) {
                    updateDBVersion();
                    return true;
                }
            } catch (SQLException e) {
                System.out.println("Erro no statement de insercao de um novo Evento: ");
                e.printStackTrace();
            }
        }
        return false;
    }

    public Event editEventInfo(Event event){
        Statement statement;
        int result;
        synchronized (conn){
            try {
                statement = conn.createStatement();
                String updateEventStatement = "UPDATE Events SET id=" + event.getId() + ", name='" + event.getName() +
                        "', local='" + event.getLocal() + "', date='" + event.getDate() + "', activeCode=" +
                        event.getActiveCode() + ", codeValidityEnding='" + event.getCodeValidityEnding() + "', startingTime='" +
                        event.getStartingTime() + "', endingTime='" + event.getEndingTime() + "'\n" +
                        "WHERE id=" + event.getId() + ";";
                result = statement.executeUpdate(updateEventStatement);

                if (result != 0){
                    updateDBVersion();
                    return event;
                }
            } catch (SQLException e) {
                System.out.println("Erro no statement de atualizacao de um Evento: ");
                e.printStackTrace();
            }
        }
        return null;
    }

    public boolean checkIfCodeExists(long eventCode, String email) {
        Statement selectStatement, insertStatement;
        ResultSet resultSet;
        int result, idFoundEvent;
        try {
            selectStatement = conn.createStatement();
            String selectEventsStatement = "SELECT id, activeCode" +
                                           "FROM Events" +
                                           "WHERE activeCode=" + eventCode + ";";
            resultSet = selectStatement.executeQuery(selectEventsStatement);

            if(resultSet.next()){
                idFoundEvent = resultSet.getInt("id");
                synchronized (conn){
                    insertStatement = conn.createStatement();
                    String insertClientEventStatement = "INSERT INTO ClientsEvents (clientEmail, eventID) VALUES " +
                            "('" + email + "'," + idFoundEvent + ");";
                    result = insertStatement.executeUpdate(insertClientEventStatement);

                    if(result != 0){
                        updateDBVersion();
                        return true;
                    }
                }
            }
        }catch (SQLException e){
            System.out.println("Erro ao ler informacao sobre eventos ou insercao na tabela relacional!");
        }

        return false;
    }
}