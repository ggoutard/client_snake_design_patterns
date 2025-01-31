package tp1progreseau;

import java.net.*;
import java.io.*;

public class Client {

    private static class ThreadInput implements Runnable {
        private PrintWriter sortie;

        public ThreadInput(PrintWriter sortie) {
            this.sortie = sortie;
        }

        @Override
        public void run() {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String chaine = "";
            try {
                while (!chaine.equals("stop")) {
                    chaine = reader.readLine(); // lire la saisie de l'utilisateur
                    sortie.println(chaine); // envoyer le message au serveur
                }
            } catch (IOException e) {
                e.printStackTrace();
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
                    message = entree.readLine(); // lire un message du serveur
                    if (message == null) break; // si on atteint la fin du stream, on sort
                    System.out.println(message); // afficher le message
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

            socket.close(); // Fermer la connexion
        } catch (UnknownHostException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println("Aucun serveur n’est rattaché au port");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
