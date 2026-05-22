package iut.info1.sae202.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import iut.info1.sae202.Polynome;

class PolynomeTest {
	
	/**
	 * Tests de validation du constructeur Polynome par les coefficients
	 * Ces tests couvrent les cas valides de construction de polynômes à partir de coefficients, incluant :
	 * - des polynômes classiques,
	 * - des constantes,
	 * - des polynômes avec des coefficients nuls,
	 * - des polynômes avec des valeurs décimales
	 * - des polynômes avec un nombre important de coefficients
	 */
    @Test
    void testPolynomeCoefficientsValides() {
        assertDoesNotThrow(() -> new Polynome(new double[] {-2, 4, 3}));            //polynome classique (3 coefficients)
        assertDoesNotThrow(() -> new Polynome(new double[] {5}));                   //constante
        assertDoesNotThrow(() -> new Polynome(new double[] {-8}));                   //constante négative
        assertDoesNotThrow(() -> new Polynome(new double[] {0, 0, 1}));             //x^2
        assertDoesNotThrow(() -> new Polynome(new double[] {12.5, -7.345, 65.8})); //valeurs décimales
        assertDoesNotThrow(() -> new Polynome(new double[] {0}));                    //cas particulier polynome nul
        assertDoesNotThrow(() -> new Polynome(new double[] {5, -3, 8, 12, -9, 7, 
                                                            6, 10, 54, -21}));     //polynome classique (10 coefficients)
        assertDoesNotThrow(() -> new Polynome(new double[] {-1, 0, 6.24, -8, 4.3, 
                                                            14.9, -7.21, 13, 0, 
                                                            -2.5, 11, -3.9, 10, 17, 
                                                            1.7}));                //polynome avec valeurs décimales (15 coefficients)
    }
    
    /**
     * Tests d'invalidation du constructeur de Polynom par les coefficients
     * Ces tests couvrent les cas invalides suivants : 
     * - tableau de coefficients null ou vide,
     * - coefficients nuls de plus haut degré,
     * - coefficients NaN ou infinis 
     */
    @Test
    void testPolynomeCoefficientsInvalides() {
        assertThrows(IllegalArgumentException.class,
                     () -> new Polynome((double[]) null),
                     "Tableau de coefficients NULL accepté");
        assertThrows(IllegalArgumentException.class,
                 () -> new Polynome(new double[] {}),
                 "Tableau de coefficients vide accepté");
        assertThrows(IllegalArgumentException.class,
                 () -> new Polynome(new double[] {-7, 4, 0, 0}),
                 "Coefficients nuls de plus haut degré accepté");
        assertThrows(IllegalArgumentException.class,
                 () -> new Polynome(new double[] {Double.NaN, 4, -7}),
                 "Coefficient NaN accepté");
        assertThrows(IllegalArgumentException.class,
                 () -> new Polynome(new double[] {Double.POSITIVE_INFINITY}),
                 "Coefficient +infini accepté");
        assertThrows(IllegalArgumentException.class,
                 () -> new Polynome(new double[] {Double.NEGATIVE_INFINITY}),
                 "Coefficient -infini accepté");
    }
    
    /**
     * Tests de validation du constructeur de Polynome par les racines
     * Ces tests couvrent les cas valides suivants :
     * - polynômes avec des racines simples,
     * - polynômes avec des racines multiples (double, triple),
     * - polynômes avec des racines distinctes et des racines multiples
     */
    @Test
    void testPolynomeRacinesValides() {
        assertDoesNotThrow(() -> new Polynome(new double[] {3, -1},
                                              new int[] {1, 1}, 2));        //Polynome simple
        assertDoesNotThrow(() -> new Polynome(new double[] {2},
                                              new int[] {2}, 1));            //Racine double
        assertDoesNotThrow(() -> new Polynome(new double[] {-1, 0, 5},
                                              new int[] {1, 1, 1}, -3));    //Trois racines distinctes
        assertDoesNotThrow(() -> new Polynome(new double[] {1},
                                              new int[] {3}, 1));            //Racine triple
        assertDoesNotThrow(() -> new Polynome(new double[] {-2, 4},
                                              new int[] {2, 1}, 2));        //Multiplicités mixtes
    }
    
    
    /**
     * Tests d'invalidation du constructeur de Polynome par les racines
     * Ces tests couvrent les cas invalides suivants :
     * - tableau de racines null,
     * - tableau de racines contenant des NaN ou des infinis,
     * - tableau d'ordre de multiplicité null,
     * - tableau d'ordre de multiplicité contenant des 0 ou des valeurs négatives,
     * - tableaux de racines et d'ordre de multiplicité de longueur différente,
     * - coefficient du plus haut monôme nul, NaN ou infini
     */
    @Test
    void testPolynomeRacinesInvalides() {
        assertThrows(IllegalArgumentException.class,
                     () -> new Polynome(null, new int[] {1}, 2),
                     "Tableau de racines null accepté");                                 //tableau de racines null
        assertThrows(IllegalArgumentException.class,
                     () -> new Polynome(new double[] {Double.NaN}, new int[] {1}, 2),
                     "Racine NaN acceptée");                                            //tableau de racines contenant uniquement un NaN
        assertThrows(IllegalArgumentException.class,
                     () -> new Polynome(new double[] {Double.POSITIVE_INFINITY},
                                         new int[] {1}, 2),
                     "Racine +infini acceptée");                                        //tableau de racines contenant +infini
        assertThrows(IllegalArgumentException.class,
                      () -> new Polynome(new double[] {Double.NEGATIVE_INFINITY},
                                     new int[] {1}, 2),
                      "Racine +infini acceptée");                                        //tableau de racines contenant -infini
        assertThrows(IllegalArgumentException.class,
                     () -> new Polynome(new double[] {3, Double.NaN},
                                         new int[] {1, 1}, 2),
                     "Racine NaN acceptée");                                            //tableau de racines contenant un NaN
        assertThrows(IllegalArgumentException.class,
                     () -> new Polynome(new double[] {1}, null, 2),
                     "Ordre de multiplicité null accepté");                                //tableau d'ordre de multiplicité null
        assertThrows(IllegalArgumentException.class,
                      () -> new Polynome(new double[] {1}, new int[] {0}, 2),
                      "Ordre de multiplicité unique nul accepté");                        //tableau d'ordre de multiplicité contenant uniquement un 0
        assertThrows(IllegalArgumentException.class,
                      () -> new Polynome(new double[] {1}, new int[] {-1}, 2),
                      "Ordre de multiplicité unique négatif accepté");                    //tableau d'ordre de multiplicité contenant uniquement une valeur négative
        assertThrows(IllegalArgumentException.class,
                      () -> new Polynome(new double[] {1, 2}, new int[] {1,0}, 2),
                      "Ordre de multiplicité nul accepté");                                //tableau d'ordre de multiplicité contenant un 0
        assertThrows(IllegalArgumentException.class,
                      () -> new Polynome(new double[] {1, 2}, new int[] {1, -2}, 2),
                      "Ordre de multiplicité négatif accepté");                            //tableau d'ordre de multiplicité contenant une valeur négative
        assertThrows(IllegalArgumentException.class,
                      () -> new Polynome(new double[] {1, 2, 3}, new int[] {1, 1}, 2),
                      "Nombre de racines et d'ordres de multiplicités différent accepté");    //tableaux de racines et d'ordre de multiplicité de longueur différente
        assertThrows(IllegalArgumentException.class,
                      () -> new Polynome(new double[] {1}, new int[] {1, 1}, 2),
                      "Nombre de racines et d'ordres de multiplicités différent accepté");
        assertThrows(IllegalArgumentException.class,
                      () -> new Polynome(new double[] {1}, new int[] {1}, 0),
                      "Coefficient nul du plus haut monôme accepté");                    //Coefficient du plus haut monôme nul
        assertThrows(IllegalArgumentException.class,
                      () -> new Polynome(new double[] {1}, new int[] {1}, Double.NaN),
                      "Coefficient NaN du plus haut monôme accepté");                    //Coefficient du plus haut monôme NaN
        assertThrows(IllegalArgumentException.class,
                       () -> new Polynome(new double[] {1}, new int[] {1}, Double.POSITIVE_INFINITY),
                       "Coefficient +infini du plus haut monôme accepté");                    //Coefficient du plus haut monôme +infini
        assertThrows(IllegalArgumentException.class,
                        () -> new Polynome(new double[] {1}, new int[] {1}, Double.NEGATIVE_INFINITY),
                        "Coefficient -infini du plus haut monôme accepté");                    //Coefficient du plus haut monôme -infini
    }
    
