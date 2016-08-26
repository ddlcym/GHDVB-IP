package com.changhong.app.book.copy;

import java.util.Vector;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.changhong.app.dtv.Common;

public class BookDataBase {

	private static final String DB_TABLE_BOOKINFO = "bookinfo";

	public static final String KEY_ID = "_id";
	public static final String BOOK_TIME_DAY = "Book_Day";
	public static final String BOOK_TIME_START = "Book_Time_Start";
	public static final String BOOK_EVENT_NAME = "Book_Envent_Name";
	public static final String BOOK_CHANNEL_NAME = "Book_Channel_Name";
	public static final String BOOK_CHANNEL_INDEX = "Book_Channel_Index";

	private Context mContext = null;

	private static SQLiteDatabase mSQLiteDatabase = null;
	private DatabaseHelper mDatabaseHelper = null;

	private static Vector<BookInfo> mso_BookInfo;
	
	
	static {
		
		mso_BookInfo = new Vector<BookInfo>();	
		
	}
	public BookDataBase(Context context) {
		mContext = context;
		mDatabaseHelper = DatabaseHelper.getInstance(mContext);
		mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();
	}

	public void close() {
		mDatabaseHelper.close();
	}


	public String completeBookInfoUpdateSQL(String Day, String Time_Start,
			String Envent_Name, String Channel_Name, int Channel_Index) {

		String SQL = null;
		SQL = "update " + DB_TABLE_BOOKINFO + " set " + "Book_Day = " + "'"
				+ Day + "'" + ", " + "Book_Time_Start = " + "'" + Time_Start
				+ "'" + ", " + "Book_Envent_Name = " + "'" + Envent_Name + "'"
				+ ", " + "Book_Channel_Name = " + "'" + Channel_Name + "'"
				+ ", " + "Book_Channel_Index = " + Channel_Index + " "
				+ "where " + "Book_Day" + " = " + "'" + Day + "'" + " AND "
				+ "Book_Time_Start" + " = " + "'" + Time_Start + "'" + ";";
		return SQL;

	}

	public String completeBookInfoInsertSQL(String Day, String Time_Start,
			String Envent_Name, String Channel_Name, int Channel_Index) {

		String SQL = null;

		SQL = "insert into "
				+ DB_TABLE_BOOKINFO
				+ " (Book_Day, Book_Time_Start, Book_Envent_Name, Book_Channel_Name, Book_Channel_Index) "
				+ "values(" + "'" + Day + "'" + ", " + "'" + Time_Start + "'"
				+ ", " + "'" + Envent_Name + "'" + ", " + "'" + Channel_Name
				+ "'" + ", " + Channel_Index + ");";
		return SQL;
	}

	public boolean isThisBookInfoExist(BookInfo data) {
		boolean ret = false;
		Cursor BookInfoCur = null;
		if (data != null) {
			BookInfoCur = mSQLiteDatabase
					.query(DB_TABLE_BOOKINFO, null, "Book_Day" + " = " + "'"
							+ data.bookDay + "'" + " AND " + "Book_Time_Start"
							+ " = " + "'" + data.bookTimeStart + "'", null,null, null, null);
			if (BookInfoCur != null) {
				if (BookInfoCur.getCount() > 0) {
					ret = true;
				} else {
					ret = false;
				}
			}
			BookInfoCur.close();
		}
		return ret;
	}

	public boolean BookInfoUpdate(BookInfo data) {
		boolean flag = false;
		if (data != null) {
			boolean isExistFlag = false;
			String SQL = null;
			isExistFlag = isThisBookInfoExist( data);
			if (isExistFlag) {
				// updata
				SQL = completeBookInfoUpdateSQL(data.bookDay,
						data.bookTimeStart, data.bookEnventName,
						data.bookChannelName, data.bookChannelIndex);
				Common.LOGI( "updata == " + SQL);
				
			} else {
				// insert
				SQL = completeBookInfoInsertSQL(data.bookDay,
						data.bookTimeStart, data.bookEnventName,
						data.bookChannelName, data.bookChannelIndex);
			}
			try {
				mSQLiteDatabase.execSQL(SQL);
			} catch (SQLException e) {
				Common.LOGE(e.toString());
				Common.LOGE("ChannelUpdate>>" + SQL);
			}
			flag = true;
		}
		return flag;
	}

