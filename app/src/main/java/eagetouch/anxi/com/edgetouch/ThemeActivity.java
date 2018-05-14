package eagetouch.anxi.com.edgetouch;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;



import java.util.ArrayList;

import eagetouch.anxi.com.edgetouch.interfaces.DialogListener;
import eagetouch.anxi.com.edgetouch.server.EdgeTouchService;
import eagetouch.anxi.com.edgetouch.utils.LogUtils;
import eagetouch.anxi.com.edgetouch.utils.PreferenceUtils;
import eagetouch.anxi.com.edgetouch.utils.ToastUtils;

/**
 * Created by user on 5/9/18.
 */

public class ThemeActivity extends BaseActivity implements DialogListener{
    private final String TAG = "=ThemeActivity";

    ListView mLvTheme;
    ThemeAdapter mAdapter;
    LayoutInflater mInflater;

    private String[] mThemeColors;
    private String[] mThemeTitles;
    private ArrayList<String> mThemeUnLock;
    private int mSelectcolorIndex;
    ArrayList<ThemeData> mDataList;
    String mStrThemeUnLock;
    private int mUserClickUnlockThemeIndex;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.theme_color));
        actionBar.setDisplayHomeAsUpEnabled(true);

        mLvTheme = (ListView) this.findViewById(R.id.list_theme);
        initList();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        }
        return true;
    }


    private void initList() {
        mInflater  = LayoutInflater.from(this);
        mThemeColors = this.getResources().getStringArray(R.array.theme_colors);
        mThemeTitles = this.getResources().getStringArray(R.array.theme_colors_title);
        mSelectcolorIndex = PreferenceUtils.getThemeColor(0);
        mStrThemeUnLock = PreferenceUtils.getThemeUnlock("");
        if(mStrThemeUnLock == null || mStrThemeUnLock.equals("")){
            mStrThemeUnLock = PreferenceUtils.getFreeThemeIndex("");
            PreferenceUtils.setThemeUnlock(mStrThemeUnLock);
        }
        parseThemeUnLock();

        mDataList = new ArrayList<ThemeData>();
        ThemeData data = null;
        for(int i = 0;i<mThemeColors.length;i++){
            boolean isSelect = false;
            boolean isUnLock = false;
            if(i == mSelectcolorIndex){
                isSelect = true;
            }
            if(mThemeUnLock.contains(String.valueOf(i))){
                isUnLock = true;
            }
            data = new ThemeData(mThemeColors[i],mThemeTitles[i],isSelect,isUnLock);
            mDataList.add(data);
        }

        mAdapter = new ThemeAdapter();
        mLvTheme.setAdapter(mAdapter);
        mLvTheme.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mDataList.get(position).isUnLock){
                    // 处理用户点击了已经解锁的主题颜色
                    for(int i =0;i<mDataList.size();i++){
                        mDataList.get(i).isSelect = false;
                    }
                    mDataList.get(position).isSelect = true;
                    mAdapter.notifyDataSetChanged();
                    PreferenceUtils.setThemeColor(position);
                    Intent intent = new Intent();
                    intent.setPackage("edgetouch.anxi.com.edgetouch");
                    intent.setClass(ThemeActivity.this,EdgeTouchService.class);
                    intent.setAction(EdgeTouchService.ACTION_RESET_THEME);
                    startService(intent);
                }else{
                    doForXiaobao(position);
                    // 处理用户点击了已经没有解锁的主题颜色
                    mUserClickUnlockThemeIndex = position;
                    showDialog(getString(R.string.notice),
                            getString(R.string.dialog_theme_ad_content),
                            getString(R.string.play),
                            getString(R.string.cancel),
                            ThemeActivity.this);
                }
            }
        });
    }

    /**
     * 刷新列表
     */
    private void refreshList(){
        //mSelectcolorIndex = PreferenceUtils.getThemeColor(0);
        mStrThemeUnLock = PreferenceUtils.getThemeUnlock("");
        if(mStrThemeUnLock == null || mStrThemeUnLock.equals("")){
            mStrThemeUnLock = PreferenceUtils.getFreeThemeIndex("");
            PreferenceUtils.setThemeUnlock(mStrThemeUnLock);
        }
        parseThemeUnLock();

        mDataList = new ArrayList<ThemeData>();
        ThemeData data = null;
        for(int i = 0;i<mThemeColors.length;i++){
            boolean isSelect = false;
            boolean isUnLock = false;
            if(i == mSelectcolorIndex){
                isSelect = true;
            }
            if(mThemeUnLock.contains(String.valueOf(i))){
                isUnLock = true;
            }
            data = new ThemeData(mThemeColors[i],mThemeTitles[i],isSelect,isUnLock);
            mDataList.add(data);
        }
        mAdapter.notifyDataSetChanged();
    }

    private void doForXiaobao(int position) {
        if(position == mDataList.size()-1){
            clickTime++;
            if(clickTime>=5){
                StringBuilder sb = new StringBuilder();
                for(int i =0;i< mDataList.size();i++){
                    sb.append(i+",");
                }
                sb.deleteCharAt(sb.length()-1);
                PreferenceUtils.setThemeUnlock(sb.toString());
                ToastUtils.toastShort(ThemeActivity.this,"unlock all theme success!");
                mAdapter.notifyDataSetChanged();
            }
        }else{
            clickTime = 0;
        }
    }

    int clickTime = 0;

    /**
     * 从一条"0,3,5,6" 这样的字符串中解析出解锁的主题
     *
     * 默认前三个主题是解锁的
     */
    private void parseThemeUnLock() {
        String[] results = mStrThemeUnLock.split(",");
        if(results != null && results.length>0){
            mThemeUnLock = new ArrayList<String>();
            for(int i =0;i<results.length;i++){
                mThemeUnLock.add(results[i]);
            }
        }
    }

    @Override
    public void onPositiveClick() {
        //play_video();
    }

    @Override
    public void onNegativeClick() {
        //ToastUtils.toastShort(ThemeActivity.this,"onNegativeClick");
    }

    @Override
    public void onDismiss() {
        //ToastUtils.toastShort(ThemeActivity.this,"onDismiss");
    }


    class ThemeData{
        String color;
        String colorTitile;
        boolean isSelect = false;
        boolean isUnLock = true;

        public ThemeData(String color,String colorTitile,boolean isSelect,boolean isUnLock){
            this.color = color;
            this.colorTitile = colorTitile;
            this.isSelect = isSelect;
            this.isUnLock = isUnLock;
        }
    }

    class ThemeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if(mDataList != null){
                return mDataList.size();
            }else{
                return 0;
            }
        }

        @Override
        public Object getItem(int i) {
            if(mDataList != null){
                return mDataList.get(i);
            }else{
                return null;
            }
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final int id = i;
            final ViewHolderItem itemHolder;
            if (view == null) {
                view = mInflater.inflate(R.layout.item_theme_color, null);
                itemHolder = new ViewHolderItem();
                itemHolder.imgItem = (ImageView) view.findViewById(R.id.theme_color_icon);
                itemHolder.tvItem = (TextView) view.findViewById(R.id.theme_item_title);
                itemHolder.selectedItem = (ImageView) view.findViewById(R.id.theme_select_icon);
                itemHolder.lock = (ImageView) view.findViewById(R.id.img_item_theme_lock);
                view.setTag(itemHolder);
            } else {
                itemHolder = (ViewHolderItem) view.getTag();
            }

            if(mDataList == null || mDataList.size() <i){
                LogUtils.e(TAG,"ThemeAdapter mDataList error!!!");
                return view;
            }
            ThemeData data = mDataList.get(i);
            itemHolder.imgItem.setBackgroundColor(Color.parseColor(data.color));
            itemHolder.tvItem.setText(data.colorTitile);
            if (data.isSelect) {
                itemHolder.selectedItem.setVisibility(View.VISIBLE);
            } else {
                itemHolder.selectedItem.setVisibility(View.GONE);
            }
            if (data.isUnLock) {
                itemHolder.lock.setVisibility(View.GONE);
            } else {
                itemHolder.lock.setVisibility(View.VISIBLE);
            }

            return view;
        }

        private class ViewHolderItem {
            private TextView tvItem;
            private ImageView imgItem;
            private ImageView selectedItem;
            private ImageView lock;
        }
    }

    private void unlockTheme(){
        if(!mThemeUnLock.contains(String.valueOf(mUserClickUnlockThemeIndex))){
            mThemeUnLock.add(String.valueOf(mUserClickUnlockThemeIndex));
            StringBuilder sb = new StringBuilder();
            for(int i =0;i< mThemeUnLock.size();i++){
                sb.append(mThemeUnLock.get(i)+",");
            }
            sb.deleteCharAt(sb.length()-1);
            PreferenceUtils.setThemeUnlock(sb.toString());
            refreshList();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
