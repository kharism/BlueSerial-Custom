package com.blueserial;

import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONRequest {
	public static HttpResponse makeRequest(String path, Map params) throws Exception 
	{
	    //instantiates httpclient to make request
	    DefaultHttpClient httpclient = new DefaultHttpClient();

	    //url with the post data
	    HttpPost httpost = new HttpPost(path);

	    //convert parameters into JSON object
	    JSONObject holder = getJsonObjectFromMap(params);

	    //passes the results to a string builder/entity
	    StringEntity se = new StringEntity(holder.toString());

	    //sets the post request as the resulting string
	    httpost.setEntity(se);
	    //sets a request header so the page receving the request
	    //will know what to do with it
	    //httpost.setHeader("Accept", "application/json");
	    httpost.setHeader("Content-type", "application/json");

	    //Handles what is returned from the page 
	    ResponseHandler responseHandler = new BasicResponseHandler();
	    return httpclient.execute(httpost, responseHandler);
	}
	private static JSONObject getJsonObjectFromMap(Map params) throws JSONException {

	    //all the passed parameters from the post request
	    //iterator used to loop through all the parameters
	    //passed in the post request
	    Iterator iter = params.entrySet().iterator();

	    //Stores JSON
	    JSONObject holder = new JSONObject();

	    //using the earlier example your first entry would get email
	    //and the inner while would get the value which would be 'foo@bar.com' 
	    //{ fan: { email : 'foo@bar.com' } }

	    //While there is another entry
	    while (iter.hasNext()) 
	    {
	        //gets an entry in the params
	        Map.Entry pairs = (Map.Entry)iter.next();

	        //creates a key for Map
	        String key = (String)pairs.getKey();

	        //Create a new map
	        
	        //object for storing Json
	        JSONObject data = new JSONObject();

	        //gets the value
	        //data.put((String)pairs2.getKey(), (String)pairs2.getValue());
	        
	        //puts email and 'foo@bar.com'  together in map
	        holder.put(key, pairs.getValue());
	    }
	    return holder;
	}
}
