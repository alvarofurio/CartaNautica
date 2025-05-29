/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cartaNautica.controller;

import cartaNautica.PoiUPVApp;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import java.util.Random;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import model.NavDAOException;
import model.Navigation;
import model.Problem;


/**
 * FXML Controller class
 *
 * @author hugol
 */
public class ProblemController implements Initializable {

    @FXML
    private TableView<Problem> problemsTable;
    @FXML
    private Button solveButton;
    @FXML
    private Button randomButton;
    
    private List<Problem> misProblems;
    private ObservableList<Problem> problems = null;
    @FXML
    private TableColumn<Problem, Integer> problemColumn;
    @FXML
    private TableColumn<Problem, String> questionColumn;
    @FXML
    private Menu profileMenu;
    @FXML
    private Button resultsButton;
    
    private static Problem problem;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            Navigation navigation = Navigation.getInstance();
            misProblems = navigation.getProblems();
            problems = FXCollections.observableList(misProblems);
            problemsTable.setItems(problems);
            
            problemColumn.setCellValueFactory(problemFila -> new SimpleIntegerProperty(navigation.getProblems().indexOf(problemFila.getValue()) + 1).asObject());
            questionColumn.setCellValueFactory(problemFila-> new SimpleStringProperty(problemFila.getValue().getText()));
            
            profileMenu.setText(PoiUPVApp.getCurrentUser().getNickName()); 
        } catch(NavDAOException e) {
            error("Error", "Error con la base de datos", "No se han podido recuperar las preguntas.");
        }
        
        // Cursor al pasar sobre el solveButton
        solveButton.setOnMouseEntered(event -> {
            solveButton.setCursor(Cursor.HAND);
        });
        solveButton.setOnMouseExited(event -> {
            solveButton.setCursor(Cursor.DEFAULT);
        });
        
        // Cursor al pasar sobre el randomButton
        randomButton.setOnMouseEntered(event -> {
            randomButton.setCursor(Cursor.HAND);
        });
        randomButton.setOnMouseExited(event -> {
            randomButton.setCursor(Cursor.DEFAULT);
        });
        
        // resultsButton 
        resultsButton.setOnMouseEntered(event -> {
            resultsButton.setStyle("-fx-background-color: #0096C9");
        });
        resultsButton.setOnMouseExited(event -> {
            resultsButton.setStyle("-fx-background-color: transparent");
        });
       
        // Botón deshabilitado mientras no tengas un problema seleccionado
        solveButton.disableProperty().bind(Bindings.equal(-1,problemsTable.getSelectionModel().selectedIndexProperty()));
        
        problemsTable.setOnKeyPressed(this::pressEnter);
    }    
 

    @FXML
    private void cerrarSesiOn(ActionEvent event) throws IOException{
        PoiUPVApp.guardarSesion();
        setScene("../view/LoginView.fxml", "Inicio de sesión");
    }

    @FXML
    private void goToResults(MouseEvent event) throws IOException {
        setScene("../view/ResultsView.fxml", "Resultados");
    }

    @FXML
    private void selectProblem(ActionEvent event) throws IOException {
        problem = problemsTable.getSelectionModel().getSelectedItem();
        setScene("../view/SolveProblemView.fxml", "Mapa de problemas");
    }
    
    @FXML
    private void selectRandomProblem(ActionEvent event) throws IOException {
        int randomIndex = (new Random()).nextInt(misProblems.size());
        problem =  misProblems.get(randomIndex);
        setScene("../view/SolveProblemView.fxml", "Mapa de problemas");
    }
    
    public static Problem getProblem(){
        return problem;
    }
    

    @FXML
    private void editarOn(ActionEvent event) throws IOException {
        PoiUPVApp.setPrev(true);
        setScene("../view/EditView.fxml", "Editar perfil");
    }
    
    private void error(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
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
    
    private void pressEnter(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER && !solveButton.isDisabled()) {
            try {
                selectProblem(new ActionEvent());
            } catch (IOException e) {
                error("Error", "Error al Abrir el Problema", e.getMessage());
            }
        }
        event.consume();
    }
}