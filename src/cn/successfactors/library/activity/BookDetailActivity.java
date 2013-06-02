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
import cn.successfactors.library.bean.SLBook;
import cn.successfactors.library.utils.Constants;
import cn.successfactors.library.utils.HTTPRequestHelper;
import cn.successfactors.library.utils.imagehelper.UrlImageViewHelper;

import com.google.gson.Gson;

@SuppressLint("HandlerLeak")
public class BookDetailActivity extends Activity {

	private ProgressDialog progressDialog;
	private ImageView detailImg;
	private TextView detailBookName;
	private TextView detailAuthor;
	private TextView detailBookISBN;
	private TextView detailAllCount;
	private TextView detailHasCount;
	private TextView detailAvailableCount;
	private TextView detailBookType;
	private TextView detailBookContent;
	private TextView detailBookPublish;
	private TextView detailBookPublishdate;
	private TextView detailBookLanguage;
	private TextView detailBookPerson;
	private TextView detailBookPrice;
	private Button btnBooking;
	private Boolean bookBorrowOrOrderStatus; // true can borrow; false can order
	private String bookISBNInfo;

	public final String BOOK_DETAIL_SUB_PATH = "book/getbookbyisbn/";
	public final String BOOK_ORDER_SUB_PATH = "order/orderbook";
	public final String BOOK_BORROW_SUB_PATH = "borrow/borrowbook";
	public final int BORROW_SUCCESS = 461193;
	public final int BORROW_FAIL = 461194;
	public final int ORDER_SUCCESS = 461195;
	public final int ORDER_FAIL = 461196;
	public final int ALREADY_BORROW = 461197;
	public final int ALREADY_ORDER = 461198;

