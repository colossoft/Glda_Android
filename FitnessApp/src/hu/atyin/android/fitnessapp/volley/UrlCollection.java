package hu.atyin.android.fitnessapp.volley;

public class UrlCollection {

	//public static final String MAIN_URL = "http://gildamax.atyin.url.ph/v1";
	public static final String MAIN_URL = "http://10.0.2.2:8080/Glda_app/BackEnd/v1";
	
	public static final String LOGIN_URL = MAIN_URL + "/login";
	public static final String REGISTER_URL = MAIN_URL + "/register";
	public static final String GET_ALL_LOCATION_URL = MAIN_URL + "/locations";
	public static final String GET_ROOMS_URL = MAIN_URL + "/rooms/";
	public static final String GET_EVENTS_URL = MAIN_URL + "/events/";
	public static final String MAKE_RESERVATION_URL = MAIN_URL + "/reservation";
	public static final String DELETE_RESERVATION_URL = MAIN_URL + "/reservation/";
}
