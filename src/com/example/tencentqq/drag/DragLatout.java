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
		this(context,null);
	}

	public DragLatout(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}

	public DragLatout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		//1.��ʼ��������ͨ����̬������
		mHelper = ViewDragHelper.create(this, mCallback);
		
	}
	ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {
		
		//a�����ݷ��ؽ��������ǰchild�Ƿ���Ա�����
		//arg0 ��ǰ����ק��view�� arg1 ���ֶ�㴥����Id
		@Override
		public boolean tryCaptureView(View arg0, int arg1) {
			// TODO Auto-generated method stub
			return arg0 == mMainContent;
		}
		
		//b�����ݽ���ֵ������Ҫ�ƶ����ģ�����λ�á�
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			return left;
		};
	};

	
	//2.���ݴ����¼�
	public boolean onInterceptHoverEvent(MotionEvent event) {
		//���ݸ�mDrahHelpter
		return mHelper.shouldInterceptTouchEvent(event);
		
	}
	public boolean onTouchEvent(MotionEvent event) {
		try {
			mHelper.processTouchEvent(event);
		} catch (Exception e){
			e.printStackTrace();
		}
		//���������¼�
		return true;
		
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		//�ݴ��Լ��
		if(getChildCount()<2){
			throw new IllegalStateException("������������Ҫ2����View��Your ViewGroup must need 2 child view at least");
		}
		if(!(getChildAt(0) instanceof ViewGroup && getChildAt(1) instanceof ViewGroup)){
			throw new IllegalArgumentException("��View������ViewGroup������.Your View must instance of ViewGroup");
		}
		mLeftContent = (ViewGroup) getChildAt(0);
		mMainContent = (ViewGroup) getChildAt(1);
	}



















}
