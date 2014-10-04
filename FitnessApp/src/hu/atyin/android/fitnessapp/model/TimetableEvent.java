package hu.atyin.android.fitnessapp.model;

public class TimetableEvent {

	private int id;
	private String startTime;
	private String endTime;
	private String trainerName;
	private String trainingName;
	private int freeSpots;
	private boolean isReserved;
	
	public TimetableEvent(int id, String startTime, String endTime,
			String trainerName, String trainingName, int freeSpots,
			boolean isReserved) {
		super();
		this.id = id;
		this.startTime = startTime;
		this.endTime = endTime;
		this.trainerName = trainerName;
		this.trainingName = trainingName;
		this.freeSpots = freeSpots;
		this.isReserved = isReserved;
	}



	public int getId() {
		return id;
	}



	public void setId(int id) {
		this.id = id;
	}



	public String getStartTime() {
		return startTime;
	}



	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}



	public String getEndTime() {
		return endTime;
	}



	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}



	public String getTrainerName() {
		return trainerName;
	}



	public void setTrainerName(String trainerName) {
		this.trainerName = trainerName;
	}



	public String getTrainingName() {
		return trainingName;
	}



	public void setTrainingName(String trainingName) {
		this.trainingName = trainingName;
	}



	public int getFreeSpots() {
		return freeSpots;
	}



	public void setFreeSpots(int freeSpots) {
		this.freeSpots = freeSpots;
	}



	public boolean isReserved() {
		return isReserved;
	}



	public void setReserved(boolean isReserved) {
		this.isReserved = isReserved;
	}



	public void decreaseFreeSpots() {
		this.freeSpots -= 1;
	}
	
	public void increaseFreeSpots() {
		this.freeSpots += 1;
	}
	
}
