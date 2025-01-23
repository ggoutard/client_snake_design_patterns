package tp1progreseau;

import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class Serveur {
    
	private int port;
    private ServerSocket ecoute;
    private ArrayList<ClientHandler> clients;

    public Serveur(int port) {
        this.port = port;
        this.clients = new ArrayList<>();
    }

    public void run() {
        try {
            this.ecoute = new ServerSocket(port);
            System.out.println("Serveur mis en place sur le port " + port);
            
            while (true) 
            {
                this.addClient( this.ecoute.accept());
                
            }
            
        } catch (IOException e) {
            System.out.println("Serveur injoignable : " + e);
        }
    }
    
    public synchronized void addClient(Socket socket) {
    	ClientHandler clientHandler = new ClientHandler(this,socket);
        this.clients.add(clientHandler);
        clientHandler.start();
        
    }

    public synchronized void removeClient(ClientHandler clientHandler) {
        this.clients.remove(clientHandler);
        System.out.println("Client retiré : " + clientHandler);
    }
    	
    public synchronized void interaction(ClientHandler client, String chaine)
    {
    	for(ClientHandler otherClient : this.clients)
    	{
    		if(client != otherClient) otherClient.sendAnswer("client_"+Integer.toString(client.getNumberClient()) + " : " + chaine);
    	}
	    	
		System.out.println("client_"+Integer.toString(client.getNumberClient()) + " : " + chaine );

	    	
    }

    public static void main(String[] argu) {
        Serveur s = new Serveur(2501);
        s.run();
    }
}
