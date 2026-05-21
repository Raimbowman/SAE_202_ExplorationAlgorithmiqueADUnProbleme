/*
 * Polynome.java                    17/04/2026
 * IUT de Rodez, BUT1 2025-2026, pas de copyright
 */
package iut.info1.sae202;

/**
 * Polynôme sous la forme Kx^n + Kx^n-1 + ... + Kx^0 avec K un réel quelconque
 * 
 * @author Noam LACOMBE
 */
public class Polynome {

    private final String MESSAGE_ERREUR_COEFFICIENTS = "Tableau de coefficients invalide";

    private final String MESSAGE_ERREUR_RACINES = "Tableaux de racines, d'ordre de multiplicité "
            + "ou de coefficient du plus haut monôme invalides";

    private final String MESSAGE_ERREUR_GET_RACINES = "Racines disponibles pour un polynome construit "
            + "à partir de ses racines";

    /**
     * différents coefficients du polynôme construit avec le constructeur prenant en
     * paramètres un tableau des coefficients
     */
    private double[] coefficients;

    /**
     * racines réelles du polynome construit avec le constructeur prenant en
     * paramètres les racines, ordre de multiplicité et plus haut coefficient
     */
    private double[] racines;

    /**
     * ordre de multiplicité de chacune des racines du polynome construit avec le
     * constructeur prenant en paramètres les racines, ordre de multiplicité et plus
     * haut coefficient
     */
    private int[] ordresMultiplicite;

    /**
     * coefficient du monôme de plus haut degré du polynome construit avec le
     * constructeur prenant en paramètres les racines, ordre de multiplicité et plus
     * haut coefficient
     */
    private double plusHautCoefficient;

    /**
     * numero du constructeur utilisé pour construire le polynome : 1 = constructeur
     * prenant en paramètre les coefficients 2 = constructeur prenant en paramètres
     * les racines, leur ordre de multiplicité et le coefficient du monôme de plus
     * haut degré
     */
    private byte numeroConstructeur;

    /**
     * Polynôme du degré choisi sous la forme : Kx^n + Kx^n-1 + ... + Kx^0 avec K un
     * réel quelconque choisi par l'utilisateur
     * 
     * @param coefficients les différents coefficients du polynôme, donnés dans
     *                     l'ordre croissant du degré. Exemple : le tableau [-2, 4,
     *                     3] donne le polynome 3x^2 + 4x - 2
     * @throws IllegalArgumentException si polynôme invalide
     */
    public Polynome(double[] tabCoefficients) {
        if (coefficientsNotValide(tabCoefficients)) {
            throw new IllegalArgumentException(MESSAGE_ERREUR_COEFFICIENTS);
        }
        for (int indice = 0; indice < tabCoefficients.length; indice++) {
            if (!Double.isFinite(tabCoefficients[indice])) {
                throw new IllegalArgumentException(MESSAGE_ERREUR_COEFFICIENTS);
            }
        }

        coefficients = tabCoefficients;
        numeroConstructeur = 1;
        plusHautCoefficient = tabCoefficients[tabCoefficients.length - 1];
    }

    /**
     * Polynôme du degré choisi sous la forme : Kx^n + Kx^n-1 + ... + Kx^0 avec K un
     * réel quelconque choisi par l'utilisateur
     * 
     * @param tabRacines           différentes racines (réelles) du polynôme
     * @param tabOrdreMultiplicite ordre de multiplicité de chacune des racines
     * @param hautCoefficient      coefficient du monôme de plus haut degré
     * @throws IllegalArgumentException si polynôme invalide
     */
    public Polynome(double[] tabRacines, int[] tabOrdreMultiplicite, double hautCoefficient) {
        if (racinesNotValide(tabRacines, tabOrdreMultiplicite, hautCoefficient)) {
            throw new IllegalArgumentException(MESSAGE_ERREUR_RACINES);
        }
        for (int indice = 0; indice < tabRacines.length; // les longueurs des 2 tableaux sont déjà vérifiées au dessus
                indice++) {
            if (!Double.isFinite(tabRacines[indice]) || tabOrdreMultiplicite[indice] <= 0) {
                throw new IllegalArgumentException(MESSAGE_ERREUR_RACINES);
            }
        }

        racines = tabRacines;
        ordresMultiplicite = tabOrdreMultiplicite;
        plusHautCoefficient = hautCoefficient;
        numeroConstructeur = 2;
        coefficients = this.getCoefficients();
    }
    
