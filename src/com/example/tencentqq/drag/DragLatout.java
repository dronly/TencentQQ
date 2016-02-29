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
		
		//1.初始化操作（通过静态方法）
		mHelper = ViewDragHelper.create(this, mCallback);
		
	}
	ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {
		
		//a、根据返回结果决定当前child是否可以被滑动
		//arg0 当前被拖拽的view， arg1 区分多点触摸的Id
		@Override
		public boolean tryCaptureView(View arg0, int arg1) {
			// TODO Auto-generated method stub
			return arg0 == mMainContent;
		}
		
		//b。根据建议值修正将要移动到的（横向）位置。
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			return left;
		};
	};

	
	//2.传递触摸事件
	public boolean onInterceptHoverEvent(MotionEvent event) {
		//传递给mDrahHelpter
		return mHelper.shouldInterceptTouchEvent(event);
		
	}
	public boolean onTouchEvent(MotionEvent event) {
		try {
			mHelper.processTouchEvent(event);
		} catch (Exception e){
			e.printStackTrace();
		}
		//持续接受事件
		return true;
		
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		//容错性检查
		if(getChildCount()<2){
			throw new IllegalStateException("布局中至少需要2个字View。Your ViewGroup must need 2 child view at least");
		}
		if(!(getChildAt(0) instanceof ViewGroup && getChildAt(1) instanceof ViewGroup)){
			throw new IllegalArgumentException("子View必须是ViewGroup的子类.Your View must instance of ViewGroup");
		}
		mLeftContent = (ViewGroup) getChildAt(0);
		mMainContent = (ViewGroup) getChildAt(1);
	}



















}
