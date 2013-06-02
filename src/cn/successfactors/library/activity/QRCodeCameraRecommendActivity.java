package cn.successfactors.library.activity;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.successfactors.library.R;
import cn.successfactors.library.bean.SLRecommendedBook;
import cn.successfactors.library.utils.Constants;
import cn.successfactors.library.utils.HTTPRequestHelper;
import cn.successfactors.library.utils.imagehelper.UrlImageViewHelper;

import com.google.gson.Gson;

public class QRCodeCameraRecommendActivity extends Activity {

	private String bookISBN;

	public final String SESSIONKEY = "sessionKey";
	public final String SLRecommendedBook = "SLRecommendedBook";

	public final String RECOMMEND_SUB_PATH = "recommend/recommendbook";
	public final String GET_RECOMMEND_FROM_DOUBAN_PATH = "book/getdoubanbookbyisbn/";
	public final int REC_SUCCESS = 461192;
	public final int REC_FAILED = 461191;

	private ProgressDialog progressDialog;
	private TextView detailName;
	private ImageView detailImg;
	private TextView detailAuthor;
	private TextView detailContent;
	private Button recommendBtn;
	private SLRecommendedBook recBook;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_qrcode_camera_recommend);

		getActionBar().setTitle("È·ÈÏÍÆ¼ö");
		getActionBar().setDisplayHomeAsUpEnabled(true); 
		
		Intent theIntent = getIntent();
		bookISBN = theIntent.getStringExtra("bookISBN");

		detailName = (TextView) this.findViewById(R.id.qr_recdetail_name);
		detailImg = (ImageView) this.findViewById(R.id.qr_recdetail_image);
		detailAuthor = (TextView) this.findViewById(R.id.qr_recdetail_author);
		detailContent = (TextView) this.findViewById(R.id.qr_recdetail_detail);
		recommendBtn = (Button) this.findViewById(R.id.qr_recdetailBtn);

		recommendBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Thread runner = new Thread(new Runnable() {
					@Override
					public void run() {
						performRecommend();
					}
				});
				runner.start();
			}
		});

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String myParam = Constants.ROOT_PATH
					+ GET_RECOMMEND_FROM_DOUBAN_PATH + bookISBN;
			performRequest(myParam);

		}
	}

	private void performRequest(final String url) {
		final ResponseHandler<String> responseHandler = HTTPRequestHelper
				.getResponseHandlerInstance(this.handler);

		this.progressDialog = ProgressDialog.show(this, "", getResources()
				.getString(R.string.loading));

		// do the HTTP dance in a separate thread (the responseHandler will fire
		// when complete)
		new Thread() {

			@Override
			public void run() {
				HTTPRequestHelper helper = new HTTPRequestHelper(
						responseHandler);
				helper.performGet(url, null, null, null);
			}
		}.start();
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {

		@Override
		public void handleMessage(final Message msg) {
			progressDialog.dismiss();
			String bundleResult = msg.getData().getString("RESPONSE");

			Gson gson = new Gson();
			recBook = gson.fromJson(bundleResult, SLRecommendedBook.class);

			detailName.setText(recBook.getBookName());
			detailAuthor.setText(recBook.getBookAuthor());
			detailContent.setText("¼ò½é:" + recBook.getBookIntro());
			UrlImageViewHelper.setUrlDrawable(detailImg,
					recBook.getBookPicUrl());

		}
	};

	private void performRecommend() {
		try {
			SharedPreferences userInfo = getSharedPreferences(
					Constants.USERPREFERENCE, 0);
			String sessionKey = userInfo.getString(Constants.SESSIONKEY, null);
			recBook.setSessionKey(sessionKey);
			recBook.setRecUserEmail(userInfo
					.getString(Constants.USERNAME, null));

			HttpClient httpclient = new DefaultHttpClient();
			Gson gson = new Gson();

			HttpPut httpput = new HttpPut(Constants.ROOT_PATH
					+ RECOMMEND_SUB_PATH);
			StringEntity se = new StringEntity(gson.toJson(recBook), "UTF-8");
			httpput.setEntity(se);
			HttpResponse response = httpclient.execute(httpput);

			String retSrc = EntityUtils.toString(response.getEntity());

			JSONObject result = new JSONObject(retSrc);

			Message msg = new Message();

			if (result.getString("restStatus").equals("fail")) {
				if (result.getString("restErrorCode").equals(
						"already_recommended")) {
					msg.what = REC_FAILED;
				}
			} else {
				msg.what = REC_SUCCESS;
			}

			recommendHandler.sendMessage(msg);
		} catch (Exception e) {
			Log.d("Login failed  ", e.getMessage());
			e.printStackTrace();
		}
	}

	@SuppressLint("HandlerLeak")
	Handler recommendHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			progressDialog.dismiss();
			int result = msg.what;
			switch (result) {

			case REC_FAILED:
				Toast.makeText(
						getApplicationContext(),
						QRCodeCameraRecommendActivity.this.getResources()
								.getString(R.string.recommend_already),
						Toast.LENGTH_SHORT).show();
				break;
			case REC_SUCCESS:
				Toast.makeText(
						getApplicationContext(),
						QRCodeCameraRecommendActivity.this.getResources()
								.getString(R.string.recommend_success),
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int item_id = item.getItemId();
		switch (item_id) {
		case android.R.id.home: {
			Intent scanint = new Intent();
			scanint.setClass(QRCodeCameraRecommendActivity.this, HomeActivity.class);
			scanint.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(scanint, 0);
			break;
		}
		}
		return true;
	}

}
