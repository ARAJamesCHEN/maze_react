package nz.ara.game.view.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.yac0105.game.R;

import java.util.ArrayList;
import java.util.List;

import nz.ara.game.model.impl.game.GameImpl;
import nz.ara.game.model.in.point.Point;

/**
 * Created by yac0105 on 18/05/2018.
 */

public class MapView extends View {

    private static final String TAG = "MapView";

    private float x;
    private float y;

    private int stepWidthX = 100;

    private int stepWidthY = 100;

    private int startPointX = 100;

    private int startPointY = 200;

    private String itemsWallLeftStr;

    private String itemsWallAboveStr;

    private Paint drawPaint;

    private Path path = new Path();

    public MapView(Context context) {
        super(context);
    }

    public MapView(Context context, AttributeSet attrs){
        super(context,attrs );

        this.drawMapByAttrs();

    }

    private void drawMapByAttrs(){
        drawPaint = new Paint(Paint.DITHER_FLAG);
        drawPaint.setAntiAlias(true);
        drawPaint.setColor(Color.BLACK);
        drawPaint.setStrokeWidth(5);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);

        setWillNotDraw(false);
    }

    @Override
    protected void onSizeChanged(int w, int h, int width, int height) {
        super.onSizeChanged(w, h, width, height);
    }


    public int getStepWidthX() {
        return stepWidthX;
    }

    public void setStepWidthX(int stepWidthX) {
        this.stepWidthX = stepWidthX;
    }

    public int getStepWidthY() {
        return stepWidthY;
    }

    public void setStepWidthY(int stepWidthY) {
        this.stepWidthY = stepWidthY;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


        int mHeight = this.getMeasuredHeight();
        int mWidth = this.getMeasuredHeight();

        int countX = 4;
        int countY = 4;

        this.stepWidthX = mWidth/(countX+1);
        this.stepWidthY = mHeight/(countY+1);

        this.startPointX = this.stepWidthX/2;

        this.startPointY = 30 + this.stepWidthY/2;


    }

    private void drawMap(Canvas canvas, String wallStr, String type){
        if(wallStr!=null && wallStr.trim().length()>0){
            String[] wallStrArray = wallStr.split("\\|");

            for(String pointStr : wallStrArray){

                String[] pointStrArray = pointStr.split(",");

                int pointX = Integer.parseInt(pointStrArray[0]);
                int pointY = Integer.parseInt(pointStrArray[1]);
                Log.d(TAG, "Point: " + pointX + "," + pointY);

                int drawPointX = startPointX + pointX*this.stepWidthX - this.stepWidthX/2;
                int drawPointY = startPointY + pointY*this.stepWidthX - this.stepWidthY/2;

                if(type!=null && type.equals("ABOVE")){
                    canvas.drawLine(drawPointX, drawPointY, drawPointX + this.stepWidthX, drawPointY, drawPaint);
                }else if(type!=null && type.equals("LEFT")){
                    canvas.drawLine(drawPointX, drawPointY, drawPointX, drawPointY + this.stepWidthY, drawPaint);
                }else{
                    Log.e(TAG, "Error Type:" + type);
                }

            }

        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        this.drawMap(canvas, this.itemsWallAboveStr, "ABOVE");
        this.drawMap(canvas,this.itemsWallLeftStr, "LEFT");

    }

    public String getItemsWallLeftStr() {
        return itemsWallLeftStr;
    }

    public void setItemsWallLeftStr(String itemsWallLeftStr) {
        this.itemsWallLeftStr = itemsWallLeftStr;
    }

    public String getItemsWallAboveStr() {
        return itemsWallAboveStr;
    }

    public void setItemsWallAboveStr(String itemsWallAboveStr) {
        this.itemsWallAboveStr = itemsWallAboveStr;
    }
}
