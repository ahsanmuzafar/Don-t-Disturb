package com.example.ahsanmuzafar.autoaudioprofilechanger;

import android.provider.BaseColumns;

/**
 * Created by ahsan.muzafar on 6/14/2015.
 */
public abstract class SQLContract {
    public static abstract class tables {
        public static abstract class Onetable implements BaseColumns {
            public static final String TABLE_NAME = "Settings";
            public static final String COLUMN_SMSCheckbox = "SMSCheckBox";
            public static final String COLUMN_SMSTEXT = "SMSText";
            public static final String COLUMN_AFTERMISSCALL = "AfterMissCall";
            public static final String COLUMN_COUNT  = "Count";
            public static final String COLUMN_TIMETOCHANGE = "TimeToChange";
        }
    }
    public static abstract class commands{
           public static final String CreateTable = "Create Table "+ tables.Onetable.TABLE_NAME +" ( "+
                tables.Onetable._ID + " TEXT PRIMARY KEY , " +
                tables.Onetable.COLUMN_SMSCheckbox + " TEXT , " +
                tables.Onetable.COLUMN_SMSTEXT + " TEXT , " +
                tables.Onetable.COLUMN_AFTERMISSCALL + " TEXT , " +
                tables.Onetable.COLUMN_COUNT +" TEXT , " +
                tables.Onetable.COLUMN_TIMETOCHANGE + " TEXT " +" )";

        }

    }