    /**
     * Polynôme du degré choisi sous la forme : Kx^n + Kx^n-1 + ... + Kx^0 avec K un
     * réel quelconque choisi par l'utilisateur, à partir d'une représentation textuelle du polynome
     * @param representation représentation textuelle d'un polynome, par exemple "3x^2 + 4x - 2"
     */
	public Polynome(String representation) {
		double[] coefficientsExtraits = parseCoefficientsFromString(representation);
	    if (coefficientsNotValide(coefficientsExtraits)) {
	        throw new IllegalArgumentException("Représentation invalide");
	    }
	    coefficients = coefficientsExtraits;
	    numeroConstructeur = 1;
	    plusHautCoefficient = coefficientsExtraits[coefficientsExtraits.length - 1];
	}

    /**
     * Transforme une représentation textuelle d'un polynome pour en extraire
     * les coefficients grâce à un REGEX
	 * @param representation représentation textuelle d'un polynome, par exemple "3x^2 + 4x - 2"
	 * @return un tableau de coefficients correspondant à la représentation
	 * 	       textuelle du polynome, dans l'ordre croissant du degré
	 */
	private static double[] parseCoefficientsFromString(String representation) {
		/* Vérifie si la String est nulle */
		if (representation == null || representation.isBlank()) {
	        throw new IllegalArgumentException("Représentation invalide");
		}
		
		/* Vérifie si un opérateur est répété 2 fois ou plus d'affilée*/
	    if (representation.matches(".*[+\\-]{2,}.*")) {
	        throw new IllegalArgumentException("Représentation invalide");
	    }

	    String noSpaces = representation.replaceAll("\\s+", "");
	    if (!noSpaces.matches("([+-]?(?:\\d+\\.?\\d*)?x\\^[0-9]+|[+-]?(?:\\d+\\.?\\d*)?x|[+-]?\\d+\\.?\\d*)+")) {
	        throw new IllegalArgumentException("Représentation invalide");
	    }

	    java.util.regex.Matcher matcher = java.util.regex.Pattern
	        .compile("[+-]?(?:\\d+\\.?\\d*)?x\\^([0-9]+)|[+-]?(?:\\d+\\.?\\d*)?x|[+-]?\\d+\\.?\\d*")
	        .matcher(noSpaces);

	    /* Trouver le degré maximum */
	    int maxDegre = 0;
	    while (matcher.find()) {
	        String terme = matcher.group();
	        int degre = 0;
	        if (terme.contains("x^")) {
	            degre = Integer.parseInt(terme.split("x\\^")[1]);
	        } else if (terme.contains("x")) {
	            degre = 1;
	        }
	        if (degre > maxDegre) {
	        	maxDegre = degre;
	        }
	    }

	    /* Remplir le tableau */
	    double[] coefficients = new double[maxDegre + 1];
	    matcher.reset();
	    while (matcher.find()) {
	        String terme = matcher.group();
	        int degre;
	        double coef;

	        if (terme.contains("x^")) {
	            String[] parts = terme.split("x\\^");
	            degre = Integer.parseInt(parts[1]);
	            String coefStr = parts[0];
	            coef = coefStr.isEmpty() || coefStr.equals("+") ? 1.0
	                 : coefStr.equals("-") ? -1.0 : Double.parseDouble(coefStr);
	        } else if (terme.contains("x")) {
	            degre = 1;
	            String coefStr = terme.replace("x", "");
	            coef = coefStr.isEmpty() || coefStr.equals("+") ? 1.0
	                 : coefStr.equals("-") ? -1.0 : Double.parseDouble(coefStr);
	        } else {
	            degre = 0;
	            coef = Double.parseDouble(terme);
	        }

	        if (!Double.isFinite(coef)) {
	            throw new IllegalArgumentException("Représentation invalide");
	        }

	        coefficients[degre] += coef;
	    }

	    if (maxDegre > 0 && coefficients[maxDegre] == 0) {
	        throw new IllegalArgumentException("Représentation invalide");
	    }

	    return coefficients;
	}

