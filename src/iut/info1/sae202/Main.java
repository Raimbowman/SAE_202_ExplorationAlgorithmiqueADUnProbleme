/*
 * Main.java                        17/04/2026
 * IUT de Rodez, BUT1 2025-2026, pas de copyright
 */
package iut.info1.sae202;

import java.util.Scanner;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Interface en ligne de commande pour manipuler des polynômes.
 * Permet la création, l'affichage, les opérations arithmétiques, 
 * ainsi que l'analyse analytique (dérivée, intégrale, limites, etc.) de polynômes.
 * @author Anaëlle HINARD
 * @author Noam LACOMBE
 * @author Eva GUENEGOU
 */
public class Main {

    /** Scanner pour la lecture des entrées utilisateur sur la console. */
    private static final Scanner saisie = new Scanner(System.in);
    
    /** Message d'erreur lorsqu'une opération nécessite au moins deux polynômes. */
    private static final String MESSAGE_ERREUR_NOMBRE_POLYNOME_CREE = "Il faut au moins 1 polynôme créé.";
    
    /** Message d'erreur lorsqu'aucun polynôme n'est disponible dans le fichier. */
    private static final String MESSAGE_ERREUR_AUCUN_POLYNOME_CREE  = "Aucun polynôme créé pour l'instant.";
    
    /** Nom du fichier texte utilisé pour sauvegarder et charger les polynômes. */
    private static final String NOM_FICHIER                         = "polynomes.txt";

