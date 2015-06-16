package at_challenge.deaguiar.daniel.atchalleng.util;

import android.os.AsyncTask;
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
 * Created by daniel on 14/06/15.
 */
public abstract class HttpRequesAsyncTask extends AsyncTask<Void, Void, String> {
    protected final static String USERNAME = "WKD4N7YMA1uiM8V";
    protected final static String PASSWORD = "DtdTtzMLQlA0hk2C1Yi5pLyVIlAQ68";

    protected String mJsonRoute;
    protected String mEndPoint;

    @Override
    protected String doInBackground(Void... params) {
        try {
            HttpResponse response = getHttpResponse(mJsonRoute);

            String result = "";
            InputStream inputStream = response.getEntity().getContent();
            if (inputStream != null) {
                result = convertInputStreamToString(inputStream);
            }

            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    protected HttpResponse getHttpResponse(String jsonValue) throws IOException{
        HttpClient client = new DefaultHttpClient();
        HttpPost postRequest = new HttpPost(mEndPoint);

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

    abstract protected void onPostExecute(String result);
}
