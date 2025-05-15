/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cartaNautica.controller;

import cartaNautica.PoiUPVApp;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
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
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.Session;
import model.User;

/**
 * FXML Controller class para la vista de resultados
 *
 * @author Jose
 */
public class ResultsController implements Initializable {

    @FXML
    private Menu profileMenu;
    @FXML
    private Button backButton;
    @FXML
    private DatePicker fechaInicioPicker;
    @FXML
    private Button aplicarFiltroButton;
    @FXML
    private Label totalAciertosLabel;
    @FXML
    private Label totalFallosLabel;
    @FXML
    private Label porcentajeAciertosLabel;
    @FXML
    private Label numeroSesionesLabel;
    @FXML
    private TabPane resultadosTabPane;
    @FXML
    private TableView<Session> sesionesTable;
    @FXML
    private TableColumn<Session, String> fechaColumn;
    @FXML
    private TableColumn<Session, String> horaColumn;
    @FXML
    private TableColumn<Session, Integer> aciertosColumn;
    @FXML
    private TableColumn<Session, Integer> fallosColumn;
    @FXML
    private TableColumn<Session, Integer> totalProblemasColumn;
    @FXML
    private LineChart<String, Number> acumuladoChart;
    
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    private List<Session> sesionesFiltradas;
    
    private User currentUser;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Obtener usuario actual
        currentUser = PoiUPVApp.getCurrentUser();
        
        profileMenu.setText(currentUser.getNickName());
        
        // Configurar DatePicker con la fecha actual menos 30 días por defecto
        fechaInicioPicker.setValue(LocalDate.now().minusDays(30));
        
        // Cursor al pasar sobre el backButton
        backButton.setOnMouseEntered(event -> {
            backButton.setStyle("-fx-background-color: #0096C9");

        });
        backButton.setOnMouseExited(event -> {
            backButton.setStyle("-fx-background-color: transparent");
        });
        
        // Cursor al pasar sobre el aplicarFiltroButton
        aplicarFiltroButton.setOnMouseEntered(event -> {
            aplicarFiltroButton.setCursor(Cursor.HAND);
        });
        aplicarFiltroButton.setOnMouseExited(event -> {
            aplicarFiltroButton.setCursor(Cursor.DEFAULT);
        });
        
        // Configurar columnas de la tabla
        fechaColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTimeStamp().format(dateFormatter)));
        
        horaColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTimeStamp().format(timeFormatter)));

        aciertosColumn.setCellValueFactory(cellData-> new SimpleIntegerProperty(cellData.getValue().getHits()).asObject());
        
        fallosColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getFaults()).asObject());
        
        totalProblemasColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getHits() + cellData.getValue().getFaults()).asObject());
        
        // Cargar datos iniciales
        cargarDatos();
    }
    
   
    private void cargarDatos() {
                
        try {
            
            LocalDate fechaInicio = fechaInicioPicker.getValue();
            if (fechaInicio == null) {
                fechaInicio = LocalDate.now().minusDays(30); // Por defecto últimos 30 días
            }
            
            // Filtrar sesiones por fecha
            filtrarSesiones(fechaInicio.atStartOfDay());
            
            // Actualizar estadísticas
            actualizarEstadisticas();
            
            // Actualizar tabla
            ObservableList<Session> sesionesObservable = FXCollections.observableList(sesionesFiltradas);
            sesionesTable.setItems(sesionesObservable);
            
            // Actualizar gráfica
            actualizarGrafica();
        } catch (Exception e) {
            error("Error", "Error al cargar datos", "Se produjo un error al cargar los datos: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void filtrarSesiones(LocalDateTime fechaInicio) {
        
        List<Session> sesiones = currentUser.getSessions();
        
        if (sesiones == null || sesiones.isEmpty()) {
            sesionesFiltradas = new ArrayList<>();
        } else {
            sesionesFiltradas = new ArrayList<>();
            for (Session i : sesiones) {
                if (i.getTimeStamp().isAfter(fechaInicio)){ sesionesFiltradas.add(i); }               
            }
            sesionesFiltradas.sort(Comparator.comparing(Session::getTimeStamp)); // ordena las sesiones por fecha :)
        }
    }
    
    private void actualizarEstadisticas() {
        
        int aciertos = 0;
        int fallos = 0;
        int numSesiones = sesionesFiltradas.size();
        
        for (Session i : sesionesFiltradas) {
            aciertos += i.getHits();
            fallos += i.getFaults();
        }
        
        totalAciertosLabel.setText(String.valueOf(aciertos));
        totalFallosLabel.setText(String.valueOf(fallos));

        double porcentaje = 0;
        if (aciertos + fallos > 0) {
            porcentaje = (double) aciertos / (aciertos + fallos) * 100;
        }
        
        porcentajeAciertosLabel.setText(String.format("%.2f%%", porcentaje));
        numeroSesionesLabel.setText(String.valueOf(numSesiones));
    }

    private void actualizarGrafica() {
        
        acumuladoChart.getData().clear();
        
        if (!sesionesFiltradas.isEmpty()) {
            // Series para aciertos y fallos acumulados
            Series<String, Number> fechaAciertos = new Series<>();
            fechaAciertos.setName("Aciertos");

            Series<String, Number> fechaFallos = new Series<>();
            fechaFallos.setName("Fallos");

            int aciertos = 0;
            int fallos = 0;
            
            String lastDate = sesionesFiltradas.getFirst().getTimeStamp().format(dateFormatter);
            for (Session sesion : sesionesFiltradas) {
                
                String fecha = sesion.getTimeStamp().format(dateFormatter);
                
                if (!fecha.equals(lastDate)) {
                    
                    fechaAciertos.getData().add(new Data<>(lastDate, aciertos));
                    fechaFallos.getData().add(new Data<>(lastDate, fallos));
                    
                    lastDate = fecha;
                }
                    
                aciertos += sesion.getHits();
                fallos += sesion.getFaults();
            }
            fechaAciertos.getData().add(new Data<>(lastDate, aciertos));
            fechaFallos.getData().add(new Data<>(lastDate, fallos));

            acumuladoChart.getData().addAll(fechaAciertos, fechaFallos);
            
            // Estilo Aciertos
            Series<String, Number> series = acumuladoChart.getData().get(0);
            series.getNode().setStyle("-fx-stroke: #4caf50; -fx-stroke-width: 3px;");
            for (Data<String, Number> data : series.getData()) {
                data.getNode().setStyle("-fx-background-color: #4caf50; -fx-background-radius: 5px;");
            }
            
            //Estilo Fallos
            series = acumuladoChart.getData().get(1);
            series.getNode().setStyle("-fx-stroke: #f44336; -fx-stroke-width: 3px;");
            for (Data<String, Number> data : series.getData()) {
                data.getNode().setStyle("-fx-background-color: #f44336; -fx-background-radius: 5px;");
            }
        }
    }


    @FXML
    private void aplicarFiltro(ActionEvent event) {
        cargarDatos();
    }


    @FXML
    private void goToProblems(ActionEvent event) throws IOException {
        setScene("../view/ProblemView.fxml", "Problemas");
    }


    @FXML
    private void editarOn(ActionEvent event) throws IOException {
        PoiUPVApp.setPrev(false); // Indicar que la vista anterior es ResultsView
        setScene("../view/EditView.fxml", "Editar perfil");
    }
    

    @FXML
    private void cerrarSesiOn(ActionEvent event) throws IOException{
        PoiUPVApp.setCurrentUser(null);
        setScene("../view/LoginView.fxml", "Inicio de sesión");
    }
    
    public void setScene(String ruta, String clave) throws IOException {
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
}