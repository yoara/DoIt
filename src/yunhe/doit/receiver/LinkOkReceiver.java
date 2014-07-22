package yunhe.doit.receiver;

import yunhe.doit.R;
import yunhe.doit.testFunction.D_1_SFATestActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class LinkOkReceiver extends BroadcastReceiver {
	public static final String EXTRA_MSG = "msg";
	public static final String BROADCAST_ACTION = "yunhe.doit.urlLink";

	@Override
	public void onReceive(Context context, Intent intent) {
		String msg = intent.getStringExtra(EXTRA_MSG);
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();

		makeNotify(context);

	}

	private void makeNotify(Context context) {
		NotificationManager nManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notify = new Notification(R.drawable.ic_launcher, "",
				System.currentTimeMillis());
		notify.flags = Notification.FLAG_AUTO_CANCEL;
		// 设置默认声音
		notify.defaults |= Notification.DEFAULT_SOUND;

		String title = "通知栏标题";
		String msgContent = "内容返回啦，可以查看啦";

		Intent notifyIntent = new Intent(context, D_1_SFATestActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		notify.setLatestEventInfo(context, title, msgContent, contentIntent);
		nManager.notify(1, notify);
	}
}
