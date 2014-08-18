package yunhe.doit;

import yunhe.database.ContentDBUtil;
import yunhe.model.ContentModel;
import yunhe.util.Constants;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class B_EditContentActivity extends Activity {
	EditText textTitle;
	EditText textContent;
	Button save_bt;
	Integer contentId;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_b_editcontent);
		
		textTitle = (EditText) findViewById(R.id.b_et_editTitle);
		textContent = (EditText) findViewById(R.id.b_et_editCont);
		
		save_bt = (Button) findViewById(R.id.b_bt_saveContent);
		save_bt.setOnClickListener(listener);
		
		setContentModel();
	}
	private void setContentModel() {
		ContentModel model = (ContentModel)getIntent().getParcelableExtra(Constants.DETAIL_MODEL);
		if(model!=null){
			contentId = model.getId();
			textTitle.setText(model.getTitle());
			textContent.setText(model.getContent());
		}
	}
	
	private OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.b_bt_saveContent:
				saveContentIntoDb();
				Toast.makeText(B_EditContentActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
				Intent intent_main = new Intent(B_EditContentActivity.this,
						B_ListContentActivity.class);
				startActivity(intent_main);
				break;
			default:
				break;
			}
		}
	};
	private void saveContentIntoDb() {
		ContentModel model = new ContentModel();
		model.setTitle(textTitle.getText().toString());
		model.setContent(textContent.getText().toString());
		ContentDBUtil dbUtil = new ContentDBUtil();
		if(contentId!=null){
			model.setId(contentId);
			dbUtil.updateContentDb(model, B_EditContentActivity.this);
		}else{
			dbUtil.saveToContentDb(model, B_EditContentActivity.this);
		}
	}
}
