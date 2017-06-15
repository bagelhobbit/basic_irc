package com.evan;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main extends Application
{
    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage)
    {
        Logger netLog = Logger.getLogger("Network");
        netLog.setLevel(Level.CONFIG);
        Logger logger = Logger.getLogger("Default");
        logger.setLevel(Level.CONFIG);
        Logger fileLog = Logger.getLogger("FileIO");
        fileLog.setLevel(Level.CONFIG);

        String[] userInfo = Config.getUserInfo();

        final Image errorIcon = new Image(getClass().getResource("Images/error.png").toString(),
                                          16,
                                          16,
                                          true,
                                          true,
                                          true);

        String nick  = "";
        String nick2 = "";
        String nick3 = "";
        String name  = "";

        if (userInfo != null)
        {
            nick = userInfo[0];
            nick2 = userInfo[1];
            nick3 = userInfo[2];
            name = userInfo[3];
            //TODO: move focus from nickname field
        }

        primaryStage.setTitle("Basic IRC");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text userInfoTitle = new Text("User Information");
        grid.add(userInfoTitle, 0, 0, 3, 1);

        Label primaryNickLabel = new Label("Nick name:");
        grid.add(primaryNickLabel, 0, 1);

        ImageView primaryNickError = new ImageView();
        // Make sure image is set properly on initialization
        Image primaryImage = nick.isEmpty() ? errorIcon : null;
        primaryNickError.setImage(primaryImage);

        TextField primaryNick = new TextField(nick);
        // Update icon based on textfield contents
        primaryNick.textProperty().addListener(observable ->
                                               {
                                                   final String text = primaryNick.getText();
                                                   Image        icon =
                                                       text.isEmpty() ? errorIcon : null;
                                                   primaryNickError.setImage(icon);
                                               });

        Tooltip nickTooltip = new Tooltip("You cannot have an empty nickname.");
        Tooltip.install(primaryNickError, nickTooltip);

        StackPane nickStack = new StackPane();
        nickStack.getChildren().addAll(primaryNick, primaryNickError);
        nickStack.setAlignment(Pos.CENTER_RIGHT);
        StackPane.setMargin(primaryNickError, new Insets(0, 10, 0, 0));

        grid.add(nickStack, 1, 1);

        Label secondaryNickLabel = new Label("Second choice:");
        grid.add(secondaryNickLabel, 0, 2);

        ImageView secondaryNickError = new ImageView();
        // Make sure image is set properly on initialization
        Image secondaryImage = nick2.isEmpty() ? errorIcon : null;
        secondaryNickError.setImage(secondaryImage);

        TextField secondaryNick = new TextField(nick2);
        // Update icon based on textfield contents
        secondaryNick.textProperty().addListener(observable ->
                                                 {
                                                     final String text = secondaryNick.getText();
                                                     Image        icon =
                                                         text.isEmpty() ? errorIcon : null;
                                                     secondaryNickError.setImage(icon);
                                                 });

        // Reuse tooltip from above
        Tooltip.install(secondaryNickError, nickTooltip);

        // This isn't a very good name, but I don't have a better one...
        StackPane nick2Stack = new StackPane();
        nick2Stack.getChildren().addAll(secondaryNick, secondaryNickError);
        nick2Stack.setAlignment(Pos.CENTER_RIGHT);
        StackPane.setMargin(secondaryNickError, new Insets(0, 10, 0, 0));

        grid.add(nick2Stack, 1, 2);

        // A third nickname choice is optional so there is no error icon here
        Label tertiaryNickLabel = new Label("Third Choice:");
        grid.add(tertiaryNickLabel, 0, 3);

        TextField tertiaryNick = new TextField(nick3);
        grid.add(tertiaryNick, 1, 3);

        Label userNameLabel = new Label("User name:");
        grid.add(userNameLabel, 0, 4);

        ImageView userNameError = new ImageView();
        // Make sure image is set properly on initialization
        Image userImage = name.isEmpty() ? errorIcon : null;
        userNameError.setImage(userImage);

        TextField userName = new TextField(name);
        // Update icon based on textfield contents
        userName.textProperty().addListener(observable ->
                                            {
                                                final String text = userName.getText();
                                                Image        icon =
                                                    text.isEmpty() ? errorIcon : null;
                                                userNameError.setImage(icon);
                                            });

        Tooltip userTooltip = new Tooltip("User name cannot be left blank.");
        Tooltip.install(userNameError, userTooltip);

        StackPane userNameStack = new StackPane();
        userNameStack.getChildren().addAll(userName, userNameError);
        userNameStack.setAlignment(Pos.CENTER_RIGHT);
        StackPane.setMargin(userNameError, new Insets(0, 10, 0, 0));

        grid.add(userNameStack, 1, 4);


        Text connectInfoTitle = new Text("Connection Information");
        grid.add(connectInfoTitle, 0, 6, 3, 1);

        Label serverAddress = new Label("Server address:");
        grid.add(serverAddress, 0, 7);

        TextField addressTextField = new TextField();
        grid.add(addressTextField, 1, 7);

        Label serverPort = new Label("Server Port:");
        grid.add(serverPort, 0, 8);

        TextField portTextField = new TextField();
        // Set prompt text to default port number
        portTextField.setPromptText("6667");
        grid.add(portTextField, 1, 8);

        Button connect = new Button("Connect");
        connect.setDefaultButton(true);
        connect.setOnAction((ActionEvent event) ->
                            {
                                // Default values
                                InetAddress address = null;
                                int         port;
                                String[]    nicks   = new String[3];
                                String      user;

                                nicks[0] = primaryNick.getText();
                                nicks[1] = secondaryNick.getText();
                                nicks[2] = tertiaryNick.getText();

                                if (nicks[0].isEmpty())
                                {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setHeaderText(null);
                                    alert.setContentText("Please enter a nick name!");
                                    alert.showAndWait();
                                    return;
                                }
                                else if (nicks[1].isEmpty())
                                {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setHeaderText(null);
                                    alert.setContentText("Please enter a backup nick name!");
                                    alert.showAndWait();
                                    return;
                                }

                                user = userName.getText();
                                if (user.isEmpty() || user.equals(""))
                                {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setHeaderText(null);
                                    alert.setContentText("Please enter a user name!");
                                    alert.showAndWait();
                                    return;
                                }

                                if (nicks[2] == null)
                                {
                                    // make sure we don't pass the string "null" to setUserInfo()
                                    nicks[2] = "";
                                }

                                Config.setUserInfo(nicks[0], nicks[1], nicks[2], user);

                                logger.log(Level.INFO, "Parsing server connection input");
                                if (addressTextField.getText().equals(""))
                                {
                                    // Address field not allowed to be empty
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setHeaderText(null);
                                    alert.setContentText(
                                        "Please enter a server address to connect to.");
                                    alert.showAndWait();
                                    return;
                                }
                                try
                                {
                                    address = InetAddress.getByName(addressTextField.getText());
                                    port = Integer.valueOf(portTextField.getText());
                                }
                                catch (UnknownHostException e)
                                {
                                    e.printStackTrace();
                                    netLog.log(Level.SEVERE, "Unknown host, could not connect");

                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setHeaderText(null);
                                    alert.setContentText(
                                        "Unknown host, could not connect to the server.");
                                    alert.showAndWait();

                                    // Stop connection if we don't have a valid host
                                    return;
                                }
                                catch (NumberFormatException e)
                                {
                                    e.printStackTrace();
                                    port = 6667;
                                    netLog.log(Level.WARNING,
                                               "Unable to parse port number, using default value");
                                }

                                startConnection(address, port, nicks, user);
                            }
        );
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(connect);
        grid.add(hbBtn, 1, 9);

        Scene scene = new Scene(grid, 300, 350);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void startConnection(InetAddress address, int port, String[] nicks, String user)
    {
        Client client = new Client(nicks, user);
        new Connection(client.startClient(address, port), address.toString(), "SERVER");
    }


}
