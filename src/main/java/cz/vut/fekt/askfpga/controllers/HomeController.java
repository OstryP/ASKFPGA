package cz.vut.fekt.askfpga.controllers;

import com.sun.jna.Pointer;
import cz.vut.fekt.askfpga.AppState;
import cz.vut.fekt.askfpga.AskfpgaApp;
import cz.vut.fekt.askfpga.WrapperJNA;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Logika ovládání Hlavní stránky
 */
public class HomeController {

    //Přiřazení proměnných
    String zarizeni;

    //Napojení na FXML prvky
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

    /**
     * Proběhne při načtení home-view.fxml, získá informace o stavu aplikace a provede příslušné změny vzhledu/funkcionality stránky
     */
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


    /**
     * Přechod na stránku Konfigurace po kliknutí na tlačítko Konfigurace
     */
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

    /**
     * Přechod na stránku Monitorování po kliknutí na tlačítko Monitorování
     */
    @FXML
    protected void onMonitorovaniButtonClick (){
        AppState.getInstance().setCurrentTime();
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

    /**
     * Přechod na stránku Řízení po kliknutí na tlačítko Řízení
     */
    @FXML
    protected void onRizeniButtonClick (){
        AppState.getInstance().setCurrentTime();
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

    /**
     * Připojení k FPGA kartě
     */
    @FXML
    protected void onConnectButtonClick (){

        //Kontrola, jestli je aplikace připojena ke kartě, pokud ano, dojde k uzavření otevřených modulů, odpojení a změně stavu v AppState
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

        //Pokud aplikace není připojená ke kartě, pokusí se k ní připojit
        else {
            zarizeni = connectTextField.getText();

            //Kontrola, jestli vstup obsahuje pouze celé nezáporné číslo, jinak zobrazení varování
            if (zarizeni.matches("\\d+")){
                AppState.getInstance().setZarizeni(zarizeni);
                AppState.getInstance().setDevPointer(WrapperJNA.wrappernfb.nfb_open(zarizeni));
            }
            else {
                new Alert(Alert.AlertType.WARNING, "Vstup musí být celé nezáporné číslo").showAndWait();
            }

            //Kontrola, jestli se podařilo připojit k zařízení
            if (AppState.getInstance().getDevPointer() == null){
                new Alert(Alert.AlertType.ERROR, "Nepodařilo se připojik k zařízení").showAndWait();
            }

            else {
                AppState.getInstance().setConnected(true);
                AppState.getInstance().setStartTime();
                AppState.getInstance().setDeviceInfo();
                infoTextArea.setText(AppState.getInstance().getDeviceInfo());
                connectButton.setText("Disconnect");
            }
        }
    }
}