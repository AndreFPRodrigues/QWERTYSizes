package assistive.com.gettingtiny;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


/**
 * A placeholder fragment containing a simple view.
 */
public class TestingFragment extends Fragment {

    private final int HUGE = 0;
    private final int LARGE = 1;
    private final int MEDIUM = 2;
    private final int SMALL = 3;

    private KeyboardQWERTY kq;
    private LinearLayout container2;
    private boolean create = true;
    private String lastLetter;
    private String phrase;
    private GestureDetectorCompat mDetector;
    private boolean adapt;
    private int keyboard;

    private boolean timeGuard;
    private long locked;
    private String lockedLetter;
    private final long LOCK_THRESHOLD=100;

    TTSCallBack mCallback;

    // Container Activity must implement this interface
    public interface TTSCallBack {
        public void toSpeak(String type, String text);
        public void sendTouch(float x, float y, long eventTime, float pressure, float size, int action, String lastLetter);


    }

    public TestingFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (TTSCallBack) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement TTSCallBack");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        keyboard = getActivity().getIntent().getExtras().getInt("keyboard");
        View v =  inflater.inflate(R.layout.fragment_keyboard_huge, container, false);

        switch(keyboard) {
            case 0:
                 v =  inflater.inflate(R.layout.fragment_keyboard_huge, container, false);
                break;
            case 1:
                v =  inflater.inflate(R.layout.fragment_keyboard_large, container, false);
                break;
            case 2:
                v =  inflater.inflate(R.layout.fragment_keyboard_medium, container, false);
                break;
            case 3:
                v =  inflater.inflate(R.layout.fragment_keyboard_small, container, false);
                break;
        }
        container2 = (LinearLayout) v.findViewById(R.id.ll0);

        container2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                //create bounds of all keys to check which letter is hovered
                if (create) {
                    create = false;
                    kq = new KeyboardQWERTY(container2, adapt);

                }
                if (mDetector==null||!mDetector.onTouchEvent(motionEvent)) {
                   // Log.d("teste", motionEvent.toString());


                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            lastLetter = kq.getCharacter((int) motionEvent.getRawX(), (int) motionEvent.getRawY());
                            mCallback.toSpeak(Testing.TO_READ, lastLetter);

                            locked = System.currentTimeMillis();
                            lockedLetter=lastLetter;

                            break;
                        case MotionEvent.ACTION_MOVE:
                            String letter = kq.getCharacter((int) motionEvent.getRawX(), (int) motionEvent.getRawY());
                            if (!lastLetter.equals(letter)) {

                                lockedLetter=lastLetter;
                                lastLetter = letter;
                                locked = System.currentTimeMillis();
                                //Log.d("teste", "Locked:" + lockedLetter + " current:" + lastLetter);

                                mCallback.toSpeak(Testing.TO_READ, lastLetter);
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            if (!lastLetter.equals("")) {

                                if(timeGuard &&(System.currentTimeMillis()-locked)<LOCK_THRESHOLD) {
                                    locked = System.currentTimeMillis();

                                    lastLetter=lockedLetter;
                                }

                                mCallback.toSpeak(Testing.TO_WRITE, lastLetter);

                                if (lastLetter.equals("espasso"))
                                    lastLetter = " ";
                                phrase += lastLetter;

                               // sl.sendMessage(getApplicationContext(), StudyListener.INIT, keyboard + "," + adapt);
                                kq.clearBounds();
                            }
                            break;
                    }
                   mCallback.sendTouch(motionEvent.getX(), motionEvent.getY(), motionEvent.getEventTime(), motionEvent.getPressure(), motionEvent.getSize(), motionEvent.getAction(),lastLetter);
                  }
                return true;
            }


        });

        return v;
    }



}
