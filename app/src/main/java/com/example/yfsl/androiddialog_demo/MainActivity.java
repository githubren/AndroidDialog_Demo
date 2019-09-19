package com.example.yfsl.androiddialog_demo;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button ordinary, singleChoice, multiChoice1, progressDialog,dialogFragment;
    private TextView textView;
    private Observable<String> rxBus;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        rxBus = RxBus.get().register("MYFRAGMENT");
        rxBus.subscribe(string -> textView.setText(string));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (rxBus != null) {
            RxBus.get().unregister("MYFRAGMENT",rxBus);
        }
        rxBus = null;
    }

    private void initView() {
        ordinary = findViewById(R.id.btn_1);
        singleChoice = findViewById(R.id.btn_2);
        multiChoice1 = findViewById(R.id.btn_3);
        progressDialog = findViewById(R.id.btn_4);
        dialogFragment = findViewById(R.id.btn_5);
        textView = findViewById(R.id.activity_text);

        ordinary.setOnClickListener(this);
        singleChoice.setOnClickListener(this);
        multiChoice1.setOnClickListener(this);
        progressDialog.setOnClickListener(this);
        dialogFragment.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_1://普通对话框
                setOrdinary();
                break;
            case R.id.btn_2://单选对话框
                setSingleChoice();
                break;
            case R.id.btn_3://多选对话框
                setMultiChoice1();
                break;
            case R.id.btn_4://进度条对话框
                setProgressDialog();
                break;
            case R.id.btn_5://dialogfragment对话框
                MyFragment.getInstance().showDialogFragment(getSupportFragmentManager());
                break;
            default:
                break;
        }
    }

    /**
     * 设置进度条对话框
     */
    private void setProgressDialog() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("正在玩命加载ing");
        // 设置一下进度条的样式
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);//水平样式
//        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//圆环样式
        // 和Toast一样 最后一定要show 出来
        dialog.show();
        // 创建一个子线程
        new Thread(() -> {
            // 设置进度条的最大值
            dialog.setMax(100);
            // 设置当前进度
            for (int i = 0; i < 100; i++) {
                dialog.setProgress(i);
                SystemClock.sleep(100);
            }
        }).start();
    }

    /**
     * 设置多选对话框
     */
    private void setMultiChoice1() {
        // 通过Builder构建器来构造
        // 下面的参数上下文 对话框里面一般都用this
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请选择您喜欢吃的水果");
        final String items[] = {"苹果", "香蕉", "梨", "哈密瓜", "西瓜", "荔枝", "芒果"};
        final boolean[] checkItems = {false, false, false, false, false, false, false};
        builder.setMultiChoiceItems(items, checkItems, (dialog, which, isChecked) -> log(items[which]));
        // 设置确定按钮
        builder.setPositiveButton("确定", (dialog, which) -> {
            StringBuffer sb = new StringBuffer();
            // 把选中的条目的数据取出来
            for (int i = 0; i < checkItems.length; i++) {
                // 判断一下选中的
                if (checkItems[i]) {
                    String fruit = items[i];
                    sb.append(fruit + " ");
                }
            }
            tip(sb.toString());
            // 关闭对话框
            dialog.dismiss();
        });
        // 和Toast一样 最后一定要show 出来
        builder.show();
    }

    /**
     * 设置单选对话框
     */
    private void setSingleChoice() {
        // 通过Builder构建器来构造
        // 下面的参数上下文 对话框里面一般都用this
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请选择您喜欢的课程");
        final String items[] = {"Android", "ios", "C", "C++", "Java", "Python", "C#"};
        // -1 代表没有条目被选中
        builder.setSingleChoiceItems(items, -1, (dialog, which) -> {
            // 把选择的条目取出来
            String item = items[which];
            tip(item);
            // 关闭对话框
            dialog.dismiss();
        });
        // 和Toast一样 最后一定要show 出来
        builder.show();
    }

    /**
     * 设置普通对话框
     */
    private void setOrdinary() {
        // 通过Builder构建器来构造
        // 下面的参数上下文 对话框里面一般都用this
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("对话框");
        log("我是一个普通的对话框！");
        DialogInterface.OnClickListener listener = (dialog, which) -> {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    log("点击了确定按钮！");
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    log("点击了取消按钮");
                    break;
            }
        };
        builder.setPositiveButton("确定",listener);
        builder.setNegativeButton("取消",listener);
//        // 设置取消按钮
//        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                log("点击了取消按钮");
//            }
//        });
//        // 设置确定按钮
//        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                log("点击了确定按钮！");
//            }
//        });
        // 和Toast一样 最后一定要show 出来
        builder.show();
    }

    private void log(String msg){
        Log.e("TAG",msg);
    }

    private void tip(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

}