    /**
     * Tests de validation du constructeur de Polynome par une chaine de caractères
     * Ces tests couvrent les cas valides suivants :
     * - polynômes de degré 0, 1, 2, 3 ou 4
     * - polynômes avec des coefficients nuls,
     * - polynômes avec des coefficients décimaux,
     */
    @Test
    void testPolynomeStringValides() {
		assertDoesNotThrow(() -> new Polynome("2x^2+5x+3")); // polynome classique de degré 2
		assertDoesNotThrow(() -> new Polynome("7x+5")); // polynome classique de degré 1
		assertDoesNotThrow(() -> new Polynome("x^4+4x^3-3x^2-5x+2")); // polynome classique de degré 4
		assertDoesNotThrow(() -> new Polynome("-6")); // constante négative
		assertDoesNotThrow(() -> new Polynome("2.0x^2+5.0x+3.0")); // valeurs décimales
    }
    
    /**
     * Tests d'invalidation du constructeur de Polynome par une chaine de caractères
     * Ces tests couvrent les cas invalides suivants :
     * - chaine de caractères null ou vide,
     * - chaine de caractères avec une syntaxe incorrecte (ex : "2x^2++5x", "x^4+4x^3-3x^2-5x+2x^-1", "2.0x^2.5+5.0x+3.0"),
     * - chaine de caractères avec des coefficients NaN ou infinis (ex : "NaNx^2+5x+3", "2x^2+5x+infini")
     */
    @Test
    void testPolynomeStringInvalides() {
    	assertThrows(IllegalArgumentException.class,
    				 () -> new Polynome((String) null),
    				 "Chaine de caractères NULL acceptée"); // chaine de caractères null
    	assertThrows(IllegalArgumentException.class,
    				 () -> new Polynome(""),
    				 "Chaine de caractères vide acceptée"); // chaine de caractères vide
    	assertThrows(IllegalArgumentException.class,
    				 () -> new Polynome("2x^2++5x"),
    				 "Syntaxe incorrecte n°1 acceptée"); // syntaxe incorrecte (double opérateur)
    	assertThrows(IllegalArgumentException.class,
    				 () -> new Polynome("x^4 + 4x^3 - 3x^2 - 5x + 2x^-1"),
    				 "Syntaxe incorrecte n°2 acceptée"); // syntaxe incorrecte (puissance négative)
    	assertThrows(IllegalArgumentException.class,
    				 () -> new Polynome("2.0x^2.5+5.0x+3.0"),
    				 "Syntaxe incorrecte n°3 acceptée"); // syntaxe incorrecte (puissance décimale)
    	assertThrows(IllegalArgumentException.class,
    				 () -> new Polynome("NaNx^2+5x+3"),
    				 "Coefficient NaN accepté"); // coefficient NaN
    	assertThrows(IllegalArgumentException.class,
    				 () -> new Polynome("2x^2 + 5x + infini"),
    				 "Coefficient infini accepté"); // coefficient +infini
    	assertThrows(IllegalArgumentException.class,
    				 () -> new Polynome("2x^2+5x-infini"),
    				 "Coefficient -infini accepté"); // coefficient -infini
    	assertThrows(IllegalArgumentException.class,
    				 () -> new Polynome("2x^NaN+5x+5"),
    				 "Coefficient NaN accepté"); // puissance NaN
    }
    
    /**
     * Tests de validation de la méthode d'accès au degré d'un polynôme
     * Ces tests couvrent les cas suivants :
     * - polynômes de degré 0 (constantes),
     * - polynômes de degré 1 (droites),
     * - polynômes de degré 2 (paraboles),
     * - polynômes de degré 3 (cubiques)
     */
    @Test
    void testGetDegre() {
        assertEquals(0, new Polynome(new double[] {5}).getDegre(),
                     "Echec des coefficients sur un degré nul");            //Constructeur coefficients degré 0
        assertEquals(1, new Polynome(new double[] {-2, 3}).getDegre(),
                     "Echec des coefficients sur un degré 1");                //Constructeur coefficients degré 1
        assertEquals(2, new Polynome(new double[] {1, 0, 1}).getDegre(),
                      "Echec des coefficients sur un degré 2");                    //Constructeur coefficients degré 2
        assertEquals(2, new Polynome(new double[] {5, -3},
                                     new int[] {1, 1}, 4).getDegre(),
                      "Echec des racines sur un degré 2");                        //Constructeur racines degré 2
        assertEquals(3, new Polynome(new double[] {-1, 4},
                                     new int[] {2, 1}, 8).getDegre(),
                      "Echec des racines sur un degré 3");                        //Constructeur racines degré 3
    }
    
