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
            System.out.println("Message envoyé : " + chaine + "\nRéponse reçue : " + reponse);
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
        c.disconnect();
    }
}
