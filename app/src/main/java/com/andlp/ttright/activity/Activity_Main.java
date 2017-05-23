package com.andlp.ttright.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;

import com.andlib.lp.util.L;
import com.andlp.ttright.R;
import com.andlp.ttright.fragment.Fragment_Ydns;

import org.greenrobot.eventbus.EventBus;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

@ContentView(R.layout.activity_main)
public class Activity_Main extends Activity_Base {
    @ViewInject(R.id.main_tv) TextView main_tv;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    private void addFragment(){
        getSupportFragmentManager().beginTransaction().add(R.id.main_content, new Fragment_Ydns(), "ydns")
                 .addToBackStack("ydns")
                .commit();
    }


    @Event(R.id.main_tv) private void main_tv(View view){
        L.i(tag+"onClick()main_tv");
        EventBus.getDefault().postSticky("2");
        addFragment();

    }



}
