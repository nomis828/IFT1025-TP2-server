/**
 * Auteur: Oussama Ben Sghaier
 */

package server;

/**
 * Permet de lancer un serveur avec un port spécifique.
 */
public class ServerLauncher {
    /**
     * Le numéro de port sur lequel un client peut envoyer des requêtes
     */
    public final static int PORT = 1337;

    /**
     * Crée et démarre le serveur
     * @param args String[]
     */
    public static void main(String[] args) {
        Server server;
        try {
            server = new Server(PORT);
            System.out.println("Server is running...");
            server.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}