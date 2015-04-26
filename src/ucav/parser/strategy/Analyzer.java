/*
 * Created by me and myself.
 */
package ucav.parser.strategy;

/**
 *
 * @author David
 */
public interface Analyzer {

    public void Analyze(String message);

    /**
     *
     * @return
     */
    public String getFlightNumber();

    /**
     *
     * @return
     */
    public String getFlightDate();
    
    /**
     *
     * @return
     */
    public int getWchr();

    /**
     *
     * @return
     */
    public int getWchs();

    /**
     *
     * @return
     */
    public int getWchc();
    
    /**
     *
     * @return
     */
    public String getEa();
    
    /**
     *
     * @return
     */
    public int getPx();
    
    /**
     *
     * @return
     */
    public String getFlightRegistration();
        
    
}
