package com.acctrue.jlyj.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.acctrue.jlyj.R;
import com.acctrue.jlyj.activity.DialogShowItemClicker;
import com.acctrue.jlyj.entity.lv_item2;

public class FMAdapter2 extends BaseAdapter{

	
	public int selectIndex = -1;
	
	//������ݵ�List
	private ArrayList<lv_item2> itemList;
		
	//�������벼��
	private LayoutInflater inflater = null;
	
	
	//���캯��
	public FMAdapter2 (ArrayList<lv_item2>list, Context context)
	{
		 
        this.itemList = list;  
 
        inflater = LayoutInflater.from(context);
		
	}
	
	//viewholder
	public static class ViewHolder {  
		 
        TextView tv_0;
        
        TextView tv_1;
        
        TextView tv_2;
 
        TextView tv_5;
        
        TextView tv_6;
        
    public    TextView tv_7;
 
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
			 
            // ���벼�ֲ���ֵ��convertview  
            convertView = inflater.inflate(R.layout.lv_item2, null);
            
            DialogShowItemClicker clicker = new DialogShowItemClicker(
					convertView.getContext());
			clicker.setAdapter(this);
			convertView.setOnClickListener(clicker);
            final AbsListView.LayoutParams lp = new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT, 100);
            convertView.setLayoutParams(lp);
//            if (position == selectIndex) {
//				convertView.setBackgroundColor(Color.parseColor("#FF8800"));
//			}
            
            holder.tv_0 = (TextView) convertView.findViewById(R.id.dcoument_tv_1);
            
            holder.tv_1 = (TextView) convertView.findViewById(R.id.dcoument_tv_2);
            
            holder.tv_2 = (TextView) convertView.findViewById(R.id.dcoument_tv_3);
 
            holder.tv_5 = (TextView) convertView.findViewById(R.id.dcoument_tv_6);
            
            holder.tv_6 = (TextView) convertView.findViewById(R.id.dcoument_tv_7);
            
            holder.tv_7 = (TextView) convertView.findViewById(R.id.dcoument_tv_8);
            // Ϊview���ñ�ǩ  
 
            convertView.setTag(holder);
		} else {  
			 
            // ȡ��holder  
 
            holder = (ViewHolder) convertView.getTag();  
 
		}

		holder.tv_7.setTag(itemList.get(position));
		
		// ����list��TextView����ʾ  
        
		 
        holder.tv_0.setText(itemList.get(position).item_1);
        
        holder.tv_1.setText(itemList.get(position).item_2);	
        
        holder.tv_2.setText(itemList.get(position).item_3);
        
        holder.tv_5.setText(itemList.get(position).item_6);
        
        holder.tv_6.setText(itemList.get(position).item_7);
        
        holder.tv_7.setText(itemList.get(position).item_8);
		
		return convertView;
	}

}
