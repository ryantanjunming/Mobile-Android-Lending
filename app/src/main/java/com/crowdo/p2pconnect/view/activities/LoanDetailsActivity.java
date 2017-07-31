package com.crowdo.p2pconnect.view.activities;

import android.animation.Animator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.crowdo.p2pconnect.R;
import com.crowdo.p2pconnect.support.NetworkConnectionChecks;
import com.crowdo.p2pconnect.helpers.LocaleHelper;
import com.crowdo.p2pconnect.view.fragments.LoanDetailsFragment;
import com.f2prateek.dart.Dart;
import com.f2prateek.dart.InjectExtra;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoanDetailsActivity extends AppCompatActivity {

    public static final String LOG_TAG = LoanDetailsActivity.class.getSimpleName();

    @InjectExtra public int id;
    @BindView(R.id.toolbar) Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_details);
        ButterKnife.bind(this);
        Dart.inject(this);

        //mToolbar view
        setSupportActionBar(mToolbar);
        mToolbar.setTitle(getString(R.string.toolbar_title_loan_detail));
        LoanDetailsActivity.this.setTitle(getString(R.string.toolbar_title_loan_detail));

        //enable back buttons
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle args = new Bundle();
        args.putInt(LoanDetailsFragment.BUNDLE_ID_KEY, this.id);

        LoanDetailsFragment loanDetailsFragment = new LoanDetailsFragment();
        loanDetailsFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.loan_details_content, loanDetailsFragment)
                .commit();
    }


    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(id == android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSupportActionModeStarted(@NonNull android.support.v7.view.ActionMode mode) {
        //cause style.xml windowActionBarOverlay doesnt work
        mToolbar.setVisibility(View.GONE);
        super.onSupportActionModeStarted(mode);
    }

    @Override
    public void onSupportActionModeFinished(@NonNull android.support.v7.view.ActionMode mode) {
        //cause style.xml windowActionBarOverlay doesnt work
        super.onSupportActionModeFinished(mode);
        mToolbar.animate()
                .alpha(1.0f)
                .setDuration(300)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) { }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mToolbar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) { }

                    @Override
                    public void onAnimationRepeat(Animator animation) { }
                });

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        //check network and dun show loggout
        NetworkConnectionChecks.isOnline(this);
    }
}
