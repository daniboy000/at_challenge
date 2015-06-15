package at_challenge.deaguiar.daniel.atchalleng;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
    private BusStopList mBusStopList;
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
                    mBusStopList = new BusStopList(resultBus);
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

//    private class FetchBusStopData extends FetchDataFromServer {
//        private final static String END_POINT_STOPS = "https://api.appglu.com/v1/queries/findStopsByRouteId/run";
//
//        public FetchBusStopData(int id) {
//            mJsonRoute = "{\"params\": {\"routeId\": \"%" + id + "%\"}}";
//            mEndPoint  = END_POINT_STOPS;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            try {
//                // Check if the result is valid or is an error
//                Log.i("ROUTES", "RESULT: " + result);
//
//                JSONObject jsonObject = new JSONObject(result);
//                if (!jsonObject.isNull("error")) {
//                    JSONObject jsonError = jsonObject.getJSONObject("error");
//
//                    Log.i("ROUTES", jsonObject.getString("error"));
//                    Toast.makeText(getApplicationContext(), jsonError.getString("message"), Toast.LENGTH_LONG).show();
//                }
//                else {
//                    mBusStopList = new BusStopList(result);
//                    BusStopAdapter busAdapter = new BusStopAdapter(mBusStopList.getBusStopList());
//                    mStopListView.setAdapter(busAdapter);
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//    }
}
