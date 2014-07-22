package yunhe.doit.testFunction;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import yunhe.doit.R;
import yunhe.doit.receiver.LinkOkReceiver;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * <strong>测试Spring for Android，简单的RestTemplate用法</strong>
 * 
 * @author yoara
 */
public class D_1_SFATestActivity extends Activity {
	private Button btSfa;
	private EditText etUrl;
	private TextView tvInfo;

	RestTemplate restTemplate = new RestTemplate();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.d_function_1_sfa);
		
		initLayoutView();
	}
	/** 初始化控件 **/
	private void initLayoutView() {
		etUrl = (EditText)findViewById(R.id.et_function_sfa);
		
		btSfa = (Button)findViewById(R.id.bt_function_sfa);
		btSfa.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sfaGetMethod();
			}
		});

		tvInfo = (TextView)findViewById(R.id.tv_function_sfa);
		
		restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
	}
	/**点击时访问指定URL**/
	private void sfaGetMethod() {
		String url = etUrl.getText().toString();
		if(!url.startsWith("http://")){	//懒汉判断
			url = "http://"+url;
		}
		new InternetLinkTask().execute(url);
	}
	
	/** 线程类，用于异步执行web访问任务 **/
	class InternetLinkTask extends AsyncTask<String,Void,String>{

		protected String doInBackground(String... params) {
			String result = restTemplate.getForObject(params[0], String.class, "Android");
			return result;
		}
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			tvInfo.setText(result);
			
			//请求返回后，将消息传递给broadcast receiver，调用toast和通知两种方式显示
			Intent intent = new Intent();
			intent.setAction(LinkOkReceiver.BROADCAST_ACTION);
			intent.putExtra(LinkOkReceiver.EXTRA_MSG, "请求已得到应答响应，请注意!");
			sendBroadcast(intent);
		}
	}
}
