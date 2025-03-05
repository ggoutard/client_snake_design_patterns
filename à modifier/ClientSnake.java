package tp1progreseau;

import java.net.*;
import tp1progreseau.accueil.Accueil;
import tp1progreseau.controller.ControllerSnakeGameNetwork;
import tp1progreseau.utils.Commande;
import tp1progreseau.utils.FeaturesItem;
import tp1progreseau.utils.FeaturesSnake;
import tp1progreseau.utils.PanelBuilder;
import java.io.*;
import java.util.Observer;
import java.util.ArrayList;
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
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            	String chaine = "";
                while (!chaine.equals("stop")) {
                	 chaine = reader.readLine(); 
                     sortie.println(chaine); 
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void update(Observable o, Object arg) {
            if (arg instanceof Commande) {
                Commande commande = (Commande) arg;
                System.out.println(commande.toJson());
                sortie.println(commande.toJson());
            }
        }
        	
        public void updatePanel(String jsonContent) {
            try {
                PanelBuilder panelBuilder = PanelBuilder.fromJson(jsonContent);
            	System.out.println(jsonContent);
                if (panelBuilder != null) {
                	System.out.println(jsonContent);
                    ArrayList<FeaturesSnake> featuresSnakes = panelBuilder.getFeaturesSnakes();
                    ArrayList<FeaturesItem> featuresItems = panelBuilder.getFeaturesItems();
                    this.controller.update(featuresSnakes, featuresItems);
                }
               
            } catch (IllegalArgumentException | IOException e) {
                System.out.println("Error deserializing JSON: " + e.getMessage());
            }
        }

        
    }

    private static class ThreadOutput implements Runnable {
        private BufferedReader entree;
        private ThreadInput input;

        public ThreadOutput(BufferedReader entree, ThreadInput input) {
            this.entree = entree;
            this.input = input;
        }

        @Override
        public void run() {
            try {
                String message;
                while (true) {
                    message = entree.readLine();
                    this.input.updatePanel(message);
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
            
        	socket = new Socket(ip, p);
            sortie = new PrintWriter(socket.getOutputStream(), true);
            entree = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            ThreadInput input  = new ThreadInput(sortie);
            Thread inputThread = new Thread(input);
            inputThread.start();

            Thread outputThread = new Thread(new ThreadOutput(entree,input));
            outputThread.start();

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