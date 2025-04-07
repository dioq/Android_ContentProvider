package cn.my.consumer;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "get";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void Retrieve(View view) {
        String URL = "content://cn.my.provider/students";
        Uri students = Uri.parse(URL);
        Cursor c = getContentResolver().query(students, null, null, null, "name");

        Log.d(TAG, "go here");
        if (c != null && c.moveToFirst()) {
            do {
                @SuppressLint("Range") String msg = String.format("id:%s,name:%s,grade:%s\n",
                        c.getString(c.getColumnIndex("id")),
                        c.getString(c.getColumnIndex("name")),
                        c.getString(c.getColumnIndex("grade")));
                Log.d(TAG, msg);
            } while (c.moveToNext());
        } else {
            Log.d(TAG, "get data fail");
        }
    }
}