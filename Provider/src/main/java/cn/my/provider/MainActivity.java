package cn.my.provider;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "dlog";

    EditText name, grade;
    Button add, retrieve;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        name = findViewById(R.id.name);
        grade = findViewById(R.id.grade);
        add = findViewById(R.id.add);
        retrieve = findViewById(R.id.retrieve);
    }

    public void add(View view) {
        ContentValues values = new ContentValues();
        values.put(MyContentProvider.NAME, name.getText().toString());
        values.put(MyContentProvider.GRADE, grade.getText().toString());

        Uri uri = getContentResolver().insert(MyContentProvider.CONTENT_URI, values);
        if (uri != null) {
            Toast.makeText(getBaseContext(), "Record added", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getBaseContext(), "Failed to add record", Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint("Recycle")
    public void Retrieve(View view) {
        String URL = String.format("content://%s/students", this.getPackageName());
        Uri students = Uri.parse(URL);
        Cursor c = getContentResolver().query(students, null, null, null, "name");

        if (c != null && c.moveToFirst()) {
            do {
                @SuppressLint("Range") String msg = String.format("%s,%s,%s",
                        c.getString(c.getColumnIndex(MyContentProvider.ID)),
                        c.getString(c.getColumnIndex(MyContentProvider.NAME)),
                        c.getString(c.getColumnIndex(MyContentProvider.GRADE)));
                Log.d(TAG, msg);
            } while (c.moveToNext());
        }
    }
}