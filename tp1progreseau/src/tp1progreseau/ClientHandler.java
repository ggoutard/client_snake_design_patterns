package tp1progreseau;

import java.io.*;
import java.net.*;

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
    
    @Override
    public void run() {
        try {
            String message;
            while ((message = entree.readLine()) != null) {
                if (message.equals("stop")) 
                {
                    break;
                }
                Serveur.sendMessage(identifiant,message, sortie);
            }
        } 
        catch (IOException e) {e.printStackTrace();} 
        finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Serveur.removeClient(sortie);
        }
    }
}
