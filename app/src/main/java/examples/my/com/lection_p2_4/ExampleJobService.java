package examples.my.com.lection_p2_4;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

//import com.firebase.jobdispatcher.JobParameters;
//import com.firebase.jobdispatcher.JobService;

import static android.content.ContentValues.TAG;

public class ExampleJobService extends JobService {

	private static final String TAG = "ExampleJobService";

    static class DownloadArtworkTask extends AsyncTask<Void, Integer, Boolean> {

		public DownloadArtworkTask(JobParameters params) {
			mParams = params;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			return true;
		}
		JobParameters mParams;
	}

	DownloadArtworkTask mDownloadArtworkTask;


	@Override
	public boolean onStartJob(JobParameters params) {
		Log.d(TAG, "onStartJob() called with: params = [" + params + "]");
		// TODO: Main Thread
		mDownloadArtworkTask = new DownloadArtworkTask(params) {
			@Override
			protected void onPostExecute(Boolean success) {
				Toast.makeText(getApplicationContext(), "Job is done", Toast.LENGTH_LONG).show();
				jobFinished(mParams, !success); ///<--- TODO: necessary
			}
		};
		mDownloadArtworkTask.execute();
		return true;
	}

	@Override
	public boolean onStopJob(JobParameters params) {
		Log.d(TAG, "onStopJob() called with: params = [" + params + "]");
		// TODO: Main Thread
		if (mDownloadArtworkTask != null) {
			mDownloadArtworkTask.cancel(true);
		}
		return false;
	}
}
