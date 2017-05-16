package com.evan;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main
{
    public final static Object monitor = new Object();

    public static void main(String[] args)
    {
        Logger netLog = Logger.getLogger("Network");
        netLog.setLevel(Level.CONFIG);

        try
        {
            InetAddress address = InetAddress.getByName("192.168.0.200");
            Client client = new Client();

            Client.ClientThread connection = client.startClient(address, 6667);
            // Wait for client to connect
            synchronized (monitor)
            {
                try
                {
                    monitor.wait();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            connection.write("CAP LS\r\n");
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
            netLog.log(Level.SEVERE, "Unknown host");
        }
    }
}
