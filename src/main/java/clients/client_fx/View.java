package clients.client_fx;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class View extends Application {
    private static Controller controller;
    private static TableView tableView = new TableView();
    private final int width = 595;
    private final int height = 500;

    public static void main(String[] args) {
        View.launch(args);
    }
    @Override
    public void start(Stage primaryStage) {
        try {
            HBox root = new HBox();
            Scene scene = new Scene(root, width, height);
            root.setBackground(new Background(
                    new BackgroundFill(Color.BEIGE, CornerRadii.EMPTY, Insets.EMPTY)));

            VBox paneauGauche = new VBox();
            paneauGauche.setSpacing(5);
            paneauGauche.setAlignment(Pos.TOP_CENTER);
            paneauGauche.setPadding(new Insets(10,10,10,10));

            // Tableau TODO: Implementer la fonction qui affichera les cours
            Label labelCours = new Label("Liste des cours");
            labelCours.setFont(new Font("Arial", 20));
            labelCours.setAlignment(Pos.CENTER);
            labelCours.setPadding(new Insets(5,5,5,5));

            TableColumn code = new TableColumn("Code");
            code.setMinWidth(50);
            TableColumn cours = new TableColumn("Cours");
            cours.setMinWidth(250);

            tableView.setEditable(false);
            tableView.getColumns().addAll(code, cours);

            // Section du selecteur de session
            HBox selecteurSession = new HBox();
            selecteurSession.setSpacing(50);
            selecteurSession.setAlignment(Pos.CENTER);

            ChoiceBox<String> choiceBox = new ChoiceBox();
            ObservableList<String> session = choiceBox.getItems();
            session.add("Hiver");
            session.add("Été");
            session.add("Automne");
            choiceBox.setMinSize(100,0);

            Button charger = new Button("Charger");
            charger.setMinSize(100,0);
            // TODO: Charger la liste de cours quand on clique sur le bouton

            selecteurSession.getChildren().addAll(choiceBox, charger);

            Separator separatorH = new Separator();
            separatorH.setMinHeight(10);
            paneauGauche.getChildren().addAll(labelCours, tableView, separatorH, selecteurSession);

            // Section du formulaire d'inscription
            VBox panneauDroite = new VBox();
            panneauDroite.setSpacing(10);
            panneauDroite.setAlignment(Pos.TOP_CENTER);
            panneauDroite.setPadding(new Insets(10,10,10,10));

            Label labelForm = new Label("Formulaire d'inscription");
            labelForm.setFont(new Font("Arial", 20));
            labelForm.setAlignment(Pos.CENTER);
            labelForm.setPadding(new Insets(5,5,5,5));

            Insets fieldInsets = new Insets(10,10,10,10);
            TextField prenom = new TextField();
            prenom.setPromptText("Prénom");
            TextField nom = new TextField();
            nom.setPromptText("Nom");
            TextField email = new TextField();
            email.setPromptText("Courriel");
            TextField matricule = new TextField();
            matricule.setPromptText("Matricule");
            prenom.setPadding(fieldInsets);
            nom.setPadding(fieldInsets);
            email.setPadding(fieldInsets);
            matricule.setPadding(fieldInsets);

            Button envoyer = new Button("Envoyer");
            envoyer.setMinSize(100,0);
            // TODO: Envoyer les informations au server et inscrire (voir video exemple)

            panneauDroite.getChildren().addAll(labelForm, prenom, nom, email, matricule, envoyer);

            // Afficher la fenetre
            Separator separatorV = new Separator();
            separatorV.setOrientation(Orientation.VERTICAL);
            root.getChildren().addAll(paneauGauche, separatorV, panneauDroite);
            primaryStage.setTitle("Inscription UdeM");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
