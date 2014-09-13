package yunhe.doit;

import yunhe.database.ContentDBUtil;
import yunhe.model.ContentModel;
import yunhe.util.Constants;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class B_EditContentActivity extends Activity {
	EditText textTitle;
	EditText textContent;
	Button save_bt;
	Button save_bt_go;
	Integer contentId;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_b_editcontent);
		
		textTitle = (EditText) findViewById(R.id.b_et_editTitle);
		textContent = (EditText) findViewById(R.id.b_et_editCont);
		
		save_bt = (Button) findViewById(R.id.b_bt_saveContent);
		save_bt.setOnClickListener(listener);
		
		save_bt_go = (Button) findViewById(R.id.b_bt_saveContent_go);
		save_bt_go.setOnClickListener(listener);
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
			String title = null;
			switch (v.getId()) {
			case R.id.b_bt_saveContent:
				title = textTitle.getText().toString();
				if(title.trim().length()==0){
					Toast.makeText(B_EditContentActivity.this, "请至少输入标题", Toast.LENGTH_SHORT).show();
					return;
				}
				saveContentIntoDb();
				finish();
				break;
			case R.id.b_bt_saveContent_go:
				title = textTitle.getText().toString();
				if(title.trim().length()==0){
					Toast.makeText(B_EditContentActivity.this, "请至少输入标题", Toast.LENGTH_SHORT).show();
					return;
				}
				saveContentIntoDb();
				clearAllInput();
				Toast.makeText(B_EditContentActivity.this, "保存成功，请继续输入", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}
	};
	private void clearAllInput() {
		textTitle.setText("");
		textContent.setText("");
	}
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
