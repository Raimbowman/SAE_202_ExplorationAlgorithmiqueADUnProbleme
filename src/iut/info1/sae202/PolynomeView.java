/*
 * PolynomeView.java
 * IUT de Rodez, BUT1 2025-2026, pas de copyright
 * Vue MVC — interface JavaFX, fenêtre unique, style épuré
 */
package iut.info1.sae202;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * Vue JavaFX principale de l'application polynômes.
 * <p>
 * Architecture : une seule fenêtre divisée en trois colonnes :
 * <ol>
 *   <li>Colonne gauche  — liste des polynômes</li>
 *   <li>Colonne centre  — panneau d'opérations (onglets)</li>
 *   <li>Colonne droite  — tracé de courbe + résultat</li>
 * </ol>
 * La vue ne contient aucune logique métier : tout est délégué au contrôleur.
 * </p>
 *
 * @author Anaëlle HINARD
 * @author Noam LACOMBE
 * @author Eva GUENEGOU
 */
public class PolynomeView extends Application {

    // ── Palette ─────────────────────────────────
    private static final String BG           = "#F7F6F3";
    private static final String SURFACE      = "#FFFFFF";
    private static final String BORDER       = "#E0DED8";
    private static final String ACCENT       = "#2A5CFF";
    private static final String ACCENT_LIGHT = "#EEF2FF";
    private static final String TEXT_PRIMARY = "#1A1A1A";
    private static final String TEXT_MUTED   = "#888888";
    private static final String SUCCESS      = "#16A34A";
    private static final String DANGER       = "#DC2626";

    // ── Couleurs de courbe ───────────────────────
    private static final Color[] CURVE_COLORS = {
        Color.web(ACCENT),
        Color.web("#E05C2A"),
        Color.web("#16A34A"),
        Color.web("#9333EA"),
        Color.web("#CA8A04"),
        Color.web("#0891B2"),
    };

    // ── MVC ─────────────────────────────────────
    private PolynomeModel      model;
    private PolynomeController controller;

    // ── Composants persistants ───────────────────
    private ListView<String>   listeView;
    private Label              labelResultat;
    private Canvas             canvas;
    private Label              labelInfo;

    // Dernier résultat calculé (pour proposition d'ajout)
    private Polynome           dernierResultat;

    // ── Données du dernier tracé (pour la fenêtre agrandie) ─────────────
    private List<List<double[]>> dernierSeries;
    private List<String>         derniersLabels;
    private double               derniereXMin;
    private double               derniereXMax;

    // ─────────────────────────────────────────────
    //  Point d'entrée JavaFX
    // ─────────────────────────────────────────────

    @Override
    public void start(Stage stage) {
        model      = new PolynomeModel();
        controller = new PolynomeController(model);
        controller.setView(this);

        // Mise à jour automatique de la liste quand le modèle change
        model.getPolynomes().addListener((ListChangeListener<Polynome>) c -> rafraichirListe());

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG + ";");

        // En-tête
        root.setTop(buildHeader());

        // Corps principal
        HBox corps = new HBox(16);
        corps.setPadding(new Insets(16));
        corps.getChildren().addAll(
                buildColonneGauche(),
                buildColonneCentre(),
                buildColonneDroite()
        );
        HBox.setHgrow(corps.getChildren().get(1), Priority.ALWAYS);
        root.setCenter(corps);

        // Barre de statut
        root.setBottom(buildStatusBar());

