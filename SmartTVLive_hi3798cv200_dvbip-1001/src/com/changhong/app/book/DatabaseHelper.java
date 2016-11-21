package com.changhong.app.book;

import java.io.File;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.changhong.app.dtv.P;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "ChannelBook.db";
	private static final int DB_VERSION = 1;

	private static final String DB_TABLE_BOOKINFO = "bookinfo";
	public static final String KEY_ID = "_id";
	public static final String BOOK_TIME_DAY = "Book_Day";
	public static final String BOOK_TIME_START = "Book_Time_Start";
	public static final String BOOK_EVENT_NAME = "Book_Envent_Name";
	public static final String BOOK_CHANNEL_NAME = "Book_Channel_Name";
	public static final String BOOK_CHANNEL_INDEX = "Book_Channel_Index";

	public static final String bookinfo_fileDir = "/data/changhong/dvb";

	private static DatabaseHelper mInstance;

	public static final String DB_CREATE_BOOK_INFO = "CREATE TABLE "
			+ DB_TABLE_BOOKINFO + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
			+ BOOK_TIME_DAY + " text not null, " + BOOK_TIME_START
			+ " text not null, " + BOOK_EVENT_NAME + " text not null, "
			+ BOOK_CHANNEL_NAME + " text not null, " + BOOK_CHANNEL_INDEX
			+ " integer);";

	public DatabaseHelper(Context context) {

		//super(context, DB_NAME, null, DB_VERSION);
		super(new CustomPathDatabaseContext(context, getDirPath()), DB_NAME, null, DB_VERSION);
	}

	 /**
     * 获取db文件在sd卡的路径
     * @return
     */
    private static String getDirPath(){
            //TODO 这里返回存放db的文件夹的绝对路径
            return bookinfo_fileDir;
    }
    
	public synchronized static DatabaseHelper getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new DatabaseHelper(context);
		}
		return mInstance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL(DB_CREATE_BOOK_INFO);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		P.e("database version update,need  onUpgrade !");
		db.execSQL("DROP TABLE IF EXISTS DB_TABLE_BOOKINFO");

		onCreate(db);
	}

}

// 添加对DB自定义路径的功能
class CustomPathDatabaseContext extends ContextWrapper {
	private String mDirPath;

	public CustomPathDatabaseContext(Context base, String dirPath) {
		super(base);
		this.mDirPath = dirPath;
	}

	@Override
	public File getDatabasePath(String name) {

		File result = new File(mDirPath + File.separator + name);

		if (!result.getParentFile().exists()) {

			result.getParentFile().mkdirs();

		}

		return result;
	}

	@Override
	public SQLiteDatabase openOrCreateDatabase(String name, int mode,
			CursorFactory factory) {
		return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name),
				factory);
	}

	@Override
	public SQLiteDatabase openOrCreateDatabase(String name, int mode,
			CursorFactory factory, DatabaseErrorHandler errorHandler) {
		return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name)
				.getAbsolutePath(), factory, errorHandler);
	}
}
