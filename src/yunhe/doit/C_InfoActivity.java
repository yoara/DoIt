package yunhe.doit;

import java.util.Calendar;
import yunhe.database.UserInfoDBUtil;
import yunhe.model.UserInfoModel;
import yunhe.util.Constants;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TextView;

public class C_InfoActivity extends Activity {
	Dialog builder;
	Dialog builder_male ;
	Dialog builder_about ;
	int year;
	int day;
	int month;
	EditText textName;
	EditText textDate;
	EditText textMale;
	CheckBox weibo;
	boolean hasUserInfo = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_c_info);

		builder = new Dialog(C_InfoActivity.this, R.style.dialog);
		builder.setContentView(R.layout.c_info_date_dialog);
		
		builder_male = new Dialog(C_InfoActivity.this, R.style.dialog);
		builder_male.setContentView(R.layout.c_info_male_dialog);
		Button saveDate = (Button) builder.findViewById(R.id.bt_c_info_saveDate);
		saveDate.setOnClickListener(listener);
		Button saveMale_girl = (Button) builder_male.findViewById(R.id.bt_c_male_girl);
		Button saveMale_boy = (Button) builder_male.findViewById(R.id.bt_c_male_boy);
		saveMale_girl.setOnClickListener(listener);
		saveMale_boy.setOnClickListener(listener);
		
		textName = (EditText) findViewById(R.id.et_c_info_name);
		textDate = (EditText) findViewById(R.id.et_c_info_editDate);
		textMale = (EditText) findViewById(R.id.et_c_info_male);
		textDate.setOnClickListener(listener);
		textMale.setOnClickListener(listener);
		weibo = (CheckBox)findViewById(R.id.cb_c_info_weibo);
		weibo.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton paramCompoundButton,
					boolean paramBoolean) {
				if(paramBoolean){
					weibo.setTag("1");
				}else{
					weibo.setTag("0");
				}
			}
		});
		
		TextView about = (TextView)findViewById(R.id.tv_info_showAbout);
		about.setOnClickListener(listener);
		builder_about = new Dialog(C_InfoActivity.this, R.style.dialog);
		builder_about.setContentView(R.layout.c_info_about_dialog);
		
		DatePicker datepicker = (DatePicker) builder.findViewById(R.id.dp_c_date_datePicker);
		timeInit(datepicker);
		
		Button save_info = (Button) findViewById(R.id.bt_c_info_save);
		save_info.setOnClickListener(listener);
		
		UserInfoModel model = (UserInfoModel)getIntent().getParcelableExtra(Constants.USERINFO_MODEL);
		if(model!=null){
			hasUserInfo = true;
			textName.setText(model.getName());
			textDate.setText(model.getDate());
			textMale.setText(model.getMale());
			if("1".equals(model.getWeibo())){
				weibo.setChecked(true);
				weibo.setTag("1");
			}else{
				weibo.setChecked(false);
				weibo.setTag("0");
			}
		}
	}
	private void timeInit(DatePicker datepicker) {
		Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		setDate();
		datepicker.init(year, month, day, new OnDateChangedListener() {
			@Override
			public void onDateChanged(DatePicker view, int year,
					int monthOfYear, int dayOfMonth) {
				C_InfoActivity.this.year = year;
				C_InfoActivity.this.month = monthOfYear;
				C_InfoActivity.this.day = dayOfMonth;
			}
		});
	}
	final private void setDate(){
		StringBuffer sbDate = new StringBuffer();
		sbDate.append(year).append("-");
		sbDate.append(month+1).append("-");
		sbDate.append(day);
		textDate.setText(sbDate.toString());
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
			case R.id.bt_c_info_save:
				saveInfoToDb();
				Toast.makeText(C_InfoActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
				Intent intent_main = new Intent(C_InfoActivity.this,
						A_MainActivity.class);
				startActivity(intent_main);
				break;
			case R.id.et_c_info_editDate:
				builder.show();
				break;
			case R.id.et_c_info_male:
				builder_male.show();
				break;
			case R.id.bt_c_info_saveDate:
				setDate();
				builder.hide();
				break;
			case R.id.bt_c_male_girl:
				builder_male.hide();
				textMale.setText("Girl");
				break;
			case R.id.bt_c_male_boy:
				builder_male.hide();
				textMale.setText("Boy");
				break;
			case R.id.tv_info_showAbout:
				builder_about.show();
				break;
			default:
				break;
			}
		}
	};
	private void saveInfoToDb() {
		UserInfoModel model = new UserInfoModel();
		model.setName(textName.getText().toString());
		model.setDate(textDate.getText().toString());
		model.setMale(textMale.getText().toString());
		model.setWeibo(weibo.getTag().toString());
		UserInfoDBUtil dbUtil = new UserInfoDBUtil();
		if(hasUserInfo){
			dbUtil.updateUserInfoDb(model, C_InfoActivity.this);
		}else{
			dbUtil.saveToUserInfoDb(model, C_InfoActivity.this);
		}
	}
}