    /**
     * Tests de validation de la méthode d'accès aux coefficients d'un polynôme
     * Ces tests couvrent les cas suivants :
     * - polynômes de degré 0 (constantes),
     * - polynômes de degré 1 (droites),
     * - polynômes de degré 2 (paraboles),
     * - polynômes de degré 3 (cubiques),
     * - polynômes avec des coefficients nuls,
     * - polynômes avec des coefficients décimaux,
     * - polynômes construits à partir de racines simples,
     * - polynômes construits à partir de racines (distinctes) multiples
     */
    @Test
    void testGetCoefficients() {
        assertArrayEquals(new double[] {5},
                          new Polynome(new double[] {5}).getCoefficients(),
                          "Echec des coefficients sur une constante");
        assertArrayEquals(new double[] {-2, 4, 3},
                          new Polynome(new double[] {-2, 4, 3}).getCoefficients(),
                          "Echec des coefficients sur un polynôme de degré 2");
        assertArrayEquals(new double[] {0, 0, -1},
                          new Polynome(new double[] {0, 0, -1}).getCoefficients(),
                          "Echec des coefficients sur un polynôme de degré 2 sans autre coefficient");
        assertArrayEquals(new double[] {-2, 1},
                          new Polynome(new double[] {2}, new int[] {1}, 1).getCoefficients(),
                          1e-9,
                          "Echec des racines sur un polynôme de degré 1");
        assertArrayEquals(new double[] {4, -4, 1},
                          new Polynome(new double[] {2}, new int[] {2}, 1).getCoefficients(),
                          1e-9,
                          "Echec des racines sur un polynôme de degré 2 avec une racine double");
        assertArrayEquals(new double[] {3, -4, 1},
                          new Polynome(new double[] {1, 3}, new int[] {1, 1}, 1).getCoefficients(),
                          1e-9,
                          "Echec des racines sur un polynôme de degré 2 avec"
                          + "deux racines simples et un coefficient = 1");
        assertArrayEquals(new double[] {6, -8, 2},
                          new Polynome(new double[] {1, 3}, new int[] {1, 1}, 2).getCoefficients(),
                          1e-9,
                          "Echec des racines sur un polynôme de degré 2 avec"
                          + "deux racines simples et un coefficient = 2");
    }
    
    /**
     * Tests de validation de la méthode d'accès aux limites d'un polynôme
     * Ces tests couvrent les cas suivants :
     * - polynômes de degré pair avec un coefficient dominant positif,
     * - polynômes de degré pair avec un coefficient dominant négatif,
     * - polynômes de degré impair avec un coefficient dominant positif,
     * - polynômes de degré impair avec un coefficient dominant négatif,
     * - idem pour le constructeur par les racines
     */
    @Test
    void testGetLimites() {
        assertArrayEquals(new double[] {Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY},
                           new Polynome(new double[] {2, -1, 3}).getLimites(),
                           "Echec des coefficients sur un coef positif de degré pair");
        assertArrayEquals(new double[] {Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY},
                           new Polynome(new double[] {2, -1, -3}).getLimites(),
                           "Echec des coefficients sur un coef négatif de degré pair");
        assertArrayEquals(new double[] {Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY},
                          new Polynome(new double[] {2, -1, 3, 6}).getLimites(),
                           "Echec des coefficients sur un coef positif de degré impair");
        assertArrayEquals(new double[] {Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY},
                           new Polynome(new double[] {2, -1, 3, -6}).getLimites(),
                           "Echec des coefficients sur un coef négatif de degré impair");
        assertArrayEquals(new double[] {Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY},
                           new Polynome(new double[] {2, 3}, new int[] {1, 1}, 2).getLimites(),
                           "Echec des racines sur un coef positif de degré pair");
        assertArrayEquals(new double[] {Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY},
                            new Polynome(new double[] {2, 3}, new int[] {1, 1}, -2).getLimites(),
                           "Echec des racines sur un coef négatif de degré pair");
        assertArrayEquals(new double[] {Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY},
                           new Polynome(new double[] {2, 3}, new int[] {1, 2}, 2).getLimites(),
                           "Echec des racines sur un coef positif de degré impair");
        assertArrayEquals(new double[] {Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY},
                           new Polynome(new double[] {2, 3}, new int[] {2, 1}, -2).getLimites(),
                           "Echec des racines sur un coef négatif de degré impair");
        assertArrayEquals(new double[] {Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY},
                          new Polynome(new double[] {3}).getLimites(),
                          "Echec des coefficients sur une constante positive");
        assertArrayEquals(new double[] {Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY},
                          new Polynome(new double[] {-3}).getLimites(),
                          "Echec des coefficients sur une constante négative");
        assertArrayEquals(new double[] {Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY},
                          new Polynome(new double[] {}, new int[] {}, 2).getLimites(),
                          "Echec des racines sur une constante positive");
        assertArrayEquals(new double[] {Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY},
                          new Polynome(new double[] {}, new int[] {}, -3).getLimites(),
                          "Echec des racines sur une constante négative");
    }
    
    /**
     * Tests de validation de la méthode d'accès aux racines d'un polynôme
     * Ces tests couvrent les cas suivants :
     * - polynômes avec une unique racine de multiplicité 5,
     * - polynômes sans racines,
     * - polynômes avec une unique racine double,
     * - polynômes avec deux racines simples,
     * - polynômes avec cinq racines simples, doubles et/ou triples,
     * - polynômes construits par les coefficients (levée d'exception)
     */
    @Test
    void testGetRacines() {
    	assertArrayEquals(new double[] {2},
                          new Polynome(new double[] {2}, new int[] {5}, 1).getRacines(),
                          "Echec des racines sur un polynôme avec une unique racine de multiplicité 5");
        assertArrayEquals(new double[] {},
                          new Polynome(new double[] {}, new int[] {}, 5).getRacines(),
                          "Echec sur un polynôme sans racines");
        assertArrayEquals(new double[] {3.5},
                           new Polynome(new double[] {3.5}, new int[] {2}, 1).getRacines(),
                           "Echec des racines sur un polynôme avec une unique racine double");
        assertArrayEquals(new double[] {-1, 4.2},
                           new Polynome(new double[] {-1, 4.2}, new int[] {1, 1}, 2).getRacines(),
                           "Echec des racines sur un polynôme avec deux racines simples");
        assertArrayEquals(new double[] {-2, -1, 0, 1, 2},
                           new Polynome(new double[] {-2, -1, 0, 1, 2}, 
                                        new int[] {1, 1, 1, 1, 1}, 1).getRacines(),
                           "Echec des racines sur un polynôme avec cinq racines simples");
        assertArrayEquals(new double[] {2.3, -3, 1, -1, 4.2},
                          new Polynome(new double[] {2.3, -3, 1, -1, 4.2}, 
                                       new int[] {1, 1, 1, 2, 2}, 4).getRacines(),
                          "Echec des racines sur un polynôme avec cinq racines simples et doubles");
        assertArrayEquals(new double[] {-2, -1, 0, 1, 2},
                          new Polynome(new double[] {-2, -1, 0, 1, 2}, 
                                       new int[] {3, 3, 3, 3, 3}, 1).getRacines(),
                          "Echec des racines sur un polynôme avec cinq racines triples");
        assertThrows(UnsupportedOperationException.class,
                      () -> new Polynome(new double[] {1, 2, 3}).getRacines(),
                      "Echec de la levée d'exception sur un polynôme construit par coefficients");
    }
    
