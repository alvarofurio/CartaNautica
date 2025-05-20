package cartaNautica.controller;

import cartaNautica.PoiUPVApp;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
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
        tamanosFuente.addAll(6,7,8,9,10,12,14,16,18,20,24,28,32,36);
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
        eraserCursor = new ImageCursor(new Image(getClass().getResourceAsStream("/resources/cursorGoma.png")), 50, 200);
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
        rotationAngleLabel.textProperty().bind(Bindings.format("%.1f", angleSlider.valueProperty()));
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
    private void goToProblems(ActionEvent event) throws IOException {
        setScene("../view/ProblemView.fxml", "Problemas");
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
            
            configurarEventosEliminacion(punto);
            
        } else if(lineToolButton.isSelected()){
            linea = new Line(pointInZoomGroup.getX(), pointInZoomGroup.getY(), pointInZoomGroup.getX(), pointInZoomGroup.getY());
            linea.setStroke(colorPicker.getValue());
            linea.setStrokeWidth(sizeSelector.getValue());
            zoomGroup.getChildren().add(linea);
            
            configurarEventosEliminacion(linea);
        }
    }
    
    @FXML
    private void onMouseDragged(MouseEvent event) {
        System.out.println("sdg");
        Point2D pointInZoomGroup = zoomGroup.sceneToLocal(event.getSceneX(), event.getSceneY());
        if(lineToolButton.isSelected()){
            linea.setEndX(pointInZoomGroup.getX());
            linea.setEndY(pointInZoomGroup.getY());
            event.consume();
        }
    }
    
    @FXML
    private void onMouseReleased(MouseEvent event) {
    }


    @FXML
    private void limpiarTodo(ActionEvent event) {
    }
    
    private void configurarEventosEliminacion(Node node) {
        // Menú contextual (clic derecho)
        node.setOnContextMenuRequested(e -> {
            ContextMenu menuContext = new ContextMenu();
            MenuItem borrarItem = new MenuItem("Eliminar");
            menuContext.getItems().add(borrarItem);
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
    
    public void setScene(String ruta, String clave) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(ruta));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage miStage = PoiUPVApp.getStage();
        miStage.setScene(scene);
        miStage.setTitle("Carta Náutica - "+clave);
        miStage.setMaximized(false);
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