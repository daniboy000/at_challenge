package at_challenge.deaguiar.daniel.atchalleng.controller;

import android.app.Activity;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
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

import at_challenge.deaguiar.daniel.atchalleng.util.HttpRequesAsyncTask;
import at_challenge.deaguiar.daniel.atchalleng.service.DownloadIntentService;
import at_challenge.deaguiar.daniel.atchalleng.service.DownloadResultReceiver;
import at_challenge.deaguiar.daniel.atchalleng.model.BusStop;
import at_challenge.deaguiar.daniel.atchalleng.model.Departure;
import at_challenge.deaguiar.daniel.atchalleng.model.DepartureList;
import at_challenge.deaguiar.daniel.atchalleng.model.Route;
import at_challenge.deaguiar.daniel.atchalleng.model.RouteList;
import at_challenge.deaguiar.daniel.atchalleng.R;

/**
 *
 */
public class RouteDetailActivity extends Activity implements DownloadResultReceiver.Receiver {

    public static final String EXTRA_ROUTE_ID   = "id";
    public static final String EXTRA_ROUTE_NAME = "longName";
    private final static String END_POINT_STOPS = "https://api.appglu.com/v1/queries/findStopsByRouteId/run";
    private final static String END_POINT_DEPARTURES = "https://api.appglu.com/v1/queries/findDeparturesByRouteId/run";

    private int mRouteId;
    private String mRouteName;
    private BusStop.BusStopList mBusStopList;
    private DepartureList mDepartureList;

    private TextView mRouteNameTextView;
    private ListView mStopListView;
    private ListView mWeekdayListView;
    private ListView mStaturdayListView;
    private ListView mSundayListView;

    private DownloadResultReceiver mResultReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_detail);

        mRouteId   = getIntent().getIntExtra(EXTRA_ROUTE_ID, 0);
        mRouteName = getIntent().getStringExtra(EXTRA_ROUTE_NAME);

        Log.i("ROUTES", "ID: " + mRouteId);
        Log.i("ROUTES", "NAME: " + mRouteName);

        mRouteNameTextView = (TextView)findViewById(R.id.route_detail_name);
        mStopListView      = (ListView)findViewById(R.id.route_detail_stop_list);
        mWeekdayListView   = (ListView)findViewById(R.id.route_detail_departure_list_weekday);
        mStaturdayListView = (ListView)findViewById(R.id.route_detail_departure_list_saturday);
        mSundayListView    = (ListView)findViewById(R.id.route_detail_departure_list_sunday);

        // Prepare for fetching data from Server
        mResultReceiver = new DownloadResultReceiver(new Handler());
        mResultReceiver.setReceiver(this);

        // Setting intent for DownloadResultReceiver
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, DownloadIntentService.class);
        intent.putExtra(DownloadIntentService.INTENT_ID, mRouteId);
        intent.putExtra(DownloadIntentService.URL_BUS_STOP, END_POINT_STOPS);
        intent.putExtra(DownloadIntentService.URL_BUS_DEPARTURE, END_POINT_DEPARTURES);
        intent.putExtra("receiver", mResultReceiver);

        startService(intent);

//        new FetchBusStopData(mRouteId).execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("Routes", "ON DESCTROY");
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case DownloadIntentService.STATUS_FINISHED:
                mRouteNameTextView.setText(mRouteId + " - " + mRouteName);
                String resultBus = resultData.getString(DownloadIntentService.RESULT_BUS);
                String resultDepart = resultData.getString(DownloadIntentService.RESULT_DEPARTURE);

                try {
                    // Set BusStop Adapter
                    mBusStopList = new BusStop.BusStopList(resultBus);
                    BusStopAdapter busAdapter = new BusStopAdapter(mBusStopList.getBusStopList());
                    mStopListView.setAdapter(busAdapter);

                    // List of Departures
                    mDepartureList = new DepartureList(resultDepart);

                    // set Weekday Departure Adapter
                    ArrayList<Departure> weekdayDeparts = mDepartureList.getDepartures("WEEKDAY");
                    DepartureAdapter weekdayAdapter = new DepartureAdapter(weekdayDeparts);
                    mWeekdayListView.setAdapter(weekdayAdapter);

                    // set Saturday Departure Adapter
                    ArrayList<Departure> saturdayDeparts = mDepartureList.getDepartures("SATURDAY");
                    DepartureAdapter saturdayAdapter = new DepartureAdapter(saturdayDeparts);
                    mStaturdayListView.setAdapter(saturdayAdapter);

                    for (Departure dep : saturdayDeparts) {
                        Log.i("ROUTES", "SATURDAY DEPARTS: " + dep.getTime());
                    }

                    // set Sunday Departure Adapter
                    ArrayList<Departure> sundayDeparts = mDepartureList.getDepartures("SUNDAY");
                    DepartureAdapter sundayAdapter = new DepartureAdapter(sundayDeparts);
                    mSundayListView.setAdapter(sundayAdapter);

                    for (Departure dep : sundayDeparts) {
                        Log.i("ROUTES", "SUNDAY DEPARTS: " + dep.getTime());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case DownloadIntentService.STATUS_ERROR:
                String error = resultData.getString(Intent.EXTRA_TEXT);
                break;
        }
    }

    private class BusStopAdapter extends ArrayAdapter<BusStop> {

        public BusStopAdapter(ArrayList<BusStop> busStops) {
            super(getApplicationContext(), 0, busStops);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.route_detail_bus_stop_item, null);
            }

            BusStop busStop = getItem(position);

            TextView busStopName = (TextView)convertView.findViewById(R.id.route_detail_bus_stop_item_name);
            busStopName.setText(busStop.getName());

            return convertView;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }
    }

    private class DepartureAdapter extends ArrayAdapter<Departure> {

        public DepartureAdapter(ArrayList<Departure> departures) {
            super(getApplicationContext(), 0, departures);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.route_detail_departure_item, null);
            }

            Departure departure = getItem(position);

            TextView busStopName = (TextView)convertView.findViewById(R.id.route_detail_departure_time);
            busStopName.setText(departure.getTime());

            return convertView;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }
    }

    /**
     *
     */
    public static class RoutesListActivity extends ListActivity {

        RouteList mRouteList;
        private ListView mListView;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_routes_list);

            mRouteList = new RouteList();
            RouteAdapter adapter = new RouteAdapter(mRouteList.getRoutes());
            setListAdapter(adapter);

            mListView = (ListView)findViewById(android.R.id.list);
            mListView.setEmptyView(findViewById(android.R.id.empty));

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
            i.putExtra(EXTRA_ROUTE_ID, route.getId());
            i.putExtra(EXTRA_ROUTE_NAME, route.getLongName());

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

            public FetchRouteData(String route) {
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
                    else {
                        mRouteList.setRoutes(result);
                        setupAdapter();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