    /**
     * Tests de validation de la méthode de multiplication d'un polynôme
     * par un réel ou par un autre polynôme
     * Ces tests couvrent les cas suivants :
     * - multiplication par un réel entier/décimal positif/négatif,
     * - multiplication par un polynôme de degré 0, 1 ou 2
     * - multiplication de deux polynômes de degré 2,
     * - multiplication de deux polynômes de degré 4 (polynômes avec des racines multiples)
     */
	@Test
	void testMultiplication() {
	    assertEquals(new Polynome(new double[] {6, 12, 15}),
	                 new Polynome(new double[] {2, 4, 5}).multiplication(3),
	                 "Echec des coefficients sur un polynôme de degré 2 "
	                 + "multiplié par un réel entier positif");
	    assertEquals(new Polynome(new double[] {10, 20, 25}),
	                 new Polynome(new double[] {4, 8, 10}).multiplication(2.5),
	                 "Echec des coefficients sur un polynôme de degré 2 "
	                 + "multiplié par un réel décimal positif");
	    assertEquals(new Polynome(new double[] {10}),
	                 new Polynome(new double[] {5}).multiplication(2),
	                 "Echec des coefficients sur un polynôme de degré 0 "
	                 + "multiplié par un réel entier positif");
	    assertEquals(new Polynome(new double[] {-14}),
	                 new Polynome(new double[] {-4}).multiplication(3.5),
	                 "Echec des coefficients sur un polynôme de degré 0 "
	                 + "multiplié par un réel décimal positif");
	    assertEquals(new Polynome(new double[] {-4, -8, -10, -12, -4}),
	                 new Polynome(new double[] {2, 4, 5, 6, 2}).multiplication(-2),
	                 "Echec des coefficients sur un polynôme de degré 4 "
	                 + "multiplié par un réel entier négatif");
	    assertEquals(new Polynome(new double[] {-7, -14, -17.5}),
	                 new Polynome(new double[] {2, 4, 5}).multiplication(-3.5),
	                 "Echec des coefficients sur un polynôme de degré 2 "
	                 + "multiplié par un réel décimal négatif");
	    assertEquals(new Polynome(new double[] {-24}),
	                 new Polynome(new double[] {6}).multiplication(-4),
	                 "Echec des coefficients sur un polynôme de degré 0 "
	                 + "multiplié par un réel entier négatif");
	    assertEquals(new Polynome(new double[] {10.5}),
	                 new Polynome(new double[] {-3}).multiplication(-3.5),
	                 "Echec des coefficients sur un polynôme de degré 0 "
	                 + "multiplié par un réel décimal négatif");
	
	    assertEquals(new Polynome(new double[] {48, -40, 8}),
	                 new Polynome(new double[] {2, 3}, new int[] {1, 1}, 2).multiplication(4),
	                 "Echec des racines sur un polynôme de degré 2 "
	                 + "multiplié par un réel entier positif");
	    assertEquals(new Polynome(new double[] {90, -52.5, 7.5}),
	                 new Polynome(new double[] {4, 3}, new int[] {1, 1}, 3).multiplication(2.5),
	                 "Echec des coefficients sur un polynôme de degré 2 "
	                 + "multiplié par un réel décimal positif");
	    assertEquals(new Polynome(new double[] {6}),
	                 new Polynome(new double[] {}, new int[] {}, 3).multiplication(2),
	                 "Echec des coefficients sur un polynôme de degré 0 "
	                 + "multiplié par un réel entier positif");
	    assertEquals(new Polynome(new double[] {-17.5}),
	                 new Polynome(new double[] {}, new int[] {}, -5).multiplication(3.5),
	                 "Echec des coefficients sur un polynôme de degré 0 "
	                 + "multiplié par un réel décimal positif");
	    assertEquals(new Polynome(new double[] {80, -20, -10}),
	                 new Polynome(new double[] {2, -4}, new int[] {1, 1}, 5).multiplication(-2),
	                 "Echec des coefficients sur un polynôme de degré 2 "
	                 + "multiplié par un réel entier négatif");
	    assertEquals(new Polynome(new double[] {-42, -49, 21, 21, -7}),
	                 new Polynome(new double[] {-1, 2, 3}, new int[] {2, 1, 1}, 2).multiplication(-3.5),
	                 "Echec des coefficients sur un polynôme de degré 4 "
	                 + "multiplié par un réel décimal négatif");
	    assertEquals(new Polynome(new double[] {-24}),
	                 new Polynome(new double[] {}, new int[] {}, 6).multiplication(-4),
	                 "Echec des coefficients sur un polynôme de degré 0 "
	                 + "multiplié par un réel entier négatif");
	    assertEquals(new Polynome(new double[] {-14}),
	                 new Polynome(new double[] {}, new int[] {}, 4).multiplication(-3.5),
	                 "Echec des coefficients sur un polynôme de degré 0 "
	                 + "multiplié par un réel décimal négatif");
	
	    assertEquals(new Polynome(new double[] {6}),
	                 new Polynome(new double[] {2}).multiplication(new Polynome(new double[] {3})),
	                 "Echec des coefficients sur un polynôme de degré 0 "
	                 + "multiplié par un polynôme de degré 0");
	    assertEquals(new Polynome(new double[] {6, 9}),
	                 new Polynome(new double[] {2, 3}).multiplication(new Polynome(new double[] {3})),
	                 "Echec des coefficients sur un polynôme de degré 1 "
	                 + "multiplié par un polynôme de degré 0");
	    assertEquals(new Polynome(new double[] {6, 9}),
	                 new Polynome(new double[] {3}).multiplication(new Polynome(new double[] {2, 3})),
	                 "Echec des coefficients sur un polynôme de degré 0 "
	                 + "multiplié par un polynôme de degré 1");
	    assertEquals(new Polynome(new double[] {6, 11, 6, 1}),
	                 new Polynome(new double[] {2, 3, 1}).multiplication(new Polynome(new double[] {3, 1})),
	                 "Echec des coefficients sur un polynôme de degré 2 "
	                 + "multiplié par un polynôme de degré 1 entier positif");
	    assertEquals(new Polynome(new double[] {4, 12, 13, 6, 1}),
	                 new Polynome(new double[] {2, 3, 1}).multiplication(new Polynome(new double[] {2, 3, 1})),
	                 "Echec des coefficients sur un polynôme de degré 2 "
	                 + "multiplié par un polynôme de degré 2 entier positif");
	    assertEquals(new Polynome(new double[] {5.0, 10.5, 7, 1.5}),
	                 new Polynome(new double[] {2.0, 3.0, 1.0}).multiplication(new Polynome(new double[] {2.5, 1.5})),
	                 "Echec des coefficients sur un polynôme de degré 2 "
	                 + "multiplié par un polynôme de degré 1 décimal positif");
	    assertEquals(new Polynome(new double[] {-6, 7, -2}),
	                 new Polynome(new double[] {-2, 1}).multiplication(new Polynome(new double[] {3, -2})),
	                 "Echec des coefficients sur un polynôme de degré 1 "
	                 + "multiplié par un polynôme de degré 1 entier négatif");
	    assertEquals(new Polynome(new double[] {-7.0, 13.5, -1.0, -1.5}),
	                 new Polynome(new double[] {-2.0, 3.0, 1.0}).multiplication(new Polynome(new double[] {3.5, -1.5})),
	                 "Echec des coefficients sur un polynôme de degré 2 "
	                 + "multiplié par un polynôme de degré 1 décimal négatif");
	    assertEquals(new Polynome(new double[] {2, 7, 9, 7, 6, 4, 1}),
	                 new Polynome(new double[] {2, 3, 1, 2, 1}).multiplication(new Polynome(new double[] {1, 2, 1})),
	                 "Echec des coefficients sur un polynôme de degré 4 "
	                 + "multiplié par un polynôme de degré 2 entier positif");
	
	    assertEquals(new Polynome(new double[] {24, -32, 14, -2}),
	                 new Polynome(new double[] {2, 3}, new int[] {1, 1}, 2).multiplication(new Polynome(new double[] {2, -1})),
	                 "Echec des racines sur un polynôme de degré 2 "
	                 + "multiplié par un polynôme de degré 1 entier positif");
	    assertEquals(new Polynome(new double[] {144, -240, 148, -40, 4}),
	                 new Polynome(new double[] {2, 3}, new int[] {1, 1}, 2).multiplication(new Polynome(new double[] {2, 3}, new int[] {1, 1}, 2)),
	                 "Echec des racines sur deux polynômes de degré 2 "
	                 + "multipliés entiers positifs");
	    assertEquals(new Polynome(new double[] {6, 9}),
	                 new Polynome(new double[] {}, new int[] {}, 3).multiplication(new Polynome(new double[] {2, 3})),
	                 "Echec des racines sur un polynôme de degré 0 "
	                 + "multiplié par un polynôme de degré 1 entier positif");
	    assertEquals(new Polynome(new double[] {-15}),
	                 new Polynome(new double[] {}, new int[] {}, -5).multiplication(new Polynome(new double[] {3})),
	                 "Echec des racines sur un polynôme de degré 0 "
	                 + "multiplié par un polynôme de degré 0 entier positif");
	    assertEquals(new Polynome(new double[] {-24, 32, -14, 2}),
	                 new Polynome(new double[] {2, 3}, new int[] {1, 1}, 2).multiplication(new Polynome(new double[] {-2, 1})),
	                 "Echec des racines sur un polynôme de degré 2 "
	                 + "multiplié par un polynôme de degré 1 entier négatif");
	    assertEquals(new Polynome(new double[] {-42.0, -49.0, 21.0, 21.0, -7.0}),
	                 new Polynome(new double[] {-1, 2, 3}, new int[] {2, 1, 1}, 2).multiplication(new Polynome(new double[] {-3.5})),
	                 "Echec des racines sur un polynôme de degré 4 "
	                 + "multiplié par un polynôme de degré 0 décimal négatif");
  }
	
