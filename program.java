import java.sql.SQLOutput;
import java.util.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.json.*;

public class program {

    public static void main(String[] args) throws JSONException {
        Scanner scanner = new Scanner(System.in);
        String userName;
        String emailAddress;
        int zipCode;
        int decimalPlaces = 2;

        System.out.println("""
                \s
                /////////////////////////////////////////////
                Thank you for using the Java Weather Program.
                /////////////////////////////////////////////
                \s
                This program is designed to send you weather updates to your email for a location that you enter.""");

        while(true) {

            System.out.println("First, please enter your name: ");
            userName = scanner.nextLine();
            boolean userNameCheck = userName.matches("[a-zA-Z]+");

            if (userNameCheck) {
                System.out.println("Thank you, " + userName);
                break;
            } else {
                System.out.println("""
                         Sorry. Please only enter letters for your name. You can use your first name,
                         your first and last name, or a nickname if you wish.
                        """);
            }


        }

        while (true) {

            System.out.println("Please enter your email address to send weather forecasts to: ");
            emailAddress = scanner.nextLine();
            boolean emailCheck = emailAddress.matches("^[A-Za-z0-9+_.-]+@(.+)$");

            if (emailCheck) {
                System.out.println("Thank you for your email address, " + userName);
                System.out.println("""
                        Please verify that the email address is correct. You entered:
                         
                         \s""");
                System.out.println(emailAddress);
                System.out.println("");

                System.out.println("Is this correct? Press \"Y\" for Yes, or \"N\" for No");

                String emailPrompt = scanner.nextLine();

                if (emailPrompt.equalsIgnoreCase("Y")) {
                    break;
                }

            } else {
                System.out.println("There was an error with the email address you entered. Please try again.");
            }

        }
        // Finished checking user's name and email address.

        String apiKey = "e1ed9b58bb55042bed20f20af00189e3";

        // Instance of WeatherAPI and FiveDayWeatherAPI
        WeatherAPI weatherAPI = new WeatherAPI(apiKey);
        FiveDayWeatherAPI fiveDayWeatherAPI = new FiveDayWeatherAPI(apiKey);

        // Location for data
        String userLocation;
        String zipCodePattern = "^\\d{5}(?:-\\d{4})?$";


        while (true) {
            System.out.println("""
                    \s
                    Please enter the zip code of the city that you want weather data from.
                    """);

            userLocation = scanner.nextLine();

            if (userLocation.matches(zipCodePattern)) {
                break;
            } else {
                System.out.println("ERROR: ZIP code incorrect or not found.");
            }
        }

        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?zip=" + userLocation + "&appid=" + apiKey;

        // Getting weather data
        Map<String, Object> weatherData = weatherAPI.getWeatherData(userLocation);
        Map<String, Object> weatherDataForecast = fiveDayWeatherAPI.getWeatherData(userLocation);

        // Process current weather data
        JSONObject jsonData = new JSONObject(weatherData);

        // Process forecasted weather data
        JSONObject jsonDataForecast = new JSONObject(weatherDataForecast);

        JSONArray forecastList = jsonDataForecast.getJSONArray("list");

        TreeMap<String, List<JSONObject>> dailyForecasts = new TreeMap<>();

        for (int i = 0; i < forecastList.length(); i++) {
            JSONObject dayForecast = forecastList.getJSONObject(i);
            String date = dayForecast.getString("dt_txt").split(" ")[0];

            dailyForecasts.computeIfAbsent(date, k -> new ArrayList<>()).add(dayForecast);
        }


        double temperature = jsonData.getJSONObject("main").getDouble("temp");
        String weatherDescription = jsonData.getJSONArray("weather").getJSONObject(0).getString("description");


        // Email message
        double temperatureCelsius = Math.round((temperature - 273.15) * Math.pow(10,decimalPlaces)) / Math.pow(10,decimalPlaces);
        temperature = Math.round(((temperatureCelsius * 9/5) + 32) * Math.pow(10,decimalPlaces)) / Math.pow(10,decimalPlaces);

        String lineSeparator = System.lineSeparator();

        // Current Weather
        String emailMessage = "Hello, " + userName + "." + lineSeparator + lineSeparator;
        emailMessage += "Here is the current weather forecast for " + userLocation + ":" + lineSeparator;
        emailMessage += "Temperature: " + temperature + "°F (" + temperatureCelsius + "°C)" + lineSeparator;
        emailMessage += "Weather description: " + weatherDescription + lineSeparator + lineSeparator;

        // 5-Day Forecast

        emailMessage += "Here is the 5-day weather forecast for " + userLocation + " :" + lineSeparator;
        emailMessage += lineSeparator;

        for (Map.Entry<String, List<JSONObject>> entry : dailyForecasts.entrySet()) {
            String date = entry.getKey();
            List<JSONObject> dayForecasts = entry.getValue();

            double totalTemp = 0.0;
            int dataPoints = 0;

            for (JSONObject forecast : dayForecasts) {
                JSONObject main = forecast.getJSONObject("main");
                temperature = main.getDouble("temp"); // In Kelvin

                totalTemp += temperature;
                dataPoints++;
            }

            if (dataPoints > 0) {
                double averageTemp = totalTemp / dataPoints;
                double celsiusAverageTemp = averageTemp - 273.15;
                celsiusAverageTemp = Math.round(celsiusAverageTemp * Math.pow(10,decimalPlaces)) / Math.pow(10,decimalPlaces);

                double fahrenheitAverageTemp = (celsiusAverageTemp * 9/5) + 32;
                fahrenheitAverageTemp = Math.round(fahrenheitAverageTemp * Math.pow(10,decimalPlaces)) / Math.pow(10,decimalPlaces);



                emailMessage += "Date: " + date + lineSeparator;

                emailMessage += "Average Temperature: " + celsiusAverageTemp + " °C (" + fahrenheitAverageTemp + " °F)"
                        + lineSeparator + lineSeparator;

            }
        }

        String subject = "Weather forecast for " + userName + ".";
        String message = """
                Current and Five-Day weather forecast:
                
               
                """ + lineSeparator + emailMessage +
                lineSeparator + lineSeparator +
                "Thank you for using the Java Weather Program.";

        EmailSender.sendEmail(emailAddress, subject, message);


    }


}


