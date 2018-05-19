package nz.ara.game.view.activity.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by yac0105 on 18/05/2018.
 */

public class MapView extends View {

    private float x;
    private float y;

    Paint drawPaint;
    private Path path = new Path();

    public MapView(Context context) {
        super(context);
    }

    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.drawMapByAttrs(attrs);
    }

    private void drawMapByAttrs(AttributeSet attrs){
        drawPaint = new Paint(Paint.DITHER_FLAG);
        drawPaint.setAntiAlias(true);
        drawPaint.setColor(Color.parseColor("#000"));
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeWidth(3);
        setWillNotDraw(false);
    }

    @Override
    protected void onSizeChanged(int w, int h, int width, int height) {
        super.onSizeChanged(w, h, width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(path, drawPaint);
    }
}
