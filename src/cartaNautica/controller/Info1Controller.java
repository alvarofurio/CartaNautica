/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cartaNautica.controller;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
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
    @FXML
    private VBox vboxVideo;
    @FXML
    private VBox vboxNoVideo;
    @FXML
    private Button closeButton;
    @FXML
    private Label puntoLabel;
    @FXML
    private Label rectaLabel;
    @FXML
    private Label arcLabel;
    @FXML
    private Label posLabel;
    @FXML
    private Label textLabel;
    @FXML
    private Label protractorLabel;
    @FXML
    private Label reglaLabel;
    @FXML
    private Label estiloLabel;
    @FXML
    private Label atajosLabel;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        vboxVideo.setVisible(false);
        vboxNoVideo.setVisible(true);
        
        vboxVideo.visibleProperty().bind(Bindings.not(infoToggleGroup.selectedToggleProperty().isNull()));
        vboxNoVideo.visibleProperty().bind(infoToggleGroup.selectedToggleProperty().isNull());
        
        infoToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                //No tiene que hacer nada: Ya lo hace el Binding
            } else if (newValue.equals(pointButton)) {
                titleLabel.setText("Punto");
                descLabel.setText(puntoLabel.getText());

                Media media = new Media(getClass().getResource("/resources/vidPunto.mp4").toExternalForm());

                MediaPlayer mediaPlayer = new MediaPlayer(media);

                videoViewer.setMediaPlayer(mediaPlayer);

                mediaPlayer.play();
           } else if (newValue.equals(lineButton)) {
                titleLabel.setText("Recta");
                descLabel.setText(rectaLabel.getText());

                Media media = new Media(getClass().getResource("/resources/vid2.mp4").toExternalForm());

                MediaPlayer mediaPlayer = new MediaPlayer(media);

                videoViewer.setMediaPlayer(mediaPlayer);

                mediaPlayer.play();
           } else if (newValue.equals(arcButton)) {
                titleLabel.setText("Arco");
                descLabel.setText(arcLabel.getText());

                Media media = new Media(getClass().getResource("/resources/vid2.mp4").toExternalForm());

                MediaPlayer mediaPlayer = new MediaPlayer(media);

                videoViewer.setMediaPlayer(mediaPlayer);

                mediaPlayer.play();
           } else if (newValue.equals(positionButton)) {
                titleLabel.setText("Coordenadas");
                descLabel.setText(posLabel.getText());

                Media media = new Media(getClass().getResource("/resources/vid2.mp4").toExternalForm());

                MediaPlayer mediaPlayer = new MediaPlayer(media);

                videoViewer.setMediaPlayer(mediaPlayer);
           } else if (newValue.equals(transButton)) {
                titleLabel.setText("Transportador");
                descLabel.setText(protractorLabel.getText());

                Media media = new Media(getClass().getResource("/resources/vid2.mp4").toExternalForm());

                MediaPlayer mediaPlayer = new MediaPlayer(media);

                videoViewer.setMediaPlayer(mediaPlayer);
           } else if (newValue.equals(reglaButton)) {
                titleLabel.setText("Regla");
                descLabel.setText(reglaLabel.getText());

                Media media = new Media(getClass().getResource("/resources/vid2.mp4").toExternalForm());

                MediaPlayer mediaPlayer = new MediaPlayer(media);

                videoViewer.setMediaPlayer(mediaPlayer);
           } else if (newValue.equals(estiloButton)) {
                titleLabel.setText("Estilo");
                descLabel.setText(estiloLabel.getText());

                Media media = new Media(getClass().getResource("/resources/vid2.mp4").toExternalForm());

                MediaPlayer mediaPlayer = new MediaPlayer(media);

                videoViewer.setMediaPlayer(mediaPlayer);
           } else if (newValue.equals(atajosButton)) {
                titleLabel.setText("Atajos");
                descLabel.setText(atajosLabel.getText());

                Media media = new Media(getClass().getResource("/resources/vid2.mp4").toExternalForm());

                MediaPlayer mediaPlayer = new MediaPlayer(media);

                videoViewer.setMediaPlayer(mediaPlayer);
           }
        });
    }    

    @FXML
    private void closeWindow(ActionEvent event) {
        closeButton.getScene().getWindow().hide();
    }
    
}
