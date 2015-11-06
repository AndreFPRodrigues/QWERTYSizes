package assistive.com.gettingtiny;

import java.util.ArrayList;

/**
 * Created by andre on 20-Jan-15.
 */


public class Phrase {
    private ArrayList<CharEntered> chars;
    private ArrayList <Touch> touches;
    private String inputStream;
    private String result_phrase;
    private String target_phrase;
    private long init;
    private long end;



    public Phrase(String target_phrase){
        this.target_phrase=target_phrase;
        result_phrase ="";
        inputStream="";
        chars = new ArrayList<CharEntered>();
        touches = new ArrayList<Touch>();
    }

    public void addLetter(String s){
        chars.add(new CharEntered(s, System.currentTimeMillis()));
        inputStream+=s;
    }

    public void save(String phrase, long init, long end){
        this.result_phrase =phrase;
        this.init=init;
        this.end=end;
    }

    public long getInit() {
        return init;
    }

    public long getEnd() {
        return end;
    }

    public String getResult() {
        return result_phrase;
    }

    public String getInputStream() {
        return inputStream;
    }

    public ArrayList<CharEntered> getChars() {
        return chars;
    }

    public String getTarget() {
        return target_phrase;
    }

    public long getTotal() {
        return (end-init)>0?(end-init):0;
    }

    public void addTouch(Touch t) {
        touches.add(t);
    }

    public ArrayList<Touch> getTouches() {
        return touches;
    }

    public class CharEntered {
        String c;
        long timestamp;


        public long getTimestamp() {
            return timestamp;
        }

        public String getC() {
            return c;
        }

        public CharEntered(String c, long timestamp){
            this.c=c;
            this.timestamp=timestamp;
        }
    }
}

