/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cartaNautica.controller;

import cartaNautica.PoiUPVApp;
import static cartaNautica.PoiUPVApp.getStage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.DialogPane;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Navigation;
import model.User;

/**
 * FXML Controller class
 *
 * @author hugol
 */
public class RegisterController implements Initializable {

    @FXML
    private TextField nicknameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private DatePicker birthDatePicker;
    @FXML
    private Button chooseAvatarButton;
    @FXML
    private Button registerButton;
    @FXML
    private Button backButton;
    @FXML
    private TextField passwordVisibleField;
    @FXML
    private ImageView eyeImageView;
    @FXML
    private Button visibilityButton;
    
    // Nuevos campos para confirmar contraseña
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private TextField confirmPasswordVisibleField;
    private Image avatarImage;
    private Image eyeOpenImage;
    private Image eyeClosedImage;
    private boolean passwordVisible = false;
    @FXML
    private Circle circleImage;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Deshabilitar el botón de registro si algún campo está vacío
        registerButton.disableProperty().bind(
            Bindings.or(
                birthDatePicker.valueProperty().isNull(),
                Bindings.or(
                    emailField.textProperty().isEmpty(),
                    Bindings.or(
                        nicknameField.textProperty().isEmpty(),
                        Bindings.or(
                            passwordField.textProperty().isEmpty(),
                            confirmPasswordField.textProperty().isEmpty()
                        )
                    )
                )
            )
        );
        
        //Valores por defecto de los campos fecha nacimiento y avatar
        birthDatePicker.setValue(LocalDate.now().minusYears(16));
        avatarImage = new Image(getClass().getResourceAsStream("/resources/default_avatar.png"));
        circleImage.setFill(new ImagePattern(avatarImage));
        
        // Listeners para quitar estilos de error cuando cambian los campos
        nicknameField.textProperty().addListener((obs, oldVal, newVal) -> {
            nicknameField.setStyle("");
        });
        
