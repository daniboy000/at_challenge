package at_challenge.deaguiar.daniel.atchalleng.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Base64;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * DownloadIntentService
 * Responsible to request data from server
 *
 * @author Daniel Besen de Aguiar
 */
public class DownloadIntentService extends IntentService {

    public static final String INTENT_ID = "id";
    public static final String URL_BUS_STOP = "url_stop";
    public static final String URL_BUS_DEPARTURE = "url_departure";
    public static final String RESULT_BUS = "result_bus";
    public static final String RESULT_DEPARTURE = "result_departure";

    public static final int STATUS_FINISHED = 0;
    public static final int STATUS_ERROR    = 1;

    private final static String USERNAME = "WKD4N7YMA1uiM8V";
    private final static String PASSWORD = "DtdTtzMLQlA0hk2C1Yi5pLyVIlAQ68";

    public DownloadIntentService() {
        super("FetchDataFromService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        final ResultReceiver resultReceiver = intent.getParcelableExtra("receiver");

        // Get id from intent
        int id = intent.getIntExtra(INTENT_ID, 0);

        // Get URL's from intent
        String urlStop = intent.getStringExtra(URL_BUS_STOP);
        String urlDepart = intent.getStringExtra(URL_BUS_DEPARTURE);

        Bundle bundle = new Bundle();
        String jsonValue = "{\"params\": {\"routeId\": \"" + id + "\"}}";

        try {
            String resultBus = "";
            String resultDepart = "";

            // Get response for Bus
            HttpResponse responseBus = getHttpResponse(jsonValue, urlStop);
            InputStream inputStreamBus = responseBus.getEntity().getContent();

            // Get response for Departures
            HttpResponse responseDepart = getHttpResponse(jsonValue, urlDepart);
            InputStream inputStreamDeparture = responseDepart.getEntity().getContent();

            // Check if response is valid
            if (inputStreamBus != null  && inputStreamDeparture != null) {
                resultBus = convertInputStreamToString(inputStreamBus);
                resultDepart = convertInputStreamToString(inputStreamDeparture);

                bundle.putString(RESULT_BUS , resultBus);
                bundle.putString(RESULT_DEPARTURE, resultDepart);
                resultReceiver.send(STATUS_FINISHED, bundle);
            }
        } catch (IOException e) {
            bundle.putString(Intent.EXTRA_TEXT, e.toString());
            resultReceiver.send(STATUS_ERROR, bundle);
        }
    }

    /**
     * Tries to fetch JSON values from url
     * @param jsonValue search for stop or departure id
     * @param url request url
     * @return server response
     * @throws IOException
     */
    protected HttpResponse getHttpResponse(String jsonValue, String url) throws IOException {
        HttpClient client = new DefaultHttpClient();
        HttpPost postRequest = new HttpPost(url);

        String credentials = USERNAME + ":" + PASSWORD;
        String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

        postRequest.addHeader("Content-type", "application/json");
        postRequest.addHeader("Authorization", "Basic " + base64EncodedCredentials);
        postRequest.addHeader("X-AppGlu-Environment", "staging");

        StringEntity se = new StringEntity(jsonValue);
        postRequest.setEntity(se);

        HttpResponse response = client.execute(postRequest);

        return response;
    }

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
