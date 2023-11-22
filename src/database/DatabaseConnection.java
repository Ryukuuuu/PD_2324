package database;
import data.ClientData;
import data.Event;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnection {
    private Connection conn;
    private long versionDB;
    public DatabaseConnection(String DATABASE_URL) {
        versionDB = 0L;
        try {
            conn = DriverManager.getConnection(DATABASE_URL);
        }catch (SQLException sqlE){
            System.out.println("<ERRO> Nao foi possivel estabelecer conexao com a DB!");
            sqlE.printStackTrace();
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
                                                    "name TEXT PRIMARY KEY NOT NULL,\n" +
                                                    "local TEXT NOT NULL,\n" +
                                                    "date DATE NOT NULL,\n" +
                                                    "activeCode INTEGER UNIQUE,\n" +
                                                    "codeValidityEnding TIME,\n" +
                                                    "startingTime TIME NOT NULL,\n" +
                                                    "endingTime TIME NOT NULL\n" +
                                                    ");";
            statement.execute(createEventsTableStatement);
            String createClientsEventsTableStatement = "CREATE TABLE IF NOT EXISTS ClientsEvents (\n" +
                                                    "clientEmail TEXT NOT NULL,\n" +
                                                    "eventName TEXT NOT NULL,\n" +
                                                    "PRIMARY KEY (clientEmail, eventName),\n" +
                                                    "FOREIGN KEY (clientEmail) REFERENCES Clients(email),\n" +
                                                    "FOREIGN KEY (eventName) REFERENCES Events(name)\n" +
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
                //e.printStackTrace();
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
                        clientData.getId() + ", password='" +
                        clientData.getPassword() + "', admin=" + clientData.isAdmin() + "\n" +
                        "WHERE email='" + clientData.getEmail() + "';";
                result = statement.executeUpdate(updateClientStatement);
                if (result != 0){
                    updateDBVersion();
                    System.out.println("Client data sent: "+ clientData);
                    return clientData;
                }
            } catch (SQLException e) {
                System.out.println("Erro no statement de atualizacao de um Cliente: ");
                e.printStackTrace();
            }
        }

        return null;
    }

    public Event getEvent(String eventName){
        Statement statement;
        ResultSet result;
        try {
            statement = conn.createStatement();
            String selectEvent = "SELECT *\n" +
                                  "FROM Events\n" +
                                  "WHERE name=" + eventName + ";";
            result = statement.executeQuery(selectEvent);
            if (result.next()){
                return new Event(
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
                String insertEvent = "INSERT INTO Events (name,local,date,startingTime,endingTime) VALUES\n" +
                        "('" + event.getName() + "','" + event.getLocal() + "','" + event.getDate() +
                        "','" + event.getStartingTime() + "','" + event.getEndingTime() + "');";
                result = statement.executeUpdate(insertEvent);

                if (result != 0) {
                    updateDBVersion();
                    return true;
                }
            } catch (SQLException e) {
                System.out.println("Erro no statement de insercao de um novo Evento: ");
                System.out.println(event);
                e.printStackTrace();
            }
        }
        return false;
    }
    public List<String> checkPresences(Event event){
        Statement statement;
        ResultSet resultSet;
        List<String> list = new ArrayList<>();
        synchronized (conn){
            try {
                statement = conn.createStatement();
                String selectClientsEventsStatement = "SELECT clientEmail\n" +
                                                      "FROM ClientsEvents\n" +
                                                      "WHERE eventName='" + event.getName() + "';";
                resultSet = statement.executeQuery(selectClientsEventsStatement);

                while (resultSet.next()){
                    list.add(resultSet.getString("clientEmail"));
                }
            } catch (SQLException e) {
                System.out.println("Erro a obter todas as presenças para um determinado evento");
                e.printStackTrace();
            }
        }

        return list;
    }

    public Event editActiveCode (String name, long code, String codeValidityEnding){
        Statement statement;
        int result;
        synchronized (conn){
            try {
                statement = conn.createStatement();
                String updateNewCodeStatement = "UPDATE Events SET activeCode=" + code + ", codeValidityEnding='" +
                                                codeValidityEnding + "'\n" +
                                                "WHERE name='" + name + "';";
                result = statement.executeUpdate(updateNewCodeStatement);
                if (result != 0){
                    updateDBVersion();
                    return getEvent(name);
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
    public Event editEventInfo(Event event){
        Statement updateStatement;
        int result;
        synchronized (conn){
            List<String> list = checkPresences(event);
            if(!list.isEmpty())
                return null;
            try {
                updateStatement = conn.createStatement();
                String updateEventStatement = "UPDATE Events SET local='" + event.getLocal() +
                        "', date='" + event.getDate() + "', startingTime='" + event.getStartingTime() +
                        "', endingTime='" + event.getEndingTime() + "'\n" +
                        "WHERE name=" + event.getName() + ";";
                result = updateStatement.executeUpdate(updateEventStatement);

                if (result != 0){
                    updateDBVersion();
                    return event;
                }
            } catch (SQLException e) {
                System.out.println("Erro no statement de obtencao da relacao ou de atualizacao de um Evento: ");
                e.printStackTrace();
            }
        }
        return null;
    }

    // só verifico o código, se o email não estiver relacionado com eventos que estejam a decorrer na mesma altura
    // que o evento ao qual possa corresponder o código digitado

    /*
    * 1) Select para ver se o código bate com algum evento (desde que esteja entre o começo e a hora validade)
    * 2) Select para ver presenças registadas do email da pessoa que introduziu código
    * 3.1) Se não houver, tudo ok e Insere
    * 3.2) Se houver, verifica se todos os eventos já terminaram
    * 4.1) Se já terminaram, tudo ok, Insere
    * 4.2) Senão, não insere
    * */
    public boolean checkIfCodeExists(long eventCode, String email) {
        Statement selectStatement, insertStatement;
        ResultSet resultSet;
        int result, idFoundEvent;
        try {
            selectStatement = conn.createStatement();
            String selectEventsStatement = "SELECT name, activeCode" +
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