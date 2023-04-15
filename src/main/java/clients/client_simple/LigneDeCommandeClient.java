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

    /**
     Démarre le portail d'inscription de cours de l'UdeM et affiche le menu principal. Après le choix du client,
     afficherMenuSecondaire est appelé et on close le scanner à la toute fin.
     */

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

    /**
     La méthode afficherMenuSecondaire affiche le menu secondaire permettant de consulter les cours pour une autre session,
     de s'inscrire à un cours ou de quitter l'application.
     <p>Si l'utilisateur entre 1, le menu principal est affiché à nouveau en appelant la méthode afficherMenuPrincipal(). </p>
     <p>Si l'utilisateur entre 2, la méthode Inscription() est appelée pour gérer l'inscription à un cours. </p>
     <p>Si l'utilisateur entre 3, le client quitte le portail d'inscription.</p>
     Un message d'erreur est affiché et le menu est affiché à nouveau tant et aussi longtemps que le client n'a pas
     fait de choix valide.
     */
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
            Inscription();
        } else if (choix.equals("3")) {
            System.out.println("Au revoir!");
            return;
        } else {
            System.out.println("Oups! Veuillez entrer 1, 2 ou 3");
        }

        afficherMenuSecondaire();
    }

    /**
     La méthode chargerCoursSession charge la liste des cours offerts pour une session donnée en communiquant avec
     le serveur.
     @param session la session pour laquelle on veut charger les cours
     */
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

    /**
     La fonction choisirSession permet à l'utilisateur de choisir une session.
     @return la session choisie par l'utilisateur.
     */
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

    /**
     La méthode validerCours permet de valider si un cours est dans la liste des cours offerts pour une session donnée.
     @param code le code du cours à valider
     @return vrai si le cours est dans la liste, faux s'il n'est pas dans la liste.
     */
    private boolean validerCours(String code) {
        for (Course course : listeDeCours) {
            if (course.getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }

    /**
     La méthode validerEmail permet de valider si l'email entré par l'utilisateur est valide.
     @param email email à valider
     @return vrai s'il est valide, faux s'il ne l'est pas
     */
    private boolean validerEmail(String email) {
        return email.matches("[-A-Za-z0-9!#$%&'*+/=?^_`{|}~]+(?:\\.[-A-Za-z0-9!#$%&'*+/=?^_`{|}~]+)*@(?:[A-Za-z0-9](?:[-A-Za-z0-9]*[A-Za-z0-9])?\\.)+[A-Za-z0-9](?:[-A-Za-z0-9]*[A-Za-z0-9])?");
    }

    /**
     La méthode validerMatricule permet de valider si le matricule entré par l'utilisateur est valide.
     @param matricule matricule à valider
     @return vrai s'il est valide, faux s'il ne l'est pas
     */
    private boolean validerMatricule(String matricule){
        return matricule.matches("^[0-9]{8}$");
    }

    /**
     La méthode inscription permet à un utilisateur de s'inscrire à un cours en se connectant au serveur.
     <p>On demande le prénom, le nom, l'email, la matricule ainsi que le code du cours.</p>
     <p>On vérifie le code du cours en appelant la méthode "validerEmail".</p>
     <p>On vérifie le code du cours en appelant la méthode "validerMatricule".</p>
     <p>On vérifie le code du cours en appelant la méthode "validerCours".</p>
     <p>Si le code de cours est valide, on inscrit l'utilisateur au cours choisi en modifiant le fichier inscription.txt</p>
     Si le code de cours n'est pas valide, un message d'erreur est affiché.
     */
    private void Inscription() {
        try {
            System.out.print("\nVeuillez saisir votre prénom: ");
            String prenom = scanner.nextLine();
            System.out.print("Veuillez saisir votre nom: ");
            String nom = scanner.nextLine();
            System.out.print("Veuillez saisir votre email: ");
            String email = scanner.nextLine();

            // Vérification de la validité du mail
            if (!validerEmail(email)) {
                System.out.println("Oups! Le courriel entré est invalide (format accepté: prenom.nom@umontreal.ca).");
                return;
            }

            System.out.print("Veuillez saisir votre matricule: ");
            String matricule = scanner.nextLine();

            // Vérification de la validité du matricule
            if (!validerMatricule(matricule)) {
                System.out.println("Oups! Le matricule entré est invalide (format accepté: 12345678).");
                return;
            }
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
                return;
            } else {
                System.out.print("Félicitations! Inscription réussie de " + prenom + " au cours " + code + ".\n");
            }

            Socket clientSocket = new Socket("127.0.0.1", 1337);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(clientSocket.getInputStream());

            RegistrationForm inscription = new RegistrationForm(prenom, nom, email, matricule, cours);

            objectOutputStream.writeObject("INSCRIRE");
            objectOutputStream.writeObject(inscription);

            objectInputStream.close();
            objectOutputStream.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
