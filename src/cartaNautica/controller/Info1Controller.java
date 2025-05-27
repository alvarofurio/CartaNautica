/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cartaNautica.controller;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

/**
 * FXML Controller class
 *
 * @author hugol
 */
public class Info1Controller implements Initializable {

    @FXML
    private ToggleButton pointButton;
    @FXML
    private ToggleGroup infoToggleGroup;
    @FXML
    private ToggleButton lineButton;
    @FXML
    private ToggleButton arcButton;
    @FXML
    private ToggleButton positionButton;
    @FXML
    private ToggleButton transButton;
    @FXML
    private ToggleButton reglaButton;
    @FXML
    private ToggleButton estiloButton;
    @FXML
    private ToggleButton atajosButton;
    @FXML
    private Label titleLabel;
    @FXML
    private Label descLabel;
    @FXML
    private MediaView videoViewer;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        pointButton.setSelected(true);
        titleLabel.setText("Punto");
        descLabel.setText("La herramienta Punto te permite dibujar puntos sobre la carta náutica. Para ello, tras seleccionar la herramienta, realiza clic izquierdo sobre el mapa.");
        
        Media media = new Media(getClass().getResource("/vid1.mp4").toString());

        MediaPlayer mp = new MediaPlayer(media);
        
        videoViewer.setMediaPlayer(mp);
        
    }    
    
}
