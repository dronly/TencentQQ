package com.example.tencentqq.drag;

import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class DragLatout extends FrameLayout {

	private ViewDragHelper mHelper;
	private ViewGroup mLeftContent;
	private ViewGroup mMainContent;

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
			// TODO Auto-generated method stub
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
		// 此时没有发生真正的移动	child 当前拖动的view left:新的位置的建议值，dx 位置的变化量   left = oldLeft+dx
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			if(child == mMainContent){
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
			if(changedView == mLeftContent){
				// 把当前变化量传递给mMainContent
				newLeft = mMainContent.getLeft() + dx;
			}
			
			// 进行修正
			newLeft = fixLeft(newLeft);
			
			if(changedView == mLeftContent) {
				// 当左面板移动之后, 再强制放回去.
				mLeftContent.layout(0, 0, 0 + mWidth, 0 + mHeight);
				mMainContent.layout(newLeft, 0, newLeft + mWidth, 0 + mHeight);
			}
			// 为了兼容低版本, 每次修改值之后, 进行重绘
			invalidate();
		}

		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			// TODO Auto-generated method stub
			super.onViewReleased(releasedChild, xvel, yvel);
		}

	};
	private int mHeight;
	private int mWidth;
	int mRange;

	// 2.传递触摸事件
	public boolean onInterceptHoverEvent(MotionEvent event) {
		// 传递给mDrahHelpter
		return mHelper.shouldInterceptTouchEvent(event);

	}

	/**
	 * 根据范围修正左边值
	 * @param left
	 * @return
	 */
	protected int fixLeft(int left) {
		if(left < 0){
			return 0;
		}else if (left > mRange) {
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
		// 容错性检查
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