	/**
     * Tests de validation de la méthode de division euclidienne d'un polynôme
     * par un autre polynôme. Ces tests couvrent les cas suivants :
     * - division par une constante entière/décimale positive/négative,
     * - division d'un polynôme par lui-même (quotient = 1, reste = 0),
     * - division exacte (reste nul) de polynômes de degré 0, 1, 2 et 3,
     * - division avec reste non nul, vérification du quotient ET du reste,
     * - division par un polynôme de degré supérieur (quotient = 0, reste = dividende),
     * - division de polynômes construits par les racines,
     * - cas invalides levant une IllegalArgumentException
     */
	@Test
	void testDivision() {
	    assertArrayEquals(new Polynome[] {new Polynome(new double[] {2, 4, 5}), new Polynome(new double[] {0})},
	                	  new Polynome(new double[] {6, 12, 15}).division(new Polynome(new double[] {3})),
	                      "Echec des coefficients sur un polynôme de degré 2 "
	                      + "divisé par une constante entière positive");
	    assertArrayEquals(new Polynome[] {new Polynome(new double[] {2, 3}), new Polynome(new double[] {0})},
	                      new Polynome(new double[] {5, 7.5}).division(new Polynome(new double[] {2.5})),
	                      "Echec des coefficients sur un polynôme de degré 1 "
	                      + "divisé par une constante décimale positive");
	    assertArrayEquals(new Polynome[] {new Polynome(new double[] {-2, -3, -1}), new Polynome(new double[] {0})},
	                      new Polynome(new double[] {6, 9, 3}).division(new Polynome(new double[] {-3})),
	                      "Echec des coefficients sur un polynôme de degré 2 "
	                      + "divisé par une constante entière négative");
	    assertArrayEquals(new Polynome[] {new Polynome(new double[] {-1.6}), new Polynome(new double[] {0})},
	                      new Polynome(new double[] {-8}).division(new Polynome(new double[] {5})),
	                      "Echec des coefficients sur un polynôme de degré 0 "
	                      + "divisé par une constante entière positive");
	    assertArrayEquals(new Polynome[] {new Polynome(new double[] {1}), new Polynome(new double[] {0})},
	                	  new Polynome(new double[] {2, 4, 5}).division(new Polynome(new double[] {2, 4, 5})),
	                	  "Echec des coefficients sur un polynôme divisé par lui-même");
	    assertArrayEquals(new Polynome[] {new Polynome(new double[] {1}), new Polynome(new double[] {0})},
	                	  new Polynome(new double[] {6, 11, 6, 1}).division(new Polynome(new double[] {6, 11, 6, 1})),
	                	  "Echec des coefficients sur un polynôme de degré 3 divisé par lui-même");
	    assertArrayEquals(new Polynome[] {new Polynome(new double[] {1}), new Polynome(new double[] {0})},
	                	  new Polynome(new double[] {3}).division(new Polynome(new double[] {3})),
	                	  "Echec des coefficients sur une constante divisée par elle-même");
	    assertArrayEquals(new Polynome[] {new Polynome(new double[] {3, 1}), new Polynome(new double[] {0})},
	                	  new Polynome(new double[] {6, 5, 1}).division(new Polynome(new double[] {2, 1})),
	                	  "Echec des coefficients sur un polynôme de degré 2 "
	                	  + "divisé par un polynôme de degré 1 sans reste");
	    assertArrayEquals(new Polynome[] {new Polynome(new double[] {3, 1}), new Polynome(new double[] {0})},
	                	  new Polynome(new double[] {6, 11, 6, 1}).division(new Polynome(new double[] {2, 3, 1})),
	                	  "Echec des coefficients sur un polynôme de degré 3 "
	                	  + "divisé par un polynôme de degré 2 sans reste");
	    assertArrayEquals(new Polynome[] {new Polynome(new double[] {1, 1}), new Polynome(new double[] {-1})},
	                	  new Polynome(new double[] {1, 3, 1}).division(new Polynome(new double[] {2, 1})),
	                	  "Echec des coefficients sur un polynôme de degré 2 "
	                	  + "divisé par un polynôme de degré 1 avec reste");
	    assertArrayEquals(new Polynome[] {new Polynome(new double[] {3, 2}), new Polynome(new double[] {2, -1})},
	                	  new Polynome(new double[] {5, 1, 3, 2}).division(new Polynome(new double[] {1, 0, 1})),
	                	  "Echec des coefficients sur un polynôme de degré 3 "
	                	  + "divisé par un polynôme de degré 2 avec reste");
	    assertArrayEquals(new Polynome[] {new Polynome(new double[] {0}), new Polynome(new double[] {1, 1})},
	                	  new Polynome(new double[] {1, 1}).division(new Polynome(new double[] {1, 0, 1})),
	                	  "Echec des coefficients quand le diviseur est de degré supérieur");
	    assertArrayEquals(new Polynome[] {new Polynome(new double[] {-3, 1}), new Polynome(new double[] {0})},
	                	  new Polynome(new double[] {2, 3}, new int[] {1, 1}, 1).division(new Polynome(new double[] {-2, 1})),
	                	  "Echec des racines sur un polynôme de degré 2 "
	                	  + "divisé par un de ses facteurs");
	    assertThrows(IllegalArgumentException.class,
	                  () -> new Polynome(new double[] {1, 2, 3}).division(new Polynome(new double[] {0})),
	                  "Division par le polynôme nul aurait dû lever une IllegalArgumentException");
	}
    
