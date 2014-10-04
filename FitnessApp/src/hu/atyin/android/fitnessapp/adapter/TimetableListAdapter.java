package hu.atyin.android.fitnessapp.adapter;

import hu.atyin.android.fitnessapp.R;
import hu.atyin.android.fitnessapp.model.TimetableEvent;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TimetableListAdapter extends ArrayAdapter<TimetableEvent>{

	private Context context;
	private ArrayList<TimetableEvent> timetableEvents;
	
	public TimetableListAdapter(Context context, int resource, ArrayList<TimetableEvent> timetableEvents) {
		super(context, resource, timetableEvents);
		
		this.context = context;
		this.timetableEvents = timetableEvents;
	}

	static class ViewHolder {
		public TextView tvReservationTime;
		public TextView tvReservationTrainerName;
		public TextView tvReservationTrainingName;
		public TextView tvReservationFreeSpots;
		public ImageView imgIsReserved;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		ViewHolder holder = null;
		
		if(rowView == null) {
			LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.timetable_row_layout, parent, false);
			
			holder = new ViewHolder();
			
			holder.tvReservationTime = (TextView) rowView.findViewById(R.id.tvReservationTime);
			holder.tvReservationTrainerName = (TextView) rowView.findViewById(R.id.tvReservationTrainerName);
			holder.tvReservationTrainingName = (TextView) rowView.findViewById(R.id.tvReservationTrainingName);
			holder.tvReservationFreeSpots = (TextView) rowView.findViewById(R.id.tvReservationFreeSpots);
			holder.imgIsReserved = (ImageView) rowView.findViewById(R.id.imgIsReserved);
			
			rowView.setTag(holder);
		}
		else {
			holder = (ViewHolder) rowView.getTag();
		}
		
		final TimetableEvent timetableEvent = timetableEvents.get(position);
		
		holder.tvReservationTime.setText(String.format("%s - %s", timetableEvent.getStartTime(), timetableEvent.getEndTime()));
		holder.tvReservationTrainerName.setText(timetableEvent.getTrainerName());
		holder.tvReservationTrainingName.setText(timetableEvent.getTrainingName());
		
		holder.tvReservationFreeSpots.setText(String.valueOf(timetableEvent.getFreeSpots()));
		
		if(timetableEvent.isReserved())
			holder.imgIsReserved.setBackgroundResource(R.drawable.checkbox_on);
		else
			holder.imgIsReserved.setBackgroundResource(R.drawable.checkbox_off);
		
		
		return rowView;
	}
}
