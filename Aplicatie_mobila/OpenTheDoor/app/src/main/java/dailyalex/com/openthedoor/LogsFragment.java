package dailyalex.com.openthedoor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by alex on 10.04.2018.
 */

public class LogsFragment extends Fragment implements TcpObserver{
    public static final String TAG = "LOGS_FRAGMENT";
    private ArrayList<LogAction> logs;
    ListView logsView;
    private static LogsCustomAdapter customAdapter;
    private ArrayList<LogAction> filteredLogs;
    String mXmlContent;

    private boolean     mThisMonth,
                         mLastWeek,
                         mAllTime,
                         mLoginActionType,
                         mOpenDoorActionType,
                         mUserChangedActionType,
                         mAdminActionType;

    @Override
    public void updateTcp(String message) {
        if(message.contains(Constants.XML_VERSION)){
            new XmlHandler().parseLogsXml(message);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.logs_layout, null, false);
    }

    //==============================================================================================
    void showFilterDialog() {
        final AlertDialog dialog;
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        final View dialogView = getLayoutInflater().inflate(R.layout.log_filter_dialog_layout, null);
        mBuilder.setView(dialogView);
        dialog = mBuilder.create();



        final RadioButton   mRbLastMonth;
        final RadioButton   mRbAllTime;
        final RadioButton   mRbLastWeek;
        final RadioGroup    mRadioGroup;

        final CheckBox      mCbLoginActionType;
        final CheckBox      mCbOpenDoorActionType;
        final CheckBox      mCbUserChangedActionType;
        final CheckBox      mCbAdminActionType;
        final Button        mCancelButton;
        final Button        mApplyButton;

        mRbAllTime   = (RadioButton) dialogView.findViewById(R.id.radioBtnAllTime);
        mRbLastMonth = (RadioButton) dialogView.findViewById(R.id.radioBtnLastWeek);
        mRbLastWeek  = (RadioButton) dialogView.findViewById(R.id.radioBtnLastWeek);

        mRadioGroup = (RadioGroup) dialogView.findViewById(R.id.radioButtonsFilter);

        mCbAdminActionType = (CheckBox) dialogView.findViewById(R.id.checkBoxLoginAdminAction);
        mCbLoginActionType = (CheckBox) dialogView.findViewById(R.id.checkBoxLoginAction);
        mCbOpenDoorActionType = (CheckBox) dialogView.findViewById(R.id.checkBoxDoorOpenAction);
        mCbUserChangedActionType = (CheckBox) dialogView.findViewById(R.id.checkBoxUserChanged);

        mCancelButton = (Button) dialogView.findViewById(R.id.btnCancelFilter);
        mApplyButton = (Button) dialogView.findViewById(R.id.btnApplyFilter);

        mApplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCbAdminActionType.isChecked()){
                    mAdminActionType = true;
                }else{
                    mAdminActionType = false;
                }

                if(mCbLoginActionType.isChecked()){
                    mLoginActionType = true;
                }else{
                    mLoginActionType = false;
                }

                if(mCbOpenDoorActionType.isChecked()){
                    mOpenDoorActionType = true;
                }else{
                    mOpenDoorActionType = false;
                }

                if(mCbUserChangedActionType.isChecked()){
                    mUserChangedActionType = true;
                }else{
                    mUserChangedActionType = false;
                }

