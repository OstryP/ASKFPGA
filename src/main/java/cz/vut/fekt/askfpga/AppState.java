package cz.vut.fekt.askfpga;

import com.sun.jna.Pointer;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;
import java.util.Timer;

public class AppState {
    private static AppState instance;

    private String zarizeni;

    private long startTime;

    private XYChart.Series<Number, Number> seriesTemperature;

    private XYChart.Series<Number, Number> seriesTrafficTX0;

    private boolean connected;

    private Pointer devPointer;

    private ArrayList<Pointer> openedComponents;

    private static final WrapperJNA.Paths[] infoPaths = {WrapperJNA.Paths.BOARD_NAME, WrapperJNA.Paths.BUILD_AUTHOR, WrapperJNA.Paths.CARD_NAME, WrapperJNA.Paths.PROJECT_NAME};

    private String deviceInfo;

    private Pointer oRx_que;
    private Pointer oTx_que;

    private Timer timer;

    private boolean isMonitorovani;

    private Long trafficTXCount;



    private AppState() {
        seriesTemperature = new XYChart.Series<>();
        timer = new Timer();

        seriesTemperature.setName("Historie teploty");

        seriesTrafficTX0 = new XYChart.Series<>();
        seriesTrafficTX0.setName("Traffic TX 0");

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

    public void clearSeriesTemperature(){
        seriesTemperature.getData().clear();
    }

    public void clearSeriesTrafficTX0(){
        seriesTrafficTX0.getData().clear();
    }

    public void setStartTime(){
        startTime = System.currentTimeMillis();
    }

    public long getStartTime(){
        return startTime;
    }

    public void startMonitoring(){
        timer.schedule(new AutoWrite(), 0, 1000);
    }

    public void stopMonitoring(){
        timer.cancel();
    }

    public void setMonitorovani(boolean state){
        this.isMonitorovani = state;
    }

    public boolean getMonitorovani(){
        return isMonitorovani;
    }

    public XYChart.Series<Number, Number> getSeriesTemperature(){
        return seriesTemperature;
    }

    public void setSeriesTemperature(int durationInSeconds){
        if (seriesTemperature.getData().size() > 100) {
            seriesTemperature.getData().removeFirst();
        }
        seriesTemperature.getData().add(new XYChart.Data<>(durationInSeconds, WrapperJNA.wrapperfpga.get_temperature(devPointer)));
    }

    public XYChart.Series<Number, Number> getSeriesTrafficTX0(){
        return seriesTrafficTX0;
    }

    public void setSeriesTrafficTX0(int durationInSeconds){
        long trafficSecTX = WrapperJNA.wrappernfb.trafficSecTX();

        if (seriesTrafficTX0.getData().size() > 100) {
            seriesTrafficTX0.getData().removeFirst();
        }
        if (!seriesTrafficTX0.getData().isEmpty()){
            seriesTrafficTX0.getData().add(new XYChart.Data<>(durationInSeconds, trafficSecTX-trafficTXCount));
            trafficTXCount = trafficSecTX;
        }
        else{
            trafficTXCount = trafficSecTX;
            seriesTrafficTX0.getData().add(new XYChart.Data<>(durationInSeconds, 0L));
        }
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
