package vue_controleur.Components;

import java.awt.Color;

import modele.Ordonnanceur;

public class Buttons {
    
    /*
     * -les classes représentant les boutons de l'interface
     * -Une seul classe regroupant les classes static des boutons pour éviter de créer plusieurs fichiers et factriser plus le code 
    */

    public static class ClearButton extends CustomButton {
    
        public ClearButton(String buttonTextValue, Color fontColor, Color backgroundColor) {
            super(buttonTextValue, fontColor, backgroundColor);
        }
    
    }

    public static class RestartButton extends CustomButton {

        public RestartButton(String buttonTextValue, Color fontColor, Color backgroundColor) {
            super(buttonTextValue, fontColor, backgroundColor);
        }
    
    }

    public static class SpeedButton extends CustomButton {
        public SpeedButton(String buttonTextValue, Color fontColor, Color backgroundColor) {
            super(buttonTextValue, fontColor, backgroundColor);
        }
        
    }

    public static class StartStopButton extends CustomButton  {

        private boolean isGameOn = true;
        
        Ordonnanceur ordonnanceur;
        public boolean isGameOn() {
            return isGameOn;
        }
        public void setGameOn(boolean isGameOn) {
            this.isGameOn = isGameOn;
        }

        public StartStopButton(String buttonTextValue, Color fontColor, Color backgroundColor) {
            super(buttonTextValue, fontColor, backgroundColor);
            // this.addActionListener(this.ActionListenerForButtons());
            this.setGameOn(false);
        }
    }

}
