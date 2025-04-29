package modele;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import vue_controleur.FenetrePrincipale;

public class XMLParser {

    Environnement env;

    public XMLParser(Environnement _environnement) {
        this.env = _environnement;
    }

    // méthode qui charge dans la grille le dernier fichier chargé par l'utilisateur
    public void loadLastXmlFile(String filename) {
        File file = new File(System.getProperty("user.dir") + "/structures/" + filename);
        if(file.exists()) {
            file.getName();
            updateGridFromXMLFileValues(file);

        }
    } 

    // méthode qui charge l'état de la grille enregistré dans le fichier xml (s'il existe), retourne le nom du fichier
    public String loadXMLState(FenetrePrincipale fenetre) {
        try {

            // fenetre pour sélectionner un fichier
            JFileChooser fichier_selection = new JFileChooser();

            // que les fichiers xml
            FileNameExtensionFilter extension = new FileNameExtensionFilter("XML Files","xml");
            fichier_selection.setFileFilter(extension);

            // ouvrir la fenêtre de sélection sur le dossier du projet directement
            File dossier = new File(System.getProperty("user.dir") + "/structures");
            fichier_selection.setCurrentDirectory(dossier);

            if(fichier_selection.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                File file = fichier_selection.getSelectedFile();
               
                updateGridFromXMLFileValues(file);

                fenetre.updateView();
            }

            return fichier_selection.getSelectedFile().getName();
        } catch (Exception e) {
            System.out.println("exception: " + e.getMessage());
            return e.getMessage();
        }
    }

    // méthode qui enregistre l'état de la grille à un moment donnée
    public void saveXMLState(FenetrePrincipale fenetre) {
        try {

            // on crée le document avec le builder
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            DocumentBuilder b = f.newDocumentBuilder();
            Document doc = b.newDocument();

            // on crée la racine et les élements dans la racine
            Element racine = doc.createElement("Jeu-De-Vie");

            // balise avec les infos sur la grille
            Element infosGrille = doc.createElement("InfosGrille");
            infosGrille.setAttribute("SizeX", String.valueOf(this.env.getSizeX()));
            infosGrille.setAttribute("SizeY", String.valueOf(this.env.getSizeY()));
            infosGrille.setAttribute("Aléatoire", String.valueOf(this.env.getIsRandom()));

            // balise qui représente la grille
            Element grille = doc.createElement("Grille");
            for (int i = 0; i < this.env.getSizeX(); i++) {
                for (int j = 0; j < this.env.getSizeY(); j++) {
                    Element cellule = doc.createElement("Cellule");
                    cellule.setAttribute("indexX", String.valueOf(i));
                    cellule.setAttribute("indexY", String.valueOf(j));
                    cellule.setAttribute("etat", String.valueOf(this.env.getState(i, j)));
                    grille.appendChild(cellule);
                }
            }

            // ajouter les balises à la racine
            racine.appendChild(infosGrille);
            racine.appendChild(grille);

            doc.appendChild(racine);

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer();
            DOMSource output = new DOMSource(doc);

            // Ouvrir un dialogue pour demander à l'utilisateur le nom du fichier
            JFileChooser fileChooser = new JFileChooser();

            // Définir le répertoire par défaut
            File structuresDir = new File(System.getProperty("user.dir") + "/structures");
            File defaultDirectory = structuresDir.exists() ? structuresDir : new File(System.getProperty("user.dir") + "/");

            fileChooser.setCurrentDirectory(defaultDirectory);
            fileChooser.setDialogTitle("Enregistrer la structure");
            fileChooser.setFileFilter(new FileNameExtensionFilter("Fichiers XML", "xml"));

            int userSelection = fileChooser.showSaveDialog(fenetre);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();

                if (fileToSave.exists()) {
                    int response = JOptionPane.showConfirmDialog(fenetre, 
                        "Le fichier existe déjà. Voulez-vous l'écraser ?", 
                        "Confirmer l'écrasement", 
                        JOptionPane.YES_NO_OPTION, 
                        JOptionPane.WARNING_MESSAGE);
                    if (response != JOptionPane.YES_OPTION) {
                        return;
                    }
                }

                // S'assurer que l'extension .xml est ajoutée si elle est manquante
                if (!fileToSave.getName().endsWith(".xml")) {
                    fileToSave = new File(fileToSave.getAbsolutePath() + ".xml");
                }

                StreamResult r = new StreamResult(fileToSave);
                t.transform(output, r);

                System.out.println("Fichier enregistré : " + fileToSave.getAbsolutePath());
            } else {
                System.out.println("Enregistrement annulé par l'utilisateur.");
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de l'enregistrement : " + e.getMessage());
        }

    }
    
    private void updateGridFromXMLFileValues(File file) {
        try {
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            DocumentBuilder b = f.newDocumentBuilder();
            Document doc = b.parse(file); // on fait 'parse' au fichier xml qui existe 
            Element racine = doc.getDocumentElement();
            // enfants de la racine
            NodeList grilleCellulesTab = racine.getLastChild().getChildNodes();
            for (int i = 0; i < grilleCellulesTab.getLength(); i++) {
                Element cellule = (Element)grilleCellulesTab.item(i);
                int x = Integer.valueOf(cellule.getAttribute("indexX"));
                int y = Integer.valueOf(cellule.getAttribute("indexY"));
                boolean etat = Boolean.parseBoolean(cellule.getAttribute("etat"));
                this.env.getTabCases()[x][y].setTempNextState(etat);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
