package at_challenge.deaguiar.daniel.atchalleng;

/**
 * Created by daniel on 14/06/15.
 */
public class BusStop {
    private int mId;
    private String mName;
    private int mSequence;
    private int mRouteId;

    public BusStop() {
        mId       = 0;
        mName     = "";
        mSequence = 0;
        mRouteId  = 0;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getSequence() {
        return mSequence;
    }

    public void setSequence(int sequence) {
        mSequence = sequence;
    }

    public int getRouteId() {
        return mRouteId;
    }

    public void setRouteId(int routeId) {
        mRouteId = routeId;
    }
}
