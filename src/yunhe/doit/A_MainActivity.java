package yunhe.doit;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import yunhe.database.ContentDBUtil;
import yunhe.model.ActivityShowContentModel;
import yunhe.model.ContentModel;
import yunhe.util.Constants;
import yunhe.util.DateUtil;
import yunhe.util.ListTitleGradientColorEnum;
import yunhe.view.SwipeDismissListView;
import yunhe.view.SwipeDismissListView.OnDismissCallback;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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

public class A_MainActivity extends _BaseSlidingActivity {
	private GridView gridDate_day;
	private GridView gridDate_week;
	private SwipeDismissListView listTitle;
	
	/** 日期栏的数据list [0]日,[1]星期,[2]整体,[3]yyyy,[4]月**/
	private List<String[]> dateList = new ArrayList<String[]>();
	/** 信息栏的数据集合 **/
	List<Map<String, Object>> listItem = new ArrayList<Map<String, Object>>();
	/** 偏差日期，初始加载为0 **/
	private int dateRate = 0;
	/** 日期栏的适配器，用于GridView容器的信息变更 **/
	private GridViewAdapter adapter_week ;
	private GridViewAdapter adapter_day ;
	/** 信息栏的适配器，用于信息ListView容器的信息变更 **/
	private SimpleAdapter listItemAdapter;
	
	private SwipeDismissListView listTitle_done;
	List<Map<String, Object>> listItem_done = new ArrayList<Map<String, Object>>();
	private SimpleAdapter listItemDoneAdapter;
	
	@Override
	public void onBackPressed() {
		exitDialog();
	}
	
