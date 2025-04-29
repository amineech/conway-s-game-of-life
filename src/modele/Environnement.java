package modele;

import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.JFileChooser;
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
import java.awt.Point;
import java.io.File;
import java.util.HashMap;

public class Environnement extends Observable implements Runnable {
    private Case[][] tab;
    private HashMap<Case, Point> casesMap= new HashMap<Case, Point>();
    private boolean isRandom; //pour gérer le mode de jeu
    private XMLParser xmlParser;
    private long totalExecutionTime = 0; // Temps total accumulé
    private int executionCount = 0; // Nombre de cycles calculés
    private boolean useThreads = true; // Par défaut, utilise la version initiale
    private int nbThreads = Runtime.getRuntime().availableProcessors();
    private boolean isFirstRandomLaunch;

    public boolean isFirstRandomLaunch() {
        return isFirstRandomLaunch;
    }

    public void setFirstRandomLaunch(boolean isFirstRandomLaunch) {
        this.isFirstRandomLaunch = isFirstRandomLaunch;
    }

    public Environnement(int _sizeX, int _sizeY) {
        sizeX = _sizeX;
        sizeY = _sizeY;
        tab = new Case[sizeX][sizeY];
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                tab[i][j] = new Case(this);
                casesMap.put(tab[i][j], new Point(i, j));
            }
        }
        isRandom = false;
        xmlParser = new XMLParser(this);
        isFirstRandomLaunch = true;
    }

    public boolean getUseThreads(){
        return this.useThreads;
    }

    public void setUseThreads(boolean useThreads) {
        this.useThreads = useThreads;
        System.out.println("Utilisation des threads : " + (useThreads ? "Activée" : "Désactivée"));
    }

    public Case[][] getTabCases() {
        return this.tab;
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    private int sizeX, sizeY;

    public boolean getState(int x, int y) {
        return tab[x][y].getState();
    }

    public Case getCase(Case source, Direction d) {
        // TODO : une case utilisera ogligatoirement cette fonction pour percevoir son environnement, et définir son état suivant
        Point point = casesMap.get(source);

        int x = (int) point.getX();
        int y = (int) point.getY();

        // on récupère (x, y) de la case à chercher (voisine de source)
        switch (d) {
            case h:
                y -= 1;
                break;
            case hd:
                x += 1;
                y -= 1;
                break;
            case gh:
                x -= 1;
                y -= 1;
                break;
            case b:
                y += 1;
                break;
            case bg:
                x -= 1;
                y += 1;
                break;
            case db:
                x += 1;
                y += 1;
                break;
            case d:
                x += 1;
                break;
            case g:
                x -= 1;
                break;
            default:
                break;
        }
        // tester si c'est les valeurs de x y valides
        if(x < 0 || x >= sizeX || y < 0 || y >= sizeY) {
            return null;
        }

        return tab[x][y];
    }

    public void rndState() {    
        if (isFirstRandomLaunch){
            for (int i = 0; i < sizeX; i++) {
                for (int j = 0; j < sizeY; j++) {
                    tab[i][j].rndState();
                }
            }
            isFirstRandomLaunch = false;
        }else{
            for (int i = 0; i < sizeX; i++) {
                for (int j = 0; j < sizeY; j++) {
                    tab[i][j].nextState();
                }
            }  
        }
    }

    public boolean getIsRandom(){
        return isRandom;
    }

    public void setIsRandom(boolean _isRandom){
        this.isRandom = _isRandom;
    }

    public void calculNextStateInitial(){
        //System.out.println("Début du calcul de l'évolution (version initiale, sans threads).");
        long startTime = System.nanoTime(); // Mesurer le temps de début
        for (int i = 0; i < getSizeX(); i++) {
            for (int j = 0; j < getSizeY(); j++) {
                tab[i][j].nextState(); // Calcul de l'état suivant
            }
        }
        long endTime = System.nanoTime(); // Mesurer le temps de fin
        long elapsedTime = endTime - startTime;
        totalExecutionTime += elapsedTime;
        executionCount++;
        double averageTime = totalExecutionTime / (double) executionCount / 1_000_000.0; // Convertir en millisecondes
        //System.out.println("Calcul terminé (version initiale). Temps d'exécution : " + (endTime - startTime) / 1_000_000.0 + " ms.");
        System.out.println("Temps moyen (initial) : " + averageTime + " ms sur " + executionCount + " cycles.");
    }

    public void calculNextStateWithThreads(){
        long startTime = System.nanoTime(); // Mesurer le temps de début
        ExecutorService executor = Executors.newFixedThreadPool(nbThreads);

        int lignesParThread = getSizeX() / nbThreads;
        int lignesRestants = getSizeX() % nbThreads;

        for (int i = 0; i < nbThreads; i++) {
            int ligneDebut = i * lignesParThread;
            int ligneFin = ligneDebut + lignesParThread - 1;

            if (i == nbThreads - 1) {
                ligneFin += lignesRestants; // Ajouter les lignes restantes au dernier thread
            }

            int ligneDebutFinale = ligneDebut;
            int ligneFinFinale = ligneFin;

            executor.submit(() -> {
                //System.out.println("Thread " + Thread.currentThread().getName() + " traite les lignes " + finalStartRow + " à " + finalEndRow);
                for (int l = ligneDebutFinale; l <= ligneFinFinale; l++) {
                    for (int c = 0; c < getSizeY(); c++) {
                        tab[l][c].nextState(); // Calcul de l'état suivant
                    }
                }
                //System.out.println("Thread " + Thread.currentThread().getName() + " a terminé.");
            });
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                System.err.println("Les threads n'ont pas terminé dans le temps imparti !");
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            System.err.println("Erreur lors de l'attente des threads : " + e.getMessage());
            executor.shutdownNow();
        }
        long endTime = System.nanoTime(); // Mesurer le temps de fin
        long elapsedTime = endTime - startTime;
        totalExecutionTime += elapsedTime;
        executionCount++;
        double averageTime = totalExecutionTime / (double) executionCount / 1_000_000.0; // Convertir en millisecondes
        //System.out.println("Calcul terminé (version initiale). Temps d'exécution : " + (endTime - startTime) / 1_000_000.0 + " ms.");
        System.out.println("Temps moyen (threads) : " + averageTime + " ms sur " + executionCount + " cycles.");
        //System.out.println("Calcul terminé.");
    }

    @Override
    public void run() {
        if (isRandom){
            rndState();
        } else {
            
        }
        // notification de l'observer
        setChanged();
        notifyObservers();
    }


    // méthodes de gestion des fichiers xml
    public String loadXMLState(FenetrePrincipale fenetre) {
        return xmlParser.loadXMLState(fenetre);
    }

    public void loadLastXmlFile(String filename) {
        xmlParser.loadLastXmlFile(filename);
    }

    public void saveXMLState(FenetrePrincipale fenetre) {
        xmlParser.saveXMLState(fenetre);
    }

}
