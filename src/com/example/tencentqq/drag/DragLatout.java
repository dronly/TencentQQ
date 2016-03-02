package com.example.tencentqq.drag;

import com.nineoldandroids.view.ViewHelper;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class DragLatout extends FrameLayout {

	private static final String TAG = "TAG";
	private ViewDragHelper mHelper;
	private ViewGroup mLeftContent;
	private ViewGroup mMainContent;
	private OnDragStatusChangeListener mListener;
	private Status mStatus = Status.Close;

	/*
	 * 状态枚举
	 */
	public static enum  Status{
		Close, Open, Draging;
	}
	
	public interface OnDragStatusChangeListener{
		void onClose();
		void onOpen();
		void onDraging(float percent);
	}
	
	public Status getStatus(){
		return mStatus;
	}
	public void setStatus(Status mStatus) {
		this.mStatus = mStatus;
	}
	
	
	public void setOnDragStatusChangeListener(OnDragStatusChangeListener mListener){
		this.mListener = mListener;
		
	}
	
	public DragLatout(Context context) {
		this(context, null);
	}

	public DragLatout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DragLatout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		// 1.初始化操作（通过静态方法）
		mHelper = ViewDragHelper.create(this, mCallback);

	}

	ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {

		// a、根据返回结果决定当前child是否可以被滑动
		// arg0 当前被拖拽的view， arg1 区分多点触摸的Id
		@Override
		public boolean tryCaptureView(View arg0, int arg1) {
			Log.d(TAG, "tryCaptureView: " + arg0);
			return true;
		}

		@Override
		public void onViewCaptured(View capturedChild, int activePointerId) {
			// 当capturedChild被捕获时调用
			super.onViewCaptured(capturedChild, activePointerId);
		}

		@Override
		public int getViewVerticalDragRange(View child) {
			// 返回拖拽的范围，不对拖拽进行真正的限制，仅仅决定动画的执行速度。
			return mRange;
		}

		// b。根据建议值修正将要移动到的（横向）位置。
		// 此时没有发生真正的移动 child 当前拖动的view left:新的位置的建议值，dx 位置的变化量 left = oldLeft+dx
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			if (child == mMainContent) {
				left = fixLeft(left);
			}
			return left;
		}

		// c. 当View位置改变的时候, 处理要做的事情 (更新状态, 伴随动画, 重绘界面)
		// 此时,View已经发生了位置的改变
		@Override
		public void onViewPositionChanged(View changedView, int left, int top,
				int dx, int dy) {
			// changedView 改变位置的View
			// left 新的左边值
			// dx 水平方向变化量
			super.onViewPositionChanged(changedView, left, top, dx, dy);

			int newLeft = left;
			if (changedView == mLeftContent) {
				// 把当前变化量传递给mMainContent
				newLeft = mMainContent.getLeft() + dx;
			}

			// 进行修正
			newLeft = fixLeft(newLeft);

			if (changedView == mLeftContent) {
				// 当左面板移动之后, 再强制放回去.
				mLeftContent.layout(0, 0, 0 + mWidth, 0 + mHeight);
				mMainContent.layout(newLeft, 0, newLeft + mWidth, 0 + mHeight);
			}
			// 更新状态,执行动画
			dispatchDragEvent(newLeft);
			// 为了兼容低版本, 每次修改值之后, 进行重绘
			invalidate();
		}

		// d.当View被释放的时候, 处理的事情(执行动画)
		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			// View releasedChild 被释放的子View
			// float xvel 水平方向的速度, 向右为+
			// float yvel 竖直方向的速度, 向下为+
			super.onViewReleased(releasedChild, xvel, yvel);
			// 判断执行 关闭/开启
			// 先考虑所有开启的情况,剩下的就都是关闭的情况
			if (xvel == 0 && mMainContent.getLeft() > mRange / 2.0f) {
				open();
			} else if (xvel > 0) {
				open();
			} else {
				close();
			}
		}

		@Override
		public void onViewDragStateChanged(int state) {
			// TODO Auto-generated method stub
			super.onViewDragStateChanged(state);
		}
		

	};
	private int mHeight;
	private int mWidth;
	private int mRange;

	// 2.传递触摸事件
	public boolean onInterceptHoverEvent(MotionEvent event) {
		// 传递给mDrahHelpter
		return mHelper.shouldInterceptTouchEvent(event);

	}

	/*
	 * 为界面添加动画
	 */
	protected void dispatchDragEvent(int newLeft) {
		float percent = newLeft * 1.0f/ mRange;
		if (mListener != null) {
			mListener.onDraging(percent);
		}
		// 更新状态执行回调
		Status preStatus = mStatus;
		mStatus = updateStatus(percent);
//		当状态发生变化
		if(mStatus != preStatus){
			//当状态变为关闭状态
			if(mStatus == Status.Close){
				if(mListener != null){
					mListener.onClose();
				}
			} else if(mStatus == Status.Open){
				if(mListener != null) {
					mListener.onOpen();
				}
			}
			
		}
		
		//* 伴随动画:
		animViews(percent);
	}
	
	private Status updateStatus(float percent){
		if(percent == 0f){
			return Status.Close;
		}else if(percent == 1f){
			return Status.Open;
		}else {
			return Status.Draging;
		}
	}

	private void animViews(float percent) {
//		> 1. 左面板: 缩放动画, 平移动画, 透明度动画
		// 缩放动画 0.0 -> 1.0 >>> 0.5f -> 1.0f  >>> 0.5f * percent + 0.5f
//		mLeftContent.setScaleX(0.5f + 0.5f * percent);
//		mLeftContent.setScaleY(0.5f + 0.5f * percent);
		ViewHelper.setScaleX(mLeftContent, evaluate(percent, 0.5f, 1.0f));
		ViewHelper.setScaleY(mLeftContent, 0.5f + 0.5f * percent);
		// 平移动画: -mWidth / 2.0f -> 0.0f
		ViewHelper.setTranslationX(mLeftContent, evaluate(percent, -mWidth / 2.0f, 0));
		// 透明度: 0.5 -> 1.0f
		ViewHelper.setAlpha(mLeftContent, evaluate(percent, 0.5f, 1.0f));
	
//		> 2. 主面板: 缩放动画
		// 1.0f -> 0.8f
		ViewHelper.setScaleX(mMainContent, evaluate(percent, 1.0f, 0.8f));
		ViewHelper.setScaleY(mMainContent, evaluate(percent, 1.0f, 0.8f));
		
//		> 3. 背景动画: 亮度变化 (颜色变化)
		getBackground().setColorFilter((Integer)evaluateColor(percent, Color.BLACK, Color.TRANSPARENT), Mode.SRC_OVER);
		
	}
	
	/**
     * 估值器
     * @param fraction
     * @param startValue
     * @param endValue
     * @return
     */
    public Float evaluate(float fraction, Number startValue, Number endValue) {
        float startFloat = startValue.floatValue();
        return startFloat + fraction * (endValue.floatValue() - startFloat);
    }
    /**
     * 颜色变化过度
     * @param fraction
     * @param startValue
     * @param endValue
     * @return
     */
    public Object evaluateColor(float fraction, Object startValue, Object endValue) {
        int startInt = (Integer) startValue;
        int startA = (startInt >> 24) & 0xff;
        int startR = (startInt >> 16) & 0xff;
        int startG = (startInt >> 8) & 0xff;
        int startB = startInt & 0xff;

        int endInt = (Integer) endValue;
        int endA = (endInt >> 24) & 0xff;
        int endR = (endInt >> 16) & 0xff;
        int endG = (endInt >> 8) & 0xff;
        int endB = endInt & 0xff;

        return (int)((startA + (int)(fraction * (endA - startA))) << 24) |
                (int)((startR + (int)(fraction * (endR - startR))) << 16) |
                (int)((startG + (int)(fraction * (endG - startG))) << 8) |
                (int)((startB + (int)(fraction * (endB - startB))));
    }


	@Override
	public void computeScroll() {
		super.computeScroll();

		// 2. 持续平滑动画 (高频率调用)
		if (mHelper.continueSettling(true)) {
			// 如果返回true, 动画还需要继续执行
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}

	public void close() {
		close(true);
	}

	private void close(boolean isSmooth) {
		int finalLeft = 0;
		if (isSmooth) {
			// 1. 触发一个平滑动画
			if (mHelper.smoothSlideViewTo(mMainContent, finalLeft, 0)) {
				// 返回true代表还没有移动到指定位置, 需要刷新界面.
				// 参数传this(child所在的ViewGroup)
				ViewCompat.postInvalidateOnAnimation(this);
			}
		} else {
			mMainContent.layout(finalLeft, 0, finalLeft + mWidth, 0 + mHeight);
		}
	}

	public void open() {
		open(true);
	}

	private void open(boolean isSmooth) {
		int finalLeft = mRange;
		if (isSmooth) {
			// 1. 触发一个平滑动画
			if (mHelper.smoothSlideViewTo(mMainContent, finalLeft, 0)) {
				// 返回true代表还没有移动到指定位置, 需要刷新界面.
				// 参数传this(child所在的ViewGroup)
				ViewCompat.postInvalidateOnAnimation(this);
			}
		} else {
			mMainContent.layout(finalLeft, 0, finalLeft + mWidth, 0 + mHeight);
		}
	}

	/**
	 * 根据范围修正左边值
	 * 
	 * @param left
	 * @return
	 */
	protected int fixLeft(int left) {
		if (left < 0) {
			return 0;
		} else if (left > mRange) {
			return mRange;
		}
		return left;
	}

	public boolean onTouchEvent(MotionEvent event) {
		try {
			mHelper.processTouchEvent(event);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 持续接受事件
		return true;

	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		//容错性检查
		if (getChildCount() < 2) {
			throw new IllegalStateException(
					"布局中至少需要2个字View。Your ViewGroup must need 2 child view at least");
		}
		if (!(getChildAt(0) instanceof ViewGroup && getChildAt(1) instanceof ViewGroup)) {
			throw new IllegalArgumentException(
					"子View必须是ViewGroup的子类.Your View must instance of ViewGroup");
		}
		mLeftContent = (ViewGroup) getChildAt(0);
		mMainContent = (ViewGroup) getChildAt(1);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		mHeight = getMeasuredHeight();
		mWidth = getMeasuredWidth();

		// 移动的范围
		mRange = (int) (mWidth * 0.6f);
	}

}
