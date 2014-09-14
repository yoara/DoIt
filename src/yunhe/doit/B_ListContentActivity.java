package yunhe.doit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yunhe.database.ContentDBUtil;
import yunhe.database.UserInfoDBUtil;
import yunhe.model.ActivityShowContentModel;
import yunhe.model.ContentModel;
import yunhe.util.Constants;
import yunhe.util.ListTitleGradientColorEnum;
import yunhe.view.SwipeDismissListView;
import yunhe.view.SwipeDismissListView.OnDismissCallback;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class B_ListContentActivity extends _BaseSlidingActivity {
	/** 信息栏的数据集合 **/
	List<Map<String, Object>> listItem = new ArrayList<Map<String, Object>>();
	List<Map<String, Object>> listItemDone = new ArrayList<Map<String, Object>>();
	List<Map<String, Object>> listItemAll = new ArrayList<Map<String, Object>>();
	private SwipeDismissListView listTitle;
	private SimpleAdapter listItemAdapter;
	
	
	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		getSupportMenuInflater().inflate(R.menu.b_button_menu, menu);
		return true;
	}
	@Override
	protected void onResume() {
		super.onResume();
		initView();
		
		//增加默认加载项
		UserInfoDBUtil db = UserInfoDBUtil.getInstance();
		db.updateActivityFlag("B",this);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}
	private void initView() {
		//查询结果
		ContentDBUtil dbUtil = new ContentDBUtil();
		listItem = dbUtil.queryContentForList(null,ContentModel.ISDONE_NOT, this);
		listItemDone = dbUtil.queryContentForList(null,ContentModel.ISDONE, this);
		listItemAll.clear();
		listItemAll.addAll(listItem);
		listItemAll.addAll(listItemDone);
		
		// 生成适配器的Item和动态数组对应的元素
		listItemAdapter = new SimpleAdapter(this, listItemAll,// 数据源
			R.layout.activity_b_listitem_title,// ListItem的XML实现
			// 动态数组与ImageItem对应的子项
			new String[] {ActivityShowContentModel.ITEM_TITLE,
				ActivityShowContentModel.ITEM_ISDONE},
			// ImageItem的XML文件里面的一个ImageView,两个TextView ID
			new int[] { R.id.tv_b_contentlist_title,
				R.id.tv_b_contentlist_isdone}){
			public View getView(int position, View convertView,
					ViewGroup parent) {
				convertView = super.getView(position, convertView, parent);
				//006FFF~00FFFF 渐变颜色
				//0FFFFF
				int colors[] = ListTitleGradientColorEnum.values()[position%11].getGradientColors();
				GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
				convertView.findViewById(R.id.tv_b_content_split).setBackground(gd);
				TextView tvDone = (TextView)convertView.findViewById(R.id.tv_b_contentlist_isdone);
				TextView tvTitle = (TextView)convertView.findViewById(R.id.tv_b_contentlist_title);
				if(tvDone.getText().equals(ContentModel.ISDONE)){
					convertView.setBackgroundColor(Color.parseColor("#CCCCCC"));
					tvTitle.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG); //中划线
				}else{
					if(position==0){
						convertView.setBackground(getResources().getDrawable(R.drawable.listtextcolorshape));
					}else{
						convertView.setBackgroundColor(Color.WHITE);
					}
					tvTitle.getPaint().setFlags(Paint. HINTING_OFF); //中划线
				}
				return convertView;
			}
		};
		
		// 添加并且显示
		listTitle = (SwipeDismissListView) findViewById(R.id.b_list_titles_lv);
		listTitle.setAdapter(listItemAdapter);
		listTitle.setOnDismissCallback(new OnDismissCallback(){
            @Override  
            public void onDismiss(View dismissView,int dismissPosition, boolean isLeft) {
            	HashMap<String,Object> map = (HashMap<String,Object>)
        				listTitle.getItemAtPosition(dismissPosition);
            	ContentDBUtil dbUtil = new ContentDBUtil();
            	if(ContentModel.ISDONE.equals(	//已完成部分操作
            			map.get(ActivityShowContentModel.ITEM_ISDONE))){
                	if(isLeft){	//删除已完成项
                		dbUtil.deleteContentById(map.get(ActivityShowContentModel.ITEM_ID).toString()
    							, B_ListContentActivity.this);
                		listItemDone.remove(dismissPosition-listItem.size());
                		resetList();
                	}else{		//置为未完成
    					dbUtil.changeContentStatusById(map.get(ActivityShowContentModel.ITEM_ID).toString()
    							, B_ListContentActivity.this,true);
    					map.put(ActivityShowContentModel.ITEM_ISDONE, ContentModel.ISDONE_NOT);
    					listItem.add(listItemDone.remove(dismissPosition-listItem.size()));
    					resetList();
                	}
            	}else{	//未完成部分操作
	            	if(isLeft){	//删除
	            		dbUtil.deleteContentById(map.get(ActivityShowContentModel.ITEM_ID).toString()
    							, B_ListContentActivity.this);
	            		listItem.remove(dismissPosition);
                		resetList();
	            	}else{		//置为已完成
						dbUtil.changeContentStatusById(map.get(ActivityShowContentModel.ITEM_ID).toString()
								, B_ListContentActivity.this,false);
    					map.put(ActivityShowContentModel.ITEM_ISDONE, ContentModel.ISDONE);
						listItemDone.add(listItem.remove(dismissPosition));
						resetList();
	            	}
            	}
            }

			private void resetList() {
				listItemAll.clear();
				listItemAll.addAll(listItem);
				listItemAll.addAll(listItemDone);
				listItemAdapter.notifyDataSetChanged();
			}
            
			@Override
			public void onEdit(int dismissPosition) {
				goIntentEdit(dismissPosition);
			}
        });
	}
	
	private void goIntentEdit(int dismissPosition){
		Intent intent_edit = new Intent(B_ListContentActivity.this, B_EditContentActivity.class);
		ContentDBUtil dbUtil = new ContentDBUtil();
		HashMap<String,Object> map = (HashMap<String,Object>)
				listTitle.getItemAtPosition(dismissPosition);
		if(ContentModel.ISDONE_NOT.equals(map.get(ActivityShowContentModel.ITEM_ISDONE))){
			ContentModel model = dbUtil.queryContentForDetail(
					map.get(ActivityShowContentModel.ITEM_ID).toString(), B_ListContentActivity.this);
			intent_edit.putExtra(Constants.DETAIL_MODEL, model);
			startActivity(intent_edit);
		}
	}
	
	@Override
	protected void setOwnView() {
		setBehindContentView(R.layout.activity_menu_frame);
		setContentView(R.layout.activity_b_list);
	}
	@Override
	protected void goSettingButton() {
		super.goSettingButton();
	}
}
