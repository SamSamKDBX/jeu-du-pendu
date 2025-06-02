package ihm.pendu;

import ihm.pendu.model.JeuPendu;
import ihm.pendu.view.GameViewController;
import ihm.pendu.view.MainMenuController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PenduFXapp extends Application {

    private Stage primaryStage;
    private BorderPane root;
    private MainMenuController mainMenuController;

    public void loadMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(PenduFXapp.class.getResource("view/MainMenu.fxml"));

            BorderPane vueMainMenu = loader.load();

            this.mainMenuController = loader.getController();
            // set du controller
            this.mainMenuController.init(this);
            this.root.setCenter(vueMainMenu);
        } catch (Exception e) {
            System.out.println("Erreur dans loadMainMenu() : " + e);
            System.exit(1);
        }
    }

    // Modifie la signature pour accepter le paramètre sonFinActive
    public void showGameView(JeuPendu jeu, boolean modeTricheActive, boolean sonFinActive) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(PenduFXapp.class.getResource("view/GameView.fxml"));

            BorderPane gameViewPane = loader.load();

            Scene scene = new Scene(gameViewPane);

            scene.getStylesheets().setAll(primaryStage.getScene().getStylesheets());

            Stage dialogStage = new Stage();

            dialogStage.setScene(scene);
            dialogStage.setTitle("Jeu du Pendu");
            dialogStage.setMinHeight(400);
            dialogStage.setMinWidth(450);
            dialogStage.initOwner(this.primaryStage);
            dialogStage.initModality(Modality.WINDOW_MODAL);

            GameViewController gameViewController = loader.getController();
            gameViewController.init(jeu, modeTricheActive, this.mainMenuController);
            gameViewController.setDialogStage(dialogStage);
            gameViewController.setSonFinActive(sonFinActive); // <-- Ajoute cette ligne

            dialogStage.showAndWait();
        } catch (Exception e) {
            System.out.println("Erreur dans showGameView() : " + e);
            System.exit(1);
        }
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.root = new BorderPane();

        Scene scene = new Scene(this.root);
        scene.getStylesheets().add(PenduFXapp.class.getResource("style-dark.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Jeu du pendu");

        loadMainMenu();

        primaryStage.show();
    }

    public static void main2(String[] args) {
        Application.launch(args);
    }

}