package com.example.administrator.saolei;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    public static int GAME = 0;//判断游戏是否已经开始,0为未开始,1为已开始
    private static int HANG = 19;
    private static int LIE = 14;
    private static int NUM = 30;//雷的个数
    private LinearLayout gv;
    private LinearLayout[] ll = new LinearLayout[HANG];
    private static Block[][] bl = new Block[HANG][LIE];
    private static int [] FKNUM={R.drawable.i0,R.drawable.i1,R.drawable.i2,R.drawable.i3,R.drawable.i4,R.drawable.i5,R.drawable.i6,R.drawable.i7,R.drawable.i8};
    private static TextView tv_num;
    private static TextView tv_time;
    private static Timer timer1;
    private static Timer timer2;
    private static int time = 0;

    public static Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0){//接受游戏开始的信息，对所有的方块进行初始化;
                init();
            }else if (msg.what == 1){//接受游戏第一个键为炸弹的信息；
                for (int i = 0;i< LIE;i++){
                    if (bl[0][i].getState() != -1){
                        bl[0][i].setState(-1);
                    }
                }
            }else if (msg.what == 3){
                int BLHANG = msg.arg1;
                int BLLIE = msg.arg2;
                bl[BLHANG][BLLIE].setState(0);
                for (int i=0;i<HANG;i++){
                    for (int j=0;j<LIE;j++){
                        if (bl[i][j].getState() != -1 ) {
                            checkout(i, j);
                        }
                    }
                }
                int state = bl[BLHANG][BLLIE].getState();
                System.out.println(BLHANG+"-"+BLLIE+"===="+state);
                bl[BLHANG][BLLIE].setImageResource(FKNUM[state]);
                bl[BLHANG][BLLIE].setFlag(false);
                if (state == 0){
                    checkoverpa(BLHANG,BLLIE);
                }
            } else if (msg.what == 2){
                int BLHANG = msg.arg1;
                int BLLIE = msg.arg2;
                int state = bl[BLHANG][BLLIE].getState();
                System.out.println(BLHANG+"-"+BLLIE+"===="+state);

                if (state == -1){
                    over();
                    bl[BLHANG][BLLIE].setImageResource(R.drawable.cklei);
                    bl[BLHANG][BLLIE].setFlag(false);
                }else if (state == 0){
                    checkoverpa(BLHANG,BLLIE);
                    bl[BLHANG][BLLIE].setImageResource(FKNUM[state]);
                    bl[BLHANG][BLLIE].setFlag(false);
                }else {
                    bl[BLHANG][BLLIE].setImageResource(FKNUM[state]);
                    bl[BLHANG][BLLIE].setFlag(false);
                }
            }else if (msg.what == 4){
                NUM--;
                tv_num.setText(NUM+"");
            }else if (msg.what == 5){
                NUM++;
                tv_num.setText(NUM+"");
            }else if (msg.what == 6){
                tv_time.setText(time+"");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_num = (TextView) findViewById(R.id.tv_num);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_time.setText(time+"");
        tv_num.setText(NUM+"");
        gv = (LinearLayout) findViewById(R.id.gv);
        create();

        timer1 = new Timer();
        timer2 = new Timer();
        timer1.schedule(new TimerTask() {
            @Override
            public void run() {
                if (GAME == 1){
                    timer2.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            time++;
                            handler.sendEmptyMessage(6);
                        }
                    }, 0, 1000);
                    timer1.cancel();
                }
            }
        }, 0, 100);

    }
    //重新开始游戏，重新加载所有的布局方块
    public void restart(View v){
        if (timer1 !=null){
            timer1.cancel();
            timer1 = null;
        }
        if (timer2 != null){
            timer2.cancel();
            timer2 = null;
        }
        for (int i=0;i<HANG;i++){
            ll[i].removeAllViews();
        }
        gv.removeAllViews();
        time = 0;
        GAME = 0;
        NUM = 30;
        tv_num.setText(NUM+"");
        tv_time.setText(time+"");
        timer1 = new Timer();
        timer2 = new Timer();
        timer1.schedule(new TimerTask() {
            @Override
            public void run() {
                if (GAME == 1){
                    timer2.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            time++;
                            handler.sendEmptyMessage(6);
                        }
                    },0,1000);
                    timer1.cancel();
                }
            }
        },0,100);
        create();
    }
    //加载所有的布局和方块
    public  void  create(){
        for (int i=0;i<HANG;i++){
            ll[i] = new LinearLayout(this);
            ll[i].setOrientation(LinearLayout.HORIZONTAL);
            gv.addView(ll[i]);
            for (int j =0;j< LIE ;j++){
                bl[i][j] = new Block(this);
                bl[i][j].setHANG(i);
                bl[i][j].setLIE(j);
                ll[i].addView(bl[i][j]);
            }
        }
    }
    //对第一个雷的位置进行初始化
    private static void init() {
        for (int i =0;i< NUM;i++) {//如果点击的第一个为雷，则将雷的位置设置到其他地方
            int rand1 = (int) (Math.random() * HANG);
            int rand2 = (int) (Math.random() * LIE);
            if (bl[rand1][rand2].getState() == -1) {
                i--;
            } else {
                bl[rand1][rand2].setState(-1);
            }
        }
    }
    //检查周围8个方块中有多少雷，并将该方块设置为雷的个数
    private static void checkout(int i,int j){
        int num = 0;
        if (i > 0) {//上一行不能够等于0
            for (int k = -1; k < 2; k++) {//检查第一行的信息
                if (j+k>=0 && j+k<LIE) {//不能超出左右两边的范围
                    if (bl[i - 1][j + k].getState() == -1) {//如果有方块为地雷则num++
                        num++;
                    }

                }
            }
        }
        if (j>0) {//检查该方块左边的状态
            if (bl[i][j - 1].getState() == -1) {//如果有方块为地雷则num++
                num++;
            }
        }
        if (j < LIE-1) {//检查该方块右边的状态
            if (bl[i][j + 1].getState() == -1) {//如果有方块为地雷则num++
                num++;
            }
        }
        if (i < HANG-1) {//下一行的行数不能大于总行数
            for (int k = -1; k < 2; k++) {//检查下一行的信息
                if (j+k>=0 && j+k<LIE) {
                    if (bl[i + 1][j + k].getState() == -1) {
                        num++;
                    }
                }
            }
        }
        bl[i][j].setState(num);
    }
    //检查元素是否可操作
    private static void checkover(int i,int j) {
        if (bl[i][j].isFlag()) {//true代表没有设置了图案(可操作),则检查没有设置的方块
            int state = bl[i][j].getState();
            if (state == 0) {
                bl[i][j].setImageResource(FKNUM[state]);
                bl[i][j].setFlag(false);
                checkoverpa(i, j);
            }else {
                bl[i][j].setImageResource(FKNUM[state]);
                bl[i][j].setFlag(false);
            }
        }
    }
    //检查传入元素周围8个元素
    private static void checkoverpa(int i,int j){
//        System.out.println("x="+i+"----"+"y="+j+"====="+bl[i][j].getState());
        if (i>0 && j >0){
            checkover(i-1,j - 1);
        }
        if (i > 0) {
            checkover(i - 1, j);
        }
        if (i>0 && j<LIE-1){
            checkover(i-1 ,j+1);
        }
        if (j>0) {
            checkover(i, j - 1);
        }
        if (j<LIE-1) {
            checkover(i, j + 1);
        }
        if (i<HANG-1 && j>0){
            checkover(i+1 , j-1);
        }
        if (i<HANG-1) {
            checkover(i + 1, j);
        }
        if (i<HANG-1 && j<LIE -1){
            checkover(i+1,j+1);
        }
    }
    //当点到雷时触发这个方法
    private static void over() {
        timer2.cancel();
        for (int i = 0; i<HANG;i++){
            for (int j=0;j<LIE;j++){
                if (bl[i][j].getState() == -1){
                    bl[i][j].setImageResource(R.drawable.lei);
                    bl[i][j].setFlag(false);
                }
                bl[i][j].setCanclick(false);
            }
        }
    }
    //游戏结束时将静态常量初始化
    private void cancel(){
        GAME = 0;
    }

    @Override
    protected void onDestroy() {
        cancel();
        super.onDestroy();
    }


}
