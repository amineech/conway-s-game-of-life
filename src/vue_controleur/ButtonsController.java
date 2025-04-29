package vue_controleur;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import modele.Case;
import modele.Ordonnanceur;
import vue_controleur.Components.Buttons;
import vue_controleur.Components.CustomButton;
import vue_controleur.Components.Buttons.ClearButton;

public class ButtonsController {

    /*
     * dans ce controlleur on doit récupérer la liste des boutons et assigner les ActionListener à ces boutons pour le traiter ici
     * 
     */

    FenetrePrincipale fenetrePrincipale;
    Ordonnanceur ordonnanceur;
    boolean isMousePressed; // pour dessiner sur la grille en cliquant et passant la souris (mouse drag)
    String lasLloadedFile = null;

    public ButtonsController(FenetrePrincipale _fenetrePrincipale, Ordonnanceur _ordonnanceur) {
        this.fenetrePrincipale = _fenetrePrincipale;
        this.ordonnanceur = _ordonnanceur;

        // méthodes qui attache les listener aux composants graphiques
        ActionListenerManagerForButtons();
        ActionListenerManagerForGrid();
        ActionListenerManagerForModeMenuItems();
        ActionListenerManagerForFileMenuItems();
        isMousePressed = false;
    }

    private void ActionListenerManagerForButtons() {
        for(int i = 0; i < fenetrePrincipale.getButtonsPanel().getComponents().length; i++) {
            CustomButton button = (CustomButton)fenetrePrincipale.getButtonsPanel().getComponent(i);
            if(button instanceof Buttons.StartStopButton) {
                // gérer le clique sur le bouton start stop
                button.addActionListener(StartStopButtonListener((Buttons.StartStopButton)button));
            } else if(button instanceof Buttons.RestartButton) {
                // gérer le clique sur le bouton recommencer
                button.addActionListener(RestartButtonListener((Buttons.RestartButton)button));
            } else if (button instanceof Buttons.SpeedButton){
                button.addActionListener(SpeedButtonListener((Buttons.SpeedButton)button));
            } else if(button instanceof Buttons.ClearButton) {
                button.addActionListener(ClearButtonListener((Buttons.ClearButton)button));
            }
        }
        
    }
        
    // méthode pour gérer le clique sur l'une des cellules de la grille
    private void ActionListenerManagerForGrid() {
        for (int i = 0; i < fenetrePrincipale.getGriCells().length; i++) {
            for (int j = 0; j < fenetrePrincipale.getGriCells()[i].length; j++) {
                // le composant cellule dans l'interface graphique
                JPanel cellule = fenetrePrincipale.getGriCells()[i][j];
                // la case dans le tableau (côté logique)
                Case c = fenetrePrincipale.env.getTabCases()[i][j];
                // pour le composant JPanel, on utilise addMouseListener car ActionListener n'existe pas pour ce type de composant
                cellule.addMouseListener(CellHoveredListener(cellule, c));
            }
        }
    }

    private void ActionListenerManagerForModeMenuItems(){
        for (int i=0; i<fenetrePrincipale.getModeMenu().getMenuComponentCount(); i++){
            JMenuItem mode = (JMenuItem)fenetrePrincipale.getModeMenu().getMenuComponent(i);
            mode.addActionListener(ModeMenuListener(mode));
        }
    }

    private void ActionListenerManagerForFileMenuItems() {
        for(int i = 0; i < fenetrePrincipale.getFileMenu().getMenuComponentCount(); i++) {
            JMenuItem fileChoice = (JMenuItem)fenetrePrincipale.getFileMenu().getMenuComponent(i);
            fileChoice.addActionListener(FileMenuListener(fileChoice));
        }
    }

    /*
     * chaque bouton / menu doit avoir un listener qui le traite
     * chaque cellule doit avoir un listener qui traite le clique sur la case
     */

