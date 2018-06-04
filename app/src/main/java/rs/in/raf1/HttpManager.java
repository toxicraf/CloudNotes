package rs.in.raf1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

public class HttpManager {

	public static String getData(RequestPackage p) {
		
		BufferedReader reader = null;
		String uri = p.getUri();

		//check that a GET request is being made then append ? and params to url
		if (p.getMethod().equals("GET")) {
			uri +="?" + p.getEncodedParams();
		}

		try {
			URL url = new URL(uri);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod(p.getMethod());
			
			JSONObject json = new JSONObject(p.getParams());
			String params = "params=" + json.toString();
			
			if (p.getMethod().equals("POST")) {
				con.setDoOutput(true);
				OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
				writer.write(params);
				writer.flush();
			}
			
			StringBuilder sb = new StringBuilder();
			reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			
			return sb.toString();
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
			}
		}
		
	}
	
}
