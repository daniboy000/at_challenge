package at_challenge.deaguiar.daniel.atchalleng;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.TextView;

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
import java.util.ArrayList;


public class RoutesListActivity extends ListActivity {

    RouteList mRouteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes_list);

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String address = intent.getStringExtra(SearchManager.QUERY);

            new FetchData(address).execute();
        }
    }

    void setupAdapter() {
        RouteAdapter routeAdapter = new RouteAdapter(mRouteList.getRoutes());

        setListAdapter(routeAdapter);

        routeAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_routes_list, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class RouteAdapter extends ArrayAdapter<Route> {

        public RouteAdapter(ArrayList<Route> routes) {
            super(getApplicationContext(), 0, routes);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.route_item_list, parent, false);
            }

            Route route = getItem(position);

            TextView shortNameTextView = (TextView)convertView.findViewById(R.id.list_item_short_name);
            shortNameTextView.setText(route.getShortName());

            TextView longNameTextView = (TextView)convertView.findViewById(R.id.list_item_long_name);
            longNameTextView.setText(route.getLongName());

            return convertView;
        }
    }

    private class FetchData extends AsyncTask<Void, Void, RouteList> {
        private final static String USERNAME = "WKD4N7YMA1uiM8V";
        private final static String PASSWORD = "DtdTtzMLQlA0hk2C1Yi5pLyVIlAQ68";
        private final static String END_POINT_ROUTES = "https://api.appglu.com/v1/queries/findRoutesByStopName/run";

        private String mJsonRoute;

        public FetchData(String route) {
            mJsonRoute = "{\"params\": {\"stopName\": \"%" + route + "%\"}}";
        }

        @Override
        protected RouteList doInBackground(Void... params) {
            try {
                HttpResponse response = getHttpResponse(mJsonRoute);

                String result = "";
                InputStream inputStream = response.getEntity().getContent();
                if (inputStream != null) {
                    result = convertInputStreamToString(inputStream);
                }

                RouteList routeList = new RouteList();
                routeList.setRoutes(result);

                return routeList;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(RouteList routeList) {
            mRouteList = routeList;
            setupAdapter();
        }

        protected HttpResponse getHttpResponse(String jsonValue) throws IOException{
            HttpClient client = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost(END_POINT_ROUTES);

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
}
