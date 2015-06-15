package at_challenge.deaguiar.daniel.atchalleng;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class RouteDetailActivity extends Activity {

    public static final String EXTRA_ROUTE_ID = "id";
    public static final String EXTRA_ROUTE_NAME = "longName";

    private int mRouteId;
    private String mRouteName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_detail);

        mRouteId = getIntent().getIntExtra(EXTRA_ROUTE_ID, 0);
        mRouteName = getIntent().getStringExtra(EXTRA_ROUTE_NAME);

        Log.i("ROUTES", "ID: " + mRouteId);
        Log.i("ROUTES", "NAME: " + mRouteName);

        new FetchStopData(mRouteId).execute();

        new FetchDepartureData(mRouteId).execute();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_route_detail, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    private class FetchDepartureData extends FetchDataFromServer {
        private final static String END_POINT_DEPARTURES = "https://api.appglu.com/v1/queries/findDeparturesByRouteId/run";

        public FetchDepartureData(int routeId) {
            mJsonRoute = "{\"params\": {\"stopName\": \"%" + routeId + "%\"}}";
            mEndPoint = END_POINT_DEPARTURES;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i("ROUTES", "DEPARTURE: " + result);
        }
    }

    private class FetchStopData extends FetchDataFromServer {
        private final static String END_POINT_STOPS = "https://api.appglu.com/v1/queries/findStopsByRouteId/run";

        public FetchStopData(int routeId) {
            mJsonRoute = "{\"params\": {\"stopName\": \"%" + routeId + "%\"}}";
            mEndPoint = END_POINT_STOPS;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i("ROUTES", "STOP: " + result);
        }
    }
}
