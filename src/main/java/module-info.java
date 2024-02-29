module cz.vut.fekt.askfpga {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.sun.jna;


    opens cz.vut.fekt.askfpga to javafx.fxml;
    exports cz.vut.fekt.askfpga;
    exports cz.vut.fekt.askfpga.controllers;
    opens cz.vut.fekt.askfpga.controllers to javafx.fxml;
}