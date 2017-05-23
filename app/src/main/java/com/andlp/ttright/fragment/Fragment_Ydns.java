package com.andlp.ttright.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.andlib.lp.util.L;
import com.andlp.ttright.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.List;

/**
 * 717219917@qq.com  2017/5/23 13:42
 */
@ContentView(R.layout.fragment_ydns)
public class Fragment_Ydns extends Fragment_Base{//1 2 3 4 5页面(复用)
    @ViewInject(R.id.fragment_ydns__img) ImageView fragment_ydns__img;//图像


    int index =0;      //默认当前页面
    int index_list=0;  //数组下标
    List<String> list; //png数组(assets)

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        L.i(tag+"onCreate()"+index);
        initView();
    }


    @Override public void onResume() {
        super.onResume();
        L.i(tag+"onResume()"+index);
    }

    //1
    private void initView(){


        initData();
    }

    //2 进行事件分发
    private void initData(){
        switch (index){
            case 1 :  one();     break;
            case 2 :  two();     break;
            case 3 :  three();   break;
            case 4 :  four();    break;
            case 5 :  five();    break;
        }
        ImageLoader.getInstance().displayImage("assets://"+list.get(index_list),fragment_ydns__img);//更新图片
    }

    //2.0 切换
    private void change(){
        int size = list.size();
        if (index_list<size){
            index_list+=1;
        }else{
            index_list=0;
        }
        ImageLoader.getInstance().displayImage("assets://"+list.get(index_list),fragment_ydns__img);//更新图片
    }


    //2.1
    private void one(){
        list.add("");//数值......晚上搞

    }

    //2.2
    private void two (){

    }

    //2.3
    private void three(){

    }

    //2.4
    private void four(){

    }

    //2.5
    private void five(){

    }


    //粘连事件,fragment没有开启也能接收到消息
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void reciveFrom__Activity_Main(String index_temp){

        switch (index_temp){
            case "1" :  index=1;     break;
            case "2" :  index=2;     break;
            case "3" :  index=3;     break;
            case "4" :  index=4;     break;
            case "5" :  index=5;     break;
        }

        L.i(tag+"on recive:"+index_temp);
    }


    @Override public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
