package tp1progreseau;

import java.net.*;
import java.io.*;

public class Client {
	
	private String name;
	private int port;
	private String currentmessage;
	private String postMessage;
	private int lengthCurrentMessage;
	private Socket socket;
	
	public Client(String name) {
		this.name = name;
	
	}
	
	public void connect(int port) {
		this.port = port;
		BufferedReader entree;
		PrintWriter sortie;
		
		try
		{
			this.socket = new Socket(name, this.port);
//			sortie = new PrintWriter(so.getOutputStream(), true);
//			entree = new BufferedReader(new InputStreamReader(so.getInputStream()));
//			sortie.println(this.currentmessage); 
//			this.lengthCurrentMessage = entree.read();
//			System.out.println("D’après le serveur la longueur de "+this.currentmessage+" est "+this.lengthCurrentMessage);
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
	
} 
