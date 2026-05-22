

/*
 * Main.java                        17/04/2026
 * IUT de Rodez, BUT1 2025-2026, pas de copyright
 */
package iut.info1.sae202;

import java.util.Scanner;
import java.util.ArrayList;

/**
 * TODO
 * TODO
 * @author Anaëlle HINARD
 */
public class Main {

    private static final Scanner saisie = new Scanner(System.in);

    public static void main(String[] args1) {

        // Permet d'ajouter des polynômes à volonté sans avoir à fixer une limite
        ArrayList<Polynome> poly = new ArrayList<>();

        int choixAction;
        do {
            System.out.println("\n--- Que voulez-vous faire ? ---");
            System.out.println("  1. Construire des polynômes");
            System.out.println("  2. Afficher les polynômes créés");
            System.out.println("  3. Addition");
            System.out.println("  4. Soustraction");
            System.out.println("  5. Multiplication");
            System.out.println("  6. Division");
            System.out.println("  7. Afficher les limites d'un polynôme");
            System.out.println("  8. Afficher les racines d'un polynôme");
            System.out.println("  9. Quitter");
            System.out.print(" --> ");
            choixAction = lireEntierSaisi();
            if (choixAction < 1 || choixAction > 9) {
                System.out.println("Veuillez saisir un nombre entre 1 et 9.");
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

                    System.out.println(nbPolynomesVoulu + " polynôme(s) à créer.");

                    for (int i = 0; i < nbPolynomesVoulu; i++) {
                        int numeroActuel = poly.size() + 1;
                        System.out.println("\nCréation du polynôme P" + numeroActuel);

                        int choixConstructeur;
                        do {
                            System.out.println("Comment voulez-vous créer votre polynôme P" + numeroActuel + " ?");
                            System.out.println("  1. Par coefficients");
                            System.out.println("  2. Par racines");
                            System.out.print(" --> ");
                            choixConstructeur = lireEntierSaisi();
                            if (choixConstructeur != 1 && choixConstructeur != 2) {
                                System.out.println("Veuillez saisir 1 ou 2.");
                                System.out.println(" --> ");
                            }
                        } while (choixConstructeur != 1 && choixConstructeur != 2);

                        Polynome nouveauPoly;
                        if (choixConstructeur == 1) {
                            nouveauPoly = saisirParCoefficients();
                        } else {
                            nouveauPoly = saisirParRacines();
                        }
                        poly.add(nouveauPoly);
                        System.out.println("P" + numeroActuel + " = " + nouveauPoly.toString());
                    }
                    break;
                }
                case 2: {
                    if (poly.isEmpty()) {
                        System.out.println("Aucun polynôme n'a été enregistré pour le moment.");
                    } else {
                        System.out.println("\n--- Polynômes créés ---");
                        for (int i = 0; i < poly.size(); i++) {
                            System.out.println("P" + (i + 1) + " = " + poly.get(i).toString());
                        }
                    }
                    break;
                }
                case 3: {
                    if (poly.size() < 2) {
                        System.out.println("Il faut au moins 2 polynômes créés.");
                        break;
                    }
                    int p1 = choisirPolynome("premier", poly.size());
                    int p2 = choisirPolynome("second", poly.size());
                    Polynome resultatAdd = poly.get(p1).addition(poly.get(p2));
                    System.out.println("Résultat : " + resultatAdd.toString());
                    proposerEnregistrement(resultatAdd, poly);
                    break;
                }
                case 4: {
                    if (poly.size() < 2) {
                        System.out.println("Il faut au moins 2 polynômes créés.");
                        break;
                    }
                    int p1 = choisirPolynome("premier", poly.size());
                    int p2 = choisirPolynome("second", poly.size());
                    Polynome resultatSous = poly.get(p1).soustraction(poly.get(p2));
                    System.out.println("Résultat : " + resultatSous.toString());
                    proposerEnregistrement(resultatSous, poly);
                    break;
                }
                case 5: {
                    if (poly.isEmpty()) {
                        System.out.println("Il faut au moins 1 polynôme créé.");
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

                    if (choixMult == 1) {
                        if (poly.size() < 2) {
                            System.out.println("Il faut au moins 2 polynômes créés.");
                            break;
                        }
                        int p1 = choisirPolynome("premier", poly.size());
                        int p2 = choisirPolynome("second", poly.size());
                        Polynome resultatMult = poly.get(p1).multiplication(poly.get(p2));
                        System.out.println("Résultat : " + resultatMult.toString());
                        proposerEnregistrement(resultatMult, poly);
                    } else {
                        int p1 = choisirPolynome("à multiplier", poly.size());
                        System.out.print("Scalaire : ");
                        double scalaire = lireReel();
                        Polynome resultatMult = poly.get(p1).multiplication(scalaire);
                        System.out.println("Résultat : " + resultatMult.toString());
                        proposerEnregistrement(resultatMult, poly);
                    }
                    break;
                }
                case 6: {
                    if (poly.size() < 2) {
                        System.out.println("Il faut au moins 2 polynômes créés.");
                        break;
                    }
                    int p1 = choisirPolynome("dividende", poly.size());
                    int p2 = choisirPolynome("diviseur", poly.size());
                    try {
                        Polynome[] resultat = poly.get(p1).division(poly.get(p2));
                        System.out.println("Quotient : " + resultat[0].toString());
                        System.out.println("Reste    : " + resultat[1].toString());
                    } catch (IllegalArgumentException e) {
                        System.out.println("Erreur : " + e.getMessage());
                    }
                    break;
                }
                case 7: {
                    if (poly.isEmpty()) {
                        System.out.println("Aucun polynôme n'a été créé pour l'instant.");
                        break;
                    }
                    int p1 = choisirPolynome("", poly.size());
                    double[] limites = poly.get(p1).getLimites();
                    System.out.println("Limite en -∞ : " + limites[0]);
                    System.out.println("Limite en +∞ : " + limites[1]);
                    break;
                }
                case 8: {
                    if (poly.isEmpty()) {
                        System.out.println("Aucun polynôme créé pour l'instant.");
                        break;
                    }
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
                case 9:
                    System.out.println("Au revoir !");
                    break;
            }
        } while (choixAction != 9);
    }

    /**
     * Propose à l'utilisateur d'enregistrer le résultat comme nouveau polynôme
     *
     * @param resultat le polynôme résultat de l'opération
     * @param poly     la liste des polynômes
     */
    private static void proposerEnregistrement(Polynome resultat, ArrayList<Polynome> poly) {
        System.out.println("Voulez-vous enregistrer ce résultat comme nouveau polynôme ?");
        System.out.println("  1. Oui");
        System.out.println("  2. Non");
        System.out.print(" --> ");
        int choix;
        do {
            choix = lireEntierSaisi();
            if (choix != 1 && choix != 2) {
                System.out.println("Veuillez saisir 1 ou 2.");
                System.out.println(" --> ");
            }
        } while (choix != 1 && choix != 2);

        if (choix == 1) {
            poly.add(resultat);
            System.out.println("Polynôme enregistré en tant que P" + poly.size() + " !");
        }
    }

    /**
     * Demande à l'utilisateur de choisir un polynôme parmi ceux créés
     * et vérifie que le numéro saisi est valide.
     *
     * @param role du polynôme dans l'opération (ex: "premier", "second")
     * @param nbPolynomes nombre total de polynômes disponibles
     * @return l'indice (0-based) du polynôme choisi
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
     * Saisie d'un polynôme par ses coefficients (ordre croissant du degré).
     * Exemple : [-2, 4, 3] donne 3x² + 4x - 2
     *
     * @return le polynôme saisi
     */
    private static Polynome saisirParCoefficients() {
        System.out.print("Nombre de coefficients (= degré + 1) : \n --> ");
        int nbCoefficients;
        do {
            nbCoefficients = lireEntierSaisi();
            if (nbCoefficients <= 0) {
                System.out.print("Veuillez saisir un entier strictement positif ");
                System.out.print(" --> ");
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
     * Saisie d'un polynôme par ses racines, leurs ordres de multiplicité
     * et le coefficient du monôme de plus haut degré.
     * Exemple : racines {2, -1}, ordres {1, 1}, coefficient 3 donne 3(x-2)(x+1)
     *
     * @return le polynôme saisi
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
     * Verifie que l'entier saisi est bien un nombre
     * @return le nombre saisi
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
     * Verifie le nombre est saisi
     * et si il y a une virgule,
     * c'est remplacé par un point
     * @return le double modifié
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

