package com.e.mybd;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.util.Log;
import android.database.Cursor;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    final String LOG_TAG = "myLogs";
    Button btnAdd, btnRead, btnClear;
    EditText etName, etEmail;

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd =  findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);

        btnRead =  findViewById(R.id.btnRead);
        btnRead.setOnClickListener(this);

        btnClear =  findViewById(R.id.btnClear);
        btnClear.setOnClickListener(this);

        etName =  findViewById(R.id.etName);
        etEmail =  findViewById(R.id.etEmail);

        /*создание объекта ниже описанного класса BDHelper, для создания и управления версиями
        * базы данных*/
        dbHelper = new DBHelper(this);

    }

    @Override
    public void onClick(View v) {

        /*создаем объект для данных. ContentValues представляет собой словарь, который
        * хранит пары ключ-значение.Первый парамметр - ключ, второй - значение*/

        ContentValues cv = new ContentValues();

        String name = etName.getText().toString();
        String email = etEmail.getText().toString();
        //Прочли данные из полей, привели к строковому и записали в строковые переменные

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //создали подключение к базе данных

        switch (v.getId()){
            case R.id.btnAdd:
            Log.d(LOG_TAG, "--- Insert in mytable: ---");
            // подготовим данные для вставки в виде пар: наименование столбца - значение

            cv.put("name",name);
            cv.put("email",email);
            //вставили запись в таблицу базы данных с помощью метода .put для ContentValues (наш cv)

            // вставляем запись и получаем ее ID
            long rowID = db.insert("my_table", null, cv);
            Log.d(LOG_TAG, "row inserted, ID = " + rowID);
            break;
            case R.id.btnRead:
                Log.d(LOG_TAG, "--- Rows in my_table: ---");
                //делаем запрос всех данных из my_table,, получаем Cursor
                Cursor c =db.query("my_table",null,null,null,null,null,null);
                /*Объекту с типа Cursor присвоили то, что вернет запрос к базе
                * данных с помощью обращения к бд с помощью метода .query*/

                // ставим позицию курсора на первую строку выборки
                // если в выборке нет строк, вернется false
                if(c.moveToFirst()){
                    // определяем номера столбцов по имени в выборке
                    int IdColIndex = c.getColumnIndex("id");
                    //создали интовую переменную. Использовали метод для курсора .getColumnIndex, а передаем в метод название столбца по факту
                    int nameColIndex = c.getColumnIndex("name");
                    int emailColIndex = c.getColumnIndex("email");

                    do {
                        // получаем значения по номерам столбцов и пишем все в лог
                        Log.d(LOG_TAG,
                                "ID = " + c.getInt(IdColIndex) + ", name = " + c.getString(nameColIndex) + ", email = " + c.getString(emailColIndex));
                        // переход на следующую строку
                        // а если следующей нет (текущая - последняя), то false - выходим из цикла

                    }while (c.moveToNext());
                    //цикл do while выполнится 1 раз точно, и выполняется пока c.moveToNext() возвращает true
                    }else
                    Log.d(LOG_TAG, "0 rows");
                    c.close();
                    //курсор закрыли
                    break;
            case R.id.btnClear:
                Log.d(LOG_TAG, "--- Clear mytable: ---");
                // удаляем все записи
                int СlearCount = db.delete("my_table",null,null);
                //зачистили таблицу без задания условия и аргументов, по которым идет условие
                Log.d(LOG_TAG, "deleted rows count = " + СlearCount);
                break;

        }
        dbHelper.close();
        //закрыли подключение к базе данных
    }

    class DBHelper extends SQLiteOpenHelper{

        public DBHelper(Context context) {
            super(context, "mydb", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(LOG_TAG, "--- onCreate database ---");
            // создаем таблицу с полями
            db.execSQL("create table mytable (" + "id integer primary key autoincrement," + "name text," + "email text" + ");");
            //использовали метод .execSQL для описания запроса на создание таблицы и полей в ней на языке sql

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