	/**
     * Vérifie la validité du tableau de coefficients en vérifiant :
     * - un tableau null
     * - un tableau vide
     * - un tableau contenant des 0 inutiles à la fin (pour les monômes de plus haut degré)
     * @param coefficients les différents coefficients du polynôme, donnés dans
     *                     l'ordre croissant du degré.
     * @return false si les valeurs du tableau sont valides, true sinon
     */
    private static boolean coefficientsNotValide(double[] coefficients) {
        return coefficients == null || coefficients.length == 0
                || coefficients.length > 1 && coefficients[coefficients.length - 1] == 0;
    }

    /**
     * Vérifie la validité des paramètres du second constructeur en vérifiant : -
     * des tableau null - un différence du nombre de valeurs dans les racines et les
     * ordres de multiplicité - un coefficient du monôme de plus haut degré nul - un
     * coefficient du monôme de plus haut degré NaN ou infini
     * 
     * @param tabRacines           différentes racines (réelles) du polynôme
     * @param tabOrdreMultiplicite ordre de multiplicité de chacune des racines
     * @param hautCoefficient      coefficient du monôme de plus haut degré
     * @return false si les valeurs des paramètres sont valides, true sinon
     */
    private static boolean racinesNotValide(double[] tabRacines, int[] tabOrdreMultiplicite, double hautCoefficient) {
        return tabRacines == null || tabOrdreMultiplicite == null || tabRacines.length != tabOrdreMultiplicite.length
                || hautCoefficient == 0 || !Double.isFinite(hautCoefficient);
    }

    /**
     * @return degré du polynome (degré du monôme de plus haut degré)
     */
    public double getDegre() {
        return coefficients.length - 1;
    }

    /**
     * @return coefficients du polynôme
     */
    public double[] getCoefficients() {
        if (numeroConstructeur == 1) {
            return coefficients;
        } else {
            double[] resultat = new double[] { 1 };
            for (int indiceRacines = 0; indiceRacines < racines.length; indiceRacines++) {
                for (int valeurOrdreMultiplicite = 0; valeurOrdreMultiplicite < ordresMultiplicite[indiceRacines]; valeurOrdreMultiplicite++) {
                    Polynome temp = new Polynome(resultat);
                    double[] facteur = new double[] { -racines[indiceRacines], 1 };
                    resultat = temp.multiplication(new Polynome(facteur)).coefficients;
                }
            }
            return (new Polynome(resultat).multiplication(plusHautCoefficient).coefficients);
        }
    }

    /**
     * @return limites du polynôme en -infini et +infini (dans cet ordre)
     */
    public double[] getLimites() {
        if ((coefficients.length - 1) % 2 == 0) {
            if (coefficients[coefficients.length - 1] > 0) {
                return new double[] { Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY };
            } else {
                return new double[] { Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY };
            }
        } else {
            if (coefficients[coefficients.length - 1] > 0) {
                return new double[] { Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY };
            } else {
                return new double[] { Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY };
            }
        }
    }

    /**
     * @return racines réelles du polynôme (si elles existent)
     */
    public double[] getRacines() {
        if (numeroConstructeur == 2) {
            return racines;
        } else {
            throw new UnsupportedOperationException(MESSAGE_ERREUR_GET_RACINES);
        }
    }

    @Override
    /** @see java.lang.Object#toString() */
    public String toString() {
        String chaine = "";
        for (int indice = coefficients.length - 1; indice >= 0; indice--) {
            double coef = coefficients[indice];
            double absCoef = Math.abs(coef);

            if (coef == 0) continue; // Ignore les termes nuls

            if (!chaine.isEmpty() && coef > 0) {
                chaine += " + ";
            } else if (coef < 0) {
                chaine += " - ";
            }
            if (indice == 0) {
                chaine += absCoef;
            } else if (indice == 1) {
                if (absCoef != 1) chaine += absCoef;
                chaine += "x";
            } else {
                if (absCoef != 1) chaine += absCoef;
                chaine += "x^" + indice;
            }
        }
        return chaine.isEmpty() ? "0" : chaine;
    }

