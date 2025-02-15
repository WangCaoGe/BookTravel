package com.application.booktravel.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMContactManager;
import com.easemob.chatuidemo.DemoApplication;
import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.activity.AlertDialog;

public class DynamicsFragmentPage2 extends Fragment {
    private EditText editText;
    private LinearLayout searchedUserLayout;
    private LinearLayout addcontactcaoback;
    private TextView nameText, mTextView;
    private Button searchBtn;
    private Button indicator2;
    private ImageView avatar;
    private InputMethodManager inputMethodManager;
    private String toAddUsername;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_add_contactcao, null);
        mTextView = (TextView) view.findViewById(R.id.add_list_friends2);
        editText = (EditText) view.findViewById(R.id.edit_note2);
        String strAdd = getResources().getString(R.string.add_friend);
        mTextView.setText(strAdd);
        String strUserName = getResources().getString(R.string.user_name);
        editText.setHint(strUserName);
        addcontactcaoback = (LinearLayout) view
                .findViewById(R.id.addcontactcaoback);
        searchedUserLayout = (LinearLayout) view.findViewById(R.id.ll_user2);
        nameText = (TextView) view.findViewById(R.id.name2);
        searchBtn = (Button) view.findViewById(R.id.search2);
        indicator2 = (Button) view.findViewById(R.id.indicator2);
        avatar = (ImageView) view.findViewById(R.id.avatar2);
        inputMethodManager = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /**
         * 搜索联系人
         */
        searchBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                final String name = editText.getText().toString();
                String saveText = searchBtn.getText().toString();

                if (getString(R.string.button_search).equals(saveText)) {
                    toAddUsername = name;
                    if (TextUtils.isEmpty(name)) {
                        String st = getResources().getString(
                                R.string.Please_enter_a_username);
                        startActivity(new Intent(getActivity(),
                                AlertDialog.class).putExtra("msg", st));
                        return;
                    }

                    // TODO 从服务器获取此contact,如果不存在提示不存在此用户

                    // 服务器存在此用户，显示此用户和添加按钮
                    searchedUserLayout.setVisibility(View.VISIBLE);
                    nameText.setText(toAddUsername);
                }
            }

        });

        /**
         * 返回键
         */
        addcontactcaoback.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onDestroyView();
            }
        });
        /**
         * 添加contact
         * 
         * @param view
         */
        indicator2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (DemoApplication.getInstance().getUserName()
                        .equals(nameText.getText().toString())) {
                    String str = getString(R.string.not_add_myself);
                    startActivity(new Intent(getActivity(), AlertDialog.class)
                            .putExtra("msg", str));
                    return;
                }

                if (DemoApplication.getInstance().getContactList()
                        .containsKey(nameText.getText().toString())) {
                    // 提示已在好友列表中，无需添加
                    if (EMContactManager.getInstance().getBlackListUsernames()
                            .contains(nameText.getText().toString())) {
                        startActivity(new Intent(getActivity(),
                                AlertDialog.class).putExtra("msg",
                                "此用户已是你好友(被拉黑状态)，从黑名单列表中移出即可"));
                        return;
                    }
                    String strin = getString(R.string.This_user_is_already_your_friend);
                    startActivity(new Intent(getActivity(), AlertDialog.class)
                            .putExtra("msg", strin));
                    return;
                }

                progressDialog = new ProgressDialog(getActivity());
                String stri = getResources().getString(
                        R.string.Is_sending_a_request);
                progressDialog.setMessage(stri);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                new Thread(new Runnable() {
                    public void run() {

                        try {
                            // demo写死了个reason，实际应该让用户手动填入
                            String s = getResources().getString(
                                    R.string.Add_a_friend);
                            EMContactManager.getInstance().addContact(
                                    toAddUsername, s);
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    progressDialog.dismiss();
                                    String s1 = getResources().getString(
                                            R.string.send_successful);
                                    getActivity().runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {
                                            searchedUserLayout
                                                    .setVisibility(View.INVISIBLE);

                                        }
                                    });
                                    Toast.makeText(
                                            getActivity()
                                                    .getApplicationContext(),
                                            s1, 1).show();
                                }
                            });
                        } catch (final Exception e) {
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    progressDialog.dismiss();
                                    String s2 = getResources().getString(
                                            R.string.Request_add_buddy_failure);
                                    Toast.makeText(
                                            getActivity()
                                                    .getApplicationContext(),
                                            s2 + e.getMessage(), 1).show();
                                }
                            });
                        }
                    }
                }).start();
            }
        });

    }

}
