package com.acctrue.jlyj.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.acctrue.jlyj.R;
import com.acctrue.jlyj.entity.lv_item3;

public class FMAdapter3 extends BaseAdapter{

	
	public int selectIndex = -1;
	
	//存放数据的List
	private ArrayList<lv_item3> itemList;
		
	//用来导入布局
	private LayoutInflater inflater = null;
	
	
	//构造函数
	public FMAdapter3 (ArrayList<lv_item3>list, Context context)
	{
		 
        this.itemList = list;  
 
        inflater = LayoutInflater.from(context);
		
	}
	
	//viewholder
	public static class ViewHolder {  
		 
        TextView tv_0;
        
        TextView tv_1;
        
        TextView tv_2;
 
        TextView tv_4;
        
        TextView tv_7;
 
    }
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return itemList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return itemList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null; 
		
		if (convertView == null) {
			holder = new ViewHolder();  
			 
            // 导入布局并赋值给convertview  
            convertView = inflater.inflate(R.layout.lv_item3, null);
            
//            if (position == selectIndex) {
//				convertView.setBackgroundColor(Color.parseColor("#FF8800"));
//			}
            
            holder.tv_0 = (TextView) convertView.findViewById(R.id.dcoument_tv_1);
            
            holder.tv_1 = (TextView) convertView.findViewById(R.id.dcoument_tv_2);
            
            holder.tv_2 = (TextView) convertView.findViewById(R.id.dcoument_tv_3);
 
            holder.tv_4 = (TextView) convertView.findViewById(R.id.dcoument_tv_5);
            
            holder.tv_7 = (TextView) convertView.findViewById(R.id.dcoument_tv_8);
            // 为view设置标签  
 
            convertView.setTag(holder);
		} else {  
			 
            // 取出holder  
 
            holder = (ViewHolder) convertView.getTag();  
 
		}
		
		// 设置list中TextView的显示  
        
		 
        holder.tv_0.setText(itemList.get(position).item_1);
        
        holder.tv_1.setText(itemList.get(position).item_2);	
        
        holder.tv_2.setText(itemList.get(position).item_3);
        
        holder.tv_4.setText(itemList.get(position).item_5);
        
        holder.tv_7.setText(itemList.get(position).item_8);
		
		return convertView;
	}

}
