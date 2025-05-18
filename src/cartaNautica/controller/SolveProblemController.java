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
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
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
    private Slider widthSlider;
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
    @FXML
    private ToggleGroup drawToggleGroup;
    @FXML
    private Button transportador;
    @FXML
    private Button regla;
    @FXML
    private Slider angleSlider;

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
        
        // Transportador
        final double[] offSetX = new double[1];
        final double[] offSetY = new double[1];
        
        transportador.setOnMousePressed( event -> {
           offSetX[0] = event.getSceneX() - transportador.getTranslateX();
           
           offSetY[0] = event.getSceneY() - transportador.getTranslateY();
        });
        
        transportador.setOnMouseDragged( event -> {
           transportador.setTranslateX(event.getSceneX() - offSetX[0]);
           
           transportador.setTranslateY(event.getSceneY() - offSetY[0]);
        });
        
        transportador.visibleProperty().bind(protractorToolButton.selectedProperty());
        
        // Regla
        regla.setOnMousePressed( event -> {
           offSetX[0] = event.getSceneX() - regla.getTranslateX();
           
           offSetY[0] = event.getSceneY() - regla.getTranslateY();
        });
        
        regla.setOnMouseDragged( event -> {
           regla.setTranslateX(event.getSceneX() - offSetX[0]);
           
           regla.setTranslateY(event.getSceneY() - offSetY[0]);
        });
        
        regla.visibleProperty().bind(rulerToolButton.selectedProperty());
        
        rotationAngleLabel.textProperty().bind(Bindings.format("%.2f", angleSlider.valueProperty()));
        
        regla.rotateProperty().bind(Bindings.add(angleSlider.valueProperty(), -180));
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
    private void limpiarTodo(ActionEvent event) {
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
            ProblemController.sumarAcierto();
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
            ProblemController.sumarFallo();
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