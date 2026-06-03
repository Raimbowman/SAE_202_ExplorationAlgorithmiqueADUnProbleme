/*
 * PolynomeController.java
 * IUT de Rodez, BUT1 2025-2026, pas de copyright
 * Contrôleur MVC — fait le lien entre PolynomeView et PolynomeModel
 */
package iut.info1.sae202;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.util.ArrayList;
import java.util.List;

/**
 * Contrôleur MVC de l'application de gestion de polynômes.
 * <p>
 * Reçoit les événements de la vue, délègue la logique métier au modèle,
 * puis demande à la vue de se mettre à jour. Le contrôleur ne contient
 * aucune logique de calcul et aucun composant graphique.
 * </p>
 *
 * @author Anaëlle HINARD
 * @author Noam LACOMBE
 * @author Eva GUENEGOU
 */
public class PolynomeController {

    private final PolynomeModel model;
    private PolynomeView        view;

    // ─────────────────────────────────────────
    //  Initialisation
    // ─────────────────────────────────────────

    public PolynomeController(PolynomeModel model) {
        this.model = model;
    }

    /** Injecte la vue après construction (évite la dépendance circulaire). */
    public void setView(PolynomeView view) {
        this.view = view;
    }

    // ─────────────────────────────────────────
    //  Création de polynômes
    // ─────────────────────────────────────────

    /**
     * Crée un polynôme à partir d'une chaîne de coefficients séparés par des espaces ou des virgules.
     * @param saisie ex. "3 5 2"  →  P(x) = 2x² + 5x + 3
     */
    public void creerParCoefficients(String saisie) {
        try {
            double[] coeffs = parseDoubles(saisie);
            model.ajouterParCoefficients(coeffs);
            view.rafraichirListe();
            view.afficherInfo("Polynôme ajouté : " + model.getPolynomes()
                    .get(model.getPolynomes().size() - 1).toString());
        } catch (Exception e) {
            afficherErreur("Coefficients invalides", e.getMessage());
        }
    }

    /**
     * Crée un polynôme à partir de ses racines.
     * @param saisieRacines  ex. "2 3"
     * @param saisieOrdres   ex. "1 1"
     * @param saisieHautCoef ex. "1"
     */
    public void creerParRacines(String saisieRacines, String saisieOrdres, String saisieHautCoef) {
        try {
            double[] racines = parseDoubles(saisieRacines);
            int[]    ordres  = parseInts(saisieOrdres);
            double   hautCoef = Double.parseDouble(saisieHautCoef.trim().replace(',', '.'));
            model.ajouterParRacines(racines, ordres, hautCoef);
            view.rafraichirListe();
            view.afficherInfo("Polynôme ajouté : " + model.getPolynomes()
                    .get(model.getPolynomes().size() - 1).toString());
        } catch (Exception e) {
            afficherErreur("Paramètres invalides", e.getMessage());
        }
    }

    /**
     * Crée un polynôme à partir d'une représentation textuelle.
     * @param representation ex. "3x^2+4x-2"
     */
    public void creerParChaine(String representation) {
        try {
            model.ajouterParChaine(representation);
            view.rafraichirListe();
            view.afficherInfo("Polynôme ajouté : " + model.getPolynomes()
                    .get(model.getPolynomes().size() - 1).toString());
        } catch (Exception e) {
            afficherErreur("Représentation invalide", e.getMessage());
        }
    }

    // ─────────────────────────────────────────
    //  Suppression
    // ─────────────────────────────────────────

    /** Supprime le polynôme sélectionné dans la liste. */
    public void supprimerPolynome(int index) {
        if (index < 0) { afficherErreur("Sélection vide", "Veuillez sélectionner un polynôme."); return; }
        model.supprimer(index);
        view.rafraichirListe();
    }

    /** Vide toute la liste. */
    public void toutEffacer() {
        model.toutEffacer();
        view.rafraichirListe();
        view.afficherInfo("Liste vidée.");
    }

    // ─────────────────────────────────────────
    //  Opérations arithmétiques
    // ─────────────────────────────────────────

    public void addition(int i, int j) {
        if (!verifierIndex(i, j)) return;
        try {
            Polynome res = model.addition(i, j);
            view.afficherResultat("Addition", res.toString());
            proposerAjout(res);
        } catch (Exception e) { afficherErreur("Erreur", e.getMessage()); }
    }

    public void soustraction(int i, int j) {
        if (!verifierIndex(i, j)) return;
        try {
            Polynome res = model.soustraction(i, j);
            view.afficherResultat("Soustraction", res.toString());
            proposerAjout(res);
        } catch (Exception e) { afficherErreur("Erreur", e.getMessage()); }
    }

