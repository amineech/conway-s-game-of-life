//version test

import modele.Environnement;
import modele.Ordonnanceur;
import vue_controleur.ButtonsController;
import vue_controleur.FenetrePrincipale;
import javax.swing.SwingUtilities;

/**
 *
 * @author frederic
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable(){
			public void run(){

				Environnement e = new Environnement(60, 60);

				FenetrePrincipale fenetre = new FenetrePrincipale(e);
				fenetre.setVisible(true);

				e.addObserver(fenetre);

				Ordonnanceur o = new Ordonnanceur(50, e, fenetre);
				o.start();

				// controlleur qui va gérer les cliques sur les boutons (cellules de grilles aussi ! on peut créer un autre controlleur pour ça peut être)
				ButtonsController controller = new ButtonsController(fenetre, o);
			}
		});
    }
}