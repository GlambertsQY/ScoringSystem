package com.example.scoringsystem.activity;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.scoringsystem.R;
import com.example.scoringsystem.base.BaseMainFragment;
import com.example.scoringsystem.base.MySupportActivity;
import com.example.scoringsystem.base.MySupportFragment;
import com.example.scoringsystem.fragment.QuestionInputFragment;
import com.example.scoringsystem.fragment.SearchFragment;
import com.example.scoringsystem.fragment.account.InfoModifyFragment;
import com.example.scoringsystem.fragment.account.LoginFragment;
import com.example.scoringsystem.fragment.home.ScoringFragment;
import com.google.android.material.navigation.NavigationView;

import me.yokeyword.fragmentation.ISupportFragment;
import me.yokeyword.fragmentation.SupportFragment;
import me.yokeyword.fragmentation.anim.FragmentAnimator;

public class MainActivity extends MySupportActivity
        implements BaseMainFragment.OnFragmentOpenDrawerListener, LoginFragment.OnLoginSuccessListener,
        NavigationView.OnNavigationItemSelectedListener {
    public static final String TAG = MainActivity.class.getSimpleName();

    // 再点一次退出程序时间设置
    private static final long WAIT_TIME = 2000L;
    private static boolean ISLOGIN = false;
    private long TOUCH_TIME = 0;
    private static String USERNAME;

    private DrawerLayout mDrawer;
    private NavigationView mNavigationView;
    private TextView mTvName;   // NavigationView上的名字
    private ImageView mImgNav;  // NavigationView上的头像

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MySupportFragment fragment = findFragment(ScoringFragment.class);
        if (fragment == null) {
            loadRootFragment(R.id.fl_container, ScoringFragment.newInstance());
        }
        initView();
    }

    /**
     * 设置动画，也可以使用setFragmentAnimator()设置
     */
    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        // 设置默认Fragment动画  默认竖向(和安卓5.0以上的动画相同)
        return super.onCreateFragmentAnimator();
        // 设置横向(和安卓4.x动画相同)
//        return new DefaultHorizontalAnimator();
        // 设置自定义动画
//        return new FragmentAnimator(enter,exit,popEnter,popExit);
    }

    private void initView() {
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        mDrawer.setDrawerListener(toggle);
        toggle.syncState();
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.setCheckedItem(R.id.nav_scoring);

        LinearLayout llNavHeader = (LinearLayout) mNavigationView.getHeaderView(0);
        mTvName = (TextView) llNavHeader.findViewById(R.id.tv_name);
        mImgNav = (ImageView) llNavHeader.findViewById(R.id.img_nav);
        llNavHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.closeDrawer(GravityCompat.START);
                mDrawer.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        goLogin();
                    }
                }, 250);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        mDrawer.closeDrawer(GravityCompat.START);

        mDrawer.postDelayed(new Runnable() {
            @Override
            public void run() {
                int id = item.getItemId();

                final ISupportFragment topFragment = getTopFragment();
                MySupportFragment myHome = (MySupportFragment) topFragment;

                if (id == R.id.nav_scoring) {

                    ScoringFragment fragment = findFragment(ScoringFragment.class);
                    Bundle newBundle = new Bundle();
                    newBundle.putString("from", "From:" + topFragment.getClass().getSimpleName());
                    fragment.putNewBundle(newBundle);

                    myHome.start(fragment, SupportFragment.SINGLETASK);
                } else if (id == R.id.nav_search) {
//                    DiscoverFragment fragment = findFragment(DiscoverFragment.class);
//                    if (fragment == null) {
//                        myHome.startWithPopTo(DiscoverFragment.newInstance(), ScoringFragment.class, false);
//                    } else {
//                        // 如果已经在栈内,则以SingleTask模式start
//                        myHome.start(fragment, SupportFragment.SINGLETASK);
//                    }
                    start(SearchFragment.newInstance("题库搜索"), SupportFragment.SINGLETASK);
                } else if (id == R.id.nav_questioninput) {
//                    ShopFragment fragment = findFragment(ShopFragment.class);
//                    if (fragment == null) {
//                        myHome.startWithPopTo(ShopFragment.newInstance(), ScoringFragment.class, false);
//                    } else {
//                        // 如果已经在栈内,则以SingleTask模式start,也可以用popTo
////                        start(fragment, SupportFragment.SINGLETASK);
//                        myHome.popTo(ShopFragment.class, false);
//                    }
                    start(QuestionInputFragment.newInstance("题目录入"), SupportFragment.SINGLETASK);
                } else if (id == R.id.nav_login) {
                    goLogin();
                } else if (id == R.id.nav_info_modify) {
                    start(InfoModifyFragment.newInstance(USERNAME), SupportFragment.SINGLETASK);
//                    startActivity(new Intent(MainActivity.this, SwipeBackSampleActivity.class));
                }
            }
        }, 300);

        return true;
    }

    @Override
    public void onBackPressedSupport() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            ISupportFragment topFragment = getTopFragment();

            // 主页的Fragment
            if (topFragment instanceof BaseMainFragment) {
                mNavigationView.setCheckedItem(R.id.nav_scoring);
            }

            if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                pop();
            } else {
                if (System.currentTimeMillis() - TOUCH_TIME < WAIT_TIME) {
                    finish();
                } else {
                    TOUCH_TIME = System.currentTimeMillis();
                    Toast.makeText(this, R.string.press_again_exit, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onOpenDrawer() {
        if (!mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.openDrawer(GravityCompat.START);
        }
    }

    private void goLogin() {
        start(LoginFragment.newInstance());
    }

    @Override
    public void onLoginSuccess(String account) {
        USERNAME = account;
        mTvName.setText(account);
        mImgNav.setImageResource(R.drawable.ic_mood_white_48dp);
        Menu menu_nv = mNavigationView.getMenu();
        menu_nv.findItem(R.id.account_menu_group).setVisible(false);
        menu_nv.findItem(R.id.account_info_group).setVisible(true);
        menu_nv.findItem(R.id.nav_search).setVisible(true);
        menu_nv.findItem(R.id.nav_questioninput).setVisible(true);
        ISLOGIN = true;
        Toast.makeText(this, R.string.sign_in_success, Toast.LENGTH_SHORT).show();
    }

    public static boolean getLoginState() {
        return ISLOGIN;
    }

    public static String getUsername() {
        return USERNAME;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ISLOGIN = false;
    }
}
