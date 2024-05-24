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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RizeniController {

    @FXML
    private Button backButton;

    @FXML
    private TextArea infoTextArea;

    @FXML
    private ComboBox<String> numComboBox;

    @FXML
    private ListView<String> listView;

    public void initialize() {
        if(AppState.getInstance().getConnected()){
            infoTextArea.setText(AppState.getInstance().getDeviceInfo());
        }

        numComboBox.getItems().add("0");
        numComboBox.getItems().add("1");

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
    protected void onPoslatButtonClick () throws IOException {
        //WrapperJNA.wrappernfb.sendData(rozhraniComboBox.getSelectionModel().getSelectedItem(), Integer.parseInt(numComboBox.getSelectionModel().getSelectedItem()));
        WrapperJNA.wrappernfb.importData(listView.getSelectionModel().getSelectedItem(), Integer.parseInt(numComboBox.getSelectionModel().getSelectedItem()));
        //https://cesnet.github.io/ndk-sw/libnfb-example.html#ndp-data-transmit-example
        //zobrait data, povolit jejich upravu, nezavirat dokud nevypnu celou applikaci
    }


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

    @FXML
    protected void onObnovitButtonClick () {
        listView.getItems().clear();
        ListFilesInDirectory();
    }


}
