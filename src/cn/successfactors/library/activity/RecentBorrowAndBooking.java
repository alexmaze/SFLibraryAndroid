package cn.successfactors.library.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ResponseHandler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import cn.successfactors.library.R;
import cn.successfactors.library.bean.BookBorrowOrderListInfo;
import cn.successfactors.library.bean.SLBorrow;
import cn.successfactors.library.bean.SLOrder;
import cn.successfactors.library.utils.Constants;
import cn.successfactors.library.utils.HTTPRequestHelper;

import com.google.gson.Gson;

public class RecentBorrowAndBooking extends Activity {

	private ProgressDialog progressDialog;
	private TextView titleName1;
	private TextView titleName2;

	private ListView lv1;

	private List<SLBorrow> data1;

	private MyAdapter1 adapter1;

	private ListView lv2;

	private List<SLOrder> data2;

	private MyAdapter2 adapter2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);//hidden title
		setContentView(R.layout.activity_recent_borrow_and_booking);
		titleName1 = (TextView) this.findViewById(R.id.book_borrow_listtitle1);
		titleName1.setText("当前借阅列表");
		titleName2 = (TextView) this.findViewById(R.id.book_borrow_listtitle2);
		titleName2.setText("当前预订队列");

		lv1 = (ListView) findViewById(R.id.recent_borrow_lv);
		// add this before set adapter

		data1 = new ArrayList<SLBorrow>();

		adapter1 = new MyAdapter1(this);
		lv1.setAdapter(adapter1);
		lv1.setDividerHeight(0);

		lv2 = (ListView) findViewById(R.id.recent_booking_lv);
		// add this before set adapter

		data2 = new ArrayList<SLOrder>();

		adapter2 = new MyAdapter2(this);
		lv2.setAdapter(adapter2);
		lv2.setDividerHeight(0);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String myParam = Constants.ROOT_PATH
					+ "book/getbookborroworderlistbyisbn/"
					+ extras.getString("url");
			performRequest(myParam);

		}

	}

	public class MyAdapter1 extends BaseAdapter {

		private LayoutInflater mInflater;

		public MyAdapter1(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return data1.size() + 1;
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

			ViewHolder1 holder = null;
			if (convertView == null) {

				holder = new ViewHolder1();

				convertView = mInflater.inflate(R.layout.current_book_borrow,
						null);
				holder.borrowPerson = (TextView) convertView
						.findViewById(R.id.borrow_person);
				holder.borrowDate = (TextView) convertView
						.findViewById(R.id.borrow_date);
				holder.returnDate = (TextView) convertView
						.findViewById(R.id.return_date);
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder1) convertView.getTag();
			}
			final int position = tmpPosition;
			if (position == 0) {
				holder.borrowPerson.setText("借阅人");
				holder.borrowDate.setText("借书时间");
				holder.returnDate.setText("应还时间");
			} else {
				SLBorrow borrow = data1.get(position - 1);
				holder.borrowPerson.setText(borrow.getTheUser().getUserName());
				holder.borrowDate.setText(borrow.getBorrowDate());
				holder.returnDate.setText(borrow.getShouldReturnDate());
			}
			return convertView;
		}
	}

	public class MyAdapter2 extends BaseAdapter {

		private LayoutInflater mInflater;

		public MyAdapter2(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return data2.size() + 1;
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

			ViewHolder2 holder = null;
			if (convertView == null) {

				holder = new ViewHolder2();

				convertView = mInflater.inflate(R.layout.current_book_booking,
						null);
				holder.bookingPerson = (TextView) convertView
						.findViewById(R.id.booking_person);
				holder.bookingDate = (TextView) convertView
						.findViewById(R.id.booking_date);

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder2) convertView.getTag();
			}
			final int position = tmpPosition;
			if (position == 0) {
				holder.bookingPerson.setText("预订人");
				holder.bookingDate.setText("预订时间");
			} else {
				SLOrder order = data2.get(position - 1);
				holder.bookingPerson.setText(order.getTheUser().getUserName());
				holder.bookingDate.setText(order.getOrderDate().toString());
			}
			return convertView;
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
			BookBorrowOrderListInfo info = gson.fromJson(bundleResult,
					BookBorrowOrderListInfo.class);

			data1.addAll(info.getTheBorrows());
			data2.addAll(info.getTheOrders());
			adapter1.notifyDataSetChanged();
			adapter2.notifyDataSetChanged();
		}
	};

	public final class ViewHolder1 {
		public TextView borrowPerson;
		public TextView borrowDate;
		public TextView returnDate;
	}

	public final class ViewHolder2 {
		public TextView bookingPerson;
		public TextView bookingDate;
	}

}
