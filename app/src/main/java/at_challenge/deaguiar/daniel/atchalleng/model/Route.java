package at_challenge.deaguiar.daniel.atchalleng.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class Route
 *
 *  @author Daniel Besen de Aguiar
 */
public class Route {
    int mId;
    String mShortName;
    String mLongName;
    Date mLastModifiedDate;
    int mAgencyId;

    public Route() {
        mId = 0;
        mShortName = "";
        mLongName  = "";
        mLastModifiedDate = null;
        mAgencyId = 0;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getShortName() {
        return mShortName;
    }

    public void setShortName(String shortName) {
        mShortName = shortName;
    }

    public String getLongName() {
        return mLongName;
    }

    public void setLongName(String longName) {
        mLongName = longName;
    }

    public Date getLastModifiedDate() {
        return mLastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        mLastModifiedDate = lastModifiedDate;
    }

    public void setDateFromString(String lastModifiedDate) {
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            mLastModifiedDate = formater.parse(lastModifiedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public int getAgencyId() {
        return mAgencyId;
    }

    public void setAgencyId(int agencyId) {
        mAgencyId = agencyId;
    }
}
