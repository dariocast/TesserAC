package com.example.dario.tesserac;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    public ListView listView;
    public EditText gruppo;
    public EditText settore;
    private SimpleCursorAdapter adapter;
    private SQLiteDatabase db = null;
    private DatabaseOpenHelper dbHelper;
    private DBQuery dbQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean success = saveExcelFile(getApplicationContext(),"Tessere.xls");
                if(success) {
                    Snackbar.make(view, "File correttamente salvato", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
                else {
                    Snackbar.make(view, "Inserisci Nome Gruppo e Settore", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
            }
        });

        FloatingActionButton fabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), NewTessera.class);
                startActivityForResult(i,1);
            }
        });

        // Create a new DatabaseHelper
        dbHelper = new DatabaseOpenHelper(getApplicationContext());

        // Get the underlying database for writing
        db = dbHelper.getWritableDatabase();

        dbQuery = DBQuery.getInstance();

        Cursor cursorAll = dbQuery.readAllEntries(db);

        adapter = new SimpleCursorAdapter(
                getApplicationContext(),    //context
                R.layout.list_layout,       //Layout della lista
                cursorAll,                  //Il cursore con i dati del database
                new String[]{DatabaseOpenHelper.columns[0],DatabaseOpenHelper.columns[1],DatabaseOpenHelper.columns[2]}, // String[] con i nomi  delle colonne database
                new int[]{R.id._id, R.id.elem_lista_nome, R.id.elem_lista_cognome}, //id dei campi nel layout
                0 //flags
        );

        listView = (ListView)findViewById(R.id.lista_tesserati);
        gruppo = (EditText) findViewById(R.id.et_gruppo);
        settore = (EditText) findViewById((R.id.et_settore));

        if(savedInstanceState!=null) {
            gruppo.setText(savedInstanceState.getString("gruppo"));
            settore.setText(savedInstanceState.getString("settore"));
        }


        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SQLiteCursor cursor = (SQLiteCursor) listView.getItemAtPosition(position);

                //Dati dell'elemento cliccato
                final int db_id = cursor.getInt(0);
                String nome = cursor.getString(1);
                String cognome = cursor.getString(2);


                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Cosa vuoi fare?");
                builder.setMessage("Tessera di " + nome + " " + cognome);
                builder.setPositiveButton("Modifica", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        Intent mod = new Intent(getApplicationContext(),NewTessera.class);
                        mod.putExtra("id",db_id);
                        startActivityForResult(mod,1);
                    }
                });
                builder.setNegativeButton("Cancella", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        db.delete(SchemaDB.Tessera.TABLE_NAME, SchemaDB.Tessera._ID + "=?", new String[]{"" + db_id});
                        updateLists();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();


                //Toast.makeText(getApplicationContext(),"Cancello il record " + db_id + "\nNome: " + nome + " - Cognome: " + cognome,Toast.LENGTH_LONG).show();

                // delete from database
                //db.delete(SchemaDB.Tessera.TABLE_NAME, SchemaDB.Tessera._ID + "=?", new String[]{"" + db_id});

                // Redisplay data
                updateLists();
            }

        });
    }

        @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 1) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                if(data.getBooleanExtra("flag", false))
                    aggiornaRecord(data.getExtras());
                else inserisciRecord(data.getExtras());
            }
        }
    }

    public void inserisciRecord(Bundle data) {

        String nome = (String) data.get("nome");
        String cognome = (String) data.get("cognome");
        String dataNascita = (String) data.get("data");
        String comune = (String) data.get("comune");
        String indirizzo = (String) data.get("indirizzo");
        String euro = (String) data.get("euro");
        String parente = (String) data.get("parente");
        String mail = (String) data.get("mail");

        if (nome.length() > 0 && cognome.length() > 0) {
            ContentValues values = new ContentValues();
            values.put(SchemaDB.Tessera.COLUMN_NOME, nome);
            values.put(SchemaDB.Tessera.COLUMN_COGNOME, cognome);
            values.put(SchemaDB.Tessera.COLUMN_DATA, dataNascita);
            values.put(SchemaDB.Tessera.COLUMN_COMUNE, comune);
            values.put(SchemaDB.Tessera.COLUMN_INDIRIZZO, indirizzo);
            values.put(SchemaDB.Tessera.COLUMN_EURO, euro);
            values.put(SchemaDB.Tessera.COLUMN_PARENTE, parente);
            values.put(SchemaDB.Tessera.COLUMN_MAIL, mail);

            db.insert(SchemaDB.Tessera.TABLE_NAME, null, values);
        } else {
            Toast.makeText(getApplicationContext(),
                    "Dati non validi!", Toast.LENGTH_LONG).show();
        }

        // Redisplay data
        updateLists();
    }

    public void aggiornaRecord(Bundle data) {
        String nome = (String) data.get("nome");
        String cognome = (String) data.get("cognome");
        String dataNascita = (String) data.get("data");
        String comune = (String) data.get("comune");
        String indirizzo = (String) data.get("indirizzo");
        String euro = (String) data.get("euro");
        String parente = (String) data.get("parente");
        String mail = (String) data.get("mail");
        String id = (String) data.get("id");

        if (nome.length() > 0 && cognome.length() > 0) {
            ContentValues values = new ContentValues();
            values.put(SchemaDB.Tessera.COLUMN_NOME, nome);
            values.put(SchemaDB.Tessera.COLUMN_COGNOME, cognome);
            values.put(SchemaDB.Tessera.COLUMN_DATA, dataNascita);
            values.put(SchemaDB.Tessera.COLUMN_COMUNE, comune);
            values.put(SchemaDB.Tessera.COLUMN_INDIRIZZO, indirizzo);
            values.put(SchemaDB.Tessera.COLUMN_EURO, euro);
            values.put(SchemaDB.Tessera.COLUMN_PARENTE, parente);
            values.put(SchemaDB.Tessera.COLUMN_MAIL, mail);

            String whereClause = "_id="+id;

            db.update(SchemaDB.Tessera.TABLE_NAME, values, whereClause,null);
        } else {
            Toast.makeText(getApplicationContext(),
                    "Dati non validi!", Toast.LENGTH_LONG).show();
        }

        // Redisplay data
        updateLists();
    }

    private void updateLists() {
        adapter.getCursor().requery();
        adapter.notifyDataSetChanged();
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
        if (id == R.id.tutorial) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private boolean saveExcelFile(Context context, String fileName) {

        // check if available and not read only
        /*if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            return false;
        }*/

        boolean success = false;

        //New Workbook
        Workbook wb = new HSSFWorkbook();

        Cell c = null;

        //Cell style for header row
        CellStyle cs = wb.createCellStyle();
        cs.setBorderBottom(CellStyle.BORDER_THIN);
        cs.setBorderTop(CellStyle.BORDER_THIN);
        cs.setBorderRight(CellStyle.BORDER_THIN);
        cs.setBorderLeft(CellStyle.BORDER_THIN);

        //New Sheet
        Sheet sheet1 = null;
        sheet1 = wb.createSheet("Tessere");
        /*sheet1.setDefaultColumnStyle(0,cs);
        sheet1.setDefaultColumnStyle(1,cs);
        sheet1.setDefaultColumnStyle(2,cs);
        sheet1.setDefaultColumnStyle(3,cs);
        sheet1.setDefaultColumnStyle(4,cs);
        sheet1.setDefaultColumnStyle(5,cs);
        sheet1.setDefaultColumnStyle(6,cs);
        sheet1.setDefaultColumnStyle(7,cs);*/

        // Generate column headings
        Row row = sheet1.createRow(0);
        row.createCell(0).setCellValue("Gruppo");
        row.getCell(0).setCellStyle(cs);
        String gr = gruppo.getText().toString();
        if(gr.length()==0)
            return false;
        row.createCell(1).setCellValue(gr);
        row.getCell(1).setCellStyle(cs);
        String sett = settore.getText().toString();
        if(sett.length()==0)
            return false;
        row.createCell(2).setCellValue("Settore:");
        row.getCell(2).setCellStyle(cs);
        row.createCell(3).setCellValue(sett);
        row.getCell(3).setCellStyle(cs);

        row = sheet1.createRow(1);
        c = row.createCell(0);
        c.setCellValue("Nome");
        c.setCellStyle(cs);

        c = row.createCell(1);
        c.setCellValue("Cognome");
        c.setCellStyle(cs);

        c = row.createCell(2);
        c.setCellValue("Data di Nascita");
        c.setCellStyle(cs);

        c = row.createCell(3);
        c.setCellValue("Comune di Nascita");
        c.setCellStyle(cs);

        c = row.createCell(4);
        c.setCellValue("Indirizzo");
        c.setCellStyle(cs);

        c = row.createCell(5);
        c.setCellValue("Euro");
        c.setCellStyle(cs);

        c = row.createCell(6);
        c.setCellValue("Parente");
        c.setCellStyle(cs);

        c = row.createCell(7);
        c.setCellValue("E-Mail");
        c.setCellStyle(cs);

        sheet1.setColumnWidth(0, (15 * 250));
        sheet1.setColumnWidth(1, (15 * 250));
        sheet1.setColumnWidth(2, (15 * 250));
        sheet1.setColumnWidth(3, (15 * 400));
        sheet1.setColumnWidth(4, (15 * 500));
        sheet1.setColumnWidth(5, (15 * 100));
        sheet1.setColumnWidth(6, (15 * 500));
        sheet1.setColumnWidth(7, (15 * 500));

        Cursor cursor = dbQuery.read(db);
        int riga = 2;
        if (cursor.moveToFirst()){
            while(!cursor.isAfterLast()){
                Row r = sheet1.createRow(riga);
                c = r.createCell(0);
                c.setCellValue(cursor.getString(cursor.getColumnIndex("nome")));
                c.setCellStyle(cs);
                c = r.createCell(1);
                c.setCellValue(cursor.getString(cursor.getColumnIndex("cognome")));
                c.setCellStyle(cs);
                c = r.createCell(2);
                c.setCellValue(cursor.getString(cursor.getColumnIndex("data")));
                c.setCellStyle(cs);
                c = r.createCell(3);
                c.setCellValue(cursor.getString(cursor.getColumnIndex("comune")));
                c.setCellStyle(cs);
                c = r.createCell(4);
                c.setCellValue(cursor.getString(cursor.getColumnIndex("indirizzo")));
                c.setCellStyle(cs);
                c = r.createCell(5);
                c.setCellValue(cursor.getString(cursor.getColumnIndex("euro")));
                c.setCellStyle(cs);
                c = r.createCell(6);
                c.setCellValue(cursor.getString(cursor.getColumnIndex("parente")));
                c.setCellStyle(cs);
                c = r.createCell(7);
                c.setCellValue(cursor.getString(cursor.getColumnIndex("mail")));
                c.setCellStyle(cs);

                riga++;

                cursor.moveToNext();
            }
        }
        cursor.close();

        // Create a path where we will place our List of objects on external storage
        File file = new File(context.getExternalFilesDir(null), fileName);
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(file);
            wb.write(os);
            Log.w("FileUtils", "Writing file" + file);
            success = true;
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + file, e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
        intentShareFile.setType("application/xls");
        intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+file.getPath()));

        startActivity(Intent.createChooser(intentShareFile, "Share File"));
        return success;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("gruppo",gruppo.getText().toString());
        outState.putString("settore",settore.getText().toString());
        super.onSaveInstanceState(outState);
    }

    public void info(MenuItem item) {
        Dialog dialog = new Dialog(this);
        dialog.setTitle("Informazioni");
        dialog.setContentView(R.layout.dialog);
        dialog.setCancelable(true);
        dialog.show();
    }

}
