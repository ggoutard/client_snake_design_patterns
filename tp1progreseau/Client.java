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
                while (!chaine.equals("stop")) 
                {
                    chaine = reader.readLine(); 
                    sortie.println(chaine); 
                }
            } 
            catch (IOException e) {e.printStackTrace();}
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
        String ip = "127.0.0.1"; 
        int p = 2545; 

        try {
            socket = new Socket(ip, p);
            sortie = new PrintWriter(socket.getOutputStream(), true);
            entree = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Connecté au serveur (" + ip + ")" + "port : " +p);


            Thread inputThread = new Thread(new ThreadInput(sortie));
            inputThread.start();

            Thread outputThread = new Thread(new ThreadOutput(entree));
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