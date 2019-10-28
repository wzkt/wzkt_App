package com.Alan.eva.model;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

public class Axis extends View {
	private int mWidth; // X轴长度
	private int mHeight; // Y轴长度
	private int countY = 5; // Y轴坐标数
	private String unitY = "温度"; // Y轴单位
	private int maxDataY = 1; // Y轴坐标最大值
	private int spaceLeft = 30 + 5; // 与左边缘的距离
	private int spaceBottom = 20 + 20; // 与下边缘的距离
	private int spaceTop = 40; // 与上边缘的距离
	private int spaceRight = 30 + 5; // 与右边缘的距离

	public Axis(Context context) {
		super(context);
	}

	public Axis(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mWidth = w - spaceLeft - spaceRight; // 宽度
		mHeight = h - spaceTop - spaceBottom - 80; // 高度
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Paint paint = new Paint();
		paint.setStrokeWidth(3);
		paint.setAntiAlias(true);
		paint.setColor(Color.GRAY);
		paint.setTextSize(20);
		// 画坐标轴
		if (canvas != null) {
			int x = mWidth + spaceLeft, y = mHeight + spaceTop;
			canvas.drawColor(Color.TRANSPARENT);
			canvas.drawLine(spaceLeft, mHeight + spaceTop, spaceLeft, spaceTop, paint);
			x = spaceLeft;
			y = spaceTop;
			drawTriangle(canvas, new Point(x, y), new Point(x - 5, y + 10), new Point(x + 5, y + 10));
			//canvas.drawText(unitY, x-60, y + 15, paint);
			// 画坐标轴刻度线
			int pieceY = mHeight / countY;
			drawBase(canvas, pieceY);
			
		}
	}

	private void drawBase(Canvas canvas, int pieceY) {

		Paint paint2 = new Paint();
		paint2.setStrokeWidth(3);
		paint2.setAntiAlias(true);
		paint2.setTextSize(25);
		paint2.setColor(Color.BLACK);

		Paint paint1 = new Paint();
		paint1.setStrokeWidth(1);
		paint1.setAntiAlias(true);
		paint1.setColor(Color.GRAY);
		String[] yValues = {"0","10","20","30","40","50"};//y轴数值
		// 画Y轴刻度
		for (int j = 0; j < countY; j++) {
			// 绘制Y网格
			/*canvas.drawLine(spaceLeft, mHeight + spaceTop - pieceY * j, mWidth, mHeight + spaceTop - pieceY * j,
					paint1);
*/
			canvas.drawLine(spaceLeft - 5, mHeight + spaceTop - pieceY * j, spaceLeft /* + 5 */,
					mHeight + spaceTop - pieceY * j, paint2);

			paint2.setTextAlign(Paint.Align.CENTER);
			canvas.drawText(yValues[j], spaceLeft - 18, mHeight + spaceTop - pieceY * j + 5, paint2);
		}
	}

	/**
	 * 画三角形 用于画坐标轴的箭头
	 */
	private void drawTriangle(Canvas canvas, Point p1, Point p2, Point p3) {
		Path path = new Path();
		path.moveTo(p1.x, p1.y);
		path.lineTo(p2.x, p2.y);
		path.lineTo(p3.x, p3.y);
		path.close();

		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.FILL);
		// 绘制多边形
		canvas.drawPath(path, paint);

	}

}
