package database;
import data.ClientData;
import data.Event;

import java.sql.*;
import java.util.ArrayList;

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
            addNewEntryToClients(new ClientData("admin", 0L, "admin", "admin", true));
            try{
                statement = conn.createStatement();
                statement.executeUpdate("UPDATE DatabaseVersion SET version=0;");
            }catch (SQLException e){
                System.out.println("Erro na alteracao da versao de 1 » 0");
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
                                                    "clientID BIGINT NOT NULL,\n" +
                                                    "email TEXT PRIMARY KEY NOT NULL,\n" +
                                                    "password TEXT NOT NULL,\n" +
                                                    "admin BOOLEAN NOT NULL DEFAULT(0)\n" +
                                                    ");";
            statement.execute(createClientsTableStatement);
            String createEventsTableStatement = "CREATE TABLE IF NOT EXISTS Events (\n" +
                                                    "name TEXT PRIMARY KEY NOT NULL,\n" +
                                                    "local TEXT NOT NULL,\n" +
                                                    "date TEXT NOT NULL,\n" +
                                                    "activeCode INTEGER UNIQUE,\n" +
                                                    "codeValidityEnding TEXT,\n" +
                                                    "startingTime TEXT NOT NULL,\n" +
                                                    "endingTime TEXT NOT NULL\n" +
                                                    ");";
            statement.execute(createEventsTableStatement);
            String createClientsEventsTableStatement = "CREATE TABLE IF NOT EXISTS ClientsEvents (\n" +
                                                    "clientEmail TEXT NOT NULL,\n" +
                                                    "eventName TEXT NOT NULL,\n" +
                                                    "atTime TEXT NOT NULL,\n" +
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
    private ClientData getClientWithoutPass(String email){
        Statement statement;
        ResultSet result;
        try {
            statement = conn.createStatement();
            String selectClient = "SELECT *\n" +
                    "FROM Clients\n" +
                    "WHERE email='" + email + "';";
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
    public synchronized boolean addNewEntryToClients(ClientData clientData) {
        Statement statement;
        int result;
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
        } catch (SQLException | RuntimeException e) {
            System.out.println("Erro no statement de insercao de um novo Cliente: ");
            e.printStackTrace();
        }
        return false;
    }
    public synchronized ClientData editClientInfo(ClientData clientData) {
        Statement statement;
        boolean first = true;
        int result;
        try {
            statement = conn.createStatement();
            StringBuilder sql = new StringBuilder("UPDATE Clients SET ");

            if (clientData != null) {
                if (!clientData.getName().isEmpty()) {
                    sql.append("name='" + clientData.getName() + "'");
                    first = false;
                }
                if (clientData.getId() != 0L) {
                    if (first) {
                        sql.append("clientID=" + clientData.getId());
                        first = false;
                    } else {
                        sql.append(", clientID=" + clientData.getId());
                    }
                }
                if (!clientData.getPassword().isEmpty()) {
                    if (!first) {
                        sql.append(", ");
                    }
                    sql.append("password='" + clientData.getPassword() + "'");
                }

                sql.append(" WHERE email='" + clientData.getEmail() + "';");

                result = statement.executeUpdate(sql.toString());

                if (result != 0) {
                    updateDBVersion();
                    System.out.println("Client data sent: " + clientData);
                    return getClientWithoutPass(clientData.getEmail());
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro no statement de atualizacao de um Cliente: ");
            e.printStackTrace();
        }
        return null;
    }
    private Event getEventByName(String eventName){
        Statement statement;
        ResultSet result;
        try {
            statement = conn.createStatement();
            String selectEvent = "SELECT *\n" +
                                  "FROM Events\n" +
                                  "WHERE name='" + eventName + "';";
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
    public String generateSQL(Event event,String email) {
        boolean first = true;
        StringBuilder sql = new StringBuilder("SELECT * FROM Events");
        if (event != null) {
            if (!event.getName().isEmpty()) {
                sql.append(" WHERE name LIKE '%").append(event.getName()).append("%'");
                first = false;
            }
            if (!event.getLocal().isEmpty()) {
                if (first) {
                    sql.append(" WHERE local='").append(event.getLocal()).append("'");
                    first = false;
                } else {
                    sql.append(" AND local='").append(event.getLocal()).append("'");
                }
            }
            if (!event.getDate().isEmpty()) {
                if (first) {
                    sql.append(" WHERE date='").append(event.getDate()).append("'");
                    first = false;
                } else {
                    sql.append(" AND date='").append(event.getDate()).append("'");
                }
            }
            if (!event.getStartingTime().isEmpty()) {
                if (first) {
                    sql.append(" WHERE '").append(event.getStartingTime()).append("' BETWEEN startingTime AND endingTime");
                    first = false;
                } else {
                    sql.append(" AND '").append(event.getStartingTime()).append("' BETWEEN startingTime AND endingTime");
                }
            }
        }
        if (email != null) { // para quando saber os eventos que uma determinada pessoa presenciou
            if (first) {
                sql.append(" WHERE name IN (SELECT eventName FROM ClientsEvents WHERE clientEmail='").append(email).append("')");
            } else {
                sql.append(" AND name IN (SELECT eventName FROM ClientsEvents WHERE clientEmail='").append(email).append("')");
            }
        }
        return sql.toString();
    }
    public synchronized ArrayList<Event> getEvents(Event event,String email) {
        try {
            Statement statement = conn.createStatement();
            ResultSet res = statement.executeQuery(generateSQL(event,email));
            ArrayList<Event> events = new ArrayList<>();
            while (res.next()) {
                events.add(new Event(
                        res.getString("name"),
                        res.getString("local"),
                        res.getString("date"),
                        res.getLong("activeCode"),
                        res.getString("codeValidityEnding"),
                        res.getString("startingTime"),
                        res.getString("endingTime")));
            }
            return events;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public synchronized boolean addNewEntryToEvents(Event event){
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
                e.printStackTrace();
            }
        }
        return false;
    }
    public synchronized Event editEventInfo(Event event){
        Statement updateStatement;
        boolean first = true;
        int result;
        ArrayList<ClientData> list = getPresences(event.getName());
        if(!list.isEmpty())
            return null;
        try {
            updateStatement = conn.createStatement();
            StringBuilder sql = new StringBuilder("UPDATE Events SET ");
            if (event != null){
                if (!event.getLocal().isEmpty())
                    sql.append("local='" + event.getLocal() + "'");
                if (!event.getDate().isEmpty())
                    if (first){
                        sql.append("date='" + event.getDate() + "'");
                        first = false;
                    }
                    else
                        sql.append(", date='" + event.getDate() + "'");
                if (!event.getStartingTime().isEmpty())
                    if (first){
                        sql.append("startingTime='" + event.getStartingTime() + "'");
                        first = false;
                    }
                    else
                        sql.append(", startingTime='" + event.getStartingTime() + "'");
                if (!event.getEndingTime().isEmpty())
                    if (first){
                        sql.append("endingTime='" + event.getEndingTime() + "'");
                    }
                    else
                        sql.append(", endingTime='" + event.getEndingTime() + "'");

                sql.append("\nWHERE name='" + event.getName() + "';");
                result = updateStatement.executeUpdate(sql.toString());

                if (result != 0){
                    updateDBVersion();
                    return getEventByName(event.getName());
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro no statement de obtencao da relacao ou de atualizacao de um Evento: ");
            e.printStackTrace();
        }
        return null;
    }
    public ArrayList<ClientData> getPresences(String eventName){
        Statement statement;
        ResultSet resultSet;
        ArrayList<ClientData> list = new ArrayList<>();
        try {
            statement = conn.createStatement();
            String selectClientsEventsStatement = "SELECT * FROM Clients\n"+
                                                  "WHERE email IN (SELECT clientEmail\n" +
                                                                  "FROM ClientsEvents\n" +
                                                                  "WHERE eventName='" + eventName + "');";
            resultSet = statement.executeQuery(selectClientsEventsStatement);

            while (resultSet.next()){
                list.add(
                        new ClientData(
                                resultSet.getString("name"),
                                resultSet.getLong("clientID"),
                                resultSet.getString("email")
                        )
                );
            }
        } catch (SQLException e) {
            System.out.println("Erro a obter todas as presenças para um determinado evento");
            e.printStackTrace();
        }
        return list;
    }
    public synchronized ArrayList<ClientData> removePresencesFromEvent(String eventName){
        Statement statement;
        int result = 0;
        ArrayList<ClientData> clientsList = getPresences(eventName);
        try {
            statement = conn.createStatement();
            for (ClientData clientData : clientsList) {
                String selectEvent = "DELETE FROM ClientsEvents\n" +
                                     "WHERE clientEmail='" + clientData.getEmail() + "';";
                result += statement.executeUpdate(selectEvent);
            }
            if(result != 0)
                updateDBVersion();
            System.out.println(result + "presenças foram eliminadas a respeito do evento: '" + eventName + "'!");
        } catch (SQLException e) {
            System.out.println("Erro no statement de obtencao de Events: ");
            e.printStackTrace();
        }

        return !clientsList.isEmpty() ? clientsList : null;
    }
    public synchronized Event editActiveCode (String eventName, long code, String codeValidityEnding){
        Statement statement;
        int result;
        try {
            statement = conn.createStatement();
            String updateNewCodeStatement = "UPDATE Events SET activeCode=" + code + ", codeValidityEnding='" +
                                            codeValidityEnding + "'\n" +
                                            "WHERE name='" + eventName + "';";
            result = statement.executeUpdate(updateNewCodeStatement);
            if (result != 0){
                updateDBVersion();
                return getEventByName(eventName);
            }

        } catch (SQLException e) {
            System.out.println("Erro a gravar um novo código de presenças!");
        }

        return null;
    }
    public synchronized boolean removeEvent(String eventName){
        Statement statement;
        int result;
        try {
            statement = conn.createStatement();
            String selectEvent = "DELETE FROM Events\n" +
                                 "WHERE name='" + eventName + "';";
            result = statement.executeUpdate(selectEvent);

            if(result != 0) {
                System.out.println("Evento: '" + eventName + "' foi removido com sucesso!");
                updateDBVersion();
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Erro no statement de obtencao de Events: ");
            e.printStackTrace();
        }

        return false;
    }
    public synchronized boolean addPresence(String clientEmail, String eventName, String atTime){
        Statement insertStatement;
        int result;
        try {
            insertStatement = conn.createStatement();
            String insertClientEventStatement = "INSERT INTO ClientsEvents (clientEmail, eventName, atTime) VALUES " +
                                                "('" + clientEmail + "','" + eventName + "','" + atTime + "');";
            result = insertStatement.executeUpdate(insertClientEventStatement);
            if(result != 0){
                updateDBVersion();
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
    public synchronized boolean checkCodeToAssignPresence(long eventCode, String email, String atTime) {
        try {
            Statement selectEventWithCorrectCodeStatement, selectPresencesStatement;
            ResultSet resultSetOfCode, resultSetOfPresence;
            int result;
            String eventNameFound;

            selectEventWithCorrectCodeStatement = conn.createStatement();
            String selectEventsStatement = "SELECT name, activeCode\n" +
                                           "FROM Events\n" +
                                           "WHERE activeCode=" + eventCode + "\n" +
                                           "AND '" + atTime + "' BETWEEN startingTime AND " +
                             "CASE WHEN codeValidityEnding > endingTime THEN endingTime ELSE codeValidityEnding END;";
            resultSetOfCode = selectEventWithCorrectCodeStatement.executeQuery(selectEventsStatement);
            if(resultSetOfCode.next()){
                eventNameFound = resultSetOfCode.getString("name");
                selectPresencesStatement = conn.createStatement();
                String selectActivePresencesStatement = "SELECT * FROM Events\n" +
                                                        "WHERE name IN (SELECT eventName\n" +
                                                                       "FROM ClientsEvents\n" +
                                                                       "WHERE clientEmail='" + email + "')" +
                                                        "AND '" + atTime + "' BETWEEN startingTime AND endingTime;";
                resultSetOfPresence = selectPresencesStatement.executeQuery(selectActivePresencesStatement);
                if(!resultSetOfPresence.next())
                    return addPresence(email, eventNameFound, atTime);
            }
        }catch (SQLException e){
            System.out.println("Erro ao ler informacao sobre eventos ou insercao na tabela relacional!");
        }
        return false;
    }
}