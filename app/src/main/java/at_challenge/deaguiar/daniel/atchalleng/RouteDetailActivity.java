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
import org.json.JSONException;
import java.util.ArrayList;

/**
 *
 */
public class RouteDetailActivity extends Activity implements FetchServerResultReceiver.Receiver {

    public static final String EXTRA_ROUTE_ID   = "id";
    public static final String EXTRA_ROUTE_NAME = "longName";
    private final static String END_POINT_STOPS = "https://api.appglu.com/v1/queries/findStopsByRouteId/run";

    private int mRouteId;
    private String mRouteName;
    private BusStopList mBusStopList;

    private TextView mRouteNameTextView;
    private ListView mStopListView;

    private FetchServerResultReceiver mResultReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_detail);

        mRouteId = getIntent().getIntExtra(EXTRA_ROUTE_ID, 0);
        mRouteName = getIntent().getStringExtra(EXTRA_ROUTE_NAME);

        Log.i("ROUTES", "ID: " + mRouteId);
        Log.i("ROUTES", "NAME: " + mRouteName);

        mRouteNameTextView = (TextView)findViewById(R.id.route_detail_name);
        mStopListView = (ListView)findViewById(R.id.route_detail_stop_list);

        //-----------------------------------------------------------------------
        mResultReceiver = new FetchServerResultReceiver(new Handler());
        mResultReceiver.setReceiver(this);

        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, FetchFromServerService.class);
        intent.putExtra(FetchFromServerService.INTENT_ID, mRouteId);
        intent.putExtra(FetchFromServerService.INTENT_URL, END_POINT_STOPS);
        intent.putExtra("receiver", mResultReceiver);

        startService(intent);

    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case FetchFromServerService.STATUS_FINISHED:
                mRouteNameTextView.setText(mRouteName + " - " + mRouteName);
                String result = resultData.getString("result");

                try {
                    mBusStopList = new BusStopList(result);
                    BusStopAdapter adapter = new BusStopAdapter(mBusStopList.getBusStopList());

                    mStopListView.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case FetchFromServerService.STATUS_ERROR:
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
}
