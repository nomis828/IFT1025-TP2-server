package clients.client_fx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import server.models.Course;

import java.util.ArrayList;

public class Controller {
    private Model model;
    private View view;

    /**
     Constructeur de la classe Controller.
     @param m Model
     @param v View
     */
    public Controller(Model m,  View v) {
        this.model = m;
        this.view = v;
    }

    /**
     La méthode chargerCours charge la liste des cours pour une session donnée.
     @param session la session pour laquelle on veut charger les cours.
     */
    public void chargerCours(String session) {
        ArrayList<Course> listeDeCours = this.model.chargerCoursSession(session);
        updateTable(listeDeCours);
    }

    /**
     La méthode updateTable met à jour les données du tableau avec la liste des cours.
     @param listeDeCours La liste de cours à afficher dans la table.
     */
    private void updateTable(ArrayList<Course> listeDeCours) {
        ObservableList<Course> data = FXCollections.observableArrayList();
        data.addAll(listeDeCours);
        view.getTableView().setItems(data);
    }

    /**
     La méthode inscriptionCours permet d'inscrire l'utiilsateur avec un formulaire et un cours.
     Elle affiche aussi une fenêtre (popup) en appelant doPopup.
     @param formulaire un tableau de chaînes de caractères contenant les informations du formulaire
     @param course le cours choisi
     */
    public void inscriptionCours(String[] formulaire, Course course) {
        ArrayList<String> resultat = model.inscription(formulaire, course);
        doPopup(resultat, formulaire, course);
        model.getErreursMessage().clear();
    }

    /**
     La méthode doPopup affiche une fenêtre (popup) différente dépendamment de si l'inscription a été réussie ou s'il
     y a eu des erreurs dans les champs nécessitant un certain format de réponse de la part de l'utilisateur.
     @param resultat liste avec le résultat de l'inscription
     @param formulaire liste avec les informations du formulaire d'inscription
     @param course le cours choisi
     */
    private void doPopup(ArrayList<String> resultat, String[] formulaire, Course course) {
        if (resultat.isEmpty()) {
            view.popupInscriptionReussie(formulaire, course);
        } else {
            view.popupInscriptionErreur(resultat);
        }
    }
}