    public void multiplication(int i, int j) {
        if (!verifierIndex(i, j)) return;
        try {
            Polynome res = model.multiplication(i, j);
            view.afficherResultat("Multiplication", res.toString());
            proposerAjout(res);
        } catch (Exception e) { afficherErreur("Erreur", e.getMessage()); }
    }

    public void multiplicationScalaire(int i, String saisieScalaire) {
        if (!verifierIndex(i)) return;
        try {
            double scalaire = Double.parseDouble(saisieScalaire.trim().replace(',', '.'));
            Polynome res = model.multiplicationScalaire(i, scalaire);
            view.afficherResultat("Multiplication par scalaire", res.toString());
            proposerAjout(res);
        } catch (Exception e) { afficherErreur("Scalaire invalide", e.getMessage()); }
    }

    public void division(int i, int j) {
        if (!verifierIndex(i, j)) return;
        try {
            Polynome[] res = model.division(i, j);
            view.afficherResultat("Division",
                    "Quotient : " + res[0].toString() + "\nReste    : " + res[1].toString());
        } catch (Exception e) { afficherErreur("Erreur de division", e.getMessage()); }
    }

    public void pgcd(int i, int j) {
        if (!verifierIndex(i, j)) return;
        try {
            Polynome res = model.pgcd(i, j);
            view.afficherResultat("PGCD", res.toString());
            proposerAjout(res);
        } catch (Exception e) { afficherErreur("Erreur", e.getMessage()); }
    }

    // ─────────────────────────────────────────
    //  Analyse
    // ─────────────────────────────────────────

    public void derivee(int i) {
        if (!verifierIndex(i)) return;
        Polynome res = model.derivee(i);
        view.afficherResultat("Dérivée", res.toString());
        proposerAjout(res);
    }

    public void primitive(int i) {
        if (!verifierIndex(i)) return;
        Polynome res = model.primitive(i);
        view.afficherResultat("Primitive", res.toString() + "  +  k");
        proposerAjout(res);
    }

    public void integrale(int i, String saisieA, String saisieB) {
        if (!verifierIndex(i)) return;
        try {
            double a = Double.parseDouble(saisieA.trim().replace(',', '.'));
            double b = Double.parseDouble(saisieB.trim().replace(',', '.'));
            double res = model.integrale(i, a, b);
            view.afficherResultat("Intégrale [" + a + " ; " + b + "]", String.valueOf(res));
        } catch (Exception e) { afficherErreur("Bornes invalides", e.getMessage()); }
    }

    public void moyenne(int i, String saisieA, String saisieB) {
        if (!verifierIndex(i)) return;
        try {
            double a = Double.parseDouble(saisieA.trim().replace(',', '.'));
            double b = Double.parseDouble(saisieB.trim().replace(',', '.'));
            double res = model.moyenne(i, a, b);
            view.afficherResultat("Moyenne sur [" + a + " ; " + b + "]", String.valueOf(res));
        } catch (Exception e) { afficherErreur("Bornes invalides", e.getMessage()); }
    }

    public void image(int i, String saisieX) {
        if (!verifierIndex(i)) return;
        try {
            double x = Double.parseDouble(saisieX.trim().replace(',', '.'));
            double res = model.image(i, x);
            view.afficherResultat("Image en x = " + x, String.valueOf(res));
        } catch (Exception e) { afficherErreur("Valeur invalide", e.getMessage()); }
    }

    // ─────────────────────────────────────────
    //  Tracé de courbe
    // ─────────────────────────────────────────

    /**
     * Demande à la vue de tracer la courbe d'un ou plusieurs polynômes.
     * @param indices  liste des index à tracer
     * @param xMin     borne gauche du tracé
     * @param xMax     borne droite du tracé
     */
    public void tracerCourbes(List<Integer> indices, String saisieXMin, String saisieXMax) {
        try {
            double xMin = Double.parseDouble(saisieXMin.trim().replace(',', '.'));
            double xMax = Double.parseDouble(saisieXMax.trim().replace(',', '.'));
            if (xMin >= xMax) {
                afficherErreur("Intervalle invalide", "xMin doit être strictement inférieur à xMax.");
                return;
            }
            if (indices.isEmpty()) {
                afficherErreur("Sélection vide", "Sélectionnez au moins un polynôme à tracer.");
                return;
            }
            List<List<double[]>> series = new ArrayList<>();
            List<String> labels = new ArrayList<>();
            for (int idx : indices) {
                series.add(model.pointsPourTracé(idx, xMin, xMax, 400));
                labels.add("P" + (idx + 1));
            }
            view.tracerCourbes(series, labels, xMin, xMax);
        } catch (NumberFormatException e) {
            afficherErreur("Valeurs invalides", "Veuillez saisir des nombres réels pour xMin et xMax.");
        }
    }

