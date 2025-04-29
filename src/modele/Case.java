package modele;

import java.awt.Component;
import java.util.Objects;
import java.util.Random;

public class Case {
    private static final Random rnd = new Random();
    private Environnement environnement;
    public boolean tempNextState;
    private boolean state;

    public boolean tempNextState() {
        return tempNextState;
    }

    public void setTempNextState(boolean tempNextState) {
        this.tempNextState = tempNextState;
    }
    public boolean getState() {
        return state;
    }

    public void setState(boolean _state) {
        state = _state;
    }

    public Case(Environnement _environnement) {
        this.environnement = _environnement;
    }

    public void rndState() {
        this.state = rnd.nextBoolean();
    }

    public void nextState() {
        // calcul de l'état suivant.
        // Utiliser la fonction getCase de Environnement

        // variable pour le nombre des voisines
        int compteur = 0;

        // calculer le nombre de voisines de la cellule
        for (Direction direction : Direction.values()) {
            // si la cellule existe (pas hors grille)
            if(this.environnement.getCase(this, direction) != null) {
                // on teste son état
                if(this.environnement.getCase(this, direction).getState()) {
                    compteur++; // incrémente si cellule est vivante
                } // sinon fait rien
            }
        }

        // si la cellule est vivante
        if(this.getState()) {
            // si seule, 1 voisine ou plus de 3 => morte
            if(compteur < 2 || compteur > 3) {
                this.tempNextState = false;
            } else {
                this.tempNextState = true;
            }
        } else {
            if(compteur == 3) {
                this.tempNextState = true;
            } else {
                this.tempNextState = false;
            }
        }
    }

    public void affectTempNextState() {
        this.state = this.tempNextState;
    }
}