    /**
     * Addtionne le polynome par un un autre polynome
     * 
     * @param secondPolynome polynome par lequel additionner le premier polynome
     * @return un tableau de coefficients qui correspondent aux coefficients du
     *         polynome résultat
     */
    public Polynome addition(Polynome secondPolynome) {
        int taillePolynome;
        if (coefficients.length > secondPolynome.coefficients.length) {
            taillePolynome = coefficients.length;
        } else {
            taillePolynome = secondPolynome.coefficients.length;
        }

        double[] somme = new double[taillePolynome];
        for (int indiceTab1 = 0; indiceTab1 < coefficients.length; indiceTab1++) {
            somme[indiceTab1] += coefficients[indiceTab1];
        }
        for (int indiceTab2 = 0; indiceTab2 < secondPolynome.coefficients.length; indiceTab2++) {
            somme[indiceTab2] += secondPolynome.coefficients[indiceTab2];
        }
        return new Polynome(tronquer(somme));
    }
    
    /**
     * Soustrait le polynome par un un autre polynome en paramètre
     * @param secondPolynome polynome par lequel soustraire le premier polynome
     * @return un polynome contenant les coefficients du résultat
     */
    public Polynome soustraction(Polynome secondPolynome) {
    	secondPolynome = secondPolynome.multiplication(-1);
    	Polynome resultat = this.addition(secondPolynome);
    	return resultat;
    }

    /**
     * Multiplie le polynome par un réel
     * @param reel nombre par lequel multiplier le polynome
     * @return un tableau de coefficients qui correspondent aux coefficients du
     *         polynome résultat
     */
    public Polynome multiplication(double reel) {
        return multiplication(new Polynome(new double[] { reel }));
    }

    /**
     * Multiplie le polynome par un un autre polynome
     * 
     * @param secondPolynome polynome par lequel multiplier le premier polynome
     * @return un tableau de coefficients qui correspondent aux coefficients du
     *         polynome résultat
     */
    public Polynome multiplication(Polynome secondPolynome) {

        double[] produit = new double[(coefficients.length - 1) + (secondPolynome.coefficients.length - 1) + 1];
        for (int indiceTab1 = 0; indiceTab1 < coefficients.length; indiceTab1++) {
            for (int indiceTab2 = 0; indiceTab2 < secondPolynome.coefficients.length; indiceTab2++) {
                produit[indiceTab1 + indiceTab2] += coefficients[indiceTab1] * secondPolynome.coefficients[indiceTab2];
            }
        }
        return new Polynome(tronquer(produit));
    }
    
    /**
     * Divise le polynome par un un autre polynome en paramètre
     * @param polynomeDiviseur polynome par lequel diviser le premier polynome
     * @return un tableau de 2 polynomes : le premier correspond au quotient
     *         de la division, le second correspond au reste de la division
     * @throws IllegalArgumentException si le polynome diviseur est nul ou si
     * 		   un des polynômes a un degré infini (ce qui peut arriver si on
     *         construit un polynome à partir de ses racines et que l'une
     *         d'entre elles est infinie)
     */
    public Polynome[] division(Polynome polynomeDiviseur) {
        if (polynomeDiviseur.getDegre() < 0) {
            throw new IllegalArgumentException("Division par un polynome nul impossible");
        } else if (!Double.isFinite(this.getDegre()) || !Double.isFinite(polynomeDiviseur.getDegre())) {
            throw new IllegalArgumentException("Division par un polynome de degré infini impossible");
        }

        Polynome polynomeQuotient = new Polynome(new double[] { 0 });
        Polynome polynomeReste = this;
        double degreePrecedent = polynomeReste.getDegre() + 1; // protection boucle infinie

        while (polynomeReste.getDegre() >= polynomeDiviseur.getDegre()) {
            if (polynomeReste.getDegre() >= degreePrecedent) { // le degré n'a pas diminué
                break;
            }
            degreePrecedent = polynomeReste.getDegre();

            double coefQuotient = polynomeReste.coefficients[polynomeReste.coefficients.length - 1]
                    / polynomeDiviseur.coefficients[polynomeDiviseur.coefficients.length - 1];
            int degreDiff = (int)(polynomeReste.getDegre() - polynomeDiviseur.getDegre());

            double[] coeffTemp = new double[degreDiff + 1];
            coeffTemp[degreDiff] = coefQuotient;
            Polynome temp = new Polynome(coeffTemp);

            polynomeQuotient = polynomeQuotient.addition(temp);
            polynomeReste = polynomeReste.soustraction(temp.multiplication(polynomeDiviseur));
        }
        return new Polynome[] {polynomeQuotient, polynomeReste};
    }
    