    // ─────────────────────────────────────────
    //  Interpolation
    // ─────────────────────────────────────────

    /**
     * Calcule le polynôme d'interpolation à partir de points saisis.
     * @param saisiePoints texte multi-lignes, une paire "x,y" par ligne
     */
    public void interpolation(String saisiePoints) {
        try {
            String[] lignes = saisiePoints.trim().split("\\R");
            double[][] pts = new double[lignes.length][2];
            for (int k = 0; k < lignes.length; k++) {
                String[] parts = lignes[k].trim().split("[,;\\s]+");
                pts[k][0] = Double.parseDouble(parts[0].replace(',', '.'));
                pts[k][1] = Double.parseDouble(parts[1].replace(',', '.'));
            }
            Polynome res = model.interpolation(pts);
            view.afficherResultat("Interpolation de Lagrange", res.toString());
            proposerAjout(res);
        } catch (Exception e) {
            afficherErreur("Points invalides",
                    "Format attendu : une paire x y (ou x,y) par ligne.\n" + e.getMessage());
        }
    }

    // ─────────────────────────────────────────
    //  Persistance
    // ─────────────────────────────────────────

    public void sauvegarder() {
        try {
            model.sauvegarderTout();
            view.afficherInfo("Polynômes sauvegardés dans \"" + model.getNomFichier() + "\".");
        } catch (Exception e) { afficherErreur("Erreur de sauvegarde", e.getMessage()); }
    }

    public void charger() {
        try {
            model.chargerTout();
            view.rafraichirListe();
            view.afficherInfo(model.getPolynomes().size() + " polynôme(s) chargé(s).");
        } catch (Exception e) { afficherErreur("Erreur de chargement", e.getMessage()); }
    }

    // ─────────────────────────────────────────
    //  Fiche récapitulative
    // ─────────────────────────────────────────

    public void ficheRecapitulative(int i) {
        if (!verifierIndex(i)) return;
        Polynome p = model.getPolynomes().get(i);
        StringBuilder sb = new StringBuilder();
        sb.append("Expression   : ").append(p.toString()).append("\n");
        sb.append("Degré        : ").append((int) p.getDegre()).append("\n");
        double[] coeffs = p.getCoefficients();
        sb.append("Coefficients : ");
        for (double c : coeffs) sb.append(c).append("  ");
        sb.append("\n");
        double[] limites = p.getLimites();
        sb.append("Limite −∞    : ").append(limites[0]).append("\n");
        sb.append("Limite +∞    : ").append(limites[1]).append("\n");
        try {
            double[] racines = p.getRacines();
            sb.append("Racines      : ");
            if (racines.length == 0) sb.append("aucune");
            else for (double r : racines) sb.append(r).append("  ");
            sb.append("\n");
        } catch (UnsupportedOperationException ex) {
            sb.append("Racines      : non disponibles\n");
        }
        sb.append("Dérivée      : ").append(p.derivee().toString()).append("\n");
        sb.append("Primitive    : ").append(p.primitive().toString()).append("  +  k");
        view.afficherResultat("Fiche — P" + (i + 1), sb.toString());
    }

    // ─────────────────────────────────────────
    //  Helpers privés
    // ─────────────────────────────────────────

    /** Propose à l'utilisateur d'ajouter un résultat à la liste. */
    private void proposerAjout(Polynome res) {
        view.proposerAjoutResultat(res);
    }

    /** Ajoute directement le polynôme résultat à la liste. */
    public void ajouterResultat(Polynome res) {
        model.ajouterPolynome(res);
        view.rafraichirListe();
    }

    private boolean verifierIndex(int... indices) {
        int taille = model.getPolynomes().size();
        for (int idx : indices) {
            if (idx < 0 || idx >= taille) {
                afficherErreur("Sélection invalide",
                        "Veuillez sélectionner un polynôme valide dans la liste.");
                return false;
            }
        }
        return true;
    }

    private void afficherErreur(String titre, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(titre);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ─────────────────────────────────────────
    //  Utilitaires de parsing
    // ─────────────────────────────────────────

    private static double[] parseDoubles(String saisie) {
        String[] parts = saisie.trim().split("[,;\\s]+");
        double[] result = new double[parts.length];
        for (int k = 0; k < parts.length; k++) {
            result[k] = Double.parseDouble(parts[k].replace(',', '.'));
        }
        return result;
    }

    private static int[] parseInts(String saisie) {
        String[] parts = saisie.trim().split("[,;\\s]+");
        int[] result = new int[parts.length];
        for (int k = 0; k < parts.length; k++) {
            result[k] = Integer.parseInt(parts[k].trim());
        }
        return result;
    }
}
