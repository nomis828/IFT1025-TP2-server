/**
 * Auteurs: Oussama Ben Sghaier et Simon Darveau
 */

package server;

import javafx.util.Pair;
import server.models.Course;
import server.models.RegistrationForm;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Classe qui représente le serveur. S'occupe de gérer les requêtes du client et d'exécuter des méthodes suite à
 * l'obtention de commande et d'argument.
 * <p>Les commandes sont : </p>
 * - INSCRIRE: Permet au client de s'inscrire à un cours <br>
 * - CHARGER: Permet au client de récupérer la liste de cours pour une session donnée.
 */
public class Server {

    /**
     * La commande qui permet au client de s'inscrire à un cours
     */
    public final static String REGISTER_COMMAND = "INSCRIRE";
    /**
     * La commande qui permet au client de charger la liste de cours pour une session donnée
     */
    public final static String LOAD_COMMAND = "CHARGER";
    private final ServerSocket server;
    private Socket client;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private final ArrayList<EventHandler> handlers;

    /**
     * Instancie le serveur (constructor).
     * @param port int le numéro du port auquel se connecter
     * @throws IOException
     */
    public Server(int port) throws IOException {
        this.server = new ServerSocket(port, 1);
        this.handlers = new ArrayList<EventHandler>();
        this.addEventHandler(this::handleEvents);
    }

    /**
     * Permet d'ajouter un évènement à la liste d'EventHandler
     * @param h - EventHandler à ajouter à la liste d'EventHandler
     */
    public void addEventHandler(EventHandler h) {
        this.handlers.add(h);
    }

    private void alertHandlers(String cmd, String arg) {
        for (EventHandler h : this.handlers) {
            h.handle(cmd, arg);
        }
    }

    /**
     * Démarre et fait fonctionner le server.
     */
    public void run() {
        while (true) {
            try {
                client = server.accept();
                System.out.println("Connecté au client: " + client);
                objectInputStream = new ObjectInputStream(client.getInputStream());
                objectOutputStream = new ObjectOutputStream(client.getOutputStream());
                listen();
                disconnect();
                System.out.println("Client déconnecté!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Permet au serveur d'écouter les requêtes du client
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void listen() throws IOException, ClassNotFoundException {
        String line;
        if ((line = this.objectInputStream.readObject().toString()) != null) {
            Pair<String, String> parts = processCommandLine(line);
            String cmd = parts.getKey();
            String arg = parts.getValue();
            this.alertHandlers(cmd, arg);
        }
    }

    /**
     * Sépare la commande et l'argument envoyer au serveur.
     * @param line String : la commande et l'argument
     * @return objet Pair contenant la commande à exécuter (String) et les arguments de la commande (String)
     */
    public Pair<String, String> processCommandLine(String line) {
        String[] parts = line.split(" ");
        String cmd = parts[0];
        String args = String.join(" ", Arrays.asList(parts).subList(1, parts.length));
        return new Pair<>(cmd, args);
    }

    /**
     * Déconnecte le client du serveur et ferme les streams d'imput et d'output
     * @throws IOException
     */
    public void disconnect() throws IOException {
        objectOutputStream.close();
        objectInputStream.close();
        client.close();
    }

    /**
     * Permet au serveur de gérer et d'exécuter les commandes qui lui sont envoyées par le client
     * @param cmd le nom de la commande a exécuter
     * @param arg utiliser par la commande "charger": la session pour laquelle on veut récupérer la liste des cours
     */
    public void handleEvents(String cmd, String arg) {
        if (cmd.equals(REGISTER_COMMAND)) {
            handleRegistration();
        } else if (cmd.equals(LOAD_COMMAND)) {
            handleLoadCourses(arg);
        }
    }

    /**
     Lire un fichier texte contenant des informations sur les cours et les transofmer en liste d'objets 'Course'.
     La méthode filtre les cours par la session spécifiée en argument.
     Ensuite, elle renvoie la liste des cours pour une session au client en utilisant l'objet 'objectOutputStream'.
     La méthode gère les exceptions si une erreur se produit lors de la lecture du fichier ou de l'écriture de l'objet dans le flux.
     @param arg la session pour laquelle on veut récupérer la liste des cours
     @throws IOException
     */
    public void handleLoadCourses(String arg) {
        ArrayList<Course> listeDeCours = new ArrayList<>();

        try {
            FileReader fr = new FileReader("data/cours.txt"); // ./src/main/java/server/
            BufferedReader reader = new BufferedReader(fr);

            String ligne;
            while ((ligne = reader.readLine()) != null) {
                String[] colonnes = ligne.split("\t");
                if (colonnes[2].equals(arg)) {
                    String code_du_cours = colonnes[0];
                    String nom_du_cours = colonnes[1];
                    String session = colonnes[2];
                    Course cours = new Course(nom_du_cours, code_du_cours, session);

                    listeDeCours.add(cours);
                }
            }
            reader.close();

            objectOutputStream.writeObject(listeDeCours);

        } catch (IOException ex) {
            System.out.println("Erreur à l'ouverture du fichier");
        }
    }

    /**
     Récupérer l'objet 'RegistrationForm' envoyé par le client en utilisant 'objectInputStream', l'enregistrer dans un fichier texte
     et renvoyer un message de confirmation au client.
     La méthode gére les exceptions si une erreur se produit lors de la lecture de l'objet, l'écriture dans un fichier ou dans le flux de sortie.
     @throws IOException
     @throws ClassNotFoundException
     */
    public void handleRegistration() {
        try {
            RegistrationForm inscription = (RegistrationForm) objectInputStream.readObject();

            BufferedWriter writer = new BufferedWriter(new FileWriter("./data/inscription.txt", true)); // ./src/main/java/server
            writer.write(inscription.getCourse().getSession() + "\t");
            writer.write(inscription.getCourse().getCode() + "\t");
            writer.write(inscription.getMatricule() + "\t");
            writer.write(inscription.getPrenom() + "\t");
            writer.write(inscription.getNom() + "\t");
            writer.write(inscription.getEmail() + "\n");

            writer.close();

        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("Erreur d'inscription");
        }
    }
}

