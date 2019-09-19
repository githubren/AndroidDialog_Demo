package com.example.yfsl.androiddialog_demo;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static android.graphics.Color.TRANSPARENT;

public class MyFragment extends DialogFragment implements View.OnClickListener {
    private Button cancleBtn,confirmBtn;
    private EditText edit;

    public static MyFragment getInstance(){
        MyFragment myFragment = new MyFragment();
        //此处可以从宿主activity或fragment传数据过来
//        Bundle bundle = new Bundle();
//        bundle.putString("",);
//        bundle.putParcelableArrayList("",);
//        bundle.putParcelable("",);
//        myFragment.setArguments(bundle);
        return myFragment;
    }

    public void showDialogFragment(FragmentManager fragmentManager){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(MyFragment.this,"myFragment");
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void onStart() {
        super.onStart();
        //设置dialog尺寸
        setDialogSize();
    }

    /**
     * 设置弹窗尺寸
     */
    private void setDialogSize() {
        int screenWidth = MeasureUtil.getScreenWidth(getContext());
        int dialogHeight = getDialog().getWindow().getAttributes().height;
        //宽度设置为屏幕的0.8
        getDialog().getWindow().setLayout((int) (screenWidth*0.8),dialogHeight);
        //设置dialog背景透明
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(TRANSPARENT));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cancleBtn = view.findViewById(R.id.btn_cancle);
        confirmBtn = view.findViewById(R.id.btn_confirm);
        edit = view.findViewById(R.id.edit);
        cancleBtn.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_cancle:
                dismiss();
                break;
            case R.id.btn_confirm:
                String data = edit.getText().toString();
                //发送事件  传递数据
                RxBus.get().post("MYFRAGMENT",data);
                Toast.makeText(getContext(),"OK",Toast.LENGTH_SHORT).show();
                dismiss();
                break;
        }
    }
}
