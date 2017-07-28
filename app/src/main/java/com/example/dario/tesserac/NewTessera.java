package com.example.dario.tesserac;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Objects;

public class NewTessera extends AppCompatActivity {
    public EditText nome;
    public EditText cognome;
    public EditText data_g;
    public EditText data_m;
    public EditText data_a;
    public EditText comune;
    public EditText indirizzo;
    public EditText euro;
    public EditText parente;
    public EditText mail;
    public boolean flag = false;
    public int id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_tessera);
        nome = (EditText) findViewById(R.id.et_nome);
        cognome = (EditText) findViewById(R.id.et_cognome);
        data_g = (EditText) findViewById(R.id.et_data_g);
        data_m = (EditText) findViewById(R.id.et_data_m);
        data_a = (EditText) findViewById(R.id.et_data_a);
        comune = (EditText) findViewById(R.id.et_comune);
        indirizzo = (EditText) findViewById(R.id.et_indirizzo);
        euro = (EditText) findViewById(R.id.et_euro);
        parente = (EditText) findViewById(R.id.et_parente);
        mail = (EditText) findViewById(R.id.et_mail);

        Intent mod = getIntent();
        Bundle extras;
        if(mod.hasExtra("id")) {
            extras = mod.getExtras();
            id = (int) extras.get("id");
            DatabaseOpenHelper dbHelper = new DatabaseOpenHelper(getApplicationContext());

            // Get the underlying database for writing
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            DBQuery dbQuery = DBQuery.getInstance();

            Cursor c = dbQuery.findById(db,id);
            if (c.moveToFirst()){
                nome.setText(c.getString(c.getColumnIndex("nome")));
                cognome.setText(c.getString(c.getColumnIndex("cognome")));
                String data[] = c.getString(c.getColumnIndex("data")).split("/",3);
                data_g.setText(data[0]);
                data_m.setText(data[1]);
                data_a.setText(data[2]);
                comune.setText(c.getString(c.getColumnIndex("comune")));
                indirizzo.setText(c.getString(c.getColumnIndex("indirizzo")));
                euro.setText(c.getString(c.getColumnIndex("euro")));
                parente.setText(c.getString(c.getColumnIndex("parente")));
                mail.setText(c.getString(c.getColumnIndex("mail")));
                c.close();
                }
            flag = true;
        }

        Button salva = (Button) findViewById(R.id.salva);

        salva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent r = new Intent();
                r.putExtra("nome",nome.getText().toString());
                r.putExtra("cognome",cognome.getText().toString());
                r.putExtra("data",data_g.getText().toString()+"/"+data_m.getText().toString() +"/"+data_a.getText().toString());
                r.putExtra("comune",comune.getText().toString());
                r.putExtra("indirizzo",indirizzo.getText().toString());
                r.putExtra("euro",euro.getText().toString());
                r.putExtra("parente",parente.getText().toString());
                r.putExtra("mail",mail.getText().toString());
                r.putExtra("flag",flag);
                r.putExtra("id",""+id);

                setResult(Activity.RESULT_OK,r);
                finish();

            }
        });
    }
}