	private void exitDialog() {
        AlertDialog.Builder builder = new Builder(A_MainActivity.this); 
        builder.setMessage("确定要退出吗?"); 
        builder.setTitle("提示"); 
        builder.setPositiveButton("确认", 
        new android.content.DialogInterface.OnClickListener() { 
            @Override 
            public void onClick(DialogInterface dialog, int which) { 
                dialog.dismiss(); 
               android.os.Process.killProcess(android.os.Process.myPid()); 
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
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		getSupportMenuInflater().inflate(R.menu.a_button_menu, menu);
		return true;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView(getSlidingMenu());
	}
	/** 初始化视图控件  **/
	private void initView(SlidingMenu menu) {
		initGridDateView();
		initListTitleView();
	}

	/** 初始化日期栏视图 **/
	private void initGridDateView() {
		gridDate_week = (GridView) findViewById(R.id.main_week_gv);
		gridDate_day = (GridView) findViewById(R.id.main_day_gv);
		
		adapter_week = new GridViewAdapter(GridViewAdapter.WEEK);
		gridDate_week.setAdapter(adapter_week);
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
		
		gridDate_week.setLayoutParams(params);
		gridDate_week.setColumnWidth(itemWidth);
		gridDate_week.setHorizontalSpacing(0);
		gridDate_week.setStretchMode(GridView.NO_STRETCH);
		gridDate_week.setNumColumns(dateList.size());
		
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
		// 生成适配器的Item和动态数组对应的元素
		listItemAdapter = new SimpleAdapter(this, listItem,// 数据源
			R.layout.a_main_listitem_title,// ListItem的XML实现
			// 动态数组与ImageItem对应的子项
			new String[] {ActivityShowContentModel.ITEM_TIME,
				ActivityShowContentModel.ITEM_TITLE},
			// ImageItem的XML文件里面的一个ImageView,两个TextView ID
			new int[] { R.id.tv_contentlist_time,
				R.id.tv_contentlist_title}){
			@Override
			public View getView(int position, View convertView,
					ViewGroup parent) {
				convertView = super.getView(position, convertView, parent);
				//006FFF~00FFFF 渐变颜色
				//0FFFFF
				int colors[] = ListTitleGradientColorEnum.values()[position%11].getGradientColors();
				GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
				convertView.findViewById(R.id.tv_content_split).setBackground(gd);
				if(position==0){
					convertView.setBackground(getResources().getDrawable(R.drawable.listtextcolorshape));
				}
				return convertView;
			}
		};
		// 添加并且显示
		listTitle = (SwipeDismissListView) findViewById(R.id.main_titles_lv);
		listTitle.setAdapter(listItemAdapter);
		listTitle.setOnDismissCallback(new OnDismissCallback() {  
            @Override  
            public void onDismiss(int dismissPosition, boolean isLeft) {
            	HashMap<String,Object> map = (HashMap<String,Object>)
        				listTitle.getItemAtPosition(dismissPosition);
            	ContentDBUtil dbUtil = new ContentDBUtil();
            	if(isLeft){
            		dbUtil.deleteContentById(map.get(ActivityShowContentModel.ITEM_ID).toString()
							, A_MainActivity.this);
            		listItem.remove(dismissPosition);
					listItemAdapter.notifyDataSetChanged();
            	}else{
            		//下降操作
					dbUtil.changeContentStatusById(map.get(ActivityShowContentModel.ITEM_ID).toString()
							, A_MainActivity.this,false);
					listItem_done.add(listItem.remove(dismissPosition));
					listItemAdapter.notifyDataSetChanged();
					listItemDoneAdapter.notifyDataSetChanged();
            	}
            }

			@Override
			public void onEdit(int dismissPosition) {
				goIntentEdit(dismissPosition);
			}
        });  
		
		listItem_done = dbUtil.queryContentForList(
				DateUtil.returnDateTo_yyyy_MM_dd(new Date()),ContentModel.ISDONE, this);
		// 生成适配器的Item和动态数组对应的元素
		listItemDoneAdapter = new SimpleAdapter(this, listItem_done,// 数据源
			R.layout.a_main_listitem_title_done,// ListItem的XML实现
			// 动态数组与ImageItem对应的子项
			new String[] {ActivityShowContentModel.ITEM_TIME,
					ActivityShowContentModel.ITEM_TITLE},
				// ImageItem的XML文件里面的一个ImageView,两个TextView ID
				new int[] { R.id.tv_contentlist_time_done,
					R.id.tv_contentlist_title_done});
		// 添加并且显示
		listTitle_done = (SwipeDismissListView) findViewById(R.id.main_titles_done_lv);
		listTitle_done.setAdapter(listItemDoneAdapter);
		listTitle_done.setOnDismissCallback(new OnDismissCallback() {
            @Override  
            public void onDismiss(int dismissPosition, boolean isLeft) {
            	HashMap<String,Object> map = (HashMap<String,Object>)
        				listTitle_done.getItemAtPosition(dismissPosition);
            	ContentDBUtil dbUtil = new ContentDBUtil();
            	if(isLeft){
            		dbUtil.deleteContentById(map.get(ActivityShowContentModel.ITEM_ID).toString()
							, A_MainActivity.this);
            		listItem_done.remove(dismissPosition);
					listItemDoneAdapter.notifyDataSetChanged();
            	}else{
            		//上升操作
					dbUtil.changeContentStatusById(map.get(ActivityShowContentModel.ITEM_ID).toString()
							, A_MainActivity.this,true);
					listItem.add(listItem_done.remove(dismissPosition));
					listItemAdapter.notifyDataSetChanged();
					listItemDoneAdapter.notifyDataSetChanged();
            	}
            }

			@Override
			public void onEdit(int dismissPosition) {}
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
		ContentModel model = dbUtil.queryContentForDetail(
				map.get(ActivityShowContentModel.ITEM_ID).toString(), A_MainActivity.this);
		intent_edit.putExtra(Constants.DETAIL_MODEL, model);
		startActivity(intent_edit);
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
				tv.setTextColor(Color.parseColor("#FFFFFF"));
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
					if(DateUtil.AROUNDDAY==position){
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
					if(DateUtil.AROUNDDAY==position){
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
								ContentDBUtil dbUtil = new ContentDBUtil();
								for(int i=0;i<parent_final.getChildCount();i++){
									if(parent_final.getChildAt(i)!=v){
										makeTextStyle((TextView)parent_final.getChildAt(i),false);
									}
								}
								makeTextStyle((TextView)v,true);
								listItem.clear();
								String paramDate = DateUtil.returnDDMMMEEETo_yyyy_MM_dd(str);
								listItem.addAll(dbUtil.queryContentForList(paramDate
										,ContentModel.ISDONE_NOT, A_MainActivity.this));
								listItemAdapter.notifyDataSetChanged();
								listItem_done.clear();
								listItem_done.addAll(dbUtil.queryContentForList(
										DateUtil.returnDDMMMEEETo_yyyy_MM_dd(str)
										,ContentModel.ISDONE_NOT, A_MainActivity.this));
								listItemDoneAdapter.notifyDataSetChanged();
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
	
	/**所有按钮的监听器 **/
	private OnTouchListener gridlistener = new OnTouchListener() {
		float x_tmp1 = 0.0f;
		float x_tmp2 = 0.0f;
		@Override
		public boolean onTouch(View paramView, MotionEvent event) {
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
					
					if(x_tmp1 - x_tmp2 > 50){
						dateRate = dateRate+(DateUtil.AROUNDDAY*2+1);
						dateList = DateUtil.returnRoundMonth(dateRate);
						adapter_day.notifyDataSetChanged();
						changeTitle(dateRate!=0,DateUtil.AROUNDDAY);
					}
					if(x_tmp2 - x_tmp1 > 50){
						dateRate = dateRate-(DateUtil.AROUNDDAY*2+1);
						dateList = DateUtil.returnRoundMonth(dateRate);
						adapter_day.notifyDataSetChanged();
						changeTitle(dateRate!=0,DateUtil.AROUNDDAY);
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
		}else{
			mTvTitle.setText(R.string.main_title);
		}
	}
}