    public ActionListener FileMenuListener(JMenuItem file) {
        return new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // on arrête l'ordonnanceur 
                ordonnanceur.pauseRunning();

                // sauvegarde ou chargement d'un fichier xml
                if(file.getText().equals("Charger")) {
                    lasLloadedFile = fenetrePrincipale.env.loadXMLState(fenetrePrincipale);
                    System.out.println(lasLloadedFile);
                    System.out.println("loaded");
                } else {
                    fenetrePrincipale.env.saveXMLState(fenetrePrincipale);
                    System.out.println("saved");
                }

                // relancer l'ordonnanceur
                ordonnanceur.resumeRunning();
            }
            
        };
    }

    public ActionListener ModeMenuListener(JMenuItem mode){
        return new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                // on retourne le parent de JMenuItem, après on retourne celui qui invoque le parent de JMenuItem, qui est Jmenu dans notre cas
                JMenu m = (JMenu)((JPopupMenu)mode.getParent()).getInvoker(); 
                
                if(mode.getText().equals("Aléatoire")){
                    ordonnanceur.pauseRunning();
                    fenetrePrincipale.env.setIsRandom(true);
                    fenetrePrincipale.env.setFirstRandomLaunch(true);
                    ordonnanceur.resumeRunning();
                    m.setText("Mode aléatoire");                    
                } else if (mode.getText().equals("Manuel")){
                    fenetrePrincipale.env.setIsRandom(false);
                    m.setText("Mode manuel");
                }
            }
        };
    }

    // méthode qui gère le changement de couleur / état lors de passage de la souris sur une cellule
    private MouseListener CellHoveredListener(JPanel cellule, Case c) {
        return new MouseListenerClass() {

            @Override
            public void mouseEntered(MouseEvent e) {
                if(isMousePressed) { // en restant appuyé sur la souris
                    if(cellule.getBackground().equals(Color.RED) && c.getState()) {
                        // en passant la souris sur une cellule, si en vie => tue 
                        cellule.setBackground(Color.WHITE);
                        c.setState(false);
                    } else {
                        // si mort => recréer la cellule à nouveau
                        cellule.setBackground(Color.RED);
                        c.setState(true);
                    }
                }
            }
            // vérifier l'état de la souris
            @Override
            public void mousePressed(MouseEvent e) {
                System.out.println("pressed");
                if(cellule.getBackground().equals(Color.RED) && c.getState()) {
                    // en passant la souris sur une cellule, si en vie => tue 
                    cellule.setBackground(Color.WHITE);
                    c.setState(false);
                } else {
                    // si mort => recréer la cellule à nouveau
                    cellule.setBackground(Color.RED);
                    c.setState(true);
                }
                isMousePressed = true;
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                System.out.println("released");
                isMousePressed = false;
            }

        };
    }

    private ActionListener ClearButtonListener(Buttons.ClearButton button) {
        return new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                fenetrePrincipale.env.setFirstRandomLaunch(true);//reinitialisation de la variable pour regenerer un environnement aleatoire
                for (int i = 0; i < fenetrePrincipale.getGriCells().length; i++) {
                    for (int j = 0; j < fenetrePrincipale.getGriCells()[i].length; j++) {
                        JPanel cellule = fenetrePrincipale.getGriCells()[i][j];
                        cellule.setBackground(Color.WHITE);
                        fenetrePrincipale.env.getTabCases()[i][j].setState(false);
                    }
                }
                for(int i = 0; i < fenetrePrincipale.getButtonsPanel().getComponents().length; i++) {
                    CustomButton b = (CustomButton)fenetrePrincipale.getButtonsPanel().getComponent(i);
                    if(b instanceof Buttons.StartStopButton) {
                        // faire reset au bouton start
                        b.setText("Start");
                        break;
                    } 
                }
            }
            
        };
    }

    // méthode qui génére un environnement vide (tuer toutes les cellules)
    private ActionListener RestartButtonListener(Buttons.RestartButton button) {
        return new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String localLastLoadedFile = lasLloadedFile;
                fenetrePrincipale.env.setFirstRandomLaunch(true);//reinitialisation de la variable pour regenerer un environnement aleatoire
                for(int i = 0; i < fenetrePrincipale.getButtonsPanel().getComponents().length; i++) {
                    CustomButton b = (CustomButton)fenetrePrincipale.getButtonsPanel().getComponent(i);
                    if(b instanceof Buttons.StartStopButton) {
                        // faire reset au bouton start
                        b.setText("Start");
                        break;
                    } 
                }

                // si on a chargé aucun fichier on vide l'environnement sinon on dessine l'env du dernier fichier chargé
                if(localLastLoadedFile == null) {
                    for (int i = 0; i < fenetrePrincipale.getGriCells().length; i++) {
                        for (int j = 0; j < fenetrePrincipale.getGriCells()[i].length; j++) {
                            JPanel cellule = fenetrePrincipale.getGriCells()[i][j];
                            cellule.setBackground(Color.WHITE);
                            fenetrePrincipale.env.getTabCases()[i][j].setState(false);
                        }
                    }
                } else {
                    // arrêter l'ord, charger le dernier fichier à nouveau, libérer l'ord 
                    ordonnanceur.pauseRunning();
                    fenetrePrincipale.env.loadLastXmlFile(localLastLoadedFile);
                    fenetrePrincipale.updateView();
                    ordonnanceur.resumeRunning();
                }
            }
            
        };
    }

    private ActionListener SpeedButtonListener(Buttons.SpeedButton button){
        return new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                if(button.getText().equals("+ vitesse")){
                    ordonnanceur.increaseSpeed();
                } else if(button.getText().equals("- vitesse")){
                    ordonnanceur.decreaseSpeed();
                }   
            }

        };
    }
    
    // la méthode qui va écouter et gérer les clique sur notre bouton start stop 
    private ActionListener StartStopButtonListener(Buttons.StartStopButton button) {
        return new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                
                // le bouton qui a été cliqué est récupérable par e.getSource() 
                // getSource() retourne un type Object,il faut faire le cast 
                
                if(button.getText().equals("Start")) {
                    button.setText("Stop");
                    button.setGameOn(true);
                    ordonnanceur.resumeRunning();
                } else if(button.getText().equals("Stop")){
                    button.setText("Start");
                    button.setGameOn(false);
                    ordonnanceur.pauseRunning();
                }
            }
        };
    }
}
