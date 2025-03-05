package tp1progreseau;

import java.io.*;
import java.net.*;
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
            final long interval = 1000 / 60; // 60 fois par seconde

            while (true) {
                long startTime = System.currentTimeMillis();

                // Lire un message si disponible
                if (entree.ready()) { // Vérifie si des données sont prêtes à être lues
                    String message = entree.readLine();
                    
                    if (message != null) {
                        System.out.println("Message reçu : " + message);

                        // Stopper la boucle si le message est "stop"
                        if (message.equals("stop")) {
                            break;
                        }
                    }
                }

                // Si le jeu est en cours d'exécution, envoyer un message
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
