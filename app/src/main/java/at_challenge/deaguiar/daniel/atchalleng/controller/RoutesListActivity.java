package at_challenge.deaguiar.daniel.atchalleng.controller;

import android.app.ActionBar;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import at_challenge.deaguiar.daniel.atchalleng.model.RouteList;
import at_challenge.deaguiar.daniel.atchalleng.model.Route;
import at_challenge.deaguiar.daniel.atchalleng.util.HttpRequesAsyncTask;
import at_challenge.deaguiar.daniel.atchalleng.R;

/**
 *
 */
public class RoutesListActivity extends ListActivity {

    RouteList mRouteList;
    private ListView mListView;
    private TextView mRouteTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes_list);

        Log.i("Routes", "ON CREATE MAIN");

        mRouteTitle = (TextView) findViewById(R.id.route_title);
        mListView = (ListView)findViewById(android.R.id.list);
        mRouteList = new RouteList();

        // Check for saved data
        if (savedInstanceState != null) {
            Log.i("ROUTES", "RECUPERANDO BUNDLE");
            mRouteList = (RouteList)savedInstanceState.getSerializable("LIST");
            String routeName = savedInstanceState.getString("ROUTE_NAME");
            mRouteTitle.setText(routeName);
            setupAdapter();
        }
        else {
            mRouteTitle.setText(R.string.initial_message);
        }

        handleIntent(getIntent());
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("Routes", "ON RESUME MAIN");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("Routes", "ON START MAIN");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("Routes", "ON PAUSE MAIN");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("ROUTES", "ON STOP MAIN");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("Routes", "ON DESTROY  MAIN");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i("ROUTES", "SALVANDO O BUNDLE");
        savedInstanceState.putSerializable("LIST", mRouteList);
        savedInstanceState.putSerializable("ROUTE_NAME", mRouteTitle.getText().toString());
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

    private class FetchRouteData extends HttpRequesAsyncTask {
        private final static String END_POINT_ROUTES = "https://api.appglu.com/v1/queries/findRoutesByStopName/run";
        private String mRoute;

        public FetchRouteData(String route) {
            mRoute = route;
            mJsonRoute = "{\"params\": {\"stopName\": \"%" + route + "%\"}}";
            mEndPoint  = END_POINT_ROUTES;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                // Check if the result is valid or is an error
                JSONObject jsonObject = new JSONObject(result);
                if (!jsonObject.isNull("error")) {
                    JSONObject jsonError = jsonObject.getJSONObject("error");

                    Log.i("ROUTES", jsonObject.getString("error"));
                    Toast.makeText(getApplicationContext(), jsonError.getString("message"), Toast.LENGTH_LONG).show();
                }
                else if (jsonObject.getJSONArray("rows").length() == 0) { // Check if the route was founded
                    Log.i("ROUTES", "result: " + result);
                    Log.i("ROUTES", "result: " + jsonObject.getJSONArray("rows").length());

                    Toast.makeText(getApplicationContext(), "Address Not Found", Toast.LENGTH_LONG).show();
                }
                else { // Show routes
                    mRouteTitle.setText(mRoute);
                    mRouteList.setRoutes(result);
                    setupAdapter();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
