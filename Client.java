package com.evan;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.scene.control.TextInputDialog;

/**
 * Created by Evan on 5/15/2017.
 * Client class to handle network connections to the server
 */
class Client
{
    private final Logger netLog = Logger.getLogger("Network");
    private final String[] nicks;
    private       String   nickname;
    private       String   user;

    Client(String[] nicks, String user)
    {
        this.nicks = nicks;
        this.user = user;
    }

    ClientThread startClient(InetAddress addr, int port)
    {
        ClientThread clientThread = new ClientThread(addr, port);
        clientThread.start();
        return clientThread;
    }

    public class ClientThread extends Thread
    {
        private Socket  mySocket = null;
        private boolean running  = true;
        private String     serverName;
        private Connection connection;
        private String     serverAddress;
        // Start at one since initial attempt is hardcoded to use 0
        private int nickAttempt = 1;

        ClientThread(InetAddress address, int port)
        {
            try
            {
                // Creates and connects to socket at specified port
                mySocket = new Socket(address, port);
                // Remove leading '/' from address string
                serverAddress = String.valueOf(address).substring(1);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                System.out.println("");
                netLog.log(Level.SEVERE, "Unable to connect to given address/port");
            }
        }

        public void run()
        {
            // Connection was accepted
            if (mySocket != null)
            {
                netLog.log(Level.INFO, "Connection made");
                // Capability negotiation
                write("CAP LS");
                // TODO Server password
                // Start registration
                // Record nickname, in case preferred nick is taken and we need to use another
                nickname = nicks[0];
                write("NICK " + nickname + "\r\nUSER " + user + " " + user + " " + serverAddress +
                      " " + ":realname");
                // End capability negotiation since nothing is supported yet...
                write("CAP END");
                while (running)
                {
                    String msg = read();
                    if (msg != null)
                    {
                        processMessage(msg);
                    }
                }
            }
        }

        void write(String str)
        {
            try
            {
                // Add end of message signifier to all passed messages
                str = str.concat("\r\n");
                DataOutputStream out = new DataOutputStream(mySocket.getOutputStream());
                out.write(str.getBytes());
                out.flush();
                netLog.log(Level.INFO, "Sent message " + str);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                netLog.log(Level.SEVERE, "Unable to write to server");
                closeConnection();
            }
        }

        private String read()
        {
            try
            {
                DataInputStream in = new DataInputStream(mySocket.getInputStream());
                try
                {
                    // Messages shouldn't ever be longer than 512 characters
                    byte[] bytes     = new byte[512];
                    int    i         = 0;
                    int    input     = in.read();
                    int    lastInput = -1;
                    while (input != -1)
                    {
                        // Add characters before we check if the message ended,
                        // otherwise log messages will not display properly
                        bytes[i] = (byte) input;

                        if (lastInput == 0x0d && input == 0x0a)
                        {
                            // Messages end in 0x0d 0x0a so end here
                            break;
                        }

                        i++;
                        lastInput = input;
                        input = in.read();
                    }
                    return new String(bytes);
                }
                catch (EOFException e)
                {
                    return null;
                }
            }
            catch (IOException e)
            {
                running = false;
                netLog.log(Level.SEVERE, "Unable to read from server");
                closeConnection();
            }

            return null;
        }

        private void processMessage(String msg)
        {
            netLog.log(Level.INFO, "Received: " + msg);
            String[] split;
            String   receivedFrom = null;

            if (msg.contains("NOTICE"))
            {
                split = msg.split("NOTICE \\*");
                // Display NOTICE message, without header info
                // Remove leading/trailing spaces, then remove leading ':'
                msg = split[1].trim().substring(1);
                if (serverName == null)
                {
                    // get the server name
                    serverName = split[0].split(" ")[0].substring(1);
                    Logger.getAnonymousLogger().log(Level.INFO, "Server name: " + serverName);
                }
            }
            else if (msg.contains("375 " + nickname) || msg.contains("372 " + nickname) ||
                     msg.contains("376 " + nickname))
            {
                split = msg.split(nickname);
                // MOTD (start, content, end), strip header information
                // Remove leading/trailing spaces, then remove leading ':'
                msg = split[1].trim().substring(1);
            }
            else if (msg.contains("PRIVMSG"))
            {
                split = msg.split(" ");
                // get the channel that sent the message
                receivedFrom = split[2];
            }
            else if (msg.contains("433 * " + nickname + " :Nickname is already in use."))
            {
                if (nickAttempt >= nicks.length || nicks[nickAttempt].isEmpty())
                {
                    // Allow JavaFX to update UI on correct thread
                    Platform.runLater(() ->
                                      {
                                          TextInputDialog dialog = new TextInputDialog("nickname");
                                          dialog.setTitle("Enter new nickname");
                                          dialog.setHeaderText(null);
                                          dialog.setContentText(
                                              "All given nicks are in use, please enter a new nickname:");

                                          Optional<String> result = dialog.showAndWait();
                                          result.ifPresent(s -> nicks[nickAttempt] = s);

                                          setNextNick();
                                      });
                }
                else
                {
                    setNextNick();
                }
            }
            else if (msg.startsWith("ERROR :Closing Link:"))
            {
                closeConnection();
                msg = "Connection closed by server";
            }
            connection.appendToWindow(msg, receivedFrom);
        }

        private void setNextNick()
        {
            nickname = nicks[nickAttempt];
            write("NICK " + nickname);
            nickAttempt = ++nickAttempt;
            Logger.getAnonymousLogger().log(Level.CONFIG, "attempts: " + nickAttempt);
        }

        void closeConnection()
        {
            try
            {
                mySocket.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
                netLog.log(Level.WARNING, "Socket failed to close properly");
            }
        }

        void setConnection(Connection c)
        {
            connection = c;
        }

        String getServerName()
        {
            return serverName;
        }

        String getNickname()
        {
            return nickname;
        }
    }
}
