package cn.successfactors.library.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import cn.successfactors.library.R;

@SuppressWarnings("deprecation")
public class HomeActivity extends Activity {
	Context context = null;
	LocalActivityManager manager = null;
	ViewPager pager = null;
	TabHost tabHost = null;
	TextView t1, t2, t3, t4;
	private int offset = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int bmpW;// 动画图片宽度
	private ImageView cursor;// 动画图片

	private boolean isSearchBarShow = false;

	public static HomeActivity singleton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		singleton = this;
		setContentView(R.layout.activity_home);

		context = HomeActivity.this;
		manager = new LocalActivityManager(this, true);
		manager.dispatchCreate(savedInstanceState);
		InitImageView();
		initTextView();
		initPagerViewer();

	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		View qrcodeMenuItem = findViewById(R.id.menu_qrcode);
		qrcodeMenuItem.setVisibility(8);
	}

	/** * 初始化标题 */
	private void initTextView() {
		t1 = (TextView) findViewById(R.id.text1);
		t2 = (TextView) findViewById(R.id.text2);
		t3 = (TextView) findViewById(R.id.text3);
		t4 = (TextView) findViewById(R.id.text4);
		t1.setOnClickListener(new MyOnClickListener(0));
		t2.setOnClickListener(new MyOnClickListener(1));
		t3.setOnClickListener(new MyOnClickListener(2));
		t4.setOnClickListener(new MyOnClickListener(3));
	}

	/** * 初始化PageViewer */
	private void initPagerViewer() {
		pager = (ViewPager) findViewById(R.id.viewpage);
		final ArrayList<View> list = new ArrayList<View>();

		Intent intent = new Intent(context, BrowserActivity.class);
		list.add(getView("A", intent));

		Intent intent2 = new Intent(context, BorrowActivity.class);
		list.add(getView("B", intent2));

		Intent intent3 = new Intent(context, ReservationActivity.class);
		list.add(getView("C", intent3));

		Intent intent4 = new Intent(context, RecommendActivity.class);
		list.add(getView("D", intent4));

		pager.setAdapter(new MyPagerAdapter(list));
		pager.setCurrentItem(0);
		pager.setOnPageChangeListener(new MyOnPageChangeListener());

	}

	/** * 初始化动画 */
	private void InitImageView() {
		cursor = (ImageView) findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.roller)
				.getWidth();// 获取图片宽度
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// 获取分辨率宽度
		offset = (screenW / 4 - bmpW) / 2;// 计算偏移量
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		cursor.setImageMatrix(matrix);// 设置动画初始位置
	}

	/** * 通过activity获取视图 * @param id * @param intent * @return */

	private View getView(String id, Intent intent) {
		return manager.startActivity(id, intent).getDecorView();
	}

	/** * Pager适配器 */

	public class MyPagerAdapter extends PagerAdapter {
		List<View> list = new ArrayList<View>();

		public MyPagerAdapter(ArrayList<View> list) {
			this.list = list;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			ViewPager pViewPager = ((ViewPager) container);
			pViewPager.removeView(list.get(position));
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			ViewPager pViewPager = ((ViewPager) arg0);
			pViewPager.addView(list.get(arg1));

			return list.get(arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}
	}

	/** * 页卡切换监听 */
	public class MyOnPageChangeListener implements OnPageChangeListener {
		int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
		int two = one * 2;// 页卡1 -> 页卡3 偏移量
		int three = one * 3;// 页卡1 -> 页卡4 偏移量

		@Override
		public void onPageSelected(int arg0) {
			Animation animation = null;
			View searchMenuItem = findViewById(R.id.menu_search);
			View qrcodeMenuItem = findViewById(R.id.menu_qrcode);

			switch (arg0) {
			case 0:

				searchMenuItem.setVisibility(0);
				qrcodeMenuItem.setVisibility(8);

				if (currIndex == 1) {
					animation = new TranslateAnimation(one, 0, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, 0, 0, 0);
				} else if (currIndex == 3) {
					animation = new TranslateAnimation(three, 0, 0, 0);
				}
				break;
			case 1:

				searchMenuItem.setVisibility(8);
				qrcodeMenuItem.setVisibility(8);

				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, one, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, one, 0, 0);
				} else if (currIndex == 3) {
					animation = new TranslateAnimation(three, one, 0, 0);
				}
				break;
			case 2:

				searchMenuItem.setVisibility(8);
				qrcodeMenuItem.setVisibility(8);

				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, two, 0, 0);
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, two, 0, 0);
				} else if (currIndex == 3) {
					animation = new TranslateAnimation(three, two, 0, 0);
				}
				break;
			case 3:

				searchMenuItem.setVisibility(8);
				qrcodeMenuItem.setVisibility(0);

				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, three, 0, 0);
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, three, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, three, 0, 0);
				}
				break;
			}
			currIndex = arg0;
			animation.setFillAfter(true);// True:图片停在动画结束位置
			animation.setDuration(300);
			cursor.startAnimation(animation);
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}
	}

	/** * 头标点击监听 */
	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			pager.setCurrentItem(index);
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.globle_menu, menu);
		inflater.inflate(R.menu.actionbar_search, menu);
		inflater.inflate(R.menu.actionbar_qrcode, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int item_id = item.getItemId();// 得到当前选中MenuItem的ID
		switch (item_id) {
		case R.id.menu_exit: {
			System.exit(0);
			break;
		}
		case R.id.menu_search: {
			if (isSearchBarShow) {
				isSearchBarShow = false;
				View searchBar = BrowserActivity.singleton
						.findViewById(R.id.top_layout);
				searchBar.setVisibility(8);

			} else {
				isSearchBarShow = true;
				View searchBar = BrowserActivity.singleton
						.findViewById(R.id.top_layout);
				searchBar.setVisibility(0);
			}
			break;
		}
		case R.id.menu_qrcode: {
			Intent scanint = new Intent();
			scanint.setClass(HomeActivity.this, CaptureActivity.class);
			startActivityForResult(scanint, 0);
			break;
		}
		}
		return true;
	}
}
