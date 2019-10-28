package com.Alan.eva.ui.activity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


import com.Alan.eva.model.XYChartData;
import com.github.mikephil.charting.utils.EntryXComparator;

import java.util.ArrayList;

public class LineCharViewY extends View {
	private int mWidth; // X轴长度
	private int mHeight; // Y轴长度

	private int countX = 5; // X轴坐标数
	private int countY = 3; // Y轴坐标数

	private String unitX; // X轴单位
	private String unitY; // Y轴单位
	private String[] coordinateX; // X轴坐标
	private int maxDataY = 1; // Y轴坐标最大值
	private int spaceLeft =40; // 与左边缘的距离
	private int spaceBottom = 20 + 20; // 与下边缘的距离
	private int spaceTop = 40; // 与上边缘的距离
	private int spaceRight = 30 + 5; // 与右边缘的距离
	private int paintColor = 0; // 画笔颜色
	private int countLine = 0; // 计算线数量

	private Context context;

	private ArrayList<XYChartData> lineList; // 线列表

	public LineCharViewY(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		lineList = new ArrayList<XYChartData>();
	}

	public LineCharViewY(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		lineList = new ArrayList<XYChartData>();
	}

	// 获取X轴的坐标数
	public int getCountX() {
		return countX;
	}

	// 设置X轴的坐标数
	public void setCountX(int countX) {
		this.countX = countX;
	}

	// 获取Y轴的坐标数
	public int getCountY() {
		return countY;
	}

	// 设置Y轴的坐标数
	public void setCountY(int countY) {
		this.countY = countY;
	}

	// 返回X坐标轴单位
	public String getUnitX() {
		return unitX;
	}

	// 设置X坐标轴单位
	public void setUnitX(String unitX) {
		this.unitX = unitX;
	}

	// 返回y坐标轴单位
	public String getUnitY() {
		return unitY;
	}

	// 设置y坐标轴单位
	public void setUnitY(String unitY) {
		this.unitY = unitY;
	}

	// 设置Y值最大值
	public void findMaxDataY() {
		for (int j = 0; j < lineList.size(); j++) {
			for (int i = 0; i < lineList.get(j).getCoordinateY().length; i++) {
				if (maxDataY < lineList.get(j).getCoordinateY()[i])
					maxDataY = (int) (lineList.get(j).getCoordinateY()[i] + 10);
			}
		}
		while (maxDataY % (countY * 10) != 0) {
			maxDataY++;
		}

	}

	public int getPaintColor() {
		return paintColor;
	}

	public void setPaintColor(int paintColor) {
		this.paintColor = paintColor;
	}

	/*
	 * 控件创建完成之后，在显示之前都会调用这个方法，此时可以获取控件的大小 并得到原点坐标的点。
	 */
	@Override
	public void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
		mWidth = width - spaceLeft - spaceRight; // 宽度
		mHeight = height - spaceTop - spaceBottom - 80; // 高度

