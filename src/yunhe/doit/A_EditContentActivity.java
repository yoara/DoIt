package yunhe.doit;

import java.util.Calendar;
import java.util.Date;

import net.simonvt.numberpicker.NumberPicker;
import net.simonvt.numberpicker.NumberPicker.OnValueChangeListener;
import yunhe.database.ContentDBUtil;
import yunhe.model.ContentModel;
import yunhe.util.Constants;
import yunhe.util.DateUtil;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class A_EditContentActivity extends Activity {
	private static int DATE_IN = 0;
	
	public static void setDATE_IN(int dateIn) {
		DATE_IN = dateIn;
	}
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
	Button save_bt_go;
	Integer contentId;

	Dialog builder_date;
	NumberPicker dateYear;
	NumberPicker dateMonth;
	NumberPicker dateDay;
	Dialog builder_time;
	NumberPicker dateHour;
	NumberPicker dateMinute;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_a_editcontent);
		
		textDate = (EditText) findViewById(R.id.et_editDate);
		textTime = (EditText) findViewById(R.id.et_editTime);
		textDate.setOnTouchListener(touchLis);
		textTime.setOnTouchListener(touchLis);
		
		textTitle = (EditText) findViewById(R.id.et_editTitle);
		textContent = (EditText) findViewById(R.id.et_editCont);
		
		builder_date = new Dialog(A_EditContentActivity.this, R.style.dialog);
		builder_date.setContentView(R.layout.activity_a_editcontent_dialog_date);
		dateYear = (NumberPicker) builder_date.findViewById(R.id.editcontent_date_year);
		initNumberPicker(dateYear,2014,1970,year_picker);
		dateMonth = (NumberPicker) builder_date.findViewById(R.id.editcontent_date_month);
		initNumberPicker(dateMonth,12,1,month_picker);
		dateDay = (NumberPicker) builder_date.findViewById(R.id.editcontent_date_day);
		initNumberPicker(dateDay,31,1,day_picker);
		
		builder_time = new Dialog(A_EditContentActivity.this, R.style.dialog);
		builder_time.setContentView(R.layout.activity_a_editcontent_dialog_time);
		dateHour = (NumberPicker) builder_time.findViewById(R.id.editcontent_date_hour);
		initNumberPicker(dateHour,23,0,hour_picker);
		dateMinute = (NumberPicker) builder_time.findViewById(R.id.editcontent_date_minute);
		initNumberPicker(dateMinute,59,0,minute_picker);
		timeInit();
		
		save_bt = (Button) findViewById(R.id.bt_saveContent);
		save_bt.setOnClickListener(listener);
		
		save_bt_go = (Button) findViewById(R.id.bt_saveContent_go);
		save_bt_go.setOnClickListener(listener);
		
		setContentModel();
	}
	
	private static final int year_picker = 0;
	private static final int month_picker = 1;
	private static final int day_picker = 2;
	private static final int hour_picker = 3;
	private static final int minute_picker = 4;
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
				A_EditContentActivity.this.year = newVal;
				break;
			case month_picker:
				A_EditContentActivity.this.month = newVal;
				break;
			case day_picker:
				A_EditContentActivity.this.day = newVal;
				break;
			case hour_picker:
				A_EditContentActivity.this.hour = newVal;
				break;
			case minute_picker:
				A_EditContentActivity.this.minute = newVal;
				break;
			default:
				break;
			}
			setDate();
		}
	};
	
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
	private void timeInit() {
		Calendar c = Calendar.getInstance();
		c.setTime(DateUtil.getAfterDateByDays(new Date(),DATE_IN));
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		hour = c.get(Calendar.HOUR_OF_DAY);
		minute = c.get(Calendar.MINUTE);
		setDate();
		
		dateYear.setValue(year);
		dateMonth.setValue(month+1);
		dateDay.setValue(day);
		dateHour.setValue(hour);
		dateMinute.setValue(minute);
	}
	
	final private void setDate(){
		StringBuffer sbDate = new StringBuffer();
		sbDate.append(year).append("-");
		sbDate.append(month+1>9?month+1:"0"+(month+1)).append("-");
		sbDate.append(day);
		StringBuffer sbTime = new StringBuffer();
		sbTime.append(hour).append(":");
		sbTime.append(minute>9?minute:"0"+minute);
		
		textDate.setText(sbDate.toString());
		textTime.setText(sbTime.toString());
	}
	
	
	
	private OnTouchListener touchLis = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (v.getId()) {
			case R.id.et_editDate:
				builder_date.show();
				break;
			case R.id.et_editTime:
				builder_time.show();
				break;
			default:
				break;
			}
			return false;
		}
	};
	private OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			String title = null;
			switch (v.getId()) {
			case R.id.bt_saveContent:
				title = textTitle.getText().toString();
				if(title.trim().length()==0){
					Toast.makeText(A_EditContentActivity.this, "请至少输入标题", Toast.LENGTH_SHORT).show();
					return;
				}
				saveContentIntoDb();
				builder_date.dismiss();
				builder_time.dismiss();
				finish();
				break;
			case R.id.bt_saveContent_go:
				title = textTitle.getText().toString();
				if(title.trim().length()==0){
					Toast.makeText(A_EditContentActivity.this, "请至少输入标题", Toast.LENGTH_SHORT).show();
					return;
				}
				saveContentIntoDb();
				clearAllInput();
				timeInit();
				Toast.makeText(A_EditContentActivity.this, "保存成功，请继续输入", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}
	};
	
	private void clearAllInput() {
		textTitle.setText("");
		textContent.setText("");
		textDate.setText("");
		textTime.setText("");
	}
	
	private void saveContentIntoDb() {
		ContentModel model = new ContentModel();
		model.setTitle(textTitle.getText().toString());
		model.setContent(textContent.getText().toString());
		model.setDate(textDate.getText().toString());
		model.setTime(textTime.getText().toString());
		if(model.getTime().length()<2){
			model.setTime("0"+model.getTime());
		}
		model.setIsAlarm(ContentModel.ISALARM_NOT);
		ContentDBUtil dbUtil = new ContentDBUtil();
		if(contentId!=null){
			model.setId(contentId);
			dbUtil.updateContentDb(model, A_EditContentActivity.this);
		}else{
			dbUtil.saveToContentDb(model, A_EditContentActivity.this);
		}
	}
}
