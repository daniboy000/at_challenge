package at_challenge.deaguiar.daniel.atchalleng.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * BusStopList
 *
 * @author Daniel Besen de Aguiar
 */
public class BusStopList {

    private static final String BUS_ROWS = "rows";
    private static final String BUS_ID = "id";
    private static final String BUS_ROUTE_ID = "route_id";
    private static final String BUS_SEQUENCE = "sequence";
    private static final String BUS_NAME = "name";

    ArrayList<BusStop> mBusStops;

    public BusStopList() {
        mBusStops = new ArrayList<BusStop>();
    }

    public BusStopList(String busStops) throws  JSONException {
        mBusStops = new ArrayList<BusStop>();
        setBusStops(busStops);
    }

    public ArrayList<BusStop> getBusStopList() {
        return mBusStops;
    }

    public BusStop getBusStop(int id) {
        for (BusStop bus : mBusStops) {
            if (bus.getId() == id)
                return bus;
        }
        return null;
    }

    public void setBusStops(String busStops) throws JSONException {

        JSONObject stopObject = new JSONObject(busStops);
        JSONArray stopArray = stopObject.getJSONArray(BUS_ROWS);

        for (int i = 0; i < stopArray.length(); i++) {
            JSONObject busJson = stopArray.getJSONObject(i);

            int id = 0;
            int routeId  = 0;
            int sequence = 0;
            String name  = "";

            if (!busJson.isNull(BUS_ID))
                id = busJson.getInt(BUS_ID);
            if (!busJson.isNull(BUS_ROUTE_ID))
                routeId = busJson.getInt(BUS_ROUTE_ID);
            if (!busJson.isNull(BUS_SEQUENCE))
                sequence = busJson.getInt(BUS_SEQUENCE);
            if (!busJson.isNull(BUS_NAME))
                name = busJson.getString(BUS_NAME);

            BusStop busStop = new BusStop();
            busStop.setId(id);
            busStop.setRouteId(routeId);
            busStop.setSequence(sequence);
            busStop.setName(name);

            mBusStops.add(busStop);
        }
    }
}

