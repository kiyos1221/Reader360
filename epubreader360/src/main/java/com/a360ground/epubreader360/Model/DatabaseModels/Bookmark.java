package com.a360ground.epubreader360.Model.DatabaseModels;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Kiyos Solomon on 11/7/2016.
 */
@DatabaseTable
public class Bookmark {
    public static final String LOCAL_DB_BOOKMARK_PAGE = "page";
    public static final String LOCAL_DB_BOOKMARK_ID = "id";

    @DatabaseField(generatedId = true)
    int id;
    @DatabaseField
    int position;
    @DatabaseField
    String text;
    @DatabaseField
    String bookName;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }
}
