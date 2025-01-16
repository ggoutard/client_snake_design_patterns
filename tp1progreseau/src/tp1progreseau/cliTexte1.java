package tp1progreseau;

import java.net.*;
import java.io.*;

public class cliTexte1 {
	public static void main(String[] argu) {
		Socket so;
		BufferedReader entree;
		PrintWriter sortie;
		String s; // le serveur
		int p; // le port de connexion
		String ch; // la chaîne envoyée
		int l; // et sa longueur reçue
		
		s="127.0.0.1";
		p=2500;
		ch="test";
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
} } 
