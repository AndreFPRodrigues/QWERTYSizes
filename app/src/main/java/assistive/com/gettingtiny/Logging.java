package assistive.com.gettingtiny;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by andre on 03-Jun-15.
 */
public class Logging {

    private static final String INIT_FILE = "{\"Touches\":[";
    private final String END_SEQUENCE = "]},";
    private final String filepath = Environment.getExternalStorageDirectory().toString() + "/";
    private final String filepathEnd = "_io_qwerty.json";
    private final String TAG = "QWERTY";
    private ArrayList<Touch> touches;

    private String username;
    private String keyboard;
    private boolean adapt;

    private ArrayList<Phrase> phrasesLog;
    private long sessionStart=0;
    private long endPhrase = 0;
    private long initPhrase = 0;

    public Logging(String name) {
        touches = new ArrayList<Touch>();
        username = name;
        phrasesLog = new ArrayList<Phrase>();

    }

    public Logging(String name, int keyboard, boolean adapt) {
        touches = new ArrayList<Touch>();
        username = name;
        phrasesLog = new ArrayList<Phrase>();
        switch(keyboard) {
            case 0:
                this.keyboard = "HUGE";
                break;
            case 1:
                this.keyboard = "LARGE";
                break;
            case 2:
                this.keyboard = "MEDIUM";
                break;
            case 3:
                this.keyboard = "SMALL";
                break;
        }
        this.adapt=adapt;
    }

    public long addTouch(Touch t) {
        if (touches != null) {
            touches.add(t);
            if (initPhrase == 0) {
                initPhrase = System.currentTimeMillis();
                if(sessionStart==0)
                    sessionStart=initPhrase;
            }
            if (t.getType() == 1){
                endPhrase = System.currentTimeMillis();
                return endPhrase-initPhrase;
            }
        }
        return 0;
    }

    public void writeTouchesToFile() {
        if (touches.size() < 1) {
            return;
        }

        File file = new File(filepath + "/textEntry/"+ username + "_"+keyboard+ "_" + filepathEnd);
        boolean exists = file.exists();
        FileWriter fw;
        try {
            fw = new FileWriter(file, true);
            if (!exists) {
                fw.write(INIT_FILE);
            }
            boolean first = true;
            for (Touch t : touches) {
                if (first) {
                    fw.write(t.toJSON());
                    first = false;
                } else {
                    fw.write(" , " + t.toJSON());
                }
            }
            fw.write("]}");

            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        touches = new ArrayList<Touch>();
    }


    public void clear() {
        initPhrase = 0;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public void closeFile(Context c, int phraseNumber, boolean completed) {
        long totalTime = System.currentTimeMillis() - sessionStart;
        XMLHandler.saveToXml(c, phrasesLog, phraseNumber, totalTime, sessionStart, username, keyboard, completed);

       // writeTouchesToFile();
        for(int i=0; i<phrasesLog.size();i++){
            writeTouchesPerTrial(phrasesLog.get(i));

        }

    }

    private void writeTouchesPerTrial(Phrase phrase) {
        ArrayList <Touch> trialTouches = phrase.getTouches();
        if (trialTouches.size() < 1) {
            return;
        }

        File file = new File(filepath + "/textEntry/"+ username + "_"+keyboard+ "_" +phrase.getInit()+"_"+ filepathEnd);
        boolean exists = file.exists();
        FileWriter fw;
        try {
            fw = new FileWriter(file, true);
            if (!exists) {
                fw.write(INIT_FILE);
            }
            boolean first = true;
            for (Touch t : trialTouches) {
                if (first) {
                    fw.write(t.toJSON());
                    first = false;
                } else {
                    fw.write(" , " + t.toJSON());
                }
            }
            fw.write("]}");

            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setTargetPhrase(String phrase) {
        phrasesLog.add(new Phrase(phrase));
    }

    public long savePhrase(int index, String phrase) {
        Log.d(TAG, "SAVED" + phrase + " init:" + initPhrase + " end:" + endPhrase);
        if (index > -1 && index<phrasesLog.size()) {
            phrasesLog.get(index).save(phrase, initPhrase, endPhrase);
            initPhrase = 0;
            return phrasesLog.get(index).getTotal();
        }
       return 0;
    }

    public void addKeystroke(int index, String c) {
        if(index<phrasesLog.size())
            phrasesLog.get(index).addLetter(c);

    }


    public void init( boolean adapt) {

        this.adapt = adapt;
    }

    public void addTouchPhrase(int index, Touch t) {
        if (index > -1 && index<phrasesLog.size()) {
            phrasesLog.get(index).addTouch(t);
        }
    }
}
