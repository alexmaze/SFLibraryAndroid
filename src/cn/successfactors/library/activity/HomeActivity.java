
package cn.successfactors.library.activity;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import cn.successfactors.library.R;

public class HomeActivity extends TabActivity implements OnCheckedChangeListener {
    private TabHost mTabHost;
    boolean isDialogOn;
    private RadioGroup mainTab;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// hidden title
        setContentView(R.layout.activity_home);
        mainTab = (RadioGroup)findViewById(R.id.main_tab);
        mainTab.setOnCheckedChangeListener(this);
        initTabs();
    }

    private void initTabs() {
        this.mTabHost = getTabHost();

        TabSpec tabSpecApple = mTabHost
                .newTabSpec("browser")
                .setIndicator(this.getResources().getString(R.string.home_browser),
                        getResources().getDrawable(R.drawable.icon_1_n))
                .setContent(new Intent(this, BrowserActivity.class));
        mTabHost.addTab(tabSpecApple);

        mTabHost.addTab(mTabHost
                .newTabSpec("borrow")
                .setIndicator(this.getResources().getString(R.string.home_borrow),
                        getResources().getDrawable(R.drawable.icon_2_n))
                .setContent(new Intent(this, BorrowActivity.class)));

        mTabHost.addTab(mTabHost
                .newTabSpec("reservation")
                .setIndicator(this.getResources().getString(R.string.home_reservation),
                        getResources().getDrawable(R.drawable.icon_3_n))
                .setContent(new Intent(this, ReservationActivity.class)));

        mTabHost.addTab(mTabHost
                .newTabSpec("recommand")
                .setIndicator(this.getResources().getString(R.string.home_recommand),
                        getResources().getDrawable(R.drawable.icon_4_n))
                .setContent(new Intent(this, RecommendActivity.class)));
        mTabHost.setCurrentTab(0);
        ((RadioButton)findViewById(R.id.radio_button0)).setChecked(true);
    }

    
    @Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch(checkedId){
        case R.id.radio_button0:
            this.mTabHost.setCurrentTabByTag("browser");
            break;
        case R.id.radio_button1:
            this.mTabHost.setCurrentTabByTag("borrow");
            break;
        case R.id.radio_button2:
            this.mTabHost.setCurrentTabByTag("reservation");
            break;
        case R.id.radio_button3:
            this.mTabHost.setCurrentTabByTag("recommand");
            break;      
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
          if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
              if(!isDialogOn) {
                  isDialogOn =true;
                  showExitGameAlert();
              }else {
                  isDialogOn=false;
                  return true;
              }
          }
          return super.dispatchKeyEvent(event);
       }

    private void showExitGameAlert() {
        new AlertDialog.Builder(HomeActivity.this).setTitle("系统提示").setMessage("确定要退出应用程序吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        HomeActivity.this.finish();
                        System.exit(1);
                        isDialogOn = false;
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        // do nothing
                        isDialogOn = false;
                    }
                }).show();
    }

}
