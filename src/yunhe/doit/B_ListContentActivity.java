package yunhe.doit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import yunhe.database.ContentDBUtil;
import yunhe.model.ActivityShowContentModel;
import yunhe.model.ContentModel;
import yunhe.util.Constants;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class B_ListContentActivity extends Activity {
	private Button mbtback;
	private ListView listTitle;
	List<Map<String, Object>> listItem = new ArrayList<Map<String, Object>>();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_c_listcontent);

		mbtback = (Button) findViewById(R.id.listcontent_go_back);
		mbtback.setOnClickListener(listener);

		//查询结果
		ContentDBUtil dbUtil = new ContentDBUtil();
		listItem = dbUtil.queryContentForList(null, this);
		// 生成适配器的Item和动态数组对应的元素
		MySimpleAdapter listItemAdapter = new MySimpleAdapter(this, listItem,// 数据源
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
		listTitle = (ListView) findViewById(R.id.listcontent_titles_lv);
		listTitle.setAdapter(listItemAdapter);
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
			case R.id.listcontent_go_back:
				finish();
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
					Intent intent_edit = new Intent(B_ListContentActivity.this, B_EditContentActivity.class);
					ContentDBUtil dbUtil = new ContentDBUtil();
					ContentModel model = dbUtil.queryContentForDetail(id, B_ListContentActivity.this);
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
							, B_ListContentActivity.this);
					listItem.remove(mPosition);
					notifyDataSetChanged();
					Toast.makeText(B_ListContentActivity.this, "刪除成功", Toast.LENGTH_SHORT).show();
				}
			});
			return convertView;
		}
	}
}
