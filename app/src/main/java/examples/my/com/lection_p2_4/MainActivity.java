package examples.my.com.lection_p2_4;

//import android.app.job.JobInfo;
//import android.app.job.JobScheduler;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;

/*
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
*/

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static android.R.attr.button;
import static android.net.ConnectivityManager.RESTRICT_BACKGROUND_STATUS_DISABLED;
import static android.net.ConnectivityManager.RESTRICT_BACKGROUND_STATUS_ENABLED;
import static android.net.ConnectivityManager.RESTRICT_BACKGROUND_STATUS_WHITELISTED;

public class MainActivity extends AppCompatActivity {

	final static int EXAMPLE_JOB_ID = 1;

	private DataSaverChangedBroadcastReceiver dataSaverChangedBroadcastReceiver = new DataSaverChangedBroadcastReceiver();
	TextView text;

	class MyTimerTask extends TimerTask {
		TextView out;

		public MyTimerTask(TextView text) {
			out = text;
		}

		@Override
		public void run() {
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					"dd:MMMM:yyyy HH:mm:ss a", Locale.getDefault());
			final String strDate = simpleDateFormat.format(calendar.getTime());

			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					out.setText(strDate);
				}
			});
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		text = (TextView)findViewById(R.id.main_text);


		TextView tvTimer = (TextView)findViewById(R.id.timer);
		Timer timer = new Timer();
		MyTimerTask timerTask = new MyTimerTask(tvTimer);
		timer.schedule(timerTask, 1000, 2000);


		//TODO: FireBase
		//Driver myDriver = new GooglePlayDriver(this);
		//final FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(myDriver);

		Button bt = (Button)findViewById(R.id.main_button);
		Button bt2 = (Button)findViewById(R.id.main_button2);
		bt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				JobScheduler jobScheduler =
						(JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
				jobScheduler.schedule(new JobInfo.Builder(EXAMPLE_JOB_ID,
						new ComponentName(MainActivity.this, ExampleJobService.class))
						.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
						.setRequiresCharging(true)
						.build());

				/*
				Job job = dispatcher.newJobBuilder()
						.setService(ExampleJobService.class)
						.setTag("my-tag")
						.setConstraints(
								Constraint.DEVICE_CHARGING,
								Constraint.ON_UNMETERED_NETWORK)
						.setTrigger(Trigger.NOW)
						.setLifetime(Lifetime.UNTIL_NEXT_BOOT)
						//.setRecurring(false)
						.build();

				int result = dispatcher.schedule(job);
				if (result != FirebaseJobDispatcher.SCHEDULE_RESULT_SUCCESS) {
					// handle error
				}
				*/
			}
		});
		checkStatus();

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
			bt2.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Intent intent = null;
					intent = new Intent(Settings.ACTION_IGNORE_BACKGROUND_DATA_RESTRICTIONS_SETTINGS, Uri.parse("package:" + getPackageName()));
					startActivity(intent);
				}
			});
			registerReceiver(dataSaverChangedBroadcastReceiver, new IntentFilter(ConnectivityManager.ACTION_RESTRICT_BACKGROUND_CHANGED));
		} else {
			bt2.setEnabled(false);
			bt2.setVisibility(View.INVISIBLE);
		}

	}

	public void checkStatus() {
		ConnectivityManager connMgr = (ConnectivityManager)
				getSystemService(Context.CONNECTIVITY_SERVICE);
		// Checks if the device is on a metered network
		if (connMgr.isActiveNetworkMetered()) {
			// Checks user’s Data Saver settings.
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				switch (connMgr.getRestrictBackgroundStatus()) {
					case RESTRICT_BACKGROUND_STATUS_ENABLED:
						// Background data usage is blocked for this app. Wherever possible,
						// the app should also use less data in the foreground.
						text.setText("Enabled Data Saver.");
						break;

					case RESTRICT_BACKGROUND_STATUS_WHITELISTED:
						// The app is whitelisted. Wherever possible,
						// the app should use less data in the foreground and background.
						text.setText("The app is whitelisted.");
						break;

					case RESTRICT_BACKGROUND_STATUS_DISABLED:
						// Data Saver is disabled. Since the device is connected to a
						// metered network, the app should use less data wherever possible.
						text.setText("Disabled Data Saver.");
						break;
				}
			}
		} else {
			// The device is not on a metered network.
			// Use data as required to perform syncs, downloads, and updates.
			text.setText("The device is not on a metered network.");
		}
	}

	private static class DataSaverChangedBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			Log.d("onReceive", "DataSaverChangedBroadcastReceiver");
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				Log.d("onReceive", "getRestrictBackgroundStatus : " + connectivityManager.getRestrictBackgroundStatus());
			}
		}
	}
}
