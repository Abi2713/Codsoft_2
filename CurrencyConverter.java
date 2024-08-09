package codsoft;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class CurrencyConverter {
    private static final String API_URL = "https://api.exchangerate-api.com/v4/latest/";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            // Get base and target currency codes from the user
            System.out.print("Enter base currency code : ");
            String baseCurrency = scanner.nextLine().toUpperCase();
            System.out.print("Enter target currency code : ");
            String targetCurrency = scanner.nextLine().toUpperCase();

            // Fetch exchange rate
            double rate = getExchangeRate(baseCurrency, targetCurrency);
            if (rate == -1) {
                System.out.println("Error fetching exchange rate. Please check the currency codes.");
                return;
            }

            // Get amount to convert from the user
            System.out.print("Enter amount in " + baseCurrency + ": ");
            double amount = scanner.nextDouble();

            // Perform the conversion
            double convertedAmount = amount * rate;

            // Display the result
            System.out.printf("Converted amount: %.2f %s\n", convertedAmount, targetCurrency);
        } finally {
            // Ensure scanner is closed
            scanner.close();
        }
    }

    // Method to fetch exchange rate from the API
    private static double getExchangeRate(String baseCurrency, String targetCurrency) {
        try {
            // Construct the URL for the API request
            URL url = new URL(API_URL + baseCurrency);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Read the response from the API
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Convert response to a string and find the exchange rate
            String responseData = response.toString();
            int startIndex = responseData.indexOf(targetCurrency) + targetCurrency.length() + 4; // Skip "":"
            int endIndex = responseData.indexOf(",", startIndex);
            if (endIndex == -1) endIndex = responseData.indexOf("}", startIndex);

            String rateString = responseData.substring(startIndex, endIndex);
            return Double.parseDouble(rateString);
        } catch (Exception e) {
            e.printStackTrace();
            return -1; // Return -1 in case of error
        }
    }
}
