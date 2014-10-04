package hu.atyin.android.fitnessapp.model;

public class WeekdaySpinnerItem {
	private String weekday;
    private String date;
    
	public WeekdaySpinnerItem(String weekday, String date) {
		super();
		this.weekday = weekday;
		this.date = date;
	}

	public String getWeekday() {
		return weekday;
	}

	public String getDate() {
		return date;
	}
}
