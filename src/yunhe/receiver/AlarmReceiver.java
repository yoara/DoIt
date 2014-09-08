package yunhe.receiver;

import yunhe.doit.A_MainActivity;
import yunhe.doit.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
	public static final String EXTRA_MSG = "msg";
	public static final String BROADCAST_ACTION = "yunhe.doit.alarmLink";

	@Override
	public void onReceive(Context context, Intent intent) {
		makeNotify(context,intent);
	}

	private void makeNotify(Context context, Intent intent) {
		NotificationManager nManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		String notifyTitle = intent.getStringExtra(EXTRA_MSG);
		Notification notify = new Notification(R.drawable.ic_launcher, notifyTitle,
				System.currentTimeMillis());
		notify.flags = Notification.FLAG_AUTO_CANCEL;
		// 设置默认声音
		notify.defaults |= Notification.DEFAULT_SOUND;

		String title = "闹钟时间到";
		String msgContent = "有任务项的时间到啦";

		Intent notifyIntent = new Intent(context, A_MainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		notify.setLatestEventInfo(context, title, msgContent, contentIntent);
		nManager.notify(1, notify);
	}
}
