package cz.vut.fekt.askfpga.controllers;

import com.sun.jna.Pointer;
import cz.vut.fekt.askfpga.NfbDevice;
import cz.vut.fekt.askfpga.WrapperJNA;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.text.Text;

public class HomeController {

    Pointer devPointer;

    @FXML
    private Button konfiguraceButton;

    @FXML
    private Button monitorovaniButton;

    @FXML
    private Button rizeniButton;

    @FXML
    private Text infoText;

    @FXML
    private MenuItem connectMenuItem;

    @FXML
    private MenuItem disconnectMenuItem;

    @FXML
    protected void onKonfiguraceButtonClick (){
        infoText.setText("konfigurace");
    }

    @FXML
    protected void onMonitorovaniButtonClick (){
        infoText.setText("monitorovani");
    }

    @FXML
    protected void onRizeniButtonClick (){
        infoText.setText("rizeni");
    }

    @FXML
    protected void onConnectMenuItem (){
        devPointer = WrapperJNA.wrapper.nfb_open("0");
        System.out.println(devPointer);
        NfbDevice nfbDevice = new NfbDevice(devPointer);
    }

    @FXML
    protected void onDisconnectMenuItem (){
        WrapperJNA.wrapper.nfb_close(devPointer);
    }


}
