package yunhe.doit;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import yunhe.database.ContentDBUtil;
import yunhe.database.UserInfoDBUtil;
import yunhe.doit.util.DateUtil;
import yunhe.model.ActivityShowContentModel;
import yunhe.model.ContentModel;
import yunhe.model.UserInfoModel;
import yunhe.util.Constants;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class A_MainActivity extends Activity {
	private Button mbtAdd;
	private Button mbtInfo;
	private Button mbtList;
	private GridView gridDate;
	private LayoutInflater inflater;
	private List<String> dataList = new ArrayList<String>();
	private ListView listTitle;
	private DisplayMetrics dm = new DisplayMetrics();
	List<Map<String, Object>> listItem = new ArrayList<Map<String, Object>>();
	private GridViewAdapter adapter ;
	private int dateRate = 0;
	private MySimpleAdapter listItemAdapter;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_a_main);

		mbtAdd = (Button) findViewById(R.id.main_go_add);
		mbtAdd.setOnClickListener(listener);
		mbtInfo = (Button) findViewById(R.id.main_go_info);
		mbtInfo.setOnClickListener(listener);
		mbtList = (Button) findViewById(R.id.main_go_list);
		mbtList.setOnClickListener(listener);
		
		adapter = new GridViewAdapter();
		dataList = DateUtil.returnRoundMonth(dateRate);
		gridDate = (GridView) findViewById(R.id.main_date_gv);
		/*
		 * inflate这个方法总共有四种形式，目的都是把xml表述的layout转化为View对象。
		 * 在android中如果想将xml中的Layout转换为View放入.java代码中操作，
		 * 只能通过Inflater，而不能通过findViewById()。
		 */
		inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		gridDate.setAdapter(adapter);
		int size = dataList.size();
		/*
		 * DisplayMetrics可以得到分辨率等信息，方法如下：DisplayMetrics
		 * metrics;getWindowManager().getDefaultDisplay().getMetrics(metrics);
		 * metrics.widthPixels 屏幕宽metrics.heightPixels 屏幕高metrics.density 屏幕密度
		 */
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int allWidth = (int) (dm.widthPixels);
		int itemWidth = (int) (dm.widthPixels / size);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				allWidth, LinearLayout.LayoutParams.FILL_PARENT);
		gridDate.setLayoutParams(params);
		gridDate.setColumnWidth(itemWidth);
		gridDate.setHorizontalSpacing(0);
		gridDate.setStretchMode(GridView.NO_STRETCH);
		gridDate.setNumColumns(size);
		
		gridDate.setOnTouchListener(new OnTouchListener() {
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
							dateRate = dateRate+5;
							dataList = DateUtil.returnRoundMonth(dateRate);
							adapter.notifyDataSetChanged();
						}
						if(x_tmp2 - x_tmp1 > 50){
							dateRate = dateRate-5;
							dataList = DateUtil.returnRoundMonth(dateRate);
							adapter.notifyDataSetChanged();
						}
						break;
				}
				return false;
			}
		});
		
		//查询结果
		ContentDBUtil dbUtil = new ContentDBUtil();
		listItem = dbUtil.queryContentForList(
				DateUtil.returnDateTo_yyyy_MM_dd(new Date()), this);
		// 生成适配器的Item和动态数组对应的元素
		listItemAdapter = new MySimpleAdapter(this, listItem,// 数据源
			R.layout.a_main_listitem_title,// ListItem的XML实现
			// 动态数组与ImageItem对应的子项
			new String[] {ActivityShowContentModel.ITEM_TITLE,
				 ActivityShowContentModel.ITEM_ID,
				 ActivityShowContentModel.ITEM_ID},
			// ImageItem的XML文件里面的一个ImageView,两个TextView ID
			new int[] { R.id.tv_contentlist_title,
				R.id.bt_contentlist_goedit,
				R.id.bt_contentlist_goDelete });
		// 添加并且显示
		listTitle = (ListView) findViewById(R.id.main_titles_lv);
		listTitle.setAdapter(listItemAdapter);
	}

	final class GridViewAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return dataList.size();
		}

		@Override
		public Object getItem(int position) {
			return dataList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = inflater.inflate(R.layout.a_main_listitem_date, null);
			TextView textView = (TextView) convertView
					.findViewById(R.id.main_date_item);
			final String str = dataList.get(position);
			textView.setText(str);
			if(DateUtil.AROUNDDAY==position){
				convertView.setBackgroundColor(Color.YELLOW);
			}
			convertView.setOnTouchListener((new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()){
						case MotionEvent.ACTION_DOWN:
							ContentDBUtil dbUtil = new ContentDBUtil();
							listItem.clear();
							listItem.addAll(dbUtil.queryContentForList(
									DateUtil.returnDDMMMEEETo_yyyy_MM_dd(str), A_MainActivity.this));
							listItemAdapter.notifyDataSetChanged();
							break;
					}
					return false;
				}
			}));
			return convertView;
		}
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_a_main, menu);
		return true;
	}

	private OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.main_go_add:
				Intent intent_add = new Intent(A_MainActivity.this,
						B_EditContentActivity.class);
				startActivity(intent_add);
				break;
			case R.id.main_go_info:
				Intent intent_info = new Intent(A_MainActivity.this,
						C_InfoActivity.class);
				UserInfoDBUtil dbUtil = new UserInfoDBUtil();
				UserInfoModel model = dbUtil.queryUserInfoForDetail
						(A_MainActivity.this);
				intent_info.putExtra(Constants.USERINFO_MODEL, model);
				startActivity(intent_info);
				break;
			case R.id.main_go_list:
				Intent intent_list = new Intent(A_MainActivity.this,
						B_ListContentActivity.class);
				startActivity(intent_list);
				break;
			default:
				break;
			}
		}
	};
	
	private class MySimpleAdapter extends SimpleAdapter {

		public MySimpleAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final int mPosition = position;
			convertView = super.getView(position, convertView, parent);
			Button buttonEdit = (Button) convertView
					.findViewById(R.id.bt_contentlist_goedit);// id为你自定义布局中按钮的id
			buttonEdit.setTag(buttonEdit.getText());
			buttonEdit.setText("编辑");
			final String id = buttonEdit.getTag().toString();
			
			buttonEdit.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent_edit = new Intent(A_MainActivity.this, B_EditContentActivity.class);
					ContentDBUtil dbUtil = new ContentDBUtil();
					ContentModel model = dbUtil.queryContentForDetail(id, A_MainActivity.this);
					intent_edit.putExtra(Constants.DETAIL_MODEL, model);
					startActivity(intent_edit);
				}
			});
			Button buttonDelete = (Button) convertView
					.findViewById(R.id.bt_contentlist_goDelete);
			buttonDelete.setText("删除");
			buttonDelete.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					ContentDBUtil dbUtil = new ContentDBUtil();
					dbUtil.deleteContentById(id
							, A_MainActivity.this);
					listItem.remove(mPosition);
					notifyDataSetChanged();
					Toast.makeText(A_MainActivity.this, "刪除成功", Toast.LENGTH_SHORT).show();
				}
			});
			return convertView;
		}
	}
}
