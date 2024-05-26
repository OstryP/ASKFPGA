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
            infoTextArea.setText(AppState.getInstance().getDeviceInfo());
        }
    }


    @FXML
    protected void onKonfiguraceButtonClick () {
        //AppState.getInstance().setCurrentTime();
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
        //AppState.getInstance().setCurrentTime();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(AskfpgaApp.class.getResource("monitorovani-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) monitorovaniButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onRizeniButtonClick (){
        //AppState.getInstance().setCurrentTime();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(AskfpgaApp.class.getResource("rizeni-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) rizeniButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onConnectButtonClick (){

        if (AppState.getInstance().getConnected()){

            if(AppState.getInstance().getoRx_que()!=null){
                WrapperJNA.wrappernfb.ndp_close_rx_queue(AppState.getInstance().getoRx_que());
                AppState.getInstance().setoRx_que(null);
            }

            if(AppState.getInstance().getoTx_que()!=null){
                WrapperJNA.wrappernfb.ndp_close_tx_queue(AppState.getInstance().getoTx_que());
                AppState.getInstance().setoTx_que(null);
            }

            if(AppState.getInstance().getOpenedComponents()!=null){
                for (Pointer comp : AppState.getInstance().getOpenedComponents()) {
                    WrapperJNA.wrappernfb.nfb_comp_close(comp);
                }
                AppState.getInstance().clearOpenedComponents();
            }

            AppState.getInstance().setConnected(false);
            AppState.getInstance().clearSeriesTemperature();
            AppState.getInstance().clearSeriesTrafficTX0();
            AppState.getInstance().clearDeviceInfo();

            WrapperJNA.wrappernfb.nfb_close(AppState.getInstance().getDevPointer());
            AppState.getInstance().setDevPointer(null);
            infoTextArea.setText(AppState.getInstance().getDeviceInfo());
        }

        else {
            zarizeni = connectTextField.getText();

            AppState.getInstance().setZarizeni(zarizeni);

            AppState.getInstance().setDevPointer(WrapperJNA.wrappernfb.nfb_open(zarizeni));

            if (AppState.getInstance().getDevPointer() == null){
                infoTextArea.setText("Nepodařilo se připojik k zařízení");
            }

            else {
                AppState.getInstance().setConnected(true);
                AppState.getInstance().setStartTime();
                AppState.getInstance().setDeviceInfo();
                infoTextArea.setText(AppState.getInstance().getDeviceInfo());
                connectButton.setText("Disconnect");

                //WrapperJNA.wrappernfb.print_component_list(devPointer);
            }
        }
    }
}