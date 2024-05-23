package cz.vut.fekt.askfpga.controllers;

import cz.vut.fekt.askfpga.AppState;
import cz.vut.fekt.askfpga.AskfpgaApp;
import cz.vut.fekt.askfpga.WrapperJNA;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import java.nio.file.*;

import java.io.IOException;
import java.util.ArrayList;

public class KonfiguraceController {

    @FXML
    private Button backButton;

    @FXML
    private Button importButton;

    @FXML
    private Button zobrazitButton;

    @FXML
    private Button zapsatButton;

    @FXML
    private TextArea outputTextArea;

    @FXML
    private ListView<String> listView;

    @FXML
    private Button obnovitButton;

    @FXML
    private TextArea infoTextArea;

    @FXML
    private ComboBox<String> comboBox;

    public void initialize() {
        listView.getItems().clear();
        ListFilesInDirectory();

        if(AppState.getInstance().getConnected()){
            StringBuilder info = new StringBuilder();
            for (WrapperJNA.Paths prop : WrapperJNA.Paths.values()) {
                String value = WrapperJNA.wrappernfb.getProp(AppState.getInstance().getDevPointer(), prop);
                info.append(prop.name()).append(": ").append(value).append("\n");
            }
            infoTextArea.setText(info.toString());

            ArrayList<String> components;

            components = WrapperJNA.wrappernfb.print_component_list(AppState.getInstance().getDevPointer());

            comboBox.getItems().addAll(components);
        }
    }


    @FXML
    protected void onBackButtonClick () {
        AppState.getInstance().setCurrentTime();
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

    @FXML
    protected void onObnovitButtonClick () {
        listView.getItems().clear();

        ListFilesInDirectory();
    }

    @FXML
    protected void onZobrazitButtonClick () {
        outputTextArea.setText("Zobrazena hex hodnota");

    }

    @FXML
    protected void onZapsatButtonClick () {

    }

    @FXML
    protected void onImportButtonClick () {
        System.out.println(listView.getSelectionModel().getSelectedItem());

    }


    void ListFilesInDirectory() {
        String currentDirectory = System.getProperty("user.dir");

        String relativePath = "src\\main\\resources\\cz\\vut\\fekt\\askfpga\\Konfigurační soubory";
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
