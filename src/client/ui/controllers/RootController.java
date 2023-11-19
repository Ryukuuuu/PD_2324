package client.ui.controllers;


import client.model.ModelManager;
import client.ui.MainJFX;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class RootController {
    private ModelManager modelManager;
    @FXML
    protected StackPane stackPane;

    @FXML
    protected BorderPane login;

    public void init(ModelManager modelManager) throws IOException {
        this.modelManager = modelManager;
        createViews();
    }
    private void createViews() throws IOException {
        createView(modelManager, "layout/LogInScreen.fxml");
        createView(modelManager, "layout/userStartMenu.fxml");
        createView(modelManager,"layout/signInScreen.fxml");
        createView(modelManager,"layout/profile.fxml");
        createView(modelManager,"layout/edit-user-info.fxml");
    }

    private void createView(ModelManager modelManager,String resourcePath) throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(MainJFX.class.getResource(resourcePath));
        Parent child = fxmlLoader.load();
        switch (resourcePath) {
            case "layout/LogInScreen.fxml"->{
                LogInController controller = fxmlLoader.getController();
                controller.init(modelManager);
            }
            case "layout/userStartMenu.fxml" -> {
                UserMenuController controller = fxmlLoader.getController();
                controller.init(modelManager);
            }
            case "layout/signInScreen.fxml" -> {
                SignInController controller = fxmlLoader.getController();
                controller.init(modelManager);
            }
            case "layout/profile.fxml" -> {
                ProfileController controller = fxmlLoader.getController();
                controller.init(modelManager);
            }
            case "layout/edit-user-info.fxml" ->{
                EditUserInfoController controller = fxmlLoader.getController();
                controller.init(modelManager);
            }
        }
        stackPane.getChildren().add(child);
    }
}
