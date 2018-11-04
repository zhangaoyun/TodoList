package com.example.aoyun.todolist;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.aoyun.todolist.db.TaskDbHelper;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private TaskDbHelper mHelper;   //声明全局变量
    private ListView mTaskListView; //声明全局变量
    private ArrayAdapter <String> mAdapter; //声明全局变量

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHelper = new TaskDbHelper(this,"tasks",null,1);    //实例化
        mTaskListView = findViewById(R.id.list_todo);

        updateUI(); //更新界面
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);  //菜单，即增加按钮
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_task:
                final EditText taskEditText = new EditText(this);
                AlertDialog dialog = new AlertDialog.Builder(this)  //是否增加Todo的AlertDialog
                        .setTitle("增加一个新的Todo")
                        .setMessage("打算做什么？")
                        .setView(taskEditText)
                        .setPositiveButton("增加", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String task = taskEditText.getText().toString();
                                SQLiteDatabase db = mHelper.getWritableDatabase();
                                ContentValues values = new ContentValues();
                                values.put("title", task);
                                db.insert("tasks", null, values);
                                db.close();
                                updateUI();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .create();
                dialog.show();
                return true;
            default:
                return true;
        }
    }

    public void deleteTask(View view) {
        View parent = (View) view.getParent();
        TextView taskTextView = parent.findViewById(R.id.task_title);
        String task = taskTextView.getText().toString();
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.delete("tasks", "title = ?", new String[]{task});
        db.close();
        updateUI();
    }

    private void updateUI() {
        ArrayList <String> taskList = new ArrayList <>();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query("tasks", new String[]{"_id", "title"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            int idx = cursor.getColumnIndex("title");//获取某一列在表中对应的位置索引
            taskList.add(cursor.getString(idx));
        }

        if (mAdapter == null) { //如果为空
            mAdapter = new ArrayAdapter <>(this, R.layout.item_todo, R.id.task_title, taskList);    //new一个
            mTaskListView.setAdapter(mAdapter);
        } else {    //如果有元素
            mAdapter.clear();   //先清空
            mAdapter.addAll(taskList);  //全部加进去
        }

        cursor.close(); //需要close
    }

}
