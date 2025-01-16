package tp1progreseau;

import java.net.*;
import java.io.*;

public class Serveur {
    private int port; 
    private ServerSocket ecoute;

    public Serveur(int port) {
        this.port = port;
    }

    public void run() {
        try {
            ecoute = new ServerSocket(port);
            System.out.println("Serveur mis en place sur le port " + port);

            while (true) {
                try {
                	Socket so = ecoute.accept();
                	BufferedReader entree = new BufferedReader(new InputStreamReader(so.getInputStream()));
                    PrintWriter sortie = new PrintWriter(so.getOutputStream());
                    String ch = entree.readLine();
                    System.out.println("On a reçu : |" + ch + "|");
                    sortie.write(ch.length());
                    sortie.flush();
                    System.out.println("On a envoyé : " + ch.length() + " et on a fermé la connexion");
                } catch (IOException e) {
                	System.out.println("Problème\n" + e);
                }
            }

       } catch (IOException e) {
    	   System.out.println("Problème\n" + e);
       }
    }
    
    public static void main(String[] argu) {
		

		Serveur s = new Serveur(2501);
		s.run();
		
	}
    
	
}
