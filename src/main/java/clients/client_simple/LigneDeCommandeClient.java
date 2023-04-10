package clients.client_simple;

import server.models.Course;
import server.models.RegistrationForm;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class LigneDeCommandeClient {

    Scanner scanner = new Scanner(System.in);
    ArrayList<Course> listeDeCours = new ArrayList<>();

    // Fonction principale qui s'exécute initialement.

    public void run() {
        System.out.println("*** Bienvenue au portail d'inscription de cours de l'UDEM ***");

        afficherMenuPrincipal();

        afficherMenuSecondaire();

        scanner.close();
    }

    /**
     * La fonction afficherMenuPrincipal demande au client de choisir une session jusqu'à ce que celui-ci entre un
     * numéro qui correspond bel et bien à 1, 2 ou 3, représentant les 3 sessions possibles.
     */
    private void afficherMenuPrincipal() {
        String session;
        do{
            session = choisirSession();
        } while (session.isEmpty());

        chargerCoursSession(session);
    }

    // La méthode afficherMenuSecondaire
    private void afficherMenuSecondaire(){

        System.out.println("> Choix: ");
        System.out.println("1. Consulter les cours offerts pour une autre session");
        System.out.println("2. Inscription à un cours");
        System.out.println("3. Quitter");
        System.out.print("> Choix: ");
        String choix = scanner.nextLine();

        if (choix.equals("1")) {
            afficherMenuPrincipal();
        } else if (choix.equals("2")) {
            if (Inscription()) {
                return;
            }
        } else if (choix.equals("3")) {
            System.out.println("Au revoir!");
            return;
        } else {
            System.out.println("Oups! Veuillez entrer 1, 2 ou 3");
        }

        afficherMenuSecondaire();
    }

    // La méthode chargerCoursSession s'occupe de communiquer avec le serveur pour charger la liste de cours disponibles
    // pour la session choisie.
    private void chargerCoursSession(String session) {
        try {
            Socket clientSocket = new Socket("127.0.0.1", 1337);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(clientSocket.getInputStream());

            // Envoie au serveur la requête CHARGER
            objectOutputStream.writeObject("CHARGER " + session);

            listeDeCours = (ArrayList<Course>) objectInputStream.readObject();

            System.out.println("Les cours offerts pendant la session d'" + session + " sont:");

            // Affichage de la liste des cours
            int compteur = 0;
            for (Course course : listeDeCours) {
                compteur++;
                System.out.print(compteur + ". " + course.getCode() + "\t\t" + course.getName() + "\n");
            }

            objectInputStream.close();
            objectOutputStream.close();

        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    // La fonction choisirSession retourne la session choisie par l'utilisateur.
    private String choisirSession(){
        System.out.println("Veuillez choisir la session pour laquelle vous voulez consulter la liste des cours:");
        System.out.println("1. Automne");
        System.out.println("2. Hiver");
        System.out.println("3. Ete");
        System.out.print("> Choix: ");
        String input = scanner.nextLine();

        String session = "";
        switch (input) {
            case "1":
                session = "Automne";
                break;
            case "2":
                session = "Hiver";
                break;
            case "3":
                session = "Ete";
                break;
            default:
                System.out.println("Oups! Veuillez entrer 1,2 ou 3.");
                break;
        }
        return session;
    }

    // La fonction validerCours prend en paramètre un code de cours et vérifie qu'il correspond à l'un des cours offert
    // dans la session choisie par l'utilisateur.
    private boolean validerCours(String code) {
        for (Course course : listeDeCours) {
            if (course.getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }

    // La méthode inscription sert à inscrire l'utilisateur au cours valide qu'il choisira. Elle communique avec le
    // serveur pour modifier le fichier inscription.txt.
    private boolean Inscription() {
        try {
            Socket clientSocket = new Socket("127.0.0.1", 1337);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(clientSocket.getInputStream());

            System.out.print("\nVeuillez saisir votre prénom: ");
            String prenom = scanner.nextLine();
            System.out.print("Veuillez saisir votre nom: ");
            String nom = scanner.nextLine();
            System.out.print("Veuillez saisir votre email: ");
            String email = scanner.nextLine();
            System.out.print("Veuillez saisir votre matricule: ");
            String matricule = scanner.nextLine();
            System.out.print("Veuillez saisir le code du cours: ");
            String code = scanner.nextLine();

            // Vérification de la validité du code de cours
            Course cours = null;
            if (validerCours(code)) {
                for (Course course : listeDeCours) {
                    if (course.getCode().equals(code)) {
                        cours = course;
                        break;
                    }
                }
            }

            if (cours == null) {
                System.out.println("Oups! Le code du cours entré n'est pas valide.");
            } else {
                System.out.print("Félicitations! Inscription réussie de " + prenom + " au cours " + code + ".\n");
            }

            RegistrationForm inscription = new RegistrationForm(prenom, nom, email, matricule, cours);

            objectOutputStream.writeObject("INSCRIRE");
            objectOutputStream.writeObject(inscription);

            objectInputStream.close();
            objectOutputStream.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
