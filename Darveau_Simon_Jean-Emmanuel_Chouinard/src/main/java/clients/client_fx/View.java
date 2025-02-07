package clients.client_fx;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
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
import server.models.Course;

import java.util.ArrayList;


public class View extends Application {
    /**
     Crée le controlleur qui fait le lien entre la vue et le modèle
     */
    private final Controller controller = new Controller(new Model(), this);
    /**
     Tableau qui affichera la liste de cours pour une session donnée
     */
    private final TableView<Course> tableView = new TableView<>();
    /**
     Champ de texte du formulaire qui demande le prenom de l'étudiant
     */
    private final TextField prenom = new TextField();
    /**
     Champ de texte du formulaire qui demande le nom de l'étudiant
     */
    private final TextField nom = new TextField();
    /**
     Champ de texte du formulaire qui demande le courriel de l'étudiant
     */
    private final TextField email = new TextField();
    /**
     Champ de texte du formulaire qui demande le matricule de l'étudiant
     */
    private final TextField matricule = new TextField();
    /**
     Bordure des champs de texte et du tableau lors d'une erreur
     */
    private final Border errorBorder = new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID,
            CornerRadii.EMPTY, BorderWidths.DEFAULT));
    /**
     Bordure des champs de texte et du tableau lorsqu'il n'y a pas d'erreur
     */
    private final Border normalBorder = new Border(new BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID,
            CornerRadii.EMPTY, BorderWidths.DEFAULT));
    /**
     Largeur de l'écran
     */
    private final int width = 600;
    /**
     Hauteur de l'écran
     */
    private final int height = 500;

    /**
     La méthode run est utilisée pour lancer l'application. Elle utilise start pour créer et afficher l'interface.
     @param args les arguments de la ligne de commande
     */
    public static void main(String[] args) {
        View.launch(args);
    }

    /**
     La méthode start démarre l'application JavaFX en créant et en affichant la fenêtre principale,
     dans notre cas, le portail d'inscription de l'UdeM.
     @param primaryStage la fenêtre principale de l'application
     */
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

            Label labelCours = new Label("Liste des cours");
            labelCours.setFont(new Font("Arial", 20));
            labelCours.setAlignment(Pos.CENTER);
            labelCours.setPadding(new Insets(5,5,5,5));

            TableColumn<Course, String> code = new TableColumn("Code");
            code.setMinWidth(50);
            code.setCellValueFactory(cellData ->
                    new ReadOnlyStringWrapper(cellData.getValue().getCode()));
            TableColumn<Course, String> cours = new TableColumn("Cours");
            cours.setMinWidth(250);
            cours.setCellValueFactory(cellData ->
                    new ReadOnlyStringWrapper(cellData.getValue().getName()));

            tableView.setEditable(true);
            tableView.getColumns().addAll(code, cours);

            // Section du selecteur de session
            HBox selecteurSession = new HBox();
            selecteurSession.setSpacing(50);
            selecteurSession.setAlignment(Pos.CENTER);

            ChoiceBox<String> choiceBox = new ChoiceBox();
            ObservableList<String> session = choiceBox.getItems();
            session.add("Hiver");
            session.add("Ete");
            session.add("Automne");
            choiceBox.setMinSize(100,0);

            Button charger = new Button("Charger");
            charger.setMinSize(100,0);

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
            prenom.setPromptText("Prénom");
            nom.setPromptText("Nom");
            email.setPromptText("Courriel");
            matricule.setPromptText("Matricule");
            prenom.setPadding(fieldInsets);
            nom.setPadding(fieldInsets);
            email.setPadding(fieldInsets);
            matricule.setPadding(fieldInsets);

            Button envoyer = new Button("Envoyer");
            envoyer.setMinSize(100,0);

            panneauDroite.getChildren().addAll(labelForm, prenom, nom, email, matricule, envoyer);

            // Événements
            charger.setOnAction( (action) -> {
                controller.chargerCours(choiceBox.getValue());
            });
            envoyer.setOnAction( (action) -> {
                // Prendre les informations
                String fPrenom = prenom.getText();
                String fNom = nom.getText();
                String fEmail = email.getText();
                String fMatricule = matricule.getText();
                String[] formulaire = new String[]{fPrenom,fNom,fEmail,fMatricule};
                Course fCours = tableView.getSelectionModel().getSelectedItem();

                controller.inscriptionCours(formulaire,fCours);
            });

            // Afficher la fenetre
            Separator separatorV = new Separator();
            separatorV.setOrientation(Orientation.VERTICAL);
            root.getChildren().addAll(paneauGauche, separatorV, panneauDroite);
            primaryStage.setTitle("Inscription UdeM");
            primaryStage.setResizable(false);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     La méthode getTableView retourne la "tableView".
     @return tableView
     */
    public TableView getTableView() {
        return tableView;
    }

    /**
     La méthode popupInscriptionReussie affiche une fenêtre indiquant que l'inscription au cours a été réussie.
     @param formulaire les informations du formulaire d'inscription
     @param course le cours choisi
     */
    public void popupInscriptionReussie(String[] formulaire, Course course) {
        Stage popup = new Stage();
        popup.setResizable(false);
        popup.setTitle("Inscription Reussite!");
        VBox root = new VBox();

        // Reset l'affichage
        prenom.setBorder(normalBorder);
        nom.setBorder(normalBorder);
        email.setBorder(normalBorder);
        matricule.setBorder(normalBorder);
        tableView.setBorder(normalBorder);
        prenom.setText(null);
        nom.setText(null);
        email.setText(null);
        matricule.setText(null);

        // Top de la fenetre
        HBox top = new HBox();
        Label topLabel = new Label("Message");
        topLabel.setFont(new Font("Arial", 32));
        topLabel.setPadding(new Insets(20,20,20,20));
        topLabel.setAlignment(Pos.CENTER);

        top.getChildren().addAll(topLabel);

        Separator separator = new Separator();

        // Bas de la fenetre
        Label bottomText = new Label("Félicitation! " + formulaire[0] + " " + formulaire[1] +
                " est inscrit(e) avec succès au cours " + course.getCode() + "!");
        bottomText.setFont(new Font("Arial", 15));
        bottomText.setWrapText(true);
        bottomText.setPadding(new Insets(10,10,10,10));

        HBox bnt = new HBox();
        bnt.setAlignment(Pos.BOTTOM_RIGHT);
        Button bouton = new Button("OK");
        bouton.setMinSize(50,0);
        bouton.setOnAction( (action) -> {
            popup.close();
        });
        bnt.setPadding(new Insets(10,10,10,10));
        bnt.getChildren().addAll(bouton);


        root.getChildren().addAll(top, separator, bottomText, bnt);

        Scene stageScene = new Scene(root, 300,200);
        popup.setScene(stageScene);
        popup.show();
    }

    /**
     La méthode popupInscriptionErreur affiche une fenêtre indiquant que l'inscription au cours a échouée.
     On indique dans cette fenête la ou les raisons de l'échec d'inscription en montrant les formats acceptés
     pour le mail et le matricule.
     @param erreurs une liste des messages d'erreur à afficher.
     */
    public void popupInscriptionErreur(ArrayList<String> erreurs) {
        Stage popup = new Stage();
        popup.setResizable(false);
        popup.setTitle("Erreur d'inscription");
        VBox root = new VBox();

        // Reset l'affichage
        prenom.setBorder(normalBorder);
        nom.setBorder(normalBorder);
        email.setBorder(normalBorder);
        matricule.setBorder(normalBorder);
        tableView.setBorder(normalBorder);

        for (String erreur : erreurs) {
            switch (erreur) {
                case "Veuillez écrire votre prénom":
                    prenom.setBorder(errorBorder);
                    break;
                case "Veuillez écrire votre nom":
                    nom.setBorder(errorBorder);
                    break;
                case "Votre courriel est invalide (format accepté: prenom.nom@umontreal.ca)":
                    email.setBorder(errorBorder);
                    break;
                case "Votre matricule est invalide (format accepté: 12345678)":
                    matricule.setBorder(errorBorder);
                    break;
                case "Veuillez sélectionner un cours":
                    tableView.setBorder(errorBorder);
                    break;
            }
        }

        // Top de la fenetre
        HBox top = new HBox();
        Label topLabel = new Label("Erreur");
        topLabel.setFont(new Font("Arial", 32));
        topLabel.setPadding(new Insets(20,20,20,20));
        topLabel.setAlignment(Pos.CENTER);
        top.getChildren().addAll(topLabel);

        Separator separator = new Separator();

        VBox messages = new VBox();
        ArrayList<Label> textes = new ArrayList<>();
        for (String erreur : erreurs) {
            Label text = new Label(erreur);
            text.setFont(new Font("Arial", 12));
            text.setWrapText(true);
            text.setPadding(new Insets(2,2,2,2));
            textes.add(text);
        }

        messages.getChildren().addAll(textes);

        HBox bnt = new HBox();
        bnt.setAlignment(Pos.BOTTOM_RIGHT);
        Button bouton = new Button("OK");
        bouton.setMinSize(50,0);
        bouton.setOnAction( (action) -> {
            popup.close();
        });
        bnt.setPadding(new Insets(10,10,10,10));
        bnt.getChildren().addAll(bouton);

        root.getChildren().addAll(top, separator, messages, bnt);


        Scene stageScene = new Scene(root, 400,225);
        popup.setScene(stageScene);
        popup.show();
    }
}
