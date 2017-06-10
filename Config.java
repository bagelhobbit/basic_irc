package com.evan;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Evan on 6/7/2017.
 * Class to handle storing and retrieving options
 */
public class Config
{
    // Users working directory will contain config directory
    private static Path configPath   = Paths.get(System.getProperty("user.dir") + "\\config");
    private static Path userInfoPath = Paths.get(configPath.toString() + "\\userInfo.dat");

    private static Logger fileLog = Logger.getLogger("FileIO");

    private static boolean configFolderExists()
    {
        return Files.isDirectory(configPath);
    }

    //Returns an array containing primary nickname, secondary nickname, tertiary nickname, username
    public static String[] getUserInfo()
    {
        if (configFolderExists() && Files.isRegularFile(userInfoPath))
        {
            try
            {
                BufferedReader reader   = Files.newBufferedReader(userInfoPath);
                String         temp     = "";
                int            input    = reader.read();
                int            i        = 0;
                String[]       userInfo = new String[4];
                while (input != -1)
                {
                    if (input != ':')
                    {
                        temp = temp.concat(String.valueOf((char) input));
                    }
                    else
                    {
                        userInfo[i] = temp;
                        temp = "";
                        i++;
                    }
                    input = reader.read();
                }

                reader.close();
                return userInfo;

            }
            catch (IOException e)
            {
                e.printStackTrace();
                fileLog.log(Level.WARNING, "Error while trying to read " + userInfoPath.toString());
                return null;
            }
        }
        else
        {
            fileLog.log(Level.INFO, userInfoPath.toString() + " does not exist");
            return null;
        }
    }

    public static void setUserInfo(String nick1, String nick2, String nick3, String user)
    {

        try
        {
            if (!configFolderExists())
            {
                Files.createDirectory(configPath);
            }
            BufferedWriter writer = Files.newBufferedWriter(userInfoPath);
            writer.flush();
            writer.write(nick1 + ":" + nick2 + ":" + nick3 + ":" + user + ":");
            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            fileLog.log(Level.WARNING, "Unable to write to " + userInfoPath.toString());
        }
    }
}
