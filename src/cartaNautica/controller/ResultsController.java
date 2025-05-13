/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cartaNautica.controller;

import cartaNautica.PoiUPVApp;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author hugol
 */
public class ResultsController implements Initializable {

    @FXML
    private Menu profileMenu;
    @FXML
    private TableView<?> problemsTable;
    @FXML
    private TableColumn<?, ?> problemColumn;
    @FXML
    private TableColumn<?, ?> questionColumn;
    @FXML
    private Button solveButton;
    @FXML
    private Button randomButton;
    @FXML
    private Button backButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
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
        backButton.setOnMouseEntered(event -> {
            backButton.setStyle("-fx-background-color: #0096C9");
        });
        backButton.setOnMouseExited(event -> {
            backButton.setStyle("-fx-background-color: transparent");
        });
       
        // Botón deshabilitado mientras no tengas un problema seleccionado
        solveButton.disableProperty().bind(Bindings.equal(-1,problemsTable.getSelectionModel().selectedIndexProperty()));
    }    

    @FXML
    private void editarOn(ActionEvent event) throws IOException {
        PoiUPVApp.setPrev(false);
        setScene("../view/EditView.fxml", "Editar perfil");
    }
    
    @FXML
    private void cerrarSesiOn(ActionEvent event) throws IOException{
        PoiUPVApp.setCurrentUser(null);
        setScene("../view/LoginView.fxml", "Inicio de sesión");
    }

    @FXML
    private void goToProblems(ActionEvent event) throws IOException {
        setScene("../view/ProblemView.fxml", "Editar perfil");
    }
    
    public void setScene(String ruta, String clave) throws IOException  {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(ruta));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage miStage = PoiUPVApp.getStage();
        miStage.setScene(scene);
        miStage.setTitle("Carta Náutica - "+clave);
        miStage.show();
    }
    
    private void error(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    //HOla
}
