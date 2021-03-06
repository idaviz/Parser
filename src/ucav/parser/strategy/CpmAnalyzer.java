/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucav.parser.strategy;

import java.util.Calendar;

/**
 * This package analyzes the content of a MVT message.
 *
 * @author dgarcia25
 */
public final class CpmAnalyzer implements Analyzer{

    private String flightNumber = new String();
    private String flightDate = new String();
    private String flightRegistration = new String();
    private String correction = new String();
    private String flightOrigin = new String();
    private String type = new String();
    private String message = new String();

    @Override
    public void Analyze(String message) {
        this.CpmAnalyze(message);
    }
    
    public CpmAnalyzer() {
    }

    /**
     *
     * @param m
     */
    private void CpmAnalyze(String m) {
        flightNumber = "";
        flightDate = "";
        flightRegistration = "";
        correction = "";
        flightOrigin = "";
        type = "";
        this.message = m;

        // Displays the message body in the command line.
        String m2display;
        m2display = m;
        m2display = m2display.replace("<br>", "\n");
        System.out.println(">>Nuevo mensaje CPM detectado:\n\n" + m2display + "\n");

        this.findFlight(message);
        if (!this.flightNumber.equals("-1")) {
            this.findFlightDate(message);
        }
    }

    /**
     * Looks for the fligh number in the message.
     *
     * @param m CPM message body as text.
     */
    public void findFlight(String m) {
        // A message can start with {null | COR CPM | PDM COR CPM | PDM CPM}
        if (m.length() == 0) {
            this.flightNumber = "-1";
        } else {
            if (m.startsWith("COR<br>CPM<br>")) {
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
            } else if (m.startsWith("CPM<br>")) {
                message = message.substring(7);
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
            } else if (m.startsWith("PDM<br>COR<br>CPM<br>")) {
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
            } else if (m.startsWith("PDM<br>CPM<br>")) {
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
                char x;
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
     * to the field #FlightDate.
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
  
    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    @Override
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

    @Override
    public String getEa() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getPx() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
}