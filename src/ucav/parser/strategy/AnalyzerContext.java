/*
 * Created by me and myself.
 */
package ucav.parser.strategy;

/**
 *
 * @author David
 */
public class AnalyzerContext {

    private Analyzer analyzer;

    public void setAnalyzer(Analyzer analyzer) {
        this.analyzer = analyzer;
    }
    
    public void analyzeMessage(String m){
        analyzer.Analyze(m);
    }

    /**
     *
     * @return
     */
    public String getFlightNumber(){
        return analyzer.getFlightNumber();
    }
    
    /**
     *
     * @return
     */
    public String getFlightDate(){
        return analyzer.getFlightDate();
    }

    public int getWchr(){
        return analyzer.getWchr();
    }
    
     public int getWchs(){
        return analyzer.getWchs();
    }
     
      public int getWchc(){
        return analyzer.getWchc();
    }
      
    public int getPx(){
        return analyzer.getPx();
    }
    
    public String getEa(){
        return analyzer.getEa();
    }
    
    public String getFlightRegistration(){
        return analyzer.getFlightRegistration();
    }
}

