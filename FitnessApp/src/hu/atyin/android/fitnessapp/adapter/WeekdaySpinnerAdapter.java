package hu.atyin.android.fitnessapp.adapter;

import hu.atyin.android.fitnessapp.R;
import hu.atyin.android.fitnessapp.model.WeekdaySpinnerItem;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class WeekdaySpinnerAdapter extends BaseAdapter {

	private TextView tvWeekdayTitle;
	private TextView tvDateTitle;
	private ArrayList<WeekdaySpinnerItem> weekdaySpinnerItems;
	private Context context;
	
	public WeekdaySpinnerAdapter(Context context, ArrayList<WeekdaySpinnerItem> weekdaySpinnerItems) {
		this.context = context;
		this.weekdaySpinnerItems = weekdaySpinnerItems;
	}
	
	@Override
	public int getCount() {
		return weekdaySpinnerItems.size();
	}

	@Override
	public Object getItem(int position) {
		return weekdaySpinnerItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.weekday_spinner_item_ab_layout, parent, false);
        }
        
        tvWeekdayTitle = (TextView) convertView.findViewById(R.id.tvWeekdayTitle);
        tvDateTitle = (TextView) convertView.findViewById(R.id.tvDateTitle);
         
        tvWeekdayTitle.setText(weekdaySpinnerItems.get(position).getWeekday());
        tvDateTitle.setText(weekdaySpinnerItems.get(position).getDate());
        
        return convertView;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.weekday_spinner_item_layout, parent, false);
        }
         
		tvWeekdayTitle = (TextView) convertView.findViewById(R.id.tvWeekdayTitle);
        tvDateTitle = (TextView) convertView.findViewById(R.id.tvDateTitle);
         
        tvWeekdayTitle.setText(weekdaySpinnerItems.get(position).getWeekday());
        tvDateTitle.setText(weekdaySpinnerItems.get(position).getDate());
        
        return convertView;
	}
}
