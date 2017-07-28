package com.example.dario.tesserac;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Dario on 10/11/2016.
 */
public class DBQuery {
    private DBQuery() {

    }

    public static DBQuery instance;

    public static DBQuery getInstance() {
        if(instance==null)
            instance = new DBQuery();
        return instance;
    }

    public Cursor read(SQLiteDatabase db) {
        String[] projection = null;
        String sortOrder = null;
        String selection = null;
        String[] selectionArgs = null;

        Cursor cursor = db.query(
                SchemaDB.Tessera.TABLE_NAME,  // The table to query
                projection,                  // The columns to return
                selection,                   // The columns for the WHERE clause
                selectionArgs,               // The values for the WHERE clause
                null,                        // don't group the rows
                null,                        // don't filter by row groups
                sortOrder                    // The sort order
        );

        return cursor;
    }

    public Cursor findById(SQLiteDatabase db,int id) {
        String[] projection = null;
        String sortOrder = null;
        String selection = SchemaDB.Tessera._ID + "=?";
        String[] selectionArgs = new String[]{""+id};

        Cursor cursor = db.query(
                SchemaDB.Tessera.TABLE_NAME,  // The table to query
                projection,                  // The columns to return
                selection,                   // The columns for the WHERE clause
                selectionArgs,               // The values for the WHERE clause
                null,                        // don't group the rows
                null,                        // don't filter by row groups
                sortOrder                    // The sort order
        );

        return cursor;
    }

    public Cursor readAllEntries(SQLiteDatabase db) {
        String[] projection = {
                SchemaDB.Tessera._ID,
                SchemaDB.Tessera.COLUMN_NOME,
                SchemaDB.Tessera.COLUMN_COGNOME
        };
        String sortOrder = null;
        String selection = null;
        String[] selectionArgs = null;

        Cursor cursor = db.query(
                SchemaDB.Tessera.TABLE_NAME,  // The table to query
                projection,                  // The columns to return
                selection,                   // The columns for the WHERE clause
                selectionArgs,               // The values for the WHERE clause
                null,                        // don't group the rows
                null,                        // don't filter by row groups
                sortOrder                    // The sort order
        );

        return cursor;
    }
}
