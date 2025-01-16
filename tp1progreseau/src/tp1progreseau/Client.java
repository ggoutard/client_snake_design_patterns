package tp1progreseau;

import java.net.*;
import java.io.*;

public class Client {
	private String s;
	private int p;
	private String ch;
	private int l;
	
	public Client(String s, int p, String ch, int l) {
		this.s = s;
		this.p = p;
		this.ch = ch;
		this.l = l;
	}
	
	public void run() {
		Socket so;
		BufferedReader entree;
		PrintWriter sortie;
		
		try{// on connecte un socket
			so = new Socket(s, p);
			sortie = new PrintWriter(so.getOutputStream(), true);
			entree = new BufferedReader(new InputStreamReader(so.getInputStream()));
			sortie.println(ch); // on écrit la chaîne et le newline dans le canal de sortie
			l = entree.read(); // on lit l’entier qui arrive
			System.out.println("D’après le serveur la longueur de "+ch+" est "+l);
			so.close(); // on ferme la connexion
		} catch(UnknownHostException e) {System.out.println(e);}
		catch (IOException e) {System.out.println("Aucun serveur n’est rattaché au port ");}
	}
	
} 
