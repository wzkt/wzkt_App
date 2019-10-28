package com.Alan.eva.ui.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;

import com.Alan.eva.R;

import static com.Alan.eva.R.styleable.HoloCircleSeekBar;

public class TempCircleView extends View {
	private static final int TEXT_SIZE_DEFAULT_VALUE = 25;
	private static final int DTEXT_SIZE_DEFAULT_VALUE = 25;
	private static final int END_WHEEL_DEFAULT_VALUE = 360;
	public static final int COLOR_WHEEL_STROKE_WIDTH_DEF_VALUE = 30;
	public static final float POINTER_RADIUS_DEF_VALUE = 8;
	public static final int MAX_POINT_DEF_VALUE = 100;
	public static final int START_ANGLE_DEF_VALUE = 0;
	private Paint mColorWheelPaint;
	private Paint mPointerHaloPaint;
	private Paint mPointerColor;
	private float mColorWheelStrokeWidth;
	private float mPointerRadius;
	private RectF mColorWheelRectangle = new RectF();
	private float mTranslationOffset;
	private float mColorWheelRadius;
	private float mAngle;
	private Paint textPaint;
	private Paint detectionPaint;
	private String detectionSite;
	private float deletWidth;// 不同分辨率需要减的数 保证圆环间距
	private int max = 100;
	private Paint mArcColor;
	private int wheel_color;
	private int unactive_wheel_color;
	private int pointer_color;
	private int pointer_halo_color;
	private int text_size;
	private int text_color;
	private int detectionText_color;
	private int detectionText_size;
	private int init_position = -1;
	private int arc_finish_radians = 360;
	private int start_arc = 270;

	private float[] pointerPosition;
	private RectF mColorCenterHaloRectangle = new RectF();
	private int end_wheel;
	private Rect bounds = new Rect();
	private Rect detectionBounds = new Rect();
	private String tempValues = "00.0";

	private float oldAngle = 0;// 之前的角度

	public TempCircleView(Context context) {
		super(context);
		init(null, 0);
	}

