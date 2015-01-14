package com.haidang.sampleapp.earthquake.network;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import android.content.Context;
import android.net.Uri;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;
import com.haidang.sampleapp.earthquake.EarthquakeInfoApp;
import com.haidang.sampleapp.earthquake.Earthquakes;
import com.haidang.sampleapp.earthquake.exception.NetworkNotAvailableException;
import com.haidang.sampleapp.earthquake.network.NetworkRequest.ActiveRequest;
import com.haidang.sampleapp.earthquake.network.NetworkRequest.NetworkCallback;
import com.haidang.sampleapp.earthquake.network.ServiceResponse.ServiceError;

/**
 * @author Hai Dang
 *
 *This Network Manager class provide simple interface functions across the app for making network call in background.
 *It provides tracking facility to track the progress of a current active network call. 
 */
public class NetworkManager {

	public interface InProgressNetworkCallChecker {
		public ActiveRequest getRequestTypeOfChecker();

		public void onNetworkCallsFinished();
	}

	private static NetworkManager _instance;
	private RequestQueue mVolleyQueue;

	private static Map<ActiveRequest, NetworkRequest> activeRequestMap = new HashMap<NetworkRequest.ActiveRequest, NetworkRequest>();
	private static Map<ActiveRequest, InProgressNetworkCallChecker> checkersMap = new HashMap<NetworkRequest.ActiveRequest, InProgressNetworkCallChecker>();

	class InternalCallback<T> implements NetworkCallback<T> {
		private NetworkCallback<T> updater;
		private ActiveRequest requestType;

		public InternalCallback(NetworkCallback<T> updater, ActiveRequest requestType) {
			this.updater = updater;
			this.requestType = requestType;
		}

		@Override
		public void update(T response) {
			if (updater != null)
				updater.update(response);
			if (requestType != null) {
				activeRequestMap.remove(requestType);
				InProgressNetworkCallChecker checker = checkersMap.get(requestType);
				if (checker != null)
					checker.onNetworkCallsFinished();
				unregisterChecker(requestType);
			}
		}
	}

	public void registerChecker(InProgressNetworkCallChecker checker) {
		if (activeRequestMap.get(checker.getRequestTypeOfChecker()) == null) {
			checker.onNetworkCallsFinished();
			return;
		}
		if (checker != null && checker.getRequestTypeOfChecker() != null && checker.getRequestTypeOfChecker() != ActiveRequest.NONE) {
			checkersMap.put(checker.getRequestTypeOfChecker(), checker);
		}
	}

	public boolean unregisterChecker(ActiveRequest requestType) {
		if (requestType != null)
			return (checkersMap.remove(requestType) != null);
		return false;
	}

	public static NetworkManager getInstance() {
		if (_instance == null)
			_instance = new NetworkManager(EarthquakeInfoApp.getInstance());
		return _instance;
	}

	private NetworkManager(Context context) {
		mVolleyQueue = Volley.newRequestQueue(context);
	}

	public NetworkRequest getEarthquakesData(final NetworkCallback<ServiceResponse<Earthquakes>> updater) throws NetworkNotAvailableException {
		return sendRequest(NetworkRequest.ActiveRequest.DOWNLOAD_EARTHQUAKE_DATA, NetworkConstant.DATE_SOURCE_URL, updater, Earthquakes.class);
	}

	/*
	 * Method interfaces
	 * 
	 * */
	

	public <T> NetworkRequest sendRequest(NetworkRequest.ActiveRequest requestType, String endPoint, NetworkCallback<ServiceResponse<T>> callback,
			Class<T> javaClass) throws NetworkNotAvailableException {
		return sendRequest(requestType, null, endPoint, callback, javaClass, NetworkConstant.TIMEOUT_NORMAL);
	}

	public <T> NetworkRequest sendRequest(NetworkRequest.ActiveRequest requestType, String endPoint, NetworkCallback<ServiceResponse<T>> callback,
			Class<T> javaClass, int timeout) throws NetworkNotAvailableException {
		return sendRequest(requestType, null, endPoint, callback, javaClass, timeout);
	}

	public <T> NetworkRequest sendRequest(NetworkRequest.ActiveRequest requestType, Object requestBody, String endPoint,
			NetworkCallback<ServiceResponse<T>> callback, Class<T> javaClass) throws NetworkNotAvailableException {
		return sendRequest(requestType, requestBody, endPoint, callback, javaClass, NetworkConstant.TIMEOUT_NORMAL);
	}

	// /**** MAIN *** ////

