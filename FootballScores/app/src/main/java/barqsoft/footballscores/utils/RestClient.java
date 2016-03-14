package barqsoft.footballscores.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by xiaoma on 14/03/16.
 */
public class RestClient {
    private static final String LOG_TAG = RestClient.class.getSimpleName();

    /**
     * Returns the data asked by a request.
     *
     * @param url the address where the data is requested.
     * @return String contains the streamed information.
     */
    public static String getData(@NonNull String url, String token) throws IOException {

        BufferedReader reader = null;
        HttpURLConnection urlConnection = null;

        try {
            //get connection
            urlConnection = connect(url, token);

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }

            return buffer.toString();

        } catch (IOException e) {
            throw e;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
    }


    /**
     * Create an url connection.
     *
     * @param path url address.
     * @return HttpURLConnection
     * @throws IOException if the address is wrong or there is not internet connection.
     */
    public static HttpURLConnection connect(@NonNull String path, String token) throws IOException {

        URL url = new URL(path);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.addRequestProperty("X-Auth-Token", token);
        urlConnection.connect();

        return urlConnection;
    }
}
