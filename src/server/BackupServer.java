package server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class BackupServer {
    private static final int MULTICAST_PORT = 4444;
    private static final String MULTICAST_ADDRESS = "230.44.44.44";
    private static final int HEARTBEAT_TIMEOUT = 30;
    public static void main(String[] args) {
        File localDirectory;

        if(args.length != 1){
            System.out.println("<Sintaxe> java BackupServer <diretoria da réplica da BD>");
            return;
        }

        localDirectory = new File(args[1].trim());

        if(!localDirectory.exists()){
            System.out.println("A diretoria " + localDirectory + " nao existe!");
            return;
        }

        if(!localDirectory.isDirectory()){
            System.out.println("O caminho " + localDirectory + " nao se refere a uma diretoria!");
            return;
        }

        if(localDirectory.list().length != 0){
            System.out.println("A diretoria " + localDirectory + "nao esta vazia!");
            return;
        }

        if(!localDirectory.canWrite()){
            System.out.println("Sem permissoes de escrita na diretoria " + localDirectory + "!");
            return;
        }

        // falta cenas....
    }
}