        emailField.textProperty().addListener((obs, oldVal, newVal) -> {
            emailField.setStyle("");
        });
        
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
            passwordField.setStyle("");
            passwordVisibleField.setStyle("");
            visibilityButton.setStyle("-fx-background-color: #FFFFFF");
        });
        
        // Listener para campo de confirmar contraseña
        confirmPasswordField.textProperty().addListener((obs, oldVal, newVal) -> {
            confirmPasswordField.setStyle("");
            confirmPasswordVisibleField.setStyle("");
        });
        
        birthDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            birthDatePicker.setStyle("");
        });
        
        // Vincular campos de contraseña con sus versiones visibles
        passwordField.textProperty().bindBidirectional(passwordVisibleField.textProperty());
        confirmPasswordField.textProperty().bindBidirectional(confirmPasswordVisibleField.textProperty());
        
        // Cargar imágenes de ojo abierto/cerrado
        eyeOpenImage = new Image(getClass().getResourceAsStream("/resources/eye_open.png"));
        eyeClosedImage = new Image(getClass().getResourceAsStream("/resources/eye_closed.png"));
        
        // Configuración de cursores para botones
        configurarCursores();
        
        // Configurar el pulsado del enter al registrarse
        configurarEnterKeyPressed();
    }
    
    /**
     * Configura los cursores para todos los elementos interactivos
     */
    private void configurarCursores() {
        // Cursor al pasar sobre el registerButton
        registerButton.setOnMouseEntered(event -> {
            registerButton.setCursor(Cursor.HAND);
        });
        registerButton.setOnMouseExited(event -> {
            registerButton.setCursor(Cursor.DEFAULT);
        });
        
        // Cursor al pasar sobre el backButton
        backButton.setOnMouseEntered(event -> {
            backButton.setCursor(Cursor.HAND);
        });
        backButton.setOnMouseExited(event -> {
            backButton.setCursor(Cursor.DEFAULT);
        });
        
        // Cursor al pasar sobre el visibilityButton
        visibilityButton.setOnMouseEntered(event -> {
            visibilityButton.setCursor(Cursor.HAND);
        });
        visibilityButton.setOnMouseExited(event -> {
            visibilityButton.setCursor(Cursor.DEFAULT);
        });
        
        // Cursor al pasar sobre el chooseAvatarButton
        chooseAvatarButton.setOnMouseEntered(event -> {
            chooseAvatarButton.setCursor(Cursor.HAND);
        });
        chooseAvatarButton.setOnMouseExited(event -> {
            chooseAvatarButton.setCursor(Cursor.DEFAULT);
        });
        
        // Cursor al pasar sobre el birthDatePicker
        birthDatePicker.setOnMouseEntered(event -> {
            birthDatePicker.setCursor(Cursor.HAND);
        });
        birthDatePicker.setOnMouseExited(event -> {
            birthDatePicker.setCursor(Cursor.DEFAULT);
        });
    }
    
    /**
     * Configura el evento de pulsado de Enter para todos los campos
     */
    private void configurarEnterKeyPressed() {
        nicknameField.setOnKeyPressed(this::pressEnter);
        emailField.setOnKeyPressed(this::pressEnter);
        passwordField.setOnKeyPressed(this::pressEnter);
        passwordVisibleField.setOnKeyPressed(this::pressEnter);
        confirmPasswordField.setOnKeyPressed(this::pressEnter);
        confirmPasswordVisibleField.setOnKeyPressed(this::pressEnter);
        birthDatePicker.setOnKeyPressed(this::pressEnter);
    }

    /**
     * Abre un diálogo para seleccionar una imagen de avatar
     */
    @FXML
    private void seleccionarImagen(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen de avatar");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File selectedFile = fileChooser.showOpenDialog(chooseAvatarButton.getScene().getWindow());
        if (selectedFile != null) {
            avatarImage = new Image(selectedFile.toURI().toString());
            circleImage.setFill(new ImagePattern(avatarImage));
        }
    }

    /**
     * Realiza el registro del usuario tras validar todos los campos
     */
    @FXML
    private void registrarse(ActionEvent event) {
        String nickname = nicknameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        LocalDate birthDate = birthDatePicker.getValue();
        boolean existsError = false;
        String error = "";
        
        // Validación del nickname
        if (!User.checkNickName(nickname)) {
            existsError=true;
            error += "El nickname debe contener entre 6 y 15 caracteres alfanuméricos, guiones o subguiones.\n\n";
            nicknameField.setStyle("-fx-background-color: #FCE5E0");
        } else {
            nicknameField.setStyle("");
        }
        
        // Validación del email
        if (!User.checkEmail(email)) {
            existsError=true;
            error += "Por favor, introduzca un email válido.\n\n";
            emailField.setStyle("-fx-background-color: #FCE5E0");
        } else {
            emailField.setStyle("");
        }
        
        // Validación de la contraseña
        if (!User.checkPassword(password)) {
            existsError=true;
            error += "La contraseña debe tener entre 8 y 20 caracteres, incluir al menos una letra mayúscula, una minúscula, un número y un carácter especial (!@#$%&*()-+=).\n\n";
            passwordField.setStyle("-fx-background-color: #FCE5E0");
            passwordVisibleField.setStyle("-fx-background-color: #FCE5E0");
            visibilityButton.setStyle("-fx-background-color: #FCE5E0");
        } else {
            passwordField.setStyle("");
            passwordVisibleField.setStyle("");
            visibilityButton.setStyle("-fx-background-color: #FFFFFF");
        }
        
        // Validación de coincidencia de contraseñas
        if (!password.equals(confirmPassword)) {
            existsError=true;
            error += "Las contraseñas no coinciden.\n\n";
            confirmPasswordField.setStyle("-fx-background-color: #FCE5E0");
            confirmPasswordVisibleField.setStyle("-fx-background-color: #FCE5E0");
        } else {
            confirmPasswordField.setStyle("");
            confirmPasswordVisibleField.setStyle("");
        }
        
        // Validación de la fecha de nacimiento
        if (birthDate.plusYears(16).isAfter(LocalDate.now())) {
            existsError=true;
            error += "Debe ser mayor de 16 años para registrarse.\n\n";
            birthDatePicker.setStyle("-fx-background-color: #FCE5E0");
        } else {
            birthDatePicker.setStyle("");
        }
        
        if (existsError) {
            error("Error", "Campos inválidos", error);
            return;
        }
        
        try {
            // Verificar si el nickname ya existe en el sistema
            Navigation navigation = Navigation.getInstance();
            if (navigation.exitsNickName(nickname)) {
                error("Error", "Nombre de usuario no disponible", "El nombre de usuario ya está en uso. Por favor, elija otro.");
                nicknameField.setStyle("-fx-background-color: #FCE5E0");
                return;
            }
            
            // Registrar usuario utilizando Navigation
            User user = navigation.registerUser(nickname, email, password, avatarImage, birthDate);
            informacion("Éxito", "Registro completado", "Se ha registrado correctamente.");
            setScene("../view/LoginView.fxml","Inicio de sesión");
        } catch (Exception e) {
            error("Error", "Error de registro", "Se produjo un error al intentar registrar: " + e.getMessage());
        }
    }

    /**
     * Vuelve a la pantalla de inicio de sesión
     */
    @FXML
    private void volver(ActionEvent event) throws IOException {
        setScene("../view/LoginView.fxml","Inicio de sesión");
    }
    
    /**
     * Muestra un mensaje de error
     */
    private void error(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setMinHeight(Region.USE_PREF_SIZE);
        dialogPane.setPrefHeight(Region.USE_COMPUTED_SIZE);
        alert.showAndWait();
    }
    
    /**
     * Muestra un mensaje de información
     */
    private void informacion(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Cambia la visibilidad solo del campo de contraseña principal
     */
    @FXML
    private void cambiarVisibilidad(ActionEvent event) {
        passwordVisible = !passwordVisible;
        
        // Cambiar visibilidad SOLO del campo de contraseña principal
        passwordField.setVisible(!passwordVisible);
        passwordField.setManaged(!passwordVisible);
        passwordVisibleField.setVisible(passwordVisible);
        passwordVisibleField.setManaged(passwordVisible);
        
        // Cambiar la imagen del ojo según el estado
        eyeImageView.setImage(passwordVisible ? eyeOpenImage : eyeClosedImage);
    }
    
    /**
     * Cambia a la escena especificada
     */
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
     * Maneja el evento de presionar Enter en cualquier campo
     */
    private void pressEnter(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER && !registerButton.isDisabled()) {
            registrarse(new ActionEvent());
        }
    }
}