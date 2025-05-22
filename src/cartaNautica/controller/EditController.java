/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cartaNautica.controller;

import cartaNautica.PoiUPVApp;
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
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
public class EditController implements Initializable {

    @FXML
    private TextField nicknameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField passwordVisibleField;
    @FXML
    private Button visibilityButton;
    @FXML
    private ImageView eyeImageView;
    @FXML
    private DatePicker birthDatePicker;
    @FXML
    private Button chooseAvatarButton;
    @FXML
    private Button confirmButton;
    @FXML
    private Button discardButton;    
    
    private Image avatarImage;
    private Image eyeOpenImage;
    private Image eyeClosedImage;
    private boolean passwordVisible = false;
    User user;
    @FXML
    private Circle circleImage;

    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        user = PoiUPVApp.getCurrentUser();
        
        confirmButton.disableProperty().bind(Bindings.or(birthDatePicker.valueProperty().isNull(),Bindings.or(emailField.textProperty().isEmpty(),Bindings.or(nicknameField.textProperty().isEmpty(),passwordField.textProperty().isEmpty()))));
        
        // Visibilidad contrasenya
        eyeOpenImage = new Image(getClass().getResourceAsStream("/resources/eye_open.png"));
        eyeClosedImage = new Image(getClass().getResourceAsStream("/resources/eye_closed.png"));
        passwordField.textProperty().bindBidirectional(passwordVisibleField.textProperty());
        
        // Campos inicializados a los valores del usuario
        nicknameField.setText(user.getNickName());
        emailField.setText(user.getEmail());
        passwordField.setText(user.getPassword()); // Capaz este campo mejor si no se inicializa a la contraseña anterior + ¿Añadir doble campo de contraseña como cuando la cambias en aplicaciones?
        birthDatePicker.setValue(user.getBirthdate());
        avatarImage=user.getAvatar();
        circleImage.setFill(new ImagePattern(avatarImage));
        
        // Deshabilitar campo nombre de usuario (no se puede modificar)
        nicknameField.setDisable(true);
        
        // Listeners
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
            passwordField.setStyle("");
            passwordVisibleField.setStyle("");
            visibilityButton.setStyle("-fx-background-color: #FFFFFF");
        });
        
        birthDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            birthDatePicker.setStyle("");
        });
        
        // Visibilidad contrasenya
        eyeOpenImage = new Image(getClass().getResourceAsStream("/resources/eye_open.png"));
        eyeClosedImage = new Image(getClass().getResourceAsStream("/resources/eye_closed.png"));
        passwordField.textProperty().bindBidirectional(passwordVisibleField.textProperty());
        
        // Cursor al pasar sobre el confirmButton
        confirmButton.setOnMouseEntered(event -> {
            confirmButton.setCursor(Cursor.HAND);
        });
        confirmButton.setOnMouseExited(event -> {
            confirmButton.setCursor(Cursor.DEFAULT);
        });
        
        // Cursor al pasar sobre el dicardButton
        discardButton.setOnMouseEntered(event -> {
            discardButton.setCursor(Cursor.HAND);
        });
        discardButton.setOnMouseExited(event -> {
            discardButton.setCursor(Cursor.DEFAULT);
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
        
        // Configurar el pulsado del enter al guardar cambios
        emailField.setOnKeyPressed(this::pressEnter);
        passwordField.setOnKeyPressed(this::pressEnter);
        passwordVisibleField.setOnKeyPressed(this::pressEnter);
        birthDatePicker.setOnKeyPressed(this::pressEnter);
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

    @FXML
    private void confirmar(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        LocalDate birthDate = birthDatePicker.getValue();
        boolean existsError = false;
        String error = "";
        
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
        
        // Validación de la fecha de nacimiento
        if (birthDate.plusYears(16).isAfter(LocalDate.now())) {
            existsError=true;
            error += "Debe ser mayor de 16 años para registrarse.\n\n";
            //Cambiar color cuándo sepamos cómo
        } else {
            birthDatePicker.setStyle("");
        }
        
        if (existsError) {
            error("Error", "Campos inválidos", error);
            return;
        }

        try {
            // Realizar los cambios en el usuario
            user.setEmail(email);
            user.setPassword(password);
            user.setAvatar(avatarImage);
            user.setBirthdate(birthDate);
            informacion("Éxito", "Cambios registrados", "Los cambios se han registrado correctamente.");
            setScene(PoiUPVApp.getPrev()?"Problemas":"Resultados");
        } catch (Exception e) {
            error("Error", "Error de registro", "Se produjo un error al intentar registrar: " + e.getMessage());
        }
    }

    @FXML
    private void descartar(ActionEvent event) throws IOException {
        setScene(PoiUPVApp.getPrev()?"Problemas":"Resultados");
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
    
    public void setScene(String clave) throws IOException  {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(PoiUPVApp.getPrev()?"../view/ProblemView.fxml":"../view/ResultsView.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage miStage = PoiUPVApp.getStage();
        miStage.setScene(scene);
        miStage.setTitle("Carta Náutica - "+clave);
        miStage.show();
    }
    
    private void pressEnter(KeyEvent event) {
    if (event.getCode() == KeyCode.ENTER && !confirmButton.isDisabled()) {
        confirmar(new ActionEvent());
    }
}
}
