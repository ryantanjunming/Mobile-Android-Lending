package com.crowdo.p2pconnect.view.activities;

import android.animation.Animator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.crowdo.p2pconnect.R;
import com.crowdo.p2pconnect.helpers.ConstantVariables;
import com.crowdo.p2pconnect.helpers.LocaleHelper;
import com.crowdo.p2pconnect.helpers.TypefaceUtils;
import com.crowdo.p2pconnect.view.fragments.LearningCenterFragment;
import com.crowdo.p2pconnect.view.fragments.LoanListFragment;
import com.crowdo.p2pconnect.view.fragments.UserSettingsFragment;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by cwdsg05 on 3/2/17.
 */

public class MainActivity extends AppCompatActivity{

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindString(R.string.pre_exit_question) String mPreExitQuestion;
    @BindString(R.string.permission_overlay_permission_request) String mOverlayPermissionRequest;
    @BindString(R.string.language_english_label) String mLanguageEnglish;
    @BindString(R.string.language_bahasa_label) String mLanguageBahasa;

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private Drawer navDrawer;

    private static final int DRAWER_SELECT_LOAN_LIST_FRAGMENT = 100;
    private static final int DRAWER_SELECT_USER_SETTINGS_FRAGMENT = 101;
    private static final int DRAWER_SELECT_LEARNING_CENTER_FRAGMENT = 102;
    private static final int DRAWER_SELECT_LANGUAGE_CHANGE = 103;
    private static final int DRAWER_SELECT_LANGUAGE_EN = 500;
    private static final int DRAWER_SELECT_LANGUAGE_IN = 501;
    private static final int DRAWER_SELECT_TOP_UP_WALLET = 104;
    private static final int DRAWER_SELECT_APPLY_AS_INVESTOR = 105;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        checkDrawOverlayPermission();

        mToolbar.setTitle(getString(R.string.toolbar_title_loan_list));
        setSupportActionBar(mToolbar);

        //finally load fragment
        getSupportFragmentManager().beginTransaction()
                .add(R.id.main_content, new LoanListFragment())
                .commit();


        navDrawer = buildNavigationDrawer()
                .withSavedInstance(savedInstanceState)
                .build();

        navDrawer.setSelection(100);

        TextView mNavDrawerAppLogo = (TextView) navDrawer.getHeader().findViewById(R.id.nav_header_app_title);
        mNavDrawerAppLogo.setTypeface(TypefaceUtils.getNothingYouCouldDoTypeFace(this));
    }

    private DrawerBuilder buildNavigationDrawer(){
        return new DrawerBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(true)
                .withToolbar(mToolbar)
                .withHeader(R.layout.nav_header)
                .withDrawerWidthDp(280)
                .withFullscreen(true)
                .addDrawerItems(
                        new PrimaryDrawerItem().withIdentifier(DRAWER_SELECT_LOAN_LIST_FRAGMENT).withName(R.string.toolbar_title_loan_list).withIcon(CommunityMaterial.Icon.cmd_gavel)
                                .withSetSelected(true).withSelectedTextColorRes(R.color.color_primary_700).withSelectedIconColorRes(R.color.color_primary_700),
                        new PrimaryDrawerItem().withIdentifier(DRAWER_SELECT_USER_SETTINGS_FRAGMENT).withName(R.string.toolbar_title_user_settings).withIcon(CommunityMaterial.Icon.cmd_settings)
                                .withSelectedTextColorRes(R.color.color_primary_700).withSelectedIconColorRes(R.color.color_primary_700),
                        new PrimaryDrawerItem().withIdentifier(DRAWER_SELECT_LEARNING_CENTER_FRAGMENT).withName(R.string.toolbar_title_learning_center).withIcon(CommunityMaterial.Icon.cmd_book_open_page_variant)
                                .withSelectedTextColorRes(R.color.color_primary_700).withSelectedIconColorRes(R.color.color_primary_700),
                        new SectionDrawerItem().withName(R.string.navmenu_label_preferences),
                        new ExpandableDrawerItem().withIdentifier(DRAWER_SELECT_LANGUAGE_CHANGE).withName(R.string.navmenu_label_language).withIcon(CommunityMaterial.Icon.cmd_translate)
                                .withSelectable(false).withSubItems(
                                    new SecondaryDrawerItem().withIdentifier(DRAWER_SELECT_LANGUAGE_EN).withName(R.string.language_english_label).withLevel(2),
                                    new SecondaryDrawerItem().withIdentifier(DRAWER_SELECT_LANGUAGE_IN).withName(R.string.language_bahasa_label).withLevel(2)
                                ),
                        new SectionDrawerItem().withName(R.string.navmenu_label_extras),
                        new SecondaryDrawerItem().withIdentifier(DRAWER_SELECT_TOP_UP_WALLET).withName(R.string.toolbar_title_top_up_wallet)
                                .withIcon(CommunityMaterial.Icon.cmd_wallet),
                        new SecondaryDrawerItem().withIdentifier(DRAWER_SELECT_APPLY_AS_INVESTOR).withName(R.string.toolbar_title_apply_investor)
                                .withIcon(CommunityMaterial.Icon.cmd_account_star_variant)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if(drawerItem != null){
                            Fragment fragment = null;
                            Class fragmentClass = null;

                            switch ((int) drawerItem.getIdentifier()){
                                case DRAWER_SELECT_LOAN_LIST_FRAGMENT:
                                    fragmentClass = LoanListFragment.class;
                                    mToolbar.setTitle(R.string.toolbar_title_loan_list);
                                    break;
                                case DRAWER_SELECT_USER_SETTINGS_FRAGMENT:
                                    fragmentClass = UserSettingsFragment.class;
                                    mToolbar.setTitle(R.string.toolbar_title_user_settings);
                                    break;
                                case DRAWER_SELECT_LEARNING_CENTER_FRAGMENT:
                                    fragmentClass = LearningCenterFragment.class;
                                    mToolbar.setTitle(R.string.toolbar_title_learning_center);
                                    break;
                                case DRAWER_SELECT_LANGUAGE_EN:
                                    LocaleHelper.setLocale(MainActivity.this, ConstantVariables.APP_LANG_EN);
                                    MainActivity.this.recreate();
                                    break;
                                case DRAWER_SELECT_LANGUAGE_IN:
                                    LocaleHelper.setLocale(MainActivity.this, ConstantVariables.APP_LANG_IN);
                                    MainActivity.this.recreate();
                                    break;
                                case DRAWER_SELECT_TOP_UP_WALLET:


                                    break;
                                default:
                            }

                            if(fragmentClass != null) {
                                try {
                                    fragment = (Fragment) fragmentClass.newInstance();
                                } catch (Exception e) {
                                    Log.e(LOG_TAG, "ERROR: " + e.getMessage(), e);
                                    e.printStackTrace();
                                }

                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.main_content, fragment)
                                        .commit();
                            }
                        }

                        return false;
                    }
                });
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage(mPreExitQuestion)
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.super.onBackPressed();
                    }
                }).create().show();
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
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mToolbar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
    }

    public boolean checkDrawOverlayPermission() {
        //Check Overlays for Marshmellow version and after
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if (!Settings.canDrawOverlays(this)) {
            new AlertDialog.Builder(this)
                    .setMessage(mOverlayPermissionRequest)
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:" + getPackageName()));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY
                                    | Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivityForResult(intent, ConstantVariables.REQUEST_CODE_PERMISSIONS_OVERLAY);
                        }
                    }).create().show();
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

}
