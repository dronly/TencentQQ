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

		// 1.��ʼ��������ͨ����̬������
		mHelper = ViewDragHelper.create(this, mCallback);

	}

	ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {

		// a�����ݷ��ؽ��������ǰchild�Ƿ���Ա�����
		// arg0 ��ǰ����ק��view�� arg1 ���ֶ�㴥����Id
		@Override
		public boolean tryCaptureView(View arg0, int arg1) {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public void onViewCaptured(View capturedChild, int activePointerId) {
			// ��capturedChild������ʱ����
			super.onViewCaptured(capturedChild, activePointerId);
		}

		@Override
		public int getViewVerticalDragRange(View child) {
			// ������ק�ķ�Χ��������ק�������������ƣ���������������ִ���ٶȡ�
			return mRange;
		}

		// b�����ݽ���ֵ������Ҫ�ƶ����ģ�����λ�á�
		// ��ʱû�з����������ƶ�	child ��ǰ�϶���view left:�µ�λ�õĽ���ֵ��dx λ�õı仯��   left = oldLeft+dx
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			if(child == mMainContent){
				left = fixLeft(left);
			}
			return left;
		}

		// c. ��Viewλ�øı��ʱ��, ����Ҫ�������� (����״̬, ���涯��, �ػ����)
		// ��ʱ,View�Ѿ�������λ�õĸı�
		@Override
		public void onViewPositionChanged(View changedView, int left, int top,
				int dx, int dy) {
			// changedView �ı�λ�õ�View
			// left �µ����ֵ
			// dx ˮƽ����仯��
			super.onViewPositionChanged(changedView, left, top, dx, dy);
			
			int newLeft = left;
			if(changedView == mLeftContent){
				// �ѵ�ǰ�仯�����ݸ�mMainContent
				newLeft = mMainContent.getLeft() + dx;
			}
			
			// ��������
			newLeft = fixLeft(newLeft);
			
			if(changedView == mLeftContent) {
				// ��������ƶ�֮��, ��ǿ�ƷŻ�ȥ.
				mLeftContent.layout(0, 0, 0 + mWidth, 0 + mHeight);
				mMainContent.layout(newLeft, 0, newLeft + mWidth, 0 + mHeight);
			}
			// Ϊ�˼��ݵͰ汾, ÿ���޸�ֵ֮��, �����ػ�
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

	// 2.���ݴ����¼�
	public boolean onInterceptHoverEvent(MotionEvent event) {
		// ���ݸ�mDrahHelpter
		return mHelper.shouldInterceptTouchEvent(event);

	}

	/**
	 * ���ݷ�Χ�������ֵ
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
		// ���������¼�
		return true;

	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		// �ݴ��Լ��
		if (getChildCount() < 2) {
			throw new IllegalStateException(
					"������������Ҫ2����View��Your ViewGroup must need 2 child view at least");
		}
		if (!(getChildAt(0) instanceof ViewGroup && getChildAt(1) instanceof ViewGroup)) {
			throw new IllegalArgumentException(
					"��View������ViewGroup������.Your View must instance of ViewGroup");
		}
		mLeftContent = (ViewGroup) getChildAt(0);
		mMainContent = (ViewGroup) getChildAt(1);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		mHeight = getMeasuredHeight();
		mWidth = getMeasuredWidth();

		// �ƶ��ķ�Χ
		mRange = (int) (mWidth * 0.6f);
	}

}
