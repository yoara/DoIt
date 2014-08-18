package yunhe.doit;

import java.util.Calendar;

import yunhe.database.ContentDBUtil;
import yunhe.model.ContentModel;
import yunhe.util.Constants;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

public class A_EditContentActivity extends Activity {
	Dialog builder;
	int year;
	int day;
	int month;
	int hour;
	int minute;
	EditText textTitle;
	EditText textContent;
	EditText textDate;
	EditText textTime;
	Button save_bt;
	Integer contentId;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_a_editcontent);

		builder = new Dialog(A_EditContentActivity.this, R.style.dialog);
		builder.setContentView(R.layout.activity_a_editcontent_date_dialog);
		
		textDate = (EditText) findViewById(R.id.et_editDate);
		textTime = (EditText) findViewById(R.id.et_editTime);
		textDate.setOnClickListener(listener);
		textTime.setOnClickListener(listener);
		
		textTitle = (EditText) findViewById(R.id.et_editTitle);
		textContent = (EditText) findViewById(R.id.et_editCont);
		
		DatePicker datepicker = (DatePicker) builder.findViewById(R.id.datepicker);
		TimePicker timepicker = (TimePicker) builder.findViewById(R.id.timePicker);
		timeInit(datepicker, timepicker);
		Button saveDate = (Button) builder.findViewById(R.id.bt_editcontent_saveDate);
		saveDate.setOnClickListener(listener);
		
		save_bt = (Button) findViewById(R.id.bt_saveContent);
		save_bt.setOnClickListener(listener);
		
		setContentModel();
	}
	private void setContentModel() {
		ContentModel model = (ContentModel)getIntent().getParcelableExtra(Constants.DETAIL_MODEL);
		if(model!=null){
			contentId = model.getId();
			textTitle.setText(model.getTitle());
			textContent.setText(model.getContent());
			textDate.setText(model.getDate());
			textTime.setText(model.getTime());
		}
	}
	private void timeInit(DatePicker datepicker, TimePicker timepicker) {
		Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		hour = c.get(Calendar.HOUR_OF_DAY);
		minute = c.get(Calendar.MINUTE);
		setDate();
		datepicker.init(year, month, day, new OnDateChangedListener() {
			@Override
			public void onDateChanged(DatePicker view, int year,
					int monthOfYear, int dayOfMonth) {
				A_EditContentActivity.this.year = year;
				A_EditContentActivity.this.month = monthOfYear;
				A_EditContentActivity.this.day = dayOfMonth;
				setDate();
			}
		});
		timepicker.setOnTimeChangedListener(new OnTimeChangedListener() {

			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				A_EditContentActivity.this.hour = hourOfDay;
				A_EditContentActivity.this.minute = minute;
				setDate();
			}
		});
	}
	final private void setDate(){
		StringBuffer sbDate = new StringBuffer();
		sbDate.append(year).append("-");
		sbDate.append(month+1).append("-");
		sbDate.append(day);
		StringBuffer sbTime = new StringBuffer();
		sbTime.append(hour).append(":");
		sbTime.append(minute);
		
		textDate.setText(sbDate.toString());
		textTime.setText(sbTime.toString());
	}
	
	private OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.bt_saveContent:
				saveContentIntoDb();
				Toast.makeText(A_EditContentActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
				Intent intent_main = new Intent(A_EditContentActivity.this,
						A_MainActivity.class);
				startActivity(intent_main);
				break;
			case R.id.et_editDate:
				builder.show();
				break;
			case R.id.et_editTime:
				builder.show();
				break;
			case R.id.bt_editcontent_saveDate:
				builder.hide();
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
		model.setDate(textDate.getText().toString());
		model.setTime(textTime.getText().toString());
		if(model.getTime().length()<2){
			model.setTime("0"+model.getTime());
		}
		ContentDBUtil dbUtil = new ContentDBUtil();
		if(contentId!=null){
			model.setId(contentId);
			dbUtil.updateContentDb(model, A_EditContentActivity.this);
		}else{
			dbUtil.saveToContentDb(model, A_EditContentActivity.this);
		}
	}
}
