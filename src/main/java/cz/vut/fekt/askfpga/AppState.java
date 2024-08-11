package cz.vut.fekt.askfpga;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;
import java.util.Timer;


/**
 * Slouží k ukládání a sdílení stavu aplikace napříč jednotlivými okny, třídami
 */
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


    /**
     * Proběhne při prvotním vytvoření AppState
     */
    private AppState() {
        seriesTemperature = new XYChart.Series<>();
        //timer = new Timer();

        seriesTemperature.setName("Historie teploty");

        seriesTrafficTX0 = new XYChart.Series<>();
        seriesTrafficTX0.setName("Traffic TX 0");

        connected = false;
        openedComponents = new ArrayList<>();
    }

    /**
     * Vrátí instanci třídy AppState, pokud žádná není, vytvoří novou
     * @return Vrací instanci AppState
     */
    public static AppState getInstance() {
        if (instance == null) {
            instance = new AppState();
        }
        return instance;
    }

    /**
     * Vytvoří nový zápis v sérii teplot, zaznamená čas (kolik uplynulo od začátku měření) a teplotu
     */
    public void setCurrentTime(){
        if(AppState.getInstance().getConnected()){
            long currentTime = System.currentTimeMillis();
            long durationInMilis = currentTime - startTime;
            int durationInSeconds = (int) (durationInMilis / 1000);

            seriesTemperature.getData().add(new XYChart.Data<>(durationInSeconds, WrapperJNA.wrapperfpga.get_temperature(devPointer)));
        }
    }


    /**
     * Vrací číslo připojeného zařízení v String formátu
     * @return String připojeného zařízení
     */
    public String getZarizeni() {
        return zarizeni;
    }

    /**
     * Zaznamenání čísla připojeného zařízení ve formátu String
     * @param zarizeni String připojeného zařízení
     */
    public void setZarizeni(String zarizeni) {
        this.zarizeni = zarizeni;
    }

    /**
     * Získání boolean hodnoty jestli je aplikace připojena k zařízení
     * @return Boolean je aplikace připojena k zařízení
     */
    public boolean getConnected() {
        return connected;
    }

    /**
     * Zaznamenání boolean hodnoty jestli je aplikace připojena k zařízení
     * @param connected Boolean je aplikace připojena k zařízení
     */
    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    /**
     * Získání pointeru na připojené zařízení
     * @return pointer zařízení
     */
    public Pointer getDevPointer() {
        return devPointer;
    }

    /**
     * Nastavení pointeru na připojené zařízení
     * @param devPointer pointer zařízení
     */
    public void setDevPointer(Pointer devPointer) {
        this.devPointer = devPointer;
    }

    /**
     * Resetování série teplot
     */
    public void clearSeriesTemperature(){
        seriesTemperature.getData().clear();
    }

    /**
     * Resetování série přenosu
     */
    public void clearSeriesTrafficTX0(){
        seriesTrafficTX0.getData().clear();
    }

    /**
     * Nastavení počátečního času měření
     */
    public void setStartTime(){
        startTime = System.currentTimeMillis();
    }

    /**
     * Získání počátečního času měření
     * @return počáteční čas
     */
    public long getStartTime(){
        return startTime;
    }

    /**
     * Spuštění automatického monitorování
     */
    public void startMonitoring(){
        timer.schedule(new AutoWrite(), 0, 1000);
    }

    /**
     * Ukončení automatického monitorování
     */
    public void stopMonitoring(){
        timer.cancel();
    }

    /**
     * Nastavení jestli je monitorování aktivní
     * @param state Aktivní monitorování
     */
    public void setMonitorovani(boolean state){
        this.isMonitorovani = state;
    }

    /**
     * Získání jestli je monitorování aktivní
     * @return Aktivní monitorování
     */
    public boolean getMonitorovani(){
        return isMonitorovani;
    }

    /**
     * Získání série teplot (teplota čas)
     * @return Série teplot
     */
    public XYChart.Series<Number, Number> getSeriesTemperature(){
        return seriesTemperature;
    }

    /**
     * Zápis do série teplot
     * @param durationInSeconds Čas v sekundách
     */
    public void setSeriesTemperature(int durationInSeconds){
        if (seriesTemperature.getData().size() > 100) {
            seriesTemperature.getData().removeFirst();
        }
        seriesTemperature.getData().add(new XYChart.Data<>(durationInSeconds, WrapperJNA.wrapperfpga.get_temperature(devPointer)));
    }

    /**
     * Získání série přenesených dat
     * @return Série přenesených dat
     */
    public XYChart.Series<Number, Number> getSeriesTrafficTX0(){
        return seriesTrafficTX0;
    }

    /**
     * Zápis do série přenesených dat
     * @param durationInSeconds Čas v sekundách
     */
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

    /**
     * Získání pole otevřených komponent
     * @return Pole otevřených komponent
     */
    public ArrayList<Pointer> getOpenedComponents() {
        return openedComponents;
    }

    /**
     * Zápis do pole otevřených komponent
     * @param component Ukazatel na komponent
     */
    public void setOpenedComponents(Pointer component) {
        openedComponents.add(component);
    }

    /**
     * Vyčištění pole otevřených komponent
     */
    public void clearOpenedComponents(){
        if (openedComponents != null) {
            openedComponents.clear();
        }
    }

    /**
     * Vrácení pointeru na RX_que
     * @return pointer na RX_que
     */
    public Pointer getoRx_que() {
        return oRx_que;
    }

    /**
     * Nastavení pointeru na RX_que
     * @param oRx_que Pointer na RX_que
     */
    public void setoRx_que(Pointer oRx_que) {
        this.oRx_que = oRx_que;
    }

    /**
     * Získání pointeru na TX_que
     * @return Pointer na TX_que
     */
    public Pointer getoTx_que() {
        return oTx_que;
    }

    /**
     * Nastavení pointeru na TX_que
     * @param oTx_que Pointer na TX_que
     */
    public void setoTx_que(Pointer oTx_que) {
        this.oTx_que = oTx_que;
    }

    /**
     * Nastavení informací o zařízení
     */
    public void setDeviceInfo(){
        StringBuilder info = new StringBuilder();
        for (WrapperJNA.Paths prop : infoPaths) {
            String value = WrapperJNA.wrappernfb.getProp(AppState.getInstance().getDevPointer(), prop);
            info.append(prop.name()).append(": ").append(value).append("\n");
        }
        deviceInfo = info.toString();
    }

    /**
     * Získání informací o zařízení
     * @return Informace o zařízení
     */
    public String getDeviceInfo(){
        return deviceInfo;
    }

    /**
     * Vyčištění informací o zařízení
     */
    public void clearDeviceInfo(){
        deviceInfo ="Žádné připojené zařízení";
    }


}
