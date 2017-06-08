package com.evan;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Evan on 6/7/2017.
 * Class to handle storing and retrieving options
 */
public class Config
{
    // Users working directory will contain config directory
    private static Path configPath   = Paths.get(System.getProperty("user.dir") + "\\config");
    private static Path userInfoPath = Paths.get(configPath.toString() + "\\userInfo.dat");

    private static boolean testFolderExists()
    {
        return Files.isDirectory(configPath);
    }

    //Returns an array containing primary nickname, secondary nickname, tertiary nickname, username
    public static String[] getUserInfo()
    {
        if (testFolderExists() && Files.isRegularFile(userInfoPath))
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

                return userInfo;

            }
            catch (IOException e)
            {
                e.printStackTrace();
                return null;
            }
        }
        else
        {
            return null;
        }
    }

    public void setUserInfo()
    {

    }
}
