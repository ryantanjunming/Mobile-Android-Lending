package com.crowdo.p2pconnect.viewholders;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.crowdo.p2pconnect.R;
import com.crowdo.p2pconnect.view.fragments.LoginFragment;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by cwdsg05 on 21/3/17.
 */

public class LoginViewHolder {

    @BindView(R.id.auth_login_email_edittext) public AppCompatEditText mEmailEditText;
    @BindView(R.id.auth_login_password_edittext) public AppCompatEditText mPasswdEditText;
    @BindView(R.id.auth_login_exit_btn) public ImageButton mExitImageButton;
    @BindView(R.id.auth_login_submit_btn) public LinearLayout mSubmitButton;

    private static final String LOG_TAG = LoginViewHolder.class.getSimpleName();
    private Context mContext;

    public LoginViewHolder(View view, Context mContext) {
        this.mContext = mContext;
        ButterKnife.bind(this, view);
    }

    public void initView(){
        mSubmitButton.setEnabled(false); //init

        mEmailEditText.setCompoundDrawables(
                new IconicsDrawable(mContext)
                        .icon(CommunityMaterial.Icon.cmd_account)
                        .colorRes(R.color.color_secondary_text)
                        .sizeRes(R.dimen.auth_field_drawable_icon_size),
                null, null, null);

        mPasswdEditText.setCompoundDrawables(
                new IconicsDrawable(mContext)
                        .icon(CommunityMaterial.Icon.cmd_lock)
                        .colorRes(R.color.color_secondary_text)
                        .sizeRes(R.dimen.auth_field_drawable_icon_size),
                null, null, null);

        mExitImageButton.setImageDrawable(
                new IconicsDrawable(mContext)
                        .icon(CommunityMaterial.Icon.cmd_close)
                        .sizeRes(R.dimen.auth_btn_drawable_close_icon_size));

        mEmailEditText.addTextChangedListener(new SubmitEnablerTextWatcher(new EditText[]{mEmailEditText, mPasswdEditText}, mSubmitButton));

        mPasswdEditText.addTextChangedListener(new SubmitEnablerTextWatcher(new EditText[]{mEmailEditText, mPasswdEditText}, mSubmitButton));

    }

    class SubmitEnablerTextWatcher implements TextWatcher {
        View view;
        EditText[] editTextList;

        public SubmitEnablerTextWatcher(EditText[] editTextList, View view) {
            this.view = view;
            this.editTextList = editTextList;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            for(EditText editText : editTextList){
                if(editText.getText().length() <= 0 ){
                    view.setEnabled(false);
                    return;
                }
            }
            view.setEnabled(true); //all pass
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }
}