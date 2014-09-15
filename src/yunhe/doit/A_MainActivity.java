package yunhe.doit;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import yunhe.database.ContentDBUtil;
import yunhe.database.UserInfoDBUtil;
import yunhe.model.ActivityShowContentModel;
import yunhe.model.ContentModel;
import yunhe.receiver.AlarmReceiver;
import yunhe.util.Constants;
import yunhe.util.DateUtil;
import yunhe.util.ListTitleGradientColorEnum;
import yunhe.view.SwipeDismissListView;
import yunhe.view.SwipeDismissListView.OnDismissCallback;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ViewHolder")
public class A_MainActivity extends _BaseSlidingActivity {
	private GridView gridDate_day;
	
	/** 日期栏的数据list [0]日,[1]星期,[2]整体,[3]yyyy,[4]月**/
	private List<String[]> dateList = new ArrayList<String[]>();
	/** 偏差日期，初始加载为0 **/
	private int dateRate = 0;
	/** 日期栏的适配器，用于GridView容器的信息变更 **/
	private GridViewAdapter adapter_day ;
	
	private SwipeDismissListView listTitle;
	/** 信息栏的数据集合 **/
	List<Map<String, Object>> listItem = new ArrayList<Map<String, Object>>();
	List<Map<String, Object>> listItemDone = new ArrayList<Map<String, Object>>();
	List<Map<String, Object>> listItemAll = new ArrayList<Map<String, Object>>();
	/** 信息栏的适配器，用于信息ListView容器的信息变更 **/
	private SimpleAdapter listItemAdapter;
	
