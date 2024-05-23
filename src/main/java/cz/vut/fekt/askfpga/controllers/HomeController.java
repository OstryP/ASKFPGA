package cz.vut.fekt.askfpga.controllers;

import com.sun.jna.Pointer;
import cz.vut.fekt.askfpga.AppState;
import cz.vut.fekt.askfpga.AskfpgaApp;
import cz.vut.fekt.askfpga.WrapperJNA;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;


public class HomeController {

    Pointer devPointer;

    String zarizeni;

    @FXML
    private Button konfiguraceButton;
    @FXML
    private TextField connectTextField;

    @FXML
    private Button connectButton;

    @FXML
    private Button monitorovaniButton;

    @FXML
    private Button rizeniButton;

    @FXML
    private TextArea infoTextArea;

    public void initialize() {
        zarizeni = AppState.getInstance().getZarizeni();

        if(zarizeni!=null){
            connectTextField.setText(zarizeni);
        }

        if(AppState.getInstance().getConnected()){
            connectButton.setText("Disconnect");

            StringBuilder info = new StringBuilder();
            for (WrapperJNA.Paths prop : WrapperJNA.Paths.values()) {
                String value = WrapperJNA.wrappernfb.getProp(devPointer, prop);
                info.append(prop.name()).append(": ").append(value).append("\n");
            }
            infoTextArea.setText(info.toString());
        }
    }


    @FXML
    protected void onKonfiguraceButtonClick () {
        AppState.getInstance().setCurrentTime();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(AskfpgaApp.class.getResource("konfigurace-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) konfiguraceButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    protected void onMonitorovaniButtonClick (){
        AppState.getInstance().setCurrentTime();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(AskfpgaApp.class.getResource("monitorovani-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) konfiguraceButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onRizeniButtonClick (){
        AppState.getInstance().setCurrentTime();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(AskfpgaApp.class.getResource("rizeni-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) konfiguraceButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onConnectButtonClick (){

        if (AppState.getInstance().getConnected()){
            WrapperJNA.wrappernfb.nfb_close(devPointer);
            AppState.getInstance().setConnected(false);
            AppState.getInstance().clearSeries();
            infoTextArea.setText("Zařízení bylo odpojeno");
        }

        else {
            zarizeni = connectTextField.getText();

            AppState.getInstance().setZarizeni(zarizeni);

            devPointer = WrapperJNA.wrappernfb.nfb_open(zarizeni);

            if (devPointer == null){
                infoTextArea.setText("Nepodařilo se připojik k zařízení");
            }
            else {
                AppState.getInstance().setConnected(true);
                AppState.getInstance().setDevPointer(devPointer);
                StringBuilder info = new StringBuilder();
                for (WrapperJNA.Paths prop : WrapperJNA.Paths.values()) {
                    String value = WrapperJNA.wrappernfb.getProp(devPointer, prop);
                    info.append(prop.name()).append(": ").append(value).append("\n");
                }
                infoTextArea.setText(info.toString());

                //WrapperJNA.wrappernfb.print_component_list(devPointer);
            }
        }
    }
}