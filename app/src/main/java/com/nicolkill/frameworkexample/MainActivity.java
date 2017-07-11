package com.nicolkill.frameworkexample;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.nicolkill.framework.activity.BaseActivity;
import com.nicolkill.framework.extractor.view.BindOnClick;
import com.nicolkill.framework.extractor.view.BindOnLongClick;
import com.nicolkill.framework.extractor.view.BindView;
import com.nicolkill.framework.extractor.view.BindLayout;

@BindLayout(R.layout.activity_main)
public class MainActivity extends BaseActivity<MainPresenter> implements MainView {

    @BindView(R.id.fab)
    private FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @BindOnClick(R.id.fab)
    private void fabClick(View view) {
        getPresenter().click(view);
    }

    @BindOnLongClick(R.id.fab)
    private void fabLongClick(View view) {
        getPresenter().longClick(view);
    }

    @Override
    public void presenterClick(View view) {
        Snackbar.make(view, "This action is called from presenter with click", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void presenterLongClick(View view) {
        Snackbar.make(view, "This action is called from presenter with long click", Snackbar.LENGTH_LONG).show();
    }

}
