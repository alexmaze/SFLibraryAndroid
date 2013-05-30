package cn.successfactors.library.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ResponseHandler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import cn.successfactors.library.bean.BorrowPage;
import cn.successfactors.library.bean.SLBook;
import cn.successfactors.library.bean.SLBorrow;
import cn.successfactors.library.utils.Constants;
import cn.successfactors.library.utils.HTTPRequestHelper;
import cn.successfactors.library.utils.imagehelper.UrlImageViewHelper;

import com.google.gson.Gson;

public class BorrowActivity extends Activity implements OnScrollListener {

	private ListView lv;
	private List<SLBorrow> data;
	private MyAdapter adapter;
	private RelativeLayout mRefreshView;
	private ProgressDialog progressDialog;

	private final int pageSize = 20;
	private int page = 1;
	private int totalPage = 1;
	private TextView footer_text;
	private boolean isLoading = false;

	private static final String SUB_PATH = "borrow/getborrowlistpage/当前记录/";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// hidden title
		setContentView(R.layout.activity_borrow);

		lv = (ListView) findViewById(R.id.borrowLv);

		// add this before set adapter
		LayoutInflater mInflater = (LayoutInflater) this
				.getApplicationContext().getSystemService(
						Context.LAYOUT_INFLATER_SERVICE);
		mRefreshView = (RelativeLayout) mInflater.inflate(
				R.layout.refresh_footer, null);
		footer_text = (TextView) mRefreshView.findViewById(R.id.footer_text);
		lv.addFooterView(mRefreshView);
		lv.setOnScrollListener(this);

		data = new ArrayList<SLBorrow>();

		adapter = new MyAdapter(this);
		lv.setAdapter(adapter);
		lv.setDividerHeight(0);

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int pos,
					long id) {
				String detailUrl = data.get(pos).getBookISBN();
				// TODO, borrow detail page
				Intent intent = new Intent(BorrowActivity.this,
						BookDetailActivity.class);
				intent.putExtra("url", detailUrl);
				startActivity(intent);
			}
		});
		SharedPreferences userInfo = getSharedPreferences(
				Constants.USERPREFERENCE, 0);
		String username = userInfo.getString(Constants.USERNAME, "");
		String url = Constants.ROOT_PATH + SUB_PATH + username + "/" + pageSize
				+ "/1";
		// String url1 =
		// "http://192.168.25.222:8089/SFLibraryService/private/borrow/getborrowlistpage/当前记录/"
		// + username + "/20/1";
		performRequest(url);
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

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {

		@Override
		public void handleMessage(final Message msg) {
			isLoading = false;
			if (page < 2) {
				progressDialog.dismiss();
			}
			String bundleResult = msg.getData().getString("RESPONSE");
			Gson gson = new Gson();
			BorrowPage borrowPage = gson.fromJson(bundleResult,
					BorrowPage.class);
			totalPage = borrowPage.getTotalPageNum();
			data.addAll(borrowPage.getTheBorrows());
			adapter.notifyDataSetChanged();
		}
	};

	public final class ViewHolder {
		public TextView name;
		public TextView author;
		public ImageView image;
		public TextView borrowDate;
		public TextView dueDate;
	}

	public class MyAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public MyAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return data.size();
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
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;
			if (convertView == null) {

				holder = new ViewHolder();

				convertView = mInflater
						.inflate(R.layout.borrow_book_item, null);
				holder.name = (TextView) convertView
						.findViewById(R.id.borrowBookName);
				holder.author = (TextView) convertView
						.findViewById(R.id.borrowBookAuthor);
				holder.image = (ImageView) convertView
						.findViewById(R.id.borrowBookImage);
				holder.borrowDate = (TextView) convertView
						.findViewById(R.id.borrowStartDate);
				holder.dueDate = (TextView) convertView
						.findViewById(R.id.borrowDueDate);

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			SLBorrow borrow = data.get(position);
			SLBook book = borrow.getTheBook();
			holder.name.setText(book.getBookName());
			holder.author.setText(book.getBookAuthor());
			UrlImageViewHelper.setUrlDrawable(holder.image,
					book.getBookPicUrl());
			holder.borrowDate.setText(getResources().getString(
					R.string.borrow_date)
					+ " " + borrow.getBorrowDate().substring(0, 10));
			holder.dueDate.setText(getResources().getString(R.string.due_date)
					+ " " + borrow.getShouldReturnDate().substring(0, 10));

			return convertView;
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if ((firstVisibleItem + visibleItemCount == totalItemCount)
				&& totalItemCount != 0) {
			if (!isLoading) {
				if (page < totalPage) {
					SharedPreferences userInfo = getSharedPreferences(
							Constants.USERPREFERENCE, 0);
					String username = userInfo
							.getString(Constants.USERNAME, "");
					String url = Constants.ROOT_PATH + SUB_PATH + username
							+ "/" + pageSize + "/" + ++page;
					// performRequest("http://192.168.25.222:8089/SFLibraryService/private/borrow/getborrowlistpage/当前记录/"
					// + username + "/20/" + ++page);
					performRequest(url);
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

	@Override
	public void onResume() {
		super.onResume();
		loadData();
	}

	private void loadData() {
		data = new ArrayList<SLBorrow>();
		page = 1;
		SharedPreferences userInfo = getSharedPreferences(
				Constants.USERPREFERENCE, 0);
		performRequest(Constants.ROOT_PATH + SUB_PATH
				+ userInfo.getString(Constants.USERNAME, null) + "/" + pageSize
				+ "/1");
	}
}
