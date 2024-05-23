package cz.vut.fekt.askfpga.controllers;

import cz.vut.fekt.askfpga.AppState;
import cz.vut.fekt.askfpga.AskfpgaApp;
import cz.vut.fekt.askfpga.WrapperJNA;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;

public class RizeniController {

    @FXML
    private Button backButton;

    @FXML
    private Button poslatButton;

    @FXML
    private TextArea infoTextArea;

    public void initialize() {
        if(AppState.getInstance().getConnected()){
            StringBuilder info = new StringBuilder();
            for (WrapperJNA.Paths prop : WrapperJNA.Paths.values()) {
                String value = WrapperJNA.wrappernfb.getProp(AppState.getInstance().getDevPointer(), prop);
                info.append(prop.name()).append(": ").append(value).append("\n");
            }
            infoTextArea.setText(info.toString());
        }
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
    protected void onPoslatButtonClick () {
        //https://cesnet.github.io/ndk-sw/libnfb-example.html#ndp-data-transmit-example
        //zobrait data, povolit jejich upravu, nezavirat dokud nevypnu celou applikaci

    }


}
