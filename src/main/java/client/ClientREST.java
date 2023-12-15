package client;

import client.model.ModelManagerREST;

import java.io.IOException;

public class ClientREST {

    private static ModelManagerREST modelManagerREST = new ModelManagerREST();

    public static void main(String[] args) throws IOException {
        modelManagerREST.login("mail","pass");
    }
}
