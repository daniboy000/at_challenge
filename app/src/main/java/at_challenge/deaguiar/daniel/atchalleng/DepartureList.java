package at_challenge.deaguiar.daniel.atchalleng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by daniel on 15/06/15.
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
        setDepartures(result);
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

    public void setDepartures(String result) {
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