        Scene scene = new Scene(root, 1200, 760);
        stage.setTitle("Polynômes — IUT Rodez");
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.show();
    }

    // ─────────────────────────────────────────────
    //  En-tête
    // ─────────────────────────────────────────────

    private Node buildHeader() {
        HBox bar = new HBox();
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(14, 20, 14, 20));
        bar.setStyle("-fx-background-color: " + SURFACE + "; -fx-border-color: " + BORDER
                + "; -fx-border-width: 0 0 1 0;");

        Label titre = new Label("∑ Polynômes");
        titre.setFont(Font.font("Georgia", FontWeight.BOLD, 20));
        titre.setStyle("-fx-text-fill: " + TEXT_PRIMARY + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnSauv  = smallButton("💾 Sauvegarder", ACCENT,    "#FFFFFF");
        Button btnCharg = smallButton("📂 Charger",    "#6B7280", "#FFFFFF");
        btnSauv.setOnAction(e  -> controller.sauvegarder());
        btnCharg.setOnAction(e -> controller.charger());

        bar.getChildren().addAll(titre, spacer, btnCharg, new Region() {{ setPrefWidth(8); }}, btnSauv);
        return bar;
    }

    // ─────────────────────────────────────────────
    //  Colonne gauche : liste des polynômes
    // ─────────────────────────────────────────────

    private Node buildColonneGauche() {
        VBox col = new VBox(10);
        col.setPrefWidth(260);
        col.setMinWidth(200);

        Label titre = sectionTitle("Mes polynômes");

        listeView = new ListView<>();
        listeView.setPrefHeight(9999);
        listeView.setStyle(
                "-fx-background-color: " + SURFACE + "; -fx-border-color: " + BORDER
                + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 13px;");
        VBox.setVgrow(listeView, Priority.ALWAYS);

        // Sélection multiple pour le tracé
        listeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        Button btnFiche  = fullButton("📋 Fiche récapitulative", ACCENT_LIGHT, ACCENT);
        Button btnSuppr  = fullButton("🗑  Supprimer",            "#FEF2F2",    DANGER);
        Button btnEffacer = fullButton("✕  Tout effacer",         "#F9FAFB",    TEXT_MUTED);

        btnFiche.setOnAction(e -> {
            int idx = listeView.getSelectionModel().getSelectedIndex();
            controller.ficheRecapitulative(idx);
        });
        btnSuppr.setOnAction(e -> {
            int idx = listeView.getSelectionModel().getSelectedIndex();
            controller.supprimerPolynome(idx);
        });
        btnEffacer.setOnAction(e -> {
            if (confirmer("Tout effacer", "Supprimer tous les polynômes de la liste ?")) {
                controller.toutEffacer();
            }
        });

        col.getChildren().addAll(titre, listeView, btnFiche, btnSuppr, btnEffacer);
        return col;
    }

    // ─────────────────────────────────────────────
    //  Colonne centre : onglets d'opérations
    // ─────────────────────────────────────────────

    private Node buildColonneCentre() {
        VBox col = new VBox(10);
        col.setPrefWidth(400);

        Label titre = sectionTitle("Opérations");

        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(tabs, Priority.ALWAYS);

        tabs.getTabs().addAll(
                tabCreer(),
                tabArithmetique(),
                tabAnalyse(),
                tabInterpolation()
        );

        col.getChildren().addAll(titre, tabs);
        VBox.setVgrow(col, Priority.ALWAYS);
        return col;
    }

    // ── Onglet Créer ────────────────────────────

    private Tab tabCreer() {
        Tab tab = new Tab("✚ Créer");
        VBox content = new VBox(14);
        content.setPadding(new Insets(16));
        content.setStyle("-fx-background-color: " + SURFACE + ";");

        // --- Par chaîne ---
        TitledPane paneChaine = new TitledPane();
        paneChaine.setText("Par expression  (ex : 3x^2+4x-2)");
        paneChaine.setStyle("-fx-font-size: 12px;");
        VBox boxChaine = new VBox(8);
        boxChaine.setPadding(new Insets(10));
        TextField fieldChaine = styledField("3x^2 + 4x - 2");
        Button btnChaine = accentButton("Ajouter");
        btnChaine.setOnAction(e -> controller.creerParChaine(fieldChaine.getText()));
        boxChaine.getChildren().addAll(fieldChaine, btnChaine);
        paneChaine.setContent(boxChaine);
        paneChaine.setExpanded(true);

        // --- Par coefficients ---
        TitledPane paneCoeff = new TitledPane();
        paneCoeff.setText("Par coefficients  (degré 0 → n, séparés par des espaces)");
        paneCoeff.setStyle("-fx-font-size: 12px;");
        VBox boxCoeff = new VBox(8);
        boxCoeff.setPadding(new Insets(10));
        TextField fieldCoeff = styledField("-2  4  3  →  3x² + 4x − 2");
        Label hintCoeff = hintLabel("Ordre croissant : d'abord le terme constant");
        Button btnCoeff = accentButton("Ajouter");
        btnCoeff.setOnAction(e -> controller.creerParCoefficients(fieldCoeff.getText()));
        boxCoeff.getChildren().addAll(fieldCoeff, hintCoeff, btnCoeff);
        paneCoeff.setContent(boxCoeff);
        paneCoeff.setExpanded(false);

        // --- Par racines ---
        TitledPane paneRac = new TitledPane();
        paneRac.setText("Par racines");
        paneRac.setStyle("-fx-font-size: 12px;");
        VBox boxRac = new VBox(8);
        boxRac.setPadding(new Insets(10));
        TextField fieldRac   = styledField("Racines  (ex : 2  3)");
        TextField fieldOrd   = styledField("Multiplicités  (ex : 1  1)");
        TextField fieldHaut  = styledField("Coeff. dominant  (ex : 1)");
        Button btnRac = accentButton("Ajouter");
        btnRac.setOnAction(e -> controller.creerParRacines(
                fieldRac.getText(), fieldOrd.getText(), fieldHaut.getText()));
        boxRac.getChildren().addAll(fieldRac, fieldOrd, fieldHaut, btnRac);
        paneRac.setContent(boxRac);
        paneRac.setExpanded(false);

        // Accordion pour n'ouvrir qu'un seul panneau à la fois
        Accordion accordion = new Accordion(paneChaine, paneCoeff, paneRac);
        accordion.setExpandedPane(paneChaine);
        VBox.setVgrow(accordion, Priority.ALWAYS);

        content.getChildren().add(accordion);
        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color: " + SURFACE + "; -fx-border-color: transparent;");
        tab.setContent(sp);
        return tab;
    }

    // ── Onglet Arithmétique ─────────────────────

    private Tab tabArithmetique() {
        Tab tab = new Tab("± Arithmétique");
        VBox content = new VBox(14);
        content.setPadding(new Insets(16));
        content.setStyle("-fx-background-color: " + SURFACE + ";");

        // Sélecteurs Pᵢ et Pⱼ
        ComboBox<String> cbA = polyCombo("P₁");
        ComboBox<String> cbB = polyCombo("P₂");

        // Lier les combos à la liste du modèle
        model.getPolynomes().addListener((ListChangeListener<Polynome>) c -> {
            refreshCombos(cbA, cbB);
        });

        HBox selRow = new HBox(10, label("Pᵢ"), cbA, label("Pⱼ"), cbB);
        selRow.setAlignment(Pos.CENTER_LEFT);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        Button btnAdd  = fullButton("Pᵢ + Pⱼ",      ACCENT_LIGHT, ACCENT);
        Button btnSub  = fullButton("Pᵢ − Pⱼ",      ACCENT_LIGHT, ACCENT);
        Button btnMul  = fullButton("Pᵢ × Pⱼ",      ACCENT_LIGHT, ACCENT);
        Button btnDiv  = fullButton("Pᵢ ÷ Pⱼ",      ACCENT_LIGHT, ACCENT);
        Button btnPgcd = fullButton("PGCD(Pᵢ, Pⱼ)", ACCENT_LIGHT, ACCENT);

        btnAdd.setOnAction(e  -> controller.addition(cbA.getSelectionModel().getSelectedIndex(),
                                                     cbB.getSelectionModel().getSelectedIndex()));
        btnSub.setOnAction(e  -> controller.soustraction(cbA.getSelectionModel().getSelectedIndex(),
                                                         cbB.getSelectionModel().getSelectedIndex()));
        btnMul.setOnAction(e  -> controller.multiplication(cbA.getSelectionModel().getSelectedIndex(),
                                                           cbB.getSelectionModel().getSelectedIndex()));
        btnDiv.setOnAction(e  -> controller.division(cbA.getSelectionModel().getSelectedIndex(),
                                                     cbB.getSelectionModel().getSelectedIndex()));
        btnPgcd.setOnAction(e -> controller.pgcd(cbA.getSelectionModel().getSelectedIndex(),
                                                  cbB.getSelectionModel().getSelectedIndex()));

        grid.addRow(0, btnAdd,  btnSub);
        grid.addRow(1, btnMul,  btnDiv);
        grid.add(btnPgcd, 0, 2, 2, 1);
        GridPane.setHgrow(btnAdd,  Priority.ALWAYS);
        GridPane.setHgrow(btnSub,  Priority.ALWAYS);
        GridPane.setHgrow(btnMul,  Priority.ALWAYS);
        GridPane.setHgrow(btnDiv,  Priority.ALWAYS);
        GridPane.setHgrow(btnPgcd, Priority.ALWAYS);

        // Multiplication par scalaire
        Separator sep = new Separator();
        Label lblScal = hintLabel("Multiplication par un scalaire");
        ComboBox<String> cbScal = polyCombo("Pᵢ");
        model.getPolynomes().addListener((ListChangeListener<Polynome>) c -> refreshCombos(cbScal));
        TextField fieldScal = styledField("Scalaire  (ex : 2.5)");
        Button btnScal = accentButton("Pᵢ × k");
        btnScal.setOnAction(e -> controller.multiplicationScalaire(
                cbScal.getSelectionModel().getSelectedIndex(), fieldScal.getText()));

        content.getChildren().addAll(selRow, grid, sep, lblScal,
                new HBox(8, label("Pᵢ"), cbScal), fieldScal, btnScal);
        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color: " + SURFACE + "; -fx-border-color: transparent;");
        tab.setContent(sp);
        return tab;
    }

    // ── Onglet Analyse ──────────────────────────

    private Tab tabAnalyse() {
        Tab tab = new Tab("∫ Analyse");
        VBox content = new VBox(14);
        content.setPadding(new Insets(16));
        content.setStyle("-fx-background-color: " + SURFACE + ";");

        ComboBox<String> cbP = polyCombo("Polynôme");
        model.getPolynomes().addListener((ListChangeListener<Polynome>) c -> refreshCombos(cbP));

        HBox selRow = new HBox(10, label("Polynôme"), cbP);
        selRow.setAlignment(Pos.CENTER_LEFT);

        // Dérivée / Primitive / Image
        HBox row1 = new HBox(8);
        Button btnDer  = halfButton("Dérivée");
        Button btnPrim = halfButton("Primitive");
        btnDer.setOnAction(e  -> controller.derivee(cbP.getSelectionModel().getSelectedIndex()));
        btnPrim.setOnAction(e -> controller.primitive(cbP.getSelectionModel().getSelectedIndex()));
        HBox.setHgrow(btnDer,  Priority.ALWAYS);
        HBox.setHgrow(btnPrim, Priority.ALWAYS);
        row1.getChildren().addAll(btnDer, btnPrim);

        TextField fieldX = styledField("x  (ex : 1.5)");
        Button btnImg = accentButton("Calculer f(x)");
        btnImg.setOnAction(e -> controller.image(cbP.getSelectionModel().getSelectedIndex(),
                                                  fieldX.getText()));

        Separator sep1 = new Separator();

        // Intégrale / Moyenne
        Label lblBornes = hintLabel("Intervalle [a, b]");
        TextField fieldA = styledField("a");
        TextField fieldB = styledField("b");
        HBox bornes = new HBox(8, fieldA, label("→"), fieldB);
        bornes.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(fieldA, Priority.ALWAYS);
        HBox.setHgrow(fieldB, Priority.ALWAYS);

        Button btnIntg = halfButton("Intégrale");
        Button btnMoy  = halfButton("Moyenne");
        btnIntg.setOnAction(e -> controller.integrale(cbP.getSelectionModel().getSelectedIndex(),
                                                       fieldA.getText(), fieldB.getText()));
        btnMoy.setOnAction(e  -> controller.moyenne(cbP.getSelectionModel().getSelectedIndex(),
                                                     fieldA.getText(), fieldB.getText()));
        HBox.setHgrow(btnIntg, Priority.ALWAYS);
        HBox.setHgrow(btnMoy,  Priority.ALWAYS);
        HBox rowIntMoy = new HBox(8, btnIntg, btnMoy);

        content.getChildren().addAll(selRow, row1,
                new Separator(), hintLabel("Image en un point"), fieldX, btnImg,
                sep1, lblBornes, bornes, rowIntMoy);
        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color: " + SURFACE + "; -fx-border-color: transparent;");
        tab.setContent(sp);
        return tab;
    }

    // ── Onglet Interpolation ────────────────────

    private Tab tabInterpolation() {
        Tab tab = new Tab("⋯ Interpolation");
        VBox content = new VBox(14);
        content.setPadding(new Insets(16));
        content.setStyle("-fx-background-color: " + SURFACE + ";");

        Label hint = hintLabel("Entrez les points à raison d'un par ligne,\n"
                + "format :  x  y   (ou x,y)");
        TextArea areaPts = new TextArea();
        areaPts.setPromptText("0  1\n1  3\n2  7");
        areaPts.setPrefRowCount(6);
        areaPts.setStyle("-fx-border-color: " + BORDER + "; -fx-border-radius: 6; "
                + "-fx-background-radius: 6; -fx-font-size: 13px;");
        VBox.setVgrow(areaPts, Priority.ALWAYS);

        Button btnInterp = accentButton("Calculer le polynôme d'interpolation");
        btnInterp.setOnAction(e -> controller.interpolation(areaPts.getText()));

        content.getChildren().addAll(hint, areaPts, btnInterp);
        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color: " + SURFACE + "; -fx-border-color: transparent;");
        tab.setContent(sp);
        return tab;
    }

    // ─────────────────────────────────────────────
    //  Colonne droite : tracé + résultat
    // ─────────────────────────────────────────────

    private Node buildColonneDroite() {
        VBox col = new VBox(10);
        col.setPrefWidth(340);
        col.setMinWidth(280);

        // ── Tracé ───────────────────────────────
        Label titreCourbe = sectionTitle("Tracé de courbe");

        Label hintTrace = hintLabel("Sélectionnez un/plusieurs polynômes dans la liste (Ctrl+clic)");

        HBox bornesRow = new HBox(8);
        TextField fieldXMin = styledField("xMin");
        fieldXMin.setText("-5");
        TextField fieldXMax = styledField("xMax");
        fieldXMax.setText("5");
        fieldXMin.setPrefWidth(70);
        fieldXMax.setPrefWidth(70);
        bornesRow.setAlignment(Pos.CENTER_LEFT);
        bornesRow.getChildren().addAll(label("x ∈ ["), fieldXMin, label(","), fieldXMax, label("]"));

        HBox traceRow = new HBox(8);
        Button btnTrace    = accentButton("Tracer");
        Button btnAgrandir = smallButton("⛶", "#F0F0F0", TEXT_PRIMARY);
        btnAgrandir.setTooltip(new Tooltip("Ouvrir dans une fenêtre agrandissable"));
        btnAgrandir.setDisable(true);
        btnAgrandir.setId("btnAgrandir");
        btnTrace.setOnAction(e -> {
            List<Integer> indices = new ArrayList<>(
                    listeView.getSelectionModel().getSelectedIndices());
            controller.tracerCourbes(indices, fieldXMin.getText(), fieldXMax.getText());
        });
        btnAgrandir.setOnAction(e -> ouvrirFenetreGraphique());
        HBox.setHgrow(btnTrace, Priority.ALWAYS);
        traceRow.getChildren().addAll(btnTrace, btnAgrandir);

        canvas = new Canvas(320, 220);
        drawEmptyCanvas();

        // ── Résultat ────────────────────────────
        Label titreRes = sectionTitle("Résultat");
        labelResultat  = new Label("—");
        labelResultat.setWrapText(true);
        labelResultat.setStyle("-fx-font-size: 13px; -fx-text-fill: " + TEXT_PRIMARY
                + "; -fx-padding: 12; -fx-background-color: " + SURFACE
                + "; -fx-border-color: " + BORDER
                + "; -fx-border-radius: 8; -fx-background-radius: 8;");
        labelResultat.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(labelResultat, Priority.ALWAYS);

        Button btnAjouterRes = fullButton("➕ Ajouter à la liste", ACCENT_LIGHT, ACCENT);
        btnAjouterRes.setDisable(true);
        btnAjouterRes.setOnAction(e -> {
            if (dernierResultat != null) {
                controller.ajouterResultat(dernierResultat);
                dernierResultat = null;
                btnAjouterRes.setDisable(true);
            }
        });
        // Stockage du bouton pour y accéder depuis proposerAjoutResultat
        btnAjouterRes.setId("btnAjouterRes");

        col.getChildren().addAll(
                titreCourbe, hintTrace, bornesRow, traceRow,
                canvas,
                titreRes, labelResultat, btnAjouterRes
        );
        VBox.setVgrow(col, Priority.ALWAYS);

        // Rendre le canvas responsive
        col.widthProperty().addListener((obs, ov, nv) -> {
            canvas.setWidth(nv.doubleValue() - 4);
        });

        return col;
    }

    // ─────────────────────────────────────────────
    //  Barre de statut
    // ─────────────────────────────────────────────

    private Node buildStatusBar() {
        HBox bar = new HBox();
        bar.setPadding(new Insets(6, 16, 6, 16));
        bar.setStyle("-fx-background-color: " + SURFACE + "; -fx-border-color: " + BORDER
                + "; -fx-border-width: 1 0 0 0;");
        labelInfo = new Label("Prêt.");
        labelInfo.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 12px;");
        bar.getChildren().add(labelInfo);
        return bar;
    }

    // ─────────────────────────────────────────────
    //  API appelée par le contrôleur
    // ─────────────────────────────────────────────

    /** Met à jour la liste à gauche depuis le modèle. */
    public void rafraichirListe() {
        listeView.getItems().clear();
        List<Polynome> polys = model.getPolynomes();
        for (int i = 0; i < polys.size(); i++) {
            listeView.getItems().add("P" + (i + 1) + "  =  " + polys.get(i).toString());
        }
    }

    /** Affiche un résultat textuel dans la zone dédiée. */
    public void afficherResultat(String operation, String valeur) {
        labelResultat.setText("[ " + operation + " ]\n\n" + valeur);
    }

    /** Affiche un message dans la barre de statut. */
    public void afficherInfo(String message) {
        if (labelInfo != null) labelInfo.setText(message);
    }

    /**
     * Propose d'ajouter un polynôme résultat à la liste.
     * Active le bouton "Ajouter à la liste" et mémorise le résultat.
     */
    public void proposerAjoutResultat(Polynome res) {
        dernierResultat = res;
        // Retrouver le bouton par son id
        Button btn = (Button) canvas.getScene().lookup("#btnAjouterRes");
        if (btn != null) btn.setDisable(false);
        afficherResultat("Résultat (non ajouté)", res.toString());
        afficherInfo("Résultat calculé. Cliquez sur « Ajouter à la liste » si souhaité.");
    }

    /**
     * Trace une ou plusieurs courbes sur le canvas avec graduations et mise à l'échelle optimale.
     * <p>
     * Mise à l'échelle : on calcule le yMin/yMax réel des données, puis on arrondit
     * les bornes au "pas propre" le plus proche (algorithme de graduation de Wilkinson simplifié)
     * afin que les graduations tombent sur des valeurs rondes lisibles.
     * </p>
     * @param series liste de séries de points [x, y]
     * @param labels noms des courbes
     * @param xMin   borne gauche saisie par l'utilisateur
     * @param xMax   borne droite saisie par l'utilisateur
     */
    public void tracerCourbes(List<List<double[]>> series, List<String> labels,
                               double xMin, double xMax) {
        // Mémoriser pour la fenêtre agrandie
        dernierSeries  = series;
        derniersLabels = labels;
        derniereXMin   = xMin;
        derniereXMax   = xMax;
        // Activer le bouton agrandir
        Button btnAg = (canvas.getScene() != null)
                ? (Button) canvas.getScene().lookup("#btnAgrandir") : null;
        if (btnAg != null) btnAg.setDisable(false);
        // Dessiner sur le canvas principal
        dessinerSurCanvas(canvas, series, labels, xMin, xMax);
    }

    /**
     * Ouvre une fenêtre dédiée au graphique, redimensionnable.
     * Le canvas se redimensionne avec la fenêtre.
     */
    private void ouvrirFenetreGraphique() {
        if (dernierSeries == null) return;

        Stage popup = new Stage();
        popup.setTitle("Tracé de courbe");

        // Canvas dans un Pane pour suivre la taille
        Pane pane = new Pane();
        pane.setStyle("-fx-background-color: " + SURFACE + ";");
        Canvas c = new Canvas(800, 560);
        pane.getChildren().add(c);

        // bind() gère le redimensionnement ; les listeners sur le canvas redessinent ensuite
        c.widthProperty().bind(pane.widthProperty());
        c.heightProperty().bind(pane.heightProperty());

        c.widthProperty().addListener((obs, ov, nv) ->
                dessinerSurCanvas(c, dernierSeries, derniersLabels, derniereXMin, derniereXMax));
        c.heightProperty().addListener((obs, ov, nv) ->
                dessinerSurCanvas(c, dernierSeries, derniersLabels, derniereXMin, derniereXMax));

        dessinerSurCanvas(c, dernierSeries, derniersLabels, derniereXMin, derniereXMax);

        Scene scene = new Scene(pane, 800, 560);
        popup.setScene(scene);
        popup.setMinWidth(400);
        popup.setMinHeight(300);
        popup.show();
    }

    /** Délégation interne : dessine sur n'importe quel Canvas. */
    private void dessinerSurCanvas(Canvas cible, List<List<double[]>> series,
                                    List<String> labels, double xMin, double xMax) {
        GraphicsContext gc = cible.getGraphicsContext2D();
        double W = cible.getWidth();
        double H = cible.getHeight();

        // ── Marges : gauche large pour les labels Y, bas pour les labels X ──
        double padL = 52;   // gauche
        double padR = 14;   // droite
        double padT = 28;   // haut (légende)
        double padB = 28;   // bas  (labels X)
        double pw = W - padL - padR;
        double ph = H - padT - padB;

        // ── 1. Trouver le vrai yMin/yMax des données ─────────────────────────
        double rawYMin = Double.MAX_VALUE, rawYMax = -Double.MAX_VALUE;
        for (List<double[]> serie : series) {
            for (double[] pt : serie) {
                if (Double.isFinite(pt[1])) {
                    rawYMin = Math.min(rawYMin, pt[1]);
                    rawYMax = Math.max(rawYMax, pt[1]);
                }
            }
        }
        if (!Double.isFinite(rawYMin)) { rawYMin = -1; rawYMax = 1; }
        if (rawYMin == rawYMax)        { rawYMin -= 1; rawYMax += 1; }

        // ── 2. Calcul du pas "propre" pour X et Y ────────────────────────────
        //   Cible : entre 4 et 6 graduations par axe.
        double xStep = calculerPasPropre(xMax - xMin, 5);
        double yStep = calculerPasPropre(rawYMax - rawYMin, 5);

        // ── 3. Bornes arrondies aux multiples du pas ──────────────────────────
        double xStart = Math.floor(xMin / xStep) * xStep;
        double xEnd   = Math.ceil(xMax  / xStep) * xStep;
        double yStart = Math.floor(rawYMin / yStep) * yStep;
        double yEnd   = Math.ceil(rawYMax  / yStep) * yStep;

        // Petite marge visuelle (~5 %) au-dessus et en dessous
        double yRange = yEnd - yStart;
        double yPad   = yRange * 0.05;
        double yMin   = yStart - yPad;
        double yMax   = yEnd   + yPad;

        // ── Helpers de projection ─────────────────────────────────────────────
        // sx(x) = coordonnée écran en X,  sy(y) = coordonnée écran en Y
        // (lambdas simulées par des doubles inline plus bas)

        // ── 4. Fond ───────────────────────────────────────────────────────────
        gc.setFill(Color.web(SURFACE));
        gc.fillRect(0, 0, W, H);

        // ── 5. Grille et graduations X ────────────────────────────────────────
        gc.setFont(Font.font("System", 10));
        double x = xStart;
        while (x <= xEnd + xStep * 0.01) {
            double sx = padL + (x - xMin) / (xMax - xMin) * pw;
            if (sx >= padL - 1 && sx <= padL + pw + 1) {
                // Ligne de grille
                gc.setStroke(Color.web("#EBEBEB"));
                gc.setLineWidth(1);
                gc.strokeLine(sx, padT, sx, padT + ph);
                // Tiret sur l'axe bas
                gc.setStroke(Color.web("#AAAAAA"));
                gc.setLineWidth(1);
                gc.strokeLine(sx, padT + ph, sx, padT + ph + 4);
                // Label
                gc.setFill(Color.web(TEXT_MUTED));
                String lx = formatGraduation(x);
                double tw = lx.length() * 5.5;
                gc.fillText(lx, sx - tw / 2, padT + ph + 16);
            }
            x += xStep;
            // Protection anti-boucle infinie sur les très petits pas
            if (xStep < 1e-12) break;
        }

        // ── 6. Grille et graduations Y ────────────────────────────────────────
        double y = yStart;
        while (y <= yEnd + yStep * 0.01) {
            double sy = padT + ph - (y - yMin) / (yMax - yMin) * ph;
            if (sy >= padT - 1 && sy <= padT + ph + 1) {
                // Ligne de grille
                gc.setStroke(Color.web("#EBEBEB"));
                gc.setLineWidth(1);
                gc.strokeLine(padL, sy, padL + pw, sy);
                // Tiret sur l'axe gauche
                gc.setStroke(Color.web("#AAAAAA"));
                gc.strokeLine(padL - 4, sy, padL, sy);
                // Label (aligné à droite)
                gc.setFill(Color.web(TEXT_MUTED));
                String ly = formatGraduation(y);
                double tw = ly.length() * 5.5;
                gc.fillText(ly, padL - 6 - tw, sy + 4);
            }
            y += yStep;
            if (yStep < 1e-12) break;
        }

        // ── 7. Axes principaux (y=0 et x=0) ──────────────────────────────────
        gc.setLineWidth(1.5);
        // Axe horizontal y = 0
        double y0s = padT + ph - (0 - yMin) / (yMax - yMin) * ph;
        if (y0s >= padT && y0s <= padT + ph) {
            gc.setStroke(Color.web("#BBBBBB"));
            gc.strokeLine(padL, y0s, padL + pw, y0s);
        }
        // Axe vertical x = 0
        double x0s = padL + (0 - xMin) / (xMax - xMin) * pw;
        if (x0s >= padL && x0s <= padL + pw) {
            gc.setStroke(Color.web("#BBBBBB"));
            gc.strokeLine(x0s, padT, x0s, padT + ph);
        }

        // ── 8. Courbes ────────────────────────────────────────────────────────
        // Seuil de coupure : on coupe le path si le saut vertical en pixels
        // dépasse 3× la hauteur du canvas (valeur aberrante / discontinuité).
        double sautMax = ph * 3;
        for (int s = 0; s < series.size(); s++) {
            List<double[]> pts = series.get(s);
            Color col = CURVE_COLORS[s % CURVE_COLORS.length];
            gc.setStroke(col);
            gc.setLineWidth(2);
            gc.beginPath();
            boolean first = true;
            double prevSy = Double.NaN;
            for (double[] pt : pts) {
                double sx = padL + (pt[0] - xMin) / (xMax - xMin) * pw;
                double sy = padT + ph - (pt[1] - yMin) / (yMax - yMin) * ph;
                if (!Double.isFinite(sy)) { first = true; prevSy = Double.NaN; continue; }
                // Coupure sur discontinuité franche
                if (!first && Math.abs(sy - prevSy) > sautMax) { first = true; }
                if (first) { gc.moveTo(sx, sy); first = false; }
                else        gc.lineTo(sx, sy);
                prevSy = sy;
            }
            gc.stroke();

            // ── Légende ───────────────────────────────────────────────────────
            double lx = padL + 8 + s * 60.0;
            double ly = padT - 10;
            gc.setFill(col);
            gc.fillRoundRect(lx, ly, 10, 10, 4, 4);
            gc.setFill(Color.web(TEXT_PRIMARY));
            gc.fillText(labels.get(s), lx + 13, ly + 9);
        }

        // ── 9. Cadre ──────────────────────────────────────────────────────────
        gc.setStroke(Color.web(BORDER));
        gc.setLineWidth(1);
        gc.strokeRect(padL, padT, pw, ph);
    }

    /**
     * Calcule un "pas propre" pour les graduations.
     * <p>
     * Algorithme : on cherche le plus petit pas de la forme 1, 2 ou 5 × 10^n
     * tel que l'intervalle contienne environ {@code cibleGraduations} graduations.
     * </p>
     * @param plage             amplitude de l'intervalle (> 0)
     * @param cibleGraduations  nombre de graduations visé (typiquement 4 à 6)
     * @return le pas arrondi
     */
    private static double calculerPasPropre(double plage, int cibleGraduations) {
        if (plage <= 0 || !Double.isFinite(plage)) return 1.0;
        double pasRaw  = plage / cibleGraduations;
        double exposant = Math.floor(Math.log10(pasRaw));
        double base     = Math.pow(10, exposant);
        double mantisse = pasRaw / base;
        // Arrondir la mantisse à 1, 2 ou 5
        double pas;
        if      (mantisse <= 1.5) pas = 1 * base;
        else if (mantisse <= 3.5) pas = 2 * base;
        else if (mantisse <= 7.5) pas = 5 * base;
        else                       pas = 10 * base;
        return pas;
    }

    /**
     * Formate une valeur de graduation : entier si possible, sinon décimal court.
     * Évite l'affichage de "-0" et adapte la précision au pas.
     */
    private static String formatGraduation(double v) {
        // Éliminer le -0 flottant
        if (Math.abs(v) < 1e-10) return "0";
        // Si la valeur est entière (à 1e-9 près), afficher sans décimale
        if (Math.abs(v - Math.round(v)) < 1e-9 && Math.abs(v) < 1e7) {
            return String.valueOf((long) Math.round(v));
        }
        // Sinon : 2 chiffres significatifs max
        double abs = Math.abs(v);
        if      (abs >= 100)  return String.format("%.0f", v);
        else if (abs >= 10)   return String.format("%.1f", v);
        else if (abs >= 1)    return String.format("%.2f", v);
        else if (abs >= 0.1)  return String.format("%.3f", v);
        else                   return String.format("%.2e", v);
    }

    // ─────────────────────────────────────────────
    //  Helpers graphiques
    // ─────────────────────────────────────────────

    private void drawEmptyCanvas() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.web(SURFACE));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.web(TEXT_MUTED));
        gc.setFont(Font.font("System", 12));
        gc.fillText("Sélectionnez un polynôme et cliquez sur « Tracer »",
                12, canvas.getHeight() / 2);
        gc.setStroke(Color.web(BORDER));
        gc.strokeRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void refreshCombos(ComboBox<String>... combos) {
        List<String> items = new ArrayList<>();
        List<Polynome> polys = model.getPolynomes();
        for (int i = 0; i < polys.size(); i++) {
            items.add("P" + (i + 1) + " = " + polys.get(i).toString());
        }
        for (ComboBox<String> cb : combos) {
            int sel = cb.getSelectionModel().getSelectedIndex();
            cb.getItems().setAll(items);
            if (sel >= 0 && sel < items.size()) cb.getSelectionModel().select(sel);
            else if (!items.isEmpty())           cb.getSelectionModel().selectFirst();
        }
    }

    private boolean confirmer(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message,
                ButtonType.OK, ButtonType.CANCEL);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        return alert.showAndWait().filter(bt -> bt == ButtonType.OK).isPresent();
    }

    // ── Widgets utilitaires ──────────────────────

    private Label sectionTitle(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Georgia", FontWeight.BOLD, 13));
        l.setStyle("-fx-text-fill: " + TEXT_PRIMARY + ";");
        return l;
    }

    private Label label(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: " + TEXT_PRIMARY + "; -fx-font-size: 13px;");
        return l;
    }

    private Label hintLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 11px;");
        l.setWrapText(true);
        return l;
    }

    private TextField styledField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setMaxWidth(Double.MAX_VALUE);
        tf.setStyle("-fx-background-color: " + SURFACE + "; -fx-border-color: " + BORDER
                + "; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-size: 13px;"
                + "-fx-padding: 7 10 7 10;");
        return tf;
    }

    private ComboBox<String> polyCombo(String prompt) {
        ComboBox<String> cb = new ComboBox<>();
        cb.setPromptText(prompt);
        cb.setMaxWidth(Double.MAX_VALUE);
        cb.setStyle("-fx-font-size: 12px;");
        HBox.setHgrow(cb, Priority.ALWAYS);
        return cb;
    }

    private Button accentButton(String text) {
        Button b = new Button(text);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setStyle("-fx-background-color: " + ACCENT + "; -fx-text-fill: white; "
                + "-fx-font-size: 13px; -fx-font-weight: bold; "
                + "-fx-background-radius: 7; -fx-padding: 9 14 9 14; -fx-cursor: hand;");
        b.setOnMouseEntered(e -> b.setStyle(b.getStyle()
                .replace(ACCENT, "#1A4BEE")));
        b.setOnMouseExited(e  -> b.setStyle(b.getStyle()
                .replace("#1A4BEE", ACCENT)));
        return b;
    }

    private Button smallButton(String text, String bg, String fg) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color: " + bg + "; -fx-text-fill: " + fg + "; "
                + "-fx-font-size: 12px; -fx-background-radius: 6; -fx-padding: 6 12 6 12;"
                + " -fx-cursor: hand;");
        return b;
    }

    private Button fullButton(String text, String bg, String fg) {
        Button b = new Button(text);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setStyle("-fx-background-color: " + bg + "; -fx-text-fill: " + fg + "; "
                + "-fx-font-size: 12px; -fx-background-radius: 7; -fx-padding: 8 12 8 12;"
                + " -fx-cursor: hand;");
        return b;
    }

    private Button halfButton(String text) {
        return fullButton(text, ACCENT_LIGHT, ACCENT);
    }

    // ─────────────────────────────────────────────
    //  main — point d'entrée de l'application
    // ─────────────────────────────────────────────

    public static void main(String[] args) {
        launch(args);
    }
}