package com.a360ground.epubreader360.Database;

import android.content.Context;
import android.util.Log;

import com.a360ground.epubreader360.Model.DatabaseModels.Bookmark;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Kiyos Solomon on 11/7/2016.
 */
public class BookmarkTable {

    private static final String Tag = "BookmarkTable";
    public static int createEntryInTableIfNotExist(Context context, Bookmark bookmark) {
        int status = -1;
        try {
            status = LomiReaderDB.getInstance(context).getBookmarksDao().create(bookmark);
        } catch (SQLException e) {
            Log.d(Tag , e.getMessage());
        }

        return status;
    }
    public static long getNoOfRowsInTable(Context context) throws SQLException {
        return LomiReaderDB.getInstance(context).getBookmarksDao().queryBuilder().countOf();
    }
    public static List<Bookmark> getAllRecords(Context context) {
        List<Bookmark> records = null;
        try {
            records = LomiReaderDB.getInstance(context).getBookmarksDao().queryForAll();
        } catch (SQLException e) {
            Log.d(Tag, e.getMessage());
        }

        return records;
    }
    public static int deleteAllBookmark(Context context) {
        int count = -1;
        try {
            Dao<Bookmark, Integer> dao = LomiReaderDB.getInstance(context).getBookmarksDao();
            TableUtils.clearTable(dao.getConnectionSource(), Bookmark.class);
            TableUtils.dropTable(dao.getConnectionSource(), Bookmark.class, true);
            TableUtils.createTableIfNotExists(dao.getConnectionSource(), Bookmark.class);
        } catch (SQLException e) {
            count = -1;
        }
        return count;
    }
    public static void save(Context context, Bookmark Bookmark) {
        if (Bookmark != null) {
            createEntryInTableIfNotExist(context, Bookmark);
        } else {
            Log.d(Tag + "SAVE:", "can't save null object");
        }
    }
    public static List<Bookmark> getAllBookmark(Context context, String bookId, int pageNo) {
        List<Bookmark> Bookmarks = null;
        try {
            Dao<Bookmark, Integer> dao = LomiReaderDB.getInstance(context).getBookmarksDao();
            Bookmarks = dao.queryBuilder().where()
                    .eq(Bookmark.LOCAL_DB_BOOKMARK_ID, bookId).and()
                    .eq(Bookmark.LOCAL_DB_BOOKMARK_PAGE, pageNo).query();
        } catch (SQLException e) {
            Log.d(Tag, e.getMessage());
        }
        return Bookmarks;
    }
    public static void remove(int BookmarkId, Context context) {
        int status = -1;
        try {
            Dao<Bookmark, Integer> dao = LomiReaderDB.getInstance(context).getBookmarksDao();
            DeleteBuilder<Bookmark, Integer> deleteBuilder = dao.deleteBuilder();
            deleteBuilder.where().eq(Bookmark.LOCAL_DB_BOOKMARK_ID, BookmarkId);
            status = deleteBuilder.delete();
        } catch (SQLException e) {
            Log.d(Tag, e.getMessage());
        }
        if (status > 0) {
            Log.d(Tag + "Remove:", "no of records removed" + status);
        } else {
            Log.d(Tag + "Remove:", "can't remove");
        }
    }
    public static List<Bookmark> getBookBookmark(Context context, String bookname) {
        List<Bookmark> records = null;
        try {
            records = LomiReaderDB.getInstance(context).getBookmarksDao()
                    .queryBuilder().where().eq("bookName", bookname).query();
        } catch (SQLException e) {
            Log.d(Tag, e.getMessage());
        }
        return records;
    }


}
