package cz.vut.fekt.askfpga.controllers;

import com.sun.jna.Pointer;
import cz.vut.fekt.askfpga.NfbDevice;
import cz.vut.fekt.askfpga.WrapperJNA;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
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
    private TextArea infoTextArea;

    @FXML
    private MenuItem connectMenuItem;

    @FXML
    private MenuItem disconnectMenuItem;

    @FXML
    protected void onKonfiguraceButtonClick (){
        infoTextArea.setText("konfigurace");
    }

    @FXML
    protected void onMonitorovaniButtonClick (){
        infoTextArea.setText("monitorovani");
    }

    @FXML
    protected void onRizeniButtonClick (){
        infoTextArea.setText("rizeni");
    }

    @FXML
    protected void onConnectMenuItemClick(){
        devPointer = WrapperJNA.wrapper.nfb_open("0");
        System.out.println(devPointer);
        NfbDevice nfbDevice = new NfbDevice(devPointer);
        infoTextArea.setText("INFORMACE O ZARIZENI");
    }

    @FXML
    protected void onDisconnectMenuItemClick(){
        WrapperJNA.wrapper.nfb_close(devPointer);
        infoTextArea.setText("ZARIZENI BYLO ODPOJENO");
    }


}