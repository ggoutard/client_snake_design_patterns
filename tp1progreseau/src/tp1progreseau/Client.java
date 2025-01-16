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
	
		try
		{
			this.socket = new Socket(adresse, this.port);
			
			System.out.println("Connecté");
		} 
		catch(UnknownHostException e) {System.out.println(e);}
		catch (IOException e) {System.out.println("Aucun serveur n’est rattaché au port ");}
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
	        sortie = new PrintWriter(this.socket.getOutputStream(), true);
	        entree = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
	        sortie.println(chaine); 
	        this.lengthCurrentMessage = entree.read();
	    } catch (IOException e) {
	        System.out.println("Aucun serveur n’est rattaché au port");
	    }
	}
	
	
    public static void main(String[] argu) {
		
    	Client c = new Client("127.0.0.1");
		c.connect(2501);
		c.sendMessage("hello world");
		c.disconnect();
		
	}

} 
