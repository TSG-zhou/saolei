package com.example.administrator.saolei;

import android.content.Context;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Administrator on 2016/9/19.
 */
public class Block extends ImageView {

    private boolean canclick = true;//判断是否游戏结束,true代表该方块不可点击
    private boolean flag = true;//判断是否已设置了图案,true代表未设置
    private boolean qizi = false;//判断是否已经设置的棋子，true代表已设置
    private int HANG;
    private int LIE;
    private int state=0;//方块周围的雷，-1代表为雷，0代表没有，等等


    public void setCanclick(boolean canclick) {
        this.canclick = canclick;
    }
    public boolean isFlag() {
        return flag;
    }
    public void setFlag(boolean flag) {
        this.flag = flag;
    }
    public void setHANG(int HANG) {
        this.HANG = HANG;
    }
    public void setLIE(int LIE) {
        this.LIE = LIE;
    }
    public int getState() {
        return state;
    }
    public void setState(int state) {
        this.state = state;
    }


    public Block(Context context) {
        super(context, null);
        setImageResource(R.drawable.block);

        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (flag) {//如果还未设置图案
                    if (qizi) {//如果已设置了旗帜
                        setImageResource(R.drawable.block);
                        qizi = false;
                        canclick = true;//则将该方块设置为可点击状态
                        MainActivity.handler.sendEmptyMessage(5);
                    } else {//如果未设置旗帜
                        if (canclick) {//判断是否可点击,true代表该方块可以点击
                            setImageResource(R.drawable.qizi);
                            qizi = true;
                            canclick = false;
                            MainActivity.handler.sendEmptyMessage(4);
                        }
                    }
                }
                return true;
            }
        });

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainActivity.GAME == 0){
                    MainActivity.GAME = 1;
                    MainActivity.handler.sendEmptyMessage(0);//发送游戏开始初始化的信息

                    if (getState() == -1){
                        MainActivity.handler.sendEmptyMessage(1);//发送点击的为炸弹的信息
                    }
                    Message msg = new Message();
                    msg.what = 3;//继续发送第一次游戏的信息
                    msg.arg1 = HANG;
                    msg.arg2 = LIE;
                    MainActivity.handler.sendMessage(msg);
                }
                else {
                    if (canclick) {
                        Message msg = new Message();
                        msg.what = 2;
                        msg.arg1 = HANG;
                        msg.arg2 = LIE;
                        MainActivity.handler.sendMessage(msg);
                    }
                }
            }
        });
    }



    public Block(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public Block(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
