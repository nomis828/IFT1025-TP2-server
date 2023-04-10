package clients.client_fx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import server.models.Course;

import java.util.ArrayList;

public class Controller {
    private Model model;
    private View view;

    public Controller(Model m,  View v) {
        this.model = m;
        this.view = v;
    }

    public void chargerCours(String session) {
        ArrayList<Course> listeDeCours = this.model.chargerCoursSession(session);
        updateTable(listeDeCours);
    }

    private void updateTable(ArrayList<Course> listeDeCours) {
        ObservableList<Course> data = FXCollections.observableArrayList();
        data.addAll(listeDeCours);
        view.getTableView().setItems(data);
    }

    public void inscriptionCours(String[] formulaire, Course course) {
        ArrayList<String> resultat = model.inscription(formulaire, course);
        doPopup(resultat, formulaire, course);
        model.getErreursMessage().clear();
    }

    private void doPopup(ArrayList<String> resultat, String[] formulaire, Course course) {
        if (resultat.isEmpty()) {
            view.popupInscriptionReussie(formulaire, course);
        } else {
            view.popupInscriptionErreur(resultat);
        }
    }
}
