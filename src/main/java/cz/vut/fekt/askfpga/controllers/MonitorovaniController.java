package cz.vut.fekt.askfpga.controllers;

import cz.vut.fekt.askfpga.AppState;
import cz.vut.fekt.askfpga.AskfpgaApp;
import cz.vut.fekt.askfpga.WrapperJNA;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Logika ovládání stránky Monitorování
 */
public class MonitorovaniController {

    //Napojení na FXML prvky
    @FXML
    private Button backButton;

    @FXML
    private TextArea infoTextArea;

    @FXML
    private TextArea podrobneinfoTextArea;

    @FXML
    private LineChart<Number, Number> grafLineChart;

    @FXML
    private LineChart<Number, Number> prenosLineChart;

    private static MonitorovaniController instance;

    public static MonitorovaniController getInstance() {
        if (instance == null) {
            instance = new MonitorovaniController();
        }
        return instance;
    }


    /**
     * Proběhne při načtení monitorovani-view.fxml, získá informace o stavu aplikace a provede příslušné změny vzhledu/funkcionality stránky
     */
    public void initialize() {

        //Nastavení grafů
        grafLineChart.getXAxis().setLabel("Čas v sekundách");
        grafLineChart.getYAxis().setLabel("Teplota ve °C");

        prenosLineChart.getXAxis().setLabel("Čas v sekundách");
        prenosLineChart.getYAxis().setLabel("Počet přenesených paketů za sekundu");

        if(AppState.getInstance().getConnected()){
            infoTextArea.setText(AppState.getInstance().getDeviceInfo());
            podrobneinfoTextArea.setText(WrapperJNA.wrappernfb.trafficTX());
        }
        grafLineChart.getData().add(AppState.getInstance().getSeriesTemperature());

        //AppState.getInstance().setMonitorovani(true);
        //AppState.getInstance().startMonitoring();
    }

    /**
     * Slouží k aktualizaci dat v grafech a v textové oblasti
     */
    public void updateData() {
        if(AppState.getInstance().getConnected()){
            if (AppState.getInstance()!=null){
                grafLineChart.getData().add(AppState.getInstance().getSeriesTemperature());
                prenosLineChart.getData().add(AppState.getInstance().getSeriesTrafficTX0());
                podrobneinfoTextArea.setText(WrapperJNA.wrappernfb.trafficTX());
            }
        }
    }

    /**
     * Návrat na Hlavní stránku
     */
    @FXML
    protected void onBackButtonClick () {
        //AppState.getInstance().setMonitorovani(false);
        //AppState.getInstance().stopMonitoring();
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
}
