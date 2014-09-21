package yunhe.doit;

import java.util.Calendar;

import net.simonvt.numberpicker.NumberPicker;
import net.simonvt.numberpicker.NumberPicker.OnValueChangeListener;
import yunhe.database.UserInfoDBUtil;
import yunhe.model.UserInfoModel;
import yunhe.util.Constants;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

public class C_InfoActivity extends Activity {
	Dialog builder_date;
	NumberPicker dateYear;
	NumberPicker dateMonth;
	NumberPicker dateDay;
	Dialog builder_male ;
	Dialog builder_about ;
	int year;
	int day;
	int month;
	EditText textName;
	EditText textDate;
	EditText textMale;
	CheckBox weibo;
	ImageView setIv; 
	boolean hasUserInfo = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_c_info);

		builder_date = new Dialog(C_InfoActivity.this, R.style.dialog);
		builder_date.setContentView(R.layout.c_info_date_dialog);
		
		builder_male = new Dialog(C_InfoActivity.this, R.style.dialog);
		builder_male.setContentView(R.layout.c_info_male_dialog);
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
		
		setIv = (ImageView) findViewById(R.id.info_lv_setting);
		setIv.setOnClickListener(listener);
		
		TextView about = (TextView)findViewById(R.id.tv_info_showAbout);
		about.setOnClickListener(listener);
		builder_about = new Dialog(C_InfoActivity.this, R.style.dialog);
		builder_about.setContentView(R.layout.c_info_about_dialog);
		dateYear = (NumberPicker) builder_date.findViewById(R.id.c_date_year);
		initNumberPicker(dateYear,2014,1970,year_picker);
		dateMonth = (NumberPicker) builder_date.findViewById(R.id.c_date_month);
		initNumberPicker(dateMonth,12,1,month_picker);
		dateDay = (NumberPicker) builder_date.findViewById(R.id.c_date_day);
		initNumberPicker(dateDay,31,1,day_picker);
		timeInit();
		
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
	
	private static final int year_picker = 0;
	private static final int month_picker = 1;
	private static final int day_picker = 2;
	private void initNumberPicker(NumberPicker np, int maxValue, int minValue,int type) {
		np.setMaxValue(maxValue);
		np.setMinValue(minValue);
		np.setFocusable(true);
		np.setFocusableInTouchMode(true);
		np.setOnValueChangedListener(new HereOnValueChangeListener(type));
	}
	private class HereOnValueChangeListener implements OnValueChangeListener{
		private int type;
		
		public HereOnValueChangeListener(int type){
			this.type = type;
		}
		@Override
		public void onValueChange(NumberPicker picker, int oldVal,
				int newVal) {
			switch (type) {
			case year_picker:
				C_InfoActivity.this.year = newVal;
				break;
			case month_picker:
				C_InfoActivity.this.month = newVal;
				break;
			case day_picker:
				C_InfoActivity.this.day = newVal;
				break;
			default:
				break;
			}
			setDate();
		}
	};
	
	private void timeInit() {
		Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		setDate();
		dateYear.setValue(year);
		dateMonth.setValue(month+1);
		dateDay.setValue(day);
	}
	final private void setDate(){
		StringBuffer sbDate = new StringBuffer();
		sbDate.append(year).append("-");
		sbDate.append(month).append("-");
		sbDate.append(day);
		textDate.setText(sbDate.toString());
	}

	private OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.bt_c_info_save:
				saveInfoToDb();
				Toast.makeText(C_InfoActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
				finish();
				break;
			case R.id.et_c_info_editDate:
				builder_date.show();
				break;
			case R.id.et_c_info_male:
				builder_male.show();
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
			case R.id.info_lv_setting:
				goSettingButton();
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
		UserInfoDBUtil dbUtil = UserInfoDBUtil.getInstance();
		if(hasUserInfo){
			dbUtil.updateUserInfoDb(model, C_InfoActivity.this);
		}else{
			dbUtil.saveToUserInfoDb(model, C_InfoActivity.this);
		}
	}
	/** 点击设置键 **/
	protected void goSettingButton(){
		 Intent intent = new Intent();  
        /* 开启Pictures画面Type设定为image */  
        intent.setType("image/*");  
        /* 使用Intent.ACTION_GET_CONTENT这个Action */  
        intent.setAction(Intent.ACTION_GET_CONTENT);   
        /* 取得相片后返回本画面 */  
        startActivityForResult(intent, 1);
	}
	
	/**
	 * goSettingButton选取图片后触发
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			Uri uri = data.getData();
			UserInfoDBUtil db = UserInfoDBUtil.getInstance();
			String path = null;
			path = uri.toString();
			db.updateBackgroundImg(path,this);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