	public <T> NetworkRequest sendRequest(NetworkRequest.ActiveRequest requestType, Object requestBody, String endPoint,
			NetworkCallback<ServiceResponse<T>> callback, Class<T> javaClass, int timepout) throws NetworkNotAvailableException {
		if (!EarthquakeInfoApp.getInstance().isNetworkConnected())
			throw new NetworkNotAvailableException();

		String uri = endPoint;

		Uri.Builder builder = Uri.parse(uri).buildUpon();
		int method = Request.Method.GET;
		if (requestBody != null)
			method = Request.Method.POST;

		InternalCallback<ServiceResponse<T>> internalCallBack = new InternalCallback<ServiceResponse<T>>(callback, requestType);
		GsonRequest<T> request = new GsonRequest<T>(method, builder.toString(), javaClass, requestBody, internalCallBack, new FailListener<T>(
				callback));
		request.setRequestType(requestType);
		request.setRetryPolicy(new DefaultRetryPolicy(timepout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		mVolleyQueue.add(request);

		// Add this to re request map
		activeRequestMap.put(requestType, request);

		return request;
	}

	private class FailListener<T> implements Response.ErrorListener {
		private NetworkCallback<ServiceResponse<T>> callback;

		public FailListener(NetworkCallback<ServiceResponse<T>> callback) {
			this.callback = callback;
		}

		@Override
		public void onErrorResponse(VolleyError error) {
			final ServiceResponse<T> sResponse = new ServiceResponse<T>();
			sResponse.error = new ServiceError(error.getCause());
			callback.update(sResponse);
		}

	}

}

class GsonRequest<T> extends Request<T> implements NetworkRequest {
	private static final String JSON_CONTENT_TYPE_HEADER = "Content-Type";
	private static final String JSON_CONTENT_TYPE = "application/json; charset=utf-8";
	private boolean isFinished = false;
	private ActiveRequest requestType;
	/** Charset for request. */
	private static final String PROTOCOL_CHARSET = "utf-8";

	/** Content type for request. */
	private static final String PROTOCOL_CONTENT_TYPE = String.format("application/json; charset=%s", PROTOCOL_CHARSET);

	private String mRequestBody;
	private final ObjectMapper objectMapper;
	private Class<T> mJavaClass;
	private Map<String, String> headers = new HashMap<String, String>();
	private NetworkCallback<ServiceResponse<T>> callback;
	private ServiceResponse<T> sResponse = new ServiceResponse<T>();

	public GsonRequest(int method, String url, Class<T> cls, Object requestBody, NetworkCallback<ServiceResponse<T>> callback,
			Response.ErrorListener errorListener) {
		super(method, url, errorListener);
		this.callback = callback;
		mJavaClass = cls;
		this.objectMapper = new ObjectMapper();// JSONMapper.getInstance();
		if (requestBody != null)
			try {
				mRequestBody = objectMapper.writeValueAsString(requestBody);
				headers.put(JSON_CONTENT_TYPE_HEADER, JSON_CONTENT_TYPE);
			} catch (Exception e) {
				// e.printStackTrace();
				mRequestBody = null;
			}
		sResponse.requestStartTime = System.currentTimeMillis();
	}

	@Override
	protected void deliverResponse(T response) {
		sResponse.result = response;
		sResponse.requestEndTime = System.currentTimeMillis();
		callback.update(sResponse);
		isFinished = true;
	}

	@Override
	public void deliverError(VolleyError error) {
		error.printStackTrace();
		sResponse.error = new ServiceError(error.getCause());
		callback.update(sResponse);
		isFinished = true;
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		return headers;
	}

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		try {
			String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			if (jsonString == null || jsonString.trim().isEmpty())
				return Response.success(null, HttpHeaderParser.parseCacheHeaders(response));
			T parsedGSON = objectMapper.readValue(jsonString, mJavaClass);

			return Response.success(parsedGSON, HttpHeaderParser.parseCacheHeaders(response));

		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JsonMappingException jme) {
			return Response.error(new ParseError(jme));
		} catch (org.codehaus.jackson.JsonParseException jpe) {
			return Response.error(new ParseError(jpe));
		} catch (IOException e) {
			return Response.error(new ParseError(e));
		}
	}

	@Override
	public String getBodyContentType() {
		return PROTOCOL_CONTENT_TYPE;
	}

	@Override
	public byte[] getBody() {
		try {
			return mRequestBody == null ? null : mRequestBody.getBytes(PROTOCOL_CHARSET);
		} catch (UnsupportedEncodingException uee) {
			VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, PROTOCOL_CHARSET);
			return null;
		}
	}

	public boolean isFinished() {
		return isFinished;
	}

	@Override
	public void setRequestType(ActiveRequest request) {
		if (request == null)
			this.requestType = ActiveRequest.NONE;
		else
			this.requestType = request;
	}

	@Override
	public ActiveRequest getRequestType() {
		return this.requestType;
	}

	@Override
	public void setDataTag(Object tag) {
		this.setTag(tag);
	}
}