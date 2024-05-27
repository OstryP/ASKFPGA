package cz.vut.fekt.askfpga;

import cz.vut.fekt.askfpga.controllers.MonitorovaniController;

import java.util.TimerTask;

public class AutoWrite extends TimerTask {

    public void setCurrentData(){
        if (AppState.getInstance().getConnected()){
            long currentTime = System.currentTimeMillis();
            long durationInMillis = currentTime - AppState.getInstance().getStartTime();
            int durationInSeconds = (int) (durationInMillis / 1000);

            AppState.getInstance().setSeriesTemperature(durationInSeconds);
            AppState.getInstance().setSeriesTrafficTX0(durationInSeconds);
        }
    }

    public void run() {
        if(AppState.getInstance().getMonitorovani()){
            setCurrentData();
            MonitorovaniController.getInstance().updateData();
        }
    }

}
