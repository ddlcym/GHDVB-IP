package com.changhong.app.dtv;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.app.book.BookInfo;
import com.changhong.app.constant.Class_Constant;
import com.changhong.app.constant.Class_Global;

public class EpgManage extends Activity implements OnItemSelectedListener,
		OnItemClickListener, OnFocusChangeListener {
	/**
	 * @sunyuanming 节目预约的查看,删除等操作
	 */
	private ListView bookList;
	private ImageView imageFocus;
	private TextView bookDateView;
	private TextView bookInfoView;
	
	private int pageCount = 7;
	private int pageSum = 0;
	private int itemIndex = 0;
	private int pageIndex = 1;
	private int start = 0;
	private int end = 0;
	public int[] margLeft = { 54, 163, 216, 240, 224, 168, 54 };
	public int[] margTop = { 52, 132, 222, 320, 428, 528, 626 };
	private SysApplication objApplication;

	private List<BookInfo> list;
	private boolean listFocus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.epgmanage);
		objApplication = SysApplication.getInstance();
		findView();
		if (objApplication.queryBookChannels()==null) {
			Toast BookToast = new Toast(getApplicationContext());
			BookToast = Toast.makeText(getApplicationContext(),
					getString(R.string.epg_no_book), Toast.LENGTH_LONG);
			BookToast.setGravity(Gravity.CENTER, 0, 0);
			BookToast.show();
			finish();
		} else {
			showBookManage();
		}
	}

	private void showBookManage() {
		init();
		showTextInfo();
        bookList.setAdapter(new BookAdapter(EpgManage.this, list.subList(start, end)));
	}

	private void init() {
		list = new ArrayList<BookInfo>();
		list.clear();
		Vector<BookInfo> bookInfos = objApplication.queryBookChannels();
		Common.LOGI("节目总数:"+bookInfos.size());
		
		for (BookInfo bookInfo : bookInfos) {
			list.add(bookInfo);
		}
		Common.LOGI("list总数:"+list.size());
		   pageSum = list.size() / pageCount;
			if (list.size() % pageCount > 0) {
				pageSum++;
			}

			start = 0;
			if (list.size() < pageCount) {
				end = list.size();
			} else {
				end = pageCount;
			}
			itemIndex = 0;
			pageIndex = 1;
		
	}
	private void showTextInfo() {
		String textInfo = new String();
		textInfo = getResources().getString(R.string.epgbook_pagenum_text)
				+ Integer.toString(list.size()) + "    "
				+ Integer.toString(pageIndex) + "/"
				+ Integer.toString(pageSum);
		bookInfoView.setText(textInfo);
	}

	private void findView() {
		bookList = (ListView) findViewById(R.id.book_program_list_id);
		imageFocus = (ImageView) findViewById(R.id.bookprogram_FocusImageID);
		bookDateView = (TextView) findViewById(R.id.bookprogram_date_id);
		bookInfoView = (TextView) findViewById(R.id.book_page_info_id);
	    bookList.setOnItemSelectedListener(this);
	    bookList.setOnItemClickListener(this);
	    bookList.setOnFocusChangeListener(this);
	}
    
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (objApplication.queryBookChannels()==null) {
		
		finish();
		}
	}

	@Override
	public void onFocusChange(View arg0, boolean arg1) {
		// TODO Auto-generated method stub
            listFocus=arg1;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
        BookInfo bookInfo=list.get(arg2+(pageIndex-1)*pageCount);
        list.remove(bookInfo);
        objApplication.delBookChannel(bookInfo.bookDay, bookInfo.bookTimeStart);
        
        if (objApplication.queryBookChannels()==null) {
			Toast BookToast = new Toast(getApplicationContext());
			BookToast = Toast.makeText(getApplicationContext(),
					getString(R.string.epg_no_book), Toast.LENGTH_LONG);
			BookToast.setGravity(Gravity.CENTER, 0, 0);
			BookToast.show();
			finish();
		} else {
        init();
        if(list.size()>0)
        {
        	pageSum=list.size()/pageCount;
        	if(list.size()%pageCount>0)
        	{
        		pageSum++;
        		
        	}
        	if(pageSum<pageIndex)
        	{
        		pageIndex--;
        	}
        	start=(pageIndex-1)*pageCount;
        	end=pageIndex*pageCount;
        	if(end>list.size())
        	{
        		end=list.size();
        	}
        	
        	
        }
        else {
			pageIndex=0;
			imageFocus.setVisibility(View.INVISIBLE);
		}
        showTextInfo();
        bookList.setAdapter(new BookAdapter(EpgManage.this, list.subList(start, end)));
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
           itemIndex=arg2;
           bookDateView.setText(list.get(itemIndex+(pageIndex-1)*pageCount).bookDay);
           LayoutParams params=new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
           params.leftMargin=margLeft[itemIndex%pageCount];
           params.topMargin=margTop[itemIndex%pageCount];
           imageFocus.setLayoutParams(params);
           imageFocus.bringToFront();
           imageFocus.setVisibility(View.VISIBLE);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}
    
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (keyCode) {
		case Class_Constant.KEYCODE_UP_ARROW_KEY:
			if(listFocus)
			{
				itemIndex--;
				if(itemIndex<0)
				{
					itemIndex=0;
					pageIndex--;
					if(pageIndex<=0)
					{
						pageIndex=1;						
					}					
				}
				start=(pageIndex-1)*pageCount;
				end=pageIndex*pageCount;
				if(end>list.size())
				{
					end=list.size();
				}
				showTextInfo();
				bookList.setAdapter(new BookAdapter(EpgManage.this, list.subList(start, end)));
			}
			break;

		case Class_Constant.KEYCODE_DOWN_ARROW_KEY:
			if(listFocus)
			{
				itemIndex++;
				if(itemIndex>=pageCount)
				{
					itemIndex=0;
					pageIndex++;
					if(pageIndex>pageSum)
					{
						pageIndex=1;
					}
				}
				if(((pageIndex-1)*pageCount+itemIndex)>=list.size())
				{
					itemIndex=0;
					pageIndex=1;
				}
				start=(pageIndex-1)*pageCount;
				end=pageIndex*pageCount;
				if(end>list.size())
				{
					end=list.size();
				}
				showTextInfo();
				bookList.setAdapter(new BookAdapter(EpgManage.this, list.subList(start, end)));
			}
			break;
			
		case Class_Constant.KEYCODE_BACK_KEY:
			Class_Global.SetRootMenuVKey(Class_Constant.KEYCODE_INFO_KEY);	
			
			break;
			
		}
		return super.onKeyDown(keyCode, event);
	}

	public class BookAdapter extends BaseAdapter {
		private Context mContext;
		private List<BookInfo> bookInfos;
		private LayoutInflater inflater;
		private int[] posX = { 100, 210, 263, 286, 270, 215, 100 };
		private int[] posY = { 15, 50, 60, 70, 76, 70, 60 };

		public BookAdapter(Context context, List<BookInfo> bookInfos) {
			this.mContext = context;
			this.bookInfos = bookInfos;
			inflater = LayoutInflater.from(mContext);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return bookInfos.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return bookInfos.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			// TODO Auto-generated method stub
			convertView = inflater.inflate(R.layout.bookitem, null);
			TextView bookInfoView = (TextView) convertView
					.findViewById(R.id.book_program_content_id);
			LinearLayout infoLayout = (LinearLayout) convertView
					.findViewById(R.id.linout_book_item_id);
			infoLayout.setPadding(posX[position], posY[position], 0, 0);
			bookInfoView.setText(bookInfos.get(position).bookTimeStart + "    "
					+ bookInfos.get(position).bookChannelName + "/"
					+ bookInfos.get(position).bookEnventName);
			convertView.setTag(bookInfoView);
			return convertView;
		}

	}
}
