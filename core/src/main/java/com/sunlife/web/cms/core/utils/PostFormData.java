package com.sunlife.web.cms.core.utils;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class PostFormData {

    public static Map<String,Object> postCallFormData(String api,String formData) {
    	Map<String,Object> responseMap = new HashMap<>();
        try {
            // The URL of the API endpoint
            URL url = new URL(api); // Replace with your API URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set up the connection properties
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // Prepare the form data
           //String formData = "username=yourUsername&password=yourPassword";  // Replace with your form fields

            // Convert form data to bytes
            byte[] postData = formData.getBytes(StandardCharsets.UTF_8);

            // Send the form data
            try (OutputStream os = connection.getOutputStream()) {
                os.write(postData);
            }
            responseMap.put("responseCode", connection.getResponseCode());
           // responseMap.put("cookie", connection.)
            // Check the response code
          

        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseMap;
        
    }
}