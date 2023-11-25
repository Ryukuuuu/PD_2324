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
        createView(modelManager, "layout/log-in-screen.fxml");
        createView(modelManager, "layout/client-start-menu.fxml");
        createView(modelManager,"layout/sign-in-screen.fxml");
        createView(modelManager,"layout/profile.fxml");
        createView(modelManager,"layout/edit-user-info.fxml");
        createView(modelManager,"layout/events-menu.fxml");
        createView(modelManager,"layout/admin-start-menu.fxml");
        createView(modelManager,"layout/admin-create-event.fxml");
        createView(modelManager,"layout/admin-edit-event.fxml");
        createView(modelManager,"layout/admin-delete-event.fxml");
        createView(modelManager,"layout/admin-generate-event-code.fxml");
    }

    private void createView(ModelManager modelManager,String resourcePath) throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(MainJFX.class.getResource(resourcePath));
        Parent child = fxmlLoader.load();
        switch (resourcePath) {
            case "layout/log-in-screen.fxml"->{
                LogInController controller = fxmlLoader.getController();
                controller.init(modelManager);
            }
            case "layout/client-start-menu.fxml" -> {
                UserMenuController controller = fxmlLoader.getController();
                controller.init(modelManager);
            }
            case "layout/sign-in-screen.fxml" -> {
                SignInController controller = fxmlLoader.getController();
                controller.init(modelManager);
            }
            case "layout/profile.fxml" -> {
                ProfileController controller = fxmlLoader.getController();
                controller.init(modelManager);
            }
            case "layout/edit-user-info.fxml" -> {
                EditUserInfoController controller = fxmlLoader.getController();
                controller.init(modelManager);
            }
            case "layout/events-menu.fxml" -> {
                EventsMenuController controller = fxmlLoader.getController();
                controller.init(modelManager);
            }
            case "layout/admin-start-menu.fxml" -> {
                AdminMenuController controller = fxmlLoader.getController();
                controller.init(modelManager);
            }
            case "layout/admin-create-event.fxml" ->{
                AdminCreateEventController controller = fxmlLoader.getController();
                controller.init(modelManager);
            }
            case "layout/admin-edit-event.fxml" -> {
                AdminEditEventController controller = fxmlLoader.getController();
                controller.init(modelManager);
            }
            case "layout/admin-delete-event.fxml" -> {
                AdminDeleteEventController controller = fxmlLoader.getController();
                controller.init(modelManager);
            }
            case "layout/admin-generate-event-code.fxml" ->{
                AdminGenerateEventCodeController controller = fxmlLoader.getController();
                controller.init(modelManager);
            }
        }
        stackPane.getChildren().add(child);
    }
}
