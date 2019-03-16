package com.example.wikifry.testandy;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

public class DrawView extends View {

    Paint paint = new Paint();

    private int linesCount = 10;
    private float[] values = new float[linesCount];
    private Line[] lines = new Line[linesCount];

    private float width = 0;
    private float height = 0;

    private float min = 0;
    private float max = 1023 + 1;

    private float range = max - min;

    private void init(){
        paint.setColor(Color.BLACK);
        for(int i = 0; i<linesCount; i++)
        {
            values[i] = 0;
            lines[i]=new Line(0,0,0,0);
        }
        drawData();

    }

    public void setSize(float width, float height)
    {
        this.width = width;
        this.height = height;

        String len = String.valueOf(width);

        linesCount = Integer.parseInt( String.valueOf(width).substring(0, len.length()-2) ) ;
        values = new float[linesCount];
        lines = new Line[linesCount];

        init();
    }

    public void setLimits(float min, float max){
        this.min = min;
        this.max = max;
    }

    public DrawView(Context context)
    {
        super(context);
        init();

    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void insertData(float value)
    {
        for(int i = 0; i<(linesCount-1); i++)
        {
            values[i] = values[i + 1];
        }
        values[linesCount-1] = value;
    }

    @Override
    public void onDraw(Canvas canvas) {
        //Draw code goes here
        //canvas.drawLine(0, 0, 45, 45, paint);
        drawData();
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.LIGHTEN);
        for (int i = 0; i < lines.length; i++) {
            canvas.drawLine(lines[i].StartX, lines[i].StartY, lines[i].EndX, lines[i].EndY, paint);
        }

    }

    public void drawData(){

        for (int i = 0; i<(values.length-1); i++) {

            lines[i]= new Line(i*(width/values.length), calculateY(values[i]), (i+1)*(width/values.length), calculateY(values[i+1]) );
        }
        lines[values.length-1]= new Line((values.length-1)*(width/values.length), calculateY(values[values.length-1]), (values.length)*(width/values.length), calculateY(values[values.length-1]) );

    }

    private float calculateY(float val)
    {
        float ret = (val/range) * height;
        return height - ret;
        //return height - (ret +1);
    }

    private void updateRange()
    {
        range = (max - min) + 1;
    }
}
