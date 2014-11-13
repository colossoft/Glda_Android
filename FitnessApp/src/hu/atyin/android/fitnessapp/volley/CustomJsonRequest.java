package hu.atyin.android.fitnessapp.volley;

import hu.atyin.android.fitnessapp.R;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

public class CustomJsonRequest extends Request<JSONObject> {

	private Context ctx;
	private Listener<JSONObject> listener;
    private Map<String, String> params;
    private Map<String, String> headers;
    
    public CustomJsonRequest(Context ctx, String url, Map<String, String> params, Map<String, String> headers, Listener<JSONObject> reponseListener, ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.ctx = ctx;
        this.listener = reponseListener;
        this.params = params;
        this.headers = headers;
    }
	
	public CustomJsonRequest(Context ctx, int method, String url, Map<String, String> params, Map<String, String> headers, Listener<JSONObject> reponseListener, ErrorListener errorListener) {
		super(method, url, errorListener);
		this.ctx = ctx;
        this.listener = reponseListener;
        this.params = params;
        this.headers = headers;
	}

	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    };
    
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
    	return headers;
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }
    
    @Override
    protected VolleyError parseNetworkError(VolleyError volleyError) {
    	try {
    		if(volleyError instanceof NoConnectionError) {
    			VolleyError error = new VolleyError(ctx.getString(R.string.app_error_no_internet));
    			volleyError = error;
    		}
    		else if(volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
    			Log.d("FITNESS", String.valueOf(volleyError.networkResponse.data));
				String jsonString = new String(volleyError.networkResponse.data, 
						HttpHeaderParser.parseCharset(volleyError.networkResponse.headers));
				JSONObject jsonObj = new JSONObject(jsonString);
				String errMessage = jsonObj.getString("message");
				
                VolleyError error = new VolleyError(errMessage);
                volleyError = error;
            }
			else {
				VolleyError error = new VolleyError(ctx.getString(R.string.app_error_unexpected));
				volleyError = error;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	
    	return volleyError;
    }
    
    @Override
    protected void deliverResponse(JSONObject response) {
        listener.onResponse(response);
    }

}
