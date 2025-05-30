package cartaNautica.controller;

import cartaNautica.PoiUPVApp;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Answer;

/**
 * FXML Controller class para la vista de resolver problemas
 *
 * @author alvar
 */
public class SolveProblemController implements Initializable {

    @FXML
    private Slider zoomSlider;
    @FXML
    private ToggleButton pointToolButton;
    @FXML
    private ToggleButton lineToolButton;
    @FXML
    private ToggleButton arcToolButton;
    @FXML
    private ToggleButton posToolButton;
    @FXML
    private ToggleButton textToolButton;
    @FXML
    private ToggleButton protractorToolButton;
    @FXML
    private ToggleButton rulerToolButton;
    @FXML
    private Label rotationAngleLabel;
    @FXML
    private ToggleButton eraserToolButton;
    @FXML
    private Button clearButton;
    @FXML
    private ColorPicker colorPicker;
    @FXML
    private ScrollPane map_scrollpane;
    @FXML
    private VBox answersContainer;
    @FXML
    private Button backButton;
    @FXML
    private Button solveButton;
    @FXML
    private Label enunciadoLabel;
    @FXML
    private RadioButton respuesta1;
    @FXML
    private ToggleGroup respuestas;
    @FXML
    private RadioButton respuesta2;
    @FXML
    private RadioButton respuesta3;
    @FXML
    private RadioButton respuesta4;
    @FXML
    private ToggleGroup toolToggleGroup;
    @FXML
    private ToggleGroup answersToggleGroup;
    private List<Integer> pos;
    
    // Variables para el zoom y la gestión de la carta náutica
    private Group zoomGroup;
    private Group contentGroup;
    private Group styleGroup;
    @FXML
    private ToggleGroup drawToggleGroup;
    @FXML
    private Button transportador;
    @FXML
    private Button regla;
    @FXML
    private Slider angleSlider;
    @FXML
    private Label RotateLabel;
    @FXML
    private Separator separador;
    @FXML
    private Label styleLabel;
    @FXML
    private Label sizeLabel;
    @FXML
    private Slider sizeSelector;
    @FXML
    private ComboBox<Integer> fontSizeSelector;
    private ObservableList<Integer> tamanosFuente; 
    
    private Cursor eraserCursor;
    @FXML
    private Label anchoLabel;
    
    private Circle punto;
    private Line linea;
    private Arc arco;
    private Line lineaHor;
    private Line lineaVer;
    
    
    private HashMap<TextField, String> textoAColor = new HashMap<>();
    private HashMap<TextField, Integer> textoATamano = new HashMap<>();
    @FXML
    private Separator separador1;
    @FXML
    private Button infoButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Cursor al pasar sobre el backButton
        backButton.setOnMouseEntered(event -> {
            backButton.setStyle("-fx-background-color: #0096C9");
        });
        backButton.setOnMouseExited(event -> {
            backButton.setStyle("-fx-background-color: transparent");
        }); 
        
        // Enunciado y soluciones
        enunciadoLabel.setText(ProblemController.getProblem().getText());
        pos = new ArrayList<>(Arrays.asList(0, 1, 2, 3)); Collections.shuffle(pos);
        respuesta1.setText(ProblemController.getProblem().getAnswers().get(pos.get(0)).getText());
        respuesta2.setText(ProblemController.getProblem().getAnswers().get(pos.get(1)).getText());
        respuesta3.setText(ProblemController.getProblem().getAnswers().get(pos.get(2)).getText());
        respuesta4.setText(ProblemController.getProblem().getAnswers().get(pos.get(3)).getText());
        
        // Deshabilitar el botón de resolver si no hay ninguna opción seleccionada
        solveButton.disableProperty().bind(respuestas.selectedToggleProperty().isNull());
        
        // Inicializar el control de zoom
        configurarZoom();
        
        // Configurara el Transportador y la Regla
        configurarTransportadoryRegla();
        
