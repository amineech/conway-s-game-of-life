package vue_controleur.Components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;

public class CustomButton  extends JButton {    

    /*
     * cette classe contient les paramètres général d'une bouton et comment on veux l'afficher 
     * chaque bouton dans notre jeu va hériter de cette classe et ajoute d'autres fonctionnalités si besoin
     */
    public CustomButton(String buttonTextValue, Color fontColor, Color backgroundColor) {
        
        super(buttonTextValue);

        this.setBorder(new EmptyBorder(6, 15, 6, 15));
        this.setBackground(backgroundColor);
        this.setForeground(fontColor);
        this.setFont(new Font("Cambria", Font.BOLD, 16));
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.setFocusable(false);
        // centrer le texte dans le bouton
        this.setHorizontalAlignment(CENTER); 

        // centrer le bouton dans BoxLayout
        this.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.setAlignmentY(Component.CENTER_ALIGNMENT);
    }

}
