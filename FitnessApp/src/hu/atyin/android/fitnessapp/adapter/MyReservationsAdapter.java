package hu.atyin.android.fitnessapp.adapter;

import hu.atyin.android.fitnessapp.R;
import hu.atyin.android.fitnessapp.model.Reservation;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class MyReservationsAdapter extends ArrayAdapter<Reservation> {
	
	private Context context;
	private ArrayList<Reservation> myReservations;

	public MyReservationsAdapter(Context context, int resource, ArrayList<Reservation> myReservations) {
		super(context, resource, myReservations);
		
		this.context = context;
		this.myReservations = myReservations;
	}

	static class ViewHolder {
		public TextView tvEventDate;
		public TextView tvEventTime;
		public TextView tvReservationTrainerName;
		public TextView tvReservationTrainingName;
		public TextView tvReservationTime;
		public Button btnDeleteReservation;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		ViewHolder holder = null;
		
		if(rowView == null) {
			LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.my_reservations_row_layout, parent, false);
			
			holder = new ViewHolder();
			
			holder.tvEventDate = (TextView) rowView.findViewById(R.id.tvEventDate);
			holder.tvEventTime = (TextView) rowView.findViewById(R.id.tvEventTime);
			holder.tvReservationTrainerName = (TextView) rowView.findViewById(R.id.tvReservationTrainerName);
			holder.tvReservationTrainingName = (TextView) rowView.findViewById(R.id.tvReservationTrainingName);
			holder.tvReservationTime = (TextView) rowView.findViewById(R.id.tvReservationTime);
			holder.btnDeleteReservation = (Button) rowView.findViewById(R.id.btnDeleteReservation);
			
			rowView.setTag(holder);
		}
		else {
			holder = (ViewHolder) rowView.getTag();
		}
		
		final Reservation reservation = myReservations.get(position);
		
		holder.tvEventDate.setText(reservation.getDate());
		holder.tvEventTime.setText(String.format("%s - %s", reservation.getStartTime(), reservation.getEndTime()));
		holder.tvReservationTrainerName.setText(reservation.getTrainerName());
		holder.tvReservationTrainingName.setText(reservation.getTrainingName());
		holder.tvReservationTime.setText(reservation.getReservationTime());
		
		holder.btnDeleteReservation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO
			}
		});
		
		return rowView;
	}
}
