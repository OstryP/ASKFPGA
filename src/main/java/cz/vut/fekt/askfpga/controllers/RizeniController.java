package cz.vut.fekt.askfpga.controllers;

import cz.vut.fekt.askfpga.AppState;
import cz.vut.fekt.askfpga.AskfpgaApp;
import cz.vut.fekt.askfpga.WrapperJNA;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Logika ovládání stránky Řízení
 */
public class RizeniController {

    //Napojení na FXML prvky
    @FXML
    private Button backButton;

    @FXML
    private TextArea infoTextArea;

    @FXML
    private ComboBox<String> numComboBox;

    @FXML
    private ListView<String> listView;

    @FXML TextArea transTextArea;

    /**
     * Proběhne při načtení rizeni-view.fxml, získá informace o stavu aplikace a provede příslušné změny vzhledu/funkcionality stránky
     */
    public void initialize() {
        if(AppState.getInstance().getConnected()){
            infoTextArea.setText(AppState.getInstance().getDeviceInfo());
            transTextArea.setText(WrapperJNA.wrappernfb.trafficTX());
        }

        numComboBox.getItems().add("0");
        numComboBox.getItems().add("1");

        listView.getItems().clear();
        ListFilesInDirectory();
    }

    /**
     * Návrat na Hlavní stránku
     */
    @FXML
    protected void onBackButtonClick () {
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

    /**
     * Zahájení přenosu dat
     * @throws IOException
     */
    @FXML
    protected void onPoslatButtonClick () throws IOException {
        //Kontrola jestli je zvolen datový soubor
        if(listView.getSelectionModel().getSelectedItem()==null){
            new Alert(Alert.AlertType.ERROR, "Je potřeba vybrat soubor s daty").showAndWait();
        }
        else{
            //Kontrola jestli je zvoleno rozhraní
            if(numComboBox.getSelectionModel().getSelectedItem()==null){
                new Alert(Alert.AlertType.ERROR, "Je potřeba zvolit rozhraní").showAndWait();
            }
            else {
                WrapperJNA.wrappernfb.importData(listView.getSelectionModel().getSelectedItem(), Integer.parseInt(numComboBox.getSelectionModel().getSelectedItem()));
            }
        }
    }


    /**
     * Výpis souborů ve složce
     */
    void ListFilesInDirectory() {
        String currentDirectory = System.getProperty("user.dir");

        String relativePath = "Data";
        Path directoryPath = Paths.get(currentDirectory, relativePath).normalize();
        try {
            Files.list(directoryPath)
                    .filter(Files::isRegularFile)
                    .forEach(file -> listView.getItems().add(file.getFileName().toString()));
        } catch (IOException e) {
            System.err.println("Error accessing directory: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Obnovení výpisu souborů ze složky
     */
    @FXML
    protected void onObnovitButtonClick () {
        listView.getItems().clear();
        ListFilesInDirectory();
    }
}
