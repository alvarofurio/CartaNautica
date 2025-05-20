/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cartaNautica;

import java.util.Optional;
import model.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author jose
 */
public class PoiUPVApp extends Application {
    
    private static User currentUser = null;
    private static Stage miStage;
    private static Boolean prev = true; // True -> Problems / False -> Results (para volver de EditView)
    
    private static int aciertos = 0;
    private static int fallos = 0;
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root;
        FXMLLoader loader;
        miStage = stage;
        
        //LoginView
        loader = new FXMLLoader(getClass().getResource("view/LoginView.fxml"));
        root = loader.load();
        Scene scene = new Scene(root);
        miStage.setMinWidth(root.minWidth(-1));
        miStage.setMinHeight(root.minHeight(-1));
        miStage.setHeight(550);miStage.setWidth(500);
        miStage.setScene(scene);
        miStage.setTitle("Carta Náutica - Inicio de sesión");
        miStage.show();
        miStage.setOnCloseRequest(this::cerrarApp);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    public static Stage getStage() {
        return miStage;
    }
    
    public static void setCurrentUser(User user) {
        currentUser = user;
    }
    
    public static User getCurrentUser() {
        return currentUser;
    }
    
    public static int getAciertos(){
        return aciertos;
    }
    public static int getFallos(){
        return fallos;
    }
    public static void sumarAcierto(){
        aciertos++;
    }
    public static void sumarFallo(){
        fallos++;
    }
    
    public static void guardarSesion(){
        currentUser.addSession(aciertos,fallos);
        currentUser = null;
        aciertos = 0; fallos = 0;
    }
    
    private void cerrarApp(WindowEvent event){
        if (miStage.getTitle().equals("Carta Náutica - Mapa de problemas")){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Cerrar sin guardar");
            alert.setHeaderText("Pregunta no respondida");
            alert.setContentText("No has contestado a la pregunta.\n¿Seguro que quieres cerrar la aplicación?");
            
            Optional<ButtonType> result = alert.showAndWait();
            if (!(result.isPresent() && result.get() == ButtonType.OK)){
                event.consume();
                return ;
            }
        }
        //guardarSesion();
        
    }
    
    
    public static Boolean getPrev() {return prev;}
    
    public static void setPrev(Boolean b) {prev = b;}
}