	public TempCircleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs, 0);
	}

	public TempCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(attrs, defStyleAttr);
	}

	private void init(AttributeSet attrs, int defStyle) {
		final TypedArray a = getContext().obtainStyledAttributes(attrs, HoloCircleSeekBar, defStyle, 0);
		initAttributes(a);
		a.recycle();
		/**
		 * mColorWheelPaint为大圆环的画笔
		 */
		mColorWheelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mColorWheelPaint.setColor(unactive_wheel_color);
		mColorWheelPaint.setStyle(Paint.Style.STROKE); // 设置空心
		mColorWheelPaint.setStrokeWidth(2);
		Paint mColorCenterHalo = new Paint(Paint.ANTI_ALIAS_FLAG);
		mColorCenterHalo.setColor(Color.CYAN);
		mColorCenterHalo.setAlpha(0xCC);

		mPointerHaloPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPointerHaloPaint.setColor(pointer_halo_color);
		mPointerHaloPaint.setStrokeWidth(mPointerRadius);
		/**
		 * 光晕效果的画笔
		 */
		Paint lightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		lightPaint.setAlpha(40);
		lightPaint.setFilterBitmap(true);// 滤波处理
		lightPaint.setDither(true);

		/**
		 * 测量部位名称的画笔
		 */
		detectionPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
		detectionPaint.setColor(detectionText_color);
		detectionPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		detectionPaint.setTextAlign(Paint.Align.LEFT);
		detectionPaint.setTextSize(detectionText_size);

		Paint heartRatePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		heartRatePaint.setFilterBitmap(true);
		heartRatePaint.setDither(true);

		textPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
		textPaint.setColor(text_color);
		textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		textPaint.setTextAlign(Paint.Align.LEFT);
		textPaint.setTextSize(text_size);

		/**
		 * 进度条的圆的画笔
		 */
		mPointerColor = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPointerColor.setStrokeWidth(mPointerRadius);
		mPointerColor.setColor(pointer_color);
		/**
		 * 显示进度的画笔
		 */
		mArcColor = new Paint(Paint.ANTI_ALIAS_FLAG);
		mArcColor.setColor(wheel_color);
		mArcColor.setStyle(Paint.Style.STROKE);
		mArcColor.setStrokeWidth(2);

		Paint mCircleTextColor = new Paint(Paint.ANTI_ALIAS_FLAG);
		mCircleTextColor.setColor(Color.WHITE);
		mCircleTextColor.setStyle(Paint.Style.FILL);

		arc_finish_radians = (int) calculateAngleFromText(init_position) - 90;

		if (arc_finish_radians > end_wheel)
			arc_finish_radians = end_wheel;
		mAngle = calculateAngleFromRadians(arc_finish_radians > end_wheel ? end_wheel : arc_finish_radians);
		setTextFromAngle();
		detectionSite = "心率";

		invalidate();

	}

	private void initAttributes(TypedArray a) {
		mColorWheelStrokeWidth = a.getDimension(R.styleable.HoloCircleSeekBar_wheel_size,
				COLOR_WHEEL_STROKE_WIDTH_DEF_VALUE);
		mPointerRadius = a.getDimension(R.styleable.HoloCircleSeekBar_pointer_size, POINTER_RADIUS_DEF_VALUE);
		max = a.getInteger(R.styleable.HoloCircleSeekBar_max, MAX_POINT_DEF_VALUE);
		deletWidth = a.getDimension(R.styleable.HoloCircleSeekBar_circle_with, COLOR_WHEEL_STROKE_WIDTH_DEF_VALUE);

		String wheel_color_attr = a.getString(R.styleable.HoloCircleSeekBar_wheel_active_color);
		String wheel_unactive_color_attr = a.getString(R.styleable.HoloCircleSeekBar_wheel_unactive_color);
		String pointer_color_attr = a.getString(R.styleable.HoloCircleSeekBar_pointer_color);
		String pointer_halo_color_attr = a.getString(R.styleable.HoloCircleSeekBar_pointer_halo_color);
		String detectionText_color_attr = a.getString(R.styleable.HoloCircleSeekBar_detectionText_color);
		String text_color_attr = a.getString(R.styleable.HoloCircleSeekBar_text_color);

		text_size = a.getDimensionPixelSize(R.styleable.HoloCircleSeekBar_text_size, TEXT_SIZE_DEFAULT_VALUE);

		detectionText_size = a.getDimensionPixelSize(R.styleable.HoloCircleSeekBar_detectionText_size,
				DTEXT_SIZE_DEFAULT_VALUE);
		init_position = a.getInteger(R.styleable.HoloCircleSeekBar_init_position, 0);

		start_arc = a.getInteger(R.styleable.HoloCircleSeekBar_start_angle, START_ANGLE_DEF_VALUE);
		Log.e("___________", start_arc + "");
		Log.e("___________", init_position + "");
		end_wheel = a.getInteger(R.styleable.HoloCircleSeekBar_end_angle, END_WHEEL_DEFAULT_VALUE);

		if (init_position < start_arc) {
			init_position = calculateTextFromStartAngle(start_arc);
		}
		if (wheel_color_attr != null) {
			try {
				wheel_color = Color.parseColor(wheel_color_attr);
			} catch (IllegalArgumentException e) {
				wheel_color = Color.DKGRAY;
			}
		} else {
			wheel_color = Color.DKGRAY;
		}
		if (wheel_unactive_color_attr != null) {
			try {
				unactive_wheel_color = Color.parseColor(wheel_unactive_color_attr);
			} catch (IllegalArgumentException e) {
				unactive_wheel_color = Color.CYAN;
			}
		} else {
			unactive_wheel_color = Color.CYAN;
		}
		if (pointer_color_attr != null) {
			try {
				pointer_color = Color.parseColor(pointer_color_attr);
			} catch (IllegalArgumentException e) {
				pointer_color = Color.CYAN;
			}

		} else {
			pointer_color = Color.CYAN;
		}
		if (pointer_halo_color_attr != null) {
			try {
				pointer_halo_color = Color.parseColor(pointer_halo_color_attr);
			} catch (IllegalArgumentException e) {
				pointer_halo_color = Color.CYAN;
			}

		} else {
			pointer_halo_color = Color.DKGRAY;
		}
		if (text_color_attr != null) {
			try {
				text_color = Color.parseColor(text_color_attr);
			} catch (IllegalArgumentException e) {
				text_color = Color.CYAN;
			}
		} else {
			text_color = Color.CYAN;
		}
		if (detectionText_color_attr != null) {
			try {
				detectionText_color = Color.parseColor(text_color_attr);
			} catch (IllegalArgumentException e) {
				detectionText_color = Color.CYAN;
			}
		} else {
			detectionText_color = Color.CYAN;
		}
	}

	private int calculateTextFromStartAngle(float angle) {
		float f = (end_wheel - start_arc) / angle;

		return (int) (max / f);
	}

	/**
	 * 算出精度条走的位置是多少度
	 *
	 * @param position pos
	 * @return double
	 */
	private double calculateAngleFromText(int position) {
		if (position == 0 || position >= max)
			return (float) 90;

		double f = (double) max / (double) position;

		double f_r = 360 / f;

		return f_r + 90;
	}

	private float calculateAngleFromRadians(int radians) {
		return (float) (((radians + 270) * (2 * Math.PI)) / 360);
	}

	private int calculateValueFromAngle(float angle) {
		float m = angle - start_arc;

		float f = (end_wheel - start_arc) / m;

		return (int) (max / f);
	}

	private void setTextFromAngle() {
	}

	protected void onDraw(Canvas canvas) {
		canvas.translate(mTranslationOffset, mTranslationOffset);
		/*
		 * lightPaint.setShader(new
		 * RadialGradient(mColorWheelRectangle.centerX(),
		 * mColorWheelRectangle.centerY(), mColorWheelRadius + 20,
		 * Color.parseColor("#80B9FF"), Color.parseColor("#FFFFFF"),
		 * Shader.TileMode.CLAMP));
		 */

		
		canvas.drawArc(mColorWheelRectangle, start_arc + 270, end_wheel - (start_arc), false, mColorWheelPaint);// 里层圆环
		/*
		 * canvas.drawArc(mColorWheelRectangle, start_arc + 270,
		 * (arc_finish_radians) > (end_wheel) ? end_wheel - (start_arc) :
		 * arc_finish_radians - start_arc, false, mArcColor);//进度条圆环
		 */
		canvas.drawCircle(mColorWheelRectangle.centerX(), mColorWheelRectangle.centerY(), mColorWheelRadius + deletWidth,
				mArcColor);

		canvas.drawCircle(pointerPosition[0], pointerPosition[1], mColorWheelStrokeWidth, mPointerHaloPaint);

		canvas.drawCircle(pointerPosition[0], pointerPosition[1], mColorWheelStrokeWidth / 3, mPointerColor);
		textPaint.getTextBounds(tempValues, 0, tempValues.length(), bounds);// Paint.getTextBounds:
																			// 当你通过这个方法来获取尺寸的时候，你可以得到能够包裹文字的最小矩形
		detectionPaint.getTextBounds(detectionSite, 0, detectionSite.length(), detectionBounds);
		// lightBitmap = ((BitmapDrawable)
		// getResources().getDrawable(R.drawable.background, null)).getBitmap();
		// canvas.drawBitmap(lightBitmap, mColorWheelRectangle.centerX() -
		// mColorWheelRadius, mColorWheelRectangle.centerY() -
		// mColorWheelRadius, lightPaint);
		/*
		 * if (show_text) { canvas.drawText(tempvalues,
		 * (mColorWheelRectangle.centerX()) - (textPaint.measureText(tempvalues)
		 * / 2), mColorWheelRectangle.centerY(), textPaint); }
		 */
		/*
		 * canvas.drawText(detectionSite, (mColorWheelRectangle.centerX()) -
		 * (detectionPaint.measureText(detectionSite) / 2),
		 * mColorWheelRectangle.centerY() + detectionBounds.height() + 10,
		 * detectionPaint);
		 */
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
		int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
		int min = Math.min(width, height);
		setMeasuredDimension(min, min);
		mTranslationOffset = min * 0.5f;
		mColorWheelRadius = mTranslationOffset - mPointerRadius;
		mColorWheelRectangle.set(-mColorWheelRadius, -mColorWheelRadius, mColorWheelRadius, mColorWheelRadius);
		mColorCenterHaloRectangle.set(-mColorWheelRadius / 2, -mColorWheelRadius / 2, mColorWheelRadius / 2,
				mColorWheelRadius / 2);

		updatePointerPosition();
	}

	private void updatePointerPosition() {
		pointerPosition = calculatePointerPosition(mAngle);
	}

	private float[] calculatePointerPosition(float angle) {
		// if (calculateRadiansFromAngle(angle) > end_wheel)
		// angle = calculateAngleFromRadians(end_wheel);
		float x = (float) ((mColorWheelRadius + 10) * Math.cos(angle));
		float y = (float) ((mColorWheelRadius + 10) * Math.sin(angle));

		return new float[] { x, y };
	}

	public void setValue(float newValue, final float showValues) {

		float newAngle = (float) (360.0 * (newValue / max));

		ValueAnimator valueAnimator = ValueAnimator.ofFloat(oldAngle, newAngle);
		valueAnimator.setDuration(600);
		// 设置加速度
		valueAnimator.setInterpolator((Interpolator) v -> 1 - (1 - v) * (1 - v) * (1 - v));
		valueAnimator.addUpdateListener(valueAnimator1 -> {
            oldAngle = (float) valueAnimator1.getAnimatedValue();
            arc_finish_radians = (int) calculateAngleFromRadians(calculateRadiansFromAngle(oldAngle));
            mAngle = calculateAngleFromRadians(arc_finish_radians);
            setTextFromAngle();
            tempValues = String.valueOf(showValues);
            Log.d("TAG", "cuurent value is " + oldAngle);
            updatePointerPosition();
            invalidate();
        });
		valueAnimator.start();
	}

	private int calculateRadiansFromAngle(float angle) {
		float unit = (float) (angle / (2 * Math.PI));
		if (unit < 0) {
			unit += 1;
		}
		int radians = (int) ((unit * 360) - ((360 / 4) * 3));
		if (radians < 0)
			radians += 360;
		return radians;
	}

	public void setInitPosition(int init) {
		init_position = init;
		setTextFromAngle();
		mAngle = calculateAngleFromRadians(init_position);
		arc_finish_radians = calculateRadiansFromAngle(mAngle);
		updatePointerPosition();
		invalidate();
	}
}