    /**
     * Point d'entrée principal de l'application. 
     * Gère la boucle principale du menu textuel et redirige vers les actions associées.
     * @param args1 arguments de la ligne de commande (non utilisés)
     */
    public static void main(String[] args1) {

        int choixAction;
        do {
            System.out.println("\n--- Que voulez-vous faire ? ---");
            System.out.println("  1.  Construire des polynômes");
            System.out.println("  2.  Afficher les polynômes créés");
            System.out.println("  3.  Addition");
            System.out.println("  4.  Soustraction");
            System.out.println("  5.  Multiplication");
            System.out.println("  6.  Division");
            System.out.println("  7.  Afficher les limites d'un polynôme");
            System.out.println("  8.  Afficher les racines d'un polynôme");
            System.out.println("  9.  Afficher le degré d'un polynôme");
            System.out.println("  10. Afficher les coefficients d'un polynôme");
            System.out.println("  11. Calculer l'image en un point");
            System.out.println("  12. Calculer la dérivée");
            System.out.println("  13. Calculer la primitive");
            System.out.println("  14. Calculer l'intégrale sur un intervalle");
            System.out.println("  15. Calculer la moyenne sur un intervalle");
            System.out.println("  16. Fiche récapitulative d'un polynôme");
            System.out.println("  17. Calculer le PGCD de deux polynômes");
            System.out.println("  18. Interpolation polynomiale");
            System.out.println("  19. Réinitialiser le fichier de sauvegarde");
            System.out.println("  20. Quitter");
            System.out.print(" --> ");
            choixAction = lireEntierSaisi();
            if (choixAction < 1 || choixAction > 20) {
                System.out.println("Veuillez saisir un nombre entre 1 et 20.");
            }

            switch (choixAction) {
                case 1: {
                    System.out.print("Combien de polynômes voulez-vous créer ? \n --> ");
                    int nbPolynomesVoulu;
                    do {
                        nbPolynomesVoulu = lireEntierSaisi();
                        if (nbPolynomesVoulu <= 0) {
                            System.out.print("Veuillez saisir un entier strictement positif : ");
                        }
                    } while (nbPolynomesVoulu <= 0);

                    for (int i = 0; i < nbPolynomesVoulu; i++) {
                        int numeroActuel = lireLigneBruteDuFichier().size() + 1;
                        System.out.println("\nCréation du polynôme P" + numeroActuel);

                        int choixConstructeur;
                        do {
                            System.out.println("Comment voulez-vous créer votre polynôme P" + numeroActuel + " ?");
                            System.out.println("  1. Par coefficients");
                            System.out.println("  2. Par racines");
                            System.out.println("  3. Par une chaîne de caractères (ex : 3x^2+4x-2)");
                            System.out.print(" --> ");
                            choixConstructeur = lireEntierSaisi();
                            if (choixConstructeur < 1 || choixConstructeur > 3) {
                                System.out.println("Veuillez saisir 1, 2 ou 3.");
                            }
                        } while (choixConstructeur < 1 || choixConstructeur > 3);

                        Polynome nouveauPoly;
                        if (choixConstructeur == 1) {
                            nouveauPoly = saisirParCoefficients();
                        } else if (choixConstructeur == 2) {
                            nouveauPoly = saisirParRacines();
                        } else {
                            nouveauPoly = saisirParChaine();
                        }
                        ecrireDansFichier("P" + numeroActuel + " = " + nouveauPoly.toString());
                        System.out.println("P" + numeroActuel + " = " + nouveauPoly.toString() + " enregistré.");
                    }
                    break;
                }
                case 2: {
                    ArrayList<String> lignes = lireLigneBruteDuFichier();
                    if (lignes.isEmpty()) {
                        System.out.println(MESSAGE_ERREUR_AUCUN_POLYNOME_CREE);
                    } else {
                        System.out.println("\n--- Polynômes enregistrés ---");
                        for (String ligne : lignes) {
                            System.out.println("  " + ligne);
                        }
                    }
                    break;
                }
                case 3: {
                    ArrayList<Polynome> poly = lirePolynomesDuFichier();
                    if (poly.size() < 2) {
                        System.out.println(MESSAGE_ERREUR_NOMBRE_POLYNOME_CREE);
                        break;
                    }
                    afficherListePolynomes(poly);
                    int p1 = choisirPolynome("premier", poly.size());
                    int p2 = choisirPolynome("second", poly.size());
                    Polynome resultat = poly.get(p1).addition(poly.get(p2));
                    System.out.println("Résultat : " + resultat.toString());
                    proposerEnregistrement(resultat);
                    break;
                }
                case 4: {
                    ArrayList<Polynome> poly = lirePolynomesDuFichier();
                    if (poly.size() < 2) {
                        System.out.println(MESSAGE_ERREUR_NOMBRE_POLYNOME_CREE);
                        break;
                    }
                    afficherListePolynomes(poly);
                    int p1 = choisirPolynome("premier", poly.size());
                    int p2 = choisirPolynome("second", poly.size());
                    Polynome resultat = poly.get(p1).soustraction(poly.get(p2));
                    System.out.println("Résultat : " + resultat.toString());
                    proposerEnregistrement(resultat);
                    break;
                }
                case 5: {
                    ArrayList<Polynome> poly = lirePolynomesDuFichier();
                    if (poly.isEmpty()) {
                        System.out.println(MESSAGE_ERREUR_NOMBRE_POLYNOME_CREE);
                        break;
                    }
                    System.out.println("Type de multiplication :");
                    System.out.println("  1. Par un autre polynôme");
                    System.out.println("  2. Par un scalaire");
                    System.out.print(" --> ");
                    int choixMult;
                    do {
                        choixMult = lireEntierSaisi();
                        if (choixMult != 1 && choixMult != 2) {
                            System.out.println("Veuillez saisir 1 ou 2.");
                        }
                    } while (choixMult != 1 && choixMult != 2);

                    afficherListePolynomes(poly);
                    if (choixMult == 1) {
                        if (poly.size() < 2) {
                            System.out.println(MESSAGE_ERREUR_NOMBRE_POLYNOME_CREE);
                            break;
                        }
                        int p1 = choisirPolynome("premier", poly.size());
                        int p2 = choisirPolynome("second", poly.size());
                        Polynome resultat = poly.get(p1).multiplication(poly.get(p2));
                        System.out.println("Résultat : " + resultat.toString());
                        proposerEnregistrement(resultat);
                    } else {
                        int p1 = choisirPolynome("à multiplier", poly.size());
                        System.out.print("Scalaire : ");
                        double scalaire = lireReel();
                        Polynome resultat = poly.get(p1).multiplication(scalaire);
                        System.out.println("Résultat : " + resultat.toString());
                        proposerEnregistrement(resultat);
                    }
                    break;
                }
                case 6: {
                    ArrayList<Polynome> poly = lirePolynomesDuFichier();
                    if (poly.size() < 2) {
                        System.out.println(MESSAGE_ERREUR_NOMBRE_POLYNOME_CREE);
                        break;
                    }
                    afficherListePolynomes(poly);
                    int p1 = choisirPolynome("dividende", poly.size());
                    int p2 = choisirPolynome("diviseur", poly.size());
                    try {
                        Polynome[] res = poly.get(p1).division(poly.get(p2));
                        System.out.println("Quotient : " + res[0].toString());
                        System.out.println("Reste    : " + res[1].toString());
                    } catch (IllegalArgumentException e) {
                        System.out.println("Erreur : " + e.getMessage());
                    }
                    break;
                }
                case 7: {
                    ArrayList<Polynome> poly = lirePolynomesDuFichier();
                    if (poly.isEmpty()) {
                        System.out.println(MESSAGE_ERREUR_AUCUN_POLYNOME_CREE);
                        break;
                    }
                    afficherListePolynomes(poly);
                    int p1 = choisirPolynome("", poly.size());
                    double[] limites = poly.get(p1).getLimites();
                    System.out.println("Limite en -infini : " + limites[0]);
                    System.out.println("Limite en +infini : " + limites[1]);
                    break;
                }
                case 8: {
                    ArrayList<Polynome> poly = lirePolynomesDuFichier();
                    if (poly.isEmpty()) {
                        System.out.println(MESSAGE_ERREUR_AUCUN_POLYNOME_CREE);
                        break;
                    }
                    afficherListePolynomes(poly);
                    int p1 = choisirPolynome("", poly.size());
                    try {
                        double[] racines = poly.get(p1).getRacines();
                        if (racines.length == 0) {
                            System.out.println("Ce polynôme n'a pas de racines.");
                        } else {
                            System.out.print("Racines : ");
                            for (double r : racines) {
                                System.out.print(r + "  ");
                            }
                            System.out.println();
                        }
                    } catch (UnsupportedOperationException e) {
                        System.out.println("Erreur : " + e.getMessage());
                    }
                    break;
                }
                case 9: {
                    ArrayList<Polynome> poly = lirePolynomesDuFichier();
                    if (poly.isEmpty()) {
                        System.out.println(MESSAGE_ERREUR_AUCUN_POLYNOME_CREE);
                        break;
                    }
                    afficherListePolynomes(poly);
                    int p1 = choisirPolynome("", poly.size());
                    System.out.println("Degré de P" + (p1 + 1) + " : " + (int) poly.get(p1).getDegre());
                    break;
                }
                case 10: {
                    ArrayList<Polynome> poly = lirePolynomesDuFichier();
                    if (poly.isEmpty()) {
                        System.out.println(MESSAGE_ERREUR_AUCUN_POLYNOME_CREE);
                        break;
                    }
                    afficherListePolynomes(poly);
                    int p1 = choisirPolynome("", poly.size());
                    double[] coeffs = poly.get(p1).getCoefficients();
                    System.out.print("Coefficients de P" + (p1 + 1)
                                     + " (du degré 0 au degré " + (coeffs.length - 1) + ") : ");
                    for (double c : coeffs) {
                        System.out.print(c + "  ");
                    }
                    System.out.println();
                    break;
                }
                case 11: {
                    ArrayList<Polynome> poly = lirePolynomesDuFichier();
                    if (poly.isEmpty()) {
                        System.out.println(MESSAGE_ERREUR_AUCUN_POLYNOME_CREE);
                        break;
                    }
                    afficherListePolynomes(poly);
                    int p1 = choisirPolynome("", poly.size());
                    System.out.print("Valeur de x : ");
                    double x = lireReel();
                    System.out.println("P" + (p1 + 1) + "(" + x + ") = " + poly.get(p1).image(x));
                    break;
                }
                case 12: {
                    ArrayList<Polynome> poly = lirePolynomesDuFichier();
                    if (poly.isEmpty()) {
                        System.out.println(MESSAGE_ERREUR_AUCUN_POLYNOME_CREE);
                        break;
                    }
                    afficherListePolynomes(poly);
                    int p1 = choisirPolynome("", poly.size());
                    Polynome derivee = poly.get(p1).derivee();
                    System.out.println("Dérivée de P" + (p1 + 1) + " : " + derivee.toString());
                    proposerEnregistrement(derivee);
                    break;
                }
                case 13: {
                    ArrayList<Polynome> poly = lirePolynomesDuFichier();
                    if (poly.isEmpty()) {
                        System.out.println(MESSAGE_ERREUR_AUCUN_POLYNOME_CREE);
                        break;
                    }
                    afficherListePolynomes(poly);
                    int p1 = choisirPolynome("", poly.size());
                    Polynome primitive = poly.get(p1).primitive();
                    System.out.println("Primitive de P" + (p1 + 1) + " : " + primitive.toString()
                                       + " + k  (k constante d'intégration)");
                    proposerEnregistrement(primitive);
                    break;
                }
                case 14: {
                    ArrayList<Polynome> poly = lirePolynomesDuFichier();
                    if (poly.isEmpty()) {
                        System.out.println(MESSAGE_ERREUR_AUCUN_POLYNOME_CREE);
                        break;
                    }
                    afficherListePolynomes(poly);
                    int p1 = choisirPolynome("", poly.size());
                    System.out.print("Borne inférieure a : ");
                    double a = lireReel();
                    System.out.print("Borne supérieure b : ");
                    double b = lireReel();
                    System.out.println("Integrale[" + a + ", " + b + "] P" + (p1 + 1)
                                       + "(x) dx = " + poly.get(p1).integrale(a, b));
                    break;
                }
                case 15: {
                    ArrayList<Polynome> poly = lirePolynomesDuFichier();
                    if (poly.isEmpty()) {
                        System.out.println(MESSAGE_ERREUR_AUCUN_POLYNOME_CREE);
                        break;
                    }
                    afficherListePolynomes(poly);
                    int p1 = choisirPolynome("", poly.size());
                    System.out.print("Borne inférieure a : ");
                    double a = lireReel();
                    System.out.print("Borne supérieure b : ");
                    double b = lireReel();
                    System.out.println("Moyenne de P" + (p1 + 1) + " sur [" + a + ", " + b
                                       + "] = " + poly.get(p1).moyenne(a, b));
                    break;
                }
                case 16: {
                    ArrayList<Polynome> poly = lirePolynomesDuFichier();
                    if (poly.isEmpty()) {
                        System.out.println(MESSAGE_ERREUR_AUCUN_POLYNOME_CREE);
                        break;
                    }
                    afficherFicheRecapitulative(poly);
                    break;
                }
                case 17: {
                    ArrayList<Polynome> poly = lirePolynomesDuFichier();
                    if (poly.size() < 2) {
                        System.out.println(MESSAGE_ERREUR_NOMBRE_POLYNOME_CREE);
                        break;
                    }
                    afficherListePolynomes(poly);
                    int p1 = choisirPolynome("premier", poly.size());
                    int p2 = choisirPolynome("second", poly.size());
                    Polynome pgcd = poly.get(p1).pgcd(poly.get(p2));
                    System.out.println("PGCD(P" + (p1 + 1) + ", P" + (p2 + 1) + ") = " + pgcd.toString());
                    proposerEnregistrement(pgcd);
                    break;
                }
                case 18: {
                    System.out.print("Nombre de points : \n --> ");
                    int nbPoints;
                    do {
                        nbPoints = lireEntierSaisi();
                        if (nbPoints <= 0) {
                            System.out.print("Veuillez saisir un entier strictement positif : ");
                        }
                    } while (nbPoints <= 0);

                    double[][] points = new double[nbPoints][2];
                    System.out.println("Entrez les coordonnées de chaque point :");
                    for (int i = 0; i < nbPoints; i++) {
                        System.out.print("  Point " + (i + 1) + " - x : ");
                        points[i][0] = lireReel();
                        System.out.print("  Point " + (i + 1) + " - y : ");
                        points[i][1] = lireReel();
                    }

                    try {
                        Polynome interpolation = Polynome.interpolationPolynomiale(points);
                        System.out.println("Polynôme d'interpolation : " + interpolation.toString());
                        proposerEnregistrement(interpolation);
                    } catch (Exception e) {
                        System.out.println("Erreur lors de l'interpolation : " + e.getMessage());
                    }
                    break;
                }
                case 19: {
                    effacerFichier();
                    System.out.println("Le fichier a bien été réinitilisé.");
                    break;
                }
                case 20:
                    System.out.println("Au revoir !");
                    break;
            }
        } while (choixAction != 20);
    }

