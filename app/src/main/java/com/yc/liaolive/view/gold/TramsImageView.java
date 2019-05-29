package com.yc.liaolive.view.gold;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import com.yc.liaolive.R;
import com.yc.liaolive.view.gold.util.DensityUtil;

/**
 * Created by Administrator on 2017/6/15/015.
 */

public class TramsImageView extends android.support.v7.widget.AppCompatImageView {
    private Paint paint;

    public TramsImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(ContextCompat.getColor(context, R.color.red));

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
      /*  Resources res =  getResources();
        Bitmap img = BitmapFactory.decodeResource(res, R.drawable.iv_red_package);
        Matrix matrix = new Matrix();
        matrix.postRotate(180);  /*//*翻转180度*//**//*

        int width = img.getWidth();
        int height = img.getHeight();
        Bitmap img_a = Bitmap.createBitmap(img, 0, 0, width, height, matrix, true);
       canvas.drawBitmap(img_a,matrix ,paint);*/
       canvas.drawLine(0,0, DensityUtil.getScreeFloatnWidth(getContext()), DensityUtil.getScreenHeight(getContext()),paint);
    }

}
