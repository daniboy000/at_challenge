package at_challenge.deaguiar.daniel.atchalleng;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Base64;
import android.util.Log;

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
 * Created by daniel on 15/06/15.
 */
public class FetchFromServerService extends IntentService {

    public static final String INTENT_ID = "id";
    public static final String INTENT_URL = "url";
    public static final int STATUS_FINISHED = 0;
    public static final int STATUS_ERROR = 1;

    private final static String USERNAME = "WKD4N7YMA1uiM8V";
    private final static String PASSWORD = "DtdTtzMLQlA0hk2C1Yi5pLyVIlAQ68";

    public FetchFromServerService() {
        super("FetchDataFromService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        final ResultReceiver resultReceiver = intent.getParcelableExtra("receiver");
        int id = intent.getIntExtra(INTENT_ID, 0);
        String url = intent.getStringExtra(INTENT_URL);

        Bundle bundle = new Bundle();

        String jsonValue = "{\"params\": {\"stopName\": \"%" + id + "%\"}}";

        try {
            String result = "";
            HttpResponse response = getHttpResponse(jsonValue, url);
            InputStream inputStream = response.getEntity().getContent();
            if (inputStream != null) {
                result = convertInputStreamToString(inputStream);
                Log.i("ROUTES", "RESULTS FETCH: " + result);

                bundle.putString("result", result);
                resultReceiver.send(STATUS_FINISHED, bundle);

                Log.i("ROUTES", "RESULTS FETCH: " + result);
            }
        } catch (IOException e) {
            bundle.putString(Intent.EXTRA_TEXT, e.toString());
            resultReceiver.send(STATUS_ERROR, bundle);
        }
    }

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
