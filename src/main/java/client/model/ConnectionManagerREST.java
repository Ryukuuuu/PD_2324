package client.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class ConnectionManagerREST {

    ModelManagerREST modelManagerREST;

    public ConnectionManagerREST(ModelManagerREST modelManagerREST) {
        this.modelManagerREST = modelManagerREST;
    }

    public String sendRequestAndShowResponse(String uri, String verb, String authorizationValue, String body) throws MalformedURLException, IOException {

        String responseBody = null;

        URL url = new URL(uri);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod(verb);
        connection.setRequestProperty("Accept", "application/xml, */*");

        if (authorizationValue != null) {
            connection.setRequestProperty("Authorization", authorizationValue);
        }

        if (body != null) {
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.getOutputStream().write(body.getBytes());
        }

        connection.connect();

        int responseCode = connection.getResponseCode();
        System.out.println("Response code: " + responseCode + " (" + connection.getResponseMessage() + ")");
/*
        Scanner s;

        if (connection.getErrorStream() != null) {
            s = new Scanner(connection.getErrorStream()).useDelimiter("\\A");
            responseBody = s.hasNext() ? s.next() : null;
        }

        try {
            s = new Scanner(connection.getInputStream()).useDelimiter("\\A");
            responseBody = s.hasNext() ? s.next() : null;
        } catch (IOException ignored) { }
*/

        if (connection.getErrorStream() != null) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                responseBody = br.readLine();
            } catch (IOException ignored) { }
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            responseBody = br.readLine();
        } catch (IOException ignored) { }


        /*
        InputStream jsonStream = connection.getInputStream();

        JsonReader jsonReader = Json.createReader(jsonStream);
        JsonArray array = jsonReader.readArray();

        jsonReader.close();
        connection.disconnect();

        Gson gson = new GsonBuilder().create();

        System.out.println();

        for(int i=0; i<array.size(); i++){
            JsonObject object = array.getJsonObject(i);
            //System.out.println(object);
            University university = gson.fromJson(object.toString(), University.class);
            System.out.println("\t- " + university);
        }
        */

        connection.disconnect();

        System.out.println(uri + " -> " + responseBody);
        System.out.println();

        return responseBody;
    }
}