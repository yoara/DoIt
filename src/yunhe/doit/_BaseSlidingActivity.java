package yunhe.doit;


import yunhe.database.UserInfoDBUtil;
import yunhe.model.UserInfoModel;
import yunhe.util.Constants;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public abstract class _BaseSlidingActivity extends SlidingFragmentActivity  {
	private ImageView mIvmain;
	private ImageView mIvList;
	private ImageView mIvInfo;
	private ImageView mIvMenu;
	protected TextView mTvGoToday;
	protected TextView mTvTitle;

	/** 设置标题栏 **/
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.a_menu_button_add:
			Intent intent_a_add = new Intent(this,A_EditContentActivity.class);
			startActivity(intent_a_add);
			return true;
		case R.id.b_menu_button_add:
			Intent intent_b_add = new Intent(this,B_EditContentActivity.class);
			startActivity(intent_b_add);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
		exitDialog();
	}
	
	private void exitDialog() {
        AlertDialog.Builder builder = new Builder(this); 
        builder.setMessage("确定要退出吗?"); 
        builder.setTitle("提示");
        builder.setPositiveButton("确认",
        new android.content.DialogInterface.OnClickListener() { 
            @Override 
            public void onClick(DialogInterface dialog, int which) { 
               dialog.dismiss();
               finish();
               android.os.Process.killProcess(android.os.Process.myPid()); 
               System.exit(0);
            } 
        });
        builder.setNegativeButton("取消", 
        new android.content.DialogInterface.OnClickListener() { 
            @Override 
            public void onClick(DialogInterface dialog, int which) { 
                dialog.dismiss(); 
            } 
        }); 
        builder.create().show(); 
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        // set the content view
		setOwnView();
		SlidingMenu menu = getSlidingMenu();
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadow);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        menu.setFadeDegree(0.55f);
        //设置菜单拉出的距离
        menu.setBehindOffset(120);
        //如果使用继承SlidingFragmentActivity方式，不需要这几行
		//SlidingMenu menu = new SlidingMenu(this);
        //menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        //menu.setMenu(R.menu.menu_frame);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.activity_actionbar_frame);
        mTvTitle = (TextView) getSupportActionBar().getCustomView().findViewById(R.id.actionbar_tv_title);
        mTvTitle.setBackground(getResources().getDrawable(R.drawable.dododoit));
        
        mIvMenu = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actionbar_iv_menu);
        mIvMenu.setOnClickListener(listener);
        mTvGoToday = (TextView) getSupportActionBar().getCustomView().findViewById(R.id.actionbar_iv_goToday);
        mTvGoToday.setOnClickListener(listener);
        
		mIvInfo = (ImageView) menu.findViewById(R.id.menu_iv_info);
		mIvInfo.setOnClickListener(listener);
		mIvmain = (ImageView) menu.findViewById(R.id.menu_tv_main);
		mIvmain.setOnClickListener(listener);
		mIvList = (ImageView) menu.findViewById(R.id.menu_tv_list);
		mIvList.setOnClickListener(listener);
	}
	
	/**所有按钮的监听器 **/
	private OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.menu_iv_info:
				Intent intent_info = new Intent(_BaseSlidingActivity.this,
						C_InfoActivity.class);
				UserInfoDBUtil dbUtil = UserInfoDBUtil.getInstance();
				UserInfoModel model = dbUtil.queryUserInfoForDetail
						(_BaseSlidingActivity.this);
				intent_info.putExtra(Constants.USERINFO_MODEL, model);
				startActivity(intent_info);
				break;
			case R.id.menu_tv_main:
				
				if(_BaseSlidingActivity.this instanceof A_MainActivity){
					toggle();
				}else{
					Intent intent_add = new Intent(_BaseSlidingActivity.this,
							A_MainActivity.class);
					startActivity(intent_add);
					finish();
				}
				break;
			case R.id.menu_tv_list:
				if(_BaseSlidingActivity.this instanceof B_ListContentActivity){
					toggle();
				}else{
					Intent intent_list = new Intent(_BaseSlidingActivity.this,
							B_ListContentActivity.class);
					startActivity(intent_list);
					finish();
				}
				break;
			case R.id.actionbar_iv_menu:
				toggle();
				break;
			case R.id.actionbar_iv_goToday:
				goToday();
				break;
			default:
				break;
			}
		}
	};
	
	/** 回到今天 **/
	protected abstract void goToday();
	/** 设置当前layout **/
	protected abstract void setOwnView();

	@Override
	protected void onResume() {
		super.onResume();
		new InternetLinkTask().execute();
	}
	/** 线程类，用于设置背景图片 **/
	class InternetLinkTask extends AsyncTask<Void,Void,Drawable>{
		protected Drawable doInBackground(Void... params) {
			/* 设置背景图片 */
			UserInfoDBUtil db = UserInfoDBUtil.getInstance();
			UserInfoModel model = db.queryUserInfoForDetail(_BaseSlidingActivity.this);
			if(model==null||model.getImgPath()==null){
				return null;
			}else{
				ContentResolver cr = _BaseSlidingActivity.this.getContentResolver();
				Uri uri = Uri.parse(model.getImgPath());
				Drawable drawable = null;
				try {
					drawable = BitmapDrawable.createFromStream(cr.openInputStream(uri)
							, "doit");
				} catch (Exception e) {
					e.printStackTrace();
				}
				return drawable;
			}
		}
		
		@Override
		protected void onPostExecute(Drawable result) {
			if(result==null){
				if(_BaseSlidingActivity.this instanceof B_ListContentActivity){
					findViewById(R.id.b_main_id)
						.setBackgroundResource(R.drawable.bg);
				}else{
					findViewById(R.id.a_main_id)
						.setBackgroundResource(R.drawable.bg);
				}
			}else{
				if(_BaseSlidingActivity.this instanceof B_ListContentActivity){
					findViewById(R.id.b_main_id).setBackground(result);
				}else{
					findViewById(R.id.a_main_id).setBackground(result);
				}
			}
		}
	}
}
