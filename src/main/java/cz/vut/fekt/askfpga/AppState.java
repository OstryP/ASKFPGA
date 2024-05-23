package cz.vut.fekt.askfpga;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import javafx.scene.chart.XYChart;

public class AppState {
    private static AppState instance;

    private String zarizeni;

    private long startTime;

    private XYChart.Series<Number, Number> series;

    private boolean connected;

    Pointer devPointer;

    private AppState() {
        series = new XYChart.Series<>();
        series.setName("Historie teploty");
        connected = false;
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

    public boolean getConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public Pointer getDevPointer() {
        return devPointer;
    }

    public void setDevPointer(Pointer devPointer) {
        this.devPointer = devPointer;
    }

    public void clearSeries(){
        series.getData().clear();
    }

    public void setStartTime(){
        startTime = System.currentTimeMillis();
    }

    public void setCurrentTime(){
        if (AppState.getInstance().getConnected()){
            IntByReference val = new IntByReference(0);
            long currentTime = System.currentTimeMillis();
            long durationInMillis = currentTime - startTime;
            int durationInMinutes = (int) (durationInMillis / 1000);

            series.getData().add(new XYChart.Data<>(durationInMinutes, WrapperJNA.wrappernfb.nc_adc_sensors_get_temp(devPointer, val)));
        }
    }

    public XYChart.Series<Number, Number> getSeries(){
        return series;
    }

}
