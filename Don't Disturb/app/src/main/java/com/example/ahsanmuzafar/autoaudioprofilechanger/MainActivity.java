package com.example.ahsanmuzafar.autoaudioprofilechanger;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

    public  AudioManager mobilemode;
    public Dialog d;
    public static  int frst=0;
    public boolean onresume=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        frst = 1;

        mobilemode = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        mobilemode.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

        SQLiteDatabase db = new MyopenHelper(this).getReadableDatabase();

        String table = SQLContract.tables.Onetable.TABLE_NAME;
        String[] columns = {SQLContract.tables.Onetable._ID,
                SQLContract.tables.Onetable.COLUMN_SMSCheckbox,
                SQLContract.tables.Onetable.COLUMN_SMSTEXT,
                SQLContract.tables.Onetable.COLUMN_AFTERMISSCALL,
                SQLContract.tables.Onetable.COLUMN_TIMETOCHANGE
        };
        String selection = null;
        String[] selectionArgs = null;
        String groupBy = null;
        String having = null;
        String orderBy = null;
        Cursor c = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
        int count = c.getCount();
        if (count == 0) {
            db.close();
            SQLiteDatabase defaultdb = new MyopenHelper(this).getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(SQLContract.tables.Onetable.COLUMN_SMSTEXT, "I am busy Call me After some time");
            cv.put(SQLContract.tables.Onetable.COLUMN_AFTERMISSCALL, "2");
            cv.put(SQLContract.tables.Onetable.COLUMN_TIMETOCHANGE, "1");
            cv.put(SQLContract.tables.Onetable.COLUMN_COUNT, "0");
            cv.put(SQLContract.tables.Onetable.COLUMN_SMSCheckbox, "true");
            defaultdb.insert(SQLContract.tables.Onetable.TABLE_NAME, null, cv);
            defaultdb.close();
        } else {
            db.close();
        }
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.rg);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                int id1 = R.id.rb1;
                int id2 = R.id.rb2;
                if (id1 == checkedId) {

                    mobilemode.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    Intent srce = new Intent(MainActivity.this, MyService.class);
                    stopService(srce);
                    frst = 1;
                } else if (id2 == checkedId) {

                    final Dialog d = new Dialog(MainActivity.this);
                    d.setContentView(R.layout.dialog);
                    d.setCanceledOnTouchOutside(false);
                    d.setTitle("Don't Disturb Mode");
                    if (frst == 1) {

                        d.show();

                        frst++;
                    }

                    if (onresume == true) {
                        d.show();
                    }
                    if (frst >= 2) {

                        onresume = true;
                    }
                    Button ok = (Button) d.findViewById(R.id.OK);
                    Button cancel = (Button) d.findViewById(R.id.Cancel);
                    CheckBox cb = (CheckBox) d.findViewById(R.id.checkBox);
                    cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked == true) {
                                EditText msg1 = (EditText) d.findViewById(R.id.msg_editText);
                                msg1.setText("I am busy,Call Later");
                            } else {
                                EditText msg1 = (EditText) d.findViewById(R.id.msg_editText);
                                msg1.setText(null);
                            }
                        }
                    });
                    EditText msg1 = (EditText) d.findViewById(R.id.msg_editText);
                    msg1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CheckBox cb1 = (CheckBox) d.findViewById(R.id.checkBox);
                            cb1.setChecked(true);
                        }
                    });
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean validation = false;
                            String msgtext = "I am busy, Call Later";
                            String checkboxtext = "false";
                            String misscallcounttext = "20";
                            String timertext = "240";


                            CheckBox cb = (CheckBox) d.findViewById(R.id.checkBox);
                            if (cb.isChecked()) {
                                validation = true;
                                checkboxtext = "true";
                                EditText msg = (EditText) d.findViewById(R.id.msg_editText);
                                if (!msg.getText().toString().equals("")) {
                                    msgtext = msg.getText().toString();
                                } else {
                                    msgtext = "I am busy Call Later";
                                }

                            } else {
                                checkboxtext = "false";
                                msgtext = "I am busy Call Later";
                            }

                            EditText misscallcount = (EditText) d.findViewById(R.id.MissCallcount_editText);
                            if (!misscallcount.getText().toString().equals("")) {
                                misscallcounttext = misscallcount.getText().toString();
                                validation = true;
                            } else {
                                misscallcounttext = "20";
                            }
                            EditText timer = (EditText) d.findViewById(R.id.timer_editText);
                            if (!timer.getText().toString().equals("")) {
                                timertext = timer.getText().toString();
                                validation = true;
                            } else {
                                timertext = "240";
                            }
                            if (validation == true) {
                                d.dismiss();
                                MainActivity.this.SetValuesToDB(checkboxtext, msgtext, misscallcounttext, timertext);
                                mobilemode.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                            } else {
                                // animation code here
                                onresume = false;
                                RadioGroup radioGroup = (RadioGroup) findViewById(R.id.rg);
                                radioGroup.check(R.id.rb1);
                                mobilemode.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                                d.dismiss();
                            }
                        }
                    });
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onresume = false;
                            RadioGroup radioGroup = (RadioGroup) findViewById(R.id.rg);
                            radioGroup.check(R.id.rb1);
                            mobilemode.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                            d.dismiss();
                        }
                    });
                }
            }
        });
    }
    private void SetValuesToDB(String checkbox,String msg,String misscallcount,String timer) {


        SQLiteDatabase db=new MyopenHelper(this).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SQLContract.tables.Onetable.COLUMN_SMSTEXT,msg);
        values.put(SQLContract.tables.Onetable.COLUMN_AFTERMISSCALL,misscallcount);
        values.put(SQLContract.tables.Onetable.COLUMN_TIMETOCHANGE, timer);
        values.put(SQLContract.tables.Onetable.COLUMN_COUNT, "0");
        values.put(SQLContract.tables.Onetable.COLUMN_SMSCheckbox,checkbox);
        int result= db.update(SQLContract.tables.Onetable.TABLE_NAME, values, null, null);
        Intent srce=new Intent(this,MyService.class);
        startService(srce);
        Toast toast = Toast.makeText(this, "Don't Disturb Mode Activated", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        LinearLayout linearLayout = (LinearLayout) toast.getView();
        TextView messageTextView = (TextView) linearLayout.getChildAt(0);
        messageTextView.setTextSize(15);

    }


    @Override
    public  void onResume(){
        super.onResume();
        this.onPause();
       // onresume=false;
        if(isMyServiceRunning(MyService.class)){
            RadioGroup radioGroup = (RadioGroup) findViewById(R.id.rg);
            radioGroup.check(R.id.rb2);
        }
        else
        {
            RadioGroup radioGroup = (RadioGroup) findViewById(R.id.rg);
            radioGroup.check(R.id.rb1);
        }

    }
    public  boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onStop(){
        super.onStop();
        this.onDestroy();

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
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        onresume=true;
        savedInstanceState.putBoolean("onresume", onresume);
        savedInstanceState.putInt("frst", frst);
    }
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        onresume= savedInstanceState.getBoolean("onresume");
        Toast.makeText(this,"On restore",Toast.LENGTH_LONG).show();
        frst=savedInstanceState.getInt("frst");

    }
}
