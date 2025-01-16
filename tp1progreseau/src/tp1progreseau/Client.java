package tp1progreseau;

import java.net.*;
import java.io.*;

public class Client {
    private String adresse;
    private int port;
    private Socket socket;
    private BufferedReader entree;
    private PrintWriter sortie;

    public Client(String adresse) {
        this.adresse = adresse;
    }

    public void connect(int port) {
        this.port = port;
        try {
            this.socket = new Socket(this.adresse, this.port);
            this.sortie = new PrintWriter(this.socket.getOutputStream(), true);
            this.entree = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            System.out.println("Connecté");
        } catch (IOException e) {
            System.out.println("Aucun serveur n’est rattaché au port");
        }
    }

    public void disconnect() {
        try {
            this.socket.close();
        } catch (IOException e) {
            System.out.println("Le socket n'est pas connecté");
        }
    }

    public void sendMessage(String chaine) {
        try {
            this.sortie.println(chaine);
            String reponse = this.entree.readLine();
            System.out.println("anwser: " + reponse);
        } catch (IOException e) {
            System.out.println("Aucun serveur n’est rattaché au port");
        }
    }
    
    
    public void runSaisie() {
        String chaine = "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Saisissez une chaîne (ou 'stop' pour quitter) : ");

        try {
            while (!chaine.equals("stop")) {
                System.out.print("shell:> ");
                chaine = reader.readLine();
                this.sendMessage(chaine);
            }
        } catch (IOException e) {
            System.out.println("Erreur de lecture : " + e);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                System.out.println("Erreur lors de la fermeture du lecteur : " + e);
            }
        }
    }

    public static void main(String[] argu) {
        Client c = new Client("127.0.0.1");
        c.connect(2501);
        c.runSaisie();
        c.disconnect();
    }
}
