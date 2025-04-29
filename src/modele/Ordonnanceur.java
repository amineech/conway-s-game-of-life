package modele;

import static java.lang.Thread.*;
import vue_controleur.FenetrePrincipale;
import vue_controleur.Components.Buttons;
import vue_controleur.Components.CustomButton;

public class Ordonnanceur extends Thread {

    private long sleepTime;
    private Runnable runnable;

    private boolean isRunning = true;
    private boolean isPaused = false;
    private FenetrePrincipale fenetre;

    public Ordonnanceur(long _sleepTime, Runnable _runnable, FenetrePrincipale f) {
        sleepTime = _sleepTime;
        runnable = _runnable;
        fenetre = f;

    }

    public void run() {
        /*
         * utiliser une variable au lieu de true dans la boucle while nous permet de faire des méthodes pour
         *  arrêter et/ou relancer l'ordonnanceur et tester si l'ordonnancer est lancé ou en arrêt
         */
        while (isRunning) {
            
            // lancer la vue (grille vide au début) à l'éxecution d'ordonnanceur
             if(fenetre.getIsFirstLunch()) { 
                // avec cette condition, on éxecute updateView() ici seulement une fois au lancement du jeu
                // après ça sera éxecutée  dans la méthode update() de FentrePrincipale
                // on dessine la grille vide
                fenetre.updateView();
                fenetre.setIsFirstLunch(false);
            }

            synchronized(this){
                if(isPaused){
                    try{
                        wait();//on suspend l'execution du thread
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }

            // lancer run() seulement si l'utilisateur à appuyer sur le bouton Start  
            for(int i = 0; i < fenetre.getButtonsPanel().getComponents().length; i++) {
                CustomButton b = (CustomButton)fenetre.getButtonsPanel().getComponent(i);
                if(b instanceof Buttons.StartStopButton && b.getText().equals("Stop")) {
                    runnable.run();
                    break;
                } 
            }
            
            try {
                sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    //pour mettre en pause l'ordonnanceur
    //et bloquer l'accès simultane par d'autres threads
    public synchronized void pauseRunning(){
        isPaused = true;
        notifyAll();
    }

    //pour reprendre l'ordonnanceur
    //et reveiller les threads en attente
    public synchronized void resumeRunning(){
        isPaused = false;
        notifyAll();
    }

    //pour changer la vitesse de l'ordonnanceur
    public void changeSpeed(long newSleepTime){
        sleepTime = newSleepTime;
    }

    public long getSleepTime() {
        return sleepTime;
    }

    public void increaseSpeed() {
        // reduit la valeur de sleepTime donc augmente la vitesse
        long newSleepTime = this.getSleepTime() - 50;
        if (newSleepTime > 0) {
            this.changeSpeed(newSleepTime);
        }
        System.out.println("Sleeptime: " + newSleepTime);
    }
    
    public void decreaseSpeed() {
        // augmente la valeur de sleepTime donc reduit la vitesse
        long newSleepTime = this.getSleepTime() + 50;
        this.changeSpeed(newSleepTime);
        System.out.println("Sleeptime: " + newSleepTime);
    }

}