		super.onSizeChanged(width, height, oldWidth, oldHeight);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int with = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		setMeasuredDimension(1800, height);
	}

	/*
	 * 重载onDraw(Canvas canvas)方法，来绘制图形
	 */
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 横纵坐标的标题
		Paint paint = new Paint();
		paint.setStrokeWidth(3);
		paint.setAntiAlias(true);
		paint.setColor(Color.GRAY);
		paint.setTextSize(40);
		// 画坐标轴
		if (canvas != null) {
			canvas.drawColor(Color.TRANSPARENT);
			// 画XY轴直线
			canvas.drawLine(spaceLeft, mHeight + spaceTop, mWidth + spaceLeft, mHeight + spaceTop, paint);
			canvas.drawLine(spaceLeft, mHeight + spaceTop, spaceLeft, spaceTop, paint);

			// 画X轴箭头
			int x = mWidth + spaceLeft, y = mHeight + spaceTop;
			drawTriangle(canvas, new Point(x, y), new Point(x - 10, y - 5), new Point(x - 10, y + 5));
			canvas.drawText(unitX, x - 50, y + 60, paint);
			// 画Y轴箭头
			x = spaceLeft;
			y = spaceTop;
			drawTriangle(canvas, new Point(x, y), new Point(x - 5, y + 10), new Point(x + 5, y + 10));
			canvas.drawText(unitY, x + 12, y + 15, paint);

			// 画坐标轴刻度线
			int pieceX = mWidth / countX, pieceY = mHeight / countY;
			drawBase(canvas, pieceX, pieceY);
			for (int i = 0; i < countLine; i++) {
				try {
					drawLine(canvas, pieceX, lineList.get(i));
				}catch (Exception e){

				}
			}
		}
	}
	private   boolean isshow  =true;
	public void setisshow(boolean isd){
		isshow = isd;
	}

	/**
	 * 画折线
	 */
	private void drawLine(Canvas canvas, int pieceX, XYChartData line) {
		Paint pLine = new Paint();
		pLine.setStrokeWidth(3);// 改变折线连线的粗细
		pLine.setColor(Color.parseColor("#10a3f0"));
		pLine.setAntiAlias(true);

		// 折线上数字的大小
		Paint pTag = new Paint();
		pTag.setStrokeWidth(2);
		pTag.setColor(Color.BLACK);
		pTag.setAntiAlias(true);
		pTag.setTextSize(20);

		final float[] y = line.getCoordinateY();

		// 绘制实际曲线图
		for (int i = 0; i < countX; i++) {

			Log.e("hjs ","isshow=="+isshow);
			if(isshow) {
				canvas.drawText(((int) y[i]) + "", spaceLeft + pieceX * (i) + 3, mHeight + spaceTop - mHeight * y[i] / maxDataY - 10,	pTag);

				canvas.drawCircle(spaceLeft + pieceX * (i), mHeight + spaceTop - mHeight * y[i] / maxDataY, 6, pLine);
			}

			if (i < countX - 1) {
				canvas.drawLine(spaceLeft + pieceX * (i), mHeight + spaceTop - mHeight * y[i] / maxDataY,
						spaceLeft + pieceX * (i + 1), mHeight + spaceTop - mHeight * y[i + 1] / maxDataY, pLine);
			}
		}
	}

	private void drawBase(Canvas canvas, int pieceX, int pieceY) {
		Paint paint = new Paint();
		paint.setStrokeWidth(3);
		paint.setAntiAlias(true);
		paint.setTextSize(25);
		paint.setColor(Color.parseColor("#10a3f0"));

		Paint paint2 = new Paint();
		paint2.setStrokeWidth(3);
		paint2.setAntiAlias(true);
		paint2.setTextSize(25);
		paint2.setColor(Color.BLACK);

		Paint paint1 = new Paint();
		paint1.setStrokeWidth(1);
		paint1.setAntiAlias(true);
		paint1.setColor(Color.GRAY);

		// 画X轴刻度
		for (int i = 0; i < countX; i++) {

			// 绘制X轴网格
			/*
			 * canvas.drawLine(spaceLeft + pieceX * i, mHeight, spaceLeft +
			 * pieceX i, mHeight + spaceTop + 5, paint1);
			 */

			paint.setTextSize(30);

			canvas.drawLine(spaceLeft + pieceX * i, mHeight + spaceTop/* - 5 */, spaceLeft + pieceX * i,
					mHeight + spaceTop + 5, paint);
			paint.setTextAlign(Paint.Align.CENTER);
			canvas.drawText(coordinateX[i], spaceLeft + pieceX * (i), mHeight + spaceTop + 5 + 25, paint);
		}

		// 画Y轴刻度
		for (int j = 0; j < countY; j++) {
			// 绘制Y网格
			canvas.drawLine(spaceLeft, mHeight + spaceTop - pieceY * j, mWidth, mHeight + spaceTop - pieceY * j,
					paint1);

			canvas.drawLine(spaceLeft - 5, mHeight + spaceTop - pieceY * j, spaceLeft /* + 5 */,
					mHeight + spaceTop - pieceY * j, paint2);

			paint2.setTextAlign(Paint.Align.CENTER);

			if(setflag){
				try {
					canvas.drawText(yValues[j]+"", spaceLeft - 18, mHeight + spaceTop - pieceY * j + 5, paint2);
				}catch (Exception e){

				}
			}else {
				canvas.drawText(maxDataY * j / countY + "", spaceLeft - 18, mHeight + spaceTop - pieceY * j + 5, paint2);

			}
		}
	}

	String[] yValues = {"未测","健康","发烧"};

	private boolean setflag = false;
	public void setyValuse(boolean falg){
		setflag = falg;
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

	/**
	 * @param @param
	 *            dataY 数据数组
	 */
	public void addLine(String lineName, String[] coordinateX, float[] dataY) {
		int paintColor = 0;
		countX = coordinateX.length;
		paintColor = Color.BLUE;

		countLine++;
		lineList.add(new XYChartData(lineName, coordinateX, dataY, paintColor));

		findMaxDataY(); // 找出纵坐标的最大值

		this.coordinateX = coordinateX;

	}
}
