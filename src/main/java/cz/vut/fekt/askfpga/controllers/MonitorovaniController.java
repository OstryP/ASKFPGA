package cz.vut.fekt.askfpga.controllers;

import cz.vut.fekt.askfpga.AppState;
import cz.vut.fekt.askfpga.AskfpgaApp;
import cz.vut.fekt.askfpga.WrapperJNA;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;

public class MonitorovaniController {

    @FXML
    private Button backButton;

    @FXML
    private TextArea infoTextArea;

    @FXML
    private TextArea podrobneinfoTextArea;

    @FXML
    private LineChart<Number, Number> grafLineChart;



    public void initialize() {
        System.out.println("Inicializace");

        ((NumberAxis) grafLineChart.getXAxis()).setLabel("Čas v minutách");
        ((NumberAxis) grafLineChart.getYAxis()).setLabel("Teplota ve °C");

        grafLineChart.getData().add(AppState.getInstance().getSeries());

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
}
