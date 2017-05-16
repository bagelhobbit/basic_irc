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

    public Client()
    {
    }

    public void startClient(InetAddress addr, int port)
    {
        ClientThread clientThread = new ClientThread(addr, port);
        clientThread.start();
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
                out.writeUTF(str);
                out.flush();
                netLog.log(Level.INFO, "Sent message" + str);
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
                    return in.readUTF();
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
        }
    }
}
