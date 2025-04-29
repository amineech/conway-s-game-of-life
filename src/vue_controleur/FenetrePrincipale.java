package vue_controleur;/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import modele.Environnement;
import vue_controleur.Components.Buttons;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Observable;
import java.util.Observer;
import javax.swing.*;

import javax.swing.border.Border;


/**
 *
 * @author frederic
 */
public class FenetrePrincipale extends JFrame implements Observer {

    private JPanel[][] tab;
    Environnement env;
    public JPanel buttonsPanel; // utiliser pour le getter de JPanel des boutons qu'on va utiliser dans le controlleur
    public JMenu modeMenu; //utilisé pour le getter du Jmenu mode
    public JMenu FileMenu; // utilisé pour le getter du JMenu fichier
    private boolean isFirstLunch; // attribut qui nous permet de savoir si c'est le premier lancement du jeu (vitesse d'execution du jeu) 

    public boolean getIsFirstLunch() { return this.isFirstLunch ;}
    public void setIsFirstLunch(boolean _isFirstLunch) { this.isFirstLunch = _isFirstLunch; }

    public FenetrePrincipale(Environnement _env) {
        super();
        isFirstLunch = true;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        env = _env;
        build();
    }
    
    public void build() {
        
        setTitle("Jeu de la Vie");
        setSize(1000, 800);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Panneau principal
        JPanel pan = new JPanel(new BorderLayout());
        
        
        // Panneau central
        JComponent pan1 = new JPanel ();
        GridLayout gridlayout = new GridLayout(env.getSizeX(),env.getSizeY());
        gridlayout.setHgap(2);
        gridlayout.setVgap(2);
        pan1.setLayout(gridlayout);
        tab = new JPanel[env.getSizeX()][env.getSizeY()];

        Border blackline = BorderFactory.createLineBorder(Color.RED,1);
        pan1.setBorder(blackline);
        for(int i = 0; i<env.getSizeX();i++){
            for (int j = 0; j < env.getSizeY(); j++) {
                tab[i][j] = new JPanel();
                tab[i][j].setOpaque(true); // pour éviter les traces des couleurs des cellules mortes
                pan1.add(tab[i][j]);
            }

        }
        
        // Panneau pour les boutons
        Border blueline = BorderFactory.createLineBorder(Color.BLUE,1);
        JPanel pan2 = new JPanel(new FlowLayout(FlowLayout.CENTER,0, 20));
        pan2.setPreferredSize(new Dimension(150, (int)pan2.getPreferredSize().getHeight()));
        pan2.setBorder(blueline);
        pan2.add(new Buttons.StartStopButton("Start", Color.WHITE, Color.DARK_GRAY), BorderLayout.CENTER);
        pan2.add(new Buttons.RestartButton("Recharger", Color.WHITE, Color.MAGENTA), BorderLayout.CENTER);
        pan2.add(new Buttons.SpeedButton("+ vitesse", Color.WHITE, Color.GREEN), BorderLayout.CENTER);
        pan2.add(new Buttons.SpeedButton("- vitesse", Color.WHITE, Color.RED), BorderLayout.CENTER);
        pan2.add(new Buttons.ClearButton("Effacer", Color.BLACK, Color.YELLOW), BorderLayout.CENTER);

        buttonsPanel = pan2; 

        
        
        pan.add(pan1, BorderLayout.CENTER);
        pan.add(pan2, BorderLayout.EAST);
        
        setContentPane(pan);
        

        
        // Ajout Menu
        JMenuBar jm = new JMenuBar();
        JMenu file = new JMenu("Fichier");
        JMenuItem loadItem = new JMenuItem("Charger");
        JMenuItem saveItem = new JMenuItem("Enregistrer");

        JMenu mode = new JMenu("Mode manuel");
        JMenuItem modeAleat = new JMenuItem("Aléatoire");
        JMenuItem modeMan = new JMenuItem("Manuel");


        file.add(loadItem);
        file.add(saveItem);
        jm.add(file);
        mode.add(modeAleat);
        mode.add(modeMan);
        jm.add(mode);
        setJMenuBar(jm);

        this.FileMenu = file;
        this.modeMenu = mode;
        
        
    }

    // méthode qui retourne les items du fichier menu
    public JMenu getFileMenu() {
        return this.FileMenu;
    }

    //methode qui retourne les items du mode menu
    public JMenu getModeMenu() {
        return this.modeMenu;
    }

    // une méthode qui retourne le JPanel qui contient tous les boutons
    public JPanel getButtonsPanel() {
        return this.buttonsPanel;
    }

    // getter de tableau des cellules(les composants JPanel) de l'interface
    public JPanel[][] getGriCells() {
        return this.tab;
    }
    

    @Override
    public void update(Observable o, Object arg) {
        if (!env.getUseThreads()){
            env.calculNextStateInitial();
        } else {
            env.calculNextStateWithThreads();
        }
        // raffraîchissement de la vue
        updateView();

    }

    // méthode qui raffraîchit la vue (la grille) même au lancement du jeu (grille vide)
    public void updateView() {
        for(int i = 0; i<env.getSizeX();i++){
            for (int j = 0; j < env.getSizeY(); j++) {
                // affectation des états de la varaible temporaire à la varaiable réel des états des cellules
                env.getTabCases()[i][j].affectTempNextState();
                if (env.getState(i, j)) {
                    tab[i][j].setBackground(Color.RED);
                } else {
                    tab[i][j].setBackground(Color.WHITE);
                }
            }
        }
    }
}
