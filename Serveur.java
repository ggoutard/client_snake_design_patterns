package tp1progreseau;

import java.net.*;
import java.awt.Panel;
import java.io.*;
import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import tp1progreseau.model.SnakeGame;
import tp1progreseau.utils.AgentAction;
import tp1progreseau.utils.Commande;
import tp1progreseau.utils.Message;
import tp1progreseau.utils.PanelBuilder;
import tp1progreseau.view.PanelSnakeGame;
import tp1progreseau.model.InputMap;
import tp1progreseau.gameElement.fabrique.TypeSnake;


public class Serveur {
    
	
	private static ArrayList<GameCore> cores;
    
    public static void main(String[] args) {
        int port = 2545;
        Serveur.cores = new ArrayList<>();
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            
        	System.out.println("Le serveur écoute sur le port " + port);
            
            while (true) 
            {
            	Socket clientSocket = serverSocket.accept();
            	Serveur.addClient(clientSocket);
            }
        } 
        
        catch (IOException e) {System.out.println("Erreur dans la tentative d'ouverture du port  : "+ e);}
    }
    
    public synchronized static void sendMessage(int identifiant, String message, PrintWriter sender) {
        synchronized (cores) {
            for (GameCore core : cores) 
            {

            	if(core.getHandlerHost().id() == identifiant)
            	{
                	core.getPrinterHost().println(message);
            	}
            	try {
	            	if(core.getHandlerNeighbor().id() == identifiant)
	            	{
	                	core.getPrinterNeighbor().println(message);
	            	}
            	}
            	catch (Exception e) {}
            	
            	
            }
        }
    }
    
    public synchronized static void removeClient(GameCore core) {
        synchronized (cores) {
        	cores.remove(core);
        }
    }
    
    public synchronized static void addClient(Socket clientSocket) {
    	try {
	    	
    		PrintWriter sortie = new PrintWriter(clientSocket.getOutputStream(), true);
	        BufferedReader entree = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	        
	        synchronized (cores) 
	        {
	        	GameCore core = new GameCore();
	        	core.init(sortie,new ClientHandler(core,clientSocket, entree, sortie));
	        	cores.add(core);
                Serveur.afficherEtatServeur();

	        }

	        System.out.println("Nouveau client connecté : " + clientSocket.getInetAddress());
	      
    	}
        catch (IOException e) {System.out.println("Erreur dans l'ajout du client : "+ e);}

    }
    
    
    
    public static  void findCommunParty()
    {
    	
    	System.out.println("Recherche de joueurs ...");
    	
    	for (int i = 0; i < Serveur.cores.size(); i++) {
    	    GameCore core1 = Serveur.cores.get(i);
    	    core1.updateMap();
    	    if (!core1.gameRunning()) {
    	        if (core1.gameSolo()) {
    	            core1.launchGame();
                    System.out.println("Partie lancée ");
    	            Serveur.afficherEtatServeur();
    	        } else {
    	            for (int j = 0; j < Serveur.cores.size(); j++) {
    	                if (i == j) continue; 
    	                GameCore core2 = Serveur.cores.get(j);
    	                if (!core2.gameRunning()) {
    	                    if (core1.isCompatible(core2)) {
    	                    	System.out.println("Joueur trouvé ...");
    	                        try {
    	                            core1.fusionGame(core2);
        	                        System.out.println("Partie lancée ");
    	                            Serveur.cores.remove(core2);
    	                            Serveur.afficherEtatServeur();
    	                            break;
    	                        } catch (Exception e) {
    	                            System.out.println(e);
    	                        }
    	                    }
    	                }
    	            }
    	        }
    	    }
    	}

	}
    
    public static void afficherEtatServeur() {
        System.out.println("\n### État du serveur ###\n");
        System.out.println("| Partie ID | Nombre de Joueurs | Carte                | En cours |");
        System.out.println("|-----------|-------------------|----------------------|----------|");

        for (GameCore core : cores) {
            String ligne = String.format(
                "| %-9d | %-17d | %-20s | %-8s |",
                core.Id(),
                core.getHost().size(),
                (core.getHandlerHost().getPath()!= null ? core.getHandlerHost().getPath() : "Non définie").replace("src/tp1progreseau/layouts/", "") ,
                core.gameRunning() ? "Oui" : "Non"
            );
            System.out.println(ligne);
        }

        System.out.println("\n"); 
    }



    
}