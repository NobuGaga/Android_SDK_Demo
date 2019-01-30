package com.greatefunhy.jpzc.ewan;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class EndClass extends Activity implements OnClickListener {
	public static final String MYACTIVITY_ACTION = "com.lqs.coe.nutsplay.EndClass";
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent dataIntent = getIntent();
        String packName = dataIntent.getStringExtra("packName");
        Log.e("YWEndClass", "-----重启-----"+packName);
        
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		ComponentName cn = new ComponentName(packName, "com.lqs.coe.nutsplay.MainActivity");
		intent.setComponent(cn);
		startActivity(intent);
		finish();
	}

	@Override
	public void onClick(View v) {
		
	}
	
}
