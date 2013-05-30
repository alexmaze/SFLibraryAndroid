package cn.successfactors.library.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.client.ResponseHandler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.successfactors.library.R;
import cn.successfactors.library.bean.BookPage;
import cn.successfactors.library.bean.SLBook;
import cn.successfactors.library.utils.Constants;
import cn.successfactors.library.utils.HTTPRequestHelper;
import cn.successfactors.library.utils.imagehelper.UrlImageViewHelper;

import com.google.gson.Gson;

@SuppressLint("HandlerLeak")
public class BrowserActivity extends Activity implements OnScrollListener {

	private ListView lv;

	private List<SLBook> data;

	private Timer searchTimer;
	private MyAdapter adapter;
	private Handler searchHandler = new Handler();
	private long keyPressInsant = -1;

	private RelativeLayout mRefreshView;

	private ProgressDialog progressDialog;

	public static BrowserActivity singleton;

	private final int pageNum = 30;
	TextView queryField;
	String pageUrl;
	boolean isSearch;
	private int page = 1;
	private TextView footer_text;
	private boolean isLoading = false;
	private boolean isLoadAll = false;

	private String SUB_PATH = "book/getallbooklistpage/";
	private String SEARCH_PATH = "book/searchbooklistpage/";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		singleton = this;

		requestWindowFeature(Window.FEATURE_NO_TITLE);// hidden title
		setContentView(R.layout.activity_browser);

		lv = (ListView) findViewById(R.id.browser_lv);
		// add this before set adapter
		LayoutInflater mInflater = (LayoutInflater) this
				.getApplicationContext().getSystemService(
						Context.LAYOUT_INFLATER_SERVICE);
		mRefreshView = (RelativeLayout) mInflater.inflate(
				R.layout.refresh_footer, null);
		footer_text = (TextView) mRefreshView.findViewById(R.id.footer_text);
		lv.addFooterView(mRefreshView);
		lv.setOnScrollListener(this);

		data = new ArrayList<SLBook>();

