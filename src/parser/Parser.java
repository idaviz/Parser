/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author dgarcia25
 */
public class Parser {

    //static File f = new File();
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws java.sql.SQLException
     * @throws java.lang.InterruptedException
     */
    public static void main(String[] args) throws IOException, SQLException, InterruptedException {
        
        System.out.println("Bienvenido " + System.getProperty("user.name") + "\n");
        System.out.println("Iniciando Parser...\n");
        
        System.out.println("Parámetros de carga");
        System.out.println("=========================================");
        Configuration c = new Configuration();
        System.out.println("Airport             "+c.getAirport());
        System.out.println("Delete files        "+c.getEmpty_after_read());
        System.out.println("Inbox path          "+c.getInbox_path());
        System.out.println("Update time (min)   "+c.getRefresh_time());
        System.out.println("Server name         "+c.getServerName());
        System.out.println("Database name       "+c.getDatabase());
        System.out.println("Table  name         "+c.getTable());
        System.out.println("User name           "+c.getUser());
        System.out.println("Password name       "+c.getPassword());
        System.out.println("==========================================");
        System.out.println("");
        
        
        while (true) {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1;
            int day = cal.get(Calendar.DAY_OF_MONTH);
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int min = cal.get(Calendar.MINUTE);
            System.out.println("Comprobando la bandeja de entrada, el " + day + "/" + month + "/" + year + " a las " + hour + " horas y " + min + " minutos ...\n");
            FolderWatcher w = new FolderWatcher();
            System.out.println("Procesando el contenido de "+c.getInbox_path()+"\n\n");
            /* Con File Handler */
            FileHandler f = new FileHandler();
            f.processFileList(w.getRawFileListMaster());
            System.out.println("El proceso se reactivará en "+c.getRefresh_time()+" minutos.");
            // Espera x minutos
            TimeUnit.MINUTES.sleep(Integer.parseInt(c.getRefresh_time()));
        }
    }
}
