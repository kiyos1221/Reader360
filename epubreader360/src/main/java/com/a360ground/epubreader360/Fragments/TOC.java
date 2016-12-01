package com.a360ground.epubreader360.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.a360ground.epubreader360.Adapters.TocAdapter;
import com.a360ground.epubreader360.R;
import com.a360ground.epubreader360.utils.TAGS;
import com.a360ground.epubreader360.utils.TinyDB;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

/**
 * A simple {@link Fragment} subclass.
 */
public class TOC extends Fragment implements AdapterView.OnItemClickListener{

    String bookname;
    public TOC() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle bundle = this.getArguments();
        bookname = bundle.getString(TAGS.BOOKNAME);
        View view = inflater.inflate(R.layout.fragment_toc, container, false);
        ListView listView = (ListView) view.findViewById(R.id.tocListView);
        listView.setOnItemClickListener(this);
        try {
            listView.setAdapter(new TocAdapter(getActivity(),getData()));
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return view;
    }
    public List<com.a360ground.epubreader360.Model.TOC> getData() throws ParserConfigurationException, SAXException, IOException {
        TinyDB tinyDB = new TinyDB(getActivity());
        ArrayList<String> arrayList = tinyDB.getListString(TAGS.CHAPTER_LIST);
        List<com.a360ground.epubreader360.Model.TOC> tocs = new ArrayList<>();
        for(int i=0;i<arrayList.size();i++){
            com.a360ground.epubreader360.Model.TOC toc = new com.a360ground.epubreader360.Model.TOC();
            toc.setChapterName(arrayList.get(i));
            tocs.add(toc);
        }
        return tocs;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent=new Intent();
        intent.putExtra("MESSAGE",i);
        getActivity().setResult(5,intent);
        getActivity().finish();
    }
}
