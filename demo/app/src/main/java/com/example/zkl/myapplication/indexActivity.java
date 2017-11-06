package com.example.zkl.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class indexActivity extends Activity implements View.OnClickListener, View.OnFocusChangeListener {
    private AutoCompleteTextView autoText;
    ArrayAdapter<String> adapter;
    private TextView textView;
    private Button button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);

        autoText = findViewById(R.id.autoCompleteTextView);
        autoText.setOnFocusChangeListener(this);

        textView = findViewById(R.id.showText);
        button = findViewById(R.id.query);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        addAutoComText();
        this.textView.setText(autoText.getText());
    }
    /*
     * 用json实现本地保存搜索记录
     */
    private void addAutoComText() {
        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        JSONArray jsonArray = new JSONArray();
        SharedPreferences.Editor editor;
        if ("".equals(sharedPreferences.getString("queryHistory", ""))) {
            jsonArray.put(autoText.getText());
        } else {
            try {
                jsonArray=new JSONArray(sharedPreferences.getString("queryHistory", ""));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //最多保存五个，当达到5个时候删除最早的搜索记录
            if (jsonArray.length() > 4) {
                jsonArray.remove(0);
                jsonArray.put(autoText.getText());
            } else if (jsonArray.length() > 0) {
                jsonArray.put(autoText.getText());
            }
        }
        editor = sharedPreferences.edit();
        editor.putString("queryHistory", jsonArray.toString());
        editor.commit();
    }

    /*
     * 读取jsonArray中的数据构建适配器，AutoCompleteText加载适配器
     */
    private void updateDropDown() {
        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(sharedPreferences.getString("queryHistory", ""));
            String str[] = new String[5];
            for (int i = 0; i < jsonArray.length(); i++) {
                str[i] = jsonArray.getString(i);
            }
            adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, str);
            autoText.setAdapter(adapter);
            autoText.setCompletionHint("历史搜索");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*
     * 获取焦点时候显示历史搜索内容
     */
    @Override
    public void onFocusChange(View view, boolean b) {
        if (view.getId() == R.id.autoCompleteTextView) {
            this.updateDropDown();
            autoText.showDropDown();
        }
    }
}
