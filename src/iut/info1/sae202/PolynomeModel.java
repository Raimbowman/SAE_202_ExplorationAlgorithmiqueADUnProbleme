/*
 * PolynomeModel.java
 * IUT de Rodez, BUT1 2025-2026, pas de copyright
 * Modèle MVC — gère la liste des polynômes et les opérations métier
 */
package iut.info1.sae202;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

/**
 * Modèle MVC de l'application de gestion de polynômes.
 * Expose une {@link ObservableList} de {@link Polynome} pour que
 * la vue se mette à jour automatiquement via les bindings JavaFX.
 *
 * @author Anaëlle HINARD
 * @author Noam LACOMBE
 * @author Eva GUENEGOU
 */
public class PolynomeModel {

    /** Liste observable des polynômes courants. */
    private final ObservableList<Polynome> polynomes =
            FXCollections.observableArrayList();

    /** Nom du fichier de persistance. */
    private static final String NOM_FICHIER = "polynomes.txt";

    // ─────────────────────────────────────────
    //  Gestion de la liste
    // ─────────────────────────────────────────

    /** @return la liste observable (utilisée pour les bindings JavaFX). */
    public ObservableList<Polynome> getPolynomes() {
        return polynomes;
    }

    /**
     * Ajoute un polynôme créé à partir de ses coefficients.
     * @param coeffs tableau de coefficients (ordre croissant du degré)
     * @throws IllegalArgumentException si les coefficients sont invalides
     */
    public void ajouterParCoefficients(double[] coeffs) {
        polynomes.add(new Polynome(coeffs));
    }

    /**
     * Ajoute un polynôme créé à partir de ses racines.
     * @param racines          tableau des racines réelles
     * @param ordres           tableau des ordres de multiplicité
     * @param hautCoefficient  coefficient du monôme de plus haut degré
     * @throws IllegalArgumentException si les paramètres sont invalides
     */
    public void ajouterParRacines(double[] racines, int[] ordres, double hautCoefficient) {
        polynomes.add(new Polynome(racines, ordres, hautCoefficient));
    }

    /**
     * Ajoute un polynôme créé à partir d'une chaîne textuelle.
     * @param representation ex. "3x^2+4x-2"
     * @throws IllegalArgumentException si la représentation est invalide
     */
    public void ajouterParChaine(String representation) {
        polynomes.add(new Polynome(representation));
    }

    /**
     * Supprime le polynôme à l'index donné.
     * @param index index dans la liste (0-based)
     */
    public void supprimer(int index) {
        if (index >= 0 && index < polynomes.size()) {
            polynomes.remove(index);
        }
    }

    /** Vide toute la liste. */
    public void toutEffacer() {
        polynomes.clear();
    }

    // ─────────────────────────────────────────
    //  Opérations arithmétiques
    // ─────────────────────────────────────────

    /** Addition de deux polynômes de la liste. */
    public Polynome addition(int i, int j) {
        return polynomes.get(i).addition(polynomes.get(j));
    }

    /** Soustraction de deux polynômes de la liste. */
    public Polynome soustraction(int i, int j) {
        return polynomes.get(i).soustraction(polynomes.get(j));
    }

    /** Multiplication de deux polynômes de la liste. */
    public Polynome multiplication(int i, int j) {
        return polynomes.get(i).multiplication(polynomes.get(j));
    }

    /** Multiplication d'un polynôme de la liste par un scalaire. */
    public Polynome multiplicationScalaire(int i, double scalaire) {
        return polynomes.get(i).multiplication(scalaire);
    }

    /**
     * Division euclidienne.
     * @return tableau [quotient, reste]
     * @throws IllegalArgumentException si division par zéro ou degré infini
     */
    public Polynome[] division(int i, int j) {
        return polynomes.get(i).division(polynomes.get(j));
    }

    /** PGCD de deux polynômes de la liste. */
    public Polynome pgcd(int i, int j) {
        return polynomes.get(i).pgcd(polynomes.get(j));
    }

    // ─────────────────────────────────────────
    //  Analyse d'un polynôme
    // ─────────────────────────────────────────

    /** @return la dérivée du polynôme i. */
    public Polynome derivee(int i) {
        return polynomes.get(i).derivee();
    }

    /** @return une primitive du polynôme i. */
    public Polynome primitive(int i) {
        return polynomes.get(i).primitive();
    }

