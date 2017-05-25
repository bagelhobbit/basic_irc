package com.evan;

import java.util.HashMap;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Created by Evan on 5/24/2017.
 * Class to manage server connection information
 */
public class Connection
{
    private final String              server;
    private final Client.ClientThread thread;
    private final HashMap<String, TextArea> channelLog = new HashMap<>();
    private final VBox                      list       = new VBox();
    private final VBox                      textPane   = new VBox();
    private String currentChannel;

    Connection(Client.ClientThread clientThread, String address, String label)
    {
        thread = clientThread;
        thread.setConnection(this);
        server = label;

        TextArea info = new TextArea("Connecting to " + address + "\n");
        info.setEditable(false);
        info.setWrapText(true);
        info.setFont(Font.font("Monospaced"));

        channelLog.put(server, info);
        currentChannel = server;

        TextField commandField = new TextField();
        commandField.setAlignment(Pos.BASELINE_LEFT);
        commandField.setOnAction((ActionEvent event) ->
                                 {
                                     processInput(commandField.getText());
                                     commandField.clear();
                                 }
        );

        textPane.getChildren().addAll(info, commandField);
        VBox.setVgrow(info, Priority.ALWAYS);

        Label serverName = new Label(server);
        serverName.setOnMouseClicked((MouseEvent event) ->
                                     {
                                         if (event.getButton().equals(MouseButton.PRIMARY))
                                         {
                                             textPane.getChildren().set(0, info);
                                             currentChannel = server;
                                         }
                                     }
        );

        list.getChildren().add(serverName);

        HBox hBox = new HBox();
        hBox.getChildren().addAll(list, textPane);
        HBox.setHgrow(textPane, Priority.ALWAYS);
        HBox.setMargin(list, new Insets(5, 5, 0, 10));

        Stage serverWindow = new Stage();
        serverWindow.setTitle("Server name");
        serverWindow.setScene(new Scene(hBox, 1000, 500));
        serverWindow.show();
    }

    private void processInput(String input)
    {
        if (input.toLowerCase().startsWith("/join"))
        {
            // Remove leading '/join' to better format request
            currentChannel = input.substring(5).trim();
            thread.write("JOIN :" + currentChannel);

            Label label = new Label(currentChannel);
            label.setOnMouseClicked((MouseEvent event) ->
                                    {
                                        if (event.getButton().equals(MouseButton.PRIMARY))
                                        {
                                            String text = label.getText();
                                            textPane.getChildren()
                                                    .set(0, channelLog.get(text));
                                            currentChannel = text;
                                        }
                                    });

            TextArea area = new TextArea("Joined channel " + currentChannel);
            area.setEditable(false);
            area.setWrapText(true);
            area.setFont(Font.font("Monospaced"));

            channelLog.put(currentChannel, area);
            textPane.getChildren().set(0, area);
            VBox.setVgrow(area, Priority.ALWAYS);

            list.getChildren().add(label);
            VBox.setMargin(label, new Insets(0, 0, 0, 6));
        }
        else if (input.toLowerCase().startsWith("/part"))
        {
            // Remove leading command for better formatting
            String toLeave = input.substring(5).trim();
            if (toLeave.isEmpty() || toLeave.equals(""))
            {
                toLeave = currentChannel;
            }
            thread.write("PART :" + toLeave);
            appendToWindow("You have left the channel\n", currentChannel);
        }
        else if (input.toLowerCase().startsWith("/quit"))
        {
            // TODO add quit message
            thread.write("QUIT");
            thread.closeConnection();
        }
        else
        {
            // Assume we are sending a message to the current channel
            thread.write("PRIVMSG " + currentChannel + " :" + input);
            appendToWindow(thread.getNickname() + ": " + input, currentChannel);
        }
    }

    public void appendToWindow(String text, String receivedFrom)
    {
        if (receivedFrom == null)
        {
            receivedFrom = server;
        }
        // Let the FX thread update the text area
        // Trim text to remove any trailing space/newline, then add a newline to ensure line breaks
        final String finalReceivedFrom = receivedFrom;
        Platform.runLater(() -> channelLog.get(finalReceivedFrom).appendText(text.trim() + "\n"));
    }
}
