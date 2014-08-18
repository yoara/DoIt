package yunhe.doit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yunhe.database.ContentDBUtil;
import yunhe.model.ActivityShowContentModel;
import yunhe.model.ContentModel;
import yunhe.util.Constants;
import yunhe.util.ListTitleGradientColorEnum;
import yunhe.view.SwipeDismissListView;
import yunhe.view.SwipeDismissListView.OnDismissCallback;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

public class B_ListContentActivity extends _BaseSlidingActivity {
	private SwipeDismissListView listTitle;
	List<Map<String, Object>> listItem = new ArrayList<Map<String, Object>>();
	private SwipeDismissListView listTitle_done;
	List<Map<String, Object>> listItem_done = new ArrayList<Map<String, Object>>();
	
	private SimpleAdapter listItemAdapter;
	private SimpleAdapter listItemDoneAdapter;
	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		getSupportMenuInflater().inflate(R.menu.b_button_menu, menu);
		return true;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//查询结果
		ContentDBUtil dbUtil = new ContentDBUtil();
		listItem = dbUtil.queryContentForList(null,ContentModel.ISDONE_NOT, this);
		// 生成适配器的Item和动态数组对应的元素
		listItemAdapter = new SimpleAdapter(this, listItem,// 数据源
			R.layout.activity_b_listitem_title,// ListItem的XML实现
			// 动态数组与ImageItem对应的子项
			new String[] {ActivityShowContentModel.ITEM_TITLE},
			// ImageItem的XML文件里面的一个ImageView,两个TextView ID
			new int[] { R.id.tv_contentlist_title}){
			public View getView(int position, View convertView,
					ViewGroup parent) {
				convertView = super.getView(position, convertView, parent);
				//006FFF~00FFFF 渐变颜色
				//0FFFFF
				int colors[] = ListTitleGradientColorEnum.values()[position%11].getGradientColors();
				GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
				convertView.findViewById(R.id.tv_content_split).setBackground(gd);
				return convertView;
			}
		};
		
		
		// 添加并且显示
		listTitle = (SwipeDismissListView) findViewById(R.id.b_list_titles_lv);
		listTitle.setAdapter(listItemAdapter);
		listTitle.setOnDismissCallback(new OnDismissCallback(){
            @Override  
            public void onDismiss(int dismissPosition, boolean isLeft) {
            	HashMap<String,Object> map = (HashMap<String,Object>)
        				listTitle.getItemAtPosition(dismissPosition);
            	ContentDBUtil dbUtil = new ContentDBUtil();
            	if(isLeft){
            		dbUtil.deleteContentById(map.get(ActivityShowContentModel.ITEM_ID).toString()
							, B_ListContentActivity.this);
            		listItem.remove(dismissPosition);
					listItemAdapter.notifyDataSetChanged();
            	}else{
            		//下降操作
					dbUtil.changeContentStatusById(map.get(ActivityShowContentModel.ITEM_ID).toString()
							, B_ListContentActivity.this,false);
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
		
		
		listItem_done = dbUtil.queryContentForList(null,ContentModel.ISDONE, this);
		// 生成适配器的Item和动态数组对应的元素
		listItemDoneAdapter = new SimpleAdapter(this, listItem_done,// 数据源
			R.layout.activity_b_listitem_title_done,// ListItem的XML实现
			// 动态数组与ImageItem对应的子项
			new String[] {ActivityShowContentModel.ITEM_TITLE},
			// ImageItem的XML文件里面的一个ImageView,两个TextView ID
			new int[] { R.id.tv_contentlist_title_done});
		// 添加并且显示
		listTitle_done = (SwipeDismissListView) findViewById(R.id.b_list_titles_done_lv);
		listTitle_done.setAdapter(listItemDoneAdapter);
		listTitle_done.setOnDismissCallback(new OnDismissCallback() {
            @Override  
            public void onDismiss(int dismissPosition, boolean isLeft) {
            	HashMap<String,Object> map = (HashMap<String,Object>)
        				listTitle_done.getItemAtPosition(dismissPosition);
            	ContentDBUtil dbUtil = new ContentDBUtil();
            	if(isLeft){
            		dbUtil.deleteContentById(map.get(ActivityShowContentModel.ITEM_ID).toString()
							, B_ListContentActivity.this);
            		listItem_done.remove(dismissPosition);
					listItemDoneAdapter.notifyDataSetChanged();
            	}else{
            		//上升操作
					dbUtil.changeContentStatusById(map.get(ActivityShowContentModel.ITEM_ID).toString()
							, B_ListContentActivity.this,true);
					listItem.add(listItem_done.remove(dismissPosition));
					listItemAdapter.notifyDataSetChanged();
					listItemDoneAdapter.notifyDataSetChanged();
            	}
            }

			@Override
			public void onEdit(int dismissPosition) {}
        });
	}
	
	private void goIntentEdit(int dismissPosition){
		Intent intent_edit = new Intent(B_ListContentActivity.this, B_EditContentActivity.class);
		ContentDBUtil dbUtil = new ContentDBUtil();
		HashMap<String,Object> map = (HashMap<String,Object>)
				listTitle.getItemAtPosition(dismissPosition);
		ContentModel model = dbUtil.queryContentForDetail(
				map.get(ActivityShowContentModel.ITEM_ID).toString(), B_ListContentActivity.this);
		intent_edit.putExtra(Constants.DETAIL_MODEL, model);
		startActivity(intent_edit);
	}
	
	@Override
	protected void setOwnView() {
		setBehindContentView(R.layout.activity_menu_frame);
		setContentView(R.layout.activity_b_list);
	}
}
