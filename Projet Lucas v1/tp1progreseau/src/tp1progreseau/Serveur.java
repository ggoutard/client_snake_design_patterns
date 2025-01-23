package tp1progreseau;

import java.net.*;
import java.io.*;
import java.util.*;

public class Serveur {

    private static Set<PrintWriter> clients = new HashSet<>();

    public static void main(String[] args) {
        int port = 2545;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Le serveur écoute sur le port " + port);

            while (true) {
                // Attente d'une connexion d'un client
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nouveau client connecté : " + clientSocket.getInetAddress());

                // Création des flux de communication
                PrintWriter sortie = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader entree = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                // Ajout du client à la liste des clients
                synchronized (clients) {
                    clients.add(sortie);
                }

                // Lancer un thread pour gérer la communication avec ce client
                new Thread(new ClientHandler(clientSocket, entree, sortie)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket socket;
        private BufferedReader entree;
        private PrintWriter sortie;

        public ClientHandler(Socket socket, BufferedReader entree, PrintWriter sortie) {
            this.socket = socket;
            this.entree = entree;
            this.sortie = sortie;
        }

        @Override
        public void run() {
            try {
                String message;
                while ((message = entree.readLine()) != null) {
                    if (message.equals("stop")) {
                        break;
                    }

                    // Transmettre le message à tous les clients
                    synchronized (clients) {
                        for (PrintWriter client : clients) {
                            if (client != sortie) {  // Ne pas renvoyer le message à l'expéditeur
                                client.println(message);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                synchronized (clients) {
                    clients.remove(sortie);  // Retirer ce client de la liste
                }
            }
        }
    }
}
