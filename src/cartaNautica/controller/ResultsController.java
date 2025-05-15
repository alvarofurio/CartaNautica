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
import javafx.scene.chart.XYChart;
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
    @FXML
    private CategoryAxis fechaEjeX;
    @FXML
    private NumberAxis cantidadEjeY;
    
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
    
   
    /**
     * Carga los datos del usuario y actualiza la vista
     */
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
            actualizarTabla();
            
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
            for (Session i : sesiones) {
                if (i.getTimeStamp().isAfter(fechaInicio)){ sesionesFiltradas.add(i); }               
            }
            sesionesFiltradas.sort(Comparator.comparing(Session::getTimeStamp)); // ordena las sesiones por fecha :)
        }
    }
    
    /**
     * Actualiza las estadísticas con los datos filtrados
     */
    private void actualizarEstadisticas() {
        int totalAciertos = 0;
        int totalFallos = 0;
        int totalSesiones = sesionesFiltradas.size();
        
        for (Session sesion : sesionesFiltradas) {
            totalAciertos += sesion.getHits();
            totalFallos += sesion.getFaults();
        }
        
        // Actualizar etiquetas
        totalAciertosLabel.setText(String.valueOf(totalAciertos));
        totalFallosLabel.setText(String.valueOf(totalFallos));
        
        // Calcular porcentaje de aciertos
        double porcentaje = 0;
        if (totalAciertos + totalFallos > 0) {
            porcentaje = (double) totalAciertos / (totalAciertos + totalFallos) * 100;
        }
        
        porcentajeAciertosLabel.setText(String.format("%.1f%%", porcentaje));
        numeroSesionesLabel.setText(String.valueOf(totalSesiones));
    }
    
    /**
     * Actualiza la tabla con los datos filtrados
     */
    private void actualizarTabla() {
        ObservableList<Session> sesionesObservable = FXCollections.observableList(sesionesFiltradas);
        sesionesTable.setItems(sesionesObservable);
    }
    
    /**
     * Actualiza la gráfica con los datos filtrados
     */
    private void actualizarGrafica() {
        // Limpiar datos anteriores
        acumuladoChart.getData().clear();

        // Si no hay sesiones, no hay nada que mostrar
        if (!sesionesFiltradas.isEmpty()) {
            // Series para aciertos y fallos acumulados
            XYChart.Series<String, Number> aciertosAcumulados = new XYChart.Series<>();
            aciertosAcumulados.setName("Aciertos");

            XYChart.Series<String, Number> fallosAcumulados = new XYChart.Series<>();
            fallosAcumulados.setName("Fallos");

            int acumuladoAciertos = 0;
            int acumuladoFallos = 0;
            
            // Rellenar datos
            for (Session sesion : sesionesFiltradas) {
                String fechaStr = sesion.getTimeStamp().format(dateFormatter);

                acumuladoAciertos += sesion.getHits();
                acumuladoFallos += sesion.getFaults();

                aciertosAcumulados.getData().add(new XYChart.Data<>(fechaStr, acumuladoAciertos));
                fallosAcumulados.getData().add(new XYChart.Data<>(fechaStr, acumuladoFallos));
            }

            // Añadir series a la gráfica
            acumuladoChart.getData().addAll(aciertosAcumulados, fallosAcumulados);

            // Aplicar estilos directamente a las series
            for (int i = 0; i < acumuladoChart.getData().size(); i++) {
                XYChart.Series<String, Number> series = acumuladoChart.getData().get(i);

                // La primera serie (aciertos) en verde, la segunda (fallos) en rojo
                String color = (i == 0) ? "#4caf50" : "#f44336";
                series.getNode().setStyle("-fx-stroke: " + color + "; -fx-stroke-width: 3px;");

                // Aplicar color a los puntos de datos
                for (XYChart.Data<String, Number> data : series.getData()) {
                    data.getNode().setStyle("-fx-background-color: " + color + "; -fx-background-radius: 5px;");
                }
            }
        }
    }

    /**
     * Maneja el evento de aplicar filtro
     * @param event El evento de acción
     */
    @FXML
    private void aplicarFiltro(ActionEvent event) {
        cargarDatos();
    }

    /**
     * Navega a la vista de problemas
     * @param event El evento de acción
     * @throws java.io.IOException Si ocurre un error al cargar la vista
     */
    @FXML
    private void goToProblems(ActionEvent event) throws IOException {
        setScene("../view/ProblemView.fxml", "Problemas");
    }

    /**
     * Abre la vista de edición de perfil
     * @param event El evento de acción
     * @throws java.io.IOException Si ocurre un error al cargar la vista
     */
    @FXML
    private void editarOn(ActionEvent event) throws IOException {
        PoiUPVApp.setPrev(false); // Indicar que la vista anterior es ResultsView
        setScene("../view/EditView.fxml", "Editar perfil");
    }
    
    /**
     * Cierra la sesión actual
     * @param event El evento de acción
     * @throws java.io.IOException Si ocurre un error al cargar la vista
     */
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