package hu.atyin.android.fitnessapp.adapter;

import hu.atyin.android.fitnessapp.R;
import hu.atyin.android.fitnessapp.RoomSelectionActivity;
import hu.atyin.android.fitnessapp.model.FitnessLocation;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LocationsListAdapter extends ArrayAdapter<FitnessLocation> {

	private Context context;
	private ArrayList<FitnessLocation> fitnessLocations;
	
	public LocationsListAdapter(Context context, int resource, ArrayList<FitnessLocation> fitnessLocations) {
		super(context, resource, fitnessLocations);
		this.context = context;
		this.fitnessLocations = fitnessLocations;
	}

	static class ViewHolder {
		public TextView tvFitnessName;
		public TextView tvFitnessAddress;
		public TextView tvFitnessDistance;
		public ImageView imgNextArrow;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		ViewHolder holder = null;
		
		if(rowView == null) {
			LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.location_row_layout, parent, false);
			
			holder = new ViewHolder();
			
			holder.tvFitnessName = (TextView) rowView.findViewById(R.id.tvFitnessName);
			holder.tvFitnessAddress = (TextView) rowView.findViewById(R.id.tvFitnessAddress);
			holder.tvFitnessDistance = (TextView) rowView.findViewById(R.id.tvFitnessDistance);
			holder.imgNextArrow = (ImageView) rowView.findViewById(R.id.imgNextArrow);
			
			rowView.setTag(holder);
		}
		else {
			holder = (ViewHolder) rowView.getTag();
		}
		
		final FitnessLocation fitnessLocation = fitnessLocations.get(position);
		
		holder.tvFitnessName.setText(fitnessLocation.getName());
		holder.tvFitnessAddress.setText(fitnessLocation.getAddress());
		holder.tvFitnessDistance.setText(String.format("%.1f " + context.getString(R.string.app_location_km), fitnessLocation.getDistance()));
		
		holder.imgNextArrow.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent roomSelectionIntent = new Intent(context, RoomSelectionActivity.class);
				roomSelectionIntent.putExtra("GYM_ID", fitnessLocation.getId());
				roomSelectionIntent.putExtra("GYM_TITLE", fitnessLocation.getName());
				context.startActivity(roomSelectionIntent);
			}
		});
		
		return rowView;
	}
}
