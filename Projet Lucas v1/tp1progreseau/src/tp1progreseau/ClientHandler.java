package tp1progreseau;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class ClientHandler extends Thread {
    private Socket socket;
    private static int nbClients = 0;
    private int numeroClient;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.numeroClient = nbClients++; 
    }

    @Override
    public void run() {
        try {
        	BufferedReader entree = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter sortie = new PrintWriter(socket.getOutputStream(), true);
            String ch; // La chaîne reçue
            while ((ch = entree.readLine()) != null && !ch.equals("stop")) {
                System.out.println("Message reçu du client " + numeroClient + " : |" + ch + "|");
                sortie.println(Integer.toString(ch.length())); // Envoie la longueur de la chaîne
                System.out.println("Longueur envoyée : " + ch.length());
            }
            socket.close(); // Fermeture du socket une fois le traitement terminé
        } catch (IOException e) {
            System.out.println("Erreur lors de la communication avec le client : " + e.getMessage());
        }
    }
}
