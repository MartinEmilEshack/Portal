package Portal.UI;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class Controller {

    @FXML
    TextField URL;
    @FXML
    TextField IP;
    @FXML
    TextField password;
    @FXML
    TextField yourIP;
    @FXML
    TextField yourPassword;
    @FXML
    Button seePassword;
    @FXML
    Button openFile;
    @FXML
    Button send;

    private static Stage theStage;
    private File selectedFile;

    static void setTheStage(Stage theStage) {
        Controller.theStage = theStage;
    }

    public void openFile(){
        FileChooser fileChooser = new FileChooser();
        selectedFile =  fileChooser.showOpenDialog(theStage);
    }

}
