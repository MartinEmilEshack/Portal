package Portal.UI;

import Portal.Network.ReceivePort;
import Portal.Network.SendPort;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Scanner;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
//        Thread loginDataReceiver = new Thread(ReceivePort.startLoginDataReceiver());
//        loginDataReceiver.setDaemon(true);
//        loginDataReceiver.run();
        Parent root = FXMLLoader.load(getClass().getResource("MainWindow.fxml"));
        primaryStage.setTitle("Portal");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
        //Controller.setTheStage(primaryStage);
    }

    public static void main(String[] args) {
        ReceivePort.generatePassword();
        Thread thread = new Thread(ReceivePort.startLoginDataReceiver());
        thread.start();

        Scanner input = new Scanner(System.in);
        int password;
        boolean success = false;

        while (!success) {
            System.out.println("Enter Password: -> "+ReceivePort.getPassword());
            password = input.nextInt();
            success = SendPort.sendRequest("127.0.0.1", password, null);
        }
        ReceivePort.close();
//        System.out.println("main while finished");
//        System.out.println(thread.isAlive());
//        launch(args);
    }

}

//Resources: -OneDark Pro color themes(of VS-code)- https://github.com/Binaryify/OneDark-Pro/blob/master/themes/OneDark-Pro.json

// #171e2b
// #1e2739