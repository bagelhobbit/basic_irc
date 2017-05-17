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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main extends Application
{
    public final static Object monitor = new Object();

    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage)
    {
        Logger netLog = Logger.getLogger("Network");
        netLog.setLevel(Level.CONFIG);

        primaryStage.setTitle("Basic IRC");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text sceneTitle = new Text("Connection information");
        grid.add(sceneTitle, 0, 0, 3, 1);

        Label serverAddress = new Label("Server address:");
        grid.add(serverAddress, 0, 1);

        TextField addressTextField = new TextField();
        grid.add(addressTextField, 1, 1);

        Label serverPort = new Label("Server Port:");
        grid.add(serverPort, 0, 2);

        TextField portTextField = new TextField();
        grid.add(portTextField, 1, 2);

        Button connect = new Button("Connect");
        connect.setOnAction((ActionEvent event) ->
                            {
                                netLog.log(Level.INFO, "Hello, World!");
                                // Default values
                                InetAddress address = null;
                                int         port = 6667;
                                String[] nicks = {"Nickname", "name", "user"};

                                try
                                {
                                    address = InetAddress.getByName(addressTextField.getText());
                                    port = Integer.valueOf(portTextField.getText());
                                }
                                catch (UnknownHostException e)
                                {
                                    e.printStackTrace();
                                    netLog.log(Level.SEVERE, "Unknown host");
                                    // Stop connection if we don't have a valid host
                                    return;
                                }
                                catch (NumberFormatException e)
                                {
                                    e.printStackTrace();
                                    netLog.log(Level.INFO,
                                               "Unable to parse port number, using default value");
                                }

                                startConnection(address, port, nicks);
                            }
        );
        grid.add(connect, 0, 3, 3, 1);

        Scene scene = new Scene(grid, 300, 250);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void startConnection(InetAddress address, int port, String[] nicks)
    {
        Client client = new Client(nicks);

        Client.ClientThread connection = client.startClient(address, port);
    }
}