	/**
	 * Tests de validation de la méthode d'accès à la représentation textuelle d'un polynôme
	 * Ces tests couvrent les cas suivants :
	 * - polynômes de degré 0, 1, 2, 3 ou 4
	 * - polynômes avec des coefficients nuls,
	 * - polynômes avec des coefficients décimaux,
	 * - polynômes construits à partir de racines (distinctes) multiples
	 */
    @Test
    void testToString() {
        assertEquals("2.0x^2 + 5.0x + 3.0", new Polynome(new double[] {3, 5, 2}).toString(),
                     "Echec des coefficients 3, 5 et 2");
        assertEquals("7.0x + 5.0", new Polynome(new double[] {5, 7}).toString(),
                     "Echec des coefficients 5 et 7");
        assertEquals("x^4 + 4.0x^3 - 3.0x^2 - 5.0x + 2.0", new Polynome(new double[] {2, -5, -3, 4, 1}).toString(),
                     "Echec des coefficients 2, -5, -3, 4 et 1");
        assertEquals("x^4 + 2.0", new Polynome(new double[] {2, 0, 0, 0, 1}).toString(),
                     "Echec des coefficients 2, 0, 0, 0 et 1");
        assertEquals(" - 6.0", new Polynome(new double[] {-6}).toString(),
                     "Echec du coefficient 6");
        assertEquals("2.0x^2 + 5.0x + 3.0",
                     new Polynome(new double[] {-1, -1.5}, new int[] {1, 1}, 2).toString(),
                     "Echec des racines -1 et -1.5 avec coefficient 2");
        assertEquals("7.0x + 5.0",
                     new Polynome(new double[] {-5.0/7.0}, new int[] {1}, 7).toString(),
                     "Echec de la racine -5/7 avec coefficient 7");
        assertEquals(" - 6.0",
                     new Polynome(new double[] {}, new int[] {}, -6).toString(),
                     "Echec du coefficient -6");
        assertEquals("x^4 - 4.0x^3 + 3.0x^2 + 4.0x - 4.0",
                      new Polynome(new double[] {2, 1, -1}, new int[] {2, 1, 1}, 1).toString(),
                      "Echec des racines 2 (ordre 2), 1 et -1 avec coefficient 1");
    }
    
