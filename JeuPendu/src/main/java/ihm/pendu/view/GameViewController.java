package ihm.pendu.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import ihm.pendu.PenduFXapp;
import ihm.pendu.model.EtatPartie;
import ihm.pendu.model.JeuPendu;
import ihm.pendu.model.ResultatProposition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class GameViewController implements Initializable {

    private JeuPendu jeu;

    @FXML
    private Label labelMotCourant;

    @FXML
    private GridPane gridButtons;

    @FXML
    private Label labelRes;
    @FXML
    private Label textModeTriche;
    @FXML
    private Label nbErreurs;

    @FXML
    private ProgressBar vie;

    private Stage dialogStage;

    private List<Button> listButtons = new ArrayList<Button>();

    @FXML
    private Parent root;

    private static final AudioClip SON_ERREUR = new AudioClip(
            PenduFXapp.class.getResource("sounds/error.mp3").toExternalForm());

    private MediaPlayer musiqueFond;
    private MediaPlayer musiqueFin;
    private MainMenuController menu;

    private boolean sonFinActive = true;

    public void setDialogStage(Stage dialoStage) {
        this.dialogStage = dialoStage;
    }

    public void setMotCourant(char[] mot) {
        String motString = "";
        for (char c : mot) {
            motString += c;
        }
        this.labelMotCourant.setText(motString);
    }

    public void setListButtons() {
        for (Node node : this.gridButtons.getChildren()) {
            if (node instanceof Button but) {
                this.listButtons.add(but);
            }
        }
    }

    @FXML
    private void actionEcrireLettre(ActionEvent event) {
        if (event.getSource() instanceof Button but) {
            char lettre = but.getText().charAt(0);
            but.setDisable(true);
            ecrireLettre(lettre);
        }
    }

    @FXML
    public void actionEcrireLettreClavier(KeyEvent e) {
        char lettre = e.getCharacter().toUpperCase().charAt(0);
        if (lettre >= 'A' && lettre <= 'Z') {
            boolean[] lettresProposees = jeu.getLettresProposees();
            if (lettresProposees[lettre - 'A'] == false) {
                ecrireLettre(lettre);
                for (Node n : this.gridButtons.getChildren()) {
                    if (n instanceof Button b && b.getText().charAt(0) == lettre) {
                        b.setDisable(true);
                    }
                }
                this.gridButtons.getChildren();
            } else {
                this.labelRes.setText("ça ne sert à rien de choisir deux fois la même lettre.");
            }
        }
    }

    private void effetErreur() {
        String oldStyle = root.getStyle();
        root.setStyle("-fx-background-color: #ffb3b3;"); // rouge clair
        SON_ERREUR.play();
        new Thread(() -> {
            try {
                Thread.sleep(50); // 200 ms
            } catch (InterruptedException e) {
            }
            javafx.application.Platform.runLater(() -> root.setStyle(oldStyle));
        }).start();
    }

    private void ecrireLettre(char lettre) {
        try {
            ResultatProposition res = jeu.proposerLettre(lettre);
            setMotCourant(jeu.getMotCourant());
            double pv;
            if (jeu.getNbErreursMax() == 0) {
                pv = 1;
            } else {
                pv = 1 - (double) (jeu.getNbErreurs() * 100 / jeu.getNbErreursMax()) / 100;
            }
            switch (res) {
                case ResultatProposition.BIEN_JOUEE:
                    this.labelRes.setText("Bien joué !");
                    break;
                case ResultatProposition.DEJA_PROPOSEE:
                    this.labelRes.setText("Tu as déjà proposé cette lettre, choisis-en une autre !");
                    this.vie.setProgress(pv);
                    effetErreur();
                    break;
                case ResultatProposition.MAUVAIS_CHOIX:
                    this.labelRes.setText("Dommage, mauvais choix !");
                    this.vie.setProgress(pv);
                    effetErreur();
                    break;
                default:
                    System.out.println("Erreur dans ecrireLettre");
                    break;
            }
            if (jeu.getEtatPartie() == EtatPartie.GAGNEE
                    || jeu.getEtatPartie() == EtatPartie.PERDUE) {
                afficherPopupFXML(jeu.getEtatPartie());
            }
        } catch (Exception e) {
            System.out.println("Erreur dans ecrireLettre : " + e);
        }
    }

    private void afficherPopupFXML(EtatPartie etatPartie) {
        try {
            // Désactive la vue de jeu
            root.setDisable(true);

            // Arrête la musique de fond
            if (musiqueFond != null) {
                musiqueFond.stop();
            }

            Runnable afficherPopup = () -> {
                try {
                    // Choix de la musique selon le résultat
                    String musiquePopup = (etatPartie == EtatPartie.GAGNEE)
                            ? "sounds/gagne.mp3"
                            : "sounds/perdu.mp3";
                    MediaPlayer musiquePopupPlayer = new MediaPlayer(
                            new Media(PenduFXapp.class.getResource(musiquePopup).toExternalForm()));
                    musiquePopupPlayer.play();

                    FXMLLoader loader = new FXMLLoader(PenduFXapp.class.getResource("view/EndGamePopup.fxml"));
                    Parent popupRoot = loader.load();
                    EndGamePopupController ctrl = loader.getController();
                    Stage popupStage = new Stage();
                    ctrl.setStage(popupStage);
                    ctrl.setData(jeu, etatPartie);

                    Scene scene = new Scene(popupRoot);
                    scene.getStylesheets().setAll(this.dialogStage.getScene().getStylesheets());
                    popupStage.setScene(scene);
                    popupStage.setTitle("Fin de partie");
                    popupStage.initOwner(dialogStage);
                    popupStage.showAndWait();

                    dialogStage.close();
                    this.menu.lancerMusique();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };

            if (sonFinActive) {
                // Joue la musique de fin de partie puis affiche la popup
                Media mediaFin = new Media(PenduFXapp.class.getResource("sounds/end-game.mp3").toExternalForm());
                musiqueFin = new MediaPlayer(mediaFin);
                musiqueFin.setOnEndOfMedia(() -> javafx.application.Platform.runLater(afficherPopup));
                musiqueFin.play();
            } else {
                // Affiche la popup immédiatement
                javafx.application.Platform.runLater(afficherPopup);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init(JeuPendu jeu, boolean modeTricheActive, MainMenuController menu) {
        this.jeu = jeu;
        this.menu = menu;
        if (modeTricheActive) {
            this.textModeTriche.setText("MOT : " + jeu.getMotATrouver());
        }
        this.setMotCourant(jeu.getMotCourant());
        setListButtons();

        // --- Musique de fond ---
        if (musiqueFond == null) {
            Media media = new Media(PenduFXapp.class.getResource("sounds/ocarina-of-time.mp3").toExternalForm());
            musiqueFond = new MediaPlayer(media);
            musiqueFond.setCycleCount(MediaPlayer.INDEFINITE); // boucle infinie
            musiqueFond.play();
        } else {
            musiqueFond.stop();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setSonFinActive(boolean actif) {
        this.sonFinActive = actif;
    }
}
