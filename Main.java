package com.evan;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main extends Application
{

    private static TextArea info;

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
        connect.setDefaultButton(true);
        connect.setOnAction((ActionEvent event) ->
                            {
                                logger.log(Level.INFO, "Parsing server connection input");
                                // Default values
                                InetAddress address = null;
                                int         port;
                                String[]    nicks   = {"Nickname", "name", "user"};

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

                                startConnection(address, port, nicks);
                            }
        );
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(connect);
        grid.add(hbBtn, 1, 3);

        Scene scene = new Scene(grid, 300, 250);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void startConnection(InetAddress address, int port, String[] nicks)
    {
        Client client = new Client(nicks);

        Client.ClientThread connection = client.startClient(address, port);

        info = new TextArea("Connecting to " + address + "\n");
        info.setEditable(false);
        info.setWrapText(true);
        info.setFont(Font.font("Monospaced"));

        TextField commandField = new TextField();
        commandField.setAlignment(Pos.BASELINE_LEFT);
        commandField.setOnAction((ActionEvent event) ->
                                 {
                                     processInput(commandField.getText());
                                     commandField.clear();
                                 }
        );

        VBox root = new VBox();
        root.getChildren().addAll(info, commandField);
        VBox.setVgrow(info, Priority.ALWAYS);

        Stage serverWindow = new Stage();
        serverWindow.setTitle("Server name");
        serverWindow.setScene(new Scene(root, 600, 400));
        serverWindow.show();
    }

    public static void appendToWindow(String text)
    {
        // Assume a newline is already added
        // Let the FX thread update the text area
        Platform.runLater(() -> info.appendText(text));
    }

    private void processInput(String input)
    {

    }
}
