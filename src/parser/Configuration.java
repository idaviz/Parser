/*
 * Loads the properties selected by the user via the configuration.properties file
 */
package parser;

import java.util.*;
import java.io.*;

/**
 *
 * @author dgarcia25
 */
public final class Configuration {

    Properties p = new Properties();

    public Configuration() throws IOException {
        p.load(new FileInputStream("../parser/configuration.properties"));
    }

    public String getAirport() {
        return p.getProperty("airport");
    }

    public String getDatabase() {
        return p.getProperty("database");
    }

    public String getEmpty_after_read() {
        return p.getProperty("empty_after_read");
    }

    public String getInbox_path() {
        return p.getProperty("inbox_path");
    }

    public String getRefresh_time() {
        return p.getProperty("refresh_time");
    }

    public String getTable() {
        return p.getProperty("table");
    }
    
     public String getUser() {
        return p.getProperty("user");
    }
     
      public String getPassword() {
        return p.getProperty("password");
    }
      
       public String getServerName() {
        return p.getProperty("server_name");
    }
}
