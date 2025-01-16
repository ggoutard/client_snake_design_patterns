package tp1progreseau;

import java.net.*;
import java.io.*;

public class Client {
	
	private String adresse;
	private int port;
	private String currentMessage;
	private String postMessage;
	private int lengthCurrentMessage;
	private Socket socket;
	
	public Client(String adresse) {
		this.adresse = adresse;
	
	}
	
	public void connect(int port) {
		
		this.port = port;

		this.sendMessage("connexion");
		System.out.println("Connecté");
		
	}
	
	public void disconnect() {
		try
		{
			this.socket.close();
		} 
		catch(UnknownHostException e) {System.out.println(e);}
		catch (IOException e) {System.out.println("Le socket n'est pas connecté");}
	}
	
	public void sendMessage(String chaine) {
	    BufferedReader entree;
	    PrintWriter sortie;

	    try {
	    	this.socket = new Socket(this.adresse, this.port);

	        sortie = new PrintWriter(this.socket.getOutputStream(), true);
	        entree = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
	        sortie.println(chaine); 
	        this.lengthCurrentMessage = entree.read();
            System.out.println("Message envoyé : " + chaine);

	    } catch (IOException e) {
	        System.out.println("Aucun serveur n’est rattaché au port");
	    }
	}
	
	
    public static void main(String[] argu) {
		
    	Client c = new Client("127.0.0.1");
		c.connect(2501);
		c.sendMessage("hello world");
		c.sendMessage("Salut monde");
		c.sendMessage("stop");
		c.sendMessage("hello world");

		
	}

} 
