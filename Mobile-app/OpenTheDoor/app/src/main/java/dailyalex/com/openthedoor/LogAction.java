package dailyalex.com.openthedoor;

import dailyalex.com.openthedoor.User;

/**
 * Created by alex on 10.04.2018.
 */

public class LogAction {
    private String mLogType;
    private String mLogTime;
    private String mLogDayName;
    private String mLogDayNumber;
    private String mLogMonth;
    private String mLogYear;
    private User mUser;

    public LogAction(){

    }

    public String getLogType() {
        return mLogType;
    }

    public void setLogType(String mLogType) {
        this.mLogType = mLogType;
    }

    public String getLogTime() {
        return mLogTime;
    }

    public void setLogTime(String mLogTime) {
        this.mLogTime = mLogTime;
    }

    public User getLogUser() {
        return mUser;
    }

    public void setLogUser(User mUser) {
        this.mUser = mUser;
    }

    public String getmLogDayName() {
        return mLogDayName;
    }

    public void setmLogDayName(String mLogDayName) {
        this.mLogDayName = mLogDayName;
    }

    public String getmLogDayNumber() {
        return mLogDayNumber;
    }

    public void setmLogDayNumber(String mLogDayNumber) {
        this.mLogDayNumber = mLogDayNumber;
    }

    public String getmLogMonth() {
        return mLogMonth;
    }

    public void setmLogMonth(String mLogMonth) {
        this.mLogMonth = mLogMonth;
    }

    public String getmLogYear() {
        return mLogYear;
    }

    public void setmLogYear(String mLogYear) {
        this.mLogYear = mLogYear;
    }


}
