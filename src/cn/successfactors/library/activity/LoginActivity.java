
package cn.successfactors.library.activity;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;
import cn.successfactors.library.R;
import cn.successfactors.library.utils.CipherUtil;
import cn.successfactors.library.utils.Constants;

public class LoginActivity extends Activity {
    public final String EMAIL = "email";

    public final String PASSWORD = "password";

    public final String SUB_PATH = "user/login";
    public final int LOGIN_SUCCESS = 461192;
    public final int LOGIN_FAILED = 461191;
    private ProgressDialog progressDialog;
    private String userName;
    private String password;
    SharedPreferences userInfo;
    CheckBox cbPassword;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);// hidden title
        setContentView(R.layout.activity_login);
        userInfo = getSharedPreferences(Constants.USERPREFERENCE, 0);  
        userName = userInfo.getString(Constants.USERNAME, ""); 
        
        if(!userName.equals("")) {
            ((TextView)findViewById(R.id.LoginID)).setText(userName);  
            ((TextView)findViewById(R.id.PassWord)).requestFocus(); 
        }
        cbPassword = (CheckBox)this.findViewById(R.id.checkbox);
        password = userInfo.getString(Constants.PASSWORD, "");      
        if(!password.equals("")) {
            ((TextView)findViewById(R.id.PassWord)).setText(password); 
            cbPassword.setChecked(true);
        }
        
        Button btnLogin = (Button)this.findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(loginListener);
    }

    OnClickListener loginListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(((EditText)findViewById(R.id.LoginID)).getWindowToken(), 0);
            
            // username and password both can not be empty
            userName = ((TextView)findViewById(R.id.LoginID)).getText().toString();
            if(userName ==null || userName.equals("")) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.username_empty), Toast.LENGTH_SHORT).show();
                return;
            }
           
            password = ((TextView)findViewById(R.id.PassWord)).getText().toString();
            if(password ==null || password.equals("")) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.password_empty), Toast.LENGTH_SHORT).show();
                return;
            }
            progressDialog =  ProgressDialog.show(LoginActivity.this, getResources().getString(R.string.system_alert), getResources().getString(R.string.login_alert), true, false);
            progressDialog.show();
            // please do not do any network handle in UI Thread
            Thread runner = new Thread(new Runnable() {
                @Override
                public void run() {
                    doLogin();
                }
            });
            runner.start();
        }
    };

    public void doLogin() {
        Message msg= new Message();
        try {
            
            HttpClient httpclient = new DefaultHttpClient();
            JSONObject param = new JSONObject();
            if(!userName.contains("@")) {
                userName = userName+ "@successfactors.com";
            }
            param.put(EMAIL, userName);
            param.put(PASSWORD, CipherUtil.generatePassword(password));
            
            HttpPost httppost = new HttpPost(Constants.ROOT_PATH + SUB_PATH);
            StringEntity se = new StringEntity(param.toString());   
            httppost.setEntity(se);  
            HttpResponse response = httpclient.execute(httppost);

            String retSrc = EntityUtils.toString(response.getEntity());  
             
            JSONObject result = new JSONObject( retSrc);  
           

            if(result.optString("sessionKey") ==null || result.isNull("sessionKey")) {
                msg.what = LOGIN_FAILED;               
            } else {
                msg.what = LOGIN_SUCCESS; 
                
                userInfo.edit().putString(Constants.SESSIONKEY, result.getString("sessionKey")).commit(); 
                userInfo.edit().putString(Constants.USERNAME, userName).commit(); 
                if(cbPassword.isChecked()) {
                    userInfo.edit().putString(Constants.PASSWORD, password).commit(); 
                }else {
                    userInfo.edit().putString(Constants.PASSWORD, "").commit(); 
                }
            }
            
            loginBarHandler.sendMessage(msg);
        } catch (Exception e) {
            Log.d("Login failed  ", e.getMessage());
            e.printStackTrace();
            msg.what = LOGIN_FAILED;
            loginBarHandler.sendMessage(msg);
        }
    }

    Handler loginBarHandler = new Handler(){
        
        @Override
		public void handleMessage(Message msg){
            progressDialog.dismiss();
            int result = msg.what;
            switch(result) {
                case LOGIN_SUCCESS:
                    Intent it = new Intent(LoginActivity.this,HomeActivity.class);
                    startActivity(it);
                    break;
                case LOGIN_FAILED:
                    Toast.makeText(getApplicationContext(), LoginActivity.this.getResources().getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
                    break;    
            }
            
            }
    };
    
   

}