    /**
     * Lit le fichier de sauvegarde et retourne la liste des lignes brutes 
     * correspondant à la définition d'un polynôme ("Px = ...").
     * @return une {@link ArrayList} de {@link String} contenant les lignes du fichier. 
     * Retourne une liste vide si le fichier est absent ou illisible.
     */
    private static ArrayList<String> lireLigneBruteDuFichier() {
        ArrayList<String> lignes = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(NOM_FICHIER))) {
            String ligne;
            while ((ligne = br.readLine()) != null) {
                if (ligne.startsWith("P")) {
                    lignes.add(ligne);
                }
            }
        } catch (IOException e) {
            // fichier absent ou vide : on retourne une liste vide
        }
        return lignes;
    }

    /**
     * Lit le fichier de sauvegarde et reconstruit les objets {@link Polynome} 
     * à partir des expressions littérales stockées après le signe '='.
     * @return une {@link ArrayList} d'objets {@link Polynome} valides et reconstruits.
     */
    private static ArrayList<Polynome> lirePolynomesDuFichier() {
        ArrayList<Polynome> poly = new ArrayList<>();
        for (String ligne : lireLigneBruteDuFichier()) {
            // format : "Px = <expression>"
            int idx = ligne.indexOf('=');
            if (idx != -1) {
                String expression = ligne.substring(idx + 1).trim();
                try {
                    poly.add(new Polynome(expression));
                } catch (IllegalArgumentException e) {
                    // ligne mal formée, on l'ignore
                }
            }
        }
        return poly;
    }

    /**
     * Affiche sur la console la liste numérotée et formatée des polynômes 
     * actuellement disponibles.
     * @param poly la liste des polynômes à afficher sur la console.
     */
    private static void afficherListePolynomes(ArrayList<Polynome> poly) {
        System.out.println("\n--- Polynômes disponibles ---");
        for (int i = 0; i < poly.size(); i++) {
            System.out.println("  P" + (i + 1) + " = " + poly.get(i).toString());
        }
    }

    /**
     * Réinitialise le fichier de sauvegarde en écrasant son contenu 
     * par une ligne d'en-tête par défaut.
     */
    private static void effacerFichier() {
        try (FileWriter fw = new FileWriter(NOM_FICHIER, false)) {
            fw.write("=== Session polynomes ===\n");
        } catch (IOException e) {
            System.out.println("Avertissement : impossible de créer le fichier " + NOM_FICHIER);
        }
    }

    /**
     * Ajoute une ligne de texte à la fin du fichier de sauvegarde (mode append).
     * @param ligne la chaîne de caractères à inscrire dans le fichier.
     */
    private static void ecrireDansFichier(String ligne) {
        try (FileWriter fw = new FileWriter(NOM_FICHIER, true)) {
            fw.write(ligne + "\n");
        } catch (IOException e) {
            System.out.println("Avertissement : impossible d'écrire dans le fichier " + NOM_FICHIER);
        }
    }

    /**
     * Demande à l'utilisateur de choisir un polynôme, puis affiche l'intégralité 
     * de ses caractéristiques (expression, degré, coefficients, limites, racines, dérivée, primitive).
     * @param poly la liste des polynômes disponibles pour l'analyse.
     */
    private static void afficherFicheRecapitulative(ArrayList<Polynome> poly) {
        afficherListePolynomes(poly);
        int p1 = choisirPolynome("", poly.size());
        Polynome p = poly.get(p1);

        System.out.println("\n========================================");
        System.out.println("  Fiche récapitulative de P" + (p1 + 1));
        System.out.println("========================================");
        System.out.println("  Expression    : " + p.toString());
        System.out.println("  Degré         : " + (int) p.getDegre());

        double[] coeffs = p.getCoefficients();
        System.out.print("  Coefficients  : ");
        for (double c : coeffs) {
            System.out.print(c + "  ");
        }
        System.out.println();

        double[] limites = p.getLimites();
        System.out.println("  Limite en -infini : " + limites[0]);
        System.out.println("  Limite en +infini : " + limites[1]);

        try {
            double[] racines = p.getRacines();
            if (racines.length == 0) {
                System.out.println("  Racines       : aucune");
            } else {
                System.out.print("  Racines       : ");
                for (double r : racines) {
                    System.out.print(r + "  ");
                }
                System.out.println();
            }
        } catch (UnsupportedOperationException e) {
            System.out.println("  Racines       : non disponibles (polynôme construit par coefficients)");
        }

        System.out.println("  Dérivée       : " + p.derivee().toString());
        System.out.println("  Primitive     : " + p.primitive().toString() + " + k");
        System.out.println("========================================");
    }

    /**
     * Interroge l'utilisateur pour savoir s'il souhaite sauvegarder le polynôme 
     * issu d'un calcul dans le fichier texte.
     * @param resultat le polynôme généré à sauvegarder.
     */
    private static void proposerEnregistrement(Polynome resultat) {
        System.out.println("Voulez-vous enregistrer ce résultat ?");
        System.out.println("  1. Oui");
        System.out.println("  2. Non");
        System.out.print(" --> ");
        int choix;
        do {
            choix = lireEntierSaisi();
            if (choix != 1 && choix != 2) {
                System.out.println("Veuillez saisir 1 ou 2.");
                System.out.print(" --> ");
            }
        } while (choix != 1 && choix != 2);

        if (choix == 1) {
            int numero = lireLigneBruteDuFichier().size() + 1;
            ecrireDansFichier("P" + numero + " = " + resultat.toString());
            System.out.println("Enregistré en tant que P" + numero + " !");
        }
    }

    /**
     * Gère la saisie utilisateur et le contrôle de cohérence pour sélectionner un polynôme 
     * parmi ceux présents dans la liste.
     * @param role le contexte ou rôle donné au polynôme ciblé (ex: "dividende", "premier")
     * @param nbPolynomes le nombre total de polynômes exploitables
     * @return l'index d'alignement (0-based) de la liste correspondant au choix utilisateur.
     */
    private static int choisirPolynome(String role, int nbPolynomes) {
        int numero;
        do {
            System.out.print("Numéro du polynôme"
                             + (role.isEmpty() ? "" : " " + role)
                             + " (1 à " + nbPolynomes + ") : ");
            numero = lireEntierSaisi();
            if (numero < 1 || numero > nbPolynomes) {
                System.out.println("Veuillez saisir un numéro entre 1 et " + nbPolynomes + ".");
            }
        } while (numero < 1 || numero > nbPolynomes);
        return numero - 1;
    }

    /**
     * Instancie un nouveau polynôme en invitant l'utilisateur à saisir 
     * individuellement chaque coefficient (du degré 0 jusqu'au degré maximal).
     * @return l'objet {@link Polynome} correctement initialisé.
     */
    private static Polynome saisirParCoefficients() {
        System.out.print("Nombre de coefficients (= degré + 1) : \n --> ");
        int nbCoefficients;
        do {
            nbCoefficients = lireEntierSaisi();
            if (nbCoefficients <= 0) {
                System.out.print("Veuillez saisir un entier strictement positif : ");
            }
        } while (nbCoefficients <= 0);

        double[] coefficients = new double[nbCoefficients];
        System.out.println("Entrez les coefficients du degré 0 au degré " + (nbCoefficients - 1) + " :");
        for (int i = 0; i < nbCoefficients; i++) {
            System.out.print("  Coefficient de x^" + i + " : ");
            coefficients[i] = lireReel();
        }

        try {
            return new Polynome(coefficients);
        } catch (IllegalArgumentException erreurPolynome) {
            System.out.println("Erreur : " + erreurPolynome.getMessage() + ". Recommencez.");
            return saisirParCoefficients();
        }
    }

    /**
     * Instancie un nouveau polynôme à partir de ses racines réelles distinctes, 
     * de leurs multiplicités associées et de son coefficient dominant.
     * @return l'objet {@link Polynome} calculé à partir de la forme factorisée.
     */
    private static Polynome saisirParRacines() {
        System.out.print("Nombre de racines distinctes (0 pour une constante) : \n --> ");
        int nbRacines;
        do {
            nbRacines = lireEntierSaisi();
            if (nbRacines < 0) {
                System.out.print("Veuillez saisir un entier positif ou nul : ");
            }
        } while (nbRacines < 0);

        double[] racines = new double[nbRacines];
        int[] ordresMultiplicite = new int[nbRacines];

        for (int i = 0; i < nbRacines; i++) {
            System.out.print("  Racine " + (i + 1) + " : ");
            racines[i] = lireReel();
            int ordre;
            do {
                System.out.print("  Ordre de multiplicité de la racine " + (i + 1) + " : ");
                ordre = lireEntierSaisi();
                if (ordre <= 0) {
                    System.out.print("Veuillez saisir un entier strictement positif : ");
                }
            } while (ordre <= 0);
            ordresMultiplicite[i] = ordre;
        }

        System.out.print("Coefficient du monôme de plus haut degré : ");
        double hautCoefficient = lireReel();

        try {
            return new Polynome(racines, ordresMultiplicite, hautCoefficient);
        } catch (IllegalArgumentException erreurPolynome) {
            System.out.println("Erreur : " + erreurPolynome.getMessage() + ". Recommencez.");
            return saisirParRacines();
        }
    }

    /**
     * Instancie un nouveau polynôme en analysant une chaîne de caractères textuelle 
     * tapée par l'utilisateur (ex: "3x^2+4x-2").
     * @return l'objet {@link Polynome} interprété.
     */
    private static Polynome saisirParChaine() {
        while (true) {
            System.out.print("Entrez le polynôme (ex : 3x^2+4x-2) : ");
            String representation = saisie.nextLine().trim();
            try {
                return new Polynome(representation);
            } catch (IllegalArgumentException e) {
                System.out.println("Erreur : " + e.getMessage() + ". Recommencez.");
            }
        }
    }

    /**
     * Capture une entrée clavier et s'assure qu'elle correspond strictement à un entier relatif.
     * Repose une question en boucle en cas d'erreur de format.
     * @return l'entier saisi validé.
     */
    private static int lireEntierSaisi() {
        while (true) {
            try {
                return Integer.parseInt(saisie.nextLine().trim());
            } catch (NumberFormatException erreurDeSaisie) {
                System.out.print("Entier attendu, réessayez : ");
            }
        }
    }

    /**
     * Capture une entrée clavier et s'assure qu'elle correspond à un nombre réel (double).
     * Remplace à la volée les virgules par des points pour tolérer la notation française.
     * @return le nombre réel (double) saisi validé.
     */
    private static double lireReel() {
        while (true) {
            try {
                return Double.parseDouble(saisie.nextLine().trim().replace(',', '.'));
            } catch (NumberFormatException erreurDeSaisie) {
                System.out.print("Réel attendu, réessayez : ");
            }
        }
    }
}