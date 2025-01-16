package tp1progreseau;

import java.net.*;
import java.io.*;

public class ClientHandler extends Thread implements Comparable<ClientHandler> {
    private static int COUNTER = 0;

    private int numberClient;
    private Socket socket;
    private Serveur serveur;
    private BufferedReader entree;
    private PrintWriter sortie;

    public ClientHandler(Serveur serveur, Socket socket) {
    	
        this.numberClient = ClientHandler.COUNTER++;
        this.socket = socket;
        this.serveur = serveur;
        
        try 
        {
            this.entree = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.sortie = new PrintWriter(this.socket.getOutputStream(), true);
        } 
        catch (IOException e) 
        {
            System.out.println("Erreur d'initialisation du client : " + e);
        }
    }

    @Override
    public void run() {
        String chaine;
        try {
            while ((chaine = entree.readLine()) != null) {
            	this.serveur.interaction(this,chaine);		
                if (chaine.equals("stop")) {
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Erreur de communication : " + e);
            try {
                if (!socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException exept) {
                System.out.println("Erreur lors de la fermeture du socket : " + exept);
            }
        } 
    }


    @Override
    public void interrupt() {
    	
    	super.interrupt();
    	this.serveur.removeClient(this);
   
    }

    @Override
    public int compareTo(ClientHandler other) {
        return Integer.toString(this.numberClient).compareTo(Integer.toString(other.numberClient));
    }

    public void sendAnswer(String chaine) {
        if (socket.isClosed()) throw new Error("Socket fermé, impossible d'envoyer le message.");
        this.sortie.println(chaine);
       
    }
    
    public int getNumberClient()
    {
    	return numberClient;
    }

    
}
