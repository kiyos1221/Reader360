package com.a360ground.epubreader360.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.a360ground.epubreader360.Model.TOC;
import com.a360ground.epubreader360.R;

import java.util.List;

/**
 * Created by Kiyos Solomon on 11/5/2016.
 */
public class TocAdapter extends BaseAdapter{
    Context context;
    List<TOC> tocList;

    public TocAdapter(Context context, List<TOC> tocList) {
        this.context = context;
        this.tocList = tocList;
    }

    @Override
    public int getCount() {
        return tocList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View view1 = LayoutInflater.from(context).inflate(R.layout.single_toc,viewGroup,false);
        TextView textView = (TextView) view1.findViewById(R.id.chapter);
        TOC toc = tocList.get(i);
        textView.setText(toc.getChapterName());
        return view1;
    }
}
