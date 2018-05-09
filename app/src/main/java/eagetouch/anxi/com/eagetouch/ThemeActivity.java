package eagetouch.anxi.com.eagetouch;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import eagetouch.anxi.com.eagetouch.server.EdgeTouchService;
import eagetouch.anxi.com.eagetouch.utils.PreferenceUtils;

/**
 * Created by user on 5/9/18.
 */

public class ThemeActivity extends AppCompatActivity {
    private final String TAG = "=ThemeActivity";

    ListView mLvTheme;
    ThemeAdapter mAdapter;
    LayoutInflater mInflater;

    private String[] mThemeColors;
    private String[] mThemeTitles;
    private int mSelectcolorIndex;
    ArrayList<ThemeData> mDataList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);
        mLvTheme = (ListView) this.findViewById(R.id.list_theme);
        initList();
    }

    private void initList() {
        mInflater  = LayoutInflater.from(this);
        mThemeColors = this.getResources().getStringArray(R.array.theme_colors);
        mThemeTitles = this.getResources().getStringArray(R.array.theme_colors_title);
        mSelectcolorIndex = PreferenceUtils.getThemeColor(0);

        mDataList = new ArrayList<ThemeData>();
        ThemeData data = null;
        for(int i = 0;i<mThemeColors.length;i++){
            if(i == mSelectcolorIndex){
                data = new ThemeData(mThemeColors[i],mThemeTitles[i],true);
            }else{
                data = new ThemeData(mThemeColors[i],mThemeTitles[i],false);
            }
            mDataList.add(data);
        }

        mAdapter = new ThemeAdapter();
        mLvTheme.setAdapter(mAdapter);
        mLvTheme.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for(int i =0;i<mDataList.size();i++){
                    mDataList.get(i).isSelect = false;
                }
                mDataList.get(position).isSelect = true;
                mAdapter.notifyDataSetChanged();
                PreferenceUtils.setThemeColor(position);
                Intent intent = new Intent();
                intent.setPackage("eagetouch.anxi.com.eagetouch");
                intent.setClass(ThemeActivity.this,EdgeTouchService.class);
                intent.setAction(EdgeTouchService.ACTION_RESET_THEME);
                startService(intent);
            }
        });
    }

    class ThemeData{
        String color;
        String colorTitile;
        boolean isSelect = false;

        public ThemeData(String color,String colorTitile,Boolean isSelect){
            this.color = color;
            this.colorTitile = colorTitile;
            this.isSelect = isSelect;
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
            /*view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mSelectcolorIndex != id) {
                        for(int i =0;i<mThemeColors.length;i++){

                        }
                        mSelectcolorIndex = id;
                        View last = mSelectedView[mSelectcolorIndex];
                        if (last != null) {
                            last.setVisibility(View.GONE);
                        }
                        itemHolder.selectedItem.setVisibility(View.VISIBLE);
                        mSelectcolorIndex = id;
                        PreferenceUtils.setThemeColor(id);
                    }
                }
            });*/
            return view;
        }

        private class ViewHolderItem {
            private TextView tvItem;
            private ImageView imgItem;
            private ImageView selectedItem;
        }
    }
}
