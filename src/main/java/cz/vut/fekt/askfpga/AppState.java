package cz.vut.fekt.askfpga;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;

public class AppState {
    private static AppState instance;

    private String zarizeni;

    private long startTime;

    private XYChart.Series<Number, Number> series;

    private boolean connected;

    private Pointer devPointer;

    private ArrayList<Pointer> openedComponents;

    private static final WrapperJNA.Paths[] infoPaths = {WrapperJNA.Paths.BOARD_NAME, WrapperJNA.Paths.BUILD_AUTHOR};

    private String deviceInfo;

    private Pointer oRx_que;
    private Pointer oTx_que;



    private AppState() {
        series = new XYChart.Series<>();
        series.setName("Historie teploty");
        connected = false;
        openedComponents = new ArrayList<>();
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

            series.getData().add(new XYChart.Data<>(durationInMinutes, WrapperJNA.wrapperfpga.nc_adc_sensors_get_temp(devPointer)));
        }
    }

    public XYChart.Series<Number, Number> getSeries(){
        return series;
    }

    public ArrayList<Pointer> getOpenedComponents() {
        return openedComponents;
    }

    public void setOpenedComponents(Pointer component) {
        openedComponents.add(component);
    }

    public void clearOpenedComponents(){
        if (openedComponents != null) {
            openedComponents.clear();
        }
    }

    public Pointer getoRx_que() {
        return oRx_que;
    }

    public void setoRx_que(Pointer oRx_que) {
        this.oRx_que = oRx_que;
    }

    public Pointer getoTx_que() {
        return oTx_que;
    }

    public void setoTx_que(Pointer oTx_que) {
        this.oTx_que = oTx_que;
    }


    public void setDeviceInfo(){
        StringBuilder info = new StringBuilder();
        for (WrapperJNA.Paths prop : infoPaths) {
            String value = WrapperJNA.wrappernfb.getProp(AppState.getInstance().getDevPointer(), prop);
            info.append(prop.name()).append(": ").append(value).append("\n");
        }
        deviceInfo = info.toString();
    }

    public String getDeviceInfo(){
        return deviceInfo;
    }

    public void clearDeviceInfo(){
        deviceInfo ="Žádné připojené zařízení";
    }


}
