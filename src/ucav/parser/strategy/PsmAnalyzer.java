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
public final class PsmAnalyzer implements Analyzer {

    private String flightNumber = new String();
    private String flightDate = new String();
    private String correction = new String();
    private String flightOrigin = new String();
    private String type = new String();
    private String message = new String();
    private int wchr = -1;
    private int wchs = -1;
    private int wchc = -1;

    @Override
    public void Analyze(String message) {
        this.PsmAnalyze(message);
    }

    public PsmAnalyzer() {
    }

    public void PsmAnalyze(String m) {

        flightNumber = "";
        flightDate = "";
        correction = "";
        flightOrigin = "";
        type = "";

        this.message = m;

        // Displays the message body in the command line.
        String m2display;
        m2display = m;
        m2display = m2display.replace("<br>", "\n");
        System.out.println(">>Nuevo mensaje PSM detectado:\n\n" + m2display + "\n");

        this.findFlight(message);
        if (!this.flightNumber.equals("-1")) {
            this.findFlightDate(message);

            if (!this.flightDate.equals("-1")) {
                this.findWch(message);
            }
        }
    }

    /**
     * Looks for the fligh number in the message.
     *
     * @param m MVT message body as text.
     */
    public void findFlight(String m) {
        // A message can start with {null | COR MVT | PDM COR MVT | PDM MVT}
        if (m.length() == 0) {
            this.flightNumber = "-1";
        } else {
            if (m.startsWith("COR<br>PSM<br>")) {
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
            } else if (m.startsWith("PDM<br>COR<br>PSM<br>")) {
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
            } else if (m.startsWith("PDM<br>PSM<br>")) {
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
            char x = m.charAt(i);
            while (isaNumber(x)) {
                date.append(x);
                i++;
                x = m.charAt(i);
            }
            this.setFlightDate(year + "-" + month + "-" + date.toString() + " 00:00:00");
        }
    }

    private Boolean isaNumber(char c) {
        if (c == '0' || c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6' || c == '7' || c == '8' || c == '9') {
            return true;
        } else {
            return false;
        }
    }

    public int getWchr() {
        return wchr;
    }

    public void setWchr(int wchr) {
        this.wchr = wchr;
    }

    public int getWchs() {
        return wchs;
    }

    public void setWchs(int wchs) {
        this.wchs = wchs;
    }

    public int getWchc() {
        return wchc;
    }

    public void setWchc(int wchc) {
        this.wchc = wchc;
    }

    private void findWch(String m) {
        String chunks[] = new String[]{};
        chunks = m.split(" ");
        for (String s : chunks) {
            s = s.trim();
            if (s.contains("WCHR")) {
                this.wchr++;
            }
            if (s.contains("WCHS")) {
                this.wchs++;
            }
            if (s.contains("WCHC")) {
                this.wchc++;
            }
        }
        if (this.wchr == -1) {
            this.wchr = 0;
        }
        if (this.wchs == -1) {
            this.wchs = 0;
        }
        if (this.wchc == -1) {
            this.wchc = 0;
        }
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
    public String getEa() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getPx() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getFlightRegistration() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
