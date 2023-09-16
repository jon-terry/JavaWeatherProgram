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
                System.out.println(emailAddress + "/n");

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

        // Instance of WeatherAPI
        WeatherAPI weatherAPI = new WeatherAPI(apiKey);

        // Location for data
        String userLocation;

        while (true) {
            System.out.println("""
                    \s
                    Please enter the city that you want weather data from.
                    """);

            userLocation = scanner.nextLine();

            if (userLocation.matches("^[A-Za-z ]+$")) {
                break;
            } else {
                System.out.println("ERROR: Name of city incorrect or not found.");
            }
        }
        // TODO: Checking for HTTPS functionality, if not use HTTP

        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + userLocation + "&appid=" + apiKey;

        // Getting weather data
        Map<String, Object> weatherData = weatherAPI.getWeatherData(userLocation);

        // Process weather data
        JSONObject jsonData = new JSONObject(weatherData);

        double temperature = jsonData.getJSONObject("main").getDouble("temp");
        String weatherDescription = jsonData.getJSONArray("weather").getJSONObject(0).getString("description");
        double precipitationChance = jsonData.getJSONObject("rain").getDouble("12h");

        // Email message
        double temperatureCelsius = temperature - 273.15;
        temperature = (temperatureCelsius * 9/5) + 32;

        String emailMessage = "Hello, " + userName + "./n/n";
        emailMessage += "Here is the weather forecast for " + userLocation + ": /n";
        emailMessage += "Temperature: " + temperature + "°F (" + temperatureCelsius + "°C)";
        emailMessage += "Weather description: " + weatherDescription + "/n /n";
        emailMessage += "Thank you for using the Java Weather Program.";

        String recipientEmail = emailAddress;
        String subject = "Weather forecast for " + userName + ".";
        String message = """
                Today's weather forecast /n
                /n
                /n
                """ + emailMessage;

        EmailSender.sendEmail(emailAddress, subject, message);


    }


}


