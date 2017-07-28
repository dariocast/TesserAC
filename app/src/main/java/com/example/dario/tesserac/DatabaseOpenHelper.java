package com.example.dario.tesserac;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseOpenHelper extends SQLiteOpenHelper {
	
	final static String[] columns = {
			SchemaDB.Tessera._ID,
			SchemaDB.Tessera.COLUMN_NOME,
			SchemaDB.Tessera.COLUMN_COGNOME,
			SchemaDB.Tessera.COLUMN_DATA,
			SchemaDB.Tessera.COLUMN_COMUNE,
			SchemaDB.Tessera.COLUMN_INDIRIZZO,
			SchemaDB.Tessera.COLUMN_EURO,
			SchemaDB.Tessera.COLUMN_PARENTE,
			SchemaDB.Tessera.COLUMN_MAIL,
	};

	final private static String CREATE_CMD =
			"CREATE TABLE "+SchemaDB.Tessera.TABLE_NAME+" ("
			+ SchemaDB.Tessera._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ SchemaDB.Tessera.COLUMN_NOME + " TEXT NOT NULL, "
			+ SchemaDB.Tessera.COLUMN_COGNOME + " TEXT NOT NULL, "
			+ SchemaDB.Tessera.COLUMN_DATA + " TEXT, "
			+ SchemaDB.Tessera.COLUMN_COMUNE + " TEXT, "
			+ SchemaDB.Tessera.COLUMN_INDIRIZZO + " TEXT, "
			+ SchemaDB.Tessera.COLUMN_EURO + " TEXT, "
			+ SchemaDB.Tessera.COLUMN_PARENTE + " TEXT, "
			+ SchemaDB.Tessera.COLUMN_MAIL + " TEXT); ";

	final private static Integer VERSION = 1;
	final private Context context;

	public DatabaseOpenHelper(Context context) {
		super(context, SchemaDB.Tessera.TABLE_NAME, null, VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_CMD);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// non serve in questo esempio, ma deve esserci
	}

	//Questo metodo serve per cancellare il database
	//Non viene usato in questo esempio
	void deleteDatabase() {
		Log.d("DEBUG","Deleting database "+ SchemaDB.Tessera.TABLE_NAME);
		context.deleteDatabase(SchemaDB.Tessera.TABLE_NAME);
	}
}
