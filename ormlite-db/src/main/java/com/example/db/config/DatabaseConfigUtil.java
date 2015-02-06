package com.example.db.config;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class DatabaseConfigUtil extends OrmLiteConfigUtil {
    public static void main(String[] args) {
        String currentDir = System.getProperty("user.dir");
        final File configFile = new File(currentDir + "/app/src/main/res/raw/ormlite_config.txt");
        try {
            writeConfigFile(configFile);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
