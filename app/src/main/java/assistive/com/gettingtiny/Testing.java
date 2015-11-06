package assistive.com.gettingtiny;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


public class Testing extends FragmentActivity implements TestingFragment.TTSCallBack {
    public static final String IOLOG = "IOLog";
    public static final String TO_READ = "toRead";
    public static final String TO_WRITE = "toWrite";
    public static final String INIT = "init";
    public static final String LOG = "debug";
    private static final String TAG = "QWERTY";
    /**
     * Request code for launching the Intent to resolve Google Play services errors.
     */
    private static final int REQUEST_RESOLVE_ERROR = 1000;
    public static String ACTION_ZIPPED_FILES = "/logfile";
    private final int TIMEOUT = 600000;
    int keyboard;
    boolean adapt;
    private Handler mHandler;
    private TextView letter;
    private TextView phrase;
    private TextView target;
    private Button start;
    private Button next;
    private boolean mResolvingError = false;
    private String username;
    private Reader reader ;
    private StudyController stdc;
    private Logging log;
    private MediaPlayer mp;
    private MediaPlayer mp2;

    private Runnable sessionTimeout;

    private LinearLayout highlight;

    private long totalTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        letter = (TextView) findViewById(R.id.letter);
        phrase = (TextView) findViewById(R.id.phrase);
        target = (TextView) findViewById(R.id.target);
        start = (Button) findViewById(R.id.start);
        next = (Button) findViewById(R.id.next);
        reader = new Reader(getApplicationContext());
        stdc = new StudyController(this);
        Intent intent = getIntent();
        username = intent.getStringExtra("user");
        keyboard = intent.getIntExtra("keyboard", -1);
        mHandler = new Handler();

        mp = MediaPlayer.create(this, R.raw.click);
        mp2 = MediaPlayer.create(this, R.raw.empty);
   /* Log.d("teste","DESNSI:" +TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, 1,
            getResources().getDisplayMetrics()));*/
        sessionTimeout =new Runnable() {
            @Override
            public void run() {
                reader.readString("Sessão Terminada", TextToSpeech.QUEUE_FLUSH);
                int index = stdc.getPhraseIndex();
                log.savePhrase(index, phrase.getText().toString());
                log.closeFile(getApplicationContext(), stdc.getPhraseIndex(), false);
                finish();
            }
        };
        highlight= (LinearLayout) findViewById(R.id.ll1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {

        //Close the Text to Speech Library
        if (reader != null) {

            reader.stop();
        }
        super.onDestroy();

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {

        super.onStop();
    }


    private void write() {
        reader.writeLetter();
        phrase.setText(reader.getPhrase());
        if (log != null)
            log.addKeystroke(stdc.getPhraseIndex(), reader.lastRead());
    }


    private void startStudy() {
        highlight.setPadding(0,0,0,0);
        Log.d("teste", "Start");
        phrase.setText("");
        start.setVisibility(View.GONE);
        next.setVisibility(View.VISIBLE);
        log = new Logging(username, keyboard, adapt);
        next(null);
        reader.clear();

        //mHandler.postDelayed(sessionTimeout, TIMEOUT);
        totalTime=0;
    }

 /*   @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            next(null);
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }*/

    public void start(View v) {
        startStudy();
    }

    public void next(View v) {
        int index = stdc.getPhraseIndex();
        final String sentence = stdc.nextPhrase();
        if (sentence != null) {
            reader.readString(sentence, TextToSpeech.QUEUE_FLUSH);
            totalTime+=log.savePhrase(index, phrase.getText().toString());
            log.setTargetPhrase(sentence);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    target.setText(sentence);
                    phrase.setText("");
                    reader.clear();

                }
            });
            //clear(null);
        } else {
            if (index > 0) {
                log.savePhrase(index, phrase.getText().toString());
                log.closeFile(getApplicationContext(), stdc.getPhraseIndex(),true);
                finish();
            }
        }
    }


    @Override
    public void toSpeak(String type, String text) {
        final long time = System.currentTimeMillis();

        if (type.contains(TO_READ)) {
            final String read = reader.getLetter(text);
            if(read.equals(""))
                mp2.start();
                mp2.setVolume(1, 1);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    letter.setText(read);

                }
            });
        } else {
            if (type.contains(TO_WRITE)) {
                final String written = reader.getLetter(text);
                if (written.length() > 0)
                    mp.start();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        letter.setText(written);
                        write();
                    }
                });

            }
        }
    }

    @Override
    public void sendTouch(float x, float y, long eventTime, float pressure, float size, int action, String lastLetter) {
        if (log != null) {
            Touch t = new Touch(x, y, eventTime, pressure, size, action, lastLetter);
             long time = log.addTouch(t);
            checkTime(time);
            int index = stdc.getPhraseIndex();
            log.addTouchPhrase(index, t);
        }
    }

    private void checkTime(long time) {
        if(totalTime+time>TIMEOUT){
            reader.readString("Sessão Terminada", TextToSpeech.QUEUE_FLUSH);
            int index = stdc.getPhraseIndex();
            log.savePhrase(index, phrase.getText().toString());
            log.closeFile(getApplicationContext(), stdc.getPhraseIndex(), false);
            finish();
        }

    }

}
