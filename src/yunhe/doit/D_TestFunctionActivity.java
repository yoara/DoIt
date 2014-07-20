package yunhe.doit;

import yunhe.doit.testFunction.D_1_SFATestActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * 提供各种测试的页面
 * @author yoara
 */
public class D_TestFunctionActivity extends Activity {
	private Button mbtSfa;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.d_test_function_);
		
		mbtSfa = (Button)findViewById(R.id.bt_function_sfa);
		mbtSfa.setOnClickListener(listener);
	}
	private OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.bt_function_sfa:
				Intent intent_sfa = new Intent(D_TestFunctionActivity.this,
						D_1_SFATestActivity.class);
				startActivity(intent_sfa);
				break;
			default:
				break;
			}
		}
	};
}
