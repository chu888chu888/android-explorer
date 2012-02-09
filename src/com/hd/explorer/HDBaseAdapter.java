/*
 * Copyright (c) 2011 yang hui <yanghui1986527@gmail.com>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License v2 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 021110-1307, USA.
 */

package com.hd.explorer;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.hd.Constant;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * ClassName:HDBaseAdapter
 * Function: TODO ADD FUNCTION
 * Reason:	 TODO ADD REASON
 *
 * @author   snowdream
 * @version  
 * @since    Ver 1.1
 * @Date	 2011-11-30		下午11:06:18
 *
 * @see 	 
 */
public class HDBaseAdapter extends BaseAdapter {

	private Context mcontext = null;
	private List<File> mfiles = null;

	public HDBaseAdapter(Context context, List<File> files) {
		mcontext = context;
		mfiles = files;
	}

	@Override
	public int getCount() {
		int msize = 0;

		if(mfiles != null)
			msize = mfiles.size();

		return msize;
	}

	@Override
	public File getItem(int position) {

		if((position >= 0) && (position < this.getCount()))
			return mfiles.get(position);	

		return null;
	}

	@Override
	public long getItemId(int position) {

		return position;
	}


	@Override
	public void notifyDataSetChanged() {

		super.notifyDataSetChanged();

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if(convertView==null)
		{
			convertView=LayoutInflater.from(mcontext).inflate(R.layout.item_row, null);
			holder=new Holder();
			holder.mfileIcon=(ImageView) convertView.findViewById(R.id.fileicon);
			holder.mfileName=(TextView) convertView.findViewById(R.id.filename);
			holder.mfileSize=(TextView) convertView.findViewById(R.id.filesize);
			holder.mfileTime=(TextView) convertView.findViewById(R.id.filetime);
			convertView.setTag(holder);
		}else
		{
			holder=(Holder) convertView.getTag();
		}

		//update the holder
		File f = this.getItem(position);
		if(f != null){
			int icon = this.getFileIcon(f);

			if(icon == -1){
				Drawable drawable = this.getApkIcon(f.getAbsolutePath()); //耗时间
				if(drawable != null){
					holder.mfileIcon.setImageDrawable(drawable);
				}
				else{
					holder.mfileIcon.setImageResource(R.drawable.icon_file);
				}
			}else{
				holder.mfileIcon.setImageResource(icon);
			}
			//holder.mfileIcon.setImageResource(R.drawable.icon_file);
			long start = System.currentTimeMillis();


			holder.mfileName.setText(f.getName()); //耗时间

			if(f.isFile()){

				holder.mfileSize.setText(this.getFileSize(f.length()));
			}else {
				holder.mfileSize.setText(""); //耗时间

			}
			long end = System.currentTimeMillis();

			Log.i("convertView","TIME: "+ String.valueOf((end - start)*0.001));
			holder.mfileTime.setText(this.getFileTime(f.lastModified()));

		}

		return convertView;
	}

	public int getFileIcon(File f) {
		int icon = 0;

		String str = Constant.getFileIcon(f);
		if(str == null)
		{
			icon = -1;
		}
		else{
			Resources res = mcontext.getResources();  
			icon =res.getIdentifier(str,"drawable",mcontext.getPackageName());  
			
			if(icon <= 0 )
				icon = R.drawable.icon_file;
		}

		return icon;
	}

	public Drawable getApkIcon(String path){      
		PackageManager pm = mcontext.getPackageManager();      
		PackageInfo info = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);      
		if(info != null){      
	   		 ApplicationInfo appInfo = info.applicationInfo;
	   		 
	   		 if(Build.VERSION.SDK_INT >= 8){
	   			appInfo.sourceDir = path;
	   			appInfo.publicSourceDir = path;
	   		 }
	   		 
	   		 return appInfo.loadIcon(pm);
		}   		
		return null;
	}

	public String getFileTime(long filetime) {
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss"); 
		String ftime =  formatter.format(new Date(filetime)); 
		return ftime;
	}

	public String getFileSize(long filesize) {
		DecimalFormat df = new DecimalFormat("#.00");
		StringBuffer mstrbuf = new StringBuffer();

		if (filesize < 1024) {
			mstrbuf.append(filesize);
			mstrbuf.append(" B");
		} else if (filesize < 1048576) {
			mstrbuf.append(df.format((double)filesize / 1024));
			mstrbuf.append(" K");			
		} else if (filesize < 1073741824) {
			mstrbuf.append(df.format((double)filesize / 1048576));
			mstrbuf.append(" M");			
		} else {
			mstrbuf.append(df.format((double)filesize / 1073741824));
			mstrbuf.append(" G");
		}

		df = null;

		return mstrbuf.toString();
	} 

	class Holder{
		ImageView mfileIcon;
		TextView mfileName;
		TextView mfileSize;
		TextView mfileTime;
	}
}

