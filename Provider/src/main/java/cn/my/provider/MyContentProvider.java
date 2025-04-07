package cn.my.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class MyContentProvider extends ContentProvider {

    static final String PROVIDER_NAME = "cn.my.provider";
    static final String URL = "content://" + PROVIDER_NAME + "/students";
    static final Uri CONTENT_URI = Uri.parse(URL);

    static final String ID = "id";
    static final String NAME = "name";
    static final String GRADE = "grade";

    private static final int STUDENTS = 1;
    private static final int STUDENT_ID = 2;

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "students", STUDENTS);
        uriMatcher.addURI(PROVIDER_NAME, "students/#", STUDENT_ID);
    }

    private SQLiteDatabase db;
    static final String DATABASE_NAME = "College";
    static final String STUDENTS_TABLE_NAME = "students";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_DB_TABLE =
            " CREATE TABLE " + STUDENTS_TABLE_NAME +
                    " (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " name TEXT NOT NULL, " +
                    " grade TEXT NOT NULL);";

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + STUDENTS_TABLE_NAME);
            onCreate(db);
        }
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        return (db != null);
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowID = db.insert(STUDENTS_TABLE_NAME, "", values);
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        switch (uriMatcher.match(uri)) {
            case STUDENTS:
                cursor = db.query(STUDENTS_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case STUDENT_ID:
                cursor = db.query(STUDENTS_TABLE_NAME, projection, ID + " = " + uri.getPathSegments().get(1), selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count;
        switch (uriMatcher.match(uri)) {
            case STUDENTS:
                count = db.update(STUDENTS_TABLE_NAME, values, selection, selectionArgs);
                break;
            case STUDENT_ID:
                count = db.update(STUDENTS_TABLE_NAME, values, ID + " = " + uri.getPathSegments().get(1) + (!selection.isEmpty() ? " AND (" + selection + ")" : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count;
        switch (uriMatcher.match(uri)) {
            case STUDENTS:
                count = db.delete(STUDENTS_TABLE_NAME, selection, selectionArgs);
                break;
            case STUDENT_ID:
                count = db.delete(STUDENTS_TABLE_NAME, ID + " = " + uri.getPathSegments().get(1) + (!selection.isEmpty() ? " AND (" + selection + ")" : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case STUDENTS:
                return "vnd.android.cursor.dir/vnd.example.students";
            case STUDENT_ID:
                return "vnd.android.cursor.item/vnd.example.students";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }
}