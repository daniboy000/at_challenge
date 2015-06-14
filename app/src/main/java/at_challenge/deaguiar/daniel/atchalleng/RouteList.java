package at_challenge.deaguiar.daniel.atchalleng;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class RouteList
 * Responsible to manage all the routes the application uses
 *  @author Daniel Besen de Aguiar
 */
public class RouteList {
    public static final String TAG = "RouteList";

    public static final String ID = "id";
    public static final String SHORT_NAME = "shortName";
    public static final String LONG_NAME = "longName";
    public static final String MODIFIED_DATE = "lastModifiedDate";
    public static final String AGENCY_ID = "agencyId";

    private static final String ROWS = "rows";
    private ArrayList<Route> mRoutes;
    private static RouteList sRouteList;
    private Context mAppContext;

    private RouteList(Context appContext) {
        mRoutes = new ArrayList<Route>();
        mAppContext = appContext;
    }

    public static RouteList getInstance(Context appContext) {
        if (sRouteList == null) {
            sRouteList =  new RouteList(appContext.getApplicationContext());
        }
        return sRouteList;
    }

    public ArrayList<Route> getRoutes() {
        return mRoutes;
    }

    public Route getRoute(int id) {
        for (Route route : mRoutes) {
            if (route.getId() == id)
                return route;
        }
        return null;
    }

    /**
     * Gets a JSON String, parse it's data and save it in a ArrayList of Route
     * @param routes
     * @throws JSONException
     */
    public void setRoutes(String routes) throws JSONException {
        mRoutes.clear();

        JSONObject routesObject = new JSONObject(routes);
        JSONArray routesArray = routesObject.getJSONArray(ROWS);

        for (int i = 0; i < routesArray.length(); i++) {
            JSONObject routeJson = routesArray.getJSONObject(i);

            int id = 0;
            String shortName = "";
            String longName  = "";
            String modifiedDate = "";
            int agencyId = 0;

            if (!routeJson.isNull(ID))
                id = routeJson.getInt(ID);
            if (!routeJson.isNull(SHORT_NAME))
                shortName = routeJson.getString(SHORT_NAME);
            if (!routeJson.isNull(LONG_NAME))
                longName = routeJson.getString(LONG_NAME);
            if (!routeJson.isNull(MODIFIED_DATE))
                modifiedDate = routeJson.getString(MODIFIED_DATE);
            if (!routeJson.isNull(AGENCY_ID))
                agencyId = routeJson.getInt(AGENCY_ID);

            Route route = new Route();
            route.setId(id);
            route.setShortName(shortName);
            route.setLongName(longName);
            route.setDateFromString(modifiedDate);
            route.setAgencyId(agencyId);

            mRoutes.add(route);
        }
    }
}
