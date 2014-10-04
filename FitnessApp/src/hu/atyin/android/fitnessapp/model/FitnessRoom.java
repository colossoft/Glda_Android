package hu.atyin.android.fitnessapp.model;

public class FitnessRoom {

	private int id;
	private String name;
	
	public FitnessRoom(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	@Override
	public String toString() {
		return "FitnessRoom [id=" + id + ", name=" + name + "]";
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
}
