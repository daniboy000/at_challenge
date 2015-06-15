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
import android.widget.ListView;
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

            new FetchRouteData(address).execute();
        }
    }

    void setupAdapter() {
        RouteAdapter routeAdapter = new RouteAdapter(mRouteList.getRoutes());

        setListAdapter(routeAdapter);

        routeAdapter.notifyDataSetChanged();
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        Route route = ((RouteAdapter)getListAdapter()).getItem(position);

        Log.i("ROUTE", "Route item: " + route.getLongName());

        Intent i = new Intent(getApplicationContext(), RouteDetailActivity.class);
        i.putExtra(RouteDetailActivity.EXTRA_ROUTE_ID, route.getId());
        i.putExtra(RouteDetailActivity.EXTRA_ROUTE_NAME, route.getLongName());

        startActivity(i);
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

    private class FetchRouteData extends FetchDataFromServer {
        private final static String END_POINT_ROUTES = "https://api.appglu.com/v1/queries/findRoutesByStopName/run";

        public FetchRouteData(String route) {
            mJsonRoute = "{\"params\": {\"stopName\": \"%" + route + "%\"}}";
            mEndPoint  = END_POINT_ROUTES;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                mRouteList = new RouteList();
                mRouteList.setRoutes(result);
                setupAdapter();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
