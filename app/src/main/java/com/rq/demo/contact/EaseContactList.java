package com.rq.demo.contact;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.hzaz.base.common_util.LOG;
import com.rq.demo.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class EaseContactList extends RelativeLayout {
    protected static final String TAG = EaseContactList.class.getSimpleName();

    protected Context context;
    protected ListView listView;
    protected EaseContactAdapter adapter;
    protected List<EaseUser> contactList;
    protected EaseSidebar sidebar;

    protected int primaryColor;
    protected int primarySize;
    protected boolean showSiderBar;
    protected Drawable initialLetterBg;

    static final int MSG_UPDATE_LIST = 0;
    static final int MSG_CASH_LIST = 1;

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_LIST:
                    if (adapter != null) {
                        adapter.clear();
                        adapter.showLetter(true);
                        adapter.addAll(new ArrayList<>(contactList));
                        adapter.notifyDataSetChanged();
                    }
                    break;
                case MSG_CASH_LIST:
                    if (adapter != null) {
                        adapter.clear();
                        adapter.showLetter(true);
                        adapter.addAll(new ArrayList<>(cashList));
                        adapter.notifyDataSetChanged();
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    protected int initialLetterColor;


    public EaseContactList(Context context) {
        super(context);
        init(context, null);
    }

    public EaseContactList(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public EaseContactList(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }


    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.EaseContactList);
        primaryColor = ta.getColor(R.styleable.EaseContactList_ctsListPrimaryTextColor, 0);
        primarySize = ta.getDimensionPixelSize(R.styleable.EaseContactList_ctsListPrimaryTextSize, 0);
        showSiderBar = ta.getBoolean(R.styleable.EaseContactList_ctsListShowSiderBar, true);
        initialLetterBg = ta.getDrawable(R.styleable.EaseContactList_ctsListInitialLetterBg);
        initialLetterColor = ta.getColor(R.styleable.EaseContactList_ctsListInitialLetterColor, 0);
        ta.recycle();


        LayoutInflater.from(context).inflate(R.layout.ease_widget_contact_list, this);
        listView = (ListView) findViewById(R.id.list);
        sidebar = (EaseSidebar) findViewById(R.id.sidebar);
        if (!showSiderBar)
            sidebar.setVisibility(View.GONE);
    }

    public void init(List<EaseUser> contactList) {
        synchronized (EaseContactList.class) {
            // sorting
            Collections.sort(contactList, new Comparator<EaseUser>() {

                @Override
                public int compare(EaseUser usr1, EaseUser usr2) {
                    if (usr1.getInitialLetter().equals(usr2.getInitialLetter())) {
                        return 0;
                    } else {
                        if ("#".equals(usr1.getInitialLetter())) {
                            return 1;
                        }
                        if ("#".equals(usr2.getInitialLetter())) {
                            return -1;
                        }
                        return usr1.getInitialLetter().compareTo(usr2.getInitialLetter());
                    }

                }
            });
            this.contactList = contactList;
            this.cashList = new ArrayList<>(contactList);
            adapter = new EaseContactAdapter(context, 0, new ArrayList<>(contactList));
            adapter.setPrimaryColor(primaryColor).setPrimarySize(primarySize).setInitialLetterBg(initialLetterBg)
                    .setInitialLetterColor(initialLetterColor);
            listView.setAdapter(adapter);

            if (showSiderBar) {
                sidebar.setListView(listView);
            }
        }
    }
    public void autoSort(boolean b) {
    }

    List<EaseUser> cashList;

    public void search(String str) {
        if (str == null || contactList == null) {
            if (cashList != null) {
                showCash();
            }
            return;
        }
        synchronized (EaseContactList.class) {
            LOG.e("EaseContactList", "str:" + str);
            List<EaseUser> searchs = new ArrayList<>();
            for (EaseUser usr : cashList.size() > contactList.size() ? cashList : contactList) {
                if (usr.match(str)) {
                    LOG.e("EaseContactList", "ADD:" + usr.getUsername());
                    searchs.add(usr);
                } else {
                    LOG.e("EaseContactList", usr.getUsername() + ".PASS:" + PingYingUtil.getSpells(usr.getUsername()));
                }
            }
            contactList.clear();
            contactList.addAll(searchs);
            LOG.e("EaseContactList", "LINE:135  " + cashList.size() + "  " + contactList.size());
            refresh();
        }

    }

    private void showCash() {
        Message msg = handler.obtainMessage(MSG_CASH_LIST);
        handler.sendMessage(msg);
    }

    public void refresh() {
        Message msg = handler.obtainMessage(MSG_UPDATE_LIST);
        handler.sendMessage(msg);
    }

    public void filter(CharSequence str) {
        adapter.getFilter().filter(str);
    }

    public ListView getListView() {
        return listView;
    }

    public void setShowSiderBar(boolean showSiderBar) {
        if (showSiderBar) {
            sidebar.setVisibility(View.VISIBLE);
        } else {
            sidebar.setVisibility(View.GONE);
        }
    }



}
