package at_challenge.deaguiar.daniel.atchalleng.model;

/**
 * Departure
 *
 * @author Daniel Besen de Aguiar
 */
public class Departure {
    private int mId;
    private String mCalendar;
    private String mTime;

    public Departure() {
        mId = 0;
        mCalendar = "";
        mTime = "";
    }

    public Departure(int id, String calendar, String time) {
        mId = id;
        mCalendar = calendar;
        mTime = time;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getCalendar() {
        return mCalendar;
    }

    public void setCalendar(String calendar) {
        mCalendar = calendar;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }
}
