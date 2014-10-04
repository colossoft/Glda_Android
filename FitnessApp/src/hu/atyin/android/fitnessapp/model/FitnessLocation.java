package hu.atyin.android.fitnessapp.model;

public class FitnessLocation {

	private int id;
	private String name;
	private String address;
	private double latitude;
	private double longitude;
	private double distance;

	public FitnessLocation(int id, String name, String address,
			double latitude, double longitude, double distance) {
		super();
		this.id = id;
		this.name = name;
		this.address = address;
		this.latitude = latitude;
		this.longitude = longitude;
		this.distance = distance;
	}

	@Override
	public String toString() {
		return "FitnessLocation [id=" + id + ", name=" + name + ", address="
				+ address + ", latitude=" + latitude + ", longitude="
				+ longitude + ", distance=" + distance + "]";
	}

	public int getId() {
		return id;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}
	
}
