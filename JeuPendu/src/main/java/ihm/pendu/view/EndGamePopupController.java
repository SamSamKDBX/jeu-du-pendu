package ihm.pendu.view;

import ihm.pendu.model.EtatPartie;
import ihm.pendu.model.JeuPendu;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class EndGamePopupController {
    @FXML
    private Label labelMot;
    @FXML
    private Label labelScore;
    @FXML
    private Label labelMessage; // Nouveau label pour le message de victoire/défaite

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setData(JeuPendu jeu, EtatPartie etat) {
        labelMot.setText("Le mot était : " + jeu.getMotATrouver());
        if (etat == EtatPartie.GAGNEE) {
            labelMessage.setText("Bien joué, tu as gagné la partie !");
            if (jeu.getNbErreursMax() != 0) {
                labelScore.setText("Score : " + (jeu.getNbErreursMax() - jeu.getNbErreurs()) + " pts");
            } else {
                labelScore.setText(null);
            }
        } else {
            labelMessage.setText("Dommage, tu as perdu !\nretente ta chance !");
            labelScore.setText("");
        }
    }

    @FXML
    private void onRetourMenu() {
        if (stage != null)
            stage.close();
    }
}