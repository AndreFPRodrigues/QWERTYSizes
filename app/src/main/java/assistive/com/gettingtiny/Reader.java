package assistive.com.gettingtiny;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

/**
 * Created by andre on 23-Jun-15.
 */
public class Reader {
    private TextToSpeech ttobj;
    private String phrase;
    private String lastLetter;

    //braille line codes
    private final static int LEFT = 1;
    private final static int RIGHT = 2;
    private final static int DOUBLE = 3;
    private final static int EMPTY = 0;
    private final static int NEXT_CHAR = 4;

    private long lastWrite;
    private final long FLUSH_THRESHOLD=500;

    public Reader(Context c){
        phrase="";
        lastLetter="";
        ttobj=new TextToSpeech(c,
                new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if(status != TextToSpeech.ERROR){
                            ttobj.setLanguage(Locale.getDefault());
                        }
                    }
                });

    }

    public String getLetter(String message){
            int type = TextToSpeech.QUEUE_FLUSH;
            if(lastWrite-System.currentTimeMillis()>FLUSH_THRESHOLD)
                type=TextToSpeech.QUEUE_ADD;
            //ttobj.speak(message,type , null);
            lastLetter=message;
        readString(lastLetter, TextToSpeech.QUEUE_FLUSH);

            return lastLetter;
    }

    public void readString(String text, int type){
        switch(text){
            case "k":
                ttobj.speak("kapa", type, null);
                break;
            case "o":
                ttobj.speak("oh",type, null);
                break;
            case "e":
                ttobj.speak("ee", type, null);
                break;
            case "m":
                ttobj.speak("eme", type, null);
                break;
            case "n":
                ttobj.speak("ene", type, null);
                break;
            case "caixa de texto":
                lastLetter = "";
                break;
            default:
                ttobj.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                break;
        }
    }

    public String writeLetter(){
            lastWrite = System.currentTimeMillis();
            readString(lastLetter,TextToSpeech.QUEUE_FLUSH );

            if(lastLetter.equals("espasso")) {
                lastLetter = " ";
                String [] toRead = phrase.split(" ");
                if(toRead.length>0)
                    readString(toRead[toRead.length-1], TextToSpeech.QUEUE_FLUSH);

            }
            if(lastLetter.equals("apagar")){
                lastLetter="<<";
                if(phrase.length()>0) {
                    String toRead = phrase.substring(phrase.length() - 1,phrase.length());
                    if(toRead.equals(" "))
                        toRead="espasso";
                    readString("apagou "+toRead , TextToSpeech.QUEUE_FLUSH);
                    phrase = phrase.substring(0, phrase.length() - 1);
                }
            }else {
                phrase += lastLetter;
            }
            return lastLetter;

    }
    public String getPhrase(){
        return phrase;

    }



    public String lastRead() {
        return lastLetter;
    }

    public void clear(){
        phrase="";
        lastLetter="";
    }

    public String decodeLog(String message) {
        int type =Integer.parseInt(message);
        switch (type){
            case LEFT:
                return "LEFT";
            case RIGHT:
               return "RIGHT";
            case DOUBLE:
                return "DOUBLE";
            case EMPTY:
                return "EMPTY";
            case NEXT_CHAR:
                return "NEXT_CHAR";
        }
        return null;
    }

    public void stop() {
        if(ttobj!=null) {
            ttobj.stop();
            ttobj.shutdown();
        }

    }
}
