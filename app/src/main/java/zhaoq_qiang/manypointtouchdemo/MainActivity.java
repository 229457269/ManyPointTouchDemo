package zhaoq_qiang.manypointtouchdemo;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.FloatMath;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;


/**
 * 拖拉功能与多点触摸：
 */
public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img = (ImageView) findViewById(R.id.img_View);

        img.setOnTouchListener(this);
    }


    PointF startPoint = new PointF();

    Matrix matrix = new Matrix();//矩阵对象

    Matrix currentMatrix = new Matrix();//存放照片当前的移动位置：

    private int mode = 0;//定义模式：放大和缩小
    private static final int DRAG = 1; //放大
    private static final int ZOOM = 2; //缩小

    private float startDis;

    private PointF midPoint;//中心点

    /**
     * @param v 用户触摸所产生的控件
     * @param event  产生的事件  需要判断事件类型：
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        //event.getAction返回一个整形值，低八位是触摸事件。高八位 不为触摸事件。
            switch(event.getAction() & MotionEvent.ACTION_MASK){

                case MotionEvent.ACTION_DOWN:

                    mode = DRAG;

                    currentMatrix.set(img.getImageMatrix());//记录当前的移动位置
                    //手指按下屏幕
                    startPoint.set(event.getX(),event.getY());//记录下来开始的坐标

                    break;
                case MotionEvent.ACTION_MOVE:

                    if(mode == DRAG){//
                        //手指移动事件屏幕  该事件会被不断的触发
                        float dx = event.getX() - startPoint.x;//获取x轴的移动距离
                        float dy = event.getY() - startPoint.y;//

                        //在上次移动位置的基础上进行移动  进行移动：
                        matrix.set(currentMatrix);

                        matrix.postTranslate(dx,dy);//移动距离
                    }else if(mode ==ZOOM){//缩放：
                        float endDis = distance(event);//结束距离

                        float scale = endDis/startDis;//得到缩放的比例   乘10  不可以放大过度

                        matrix.postScale(scale,scale,midPoint.x,midPoint.y);

                    }
                    break;
                case MotionEvent.ACTION_UP://手指离开屏
                case MotionEvent.ACTION_POINTER_UP:
                    //有手指离开屏幕  但屏幕上还有触点
                    mode = 0;
                    break;

                case MotionEvent.ACTION_POINTER_DOWN:
                    //当屏幕上已经有触点，再有一个手指压下事件：
                    mode = ZOOM;
                    startDis = distance(event);

                    if(startDis>10f){//距离大于是个像素   进行处理
                        midPoint = mid(event);  //得到缩放倍数
                        matrix.set(currentMatrix);
                        currentMatrix.set(img.getImageMatrix());//记录当前的缩放倍数
                    }

                    break;

            }

        img.setImageMatrix(matrix);//设置图片的   位置

        return true;
    }

    /**
     * 计算两点之间的  距离
     * @param event
     * @return
     */
    private static float distance(MotionEvent event) {

        float dx = event.getX(1) - event.getX(0);
        float dy = event.getY(1) - event.getY(0);

        return (float) Math.sqrt(dx*dx + dy*dy);
    }


    /**
     * 计算两点之间的   中间点：
     * @param event
     * @return
     */
    public static PointF mid(MotionEvent event){

        float midX = (event.getX(1))+event.getX(0)/2;
        float midY = (event.getX(1))+event.getY(0)/2;

        return new PointF(midX,midY);

    }


}
