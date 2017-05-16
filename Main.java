package com.evan;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main
{

    public static void main(String[] args)
    {
        // write your code here
        Logger netLog = Logger.getLogger("Network");
        netLog.setLevel(Level.CONFIG);

        try
        {
            InetAddress address = InetAddress.getByName("192.168.0.200");
            Client client = new Client();

            client.startClient(address, 6667);
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
            netLog.log(Level.SEVERE, "Unknown host");
        }
    }
}
