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

import com.pad.android_independent_video_sdk.IndependentVideoAvailableState;
import com.pad.android_independent_video_sdk.IndependentVideoListener;
import com.pad.android_independent_video_sdk.IndependentVideoManager;

import java.util.ArrayList;

import eagetouch.anxi.com.edgetouch.interfaces.DialogListener;
import eagetouch.anxi.com.edgetouch.server.EdgeTouchService;
import eagetouch.anxi.com.edgetouch.utils.LogUtils;
import eagetouch.anxi.com.edgetouch.utils.PreferenceUtils;
import eagetouch.anxi.com.edgetouch.utils.ToastUtils;

/**
 * Created by user on 5/9/18.
 */

public class ThemeActivity extends BaseActivity implements DialogListener,IndependentVideoListener {
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

        initDuoMengAd();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        }
        return true;
    }

    /**
     * 初始化多盟广告
     */
    private void initDuoMengAd() {
        IndependentVideoManager.newInstance().enableLog(true); //是否开启sdk的log，默认是开启
        IndependentVideoManager.newInstance().init(this,false);//初始化
        //IndependentVideoManager.newInstance().updateUserID(this,"abcd");//设置用户唯一标示，不是媒体id，是开发者用户体系中，用户的唯一标示，没有，则可以不设置。
        IndependentVideoManager.newInstance().disableShowAlert(this,false);//是否使用多盟提示框，提示完成任务，默认为true
        IndependentVideoManager.newInstance().addIndependentVideoListener(this);
    }

    private void initList() {
        mInflater  = LayoutInflater.from(this);
        mThemeColors = this.getResources().getStringArray(R.array.theme_colors);
        mThemeTitles = this.getResources().getStringArray(R.array.theme_colors_title);
        mSelectcolorIndex = PreferenceUtils.getThemeColor(0);
        mStrThemeUnLock = PreferenceUtils.getThemeUnlock("");
        if(mStrThemeUnLock == null || mStrThemeUnLock.equals("")){
            mStrThemeUnLock = "0,1,2";
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
                    //doForXiaobao(position);
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
            mStrThemeUnLock = "0,1,2";
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
            if(clickTime>=10){
                StringBuilder sb = new StringBuilder();
                for(int i =0;i< mDataList.size();i++){
                    sb.append(i+",");
                }
                sb.deleteCharAt(sb.length()-1);
                PreferenceUtils.setThemeUnlock(sb.toString());
                ToastUtils.toastShort(ThemeActivity.this,"unlock all theme success!");
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
        play_video();
    }

    @Override
    public void onNegativeClick() {
        ToastUtils.toastShort(ThemeActivity.this,"onNegativeClick");
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

    //===========多盟广告实现监听方法实现=======================
    //如果开启了权限检查，需要重写次方法，并调用多盟视频sdk相应方法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        IndependentVideoManager.newInstance().onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    @Override
    public void videoDidStartLoad() {
        //进入播放界面 - 视频开始加载
        LogUtils.d(">>>>>", "demo videoDidStartLoad");
    }

    @Override
    public void videoDidFinishLoad(boolean b) {
        //进入播放界面 - 视频加载完成
        LogUtils.e(">>>>>", "demo videoDidFinishLoad");
    }

    @Override
    public void videoDidLoadError(String error) {
        //进入播放界面 - 视频加载失败
        LogUtils.d(TAG, "demo videoDidLoadError "+error);
        show_error();
    }

    @Override
    public void videoDidClosed() {
        //退出整个播放界面，返回本应用
        LogUtils.d(TAG, "demo videoDidClosed");
    }

    @Override
    public void videoCompletePlay() {
        //进入播放界面 - 视频播放完成，或手动关闭（视为用户完成了任务，可以获取奖励）
        LogUtils.d(TAG, "demo videoCompletePlay");
        //在此可给用户奖励
        Toast.makeText(this, "恭喜你完成任务", Toast.LENGTH_SHORT).show();
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
    public void videoPlayError(String s) {
        //进入播放界面 - 播放过程中出错
        LogUtils.d(TAG, "demo videoPlayError");
    }

    @Override
    public void videoWillPresent() {
        //进入播放界面 - 视频开始播放
        LogUtils.d(TAG, "demo videoWillPresent");
    }

    @Override
    public void videoVailable(IndependentVideoAvailableState independentVideoAvailableState) {
        switch (independentVideoAvailableState) {
            case VideoStateDownloading:
                show_caheing();
                break;
            case VideoStateFinishedCache:
                show_has_cache();
                break;
            case VideoStateNoExist:
                show_no_cache();
                break;
        }
    }

    /**
     * 直接播放视频
     *
     */
    public void play_video() {
        //直接播放视频
        IndependentVideoManager.newInstance().presentIndependentVideo(this);
    }

    /**
     * 检查可用缓存
     *
     */
    public void check() {
        IndependentVideoManager.newInstance().checkVideoAvailable(this);
    }

    public void show_caheing() {
        LogUtils.d(TAG,"缓存中");
    }

    public void show_error() {
        LogUtils.d(TAG,"错误");
    }

    public void show_no_cache() {
        LogUtils.d(TAG,"无缓存");
    }

    public void show_has_cache() {
        LogUtils.d(TAG,"有缓存");
    }

    @Override
    protected void onDestroy() {
        //移除监听
        IndependentVideoManager.newInstance().removeIndependentVideoListener(this);
        IndependentVideoManager.newInstance().exit(this);
        super.onDestroy();
    }
}
