package com.example.memo;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Button;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    Button btnSave;
    Button btnDelete;
    ListView lvMemoList = null;
    int memoId = -1;
    int save_select = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);
        lvMemoList= findViewById(R.id.lvMemoList);

        memoListDisplay();

        lvMemoList.setOnItemClickListener(new ListItemClickListener());
    }
    public void onAddButtonClick(View view){

        EditText etTitle = findViewById(R.id.etTitle);
        etTitle.setText("new memo");
        EditText etNote = findViewById(R.id.etNote);
        etNote.setText("");
        btnSave.setEnabled(true);

        save_select = 0;

    }
    public void onSaveButtonClick(View view) {
        EditText etNote = findViewById(R.id.etNote);
        String note = etNote.getText().toString();

        EditText etTitle = findViewById(R.id.etTitle);
        String title = etTitle.getText().toString();

        DatabaseHelper helper = new DatabaseHelper(MainActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();
        if (save_select == 1) {
            try {
                String sqlDelete = "DELETE FROM notememo WHERE _id = ?";
                SQLiteStatement stmt = db.compileStatement(sqlDelete);
                stmt.bindLong(1,memoId);
                stmt.executeUpdateDelete();

                String sqlInsert = "INSERT INTO notememo (_id, name, note) VALUES (?, ?, ?)";
                stmt = db.compileStatement(sqlInsert);

                stmt.bindLong(1,memoId);
                stmt.bindString(2,title);
                stmt.bindString(3,note);

                stmt.executeInsert();

            } finally {
                db.close();
            }
        }
        else {
            try {
                String sqInsert = "INSERT INTO notememo (name, note) VALUES(?, ?)";
                SQLiteStatement stmt = db.compileStatement(sqInsert);

                stmt.bindString(1, title);
                stmt.bindString(2, note);

                stmt.executeInsert();
            } finally {
                db.close();
            }
        }
        etTitle.setText("");
        etNote.setText("");
        btnSave.setEnabled(false);
        btnDelete.setEnabled(false);

        memoListDisplay();
    }

    private void memoListDisplay(){

        DatabaseHelper helper = new DatabaseHelper(MainActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            String sql = "SELECT _id,name FROM notememo";
            Cursor cursor = db.rawQuery(sql,null);
            String[] from = {"name"};
            int[] to = {android.R.id.text1};
            SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(this,android.R.layout.simple_list_item_1,cursor,from,to,0);
            lvMemoList.setAdapter(simpleCursorAdapter);

        } finally {
            db.close();
        }
    }

    private class ListItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.i("debug","onItemClick position"+position+" id="+id);
            memoId = (int)id;
            save_select = 1;

            btnSave.setEnabled(true);
            btnDelete.setEnabled(true);

            DatabaseHelper helper = new DatabaseHelper(MainActivity.this);
            SQLiteDatabase db = helper.getReadableDatabase();
            try {
                String sql = "SELECT name, note FROM notememo WHERE _id = "+ memoId;
                Cursor cursor = db.rawQuery(sql,null);
                Log.i("debug","cursor:"+cursor.toString());
                String note = "";
                String title = "";
                while(cursor.moveToNext()){
                    int idxNote = cursor.getColumnIndex("note");
                    Log.i("debug","idxNote"+idxNote);
                    note = cursor.getString(idxNote);
                    Log.i("debug","idxNote"+idxNote+" note="+note);

                    int idxTitle = cursor.getColumnIndex("name");
                    Log.i("debug","idxTitle"+idxTitle);
                    title = cursor.getString(idxTitle);
                    Log.i("debug","idxTitle"+idxTitle+" title="+title);

                }
                EditText etNote = findViewById(R.id.etNote);
                etNote.setText(note);

                EditText etTitle = findViewById(R.id.etTitle);
                etTitle.setText(title);
            } finally {
                db.close();
            }
        }
    }
    public void onDeleteButtonClick(View view){

        DatabaseHelper helper = new DatabaseHelper(MainActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();

        try {
            String sqlDelete = "DELETE FROM notememo WHERE _id = ?";
            SQLiteStatement stmt = db.compileStatement(sqlDelete);
            stmt.bindLong(1,memoId);
            stmt.executeUpdateDelete();
        } finally {
            db.close();
        }
        EditText etNote = findViewById(R.id.etNote);
        EditText etTitle = findViewById(R.id.etTitle);
        etTitle.setText("");
        etNote.setText("");
        btnSave.setEnabled(false);
        btnDelete.setEnabled(false);

        memoListDisplay();
    }
}