                int radioButtonSelected = mRadioGroup.getCheckedRadioButtonId();
                switch (radioButtonSelected){
                    case R.id.radioBtnAllTime:
                        mAllTime = true;
                        mThisMonth = false;
                        mLastWeek  = false;
                        break;
                    case R.id.radioBtnLastMonth:
                        mAllTime = false;
                        mThisMonth = true;
                        mLastWeek  = false;
                        break;
                    case R.id.radioBtnLastWeek:
                        mAllTime = false;
                        mThisMonth = false;
                        mLastWeek  = true;
                        break;
                }
                dialog.dismiss();
                updateLogList();
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void updateLogList(){

        filteredLogs.clear();
        Date compareDate = new Date();

        if(mLastWeek){
            compareDate = getPrevDate(7);
        }else if(mThisMonth){
            compareDate = getPrevDate(30);
        }

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(compareDate);

        try {
            compareDate = df.parse(formattedDate);
        }catch (Exception e){
            e.printStackTrace();
        }

        for (int i = 0; i < logs.size(); i++) {
            LogAction log = logs.get(i);
            String logMonthString = log.getmLogMonth();

            String logDateStr = log.getmLogDayNumber() + "-" +
                    getMonthNumber(logMonthString) + "-" +
                    log.getmLogYear();

            Date logDate = new Date();
            try {
                logDate = df.parse(logDateStr);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (mThisMonth && betweenDates(logDate, compareDate) <= 31) {
                verifyAndAddLog(log);
                continue;
            }
            if (mLastWeek && betweenDates(logDate, compareDate) <= 7) {
                verifyAndAddLog(log);
                continue;
            }
            if(mAllTime)
            {
                verifyAndAddLog(log);
                continue;
            }
        }

//        for(int i = 0; i < filteredLogs.size() ; i++) {
//            LogAction log = filteredLogs.get(i);
//            if (mLoginActionType && !log.getLogType().equals(Constants.USER_LOGGED_IN_ACTION_TYPE)) {
//                filteredLogs.remove(log);
//            }else if(mOpenDoorActionType && !log.getLogType().equals(Constants.DOOR_OPEN_ACTION_TYPE)){
//                filteredLogs.remove(log);
//            }else if(mUserChangedActionType && !log.getLogType().equals(Constants.USER_CHANGED_ACTION_TYPE)){
//                filteredLogs.remove(log);
//            }else if(mAdminActionType && !log.getLogType().equals(Constants.ADMIN_LOGGED_IN_ACTION_TYPE)){
//                filteredLogs.remove(log);
//            }
//        }
        customAdapter = new LogsCustomAdapter(filteredLogs,getActivity());
        logsView.setAdapter(customAdapter);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Logs");
        setHasOptionsMenu(true);
        logsView = (ListView)view.findViewById(R.id.logsListView);
        logs = new ArrayList<LogAction>();
        filteredLogs = new ArrayList<LogAction>();
        mXmlContent = new String();

        customAdapter = new LogsCustomAdapter(logs,getActivity());
        logsView.setAdapter(customAdapter);

        TcpClient.getInstance().Attach(this);
        TcpClient.getInstance().sendMessageToServer(Constants.LOGS_GET_LOG_LIST);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.logs_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logs_filter:
                showFilterDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //==============================================================================================
    private class XmlHandler{
        //==============================================================================================
        void parseLogsXml(String usersXml){
            try {
                InputStream is = new ByteArrayInputStream(usersXml.getBytes("UTF-8"));

                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(is);

                Element element=doc.getDocumentElement();
                element.normalize();

                NodeList nList = doc.getElementsByTagName("action");

                for (int i=0; i<nList.getLength(); i++) {

                    Node node = nList.item(i);

                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element2 = (Element) node;
                        User user = new User();
                        LogAction act = new LogAction();
                        act.setLogType(getValue("type", element2));
                        act.setLogTime(getValue("time", element2));
                        act.setmLogDayName(getValue("day_name", element2));
                        act.setmLogDayNumber(getValue("day_number",element2));
                        act.setmLogMonth(getValue("month",element2));
                        act.setmLogYear(getValue("year",element2));
                        user.setFirstName(getValue("first_name", element2));
                        user.setLastName(getValue("last_name", element2));
                        user.setUsername(getValue("username", element2));
                        user.setPassword(getValue("password", element2));
                        user.setIsAdministrator(string2Bool(getValue("administrator", element2)));
                        act.setLogUser(user);
                        logs.add(act);
                        customAdapter.notifyDataSetChanged();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        //==============================================================================================
        boolean string2Bool(String m){
            if(m.equals("true")){
                return true;
            }else{
                return false;
            }
        }
        //==============================================================================================
        String bool2string(boolean b){
            if(b)
                return "true";
            else
                return "false";
        }
        //==============================================================================================
        private String getValue(String tag, Element element) {
            NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
            Node node = nodeList.item(0);
            return node.getNodeValue();
        }
    }
    //==============================================================================================
    private String getMonthNumber(String month){
        String logMonthNumber;
        switch (month){
            case "Jan":
                logMonthNumber = "01";
                break;
            case "Feb":
                logMonthNumber = "02";
                break;
            case "Mar":
                logMonthNumber = "03";
                break;
            case "Apr":
                logMonthNumber = "04";
                break;
            case "May":
                logMonthNumber = "05";
                break;
            case "Jun":
                logMonthNumber = "06";
                break;
            case "Jul":
                logMonthNumber = "07";
                break;
            case "Aug":
                logMonthNumber = "08";
                break;
            case "Sep":
                logMonthNumber = "09";
                break;
            case "Oct":
                logMonthNumber = "10";
                break;
            case "Nov":
                logMonthNumber = "11";
                break;
            case "Dec":
                logMonthNumber = "12";
                break;
            default:
                logMonthNumber = "";
                break;
        }
        return logMonthNumber;
    }
    //==============================================================================================
    private void verifyAndAddLog(LogAction log) {
        if (mLoginActionType && log.getLogType().equals(Constants.USER_LOGGED_IN_ACTION_TYPE)) {
            filteredLogs.add(log);
        }else if(mOpenDoorActionType && log.getLogType().equals(Constants.DOOR_OPEN_ACTION_TYPE)){
            filteredLogs.add(log);
        }else if(mUserChangedActionType && log.getLogType().equals(Constants.USER_CHANGED_ACTION_TYPE)){
            filteredLogs.add(log);
        }else if(mAdminActionType && log.getLogType().equals(Constants.ADMIN_LOGGED_IN_ACTION_TYPE)){
            filteredLogs.add(log);
        }
    }
    //==============================================================================================
    private Date getPrevDate(int numberOfDays) {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, - numberOfDays);
        return cal.getTime();
    }
    //==============================================================================================
    public static long betweenDates(Date firstDate, Date secondDate) {
        long days;
        long diff = secondDate.getTime() - firstDate.getTime();
        days = TimeUnit.DAYS.convert(diff,TimeUnit.MILLISECONDS);
        return days;
    }
    //==============================================================================================
}
