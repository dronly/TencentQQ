package com.example.tencentqq;

import com.example.tencentqq.drag.DragLatout;
import com.example.tencentqq.drag.DragLatout.OnDragStatusChangeListener;
import com.example.tencentqq.util.Cheeses;
import com.example.tencentqq.util.Utils;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        
        final ListView mMainList = (ListView) findViewById(R.id.lv_main);
        final ListView mLeftList = (ListView) findViewById(R.id.lv_left);
        final ImageView mHeaderImage = (ImageView) findViewById(R.id.lv_head);
        
        DragLatout mDragLatout = (DragLatout) findViewById(R.id.dl);
        mDragLatout.setOnDragStatusChangeListener(new OnDragStatusChangeListener() {
			
			@Override
			public void onOpen() {
				Utils.showToast(MainActivity.this, "onOpen");
			}
			
			@Override
			public void onDraging(float percent) {
				
			}
			
			@Override
			public void onClose() {
				Utils.showToast(MainActivity.this, "onClose");
				
			}
		});
        
        mMainList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Cheeses.NAMES));
        mLeftList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Cheeses.sCheeseStrings){
        	@Override
        	public View getView(int position, View convertView, ViewGroup parent) {
        		
        		View view = super.getView(position, convertView, parent);
        		TextView mText = (TextView) view;
        		mText.setTextColor(Color.WHITE);
        		return view;
        	}
        });
    }
}
