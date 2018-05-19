package nz.ara.game.view.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import nz.ara.game.model.impl.game.GameImpl;
import nz.ara.game.model.in.point.Point;

/**
 * Created by yac0105 on 18/05/2018.
 */

public class MapView extends View {

    private float x;
    private float y;

    private int stepWidthX = 100;

    private int stepWidthY = 100;

    private int startPointX = 100;

    private int startPointY = 200;

    private List<Point> wallLeftPointList = new ArrayList<Point>();

    private List<Point> wallAbovePointList = new ArrayList<Point>();


    Paint drawPaint;
    private Path path = new Path();

    public MapView(Context context) {
        super(context);
    }

    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.drawMapByAttrs(attrs);

        this.calStepwidthAndStartPoint();
    }

    private void drawMapByAttrs(AttributeSet attrs){
        drawPaint = new Paint(Paint.DITHER_FLAG);
        drawPaint.setAntiAlias(true);
        drawPaint.setColor(Color.BLACK);
        drawPaint.setStrokeWidth(5);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);

        setWillNotDraw(false);
    }

    private void calStepwidthAndStartPoint(){

    }

    @Override
    protected void onSizeChanged(int w, int h, int width, int height) {
        super.onSizeChanged(w, h, width, height);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


        int mHeight = this.getMeasuredHeight();
        int mWidth = this.getMeasuredHeight();

        int countX = 11;
        int countY = 11;

        this.stepWidthX = mWidth/(countX);
        this.stepWidthY = mHeight/(countY);

        this.startPointX = this.stepWidthX;
        this.startPointY = 30 + this.stepWidthY;


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        GameImpl gameModel =  new GameImpl(10,this.stepWidthX,this.stepWidthX);

        wallLeftPointList = gameModel.getMazeBean().getWallLeftPointList();

        wallAbovePointList = gameModel.getMazeBean().getWallAbovePointList();

        for( int i = 0; i<wallAbovePointList.size(); i++){

            Point wallAbovePoint = wallAbovePointList.get(i);

            int pointX = wallAbovePoint.across();
            int pointY = wallAbovePoint.down();

            int drawPointX = startPointX + pointX - this.stepWidthX/2;
            int drawPointY = startPointY + pointY - this.stepWidthY/2;

            canvas.drawLine(drawPointX, drawPointY, drawPointX + this.stepWidthX, drawPointY, drawPaint);

        }

        for( int i = 0; i<wallLeftPointList.size(); i++){

            Point wallAbovePoint = wallLeftPointList.get(i);

            int pointX = wallAbovePoint.across();
            int pointY = wallAbovePoint.down();

            int drawPointX = startPointX + pointX - this.stepWidthX/2;
            int drawPointY = startPointY + pointY - this.stepWidthY/2;

            canvas.drawLine(drawPointX, drawPointY, drawPointX, drawPointY + this.stepWidthY, drawPaint);

        }


    }

}
