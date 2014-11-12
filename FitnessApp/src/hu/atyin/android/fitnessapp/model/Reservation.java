package hu.atyin.android.fitnessapp.model;

public class Reservation {
	private int id;
	private String reservationTime;
	private String date;
	private String startTime;
	private String endTime;
	private String trainerName;
	private String trainingName;
	private boolean past;
	
	public Reservation(int id, String reservationTime, String date,
			String startTime, String endTime, String trainerName,
			String trainingName, boolean past) {
		super();
		this.id = id;
		this.reservationTime = reservationTime;
		this.date = date;
		this.startTime = startTime;
		this.endTime = endTime;
		this.trainerName = trainerName;
		this.trainingName = trainingName;
		this.past = past;
	}

	@Override
	public String toString() {
		return "Reservation [id=" + id + ", reservationTime=" + reservationTime
				+ ", date=" + date + ", startTime=" + startTime + ", endTime="
				+ endTime + ", trainerName=" + trainerName + ", trainingName="
				+ trainingName + ", past=" + past + "]";
	}

	public int getId() {
		return id;
	}

	public String getReservationTime() {
		return reservationTime;
	}

	public String getDate() {
		return date;
	}

	public String getStartTime() {
		return startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public String getTrainerName() {
		return trainerName;
	}

	public String getTrainingName() {
		return trainingName;
	}

	public boolean isPast() {
		return past;
	}
	
}
