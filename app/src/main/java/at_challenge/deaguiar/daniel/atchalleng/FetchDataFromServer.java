package at_challenge.deaguiar.daniel.atchalleng;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 */
public class FetchDataFromServer extends AsyncTask<Void, Void, Void> {

    private final static String USERNAME = "WKD4N7YMA1uiM8V";
    private final static String PASSWORD = "DtdTtzMLQlA0hk2C1Yi5pLyVIlAQ68";
    private final static String END_POINT_ROUTES = "https://api.appglu.com/v1/queries/findRoutesByStopName/run";
    private final static String END_POINT_STOPS = "https://api.appglu.com/v1/queries/findStopsByRouteId/run";
    private final static String END_POINT_DEPARTURES = "https://api.appglu.com/v1/queries/findDeparturesByRouteId/run";

    private String mJsonRoute;
    private RouteList mRouteList;

    public FetchDataFromServer(String route, RouteList routeList) {
        mRouteList = routeList;
        mJsonRoute = "{\"params\": {\"stopName\": \"%" + route + "%\"}}";
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            HttpResponse response = getHttpResponse(mJsonRoute);

            String result = "";
            InputStream inputStream = response.getEntity().getContent();
            if (inputStream != null) {
                result = convertInputStreamToString(inputStream);
            }
            else {
                result = "Did not work.";
            }

            mRouteList.setRoutes(result);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    protected HttpResponse getHttpResponse(String jsonValue) throws IOException{
        HttpClient client = new DefaultHttpClient();
        HttpPost postRequest = new HttpPost(END_POINT_ROUTES);

        String credentials = USERNAME + ":" + PASSWORD;
        String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

        //postRequest.addHeader("Accept", "application/json");
        postRequest.addHeader("Content-type", "application/json");
        postRequest.addHeader("Authorization", "Basic " + base64EncodedCredentials);
        postRequest.addHeader("X-AppGlu-Environment", "staging");

        StringEntity se = new StringEntity(jsonValue);
        postRequest.setEntity(se);

        //
        HttpResponse response = client.execute(postRequest);

        return response;
    }

    /**
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line   = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }
        inputStream.close();

        return result;
    }
}
