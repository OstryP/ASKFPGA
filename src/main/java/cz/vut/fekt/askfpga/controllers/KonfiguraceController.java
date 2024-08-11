package cz.vut.fekt.askfpga.controllers;

import cz.vut.fekt.askfpga.AppState;
import cz.vut.fekt.askfpga.AskfpgaApp;
import cz.vut.fekt.askfpga.WrapperJNA;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.nio.file.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Logika ovládání stránky Konfigurace
 */
public class KonfiguraceController {

    //Přiřazení proměnných
    private ArrayList<WrapperJNA.myNode> components;

    //Napojení na FXML prvky
    @FXML
    private Button backButton;

    @FXML
    private TextField outputTextField;

    @FXML
    private ListView<String> listView;

    @FXML
    private TextArea infoTextArea;

    @FXML
    private ComboBox<String> comboBox;

    @FXML
    private TextField offsetTextField;

    @FXML
    private TextField valTextField;


    /**
     * Proběhne při načtení konfigurace-view.fxml, získá informace o stavu aplikace a provede příslušné změny vzhledu/funkcionality stránky
     */
    public void initialize() {
        listView.getItems().clear();
        ListFilesInDirectory();

        if(AppState.getInstance().getConnected()){
            infoTextArea.setText(AppState.getInstance().getDeviceInfo());

            components = WrapperJNA.wrappernfb.print_component_list(AppState.getInstance().getDevPointer());
            for(WrapperJNA.myNode component : components){

                comboBox.getItems().add(component.path);
            }
        }
    }


    /**
     * Návrat na hlavní stránku
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
     * Obnovení zobrazení složky s konfiguračními soubory
     */
    @FXML
    protected void onObnovitButtonClick () {
        listView.getItems().clear();
        ListFilesInDirectory();
    }

    /**
     * Zobrazení dat ze zvolené komponenty z připojené FPGA karty
     */
    @FXML
    protected void onZobrazitButtonClick () {
        //Kontrola jestli je aplikace připojena k FPGA kartě
        if(AppState.getInstance().getConnected()){
            String selectedItem = comboBox.getSelectionModel().getSelectedItem();
            //Kontrola, jestli byl vybrán komponent
            if(selectedItem==null){
                new Alert(Alert.AlertType.ERROR, "Je potřeba vybrat komponent z dropdown menu").showAndWait();
            }

            else{

                //Kontrola, jestli byl zadán offset a jestli se jedná o kladné celé nenulové číslo
                if(offsetTextField.getText().matches("\\d+")){
                    for(WrapperJNA.myNode component : components){
                        if (Objects.equals(component.path, selectedItem)){
                            int value = Integer.parseInt(offsetTextField.getText());
                            outputTextField.setText(String.valueOf(WrapperJNA.wrapperfpga.nfb_comp_read(component.offset, value)));
                            break;
                        }
                    }
                }

                else{
                    new Alert(Alert.AlertType.ERROR, "Offset musí obsahovat celé nenulové číslo").showAndWait();
                }
            }
        }

        else {
            new Alert(Alert.AlertType.WARNING, "Žádné připojené zařízení").showAndWait();
        }
    }

    /**
     * Zapsání dat na FPGA kartu, do zvolené komponenty, do zvoleného offsetu
     */
    @FXML
    protected void onZapsatButtonClick () {
        //Kontrola, jestli je aplikace připojena k FPGA kartě
        if(AppState.getInstance().getConnected()){
            String selectedItem = comboBox.getSelectionModel().getSelectedItem();
            //Kontrola, jestli byl vybrán komponent
            if(selectedItem==null){
                new Alert(Alert.AlertType.ERROR, "Je potřeba vybrat komponent z dropdown menu").showAndWait();
            }

            else{
                //Kontrola, jestli byl zadán offset a jestli se jedná o kladné celé nenulové číslo
                if(offsetTextField.getText().matches("\\d+")){
                    //Kontrola, jestli byla zadána hodnota zápisu a jestli se jedná o kladné celé nenulové číslo
                    if(valTextField.getText().matches("\\d+")){
                        for(WrapperJNA.myNode component : components){
                            if (Objects.equals(component.path, selectedItem)){
                                int offset = Integer.parseInt(offsetTextField.getText());
                                int data = Integer.parseInt(valTextField.getText());
                                WrapperJNA.wrapperfpga.nfb_comp_write(component.offset, offset, data);
                                outputTextField.setText(String.valueOf(WrapperJNA.wrapperfpga.nfb_comp_read(component.offset, offset)));
                                break;
                            }
                        }
                    }

                    else{
                        new Alert(Alert.AlertType.ERROR, "Hodnota zápisu musí obsahovat celé nenulové číslo").showAndWait();
                    }
                }

                else{
                    new Alert(Alert.AlertType.ERROR, "Offset musí obsahovat celé nenulové číslo").showAndWait();
                }
            }
        }
        else {
            new Alert(Alert.AlertType.WARNING, "Žádné připojené zařízení").showAndWait();
        }
    }

    /**
     * Importování nastavené karty z JSON konfiguračního souboru
     * @throws IOException
     */
    @FXML
    protected void onImportButtonClick () throws IOException {
        //Kontrola, jestli je aplikace připojena k FPGA kartě
        if(AppState.getInstance().getConnected()){
            //Kontrola, jestli je vybrán konfigurační soubor
            if(listView.getSelectionModel().getSelectedItem()==null){
                new Alert(Alert.AlertType.ERROR, "Je potřeba vybrat konfigurační soubor").showAndWait();
            }
            else {
                //Načtení dat z JSON souboru a jejich zápis na kartu
                Object[][] configArray = loadJsonData(listView.getSelectionModel().getSelectedItem());

                for (Object[] config : configArray) {
                    String path = (String) config[0];
                    int offset = (int) config[1];
                    int data = (int) config[2];

                    for(WrapperJNA.myNode component : components){
                        if (Objects.equals(component.path, path)){
                            WrapperJNA.wrappernfb.nfb_comp_write(component.offset, offset, data);
                        }
                    }
                }
            }
        }
        else {
            new Alert(Alert.AlertType.WARNING, "Žádné připojené zařízení").showAndWait();
        }
    }

    /**
     * Funkce pro načtení dat z JSON souboru
     * @param fileName název souboru
     * @return 2D Array konfiguračních dat: path, offset, data
     * @throws IOException
     */
    private Object[][] loadJsonData(String fileName) throws IOException {
        String currentDirectory = System.getProperty("user.dir");
        String relativePath = "Konfigurační soubory";
        Path directoryPath = Paths.get(currentDirectory, relativePath).normalize();
        Path filePath = directoryPath.resolve(fileName).normalize();

        String content = new String(Files.readAllBytes(filePath));
        JSONArray jsonArray = new JSONArray(content);

        Object[][] configArray = new Object[jsonArray.length()][3];

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject config = jsonArray.getJSONObject(i);

            String path = config.getString("path");
            int offset = config.getInt("offset");
            int data = config.getInt("data");

            configArray[i][0] = path;
            configArray[i][1] = offset;
            configArray[i][2] = data;
        }
        return configArray;
    }


    /**
     * Funkce pro zobrazení souborů ve složce
     */
    void ListFilesInDirectory() {
        String currentDirectory = System.getProperty("user.dir");

        String relativePath = "Konfigurační soubory";
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
}
