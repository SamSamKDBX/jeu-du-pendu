package ihm.pendu.view;

import java.net.URL;
import java.util.ResourceBundle;

import ihm.pendu.PenduFXapp;
import ihm.pendu.model.JeuPendu;
import ihm.pendu.model.JeuPenduBuilder;
import ihm.pendu.model.CategorieMot;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.RadioButton;
import javafx.util.converter.NumberStringConverter;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class MainMenuController implements Initializable {

    @FXML
    private CheckBox modeTriche;
    @FXML
    private Slider sliderErreurMax;
    @FXML
    private Slider sliderLettres;
    @FXML
    private Label nbErrMax;
    @FXML
    private Label nbLettres;
    @FXML
    private CheckMenuItem desactiverSonEndGame;

    @FXML
    private RadioButton rbJavaFX;
    @FXML
    private RadioButton rbAnimaux;
    @FXML
    private RadioButton rbFruits;
    @FXML
    private RadioButton rbCouleurs;
    @FXML
    private RadioButton rbPays;
    @FXML
    private RadioButton rbMetiers;
    @FXML
    private RadioButton rbToutes;
    @FXML
    private RadioMenuItem darkMode;
    @FXML
    private RadioMenuItem lightMode;

    private PenduFXapp app;
    private MediaPlayer musiqueMenu;

    @FXML
    private void actionNouvelPartie() {
        if (musiqueMenu != null) {
            musiqueMenu.stop();
        }
        CategorieMot categorie = getCategorieSelectionnee();
        JeuPendu jeu = JeuPenduBuilder.creer()
                .avecCategorie(categorie)
                .avecNbErreursMax((int) this.sliderErreurMax.valueProperty().get())
                .avecNombreLettres((int) this.sliderLettres.valueProperty().get())
                .construire();

        app.showGameView(jeu, this.modeTriche.isSelected(), isSonFinActive());
    }

    @FXML
    private void actionChangerTheme() {
        Scene scene = this.modeTriche.getScene();
        if (this.darkMode.isSelected()) {
            // this.lightMode.setSelected(false);
            scene.getStylesheets().clear();
            scene.getStylesheets().add(PenduFXapp.class.getResource("style-dark.css").toExternalForm());
        } else {
            // this.darkMode.setSelected(false);
            scene.getStylesheets().clear();
            scene.getStylesheets().add(PenduFXapp.class.getResource("style-light.css").toExternalForm());
        }
    }

    private CategorieMot getCategorieSelectionnee() {
        if (rbJavaFX.isSelected())
            return CategorieMot.JAVA_FX;
        if (rbAnimaux.isSelected())
            return CategorieMot.ANIMAUX;
        if (rbFruits.isSelected())
            return CategorieMot.FRUITS;
        if (rbCouleurs.isSelected())
            return CategorieMot.COULEURS;
        if (rbPays.isSelected())
            return CategorieMot.PAYS;
        if (rbMetiers.isSelected())
            return CategorieMot.METIERS;
        return CategorieMot.TOUTES;
    }

    public void lancerMusique() {
        // --- Musique de fond du menu ---
        Media media = new Media(PenduFXapp.class.getResource("sounds/menu.mp3").toExternalForm());
        musiqueMenu = new MediaPlayer(media);
        musiqueMenu.setCycleCount(MediaPlayer.INDEFINITE); // boucle infinie
        musiqueMenu.play();
    }

    public void init(PenduFXapp app) {
        this.app = app;
        this.nbErrMax.textProperty().bindBidirectional(this.sliderErreurMax.valueProperty(),
                new NumberStringConverter("#"));
        this.nbLettres.textProperty().bindBidirectional(this.sliderLettres.valueProperty(),
                new NumberStringConverter("#"));
        lancerMusique();
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // Force les sliders à ne prendre que des valeurs entières
        sliderErreurMax.valueProperty().addListener((obs, oldVal, newVal) -> {
            sliderErreurMax.setValue(newVal.intValue());
        });
        sliderLettres.valueProperty().addListener((obs, oldVal, newVal) -> {
            sliderLettres.setValue(newVal.intValue());
        });
    }

    public boolean isSonFinActive() {
        return !desactiverSonEndGame.isSelected();
    }
}