        // Configurar sección de estilos
        colorPicker.setValue(Color.RED);
        fontSizeSelector.setValue(12);
        BooleanBinding toolSelected = Bindings.or(pointToolButton.selectedProperty(),Bindings.or(lineToolButton.selectedProperty(),Bindings.or(arcToolButton.selectedProperty(), Bindings.or(posToolButton.selectedProperty(), textToolButton.selectedProperty()))));
        colorPicker.visibleProperty().bind(toolSelected);
        fontSizeSelector.visibleProperty().bind(Bindings.and(toolSelected,textToolButton.selectedProperty()));
        sizeSelector.visibleProperty().bind(Bindings.and(toolSelected,Bindings.not(textToolButton.selectedProperty())));
        anchoLabel.visibleProperty().bind(Bindings.and(toolSelected,Bindings.not(textToolButton.selectedProperty())));
        sizeLabel.visibleProperty().bind(toolSelected);
        styleLabel.visibleProperty().bind(toolSelected);
        separador.visibleProperty().bind(toolSelected);
        textToolButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) sizeLabel.setText("Tamaño: ");
            else sizeLabel.setText("Grosor: ");
        });
        tamanosFuente = fontSizeSelector.getItems();
        tamanosFuente.addAll(6,7,8,9,10,12,14,16,18,20,24,28,32,36,48,72);
        anchoLabel.textProperty().bind(Bindings.format("%.1f px", sizeSelector.valueProperty()));
        
        
        // Cursores
        map_scrollpane.setCursor(Cursor.MOVE);
        pointToolButton.setOnAction(e -> {
            if (pointToolButton.isSelected()) map_scrollpane.setCursor(Cursor.CROSSHAIR); 
            else map_scrollpane.setCursor(Cursor.MOVE);
        });
        lineToolButton.setOnAction(e -> {
            if (lineToolButton.isSelected()) map_scrollpane.setCursor(Cursor.CROSSHAIR); 
            else map_scrollpane.setCursor(Cursor.MOVE);
        });
        arcToolButton.setOnAction(e -> {
            if (arcToolButton.isSelected()) map_scrollpane.setCursor(Cursor.CROSSHAIR); 
            else map_scrollpane.setCursor(Cursor.MOVE);
        });
        posToolButton.setOnAction(e -> {
            if (posToolButton.isSelected()) map_scrollpane.setCursor(Cursor.CROSSHAIR); 
            else map_scrollpane.setCursor(Cursor.MOVE);
        });
        textToolButton.setOnAction(e -> {
            if (textToolButton.isSelected()) map_scrollpane.setCursor(Cursor.TEXT); 
            else map_scrollpane.setCursor(Cursor.MOVE);
        });
        
        eraserCursor = new ImageCursor(new Image(getClass().getResourceAsStream("/resources/iconoGoma2.png")), 50, 400);
        
        eraserToolButton.setOnAction(e -> {
            if (eraserToolButton.isSelected()) map_scrollpane.setCursor(eraserCursor); 
            else map_scrollpane.setCursor(Cursor.MOVE);
        });
    }

    private void configurarZoom() {
        // Inicializar el slider de zoom
        zoomSlider.setMin(0.5);
        zoomSlider.setMax(1.5);
        zoomSlider.setValue(1.0);
        zoomSlider.valueProperty().addListener((o, oldVal, newVal) -> zoom((Double) newVal));
        
        // Configurar la estructura para el zoom
        contentGroup = new Group();
        zoomGroup = new Group();
        contentGroup.getChildren().add(zoomGroup);
        //zoomGroup.getChildren().add(map_scrollpane.getContent());
        //map_scrollpane.setContent(contentGroup);
        contentGroup.setOnMousePressed(this::onMousePressed);
        contentGroup.setOnMouseDragged(this::onMouseDragged);
        contentGroup.setOnMouseReleased(this::onMouseReleased);
        
        // Cargar y configurar la imagen de la carta náutica
        ImageView chartImageView = new ImageView(new Image(getClass().getResourceAsStream("/resources/carta_nautica.jpg")));
        zoomGroup.getChildren().add(chartImageView);
        
        // Asignar el contenido al ScrollPane
        map_scrollpane.setContent(contentGroup);
        
        // Configuración adicional del ScrollPane
        map_scrollpane.setPannable(true);
    }
    
    private void zoom(double scaleValue) {
        // Guardar valores de scroll antes del escalado
        double scrollH = map_scrollpane.getHvalue();
        double scrollV = map_scrollpane.getVvalue();
        
        // Escalar el zoomGroup
        zoomGroup.setScaleX(scaleValue);
        zoomGroup.setScaleY(scaleValue);
        
        // Restaurar valores de scroll después del escalado
        map_scrollpane.setHvalue(scrollH);
        map_scrollpane.setVvalue(scrollV);
    }
    
    private void configurarTransportadoryRegla() {
        // Configurar Transportador
        final double[] offSetX = new double[1];
        final double[] offSetY = new double[1];
        transportador.setOnMousePressed( event -> {
           offSetX[0] = event.getSceneX() - transportador.getTranslateX();
           offSetY[0] = event.getSceneY() - transportador.getTranslateY();
        });
        transportador.setOnMouseDragged(event -> {
            // Acotamos la posición del transportador dentro del mapa -> posFinal = max(posMin, min(pos, posMax))
            double newX = Math.max(-map_scrollpane.getWidth()/2+transportador.getWidth()/2, Math.min(event.getSceneX() - offSetX[0], map_scrollpane.getWidth()/2-transportador.getWidth()/2));
            double newY = Math.max(-map_scrollpane.getHeight()/2+transportador.getHeight()/2, Math.min(event.getSceneY() - offSetY[0], map_scrollpane.getHeight()/2-transportador.getHeight()/2));

            transportador.setTranslateX(newX);
            transportador.setTranslateY(newY);
        });
        transportador.visibleProperty().bind(protractorToolButton.selectedProperty());
        
        // Configurar Regla
        regla.setOnMousePressed( event -> {
           offSetX[0] = event.getSceneX() - regla.getTranslateX();
           offSetY[0] = event.getSceneY() - regla.getTranslateY();
        });
        regla.setOnMouseDragged(event -> {
            // Acotamos la posición de la regla dentro del mapa -> posFinal = max(posMin, min(pos, posMax))
            double alfa = angleSlider.getValue()*Math.PI/180; // Calculamos el ángulo de la regla en radianes
            double AnchuraReglaFinal = (regla.getWidth())*Math.abs(Math.cos(alfa)); // cos(α) = cos(-α) por lo que no hace falta valor absoluto en α
            double AlturaReglaFinal = (regla.getWidth())*Math.abs(Math.sin(alfa)) + regla.getHeight(); // sin(α) = -sin(-α) por lo que hace falta valor absoluto en α

            double newX = Math.max(-map_scrollpane.getWidth()/2+AnchuraReglaFinal/2, Math.min(event.getSceneX() - offSetX[0], map_scrollpane.getWidth()/2-AnchuraReglaFinal/2));
            double newY = Math.max(-map_scrollpane.getHeight()/2+AlturaReglaFinal/2, Math.min(event.getSceneY() - offSetY[0], map_scrollpane.getHeight()/2-AlturaReglaFinal/2));

            regla.setTranslateX(newX);
            regla.setTranslateY(newY);
        });
        
        angleSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                double alfa = newValue.doubleValue() * Math.PI / 180; // Calculamos el ángulo de la regla en radianes
                double anchuraReglaFinal = regla.getWidth() * Math.abs(Math.cos(alfa));
                double alturaReglaFinal = regla.getWidth() * Math.abs(Math.sin(alfa)) + regla.getHeight();
    
                // Obtener la posición actual de la regla
                double reglaPosX = regla.getTranslateX();
                double reglaPosY = regla.getTranslateY();
                boolean seSaldria = 
                    reglaPosX - anchuraReglaFinal/2 < -map_scrollpane.getWidth()/2 ||
                    reglaPosX + anchuraReglaFinal/2 > map_scrollpane.getWidth()/2 ||
                    reglaPosY - alturaReglaFinal/2 < -map_scrollpane.getHeight()/2 ||
                    reglaPosY + alturaReglaFinal/2 > map_scrollpane.getHeight()/2;

                if (seSaldria) {
                    angleSlider.setValue(oldValue.doubleValue());
                }
        });
        regla.visibleProperty().bind(rulerToolButton.selectedProperty());
        regla.rotateProperty().bind(Bindings.multiply(angleSlider.valueProperty(),-1));
        // Desactivar las opciones de rotación si al regla no está seleccioanda
        angleSlider.disableProperty().bind(Bindings.not(rulerToolButton.selectedProperty()));
        rulerToolButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {RotateLabel.setStyle(""); rotationAngleLabel.setStyle("");}
            else {RotateLabel.setStyle("-fx-text-fill: #AAAAAA;"); rotationAngleLabel.setStyle("-fx-text-fill: #AAAAAA;");}
        });
        rotationAngleLabel.textProperty().bind(Bindings.format("%.1f º", angleSlider.valueProperty())
);
    
    }

    @FXML
    private void zoomOut(ActionEvent event) {
        double sliderVal = zoomSlider.getValue();
        zoomSlider.setValue(sliderVal - 0.1);
    }

    @FXML
    private void zoomIn(ActionEvent event) {
        double sliderVal = zoomSlider.getValue();
        zoomSlider.setValue(sliderVal + 0.1);
    }

    @FXML
    private void onMousePressed(MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY) return ;
        Point2D pointInZoomGroup = zoomGroup.sceneToLocal(event.getSceneX(), event.getSceneY());
        if (pointToolButton.isSelected()){
            punto = new Circle(sizeSelector.getValue());
            punto.setFill(colorPicker.getValue());
            
            zoomGroup.getChildren().add(punto);
            punto.setCenterX(pointInZoomGroup.getX());
            punto.setCenterY(pointInZoomGroup.getY());
            
            configurarMenuContextual(punto);
            event.consume();
        } else if(lineToolButton.isSelected()){
            linea = new Line(pointInZoomGroup.getX(), pointInZoomGroup.getY(), pointInZoomGroup.getX(), pointInZoomGroup.getY());
            linea.setStroke(colorPicker.getValue());
            linea.setStrokeWidth(sizeSelector.getValue());
            linea.getStrokeDashArray().addAll(5.0, 5.0+sizeSelector.getValue());
            zoomGroup.getChildren().add(linea);
            
            configurarMenuContextual(linea);
            event.consume();
        } else if (arcToolButton.isSelected()){
            arco = new Arc();
            arco.setStartAngle(0); arco.setLength(180); 
            arco.setRadiusX(1); arco.setRadiusY(1);
            
            arco.setType(ArcType.OPEN);
            arco.setFill(Color.TRANSPARENT);
            arco.setStroke(colorPicker.getValue());
            arco.setStrokeWidth(sizeSelector.getValue());
            arco.getStrokeDashArray().addAll(5.0, 5.0+sizeSelector.getValue());

            arco.setCenterX(pointInZoomGroup.getX());
            arco.setCenterY(pointInZoomGroup.getY());
            
            zoomGroup.getChildren().add(arco);

            configurarMenuContextual(arco);
            event.consume();
        } else if (posToolButton.isSelected()) {
            Bounds bounds = zoomGroup.getChildren().get(0).getBoundsInParent();
            
            lineaHor = new Line(0, pointInZoomGroup.getY(), bounds.getWidth(), pointInZoomGroup.getY());
            lineaHor.setStroke(colorPicker.getValue());
            lineaHor.setStrokeWidth(sizeSelector.getValue());
            lineaHor.getStrokeDashArray().addAll(5.0, 5.0+sizeSelector.getValue());

            lineaVer = new Line(pointInZoomGroup.getX(), 0, pointInZoomGroup.getX(), bounds.getHeight());
            lineaVer.setStroke(colorPicker.getValue());
            lineaVer.setStrokeWidth(sizeSelector.getValue());
            lineaVer.getStrokeDashArray().addAll(5.0, 5.0+sizeSelector.getValue());
            
            zoomGroup.getChildren().add(lineaHor);
            zoomGroup.getChildren().add(lineaVer);

            configurarMenuContextual(lineaHor);
            configurarMenuContextual(lineaVer);
            event.consume();
        } else if (textToolButton.isSelected()) {
            TextField texto = new TextField();
            zoomGroup.getChildren().add(texto);
            textoAColor.put(texto, colorPicker.getValue().toString().substring(2,8));
            textoATamano.put(texto, fontSizeSelector.getValue());
            texto.setStyle("-fx-background-color: transparent; -fx-border-color: black; -fx-text-fill: #"+textoAColor.get(texto)+"; -fx-font-size: "+textoATamano.get(texto));
            double initialWidth = Math.max(150, textoATamano.get(texto) * 9);
            texto.setPrefWidth(initialWidth);
            texto.setLayoutX(pointInZoomGroup.getX() - initialWidth/2);
            texto.setLayoutY(pointInZoomGroup.getY()- textoATamano.get(texto));
            texto.requestFocus();
            texto.setAlignment(Pos.CENTER);
            
            texto.textProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal == null || newVal.isEmpty()) {
                    texto.setPrefWidth(Math.max(150, textoATamano.get(texto) * 9));
                } else {
                    double charWidth = textoATamano.get(texto) * 0.5; // Aproximación conservadora
                    double calculatedWidth = newVal.length() * charWidth + 30; // 40px de padding
                    double finalWidth = Math.max(Math.max(150, textoATamano.get(texto) * 9), calculatedWidth);

                    texto.setPrefWidth(finalWidth);
                }
            });

            texto.focusedProperty().addListener((obsV, oldV, newV)-> {
                if (newV && !eraserToolButton.isSelected()) texto.setStyle("-fx-background-color: transparent; -fx-border-color: black; -fx-text-fill: #"+textoAColor.get(texto)+"; -fx-font-size: "+textoATamano.get(texto));
                else {
                    texto.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-text-fill: #"+textoAColor.get(texto)+"; -fx-font-size: "+textoATamano.get(texto));
                    if (texto.getText().equals("")) zoomGroup.getChildren().remove(texto);
                }
            });

            texto.setOnAction(e -> {
               texto.getScene().getRoot().requestFocus();
            });
            
            texto.setOnMouseEntered(e -> {
                if (eraserToolButton.isSelected()){ texto.setCursor(eraserCursor); texto.setEditable(false);}
                else {texto.setCursor(Cursor.TEXT); texto.setEditable(true);}
                e.consume();
            });
            
            configurarMenuContextual(texto);
            event.consume();
        }
        
    }
    
    @FXML
    private void onMouseDragged(MouseEvent event) {
        Point2D pointInZoomGroup = zoomGroup.sceneToLocal(event.getSceneX(), event.getSceneY());
        Bounds bounds = zoomGroup.getChildren().get(0).getBoundsInParent();
        if (pointToolButton.isSelected()) {
            map_scrollpane.setCursor(Cursor.CROSSHAIR);
            event.consume();
        } else if(lineToolButton.isSelected()){
            Double X = Math.max(0, Math.min(pointInZoomGroup.getX(), bounds.getWidth()));
            Double Y = Math.max(0, Math.min(pointInZoomGroup.getY(), bounds.getHeight()));
            
            linea.setEndX(X);
            linea.setEndY(Y);
            event.consume();
        } else if(arcToolButton.isSelected()){
            // Cálculos trigonométrios de ajuste del arco dentro de la carta naútica
            Double w = bounds.getWidth();
            Double h = bounds.getHeight();
            Double xr = Math.max(0, Math.min(pointInZoomGroup.getX(), w));
            Double yr = Math.max(0, Math.min(pointInZoomGroup.getY(), h));
            Double xc = arco.getCenterX();
            Double yc = arco.getCenterY();
 
            Double alpha = - Math.atan2(yr - yc, xr - xc);
 
            Double cosmaxx = Math.abs( (Double) Math.cos(Math.PI/2 -  alpha) );
            Double cosminx = cosmaxx;
 
            if (alpha - Math.PI/2 <= 0 && alpha + Math.PI/2 >= 0) {cosmaxx = 1.0;}
            else {cosminx = 1.0;}
            Double sinmaxy = Math.abs( (Double) Math.sin(Math.PI/2 -  alpha) );
            Double sinminy = sinmaxy;
 
            if (alpha - Math.PI/2 <= Math.PI/2 && alpha + Math.PI/2 >= Math.PI/2) {sinmaxy = 1.0;}
            else {sinminy = 1.0;}
            Double r = Math.sqrt(Math.pow(xr - xc, 2) + Math.pow(yr - yc, 2));
 
            double maxR = Math.min(
                r ,
                Math.min( Math.min((w-xc)/cosmaxx, xc/cosminx), Math.min((h-yc)/sinminy, yc/sinmaxy))
            );
 
            arco.setRadiusX(maxR); arco.setRadiusY(maxR);
 
            double angle = Math.toDegrees(Math.atan2(-(yr-yc), (xr-xc)))-90;
            while (angle < 0) angle += 360;
            angle %= 360;
            arco.setStartAngle(angle); // Restamos 90 para calcular el ángulo del extremo derecho del arco
            event.consume();
        } else if (posToolButton.isSelected()) {
            map_scrollpane.setCursor(Cursor.CROSSHAIR);
            event.consume();
        }
    }
    
    @FXML
    private void onMouseReleased(MouseEvent event) {
        if(lineToolButton.isSelected()){
            linea.getStrokeDashArray().clear(); 
        } else if(arcToolButton.isSelected()){
            arco.getStrokeDashArray().clear(); 
        }
        event.consume();
    }


    @FXML
    private void limpiarTodo(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Limpiar carta");
        alert.setHeaderText("¿Está seguro de que desea limpiar la carta?");
        alert.setContentText("Esta acción eliminará todos los elementos dibujados\n y no se puede deshacer.");
            
        Optional<ButtonType> result = alert.showAndWait();
        if ((result.isPresent() && result.get() == ButtonType.OK)){
            System.out.println(zoomGroup.getChildren().size());
            int size = zoomGroup.getChildren().size();
            for (int i = 1; i<size; i++) zoomGroup.getChildren().remove(1);
            System.out.println(zoomGroup.getChildren());
        }
        
    }
    
    private void configurarMenuContextual(Node node) {
        node.setOnContextMenuRequested(e -> {
            ContextMenu menuContext = new ContextMenu();
            menuContext.setStyle("-fx-font-size: 12;");
            // Sección de Color
            Menu cambiarColorItem = new Menu("Color");
            MenuItem colorItem = new MenuItem();       
            ColorPicker MenuColorPicker = new ColorPicker();
            Color currentColor = null;
            if (node instanceof Circle circle) currentColor = (Color) circle.getFill();
            else if (node instanceof Line line) currentColor = (Color) line.getStroke();
            else if (node instanceof Arc arc) currentColor = (Color) arc.getStroke();
            else if (node instanceof TextField textField) currentColor = Color.valueOf(textoAColor.get(textField));
            MenuColorPicker.setValue(currentColor);
            colorItem.setGraphic(MenuColorPicker);
            cambiarColorItem.getItems().add(colorItem);
            menuContext.getItems().add(cambiarColorItem);
            MenuColorPicker.valueProperty().addListener((obsV, oldV, newV) -> {
                if (node instanceof Circle circle) circle.setFill(newV);
                else if (node instanceof Line line) line.setStroke(newV);
                else if (node instanceof Arc arc) arc.setStroke(newV);
                else if (node instanceof TextField textField){
                    textoAColor.put(textField, newV.toString().substring(2,8));
                    textField.setStyle("-fx-background-color: transparent; -fx-border-color: black; -fx-text-fill: #"+textoAColor.get(textField)+"; -fx-font-size: "+textoATamano.get(textField));
                }
            });
            
            // Sección de Tamaño
            if (node instanceof TextField textField){
                Menu cambiarTamanoItem = new Menu("Tamaño");
                MenuItem tamanoItem = new MenuItem();
                ComboBox MenuTamanoPicker = new ComboBox();
                MenuTamanoPicker.getItems().addAll(6,7,8,9,10,12,14,16,18,20,24,28,32,36,48,72);
                MenuTamanoPicker.setValue(textoATamano.get(textField));
                
                tamanoItem.setGraphic(MenuTamanoPicker);
                cambiarTamanoItem.getItems().add(tamanoItem);
                menuContext.getItems().add(cambiarTamanoItem);
                
                MenuTamanoPicker.valueProperty().addListener((obsV, oldV, newV) -> {
                    textoATamano.put(textField, (Integer) newV);
                    textField.setStyle("-fx-background-color: transparent; -fx-border-color: black; -fx-text-fill: #"+textoAColor.get(textField)+"; -fx-font-size: "+textoATamano.get(textField));
                    
                    double initialWidth = Math.max(150, textoATamano.get(textField) * 9);
                    double charWidth = textoATamano.get(textField) * 0.5;
                    double calculatedWidth = textField.getText().length() * charWidth + 30;
                    double finalWidth = Math.max(initialWidth, calculatedWidth);

                    textField.setPrefWidth(finalWidth);
                });
            } else {
                Menu cambiarGrosorItem = new Menu("Grosor");
                MenuItem grosorItem = new MenuItem();
                Slider MenuGrosorSlider = new Slider();
                MenuGrosorSlider.setMin(3); MenuGrosorSlider.setMax(10);
                grosorItem.setGraphic(MenuGrosorSlider);
                cambiarGrosorItem.getItems().add(grosorItem);
                menuContext.getItems().add(cambiarGrosorItem);
                
                grosorItem.textProperty().bind(Bindings.format("%.1f px", MenuGrosorSlider.valueProperty()));
                
                if (node instanceof Circle circle) MenuGrosorSlider.setValue(circle.getRadius());
                else if (node instanceof Line line) MenuGrosorSlider.setValue(line.getStrokeWidth());
                else if (node instanceof Arc arc) MenuGrosorSlider.setValue(arc.getStrokeWidth());
                
                MenuGrosorSlider.valueProperty().addListener((obsV, oldV, newV) -> {
                    if (node instanceof Circle circle) circle.setRadius((double) newV);
                    else if (node instanceof Line line) line.setStrokeWidth((double) newV);
                    else if (node instanceof Arc arc) arc.setStrokeWidth((double) newV);
                });
            }
            
            // Sección de Borrar
            MenuItem borrarItem = new MenuItem("Eliminar"); menuContext.getItems().add(borrarItem);
            borrarItem.setOnAction(ev -> {
                zoomGroup.getChildren().remove(node);
                ev.consume();
            });

            menuContext.show(node, e.getSceneX(), e.getSceneY());
            e.consume();
        });

        node.setOnMouseClicked(e -> {
            if (eraserToolButton.isSelected()) {
                zoomGroup.getChildren().remove(node);
            }
            e.consume();
        });
    }

    @FXML
    private void resolver(ActionEvent event) throws IOException{
        RadioButton seleccion = (RadioButton) respuestas.getSelectedToggle();
        boolean correcto = false;
        if (seleccion.equals(respuesta1)) correcto = ProblemController.getProblem().getAnswers().get(pos.get(0)).getValidity();
        if (seleccion.equals(respuesta2)) correcto = ProblemController.getProblem().getAnswers().get(pos.get(1)).getValidity();
        if (seleccion.equals(respuesta3)) correcto = ProblemController.getProblem().getAnswers().get(pos.get(2)).getValidity();
        if (seleccion.equals(respuesta4)) correcto = ProblemController.getProblem().getAnswers().get(pos.get(3)).getValidity();
        
        if (correcto) {
            informacion("Correcto", "Respuesta correcta", "Has seleccionado la respuesta correcta.");
            PoiUPVApp.sumarAcierto();
        } else {
            // Buscar la respuesta correcta
            String correctAnswerText = "";
            for (Answer a : ProblemController.getProblem().getAnswers()) {
                if (a.getValidity()) {
                    correctAnswerText = a.getText();
                    break;
                }
            }
            error("Incorrecto", "Respuesta incorrecta", "La respuesta correcta es: " + correctAnswerText);
            PoiUPVApp.sumarFallo();
        }

        setScene("../view/ProblemView.fxml", "Problemas");
    }
    
    @FXML
    private void ZoomShortCut(KeyEvent event) {
        if ((new KeyCharacterCombination("+", KeyCombination.CONTROL_ANY)).match(event)) zoomIn((new ActionEvent()));
        if ((new KeyCharacterCombination("-", KeyCombination.CONTROL_ANY)).match(event)) zoomOut((new ActionEvent()));
    }
    
    
    @FXML
    private void goToProblems(ActionEvent event) throws IOException {
        if (respuesta1.isSelected() || respuesta2.isSelected() || respuesta3.isSelected() || respuesta4.isSelected()){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Abandonar sin Resolver");
            alert.setHeaderText("Pregunta no resuelta");
            alert.setContentText("No has confirmado tu respuesta.\n¿Seguro que quieres abandonar el problema?");
            
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() != ButtonType.OK) return;
        }
        setScene("../view/ProblemView.fxml", "Problemas");
    }
    
    @FXML
    private void goToInfo(ActionEvent event) throws IOException {
        FXMLLoader miCargador = new FXMLLoader(getClass().getResource("../view/InfoView_1.fxml"));
        Parent root = miCargador.load();
        
        //acceso al controlador de datos persona
        //InfoController controlador2 = miCargador.getController();
        
        Scene scene = new Scene(root,780,700);
        Stage stage = new Stage();
        stage.setMinWidth(700); stage.setMinHeight(865);
        stage.setMaxWidth(700); stage.setMaxHeight(865);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.setTitle("Carta Náutica -  Información");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }
    
    
    public void setScene(String ruta, String clave) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(ruta));
        Parent root = loader.load();

        Stage miStage = PoiUPVApp.getStage();

        boolean wasMaximized = miStage.isMaximized();
        if (!miStage.getTitle().equals("Carta Náutica - Mapa de problemas")){
            PoiUPVApp.currentHeight = miStage.getHeight();
            PoiUPVApp.currentWidth = miStage.getWidth();
        }
        
        // Crear y establecer la nueva escena
        Scene scene = new Scene(root);
        miStage.setScene(scene);
        miStage.setTitle("Carta Náutica - " + clave);

        // Casos especiales donde queremos cambiar el tamaño
        if (clave.equals("Mapa de problemas")) {
            miStage.setMaximized(true);
            miStage.setMinWidth(1250);
            miStage.setMinHeight(735);
        } else {
            miStage.setMinWidth(600);
            miStage.setMinHeight(735);
            if (wasMaximized) {
                miStage.setMaximized(false);
            } else {
                miStage.setWidth(PoiUPVApp.currentWidth);
                miStage.setHeight(PoiUPVApp.currentHeight);
            }
        }

        miStage.show();
    }

    private void error(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private void informacion(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}