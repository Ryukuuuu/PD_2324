package cosumer;

import com.nimbusds.jose.shaded.gson.Gson;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Base64;

public class PdRestApiComSegurancatConsumer {

    public static String sendRequestAndShowResponse(String uri, String verb, String authorizationValue) throws MalformedURLException, IOException {

        String responseBody = null;
        URL url = new URL(uri);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod(verb);
        connection.setRequestProperty("Accept", "application/xml, */*");

        if(authorizationValue!=null) {
            connection.setRequestProperty("Authorization", authorizationValue);
        }

        connection.connect();

        int responseCode = connection.getResponseCode();
        System.out.println("Response code: " +  responseCode + " (" + connection.getResponseMessage() + ")");

        if(connection.getErrorStream()!=null) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                responseBody = br.readLine();
            } catch (IOException e) {}
        }

        try(BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            responseBody = br.readLine();
        }catch (IOException e) {}

        connection.disconnect();

        System.out.println(uri + " -> " + responseBody);
        System.out.println();

        return responseBody;
    }
    
    public static void main(String args[]) throws MalformedURLException, IOException {

        String helloUri = "http://localhost:8080/hello/fr?name=Jeanne";
        String helloUri2 = "http://localhost:8080/hello/gr?name=Jeanne";
        String loginUri = "http://localhost:8080/login";
        String loremUri = "http://localhost:8080/lorem?type=paragraph";
        String signingUri = "http://localhost:8080/signing";

        System.out.println();

        //OK
        sendRequestAndShowResponse(helloUri, "GET", null);

        //Língua "gr" não suportada
        sendRequestAndShowResponse(helloUri2, "GET", null);

        //Falta um campo "Authorization: basic ..." válido no cabeçalho do pedido para autenticação básica
        String token = sendRequestAndShowResponse(loginUri, "POST",null);

        //OK
        token = sendRequestAndShowResponse(loginUri, "POST","basic YWRtaW46YWRtaW4="); //Base64(admin:admin)
        

        //Falta um campo "Authorization: bearer ..." no cabeçalho do pedido com um token JWT válido
        sendRequestAndShowResponse(loremUri, "GET", null);

        //OK
        sendRequestAndShowResponse(loremUri, "GET", "bearer " + token);

        //POST não suportado para esta URI
        sendRequestAndShowResponse(loremUri, "POST", "bearer " + token);

        //Teste signing
        sendRequestAndShowResponse(signingUri,"POST",null);
    }
}
