/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * This class receives a B-Type message and inserts it into the DB.
 *
 * @author dgarcia25
 */
public class FileHandler {

    /**
     * Database connection definition.
     */
    private Connection con;

//    /**
//     * Method to establish connection to the DB.
//     *
//     * @throws SQLException
//     */
//    private void DBconnection() throws SQLException, IOException {
//        Configuration c = new Configuration();
//        try {
//            // Driver to be used
//            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//            // Connection definition
//            con = DriverManager.getConnection("jdbc:sqlserver://"+c.getServerName()+";user="+c.getUser()+";password="+c.getPassword()+";");
//        } catch (ClassNotFoundException ex) {
//            Logger.getLogger( FileHandler.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    private void DBconnection() throws NamingException, SQLException, IOException {
        Configuration c = new Configuration();
        String url = "jdbc:mysql://localhost:3306/";
        String dbName = "hermes";
        String driver = "com.mysql.jdbc.Driver";
        String userName = "root";
        String password = "root";
        try {
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + dbName, userName, password);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException e) {
        }
    }

    /**
     * Method to disconnect from DB.
     */
    private void DBdisconnection() throws IOException {
        Configuration c = new Configuration();
        try {
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(FileHandler.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Given a text message file, returns a BTypeFile object if the message
     * shall be processed.
     *
     * @param messageText Content of a B-Type message file.
     * @return A BTypeFile object if messageText is a MVT message, otherwise
     * NULL.
     */
    private BTypeFile processBTypeFile(String messageText) {
        // BTypeFile object creation.
        BTypeFile btf = new BTypeFile();
        // initialization of variables
        String header = "";
        String priority = "";
        String destinationtypeb = "";
        String origin = "";
        String dblsig = "";
        String msgid = "";
        String subject = "";
        String smi = "";
        String faxheader = "";
        String text = "";
        String attachements = "";
        // Divide the string message into substrings starting with '=' sign.
        String[] tokens = messageText.split("\\=");
        // bft object properties assignment
        for (String s : tokens) {
            if (s.startsWith("HEADER")) {
                btf.setHeader(s.substring(6, s.length()));
            }
            if (s.startsWith("PRIORITY")) {
                btf.setPriority(s.substring(8, s.length()));
            }
            if (s.startsWith("DESTINATION TYPE B")) {
                btf.setDestinationTypeB(s.substring(18, s.length()));
            }
            if (s.startsWith("ORIGIN")) {
                btf.setOrigin(s.substring(6, s.length()));
            }
            if (s.startsWith("DBLSIG")) {
                btf.setDblsig(s.substring(6, s.length()));
            }
            if (s.startsWith("MSGID")) {
                btf.setMsgid(s.substring(5, s.length()));
            }
            if (s.startsWith("SUBJECT")) {
                btf.setSubject(s.substring(7, s.length()));
            }
            if (s.startsWith("SMI")) {
                btf.setSmi(s.substring(3, s.length()));
            }
            if (s.startsWith("FAX HEADER")) {
                btf.setFaxHeader(s.substring(10, s.length()));
            }
            if (s.startsWith("TEXT")) {
                btf.setText(s.substring(4, s.length()));
            }
            if (s.startsWith("ATTACHMENTS")) {
                btf.setAttachments(s.substring(12, s.length()));
            }
        }
        return btf;
    }

    /**
     * Inserts a new record into DB if the SMI - message identifier - is one of
     * LDM, MVT or PSM
     *
     * @param fileContent Message body in string format.
     * @throws UnsupportedEncodingException
     */
    private void DBinsertData(String fileContent) throws UnsupportedEncodingException, IOException {
        Configuration c = new Configuration();

        BTypeFile b;

        b = processBTypeFile(fileContent);

        //if ((b.getSmi().equals("MVT")) || (b.getSmi().equals("LDM")) || (b.getSmi().equals("PSM"))) {
        if ((b.getSmi().equals("MVT")) || (b.getSmi().equals("PSM")) || (b.getSmi().equals("LDM")) || (b.getSmi().equals("CPM"))) {

            try {
                PreparedStatement stmt = null;
                //MESSAGES>>SQL // MESSAGE>>LOCAL
                stmt = con.prepareStatement("INSERT INTO tb_messages (HEADER) VALUES (1)");
                
                //stmt = con.prepareStatement("INSERT INTO tb_messages (HEADER,PRIORITY,DESTINATIONTYPEB,ORIGIN,DBLSIG,MSGID,SUBJECT,SMI,FAXHEADER,TEX,ATTACHMENTS,TEX_FLT,TEX_DATE,TEX_EA,TEX_TOTALPAX,TEX_IN_WCHR,TEX_IN_WCHS,TEX_IN_WCHC,TEX_REG) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                stmt.setString(1, b.getHeader());
                stmt.setString(2, b.getPriority());
                stmt.setString(3, b.getDestinationTypeB());
                stmt.setString(4, b.getOrigin());
                stmt.setString(5, b.getDblsig());
                stmt.setString(6, b.getMsgid());
                stmt.setString(7, b.getSubject());
                stmt.setString(8, b.getSmi());
                stmt.setString(9, b.getFaxHeader());
                stmt.setString(10, b.getText());
                stmt.setString(11, b.getAttachments());
                if (b.getSmi().equals("MVT")) {
                    MvtAnalyzer mvt = new MvtAnalyzer(b.getText());
                    stmt.setString(12, mvt.getFlightNumber());
                    stmt.setString(13, mvt.getFlightDate());
                    stmt.setString(14, mvt.getEa());
                    int pxx = mvt.getPx();
                    String pxxx = Integer.toString(pxx);
                    stmt.setString(15, pxxx);
                    stmt.setString(16, null);
                    stmt.setString(17, null);
                    stmt.setString(18, null);
                    stmt.setString(19, mvt.getFlightRegistration());
                    // At least flight EA and PX fields are required
                    if ((!mvt.getEa().equals("")) && (mvt.getPx() >= 0)) {
                        System.out.println("Mensaje para vuelo añadido: " + mvt.getFlightNumber() + "\n");
                        stmt.executeUpdate();
                    } else {
                        System.out.println("Mensaje para vuelo ignorado: " + mvt.getFlightNumber() + "\n");
                    }
                }
                if (b.getSmi().equals("PSM")) {
                    PsmAnalyzer psm = new PsmAnalyzer(b.getText());
                    stmt.setString(12, psm.getFlightNumber());
                    stmt.setString(13, psm.getFlightDate());
                    stmt.setString(14, null);
                    stmt.setString(15, null);
                    int wcr, wcs, wcc;
                    wcr = psm.getWchr();
                    wcs = psm.getWchs();
                    wcc = psm.getWchc();
                    stmt.setString(16, Integer.toString(wcr));
                    stmt.setString(17, Integer.toString(wcs));
                    stmt.setString(18, Integer.toString(wcc));
                    stmt.setString(19, null);
                    if ((!psm.getFlightNumber().equals(""))) {
                        System.out.println("Mensaje para vuelo añadido: " + psm.getFlightNumber() + "\n");
                        stmt.executeUpdate();
                    } else {
                        System.out.println("Mensaje para vuelo ignorado: " + psm.getFlightNumber() + "\n");
                    }
                }
                if (b.getSmi().equals("LDM")) {
                    LdmAnalyzer ldm = new LdmAnalyzer(b.getText());
                    stmt.setString(12, ldm.getFlightNumber());
                    stmt.setString(13, ldm.getFlightDate());
                    stmt.setString(14, null);
                    stmt.setString(15, null);
                    stmt.setString(16, null);
                    stmt.setString(17, null);
                    stmt.setString(18, null);
                    stmt.setString(19, null);
                    if ((!ldm.getFlightNumber().equals(""))) {
                        System.out.println("Mensaje para vuelo añadido: " + ldm.getFlightNumber() + "\n");
                        stmt.executeUpdate();
                    } else {
                        System.out.println("Mensaje para vuelo ignorado: " + ldm.getFlightNumber() + "\n");
                    }
                }
                if (b.getSmi().equals("CPM")) {
                    CpmAnalyzer cpm = new CpmAnalyzer(b.getText());
                    stmt.setString(12, cpm.getFlightNumber());
                    stmt.setString(13, cpm.getFlightDate());
                    stmt.setString(14, null);
                    stmt.setString(15, null);
                    stmt.setString(16, null);
                    stmt.setString(17, null);
                    stmt.setString(18, null);
                    stmt.setString(19, null);
                    if ((!cpm.getFlightNumber().equals(""))) {
                        System.out.println("Mensaje para vuelo añadido: " + cpm.getFlightNumber() + "\n");
                        stmt.executeUpdate();
                    } else {
                        System.out.println("Mensaje para vuelo ignorado: " + cpm.getFlightNumber() + "\n");
                    }
                }
                System.out.println(stmt);
                stmt.close();

            } catch (SQLException ex) {
                Logger.getLogger(FileHandler.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Receives a list of files which are inserted into the DB after being
     * validated.
     *
     * @param filesArray List of Files to be processed.
     * @throws SQLException
     * @throws java.io.UnsupportedEncodingException
     * @throws javax.naming.NamingException
     */
    public void processFileList(ArrayList<String> filesArray) throws SQLException, UnsupportedEncodingException, IOException, NamingException {
        // Connection to LoaderDB
        this.DBconnection();
        // Injection into SQL
        for (String message : filesArray) {
            DBinsertData(message);
        }
        // Disconnection from LoaderDB
        this.DBdisconnection();
    }
}
