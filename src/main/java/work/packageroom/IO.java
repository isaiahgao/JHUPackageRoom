package work.packageroom;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class IO {
	
	public static StringBuffer getHTTPResponse(String url) {
		try {
	        URL obj = new URL(url.replace(" ", "%20"));
	        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	
	        StringBuffer response = new StringBuffer();
	        try {
	            BufferedReader in = new BufferedReader(
	                    new InputStreamReader(con.getInputStream()));
	            String inputLine;
	    
	            while ((inputLine = in.readLine()) != null) {
	                response.append(inputLine);
	            }
	            in.close();
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }
	        return response;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }

}
