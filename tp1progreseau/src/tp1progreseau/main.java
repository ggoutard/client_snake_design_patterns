package tp1progreseau;

import tp1progreseau.Serveur;

public class main {
	public static void main(String[] argu) {
		
		Serveur s = new Serveur(2500);
		
		s.run();
		
	}
}