	private static boolean writeBookInfoToDataStruct() {
		boolean b_Succes = false;
		Cursor curCarrier;


		curCarrier = mSQLiteDatabase.query(DB_TABLE_BOOKINFO, null, null, null, null,
				null, null);
		if (curCarrier.getCount() > 0) {
			curCarrier.moveToFirst();
			while (true) {

				BookInfo data = new BookInfo();
				int columnIndex;
				columnIndex = curCarrier.getColumnIndex("Book_Day");
				data.bookDay = curCarrier.getString(columnIndex);

				columnIndex = curCarrier.getColumnIndex("Book_Time_Start");
				data.bookTimeStart = curCarrier.getString(columnIndex);

				columnIndex = curCarrier.getColumnIndex("Book_Envent_Name");
				data.bookEnventName = curCarrier.getString(columnIndex);

				columnIndex = curCarrier.getColumnIndex("Book_Channel_Name");
				data.bookChannelName = curCarrier.getString(columnIndex);

				columnIndex = curCarrier.getColumnIndex("Book_Channel_Index");
				data.bookChannelIndex = curCarrier.getInt(columnIndex);
				mso_BookInfo.add(data);

				/*
				 * ContentValues cv = new ContentValues(); Integer value = new
				 * Integer(mso_CarrierInfo.indexOf(data)); cv.clear();
				 * cv.put(ITEMNAME_VECTORID, value);
				 * mso_Db.update(TABLENAME_CARRIER, cv, ITEMNAME_TSID + " = " +
				 * data.i_TsID + " AND " + ITEMNAME_NETID + " = " +
				 * data.i_OrgNetId, null);
				 */

				if (curCarrier.isLast()) {
					break;
				}
				curCarrier.moveToNext();
			}
		}
		curCarrier.close();
		return b_Succes;
	}

	public boolean BookInfoCommit(BookInfo data) {

		boolean flag = false;
		flag = BookInfoUpdate(data);
		if (true == flag) {
			writeBookInfoToDataStruct();
		}
		return flag;
	}

	public int BookInfoNum() {
		int i_Cnt = 0;
		Cursor cur;
		cur = mSQLiteDatabase.query(DB_TABLE_BOOKINFO, null, null, null, null, null,
				null);
		i_Cnt = cur.getCount();
		cur.close();
		return i_Cnt;
	}

	public Vector<BookInfo> GetBookInfo() {

		Cursor curCarrier;
		mso_BookInfo.clear();
		curCarrier = mSQLiteDatabase.query("bookinfo", null, null, null, null, null,
				null);
		if (curCarrier.getCount() > 0) {
			curCarrier.moveToFirst();
			while (true) {

				BookInfo data = new BookInfo();
				int columnIndex;
				columnIndex = curCarrier.getColumnIndex("Book_Day");
				data.bookDay = curCarrier.getString(columnIndex);
				columnIndex = curCarrier.getColumnIndex("Book_Time_Start");
				data.bookTimeStart = curCarrier.getString(columnIndex);
				columnIndex = curCarrier.getColumnIndex("Book_Envent_Name");
				data.bookEnventName = curCarrier.getString(columnIndex);
				columnIndex = curCarrier.getColumnIndex("Book_Channel_Name");
				data.bookChannelName = curCarrier.getString(columnIndex);
				columnIndex = curCarrier.getColumnIndex("Book_Channel_Index");
				data.bookChannelIndex = curCarrier.getInt(columnIndex);
				mso_BookInfo.add(data);
				if (curCarrier.isLast()) {
					break;
				}
				curCarrier.moveToNext();
			}
			curCarrier.close();
		}
		return mso_BookInfo;
	}

	public void RemoveOneBookInfo(String BookDate, String StartTime) {
		String where = "Book_Day" + "=" + "'" + BookDate + "'" + " AND "
				+ "Book_Time_Start" + "=" + "'" + StartTime + "'";

		int i_Affected = mSQLiteDatabase.delete(DB_TABLE_BOOKINFO, where, null);
		Common.LOGI("i_Affected = " + i_Affected);
		if (i_Affected > 0) {
			writeBookInfoToDataStruct();
		}
	}
}