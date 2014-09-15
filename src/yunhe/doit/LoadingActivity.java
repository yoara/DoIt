package yunhe.doit;

import yunhe.database.UserInfoDBUtil;
import yunhe.model.UserInfoModel;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

public class LoadingActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);
		new InternetLinkTask().execute();
		
	}
	/** 线程类，用于设置背景图片 **/
	class InternetLinkTask extends AsyncTask<Void,Void,Void>{
		protected Void doInBackground(Void... params) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			UserInfoDBUtil dbUtil = UserInfoDBUtil.getInstance();
			UserInfoModel model = dbUtil.queryUserInfoForDetail(LoadingActivity.this);
			if(model!=null){
				goActivity(model.getInitAc());
			}else{
				goActivity("A");
			}
		}
	}
	private void goActivity(String flag) {
		Class loadClass = null; 
		if(flag==null||flag.equals("A")){
			loadClass = A_MainActivity.class;
		}else{
			loadClass = B_ListContentActivity.class;
		}
		Intent intent_list = new Intent(LoadingActivity.this,loadClass);
		startActivity(intent_list);
		finish();
	}
}
