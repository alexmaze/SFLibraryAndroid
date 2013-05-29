package cn.successfactors.library.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ResponseHandler;

import com.google.gson.Gson;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import cn.successfactors.library.R;
import cn.successfactors.library.bean.RecommendedBookPage;
import cn.successfactors.library.bean.SLRecommendedBook;
import cn.successfactors.library.utils.Constants;
import cn.successfactors.library.utils.HTTPRequestHelper;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;


public class RecommendActivity extends Activity implements OnScrollListener {
	
	private ListView lv;
	private List<SLRecommendedBook> data;
	private MyAdapter adapter;
	private ProgressDialog progressDialog;
	private final int pageNum = 30;
    private int page = 1;
    private TextView footer_text;
    private boolean isLoading =false;
    private boolean isLoadAll = false;
    private String SUB_PATH = "recommend/getallrecbooklistpage/";
    private RelativeLayout mRefreshView; 
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//hidden title
        setContentView(R.layout.activity_recommand);
        
        lv = (ListView)findViewById(R.id.recommandLv);
        
        data = new ArrayList<SLRecommendedBook>();
        
        adapter = new MyAdapter(this);
        lv.setAdapter(adapter);
        lv.setDividerHeight(0);
        
        LayoutInflater mInflater = (LayoutInflater) this.getApplicationContext().getSystemService(   
                Context.LAYOUT_INFLATER_SERVICE);  
        mRefreshView = (RelativeLayout) mInflater.inflate(R.layout.refresh_footer, null);  
        footer_text = (TextView)mRefreshView.findViewById(R.id.footer_text);
        lv.addFooterView(mRefreshView);
        lv.setOnScrollListener(this);
        
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
            	String detailUrl = data.get(pos).getBookISBN();
            	Intent intent = new Intent(RecommendActivity.this, RecommendDetailActivity.class);
        		intent.putExtra("url", detailUrl);
        		startActivity(intent);    
            }
        });
        
        performRequest(Constants.ROOT_PATH + SUB_PATH + pageNum + "/" + page);
    }
	
    private void performRequest(final String url) {
        final ResponseHandler<String> responseHandler = HTTPRequestHelper.getResponseHandlerInstance(this.handler);

        if(page<2) {
            this.progressDialog = ProgressDialog.show(this, "",
                getResources().getString(R.string.loading));
        }

        // do the HTTP dance in a separate thread (the responseHandler will fire when complete)
        new Thread() {

            @Override
            public void run() {
            	isLoading = true;
                HTTPRequestHelper helper = new HTTPRequestHelper(responseHandler);
                helper.performGet(url, null, null, null);
            }
        }.start();
	}
    
    Handler handler = new Handler() {

        @Override
        public void handleMessage(final Message msg) {
        	isLoading = false;
        	if(page<2) {
                progressDialog.dismiss();
            }
        	page ++;
            String bundleResult = msg.getData().getString("RESPONSE");
            Gson gson = new Gson();
            RecommendedBookPage recBookPage = gson.fromJson(bundleResult, RecommendedBookPage.class);
            if(recBookPage.getTheBooks().size() < pageNum) {
                isLoadAll =true;
            }
            data.addAll(recBookPage.getTheBooks());
            adapter.notifyDataSetChanged(); 
        }
    };

	public final class ViewHolder{
        public TextView name;
        public TextView author;
        public ImageView image;
        public TextView recUser;
        public List<ImageView> hotImgs;
    }
	
    public class MyAdapter extends BaseAdapter{
        
        private LayoutInflater mInflater;
        
        public MyAdapter(Context context){
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
                 
                holder=new ViewHolder(); 
                 
                
                convertView = mInflater.inflate(R.layout.recommend_book_item, null);
                holder.name = (TextView)convertView.findViewById(R.id.recommendBookName);
                holder.author = (TextView)convertView.findViewById(R.id.recommendBookAuthor);
                holder.image = (ImageView)convertView.findViewById(R.id.recommendBookImage);
                
                holder.hotImgs = new ArrayList<ImageView>();
                holder.hotImgs.add((ImageView)convertView.findViewById(R.id.recommendBookHot1));
                holder.hotImgs.add((ImageView)convertView.findViewById(R.id.recommendBookHot2));
                holder.hotImgs.add((ImageView)convertView.findViewById(R.id.recommendBookHot3));
                holder.hotImgs.add((ImageView)convertView.findViewById(R.id.recommendBookHot4));
                holder.hotImgs.add((ImageView)convertView.findViewById(R.id.recommendBookHot5));
                
                holder.recUser = (TextView)convertView.findViewById(R.id.recommendRecUser);
                
                convertView.setTag(holder);
                 
            }else {
                holder = (ViewHolder)convertView.getTag();
            }
             
            SLRecommendedBook book = data.get(position);
            holder.name.setText(book.getBookName());
            holder.author.setText(book.getBookAuthor());
            UrlImageViewHelper.setUrlDrawable(holder.image, book.getBookPicUrl());
            holder.recUser.setText(getResources().getString(R.string.recommend_user) + book.getRecUserName());
            
            int recRate = book.getRecRate();
            for (int i = 0; i < holder.hotImgs.size(); i ++){
            	ImageView hotImg = holder.hotImgs.get(i);
            	if (i < recRate){
            		hotImg.setVisibility(View.VISIBLE);
            	}else{
            		hotImg.setVisibility(View.GONE);
            	}
            }
            
            return convertView;
        }
    }
    
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
            int totalItemCount) {
        if((firstVisibleItem+visibleItemCount ==totalItemCount) && totalItemCount!=0) {
            if(!isLoading) {
                if(!isLoadAll) {
                    performRequest(Constants.ROOT_PATH + SUB_PATH + pageNum + "/" + page);
                } else {
                    footer_text.setText(this.getResources().getString(R.string.loading_all));
                }
            }
        }
        
    }

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		
	}
}
