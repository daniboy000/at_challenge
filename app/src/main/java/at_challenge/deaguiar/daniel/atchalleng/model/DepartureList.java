package at_challenge.deaguiar.daniel.atchalleng.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * DepartureList
 *
 * @author Daniel Besen de Aguiar
 */
public class DepartureList {

    private static final String DEPART_ID = "id";
    private static final String DEPART_CALENDAR = "calendar";
    private static final String DEPART_TIME = "time";

    ArrayList<Departure> mDepartures;

    public DepartureList() {
        mDepartures = new ArrayList<Departure>();
    }

    public DepartureList(String result) {
        mDepartures = new ArrayList<Departure>();
        try {
            setDepartures(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Departure> getDepartures(String calendarValue) {
        ArrayList<Departure> departures = new ArrayList<Departure>();
        for (Departure departure : mDepartures) {
            if (departure.getCalendar().equals(calendarValue))
                departures.add(departure);
        }

        return departures;
    }

    public ArrayList<Departure>getDepartures() {
        return mDepartures;
    }

    /**
     * Gets a JSON String, parse it's data and save it in a ArrayList of Departure
     * @param result
     * @throws JSONException
     */
    public void setDepartures(String result) throws JSONException {
        try {
            JSONObject jsonObject = new JSONObject(result);

            JSONArray jsonDepartures = jsonObject.getJSONArray("rows");
            for (int i = 0; i < jsonDepartures.length(); i++) {
                JSONObject jsonDepart = jsonDepartures.getJSONObject(i);

                int id = 0;
                String calendar = "";
                String time = "";

                // Check if values are valid
                if (!jsonDepart.isNull(DEPART_ID))
                    id = jsonDepart.getInt(DEPART_ID);
                if (!jsonDepart.isNull(DEPART_CALENDAR))
                    calendar = jsonDepart.getString(DEPART_CALENDAR);
                if (!jsonDepart.isNull(DEPART_TIME))
                    time = jsonDepart.getString(DEPART_TIME);

                Departure departure = new Departure();
                departure.setId(id);
                departure.setCalendar(calendar);
                departure.setTime(time);

                mDepartures.add(departure);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
