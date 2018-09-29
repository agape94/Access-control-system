package dailyalex.com.openthedoor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by alex on 10.04.2018.
 */

public class LogsCustomAdapter extends ArrayAdapter<LogAction> {

    private ArrayList<LogAction> logsList;
    Context mContext;
    private int lastPosition = -1;

    public LogsCustomAdapter(ArrayList<LogAction> data, Context context) {
        super(context, R.layout.log_row_item, data);
        this.logsList = data;
        this.mContext = context;
    }

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LogAction act = getItem(position);
        User user = act.getLogUser();
        ViewHolder viewHolder;

        final View result;

        if(convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.log_row_item, parent, false);
            viewHolder.txtActionType = (TextView)convertView.findViewById(R.id.logActionType);
            viewHolder.txtUserName = (TextView)convertView.findViewById(R.id.logUserName);
            viewHolder.txtLogTime = (TextView)convertView.findViewById(R.id.logTime);
            viewHolder.txtLogDate = (TextView)convertView.findViewById(R.id.logDate);

            result = convertView;
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);

        viewHolder.txtActionType.setText(act.getLogType());
        viewHolder.txtUserName.setText(user.getFirstName() + "\n" + user.getLastName());
        String logTime = act.getLogTime();
        String logDate = act.getmLogDayName() + " " + act.getmLogDayNumber() + " " + act.getmLogMonth() + " " + act.getmLogYear();

        viewHolder.txtLogTime.setText(logTime);
        viewHolder.txtLogDate.setText(logDate);

        return convertView;
    }
    public void changeData(ArrayList<LogAction> newData)
    {
        logsList.clear();
        logsList = newData;
    }


    private static class ViewHolder {
        TextView txtActionType;
        TextView txtUserName;
        TextView txtLogTime;
        TextView txtLogDate;
    }
}