	OnClickListener changeListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			String detailUrl = getIntent().getExtras().getString("url");
			Intent intent = new Intent(BookDetailActivity.this,
					RecentBorrowAndBooking.class);
			intent.putExtra("url", detailUrl);
			startActivity(intent);
		}

	};

	OnClickListener borrowOrOrderListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Thread runner = new Thread(new Runnable() {
				@Override
				public void run() {
					borrowOrOrderCurrentBook();
				}
			});
			runner.start();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// requestWindowFeature(Window.FEATURE_NO_TITLE);//hidden title
		setContentView(R.layout.activity_book_detail);

		getActionBar().setTitle("书目详情");
		getActionBar().setDisplayHomeAsUpEnabled(true);  

		detailImg = (ImageView) this.findViewById(R.id.book_detail_image);
		detailBookName = (TextView) this.findViewById(R.id.book_detail_name);
		detailAuthor = (TextView) this.findViewById(R.id.book_detail_author);
		detailBookISBN = (TextView) this.findViewById(R.id.book_detail_isbn);
		detailAllCount = (TextView) this
				.findViewById(R.id.book_detail_allcount);
		detailHasCount = (TextView) this
				.findViewById(R.id.book_detail_hascount);
		detailAvailableCount = (TextView) this
				.findViewById(R.id.book_detail_availablecount);
		detailBookType = (TextView) this.findViewById(R.id.book_detail_type);
		detailBookContent = (TextView) this
				.findViewById(R.id.book_detail_realcontent);
		detailBookPublish = (TextView) this
				.findViewById(R.id.book_detail_publish);
		detailBookPublishdate = (TextView) this
				.findViewById(R.id.book_detail_publishdate);
		detailBookLanguage = (TextView) this
				.findViewById(R.id.book_detail_language);
		detailBookPerson = (TextView) this
				.findViewById(R.id.book_detail_person);
		detailBookPrice = (TextView) this.findViewById(R.id.book_detail_price);
		btnBooking = (Button) this.findViewById(R.id.book_borrow_or_order);

		Button btnCurrentStatus = (Button) this
				.findViewById(R.id.book_current_status);
		btnCurrentStatus.setText("当前借阅与预订");
		btnCurrentStatus.setOnClickListener(changeListener);
		btnBooking.setOnClickListener(borrowOrOrderListener);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String myParam = Constants.ROOT_PATH + BOOK_DETAIL_SUB_PATH
					+ extras.getString("url");
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

	Handler handler = new Handler() {

		@Override
		public void handleMessage(final Message msg) {
			progressDialog.dismiss();
			String bundleResult = msg.getData().getString("RESPONSE");
			Gson gson = new Gson();
			SLBook Book = gson.fromJson(bundleResult, SLBook.class);
			int CurrentBookAvailableQuantity = Book.getBookAvailableQuantity();
			bookISBNInfo = Book.getBookISBN();

			detailBookName.setText("" + Book.getBookName());
			detailAuthor.setText("" + Book.getBookAuthor());
			detailBookISBN.setText("ISBN: " + bookISBNInfo);

			detailAllCount.setText(Book.getBookTotalQuantity() + "本");
			detailHasCount.setText(Book.getBookInStoreQuantity() + "本");
			detailAvailableCount.setText(CurrentBookAvailableQuantity + "本");

			detailBookType.setText("类别:  " + Book.getBookClass());
			detailBookContent.setText("简介:  " + Book.getBookIntro());

			detailBookPublish.setText("出版社:  " + Book.getBookPublisher());
			String s = Book.getBookPublishDate();
			int i = s.indexOf(" ");
			String date = s.substring(0, i);
			detailBookPublishdate.setText("出版日期:  " + date);
			detailBookLanguage.setText("语言:  " + Book.getBookLanguage());
			detailBookPerson.setText("贡献者:  " + Book.getBookContributor());
			detailBookPrice.setText("价格:  " + Book.getBookPrice());

			if (CurrentBookAvailableQuantity > 0) {
				bookBorrowOrOrderStatus = true;
				btnBooking.setText("借阅本书");
			} else {
				bookBorrowOrOrderStatus = false;
				btnBooking.setText("预订本书");
			}

			UrlImageViewHelper.setUrlDrawable(detailImg, Book.getBookPicUrl());
		}
	};

	private void borrowOrOrderCurrentBook() {
		try {
			SharedPreferences userInfo = getSharedPreferences(
					Constants.USERPREFERENCE, 0);
			String sessionKey = userInfo.getString(Constants.SESSIONKEY, null);

			JSONObject param = new JSONObject();
			param.put("bookISBN", bookISBNInfo);
			param.put("sessionKey", sessionKey);

			HttpClient httpclient = new DefaultHttpClient();

			String httpSubPath = "";
			httpSubPath = bookBorrowOrOrderStatus == true ? BOOK_BORROW_SUB_PATH
					: BOOK_ORDER_SUB_PATH;
			HttpPut httpput = new HttpPut(Constants.ROOT_PATH + httpSubPath);
			StringEntity se = new StringEntity(param.toString());
			httpput.setEntity(se);
			HttpResponse response = httpclient.execute(httpput);

			String retSrc = EntityUtils.toString(response.getEntity());

			JSONObject result = new JSONObject(retSrc);

			Message msg = new Message();

			if (result.getString("restStatus").equals("success")) {
				msg.what = bookBorrowOrOrderStatus == true ? BORROW_SUCCESS
						: ORDER_SUCCESS;
			} else if (result.getString("restErrorCode").equals(
					"already_borrowed")) {
				msg.what = ALREADY_BORROW;
			} else if (result.getString("restErrorCode").equals(
					"already_ordered")) {
				msg.what = ALREADY_ORDER;
			} else {
				msg.what = bookBorrowOrOrderStatus == true ? BORROW_FAIL
						: ORDER_FAIL;
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
			case BORROW_SUCCESS:
				Toast.makeText(
						getApplicationContext(),
						BookDetailActivity.this.getResources().getString(
								R.string.borrow_success), Toast.LENGTH_SHORT)
						.show();
				break;
			case BORROW_FAIL:
				Toast.makeText(
						getApplicationContext(),
						BookDetailActivity.this.getResources().getString(
								R.string.borrow_fail), Toast.LENGTH_SHORT)
						.show();
				break;
			case ALREADY_BORROW:
				Toast.makeText(
						getApplicationContext(),
						BookDetailActivity.this.getResources().getString(
								R.string.already_borrow), Toast.LENGTH_SHORT)
						.show();
				break;
			case ORDER_SUCCESS:
				Toast.makeText(
						getApplicationContext(),
						BookDetailActivity.this.getResources().getString(
								R.string.order_success), Toast.LENGTH_SHORT)
						.show();
				break;
			case ORDER_FAIL:
				Toast.makeText(
						getApplicationContext(),
						BookDetailActivity.this.getResources().getString(
								R.string.order_fail), Toast.LENGTH_SHORT)
						.show();
				break;
			case ALREADY_ORDER:
				Toast.makeText(
						getApplicationContext(),
						BookDetailActivity.this.getResources().getString(
								R.string.already_order), Toast.LENGTH_SHORT)
						.show();
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
			scanint.setClass(BookDetailActivity.this, HomeActivity.class);
			scanint.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(scanint, 0);
			break;
		}
		}
		return true;
	}

}
