/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cartaNautica;

import model.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author jose
 */
public class PoiUPVApp extends Application {
    
    private static User currentUser = null;
    private static Stage miStage;
    private static Boolean prev = true; // True -> Problems / False -> Results (para volver de EditView)
    
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
    
    public static Boolean getPrev() {return prev;}
    
    public static void setPrev(Boolean b) {prev = b;}
}
