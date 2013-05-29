package cn.successfactors.library.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.successfactors.library.R;
import cn.successfactors.library.bean.SLRecommendedBook;
import cn.successfactors.library.utils.CipherUtil;
import cn.successfactors.library.utils.Constants;
import cn.successfactors.library.utils.HTTPRequestHelper;

import com.google.gson.Gson;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

public class RecommendDetailActivity extends Activity {

    public final String SESSIONKEY = "sessionKey";
    public final String SLRecommendedBook = "SLRecommendedBook";

    public final String RECOMMEND_SUB_PATH = "recommend/recommendbook";
    public final String GET_RECOMMEND_SUB_PATH = "recommend/getrecommendedbook/";
    public final int REC_SUCCESS = 461192;
    public final int REC_FAILED = 461191;
	
	private ProgressDialog progressDialog;
	private TextView detailName;
	private TextView detailRecUser;
	private ImageView detailImg;
	private TextView detailAuthor;
	private TextView detailContent;
	private List<ImageView> hotImgs;
	private Button recommendBtn;
	private SLRecommendedBook recBook;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);//hidden title
        setContentView(R.layout.activity_recommend_detail);

        detailName = (TextView)this.findViewById(R.id.recdetail_name);
        detailRecUser = (TextView)this.findViewById(R.id.recdetail_recuser);
        detailImg = (ImageView)this.findViewById(R.id.recdetail_image);
        detailAuthor = (TextView)this.findViewById(R.id.recdetail_author);
        detailContent = (TextView)this.findViewById(R.id.recdetail_detail);
        recommendBtn = (Button)this.findViewById(R.id.recdetailBtn);
        
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
        
        hotImgs = new ArrayList<ImageView>();
        hotImgs.add((ImageView)this.findViewById(R.id.recommendDetailBookHot1));
        hotImgs.add((ImageView)this.findViewById(R.id.recommendDetailBookHot2));
        hotImgs.add((ImageView)this.findViewById(R.id.recommendDetailBookHot3));
        hotImgs.add((ImageView)this.findViewById(R.id.recommendDetailBookHot4));
        hotImgs.add((ImageView)this.findViewById(R.id.recommendDetailBookHot5));
        
        Bundle extras = getIntent().getExtras(); 
        if (extras != null){
        	String myParam = Constants.ROOT_PATH + GET_RECOMMEND_SUB_PATH + extras.getString("url"); 
        	performRequest(myParam);

        }
    }

    private void performRequest(final String url) {
        final ResponseHandler<String> responseHandler = HTTPRequestHelper
                .getResponseHandlerInstance(this.handler);

        this.progressDialog = ProgressDialog.show(this, "", getResources().getString(R.string.loading));

        // do the HTTP dance in a separate thread (the responseHandler will fire
        // when complete)
        new Thread() {

            @Override
            public void run() {
                HTTPRequestHelper helper = new HTTPRequestHelper(responseHandler);
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
            recBook = gson.fromJson(bundleResult, SLRecommendedBook.class);

            detailName.setText(recBook.getBookName());
            detailAuthor.setText(recBook.getBookAuthor());
            detailContent.setText(recBook.getBookIntro());
            detailRecUser.setText(getResources().getString(R.string.recommend_user) + recBook.getRecUserName());
            UrlImageViewHelper.setUrlDrawable(detailImg, recBook.getBookPicUrl());
            
            int recRate = recBook.getRecRate();
            for (int i = 0; i < hotImgs.size(); i ++){
            	ImageView hotImg = hotImgs.get(i);
            	if (i < recRate){
            		hotImg.setVisibility(View.VISIBLE);
            	}else{
            		hotImg.setVisibility(View.GONE);
            	}
            }
        }
    };
    
    private void performRecommend() {
    	try {
    		SharedPreferences userInfo = getSharedPreferences(Constants.USERPREFERENCE, 0);  
    		String sessionKey = userInfo.getString(Constants.SESSIONKEY, null);
    		recBook.setSessionKey(sessionKey);
    		recBook.setRecUserEmail(userInfo.getString(Constants.USERNAME, null));
            
    		HttpClient httpclient = new DefaultHttpClient();
            Gson gson = new Gson();
            
            HttpPut httpput = new HttpPut(Constants.ROOT_PATH + RECOMMEND_SUB_PATH);
            StringEntity se = new StringEntity(gson.toJson(recBook));   
            httpput.setEntity(se);
            HttpResponse response = httpclient.execute(httpput);

            String retSrc = EntityUtils.toString(response.getEntity());  
             
            JSONObject result = new JSONObject( retSrc);  
            
            Message msg= new Message();
            
            if(result.getString("restStatus").equals("fail")){
            	if(result.getString("restErrorCode").equals("already_recommended")){
            		msg.what = REC_FAILED;
            	}
            }else{
            	msg.what = REC_SUCCESS;
            }
            
            recommendHandler.sendMessage(msg);
        } catch (Exception e) {
            Log.d("Login failed  ", e.getMessage());
            e.printStackTrace();
        }
	}
	
    Handler recommendHandler = new Handler() {

        @Override
        public void handleMessage(Message msg){
            progressDialog.dismiss();
            int result = msg.what;
            switch(result) {
              
            	case REC_FAILED:
            		Toast.makeText(getApplicationContext(), RecommendDetailActivity.this.getResources().getString(R.string.recommend_already), Toast.LENGTH_SHORT).show();
                    break;
                case REC_SUCCESS:
                	Toast.makeText(getApplicationContext(), RecommendDetailActivity.this.getResources().getString(R.string.recommend_success), Toast.LENGTH_SHORT).show();
                    break;    
            }
        }
    };
    

}
