package tp1progreseau;

import java.io.*;
import java.net.*;

import tp1progreseau.utils.Commande;
//modifier
public class ClientHandler implements Runnable {
	
	
	private static int COUNTER = 0;
	
	
	private int identifiant;
    private Socket socket;
    private BufferedReader entree;
    private PrintWriter sortie;
    
    public ClientHandler(Socket socket, BufferedReader entree, PrintWriter sortie) {
    	this.identifiant = ClientHandler.COUNTER++;
        this.socket = socket;
        this.entree = entree;
        this.sortie = sortie;
    }
    
    public void run() {
        try {
            final long interval = 1000 / 60; 

            while (true) {
                long startTime = System.currentTimeMillis();

                if (entree.ready()) { 
                    String json = entree.readLine();
                    
                    if (json != null) 
                    {
                                                
                        Commande commande = Commande.fromJson(json);
                        
                        if(this.identifiant+1 % 2 == 0) Serveur.setMovementJoueur1(commande.getAction());
                        else Serveur.setMovementJoueur2(commande.getAction());

                    }
                }

                if (Serveur.gameIsRunning()) {
                    Serveur.sendMessage(identifiant, Serveur.buildPanelBuilder().toJson(), sortie);
                }

                long elapsedTime = System.currentTimeMillis() - startTime;
                long sleepTime = interval - elapsedTime;

                // Attendre le temps restant
                if (sleepTime > 0) {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Serveur.removeClient(sortie);
        }
    }

}
