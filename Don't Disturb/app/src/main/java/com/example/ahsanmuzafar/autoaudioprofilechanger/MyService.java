package com.example.ahsanmuzafar.autoaudioprofilechanger;

import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MyService extends Service {
    SQLiteDatabase db;
    public AudioManager mobilemode;
    TelephonyManager TelephonyMgr;
    public    String smscheckbox;
    public String smsText;
    public String AfterMissCalls;
    public int afterMissCalls;
    public String Count;
    public int count;
    public String time;
    public int timetoget;
    public Integer i;
    public MyService() {

        Log.d("Service", "Contructor");

    }
    @Override
    public void onCreate() {

        Log.d("Service","Created");
        super.onCreate();
    };
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        try {
            TelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            mobilemode = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        }catch (Exception e){
            Log.d("Serive ", "Not started");
        }
        db=new MyopenHelper(this).getReadableDatabase();
        Log.d("Service","Working");
        String table=SQLContract.tables.Onetable.TABLE_NAME;
        String []Columns={SQLContract.tables.Onetable.COLUMN_SMSCheckbox,SQLContract.tables.Onetable.COLUMN_SMSTEXT,SQLContract.tables.Onetable.COLUMN_AFTERMISSCALL,SQLContract.tables.Onetable.COLUMN_TIMETOCHANGE,SQLContract.tables.Onetable.COLUMN_COUNT};
        Cursor c=db.query(table, Columns, null, null, null, null, null);
        Log.d("Service", "after query");
        if(c.getCount()==0){
            Log.d("Service","self Stop");
            stopSelf();
        }
        c.moveToFirst();
        Log.d("Service", "move to first");
        smscheckbox=c.getString(0).toString();
        smsText=c.getString(1).toString();
        AfterMissCalls=c.getString(2).toString();
        i = Integer.parseInt(AfterMissCalls);
        afterMissCalls=i.intValue();
        time=c.getString(3).toString();
        i = Integer.parseInt(time);
        timetoget=i.intValue();
        long timetostop=timetoget*60000;
        Count=c.getString(4).toString();
        i = Integer.parseInt(Count);
        count=i.intValue();
        db.close();
        if(timetostop!=0) {
            new Handler().postAtTime(new Runnable() {
                @Override
                public void run() {
                    MyService.this.stopnow();
                }
            }, SystemClock.uptimeMillis() + timetostop);
        }

        TelephonyMgr.listen(new PhoneStateListener(){
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:
                        // CALL_STATE_RINGING
                       count=getUpdatedCount();
                        if (count >= afterMissCalls) {
                            MyService.this.stopnow();
                        } else {

                            String sent = "SMS_SENT";
                            PendingIntent sentPI = PendingIntent.getBroadcast(MyService.this, 0, new Intent(sent), 0);
                            SmsManager sms = SmsManager.getDefault();
                            sms.sendTextMessage(incomingNumber, null, smsText, sentPI, null);
                        }
                        count++;
                        updateCount(count);


                        break;
                    default:
                        break;
                }
            }
                public void updateCount(int count){

                    SQLiteDatabase db=new MyopenHelper(MyService.this).getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put(SQLContract.tables.Onetable.COLUMN_SMSTEXT,smsText);
                    values.put(SQLContract.tables.Onetable.COLUMN_AFTERMISSCALL,afterMissCalls);
                    values.put(SQLContract.tables.Onetable.COLUMN_TIMETOCHANGE, timetoget);
                    values.put(SQLContract.tables.Onetable.COLUMN_COUNT, count);
                    int result= db.update(SQLContract.tables.Onetable.TABLE_NAME, values, null, null);

                    db.close();

                }
                public int getUpdatedCount(){
                    SQLiteDatabase db=new MyopenHelper(MyService.this).getReadableDatabase();
                    String table=SQLContract.tables.Onetable.TABLE_NAME;
                    String []Columns={SQLContract.tables.Onetable.COLUMN_SMSCheckbox,SQLContract.tables.Onetable.COLUMN_SMSTEXT,SQLContract.tables.Onetable.COLUMN_AFTERMISSCALL,SQLContract.tables.Onetable.COLUMN_TIMETOCHANGE,SQLContract.tables.Onetable.COLUMN_COUNT};
                    Cursor c=db.query(table, Columns, null, null, null, null, null);
                    c.moveToFirst();
                    String Coun=c.getString(4).toString();
                    db.close();
                    Integer in = Integer.parseInt(Coun);
                    int coun = in.intValue();
                    return coun;


                }

        }, PhoneStateListener.LISTEN_CALL_STATE);

        //changing mobile mode

        mobilemode.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        return START_STICKY;
    }


    public  void stopnow(){
        this.onDestroy();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mobilemode.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        Toast toast = Toast.makeText(this, "Don't Disturb Mode Deactivated", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        LinearLayout linearLayout = (LinearLayout) toast.getView();
        TextView messageTextView = (TextView) linearLayout.getChildAt(0);
        messageTextView.setTextSize(15);
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }



}
