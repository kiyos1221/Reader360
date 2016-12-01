package com.a360ground.epubreader360.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.a360ground.epubreader360.Model.DatabaseModels.Bookmark;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;

import java.io.File;
import java.io.InputStream;
import java.sql.SQLException;

/**
 * Created by Kiyos Solomon on 11/7/2016.
 */
public class LomiReaderDB extends OrmLiteSqliteOpenHelper{

    private static final String DBName = "LomiReader";
    private static final int DBVersion = 2;
    private Dao<Bookmark,Integer> bookmarksDao;
    private static LomiReaderDB mInstance;
    public LomiReaderDB(Context context){
        super(context,DBName,null,DBVersion);
    }
    public LomiReaderDB(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int databaseVersion) {
        super(context, databaseName, factory, databaseVersion);
    }

    public LomiReaderDB(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int databaseVersion, int configFileId) {
        super(context, databaseName, factory, databaseVersion, configFileId);
    }
    public static LomiReaderDB getInstance(Context context){
        if(mInstance == null) {
            mInstance = OpenHelperManager.getHelper(context, LomiReaderDB.class);
        }
        return mInstance;
    }

    public LomiReaderDB(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int databaseVersion, File configFile) {
        super(context, databaseName, factory, databaseVersion, configFile);
    }
    public Dao<Bookmark,Integer> getBookmarksDao() throws SQLException {
        if(bookmarksDao==null){
            bookmarksDao = getDao(Bookmark.class);
        }
        return bookmarksDao;
    }

    public LomiReaderDB(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int databaseVersion, InputStream stream) {
        super(context, databaseName, factory, databaseVersion, stream);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, com.j256.ormlite.support.ConnectionSource connectionSource) {

        try {
            TableUtils.createTableIfNotExists(connectionSource, Bookmark.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, com.j256.ormlite.support.ConnectionSource connectionSource, int i, int i1) {

        try {
            TableUtils.dropTable(connectionSource,Bookmark.class,true);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
