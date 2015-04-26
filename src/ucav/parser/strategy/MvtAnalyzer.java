/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucav.parser.strategy;

import java.io.IOException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import parser.Configuration;

/**
 * This package analyzes the content of a MVT message.
 *
 * @author dgarcia25
 */
public final class MvtAnalyzer implements Analyzer {
    
    private String flightNumber = new String();
    private String flightDate = new String();
    private String flightRegistration = new String();
    private String correction = new String();
    private String flightOrigin = new String();
    private String px = new String();
    private String ea = new String();
    private String message = new String();
    
    @Override
    public void Analyze(String message) {
        try {
            this.MvtAnalyze(message);
        } catch (IOException ex) {
            Logger.getLogger(MvtAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public MvtAnalyzer() {
    }
    
    public void MvtAnalyze(String m) throws IOException {
        flightNumber = "";
        flightDate = "";
        flightRegistration = "";
        correction = "";
        flightOrigin = "";
        px = "-1";
        ea = "";
        
        this.message = m;

        // Displays the message body in the command line.
        String m2display;
        m2display = m;
        m2display = m2display.replace("<br>", "\n");
        System.out.println(">>Nuevo mensaje MVT detectado:\n\n" + m2display + "\n");
        
        this.findFlight(message);
        if (!this.flightNumber.equals("-1")) {
            this.findFlightDate(message);
            if (!this.flightDate.equals("-1")) {
                this.findFlightRegistration(message);
                if (!this.flightRegistration.equals("-1")) {
                    this.findEa(message);
                    if (!this.ea.equals("-1")) {
                        this.findPx(message);
                    }
                }
            }
        }
    }

    /**
     * Looks for the fligh number in the message and stores it into flightNumber
     * atrribute. If a valid flight number is not found, then -1 is returned.
     *
     * @param m MVT message body as text.
     */
    public void findFlight(String m) {
        // A message can start with {null | COR MVT | PDM COR MVT | PDM MVT}
        if (m.length() == 0) {
            this.flightNumber = "-1";
        } else {
            if (m.startsWith("COR<br>MVT<br>")) {
                message = message.substring(14);
                StringBuilder flt = new StringBuilder();
                int i = 0;
                char x;
                while (message.charAt(i) != '/' && i < message.length()) {
                    flt.append(message.charAt(i));
                    i++;
                }
                // Store the flight number
                this.setFlightNumber(flt.toString());
                message = message.substring(i);
            } else if (m.startsWith("PDM<br>COR<br>MVT<br>")) {
                message = message.substring(21);
                StringBuilder flt = new StringBuilder();
                int i = 0;
                char x;
                while (message.charAt(i) != '/' && i < message.length()) {
                    flt.append(message.charAt(i));
                    i++;
                }
                // Store the flight number
                this.setFlightNumber(flt.toString());
                message = message.substring(i);
            } else if (m.startsWith("PDM<br>MVT<br>")) {
                message = message.substring(14);
                StringBuilder flt = new StringBuilder();
                int i = 0;
                char x;
                while (message.charAt(i) != '/' && i < message.length()) {
                    flt.append(message.charAt(i));
                    i++;
                }
                // Store the flight number
                this.setFlightNumber(flt.toString());
                message = message.substring(i);
            } else {
                StringBuilder flt = new StringBuilder();
                int i = 0;
                while (message.charAt(i) != '/' && i < message.length()) {
                    flt.append(message.charAt(i));
                    i++;
                    if (i == message.length()) {
                        this.flightNumber = "-1";
                        break;
                    }
                }
                // Store the flight number
                if (!this.flightNumber.equals("-1")) {
                    this.setFlightNumber(flt.toString());
                    message = message.substring(i);
                    
                }
            }
        }
    }

    /**
     * Looks for the flight date in the message. If found, it assigns the value
     * to the field #FlightDate otherwise returns -1.
     *
     * @param m MVT message body without Flight Number part.
     */
    private void findFlightDate(String m) {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1; // January is '0'
        // If a flight number exists, then I look for the date.
        if (!this.flightNumber.equals("-1")) {
            int i = 1;
            StringBuilder date = new StringBuilder();
            while (m.charAt(i) != '.' && i < m.length()) {
                date.append(m.charAt(i));
                i++;
                if (i == m.length()) {
                    this.flightDate = "-1";
                    break;
                }
            }
            if (!this.flightDate.equals("-1")) {
                this.setFlightDate(year + "-" + month + "-" + date.toString() + " 00:00:00");
            }
        }
    }
    
    private void findFlightRegistration(String m) {
        if (!this.flightDate.equals("-1")) {
            int i = 1;
            StringBuilder reg = new StringBuilder();
            // Skip initial part of the message LX2151/29DEC.
            while (m.charAt(i) != '.' && i < m.length()) {
                i++;
                if (i == m.length()) {
                    this.flightRegistration = "-1";
                    break;
                }
            }
            // now we expect to read reg pattern between two dots .HBIJS.
            i++;
            while (m.charAt(i) != '.' && i < m.length()) {
                reg.append(m.charAt(i));
                i++;
                if (i == m.length()) {
                    this.flightRegistration = "-1";
                    break;
                }
            }
            if (!this.flightRegistration.equals("-1")) {
                this.setFlightRegistration(reg.toString());
            }
        }
    }

    /**
     * Looks for the field EA in the message. If found, it assigns the value to
     * the field EA, if not, it assigns "" to field EA.
     *
     * @param m MVT message body without Flight Number part.
     */
    private void findEa(String m) throws IOException {
        Configuration c = new Configuration();
        
        int i = 0;
        String chunk;
        String eaTime;
        String eaHour = null, eaMinute = new String();
        Boolean found = false;
        // searh for EA item
        while ((i <= m.length() - 10) && (!found)) {
            chunk = m.substring(i, i + 10);
            if (chunk.startsWith("EA") && (chunk.endsWith(c.getAirport()))) {
                chunk = chunk.trim();
                eaHour = chunk.substring(2, 4);
                eaMinute = chunk.substring(4, 6);
                eaTime = chunk.substring(2, 6);
                found = true;
            }
            i++;
        }
        if (found) {
            eaTime = eaHour + ":" + eaMinute + ":00";
            this.setEa(eaTime);
        } else {
            this.setEa("");
        }
    }

    /**
     * Looks for the field PX in the message. If found, it assigns the value to
     * the field PX, if not, it assigns "" to field PX.
     *
     * @param m MVT message body without Flight Number part.
     */
    private void findPx(String m) {
        int i = 0;
        String pxNumber = "-1";
        Boolean found = false;
        String chunk;
        // search for PX item
        while (((i <= m.length() - 5)) && (!found)) {
            chunk = m.substring(i, i + 5);
            if (chunk.startsWith("PX")) {
                chunk = chunk.trim();
                pxNumber = chunk.substring(2, chunk.length());
                found = true;
            }
            i++;
        }
        if (found) {
            this.setPx(pxNumber);
        } else {
            this.setPx("-1");
        }
    }
    
    public String getEa() {
        return ea;
    }
    
    public void setEa(String ea) {
        this.ea = ea;
    }
    
    public int getPx() {
        int i;
        try {
            i = Integer.parseInt(px);
        } catch (NumberFormatException ex) {
            i = -1;
        }
        
        return i;
    }
    
    public void setPx(String px) {
        this.px = px;
    }
    
    public String getFlightNumber() {
        return flightNumber;
    }
    
    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }
    
    public String getFlightDate() {
        return flightDate;
    }
    
    public void setFlightDate(String flightDate) {
        this.flightDate = flightDate;
    }
    
    public String getFlightRegistration() {
        return flightRegistration;
    }
    
    public void setFlightRegistration(String flightRegistration) {
        this.flightRegistration = flightRegistration;
    }
    
    public String getCorrection() {
        return correction;
    }
    
    public void setCorrection(String correction) {
        this.correction = correction;
    }
    
    public String getFlightOrigin() {
        return flightOrigin;
    }
    
    public void setFlightOrigin(String flightOrigin) {
        this.flightOrigin = flightOrigin;
    }

    @Override
    public int getWchr() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getWchs() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getWchc() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
