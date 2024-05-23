package cz.vut.fekt.askfpga.controllers;

import cz.vut.fekt.askfpga.AppState;
import cz.vut.fekt.askfpga.AskfpgaApp;
import cz.vut.fekt.askfpga.WrapperJNA;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;

public class RizeniController {

    @FXML
    private Button backButton;

    @FXML
    private TextArea infoTextArea;

    @FXML
    private ComboBox<String> rozhraniComboBox;

    @FXML
    private ComboBox<String> numComboBox;

    public void initialize() {
        if(AppState.getInstance().getConnected()){
            infoTextArea.setText(AppState.getInstance().getDeviceInfo());
        }
        rozhraniComboBox.getItems().add("rxq");
        rozhraniComboBox.getItems().add("txq");

        numComboBox.getItems().add("0");
        numComboBox.getItems().add("1");
    }

    @FXML
    protected void onBackButtonClick () {
        AppState.getInstance().setCurrentTime();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(AskfpgaApp.class.getResource("home-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onPoslatButtonClick () throws InterruptedException {
        WrapperJNA.wrappernfb.sendData(rozhraniComboBox.getSelectionModel().getSelectedItem(), Integer.parseInt(numComboBox.getSelectionModel().getSelectedItem()));

        //https://cesnet.github.io/ndk-sw/libnfb-example.html#ndp-data-transmit-example
        //zobrait data, povolit jejich upravu, nezavirat dokud nevypnu celou applikaci

    }


}
