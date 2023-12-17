package client.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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

        // Não é necessário?
        /*
        if (body != null) {
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "Application/Json");
            connection.getOutputStream().write(body.getBytes());
        }
        */

        connection.connect();

        int responseCode = connection.getResponseCode();
        System.out.println("Response code: " + responseCode + " (" + connection.getResponseMessage() + ")");

        if (connection.getErrorStream() != null) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                responseBody = br.readLine();
            } catch (IOException ignored) { }
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            responseBody = br.readLine();
        } catch (IOException ignored) { }

        connection.disconnect();

        System.out.println(uri + " -> " + responseBody);
        System.out.println();

        return responseBody;
    }
}