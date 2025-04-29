package vue_controleur;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public abstract class MouseListenerClass implements MouseListener{

    /*
     * on a créer cette classe qui implémente MouseListener pour éviter
     * de réecrire toutees les méthodes de l'interface même si on ne veut
     * pas les utiliser toutes 
     */

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        
    }

}
