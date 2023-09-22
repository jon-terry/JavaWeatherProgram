import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class WeatherAPI {
    private final String apiKey;

    public WeatherAPI(String apiKey) {
        this.apiKey = apiKey;
    }

    public Map<String, Object> getWeatherData(String location) {
        try {
            // Construct URL for API request
            String apiUrl = "http://api.openweathermap.org/data/2.5/weather?zip=" + location + "&appid=" + apiKey;
            URL url = new URL(apiUrl);

            // Open a connection to URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set HTTP request to 'GET'
            connection.setRequestMethod("GET");

            // Set request headers (e.g. API key)
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);

            // Get response code
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read + return the response data
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Parse JSON response into Map
                JSONParser parser = new JSONParser();
                JSONObject jsonObject = (JSONObject) parser.parse(response.toString());

                // Convert JSONObject to Map
                Map<String, Object> weatherData = new HashMap<>(jsonObject);

                return weatherData;

            } else {
                System.out.println("HTTP Request Failed with Response Code: " + responseCode);
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);

        } catch (org.json.simple.parser.ParseException e) {
            throw new RuntimeException(e);
        }
    }

}
