package yunhe.doit.testFunction;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import yunhe.doit.R;
import android.app.Activity;
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
	private Button mbtSfa;
	private EditText etUrl;
	private TextView tvInfo;
	
	private RestTemplate restTemplate = new RestTemplate();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.d_function_1_sfa);

		restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
		
		etUrl = (EditText)findViewById(R.id.et_function_sfa);
		mbtSfa = (Button)findViewById(R.id.bt_function_sfa);
		tvInfo = (TextView)findViewById(R.id.tv_function_sfa);
				
		mbtSfa.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sfaGetInfo();
			}
		});
	}
	private void sfaGetInfo() {
		String url = etUrl.getText().toString();
		if(!url.startsWith("http://")){
			url = "http://"+url;
		}
		String result = restTemplate.getForObject(url, String.class, "Android");
		tvInfo.setText(result);
	}
}
