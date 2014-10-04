package hu.atyin.android.fitnessapp.adapter;

import hu.atyin.android.fitnessapp.R;
import hu.atyin.android.fitnessapp.model.FitnessRoom;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RoomListAdapter extends ArrayAdapter<FitnessRoom> {
	
	private Context context;
	private ArrayList<FitnessRoom> rooms;

	public RoomListAdapter(Context context, int resource, ArrayList<FitnessRoom> rooms) {
		super(context, resource, rooms);
		this.context = context;
		this.rooms = rooms;
	}

	static class ViewHolder {
		public TextView tvRoomName;
		public ImageView imgNextArrow;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		ViewHolder holder = null;
		
		if(rowView == null) {
			LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.trainings_row_layout, parent, false);
			
			holder = new ViewHolder();
			
			holder.tvRoomName = (TextView) rowView.findViewById(R.id.tvTrainingName);
			holder.imgNextArrow = (ImageView) rowView.findViewById(R.id.imgNextArrow);
			
			rowView.setTag(holder);
		}
		else {
			holder = (ViewHolder) rowView.getTag();
		}
		
		final FitnessRoom room = rooms.get(position);
		
		holder.tvRoomName.setText(room.getName());
		
		/*holder.imgNextArrow.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent reservationIntent = new Intent(context, ReservationActivity.class);
				context.startActivity(reservationIntent);
			}
		});*/
		
		return rowView;
	}
}