		adapter = new MyAdapter(this);
		lv.setAdapter(adapter);
		lv.setDividerHeight(0);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int pos,
					long id) {

			}
		});

		pageUrl = Constants.ROOT_PATH + SUB_PATH + pageNum + "/" + page;
		performRequest(pageUrl);

		initializeSearchTextEntryField();

		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(queryField.getWindowToken(), 0);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

	}

	private void initializeSearchTextEntryField() {
		queryField = (TextView) findViewById(R.id.search_field);
		queryField.setTag(queryField.getKeyListener());
		queryField.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				keyPressInsant = System.currentTimeMillis();
				if (searchTimer == null) {
					searchTimer = new Timer();
					searchTimer.scheduleAtFixedRate(searchTimerTask, 0, 300);
				}
			}
		});
		queryField
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (actionId == EditorInfo.IME_ACTION_DONE) {
							InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
							return true;
						}
						return false;
					}
				});
	}

	private TimerTask searchTimerTask = new TimerTask() {
		@Override
		public void run() {
			// search when user pauses for 0.8 second
			if (keyPressInsant != -1
					&& System.currentTimeMillis() - keyPressInsant > 800) {
				searchHandler.post(searchTask);
				keyPressInsant = -1;
			}
		}
	};

	private Runnable searchTask = new Runnable() {
		@Override
		public void run() {
			page = 1;
			isSearch = true;
			data = new ArrayList<SLBook>();
			data.clear();
			doSearch();
		}
	};

	private void doSearch() {
		isLoadAll = false;
		String strSearch = queryField.getText().toString();
		if (strSearch.equals("")) {
			performRequest(Constants.ROOT_PATH + SUB_PATH + pageNum + "/"
					+ page);
		} else {
			performRequest(Constants.ROOT_PATH + SEARCH_PATH + "书名/"
					+ strSearch + "/" + pageNum + "/" + page);
		}
	}

	private void performRequest(final String url) {
		final ResponseHandler<String> responseHandler = HTTPRequestHelper
				.getResponseHandlerInstance(this.handler);
		if (page < 2) {
			this.progressDialog = ProgressDialog.show(this, "", getResources()
					.getString(R.string.loading));
		}
		// do the HTTP dance in a separate thread (the responseHandler will fire
		// when complete)
		new Thread() {

			@Override
			public void run() {
				isLoading = true;
				HTTPRequestHelper helper = new HTTPRequestHelper(
						responseHandler);
				helper.performGet(url, null, null, null);
			}
		}.start();
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(final Message msg) {
			isLoading = false;

			if (page < 2) {
				progressDialog.dismiss();
			}
			page++;

			String bundleResult = msg.getData().getString("RESPONSE");
			Gson gson = new Gson();
			BookPage recBookPage = gson.fromJson(bundleResult, BookPage.class);
			if (recBookPage.getTheBooks().size() < pageNum) {
				isLoadAll = true;
			}
			data.addAll(recBookPage.getTheBooks());
			adapter.notifyDataSetChanged();
		}
	};

	public final class ViewHolder {
		public TextView nameA;

		public ImageView imageA;

		public TextView nameB;

		public ImageView imageB;

		public TextView nameC;

		public ImageView imageC;
	}

	public class MyAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public MyAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			if (data.size() % 3 == 0) {
				return data.size() / 3;
			} else {
				return data.size() / 3 + 1;
			}
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(final int tmpPosition, View convertView,
				ViewGroup parent) {

			ViewHolder holder = null;
			if (convertView == null) {

				holder = new ViewHolder();

				convertView = mInflater.inflate(R.layout.browser_book_item,
						null);
				holder.nameA = (TextView) convertView
						.findViewById(R.id.browserBookNameA);
				holder.imageA = (ImageView) convertView
						.findViewById(R.id.browserBookImageA);
				holder.nameB = (TextView) convertView
						.findViewById(R.id.browserBookNameB);
				holder.imageB = (ImageView) convertView
						.findViewById(R.id.browserBookImageB);
				holder.nameC = (TextView) convertView
						.findViewById(R.id.browserBookNameC);
				holder.imageC = (ImageView) convertView
						.findViewById(R.id.browserBookImageC);

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			SLBook book;
			final int position = tmpPosition * 3;

			if (data.size() <= (position)) {
				holder.nameA.setVisibility(View.INVISIBLE);
				holder.imageA.setVisibility(View.INVISIBLE);
			} else {
				holder.nameA.setVisibility(View.VISIBLE);
				holder.imageA.setVisibility(View.VISIBLE);
				book = data.get(position);
				holder.nameA.setText(book.getBookName());
				UrlImageViewHelper.setUrlDrawable(holder.imageA,
						book.getBookPicUrl());

				holder.imageA.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						String detailUrl = data.get(position).getBookISBN();
						Intent intent = new Intent(BrowserActivity.this,
								BookDetailActivity.class);
						intent.putExtra("url", detailUrl);
						startActivity(intent);
					}
				});
			}

			if (data.size() <= (position + 1)) {
				holder.nameB.setVisibility(View.INVISIBLE);
				holder.imageB.setVisibility(View.INVISIBLE);
			} else {
				holder.nameB.setVisibility(View.VISIBLE);
				holder.imageB.setVisibility(View.VISIBLE);
				book = data.get(position + 1);
				holder.nameB.setText(book.getBookName());
				UrlImageViewHelper.setUrlDrawable(holder.imageB,
						book.getBookPicUrl());

				holder.imageB.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {

						String detailUrl = data.get(position + 1).getBookISBN();
						Intent intent = new Intent(BrowserActivity.this,
								BookDetailActivity.class);
						intent.putExtra("url", detailUrl);
						startActivity(intent);
					}
				});
			}

			if (data.size() <= (position + 2)) {
				holder.nameC.setVisibility(View.INVISIBLE);
				holder.imageC.setVisibility(View.INVISIBLE);
			} else {
				holder.nameC.setVisibility(View.VISIBLE);
				holder.imageC.setVisibility(View.VISIBLE);
				book = data.get(position + 2);
				holder.nameC.setText(book.getBookName());
				UrlImageViewHelper.setUrlDrawable(holder.imageC,
						book.getBookPicUrl());

				holder.imageC.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						String detailUrl = data.get(position + 2).getBookISBN();
						Intent intent = new Intent(BrowserActivity.this,
								BookDetailActivity.class);
						intent.putExtra("url", detailUrl);
						startActivity(intent);
					}
				});
			}

			return convertView;
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if ((firstVisibleItem + visibleItemCount == totalItemCount)
				&& totalItemCount != 0) {
			if (!isLoading) {
				if (!isLoadAll) {
					if (isSearch) {
						doSearch();
					} else {
						performRequest(Constants.ROOT_PATH + SUB_PATH + pageNum
								+ "/" + page);
					}
				} else {
					footer_text.setText(this.getResources().getString(
							R.string.loading_all));
				}
			}
		}

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}
}