    /**
     * @return l'intégrale du polynôme i sur [a, b].
     */
    public double integrale(int i, double a, double b) {
        return polynomes.get(i).integrale(a, b);
    }

    /**
     * @return la moyenne du polynôme i sur [a, b].
     */
    public double moyenne(int i, double a, double b) {
        return polynomes.get(i).moyenne(a, b);
    }

    /**
     * @return l'image du polynôme i en x.
     */
    public double image(int i, double x) {
        return polynomes.get(i).image(x);
    }

    /**
     * Calcule une série de points (x, y) pour tracer le polynôme i.
     * <p>
     * Les valeurs non finies (NaN, ±Infini) sont conservées comme NaN ;
     * la vue les utilise comme signal de coupure de tracé.
     * Les valeurs finies aberrantes (outliers IQR × 10) sont aussi remplacées
     * par NaN pour éviter qu'un seul point hors-norme n'écrase toute l'échelle.
     * </p>
     * @param i      index du polynôme
     * @param xMin   borne gauche
     * @param xMax   borne droite
     * @param nbPts  nombre de points à échantillonner
     * @return liste de tableaux [x, y], y peut valoir NaN (coupure de tracé)
     */
    public List<double[]> pointsPourTracé(int i, double xMin, double xMax, int nbPts) {
        Polynome p = polynomes.get(i);
        double pas = (xMax - xMin) / (nbPts - 1);

        // Première passe : calculer tous les y
        double[] ys = new double[nbPts];
        for (int k = 0; k < nbPts; k++) {
            ys[k] = p.image(xMin + k * pas);
        }

        // Détection des outliers via l'écart interquartile (IQR)
        java.util.List<Double> finies = new java.util.ArrayList<>(nbPts);
        for (double v : ys) { if (Double.isFinite(v)) finies.add(v); }

        double yLow = Double.NEGATIVE_INFINITY, yHigh = Double.POSITIVE_INFINITY;
        if (finies.size() >= 4) {
            java.util.Collections.sort(finies);
            int n = finies.size();
            double q1 = finies.get(n / 4);
            double q3 = finies.get(3 * n / 4);
            double iqr = q3 - q1;
            if (iqr > 0) {
                // Seuil généreux (×10) pour ne pas couper les polynômes à forte pente
                yLow  = q1 - 10 * iqr;
                yHigh = q3 + 10 * iqr;
            }
        }

        // Deuxième passe : construire la liste avec écrêtage des outliers
        List<double[]> points = new ArrayList<>(nbPts);
        for (int k = 0; k < nbPts; k++) {
            double x = xMin + k * pas;
            double y = ys[k];
            if (!Double.isFinite(y) || y < yLow || y > yHigh) {
                y = Double.NaN;  // signal de coupure pour la vue
            }
            points.add(new double[]{x, y});
        }
        return points;
    }

    // ─────────────────────────────────────────
    //  Interpolation
    // ─────────────────────────────────────────

    /**
     * @return le polynôme d'interpolation de Lagrange passant par les points donnés.
     */
    public Polynome interpolation(double[][] points) {
        return Polynome.interpolationPolynomiale(points);
    }

    // ─────────────────────────────────────────
    //  Persistance
    // ─────────────────────────────────────────

    /**
     * Sauvegarde tous les polynômes de la liste dans le fichier.
     * Le fichier est réécrit de zéro à chaque appel.
     */
    public void sauvegarderTout() {
        Polynome.supprimerFichier(NOM_FICHIER);
        for (Polynome p : polynomes) {
            p.sauvegarderPolynome(NOM_FICHIER);
        }
    }

    /**
     * Charge tous les polynômes depuis le fichier et remplace la liste courante.
     * Les lignes illisibles sont ignorées silencieusement.
     */
    public void chargerTout() {
        polynomes.clear();
        int n = 1;
        while (true) {
            try {
                polynomes.add(Polynome.chargerPolynome(NOM_FICHIER, n));
                n++;
            } catch (RuntimeException e) {
                break; // fin du fichier ou ligne absente
            }
        }
    }

    /**
     * Ajoute le polynôme résultat à la liste.
     * @param p polynôme à ajouter
     */
    public void ajouterPolynome(Polynome p) {
        polynomes.add(p);
    }

    /** @return le nom du fichier de persistance. */
    public String getNomFichier() {
        return NOM_FICHIER;
    }
}
