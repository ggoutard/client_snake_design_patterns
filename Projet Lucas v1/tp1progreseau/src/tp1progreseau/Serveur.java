package tp1progreseau;

import java.net.*;
import java.io.*;
import java.util.*;

public class Serveur {
    
	private static Set<PrintWriter> clients = new HashSet<>();
    
    public static void main(String[] args) {
        int port = 2545;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Le serveur écoute sur le port " + port);
            while (true) {

	            	Socket clientSocket = serverSocket.accept();
	            	Serveur.addClient(clientSocket);
                

            }
        } catch (IOException e) {System.out.println("Erreur dans la tentative d'ouverture du port  : "+ e);}
    }
    
    public synchronized static void sendMessage(int identifiant, String message, PrintWriter sender) {
        synchronized (clients) {
            for (PrintWriter client : clients) {
                if (client != sender) {
                    client.println("client_"+identifiant+" : " + message);
                }
            }
        }
    }
    
    public synchronized static void removeClient(PrintWriter client) {
        synchronized (clients) {
            clients.remove(client);
        }
    }
    
    public synchronized static void addClient(Socket clientSocket) {
    	try {
	    	PrintWriter sortie = new PrintWriter(clientSocket.getOutputStream(), true);
	        BufferedReader entree = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	        
	        synchronized (clients) {
	            clients.add(sortie);
	        }
	        
	        new Thread(new ClientHandler(clientSocket, entree, sortie)).start();
	        System.out.println("Nouveau client connecté : " + clientSocket.getInetAddress());
	      
    	}
        catch (IOException e) {System.out.println("Erreur dans l'ajout du client : "+ e);}

    }
}