    /**
     * Tronque les zéros inutiles en tête du tableau de coefficients
     * (les zéros de plus haut degré), tout en conservant le polynôme nul [0]
     * 
     * @param aTronquer tableau de coefficients à tronquer
     * @return tableau tronqué sans les zéros inutiles de plus haut degré
     */
    private static double[] tronquer(double[] aTronquer) {
        int nouvelleTaille = aTronquer.length;
        while (nouvelleTaille > 1 && aTronquer[nouvelleTaille - 1] == 0) {
            nouvelleTaille--;
        }
        if (nouvelleTaille == aTronquer.length) {
            return aTronquer;
        }
        double[] resultat = new double[nouvelleTaille];
        System.arraycopy(aTronquer, 0, resultat, 0, nouvelleTaille);
        return resultat;
    }
    
    /**
     * Calcule l'image du polynome pour une valeur x donnée en paramètre,
     * en appliquant la méthode de Horner
     * @param x valeur à laquelle calculer l'image du polynome
     * @return l'image du polynome pour la valeur x donnée en paramètre
     */
	public double image(double x) {
		double resultat = 0;
		for (int indice = coefficients.length - 1; indice >= 0; indice--) {
			resultat = resultat * x + coefficients[indice];
		}
		return resultat;
	}
    
    /**
     * Calcule la dérivée du polynome en appliquant la formule de dérivation
     * d'un polynome : la dérivée de Kx^n est nKx^n-1
     * @return les coefficients du polynome dérivé
     */
	public Polynome derivee() {
		if (coefficients.length <= 1) {
			return new Polynome(new double[] {0});
		} // la dérivée d'un polynome constant ou nul est le polynome nul
		double[] resultat = new double[coefficients.length - 1];
		for (int indice = coefficients.length - 1; indice > 0; indice--) {
			resultat[indice - 1] = indice * coefficients[indice];
		}
		return new Polynome(resultat);
	}
	
	/**
	 * Calcule une primitive du polynome en appliquant la formule de primitivation
	 * d'un polynome : une primitive de Kx^n est K/(n+1)x^n+1
	 * @return les coefficients du polynome primitivé,
	 * 		   à noter que le constante k est représentée par un 0
	 */
	public Polynome primitive() {
		if (coefficients.length == 1 && coefficients[0] == 0) {
			return new Polynome(new double[] {0});
		} // cas du polynome nul, sa primitive est aussi le polynome nul
		double[] resultat = new double[coefficients.length + 1];
		resultat[0] = 0; // la constante d'intégration est représentée par un 0
		for (int indice = 0; indice < coefficients.length; indice++) {
			resultat[indice + 1] = coefficients[indice] / (indice + 1);
		}
		return new Polynome(resultat);
	}
    
	/**
	 * Calcule l'intégrale du polynome entre les limites a et b
	 * en appliquant la formule de calcul d'une intégrale définie :
	 * l'intégrale de a à b d'une fonction f est égale à F(b) - F(a)
	 * avec F une primitive de f
	 * @param a borne inférieure de l'intervalle d'intégration
	 * @param b borne supérieure de l'intervalle d'intégration
	 * @return l'intégrale du polynome entre les limites a et b
	 * @throws IllagalArgumentException si a est supérieur à b
	 */
	public double integrale(double a, double b) {
		if (a > b) {
			throw new IllegalArgumentException("La borne inférieure doit être"
					  + "inférieure ou égale à la borne supérieure");
		}
		return primitive().image(b) - primitive().image(a);
	}
	
	
	
	
    /**
     * Vérifie si le polynome est égal à un autre polynome en paramètre
     * en comparant les coefficients de chacun des polynomes
     * Permet de remplacer les assertArrayEquals dans les tests par
     * des assertEquals qui appellent automatiquement cette méthode
     * @return true si les polynomes sont égaux, false sinon
     */
	@Override
	public boolean equals(Object obj) {
	    if (obj == null || !(obj instanceof Polynome)) {
	        return false;
	    }
	    Polynome aComparer = (Polynome) obj;
	
	    // Vérifier que les deux polynômes ont le même nombre de coefficients
	    if (this.coefficients.length != aComparer.coefficients.length) {
	        return false;
	    }
	
	    // Comparer chaque coefficient avec une petite tolérance pour les décimaux
	    for (int i = 0; i < this.coefficients.length; i++) {
	        if (Math.abs(this.coefficients[i] - aComparer.coefficients[i]) > 1e-9) {
	            return false;
	        }
	    }
	    return true;
	}
}