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
                        clientData.getId() + ", password='" + clientData.getPassword() + "', admin=" +
                        clientData.isAdmin() + "\n" +
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

    public Event getEventByName(String eventName){
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
            if (event.getName() != null) {
                sql.append(" WHERE name='").append(event.getName()).append("'");
                first = false;
            }
            if (event.getLocal() != null) {
                if (first) {
                    sql.append(" WHERE local='").append(event.getLocal()).append("'");
                    first = false;
                } else {
                    sql.append(" AND local='").append(event.getLocal()).append("'");
                }
            }
            if (event.getDate() != null) {
                if (first) {
                    sql.append(" WHERE date='").append(event.getDate()).append("'");
                    first = false;
                } else {
                    sql.append(" AND date='").append(event.getDate()).append("'");
                }
            }
            if (event.getStartingTime() != null) {
                if (first) {
                    sql.append(" WHERE startingTime='").append(event.getStartingTime()).append("'");
                    first = false;
                } else {
                    sql.append(" AND startingTime='").append(event.getStartingTime()).append("'");
                }
            }
            if (event.getEndingTime() != null) {
                if (first) {
                    sql.append(" WHERE endingTime='").append(event.getEndingTime()).append("'");
                    first = false;
                } else {
                    sql.append(" AND endingTime='").append(event.getEndingTime()).append("'");
                }
            }
        }
        if (email != null) {
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

    public ArrayList<Event> getEventsByLocal(String local){
        Statement statement;
        ResultSet result;
        ArrayList<Event> list = new ArrayList<>();
        try {
            statement = conn.createStatement();
            String selectEvent = "SELECT *\n" +
                                 "FROM Events\n" +
                                 "WHERE local='" + local + "';";
            result = statement.executeQuery(selectEvent);
            while (result.next()){
                list.add(new Event(
                                    result.getString("name"),
                                    result.getString("local"),
                                    result.getString("date"),
                                    result.getLong("activeCode"),
                                    result.getString("codeValidityEnding"),
                                    result.getString("startingTime"),
                                    result.getString("endingTime")
                        )
                );
            }
        } catch (SQLException e) {
            System.out.println("Erro no statement de obtencao de Events: ");
            e.printStackTrace();
        }

        return !list.isEmpty() ? list : null;
    }

    public ArrayList<Event> getEventsByDate(String date){
        Statement statement;
        ResultSet result;
        ArrayList<Event> list = new ArrayList<>();
        try {
            statement = conn.createStatement();
            String selectEvent = "SELECT *\n" +
                    "FROM Events\n" +
                    "WHERE date='" + date + "';";
            result = statement.executeQuery(selectEvent);
            while (result.next()){
                list.add(new Event(
                                result.getString("name"),
                                result.getString("local"),
                                result.getString("date"),
                                result.getLong("activeCode"),
                                result.getString("codeValidityEnding"),
                                result.getString("startingTime"),
                                result.getString("endingTime")
                        )
                );
            }
        } catch (SQLException e) {
            System.out.println("Erro no statement de obtencao de Events: ");
            e.printStackTrace();
        }

        return !list.isEmpty() ? list : null;
    }

    // falta aqui a condição
    public ArrayList<Event> getEventsByPeriod(String begin, String finish){
        Statement statement;
        ResultSet result;
        ArrayList<Event> list = new ArrayList<>();
        try {
            statement = conn.createStatement();
            String selectEvent = "SELECT *\n" +
                                 "FROM Events\n" +
                                 "WHERE startingTime='" + begin + "';";
            result = statement.executeQuery(selectEvent);
            while (result.next()){
                list.add(new Event(
                                result.getString("name"),
                                result.getString("local"),
                                result.getString("date"),
                                result.getLong("activeCode"),
                                result.getString("codeValidityEnding"),
                                result.getString("startingTime"),
                                result.getString("endingTime")
                        )
                );
            }
        } catch (SQLException e) {
            System.out.println("Erro no statement de obtencao de Events: ");
            e.printStackTrace();
        }

        return !list.isEmpty() ? list : null;
    }

    public boolean addNewEntryToEvents(Event event){
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

    public ArrayList<String> getPresences(String eventName){
        Statement statement;
        ResultSet resultSet;
        ArrayList<String> list = new ArrayList<>();
        synchronized (conn){
            try {
                statement = conn.createStatement();
                String selectClientsEventsStatement = "SELECT clientEmail\n" +
                                                      "FROM ClientsEvents\n" +
                                                      "WHERE eventName='" + eventName + "';";
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

    public ArrayList<Event> getEventsFromClientsPresences(String email){
        Statement statement;
        ResultSet result;
        ArrayList<Event> list = new ArrayList<>();
        try {
            statement = conn.createStatement();
            String selectEvent = "SELECT eventName\n" +
                                 "FROM ClientsEvents\n" +
                                 "WHERE clientEmail='" + email + "';";
            result = statement.executeQuery(selectEvent);
            while (result.next()){
                list.add(getEventByName(result.getString("eventName")));
            }
        } catch (SQLException e) {
            System.out.println("Erro no statement de obtencao de Events: ");
            e.printStackTrace();
        }

        return !list.isEmpty() ? list : null;
    }

    public synchronized ArrayList<String> removePresencesFromEvent(String eventName){
        Statement statement;
        int result = 0;
        ArrayList<String> clientsList = getPresences(eventName);
        try {
            statement = conn.createStatement();
            for (String clientEmail : clientsList) {
                String selectEvent = "DELETE FROM ClientsEvents\n" +
                                     "WHERE clientEmail='" + clientEmail + "';";
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

    public Event editActiveCode (String eventName, long code, String codeValidityEnding){
        Statement statement;
        int result;
        synchronized (conn){
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
        }
        return null;
    }
    public Event editEventInfo(Event event){
        Statement updateStatement;
        int result;
        synchronized (conn){
            ArrayList<String> list = getPresences(event.getName());
            if(!list.isEmpty())
                return null;
            try {
                updateStatement = conn.createStatement();
                String updateEventStatement = "UPDATE Events SET local='" + event.getLocal() +
                        "', date='" + event.getDate() + "', startingTime='" + event.getStartingTime() +
                        "', endingTime='" + event.getEndingTime() + "'\n" +
                        "WHERE name='" + event.getName() + "';";
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

    public boolean removeEvent(String eventName){
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
    public boolean checkCodeToAssignPresence(long eventCode, String email) {
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