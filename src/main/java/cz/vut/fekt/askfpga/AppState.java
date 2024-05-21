package cz.vut.fekt.askfpga;

import javafx.scene.chart.XYChart;

public class AppState {
    private static AppState instance;

    private String zarizeni;

    private long startTime;

    private XYChart.Series<Number, Number> series;

    private AppState() {
        series = new XYChart.Series<>();
        series.setName("Historie teploty");
    }

    public static AppState getInstance() {
        if (instance == null) {
            instance = new AppState();
        }
        return instance;
    }

    public String getZarizeni() {
        return zarizeni;
    }

    public void setZarizeni(String zarizeni) {
        this.zarizeni = zarizeni;
    }

    public void setStartTime(){
        startTime = System.currentTimeMillis();
    }

    public void setCurrentTime(){
        long currentTime = System.currentTimeMillis();
        long durationInMillis = currentTime - startTime;
        int durationInMinutes = (int) (durationInMillis / 1000);

        series.getData().add(new XYChart.Data<>(durationInMinutes, 23));
    }

    public XYChart.Series<Number, Number> getSeries(){
        return series;
    }

}
