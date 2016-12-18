package com.moly.hooyee.common.ui.listencer;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

import com.moly.hooyee.common.ui.R;

/**
 * Created by Hooyee on 2016/12/6.
 * mail: hooyee01_moly@foxmail.com
 */

public class OnNavigationItemSelectedListenerAdapter implements NavigationView.OnNavigationItemSelectedListener {
    private Context mContext;
    private DrawerLayout mDrawerLayout;

    public OnNavigationItemSelectedListenerAdapter(Context mContext, DrawerLayout mDrawerLayout) {
        this.mContext = mContext;
        this.mDrawerLayout = mDrawerLayout;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_rate) {
            openAppInStore(mContext, mContext.getPackageName());
        } else if (id == R.id.nav_share) {
            shareApp("s");
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void openAppInStore(Context context, String pkg) {
        if (!pkg.startsWith("market") && !pkg.startsWith("http")) {
            pkg = "market://details?id=" + pkg;
        }
        Uri uri = Uri.parse(pkg);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Uri getAppIcon() {
        Resources r = mContext.getResources();
        Uri uri =  Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                + r.getResourcePackageName(R.drawable.ic_rate) + "/"
                + r.getResourceTypeName(R.drawable.ic_rate) + "/"
                + r.getResourceEntryName(R.drawable.ic_rate));

        return uri;
    }

    public void shareApp(String shareDescription) {
        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Share");
        intent.putExtra(Intent.EXTRA_TEXT, "<<" + mContext.getString(R.string.app_name) + ">>" + "一个非常不错的应用，" + "market://details?id=" + mContext.getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(Intent.createChooser(intent, "分享"));
    }
}
