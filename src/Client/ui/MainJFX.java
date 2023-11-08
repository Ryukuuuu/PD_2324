package Client.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class MainJFX extends Application {

    @Override
    public void init() throws Exception{
        super.init();
    }

    @Override
    public void start(Stage stage) throws Exception{
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/LogInScreen.fxml")));
        stage.setTitle("Programação Distribuida - 23/24");
        stage.setScene(new Scene(root,800,800));
        stage.show();
    }


}
