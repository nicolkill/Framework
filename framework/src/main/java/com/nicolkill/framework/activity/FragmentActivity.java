package com.nicolkill.framework.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.nicolkill.framework.mvp.Presenter;

import java.util.LinkedList;

/**
 * Created by Nicol Acosta on 10/12/16.
 * nicol@parkiller.com
 */
public abstract class FragmentActivity<P extends Presenter> extends BaseActivity<P> {

    private static final String TAG = FragmentActivity.class.getSimpleName();

    private int mContainerId = 0;

    private LinkedList<String> mTitles = new LinkedList<>();

    public void setContainerId(int containerId) {
        mContainerId = containerId;
    }

    public int getContainerId() {
        return mContainerId;
    }

    public void updateFragment(String title, Fragment fragment) {
        updateFragment(title, fragment, false);
    }

    public void updateFragment(String title, Fragment fragment, boolean removePresent) {
        if (mContainerId == 0) {
            throw new IllegalStateException("You need change container id on setContainerId(int containerId)");
        }
        if (fragment != null) {
            if (removePresent && getSupportFragmentManager().getBackStackEntryCount() > 0) {
                popBackStack();
            }
            if (title == null) {
                title = getTitle().toString();
            }
            setTitle(title);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(mContainerId, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack(null)
                    .commit();
            mTitles.add(title);
        }
    }

    public int getFragmentPosition() {
        return getSupportFragmentManager().getBackStackEntryCount() - 1;
    }

    public void popBackStack() {
        getSupportFragmentManager().popBackStack();
        mTitles.removeLast();
        setTitle(mTitles.size() > 0 ? mTitles.getLast():"");
    }

    @Override
    public void onBackPressed() {
        if (getFragmentPosition() > 0) {
            popBackStack();
        } else {
            finish();
        }
    }
}
