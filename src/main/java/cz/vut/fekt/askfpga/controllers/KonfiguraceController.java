package cz.vut.fekt.askfpga.controllers;

import cz.vut.fekt.askfpga.AppState;
import cz.vut.fekt.askfpga.AskfpgaApp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import java.nio.file.*;

import java.io.IOException;

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
    private Button exportButton;

    @FXML
    private TextArea outputTextArea;


    @FXML
    private ListView<String> listView;

    @FXML
    private Button obnovitButton;

    public void initialize() {
        listView.getItems().clear();

        ListFilesInDirectory();
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
    protected void onExportButtonClick () {

    }

    @FXML
    protected void onImportButtonClick () {
        System.out.println(listView.getSelectionModel().getSelectedItem());

    }


    void ListFilesInDirectory() {
        // Get the current working directory
        String currentDirectory = System.getProperty("user.dir");

        // Specify the relative path to your targeted directory
        String relativePath = "src\\main\\resources\\cz\\vut\\fekt\\askfpga\\Konfigurační soubory";
        Path directoryPath = Paths.get(currentDirectory, relativePath).normalize();
        try {
            // Get list of files in the directory
            Files.list(directoryPath)
                    .filter(Files::isRegularFile)
                    .forEach(file -> listView.getItems().add(file.getFileName().toString()));
        } catch (IOException e) {
            System.err.println("Error accessing directory: " + e.getMessage());
            e.printStackTrace();
        }
    }




}
