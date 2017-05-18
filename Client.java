package com.evan;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Evan on 5/15/2017.
 * Client class to handle network connections to the server
 */
public class Client
{
    private Logger netLog = Logger.getLogger("Network");
    private String[] nicks;

    public Client(String[] nicks)
    {
        this.nicks = nicks;
    }

    public ClientThread startClient(InetAddress addr, int port)
    {
        ClientThread clientThread = new ClientThread(addr, port);
        clientThread.start();
        return clientThread;
    }

    public class ClientThread extends Thread
    {
        Socket mySocket = null;
        private boolean running = true;

        public ClientThread(InetAddress address, int port)
        {
            try
            {
                // Creates and connects to socket at specified port
                mySocket = new Socket(address, port);
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
                write("CAP LS\r\n");
                // TODO Server password
                // Start registration
                // Use preferred nickname
                // TODO USER params
                write("NICK " + nicks[0] + "\r\nUSER user host server :realname\r\n");
                // End capability negotiation since nothing is supported yet...
                write("CAP END\r\n");
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

        public void write(String str)
        {
            try
            {
                DataOutputStream out = new DataOutputStream(mySocket.getOutputStream());
                out.write(str.getBytes());
                out.flush();
                netLog.log(Level.INFO, "Sent message " + str);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                netLog.log(Level.SEVERE, "Unable to write to server");
            }
        }

        private String read()
        {
            try
            {
                DataInputStream in = new DataInputStream(mySocket.getInputStream());
                try
                {
                    // Should only need 512 bytes
                    byte[] bytes = new byte[512];
                    int i = 0;
                    int input = in.read();
                    while(input != -1)
                    {
                        if (input == 0x0d)
                        {
                            // Messages end in 0x0d 0x0a so end here
                            // TODO check for following 0x0a
                            break;
                        }
                        bytes[i] = (byte) input;
                        i++;
                        input = in.read();
                    }
                    return new String(bytes);
                }
                catch (EOFException e)
                {
                    return null;
                }
            }
            catch(IOException e)
            {
                e.printStackTrace();
                running = false;
                netLog.log(Level.SEVERE, "Unable to read from server");
            }

            return null;
        }

        private void processMessage(String msg)
        {
            netLog.log(Level.INFO, "Received: " + msg);
            String[] split = msg.split(":");
            if (split[0].contains("NOTICE"))
            {
                msg = split[1];
            }
            Main.appendToWindow(msg);
        }
    }
}
