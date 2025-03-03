package tp1progreseau;

import java.net.*;
import tp1progreseau.accueil.Accueil;
import tp1progreseau.controller.ControllerSnakeGameNetwork;
import tp1progreseau.utils.Commande;
import java.io.*;
import java.util.Observer;
import java.util.Observable;

public class ClientSnake extends Client {

    private static class ThreadInput implements Runnable, Observer {
        private PrintWriter sortie;
        private Accueil accueil;
        private ControllerSnakeGameNetwork controller;
        private boolean running;

        public ThreadInput(PrintWriter sortie) {
            this.sortie = sortie;
            this.running = true;
        }

        @Override
        public void run() {
            this.controller = new ControllerSnakeGameNetwork();
            this.controller.addObserver(this);
            this.accueil = new Accueil(controller);

            try {
                while (this.running) {}
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void update(Observable o, Object arg) {
            if (arg instanceof Commande) {
                Commande commande = (Commande) arg;
                System.out.println(commande.toJson());
                sortie.println(commande.show());
            }
        }
    }

    private static class ThreadOutput implements Runnable {
        private BufferedReader entree;

        public ThreadOutput(BufferedReader entree) {
            this.entree = entree;
        }

        @Override
        public void run() {
            try {
                String message;
                while (true) {
                    message = entree.readLine();
                    if (message == null) break;
                    System.out.println(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] argu) {
        Socket socket;
        BufferedReader entree;
        PrintWriter sortie;
        String ip = "127.0.0.1"; // l'adresse du serveur
        int p = 2545; // le port de connexion
        try {
            // Connexion au serveur
            socket = new Socket(ip, p);
            sortie = new PrintWriter(socket.getOutputStream(), true);
            entree = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Lancer le thread pour la saisie de l'utilisateur
            Thread inputThread = new Thread(new ThreadInput(sortie));
            inputThread.start();

            // Lancer le thread pour écouter les messages du serveur (qui sont les messages des autres clients)
            Thread outputThread = new Thread(new ThreadOutput(entree));
            outputThread.start();

            // Attendre la fin des threads
            inputThread.join();
            outputThread.join();
            socket.close();

        } catch (UnknownHostException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println("Aucun serveur n’est rattaché au port");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
