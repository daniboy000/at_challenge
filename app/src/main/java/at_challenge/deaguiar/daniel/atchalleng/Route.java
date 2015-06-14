package at_challenge.deaguiar.daniel.atchalleng;

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

    public Route(int id, String shortName, String longName, Date modifiedDate, int agencyId) {
        mId = id;
        mShortName = shortName;
        mLongName  = longName;
        mLastModifiedDate = modifiedDate;
        mAgencyId = agencyId;
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

    public int getAgencyId() {
        return mAgencyId;
    }

    public void setAgencyId(int agencyId) {
        mAgencyId = agencyId;
    }
}
