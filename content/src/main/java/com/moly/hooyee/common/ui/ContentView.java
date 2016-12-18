package com.moly.hooyee.common.ui;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.moly.hooyee.common.ui.listencer.OnNavigationItemSelectedListenerAdapter;

/**
 * Created by Hooyee on 2016/12/6.
 * mail: hooyee01_moly@foxmail.com
 */

public class ContentView extends DrawerLayout {
    private NavigationView navView;
    private DrawerLayout mDrawerLayout;
    private RelativeLayout mCoordinatorLayout;

    public ContentView(Context context) {
        super(context);
        View mainLayout =  LayoutInflater.from(getContext()).inflate(R.layout.activity_main, null);
        mDrawerLayout = (DrawerLayout) mainLayout.findViewById(R.id.drawer_layout);
        navView = (NavigationView) mainLayout.findViewById(R.id.nav_view);
        mCoordinatorLayout = (RelativeLayout) mainLayout.findViewById(R.id.content_container);

        FloatingActionButton fab = (FloatingActionButton) mainLayout.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Toolbar toolbar = (Toolbar) mainLayout.findViewById(R.id.toolbar);
        toolbar.setTitle("");
        ((AppCompatActivity)context).setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                (Activity)getContext(), mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        navView.setNavigationItemSelectedListener(new OnNavigationItemSelectedListenerAdapter(mDrawerLayout.getContext(), mDrawerLayout));
    }

    /**
     * 获取布局(centent)
     * @return
     */
    public DrawerLayout getContent() {
        return mDrawerLayout;
    }

    /**
     * 设置Navigation的header布局
     * @param resId
     */
    public void inflateNavHeaderView(int resId) {
        navView.removeHeaderView(navView.getHeaderView(0));
        navView.inflateHeaderView(resId);
    }

    /**
     * 设置header的背景图片
     * @param resId
     */
    public void setNavHeaderBackground(int resId) {
        View v = navView.getHeaderView(0);
        v.setBackgroundResource(resId);
    }

    /**
     * 设置Navigation的menu布局
     * @param resId
     */
    public void inflateNavMenu(int resId) {
        navView.inflateMenu(resId);
    }

    public void addMainContent(View v) {
        mCoordinatorLayout.addView(v);
    }
}
