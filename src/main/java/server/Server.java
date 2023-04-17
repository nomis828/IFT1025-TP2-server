/**
 * Auteurs: Oussama Ben Sghaier
 * Équipe: Simon Darveau (20246880) et Jean-Emmanuel Chouinard (20246807)
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
     Constructeur de la classe Server. Initialise un objet ServerSocket sur le port spécifié et
     une liste d'handlers pour les événements en ajoutant la méthode handleEvents.
     @param port le port sur lequel le serveur doit écouter (dans notre cas, 1337).
     @throws IOException si une erreur d'entrée/sortie se produit lors de la création du socket.
     */
    public Server(int port) throws IOException {
        this.server = new ServerSocket(port, 1);
        this.handlers = new ArrayList<EventHandler>();
        this.addEventHandler(this::handleEvents);
    }

    /**
     Permet d'ajouter un évènement à la liste d'EventHandler
     @param h - EventHandler à ajouter à la liste d'EventHandler
     */
    public void addEventHandler(EventHandler h) {
        this.handlers.add(h);
    }

    /**
     La méthode alertHandlers notifie tous les EventHandler avec la commande et l'argument passés en paramètres.
     @param cmd La commande à notifier.
     @param arg L'argument à notifier.
     */
    private void alertHandlers(String cmd, String arg) {
        for (EventHandler h : this.handlers) {
            h.handle(cmd, arg);
        }
    }

    /**
     La méthode run lance le serveur et accepte les connexions des futurs clients.
     Elle établit aussi une connexion du flux d'entrée/sortie avec le client, écoute ses commandes et se déconnecte
     quand la communication est terminée.
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
     La méthode listen écoute en permanence les données entrantes à partir du flux d'entrée
     et appelle la fonction alertHandlers dépendamment de la commande reçue et de l'argument reçu.
     @throws IOException si une erreur se produit lors de la lecture de données à partir du flux d'entrée de l'objet.
     @throws ClassNotFoundException si la classe d'un objet sérialisé reçu n'a pas été trouvée.
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
     La méthode processCommandLine sépare la commande et l'argument envoyé au serveur.
     @param line La ligne de commande à traiter.
     @return Une paire contenant la commande et les arguments.
     */
    public Pair<String, String> processCommandLine(String line) {
        String[] parts = line.split(" ");
        String cmd = parts[0];
        String args = String.join(" ", Arrays.asList(parts).subList(1, parts.length));
        return new Pair<>(cmd, args);
    }

    /**
     La méthode disconnect déconnecte le client du serveur et ferme les streams d'input et d'output
     @throws IOException si une erreur se produit lors de la fermeture des flux ou de la connexion.
     */
    public void disconnect() throws IOException {
        objectOutputStream.close();
        objectInputStream.close();
        client.close();
    }

    /**
     La méthode handleEvents traite les événements en fonction de la commande et de l'argument fournis.
     Si la commande est "INSCRIRE", on appelle la méthode handleRegistration().
     Si la commande est "CHARGER", on appelle la méthode handleLoadCourses() avec l'argument fourni.
     @param cmd le nom de la commande à exécuter
     @param arg L'argument utilisé par la commande "CHARGER". C'est la session pour laquelle on veut récupérer la liste des cours
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