    /**
     * Tests de validation de la méthode d'addition et de soustraction de polynômes
     * Ces tests couvrent les cas suivants :
     * - addition de deux polynômes de même degré,
     * - addition de deux polynômes de degrés différents,
     * - addition de polynômes construits par les coefficients et par les racines
     * - addition de polynômes avec des coefficients négatifs et décimaux
     * - addition de polynômes de degré 0
     */
    @Test
    void testAddition() {
        assertEquals(new Polynome(new double[] {16, -10}),
                     new Polynome(new double[] {4, 20})
                         .addition(new Polynome(new double[] {12, -30})),
                     "Echec addition de deux polynômes de degré 1");
        assertEquals(new Polynome(new double[] {15, -5, -8}),
                     new Polynome(new double[] {2, -10, 4})
                         .addition(new Polynome(new double[] {13, 5, -12})),
                     "Echec addition de deux polynômes de degré 2");
        assertEquals(new Polynome(new double[] {-6, -2, -2, 4}),
                     new Polynome(new double[] {1, -5, 2, 3})
                         .addition(new Polynome(new double[] {-7, 3, -4, 1})),
                     "Echec addition de deux polynômes de degré 3");
        assertEquals(new Polynome(new double[] {30, 3, -7, 20, 3}),
                     new Polynome(new double[] {32, -1, -10, 5, 3})
                         .addition(new Polynome(new double[] {-2, 4, 3, 15})),
                     "Echec addition d'un polynome de degré 4 et d'un polynome de degré 3");
        assertEquals(new Polynome(new double[] {-4, 37, -40, -3, 21, 12}),
                     new Polynome(new double[] {-11, 31, -54})
                         .addition(new Polynome(new double[] {7, 6, 14, -3, 21, 12})),
                     "Echec addition d'un polynome de degré 2 et d'un polynome de degré 5");
        assertEquals(new Polynome(new double[] {6, 2, 3, 4, 5}),
                     new Polynome(new double[] {1, 2, 3, 4, 5})
                         .addition(new Polynome(new double[] {5})),
                     "Echec addition polynôme degré 4 et degré 0");
        assertEquals(new Polynome(new double[] {-23, 53, -66, 2}),
                     new Polynome(new double[] {-11, 31, -54})
                         .addition(new Polynome(new double[] {1, 2, 3},
                                                new int[] {1, 1, 1},
                                                2)),
                     "Echec addition polynôme degré 2 et polynôme degré 3 constructeur 2");
        assertEquals(new Polynome(new double[] {-14, 31, -51}),
                     new Polynome(new double[] {-11, 31, -54})
                         .addition(new Polynome(new double[] {-1, 1},
                                                new int[] {1, 1},
                                                3)),
                     "Echec addition polynôme degré 2 constructeur 1 et degré 2 constructeur 2");
        assertEquals(new Polynome(new double[] {1, 4, 3, 4, 5}),
                     new Polynome(new double[] {1, 2, 3, 4, 5})
                         .addition(new Polynome(new double[] {0},
                                                new int[] {1},
                                                2)),
                     "Echec addition polynôme degré 4 constructeur 1 et degré 1 constructeur 2");
        assertEquals(new Polynome(new double[] {0, 0, 1}),
                     new Polynome(new double[] {3, 5, 1})
                         .addition(new Polynome(new double[] {-3, -5})),
                     "Echec addition avec coefficients négatifs");
        assertEquals(new Polynome(new double[] {3.5, 2.0}),
                     new Polynome(new double[] {1.5, 2.0})
                         .addition(new Polynome(new double[] {2.0})),
                     "Echec addition avec coefficients décimaux");
        assertEquals(new Polynome(new double[] {7}),
                     new Polynome(new double[] {3})
                         .addition(new Polynome(new double[] {4})),
                     "Echec addition deux polynômes de degré 0");
    }
    
    /**
     * Tests de validation de la méthode de soustraction de polynômes
     * Ces tests couvrent les cas suivants :
     * - soustraction de deux polynômes de même degré,
     * - soustraction de deux polynômes de degrés différents,
     * - soustraction de polynômes construits par les coefficients et par les racines
     * - soustraction de polynômes avec des coefficients négatifs et décimaux
     * - soustraction de polynômes de degré 0
     */
    @Test
    void testSoustraction() {
        assertEquals(new Polynome(new double[] {2, 10}),
                     new Polynome(new double[] {4, 20})
                         .soustraction(new Polynome(new double[] {2, 10})),
                     "Echec soustraction de deux polynômes de degré 1");
        assertEquals(new Polynome(new double[] {-11, -15, 16}),
                     new Polynome(new double[] {2, -10, 4})
                         .soustraction(new Polynome(new double[] {13, 5, -12})),
                     "Echec soustraction de deux polynômes de degré 2");
        assertEquals(new Polynome(new double[] {25, -5, -13, -10, 3}),
                     new Polynome(new double[] {32, -1, -10, 5, 3})
                         .soustraction(new Polynome(new double[] {7, 4, 3, 15})),
                     "Echec soustraction d'un polynôme de degré 4 et d'un polynôme de degré 3");
        assertEquals(new Polynome(new double[] {6, -10, 1}),
                     new Polynome(new double[] {3, 5, 1})
                         .soustraction(new Polynome(new double[] {-3, 15})),
                     "Echec soustraction avec coefficients négatifs");
        assertEquals(new Polynome(new double[] {-0.5, 2.0}),
                     new Polynome(new double[] {1.5, 2.0})
                         .soustraction(new Polynome(new double[] {2.0})),
                     "Echec soustraction avec coefficients décimaux");
        assertEquals(new Polynome(new double[] {-1}),
                     new Polynome(new double[] {3})
                         .soustraction(new Polynome(new double[] {4})),
                     "Echec soustraction deux polynômes de degré 0");
        assertEquals(new Polynome(new double[] {0}),
                     new Polynome(new double[] {5})
                         .soustraction(new Polynome(new double[] {5})),
                     "Echec soustraction d'un polynôme par lui-même");
        assertEquals(new Polynome(new double[] {-4, 2, 3, 4, 5}),
                     new Polynome(new double[] {1, 2, 3, 4, 5})
                         .soustraction(new Polynome(new double[] {5})),
                     "Echec soustraction polynôme degré 4 et degré 0");
    }
    
    /**
     * Tests de la méthode de calcul de l'image d'un polynôme en un point donné
     * Ces tests couvrent les cas suivants :
     * - image d'une constante,
     * - image d'un polynôme de degré 1, 2, 3 et 4
     */
    @Test
	void testImage() {
		assertEquals(3.0, new Polynome(new double[] {3}).image(5), 1e-9,
					 "Echec de l'image d'une constante");
		assertEquals(-6.0, new Polynome(new double[] {2, -4}).image(2), 1e-9,
					 "Echec de l'image d'un polynôme de degré 1");
		assertEquals(15.0, new Polynome(new double[] {1, -5, 6}).image(2), 1e-9,
					 "Echec de l'image d'un polynôme de degré 2");
		assertEquals(-2.0, new Polynome(new double[] {2, -3, 4, -5}).image(1), 1e-9,
					 "Echec de l'image d'un polynôme de degré 3");
		assertEquals(0.0, new Polynome(new double[] {1, -4, 6, -4, 1}).image(1), 1e-9,
					 "Echec de l'image d'un polynôme de degré 4");
		assertEquals(359.0/9.0, new Polynome(new double[] {45, -32, 50}).image(1.0/3.0), 1e-9,
					 "Echec de l'image d'un polynome de degré 2 avec une image décimale");
	}
    
