package com.example.gtapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {

    private Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        FloatingActionButton insertButton = (FloatingActionButton) findViewById(R.id.insert_button);
        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), InsertActivity.class));

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        DBHelper db = new DBHelper(getBaseContext());
        SQLiteDatabase banco = db.getWritableDatabase();

        //CURSOR PARA PERCORRER O BANCO DE DADOS
        mCursor = banco.rawQuery("SELECT _id, tarefa, data FROM tarefas", null);

        //PEGA OS DADOS DA COLUNA DE TAREFA E DE DATA
        String[] from = {
                "tarefa",
                "data"
        };

        //COLOCA OS DADOS NAS TEXTVIEWS
        int[] to = {
                R.id.txvTarefa,
                R.id.txvData
        };

        //CRIA O ADAPTER COM OS VETORES from E to INSTANCIADOS ANTERIORMENTE
        MeuAdapter adapter = new MeuAdapter(getBaseContext(), R.layout.lista_tarefas, mCursor, from ,to ,0);

        //COLOCA OS DADOS DO ADAPTER NA LISTVIEW QUE MOSTRA O HISTÓRICO
        ListView ltvTarefas = (ListView)findViewById(R.id.ltvTarefas);
        ltvTarefas.setAdapter(adapter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        //FECHA O CURSOR
        mCursor.close();
    }

    //ADAPTER CUSTOMIZADO PARA MOSTRAR OS DADOS EM VERMELHO CASO A DATA DA TAREFA SEJA ANTERIOR À ATUAL
    public class MeuAdapter extends SimpleCursorAdapter {

        public MeuAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, flags);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            super.bindView(view, context, cursor);

            TextView txvData   = (TextView) view.findViewById(R.id.txvData);

            String data = cursor.getString(2);

            //6 primeiros caracteres descarta, sobra dd/mm/aaaa
            //00 01 02 03 04 05 06 07 08 09 10 11 12 13 14 15
            //S  E  X  .  -  .  2  9  /  0  5  /  2  0  2  0

            int ano = Integer.parseInt(data.substring(12,16));
            int mes = (Integer.parseInt(data.substring(9,11)) - 1);
            int dia = Integer.parseInt(data.substring(6,8));

            //DATA DO BANCO DE DADOS
            Calendar user = new GregorianCalendar(ano, mes, dia);

            //DATA ATUAL
            Calendar now = new GregorianCalendar();

            //RETORNA TRUE SE A DATA INFORMADA FOR ANTERIOR A ATUAL
            if(user.before(now))
            {
                txvData.setTextColor(Color.RED);
            }
        }

        private void esvaziarTarefas() //UTILIZADO DURANTE O DESENVOLVIMENTO DA APLICAÇÃO
        {
            //ATUALIZAR BANCO DE DADOS
            Bundle bundle = new Bundle();
            bundle.putInt("id", 3);

            Intent intent = new Intent(getBaseContext(), CursoresActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
}
