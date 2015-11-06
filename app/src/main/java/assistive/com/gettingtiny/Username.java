package assistive.com.gettingtiny;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;


public class Username extends Activity {
    private NumberPicker np;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username);
        np = (NumberPicker) findViewById(R.id.numberPicker);
        np.setMinValue(0);
        np.setMaxValue(3);
        np.setDisplayedValues(new String[]{"Full", "Large", "Medium", "Small"});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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


    public void start(View v) {

        String name = ((EditText) findViewById(R.id.name)).getText().toString();
        Intent i = new Intent(this, Testing.class);
        i.putExtra("user",name);
        i.putExtra("keyboard",np.getValue());

        startActivity(i);
        finish();

       // ((EditText) findViewById(R.id.name)).setText(System.currentTimeMillis() + "", TextView.BufferType.EDITABLE);
    }

}