    /**
     * Tests de validation de la méthode de calcul de dérivée d'un polynôme
     * Ces tests couvrent les cas suivants :
     * - dérivée d'une constante,
     * - dérivée d'un polynôme de degré 1, 2, 3, 4 et 5
     */
    @Test
    void testDerivee() {
    	assertEquals(new Polynome(new double[] {0}),
    				 new Polynome(new double[] {5}).derivee(),
    				 "Echec de la dérivée d'une constante");
    	assertEquals(new Polynome(new double[] {1}),
    				 new Polynome(new double[] {7, 1}).derivee(),
    				 "Echec de la dérivée d'un polynôme de degré 1");
		assertEquals(new Polynome(new double[] {5, 6}),
					 new Polynome(new double[] {1, 5, 3}).derivee(),
					 "Echec de la dérivée d'un polynôme de degré 2");
		assertEquals(new Polynome(new double[] {6, 3, 5}),					 // 3x^2 + 3x + 6
					 new Polynome(new double[] {46, 6, 3.0/2.0, 5.0/3.0}).derivee(), //5/3x^3 + 3/2x^2 + 6x + 46
					 "Echec de la dérivée d'un polynome de degré 3");
		assertEquals(new Polynome(new double[] {2, 6, 12}),
				 	 new Polynome(new double[] {1, 2, 3, 4}).derivee(),
				 	 "Echec de la dérivée d'un polynome de degré 4");
		assertEquals(new Polynome(new double[] {6, -16, 36, -16, 30}),
				 	 new Polynome(new double[] {-3, 6, -8, 12, -4, 6}).derivee(),
				 	 "Echec de la dérivée d'un polynome de degré 5");
    }
    
    /**
     * Tests de validation de la méthode de calcul de la primitive d'un polynôme
     * Ces tests couvrent les cas suivants :
     * - primitive d'un polynôme nul
     * - primitive d'un polynôme de degré 1, 2, 3 et 4
     */
    @Test
    void testPrimitive() {
    	assertEquals(new Polynome(new double[] {0}), //constante k
					 new Polynome(new double[] {0}).primitive(),
					 "Echec de la primitive d'un polynome nul");
    	assertEquals(new Polynome(new double[] {0, 1}),
					 new Polynome(new double[] {1}).primitive(),
					 "Echec de la primitive d'une constante");
		assertEquals(new Polynome(new double[] {0, 5, 3}),
					 new Polynome(new double[] {5, 6}).primitive(),
					 "Echec de la primitive d'un polynôme de degré 1");
		assertEquals(new Polynome(new double[] {0, 6, 3.0/2.0, 5.0/3.0}),
					 new Polynome(new double[] {6, 3, 5}).primitive(),
					 "Echec de la primitive d'un polynome de degré 2");
		assertEquals(new Polynome(new double[] {0, 2, 3, 4}),
				 	 new Polynome(new double[] {2, 6, 12}).primitive(),
				 	 "Echec de la primitive d'un polynome de degré 3");
		assertEquals(new Polynome(new double[] {0, 6, -8, 12, -4, 6}),
				 	 new Polynome(new double[] {6, -16, 36, -16, 30}).primitive(),
				 	 "Echec de la primitive d'un polynome de degré 4");
    }
    
    /**
     * Tests de validation de la méthode de calcul de l'intégrale d'un polynôme
     * Ces tests couvrent les cas suivants :
     * - intégrale d'un polynôme nul
     * - intégrale d'un polynôme de degré 1, 2, 3 et 4
     * - cas où a et b sont égaux (intégrale nulle)
     */
    @Test
    void testIntegrale() {
		assertEquals(0.0, new Polynome(new double[] {1, 2, 3}).integrale(1, 1),
					 "Echec de l'intégrale d'un polynôme entre deux bornes égales");
		assertEquals(5.0, new Polynome(new double[] {5}).integrale(0, 1),
					 "Echec de l'intégrale d'une constante entre 0 et 1");
		assertEquals(12.0, new Polynome(new double[] {1, 2}).integrale(0, 3),
					 "Echec de l'intégrale d'un polynôme de degré 1 entre 0 et 3");
		assertEquals(39.0, new Polynome(new double[] {1, 2, 3}).integrale(0, 3),
					 "Echec de l'intégrale d'un polynôme de degré 2 entre 0 et 3");
		assertEquals(237.0/2.0, new Polynome(new double[] {5, 6, 4, 2}).integrale(0, 3),
					 "Echec de l'intégrale d'un polynôme de degré 3 entre 0 et 3");
		assertEquals(364.0/3.0, new Polynome(new double[] {1, 6, 4}).integrale(-5, 2), 1e-9,
				 "Echec de l'intégrale d'un polynôme de degré 2 entre -5 et 2");
		assertEquals(-2600.0/3.0, new Polynome(new double[] {3, -9, -4}).integrale(-10, -2), 1e-9,
				 "Echec de l'intégrale d'un polynôme de degré 2 entre -10 et -2");
		assertEquals(-180, new Polynome(new double[] {5, -12, 3}).integrale(7, -3), 1e-9,
				 "Echec de l'intégrale d'un polynôme de degré 2 entre 7 et -3");
    }
    
    /**
     * Tests de validation de la méthode de calcul de la moyenne d'un polynôme
     * Ces tests couvrent les cas suivants :
     * - moyenne d'un polynôme nul
     * - moyenne d'une constante
     * - moyenne d'un polynôme de degré 1, 2, 3 et 4
     * - cas où a et b sont égaux (moyenne égale à l'image du polynôme en ce point)
     */
	@Test
	void testMoyenne() {
	    assertEquals(0.0, new Polynome(new double[] {0}).moyenne(0, 1), 1e-9,
	        "Echec de la moyenne d'un polynôme nul");
	    assertEquals(5.0, new Polynome(new double[] {5}).moyenne(0, 1), 1e-9,
	        "Echec de la moyenne d'une constante entre 0 et 1");
	    assertEquals(-3.0, new Polynome(new double[] {-3}).moyenne(2, 5), 1e-9,
	        "Echec de la moyenne d'une constante négative");
	    assertEquals(4.0, new Polynome(new double[] {1, 2}).moyenne(0, 3), 1e-9,
	        "Echec de la moyenne d'un polynôme de degré 1 entre 0 et 3");
	    assertEquals(13.0, new Polynome(new double[] {1, 2, 3}).moyenne(0, 3), 1e-9,
	        "Echec de la moyenne d'un polynôme de degré 2 entre 0 et 3");
	    assertEquals(39.5, new Polynome(new double[] {5, 6, 4, 2}).moyenne(0, 3), 1e-9,
	        "Echec de la moyenne d'un polynôme de degré 3 entre 0 et 3");
	    assertEquals(31.0, new Polynome(new double[] {1, 2, 3, 4, 5}).moyenne(0, 2), 1e-9,
	        "Echec de la moyenne d'un polynôme de degré 4 entre 0 et 2");
	    assertEquals(5.0, new Polynome(new double[] {5}).moyenne(3, 3), 1e-9,
	        "Echec de la moyenne quand a = b (constante)");
	    assertEquals(3.0, new Polynome(new double[] {1, 2}).moyenne(1, 1), 1e-9,
	        "Echec de la moyenne quand a = b (degré 1, image en x=1)");
	    assertEquals(17.0, new Polynome(new double[] {1, 2, 3}).moyenne(2, 2), 1e-9,
	        "Echec de la moyenne quand a = b (degré 2, image en x=2)");
	}

}
