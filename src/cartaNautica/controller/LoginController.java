/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cartaNautica.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import cartaNautica.PoiUPVApp;
import static cartaNautica.PoiUPVApp.getStage;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.NavDAOException;
import model.User;
import model.Navigation;

/**
 *
 * @author hugol
 */
public class LoginController implements Initializable {
    
    @FXML
    private TextField nicknameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Button registerButton;
    @FXML
    private TextField passwordVisibleField;
    @FXML
    private Button visibilityButton;
    @FXML
    private ImageView eyeImageView;
    
    private Image eyeOpenImage;
    private Image eyeClosedImage;
    private boolean passwordVisible = false;
    
    /**
     * Inicializa el controlador.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Desactivar botón inicio de sesión
        loginButton.disableProperty().bind(Bindings.or(nicknameField.textProperty().isEmpty(),passwordField.textProperty().isEmpty()));
        
        // Visibilidad contrasenya
        eyeOpenImage = new Image(getClass().getResourceAsStream("/resources/eye_open.png"));
        eyeClosedImage = new Image(getClass().getResourceAsStream("/resources/eye_closed.png"));
        passwordField.textProperty().bindBidirectional(passwordVisibleField.textProperty());
        
        // Cursor al pasar sobre el loginButton
        loginButton.setOnMouseEntered(event -> {
            loginButton.setCursor(Cursor.HAND);
        });
        loginButton.setOnMouseExited(event -> {
            loginButton.setCursor(Cursor.DEFAULT);
        });
        
        // Cursor al pasar sobre el registerButton
        registerButton.setOnMouseEntered(event -> {
            registerButton.setCursor(Cursor.HAND);
        });
        registerButton.setOnMouseExited(event -> {
            registerButton.setCursor(Cursor.DEFAULT);
        });
        
        // Cursor al pasar sobre el visibilityButton
        visibilityButton.setOnMouseEntered(event -> {
            visibilityButton.setCursor(Cursor.HAND);
        });
        visibilityButton.setOnMouseExited(event -> {
            visibilityButton.setCursor(Cursor.DEFAULT);
        });
        
        // configurar el pulsado del enter para iniciar sesión
        nicknameField.setOnKeyPressed(this::pressEnter);
        passwordField.setOnKeyPressed(this::pressEnter);
        passwordVisibleField.setOnKeyPressed(this::pressEnter);
        
    }
    
    /**
     * Maneja el evento de clic en el botón de login.
     * Comprueba las credenciales y carga la vista principal si son correctas.
     */
    @FXML
    private void iniciarSesion(ActionEvent event) throws NavDAOException, IOException  {
        String nickname = nicknameField.getText().trim();
        String password = passwordField.getText();

        // Autenticar usuario utilizando Navigation
        Navigation navigation = Navigation.getInstance();
        if (navigation.exitsNickName(nickname)) { 
            User user = navigation.authenticate(nickname, password);
            if (user != null) {
                PoiUPVApp.setCurrentUser(user);
                setScene("../view/ProblemView.fxml","Problemas");
            } else {
                error("Error", "Autenticación fallida", "Contraseña incorrecta.");
            }
        } else { 
            error("Error", "Autenticación fallida", "Nombre de usuario no registrado.");
        }
    }
    /**
     * Maneja el evento de clic en el botón de registro.
     * Carga la vista de registro.
     */
    @FXML
    private void registrarse(ActionEvent event) throws IOException {
        setScene("../view/RegisterView.fxml","Registro");
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
    
    /**
     * Muestra una alerta con la información proporcionada.
     */
    private void error(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void cambiarVisibilidad(ActionEvent event) {
        passwordVisible = !passwordVisible;
        passwordField.setVisible(!passwordVisible);
        passwordField.setManaged(!passwordVisible);
        passwordVisibleField.setVisible(passwordVisible);
        passwordVisibleField.setManaged(passwordVisible); // Cambiar la imagen del ojo según el estado
        eyeImageView.setImage(passwordVisible ? eyeOpenImage : eyeClosedImage);
    }
      
    private void pressEnter(KeyEvent event) {
    if (event.getCode() == KeyCode.ENTER && !loginButton.isDisabled()) {
        try {
            iniciarSesion(new ActionEvent());
        } catch (NavDAOException | IOException e) {
            error("Error", "Error al iniciar sesión", e.getMessage());
        }
    }
}
}