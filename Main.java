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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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

        String[] userInfo = Config.getUserInfo();

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

        TextField primaryNick = new TextField(nick);
        grid.add(primaryNick, 1, 1);

        Label secondaryNickLabel = new Label("Second choice:");
        grid.add(secondaryNickLabel, 0, 2);

        TextField secondaryNick = new TextField(nick2);
        grid.add(secondaryNick, 1, 2);

        Label tertiaryNickLabel = new Label("Third Choice:");
        grid.add(tertiaryNickLabel, 0, 3);

        TextField tertiaryNick = new TextField(nick3);
        grid.add(tertiaryNick, 1, 3);

        Label userNameLabel = new Label("User name:");
        grid.add(userNameLabel, 0, 4);

        TextField userName = new TextField(name);
        grid.add(userName, 1, 4);


        Text connectInfoTitle = new Text("Connection Information");
        grid.add(connectInfoTitle, 0, 6, 3, 1);

        Label serverAddress = new Label("Server address:");
        grid.add(serverAddress, 0, 7);

        TextField addressTextField = new TextField();
        grid.add(addressTextField, 1, 7);

        Label serverPort = new Label("Server Port:");
        grid.add(serverPort, 0, 8);

        TextField portTextField = new TextField();
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

                                if (nicks[0].isEmpty() || nicks[0].equals(""))
                                {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setHeaderText(null);
                                    alert.setContentText("Please enter a nick name!");
                                    alert.showAndWait();
                                    return;
                                }
                                else if (nicks[1].isEmpty() || nicks[1].equals(""))
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

                                logger.log(Level.INFO, "Parsing server connection input");
                                try
                                {
                                    address = InetAddress.getByName(addressTextField.getText());
                                    port = Integer.valueOf(portTextField.getText());
                                }
                                catch (UnknownHostException e)
                                {
                                    e.printStackTrace();
                                    netLog.log(Level.SEVERE, "Unknown host, could not connect");
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