	public void setDATE_IN(int position) {
		int dateIn = dateRate+position-DateUtil.returnTodayAround();
		A_EditContentActivity.setDATE_IN(dateIn);
	}
	
	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		getSupportMenuInflater().inflate(R.menu.a_button_menu, menu);
		return true;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//初始化时间
		DateUtil.returnTodayAround();
		initView(getSlidingMenu());
	}
	@Override
	protected void onResume() {
		super.onResume();
		initView(getSlidingMenu());
		
		//增加默认加载项
		UserInfoDBUtil db = UserInfoDBUtil.getInstance();
		db.updateActivityFlag("A",this);
	}
	/** 初始化视图控件  **/
	private void initView(SlidingMenu menu) {
		initGridDateView();
		initListTitleView();
	}

	/** 初始化日期栏视图 **/
	private void initGridDateView() {
		gridDate_day = (GridView) findViewById(R.id.main_day_gv);
		
		adapter_day = new GridViewAdapter(GridViewAdapter.DAY);
		gridDate_day.setAdapter(adapter_day);
		
		DisplayMetrics dm = new DisplayMetrics();
		/*
		 * DisplayMetrics可以得到分辨率等信息，方法如下：DisplayMetrics
		 * metrics;getWindowManager().getDefaultDisplay().getMetrics(metrics);
		 * metrics.widthPixels 屏幕宽metrics.heightPixels 屏幕高metrics.density 屏幕密度
		 */
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		dateList = DateUtil.returnRoundMonth(dateRate);
		int allWidth = (int) (dm.widthPixels);
		int itemWidth = (int) (dm.widthPixels / dateList.size());
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				allWidth, LinearLayout.LayoutParams.MATCH_PARENT);
		
		gridDate_day.setLayoutParams(params);
		gridDate_day.setColumnWidth(itemWidth);
		gridDate_day.setHorizontalSpacing(0);
		gridDate_day.setStretchMode(GridView.NO_STRETCH);
		gridDate_day.setNumColumns(dateList.size());
		gridDate_day.setOnTouchListener(gridlistener);
	}
	
	/** 初始化信息栏视图 **/
	private void initListTitleView() {
		//查询结果
		ContentDBUtil dbUtil = new ContentDBUtil();
		listItem = dbUtil.queryContentForList(
				DateUtil.returnDateTo_yyyy_MM_dd(new Date()),ContentModel.ISDONE_NOT, this);
		listItemDone = dbUtil.queryContentForList(
				DateUtil.returnDateTo_yyyy_MM_dd(new Date()),ContentModel.ISDONE, this);
		listItemAll.clear();
		listItemAll.addAll(listItem);
		listItemAll.addAll(listItemDone);
		// 生成适配器的Item和动态数组对应的元素
		listItemAdapter = new SimpleAdapter(this, listItemAll,// 数据源
			R.layout.a_main_listitem_title,// ListItem的XML实现
			// 动态数组与ImageItem对应的子项
			new String[] {ActivityShowContentModel.ITEM_TIME,
				ActivityShowContentModel.ITEM_TITLE,
				ActivityShowContentModel.ITEM_ISDONE,
				ActivityShowContentModel.ITEM_ISALARM},
			// ImageItem的XML文件里面的一个ImageView,两个TextView ID
			new int[] { R.id.tv_contentlist_time,
				R.id.tv_contentlist_title,
				R.id.tv_contentlist_isdone,
				R.id.tv_contentlist_isalarm}){
			@Override
			public View getView(int position, View convertView,
					ViewGroup parent) {
				convertView = super.getView(position, convertView, parent);
				//006FFF~00FFFF 渐变颜色
				//0FFFFF
				int colors[] = ListTitleGradientColorEnum.values()[position%11].getGradientColors();
				GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
				convertView.findViewById(R.id.tv_content_split).setBackground(gd);
				TextView tvDone = (TextView)convertView.findViewById(R.id.tv_contentlist_isdone);
				
				TextView tvTitle = (TextView)convertView.findViewById(R.id.tv_contentlist_title);
				TextView tvTime = (TextView)convertView.findViewById(R.id.tv_contentlist_time);
				if(tvDone.getText().equals(ContentModel.ISDONE)){
					convertView.setBackgroundColor(Color.TRANSPARENT);
					tvTitle.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG); //中划线
					tvTime.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG);
				}else{
					if(position==0){
						convertView.setBackground(getResources().getDrawable(R.drawable.listtextcolorshape));
					}else{
						convertView.setBackgroundColor(Color.WHITE);
					}
					tvTitle.getPaint().setFlags(Paint. HINTING_OFF);
					tvTime.getPaint().setFlags(Paint. HINTING_OFF);
				}
				TextView tvAlarm = (TextView)convertView.findViewById(R.id.tv_contentlist_isalarm);
				if(tvAlarm.getText().equals(ContentModel.ISALARM)){
					convertView.findViewById(R.id.iv_alarm).setVisibility(View.VISIBLE);
				}else{
					convertView.findViewById(R.id.iv_alarm).setVisibility(View.GONE);
				}
				return convertView;
			}
		};
		
		// 添加并且显示
		listTitle = (SwipeDismissListView) findViewById(R.id.main_titles_lv);
		listTitle.setAdapter(listItemAdapter);
		listTitle.setOnDismissCallback(new OnDismissCallback() {  
            @Override
            public void onDismiss(View dismissView,int dismissPosition, boolean isLeft) {
            	HashMap<String,Object> map = (HashMap<String,Object>)
        				listTitle.getItemAtPosition(dismissPosition);
            	ContentDBUtil dbUtil = new ContentDBUtil();
            	if(ContentModel.ISDONE.equals(	//已完成部分操作
            			map.get(ActivityShowContentModel.ITEM_ISDONE))){
                	if(isLeft){	//删除已完成项
                		dbUtil.deleteContentById(map.get(ActivityShowContentModel.ITEM_ID).toString()
    							, A_MainActivity.this);
                		listItemDone.remove(dismissPosition-listItem.size());
                		resetList();
                	}else{		//置为未完成
    					dbUtil.changeContentStatusById(map.get(ActivityShowContentModel.ITEM_ID).toString()
    							, A_MainActivity.this,true);
    					map.put(ActivityShowContentModel.ITEM_ISDONE, ContentModel.ISDONE_NOT);
    					listItem.add(listItemDone.remove(dismissPosition-listItem.size()));
    					resetList();
                	}
            	}else{	//未完成部分操作
	            	if(isLeft){	//增加一次性闹钟
	            		View alarmView = dismissView.findViewById(R.id.iv_alarm);
	            		if(ContentModel.ISALARM_NOT.equals(	//无闹钟
	                			map.get(ActivityShowContentModel.ITEM_ISALARM))){
	            			//建立Intent和PendingIntent来调用闹钟管理器
	            			Intent intent = new Intent(A_MainActivity.this, AlarmReceiver.class);  
	            			intent.setAction("ALARM_ACTION");
	            			intent.putExtra(AlarmReceiver.EXTRA_MSG,map.get
	            					(ActivityShowContentModel.ITEM_TITLE).toString()+"该执行啦" );
	            			PendingIntent pi = PendingIntent.getBroadcast(
	                                A_MainActivity.this, Integer.parseInt(
	                                		map.get(ActivityShowContentModel.ITEM_ID).toString()), intent, 0);
	            			
	            			// 根据用户选择时间来设置Calendar对象  
	            			Calendar c = Calendar.getInstance();  
	            			// 根据用户选择时间来设置Calendar对象  
                            c.setTimeInMillis(System.currentTimeMillis());  
                            String[] time = map.get(ActivityShowContentModel.ITEM_TIME).toString().split(":");
                            c.set(Calendar.HOUR_OF_DAY , Integer.parseInt(time[0]));
                            c.set(Calendar.MINUTE , Integer.parseInt(time[1]));
                            
                            //获取闹钟管理器
                            AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
                            //设置闹钟
                            alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
                            // 显示闹铃设置成功的提示信息  
                            Toast.makeText(A_MainActivity.this ,"闹铃设置成功",Toast.LENGTH_SHORT).show();
	            			
	            			dbUtil.changeContentAlarmById(map.get(ActivityShowContentModel.ITEM_ID).toString()
									, A_MainActivity.this,true);
	            			alarmView.setVisibility(View.VISIBLE);
	            			map.put(ActivityShowContentModel.ITEM_ISALARM, ContentModel.ISALARM);
	            		}else{	//取消闹钟
	            			Intent intent = new Intent(A_MainActivity.this, AlarmReceiver.class);  
	            			intent.setAction("ALARM_ACTION");
	            			PendingIntent pi = PendingIntent.getBroadcast(  
	                                A_MainActivity.this, Integer.parseInt(
	                                		map.get(ActivityShowContentModel.ITEM_ID).toString()), intent, 0);
	            			//获取闹钟管理器
                            AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
                            //设置闹钟
                            alarmManager.cancel(pi);
                            Toast.makeText(A_MainActivity.this ,"闹铃已取消",Toast.LENGTH_SHORT).show();  
	            			
	            			dbUtil.changeContentAlarmById(map.get(ActivityShowContentModel.ITEM_ID).toString()
									, A_MainActivity.this,false);
	            			alarmView.setVisibility(View.GONE);
	            			map.put(ActivityShowContentModel.ITEM_ISALARM, ContentModel.ISALARM_NOT);
	            		}
	            	}else{		//置为已完成
						dbUtil.changeContentStatusById(map.get(ActivityShowContentModel.ITEM_ID).toString()
								, A_MainActivity.this,false);
    					map.put(ActivityShowContentModel.ITEM_ISDONE, ContentModel.ISDONE);
						listItemDone.add(listItem.remove(dismissPosition));
						resetList();
	            	}
            	}
            }

			@Override
			public void onEdit(int dismissPosition) {
				goIntentEdit(dismissPosition);
			}
        });  
	}

	/**
	 * 跳转到编辑页面
	 * **/
	private void goIntentEdit(final int position) {
		Intent intent_edit = new Intent(A_MainActivity.this, A_EditContentActivity.class);
		ContentDBUtil dbUtil = new ContentDBUtil();
		HashMap<String,Object> map = (HashMap<String,Object>)
				listTitle.getItemAtPosition(position);
		if(ContentModel.ISDONE_NOT.equals(map.get(ActivityShowContentModel.ITEM_ISDONE))){
			ContentModel model = dbUtil.queryContentForDetail(
					map.get(ActivityShowContentModel.ITEM_ID).toString(), A_MainActivity.this);
			intent_edit.putExtra(Constants.DETAIL_MODEL, model);
			startActivity(intent_edit);
		}
	}
	/** 日期栏的适配器，用于GridView容器的信息变更 **/
	private class GridViewAdapter extends BaseAdapter {
		public static final int DAY = 0;
		public static final int WEEK = 1;
		private int type;
		public GridViewAdapter(int type) {
			this.type = type;
		}
		/*
		 * inflate这个方法总共有四种形式，目的都是把xml表述的layout转化为View对象。
		 * 在android中如果想将xml中的Layout转换为View放入.java代码中操作，
		 * 只能通过Inflater，而不能通过findViewById()。
		 */
		private LayoutInflater inflater = (LayoutInflater)
				getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		@Override
		public int getCount() {
			return dateList.size();
		}

		@Override
		public Object getItem(int position) {
			return dateList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		/** 焦点变化产生的字体变化 **/
		private void makeTextStyle(TextView tv,boolean focus){
			if(focus){
				tv.setTextColor(Color.parseColor("#2C85D7"));
				tv.getPaint().setFakeBoldText(true);
			}else{
				tv.setTextColor(Color.parseColor("#CCCCCC"));
			}
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			TextView textView = null;
			switch(type){
				case DAY:
					convertView = inflater.inflate(R.layout.a_main_listitem_date_day, null);
					textView = (TextView) convertView
							.findViewById(R.id.main_date_day_item);
					textView.setText(dateList.get(position)[0]);
					if(DateUtil.returnTodayAround()==position){
						makeTextStyle((TextView)convertView,true);
					}
					break;
				case WEEK:
					convertView = inflater.inflate(R.layout.a_main_listitem_date_week, null);
					textView = (TextView) convertView
							.findViewById(R.id.main_date_week_item);
					textView.setText(dateList.get(position)[1]);
					
					break;
				default:
					convertView = inflater.inflate(R.layout.a_main_listitem_date_day, null);
					textView = (TextView) convertView
							.findViewById(R.id.main_date_day_item);
					textView.setText(dateList.get(position)[0]);
					if(DateUtil.returnTodayAround()==position){
						makeTextStyle((TextView)convertView,true);
					}
					break;
			}

			final String[] str = dateList.get(position);
			
			final ViewGroup parent_final = parent;
			//只监听日期adapter
			if(type==DAY){
				convertView.setOnTouchListener((new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						switch (event.getAction()){
							case MotionEvent.ACTION_DOWN:
								for(int i=0;i<parent_final.getChildCount();i++){
									if(parent_final.getChildAt(i)!=v){
										makeTextStyle((TextView)parent_final.getChildAt(i),false);
									}else{
										setDATE_IN(i);
									}
								}
								makeTextStyle((TextView)v,true);
								String paramDate = DateUtil.returnDDMMMEEETo_yyyy_MM_dd(str);
								
								itemListChange(paramDate);
								changeTitle(!DateUtil.checkTodayByYyyy_MM_dd(paramDate),position);
								break;
						}
						return false;
					}
				}));
			}
			return convertView;
		}
	}
	/** 改变内容框 **/
	private void itemListChange(String paramDate) {
		ContentDBUtil dbUtil = new ContentDBUtil();
		listItemAll.clear();
		listItem = dbUtil.queryContentForList(paramDate
				,ContentModel.ISDONE_NOT, A_MainActivity.this);
		listItemDone = dbUtil.queryContentForList(paramDate
				,ContentModel.ISDONE, A_MainActivity.this);
		listItemAll.addAll(listItem);
		listItemAll.addAll(listItemDone);
		listItemAdapter.notifyDataSetChanged();
	}
	/** 当日操作，改变被容框 **/
	private void resetList() {
		listItemAll.clear();
		listItemAll.addAll(listItem);
		listItemAll.addAll(listItemDone);
		listItemAdapter.notifyDataSetChanged();
	}
	/**所有按钮的监听器 **/
	private OnTouchListener gridlistener = new OnTouchListener() {
		float x_tmp1 = 0.0f;
		float x_tmp2 = 0.0f;
		@Override
		public boolean onTouch(final View paramView, MotionEvent event) {
			/**
			 * 判断是向左还是滑动方向
			 */
			//获取当前坐标
			float x = event.getX();
			switch (event.getAction()){
				case MotionEvent.ACTION_DOWN:
					x_tmp1 = x;
					break;
				case MotionEvent.ACTION_UP:
					x_tmp2 = x;
					
					if(x_tmp1 - x_tmp2 > 50){	//往左
						ViewPropertyAnimator.animate(paramView)
							.translationX(x_tmp2 - x_tmp1)
							.setDuration(500).setListener(new AnimatorListenerAdapter() {
								@Override
								public void onAnimationEnd(Animator animation) {
									ValueAnimator animator = ValueAnimator.ofInt(paramView.getHeight(), 0).setDuration(50);
									animator.start();

									animator.addListener(new AnimatorListenerAdapter() {
										@Override
										public void onAnimationEnd(Animator animation) {
											//这段代码很重要，因为我们并没有将item从ListView中移除，而是将item的高度设置为0
											//所以我们在动画执行完毕之后将item设置回来
											ViewHelper.setAlpha(paramView, 1f);
											ViewHelper.setTranslationX(paramView, 0);
											dateRate = dateRate+7;
											dateList = DateUtil.returnRoundMonth(dateRate);
											changeTitle(dateRate!=0,DateUtil.returnTodayAround());
											adapter_day.notifyDataSetChanged();
											
											String paramDate = DateUtil.returnDateTo_yyyy_MM_dd(DateUtil.getAfterDateByDays
													(new Date(),dateRate));
											itemListChange(paramDate);
										}
									});
									
//									ViewPropertyAnimator.animate(paramView)
//										.translationX(0).alpha(1)
//										.setDuration(50);

								}
						});
						
					}
					if(x_tmp2 - x_tmp1 > 50){	//往右
						ViewPropertyAnimator.animate(paramView)
							.translationX(x_tmp2 - x_tmp1)
							.setDuration(500).setListener(new AnimatorListenerAdapter() {
								@Override
								public void onAnimationEnd(Animator animation) {
									ValueAnimator animator = ValueAnimator.ofInt(paramView.getHeight(), 0).setDuration(50);
									animator.start();

									animator.addListener(new AnimatorListenerAdapter() {
										@Override
										public void onAnimationEnd(Animator animation) {
											//这段代码很重要，因为我们并没有将item从ListView中移除，而是将item的高度设置为0
											//所以我们在动画执行完毕之后将item设置回来
											ViewHelper.setAlpha(paramView, 1f);
											ViewHelper.setTranslationX(paramView, 0);
											dateRate = dateRate-7;
											dateList = DateUtil.returnRoundMonth(dateRate);
											changeTitle(dateRate!=0,DateUtil.returnTodayAround());
											adapter_day.notifyDataSetChanged();
											
											String paramDate = DateUtil.returnDateTo_yyyy_MM_dd(DateUtil.getAfterDateByDays
													(new Date(),dateRate));
											itemListChange(paramDate);
										}
									});
								}
						});
					}
					break;
			}
			return false;
		}
	};

	@Override
	protected void setOwnView() {
		setBehindContentView(R.layout.activity_menu_frame);
		setContentView(R.layout.activity_a_main);
	}
	/**
	 * 是否需要改变标题头
	 * **/
	private void changeTitle(boolean needChange, int position) {
		if(needChange){
			String title = dateList.get(position)[3]
					+"年"+dateList.get(position)[4]+"月" ;
			mTvTitle.setText(title);
			mTvTitle.setBackground(null);
			mTvGoToday.setBackground(getResources().getDrawable(R.drawable.huijintian));
		}else{
			//mTvTitle.setText(R.string.main_title);
			mTvTitle.setText("");
			mTvTitle.setBackground(getResources().getDrawable(R.drawable.dododoit));
			mTvGoToday.setBackground(null);
		}
	}

	@Override
	protected void goToday() {
		/** 回到当前日期 **/
		itemListChange(DateUtil.returnDateTo_yyyy_MM_dd(new Date()));
		
		changeTitle(false,-1);
		dateRate = 0;
		dateList = DateUtil.returnRoundMonth(dateRate);
		adapter_day.notifyDataSetChanged();
		
	}
}
