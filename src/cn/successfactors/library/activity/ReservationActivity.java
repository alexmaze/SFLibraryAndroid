package cn.successfactors.library.activity;

import java.util.ArrayList;

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
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.successfactors.library.R;
import cn.successfactors.library.bean.OrderPage;
import cn.successfactors.library.bean.SLOrder;
import cn.successfactors.library.utils.Constants;
import cn.successfactors.library.utils.HTTPRequestHelper;
import cn.successfactors.library.utils.imagehelper.UrlImageViewHelper;

import com.google.gson.Gson;

public class ReservationActivity extends Activity implements OnScrollListener {

	private ListView lv;
	private ArrayList<SLOrder> data;
	private MyAdapter adapter;
	private ProgressDialog progressDialog;
	private final int pageNum = 30;
	private int page = 1;
	private TextView footer_text;
	private boolean isLoading = false;
	private boolean isLoadAll = false;
	private String SUB_PATH = "order/getorderlistpage/排队中/";
	private String CANCEL_SUB_PATH = "order/cancelorder";
	private RelativeLayout mRefreshView;
	public final int CANCEL_SUCCESS = 461192;
	public final int CANCEL_FAILED = 461191;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);//hidden title
		setContentView(R.layout.activity_reservation);

		lv = (ListView) findViewById(R.id.reserveLv);

		data = new ArrayList<SLOrder>();

		adapter = new MyAdapter(this);
		lv.setAdapter(adapter);

		LayoutInflater mInflater = (LayoutInflater) this
				.getApplicationContext().getSystemService(
						Context.LAYOUT_INFLATER_SERVICE);
		mRefreshView = (RelativeLayout) mInflater.inflate(
				R.layout.refresh_footer, null);
		footer_text = (TextView) mRefreshView.findViewById(R.id.footer_text);
		lv.addFooterView(mRefreshView);
		lv.setDividerHeight(0);
		lv.setOnScrollListener(this);

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int pos,
					long id) {
				showDialog(ReservationActivity.this, data.get(pos));
			}
		});

		SharedPreferences userInfo = getSharedPreferences(
				Constants.USERPREFERENCE, 0);
		performRequest(Constants.ROOT_PATH + SUB_PATH
				+ userInfo.getString(Constants.USERNAME, null) + "/" + pageNum
				+ "/" + page);
	}

	@Override
	public void onResume() {
		super.onResume();
		loadData();
	}

	private void loadData() {
		data = new ArrayList<SLOrder>();
		page = 0;
		SharedPreferences userInfo = getSharedPreferences(
				Constants.USERPREFERENCE, 0);
		performRequest(Constants.ROOT_PATH + SUB_PATH
				+ userInfo.getString(Constants.USERNAME, null) + "/" + pageNum
				+ "/" + page);
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
			page++;
			String bundleResult = msg.getData().getString("RESPONSE");
			Gson gson = new Gson();
			OrderPage orderPage = gson.fromJson(bundleResult, OrderPage.class);
			if (orderPage.getTheOrders().size() < pageNum) {
				isLoadAll = true;
			}
			data.addAll(orderPage.getTheOrders());
			adapter.notifyDataSetChanged();
		}
	};

	public final class ViewHolder {
		public TextView name;
		public TextView author;
		public ImageView image;
		public TextView orderUser;
		public TextView orderDate;
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

				convertView = mInflater.inflate(R.layout.reserve_book_item,
						null);
				holder.name = (TextView) convertView
						.findViewById(R.id.reserveBookName);
				holder.author = (TextView) convertView
						.findViewById(R.id.reserveBookAuthor);
				holder.image = (ImageView) convertView
						.findViewById(R.id.reserveBookImage);
				holder.orderDate = (TextView) convertView
						.findViewById(R.id.reserveDate);
				holder.orderUser = (TextView) convertView
						.findViewById(R.id.reserveRecUser);

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			SLOrder order = data.get(position);
			holder.name.setText(order.getTheBook().getBookName());
			holder.author.setText(order.getTheBook().getBookAuthor());
			UrlImageViewHelper.setUrlDrawable(holder.image, order.getTheBook()
					.getBookPicUrl());
			holder.orderUser.setText(getResources().getString(
					R.string.reserve_user)
					+ order.getTheUser().getUserName());
			holder.orderDate.setText(getResources().getString(
					R.string.reserve_time)
					+ order.getOrderDate());

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
					SharedPreferences userInfo = getSharedPreferences(
							Constants.USERPREFERENCE, 0);
					performRequest(Constants.ROOT_PATH + SUB_PATH
							+ userInfo.getString(Constants.USERNAME, null)
							+ "/" + pageNum + "/" + page);
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

	private void showDialog(Context context, final SLOrder slOrder) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("取消预定？");
		builder.setMessage(slOrder.getTheBook().getBookName());
		builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Thread runner = new Thread(new Runnable() {
					@Override
					public void run() {
						performCancelReservation(slOrder);
					}
				});
				runner.start();
			}
		});
		builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

			}
		});
		builder.show();
	}

	private void performCancelReservation(SLOrder order) {
		try {
			HttpClient httpclient = new DefaultHttpClient();

			SharedPreferences userInfo = getSharedPreferences(
					Constants.USERPREFERENCE, 0);
			String sessionKey = userInfo.getString(Constants.SESSIONKEY, null);

			JSONObject param = new JSONObject();

			param.put("orderId", order.getOrderId());
			param.put("sessionKey", sessionKey);

			String cancelURL = Constants.ROOT_PATH + CANCEL_SUB_PATH;
			HttpPut httpPut = new HttpPut(cancelURL);
			StringEntity se = new StringEntity(param.toString());
			httpPut.setEntity(se);
			HttpResponse response = httpclient.execute(httpPut);

			String retSrc = EntityUtils.toString(response.getEntity());

			JSONObject result = new JSONObject(retSrc);

			Message msg = new Message();

			if (result.getString("restStatus").equals("fail")) {
				if (result.getString("restErrorCode").equals("no_such_order")) {
					msg.what = CANCEL_FAILED;
				}
			} else {
				msg.what = CANCEL_SUCCESS;
			}

			cancelHandler.sendMessage(msg);
		} catch (Exception e) {
			// Log.d("Login failed  ", e.getMessage());
			e.printStackTrace();
		}
	}

	@SuppressLint("HandlerLeak")
	Handler cancelHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			progressDialog.dismiss();
			int result = msg.what;
			switch (result) {

			case CANCEL_FAILED:
				Toast.makeText(
						getApplicationContext(),
						ReservationActivity.this.getResources().getString(
								R.string.order_cancel_fail), Toast.LENGTH_SHORT)
						.show();
				break;
			case CANCEL_SUCCESS:
				loadData();
				Toast.makeText(
						getApplicationContext(),
						ReservationActivity.this.getResources().getString(
								R.string.order_cancel_success),
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
}
