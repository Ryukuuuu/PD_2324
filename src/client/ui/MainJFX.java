package client.ui;

import client.model.ModelManager;
import client.ui.controllers.RootController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainJFX extends Application {

    ModelManager modelManager;

    @Override
    public void init() throws Exception{
        super.init();
        modelManager = new ModelManager(getParameters().getRaw().toArray(new String[0]));
    }

    @Override
    public void start(Stage stage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(MainJFX.class.getResource("layout/root-pane.fxml"));
        Parent child = fxmlLoader.load();
        RootController r = fxmlLoader.getController();
        r.init(modelManager);
        Scene scene = new Scene(child,1000,800);
        stage.setScene(scene);
        stage.setTitle("Programacão Distribuida - 23/24");
        stage.setResizable(false);
        stage.show();
    }

}
