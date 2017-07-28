package com.example.dario.tesserac;

import android.provider.BaseColumns;

/**
 * Created by xyzw on 01/11/15.
 */
public class SchemaDB {
    // To prevent someone from accidentally instantiating the
    // schema class, give it an empty constructor.
    public SchemaDB() {
    }

    /* Inner class that defines the table contents */
    public static abstract class Tessera implements BaseColumns {
        public static final String TABLE_NAME = "tessera";
        public static final String COLUMN_NOME = "nome";
        public static final String COLUMN_COGNOME = "cognome";
        public static final String COLUMN_DATA = "data";
        public static final String COLUMN_COMUNE = "comune";
        public static final String COLUMN_INDIRIZZO = "indirizzo";
        public static final String COLUMN_EURO = "euro";
        public static final String COLUMN_PARENTE = "parente";
        public static final String COLUMN_MAIL = "mail";
    }

}
