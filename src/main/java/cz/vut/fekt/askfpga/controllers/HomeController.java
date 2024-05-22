package cz.vut.fekt.askfpga.controllers;

import com.sun.jna.Pointer;
import cz.vut.fekt.askfpga.AppState;
import cz.vut.fekt.askfpga.AskfpgaApp;
import cz.vut.fekt.askfpga.WrapperJNA;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;


public class HomeController {

    Pointer devPointer;

    String zarizeni;



    @FXML
    private Button konfiguraceButton;
    @FXML
    private TextField connectTextField;

    @FXML
    private Button connectButton;

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

    public void initialize() {
        zarizeni = AppState.getInstance().getZarizeni();
        System.out.println(zarizeni);
        if(zarizeni!=null){
            System.out.println(zarizeni+"notnull");
            connectTextField.setText(zarizeni);
        }
    }


    @FXML
    protected void onKonfiguraceButtonClick () {
        AppState.getInstance().setCurrentTime();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(AskfpgaApp.class.getResource("konfigurace-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) konfiguraceButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    protected void onMonitorovaniButtonClick (){
        AppState.getInstance().setCurrentTime();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(AskfpgaApp.class.getResource("monitorovani-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) konfiguraceButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onRizeniButtonClick (){
        AppState.getInstance().setCurrentTime();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(AskfpgaApp.class.getResource("rizeni-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) konfiguraceButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onConnectButtonClick (){
        zarizeni = connectTextField.getText();
        System.out.println(zarizeni);
        AppState.getInstance().setZarizeni(zarizeni);

        devPointer = WrapperJNA.wrappernfb.nfb_open(zarizeni);
        System.out.println(devPointer);
        infoTextArea.setText("INFORMACE O ZARIZENI");

        WrapperJNA.wrappernfb.getProp(devPointer);




    }

    /*

public class PointerHexToText {
        Pointer ptr = MyCLibrary.INSTANCE.getPointer();

        String readableText = ptr.getString(0);

        System.out.println("Readable text from pointer: " + readableText);

        byte[] byteArray = ptr.getByteArray(0, readableText.length());
        StringBuilder hexString = new StringBuilder();
        for (byte b : byteArray) {
            hexString.append(String.format("%02X ", b));
        }
        System.out.println("Hex value: " + hexString.toString().trim());
    }
}
*/




    @FXML
    protected void onConnectMenuItemClick(){
        devPointer = WrapperJNA.wrappernfb.nfb_open("0");
        System.out.println(devPointer);
        infoTextArea.setText("INFORMACE O ZARIZENI");

    }

    @FXML
    protected void onDisconnectMenuItemClick(){
        WrapperJNA.wrappernfb.nfb_close(devPointer);
        infoTextArea.setText("ZARIZENI BYLO ODPOJENO");
    }


}