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
 * Created by alex on 25.02.2018.
 */

public class UsersCustomAdapter extends ArrayAdapter<User> implements View.OnClickListener{

    private ArrayList<User> usersList;
    Context mContext;
    private int lastPosition = -1;
    OnEditButtonClicked meditButtonClickedListener;

    public UsersCustomAdapter(ArrayList<User> data, Context context, OnEditButtonClicked listener) {
        super(context, R.layout.users_row_item, data);
        this.usersList = data;
        this.mContext=context;
        meditButtonClickedListener = listener;
    }



    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        User user = getItem(position);
        ViewHolder viewHolder;

        final View result;

        if(convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.users_row_item, parent, false);
            viewHolder.txtFirstName = (TextView)convertView.findViewById(R.id.userFirstName);
            viewHolder.txtLastName = (TextView)convertView.findViewById(R.id.userLastName);
            viewHolder.txtUsername = (TextView)convertView.findViewById(R.id.userUsername);
            viewHolder.txtIsAdministrator = (TextView)convertView.findViewById(R.id.userIsAdministrator);
            viewHolder.editUser = (ImageView)convertView.findViewById(R.id.ivEditUser);

            result = convertView;
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);

        viewHolder.txtFirstName.setText(user.getFirstName());
        viewHolder.txtLastName.setText(user.getLastName());
        viewHolder.txtUsername.setText(user.getUsername());

        if(user.isAdministrator())
        {
            viewHolder.txtIsAdministrator.setText("admin");
        }else{
            viewHolder.txtIsAdministrator.setText("user");
        }

        viewHolder.editUser.setOnClickListener(this);
        viewHolder.editUser.setTag(position);

        return convertView;
    }


    private static class ViewHolder {
        TextView txtFirstName;
        TextView txtLastName;
        TextView txtUsername;
        TextView txtIsAdministrator;
        ImageView editUser;
    }

    @Override
    public void onClick(View view) {
        int position = (Integer)view.getTag();
        Object object = getItem(position);
        User selectedUser = (User)object;

        switch (view.getId())
        {
            case R.id.ivEditUser:

                meditButtonClickedListener.onEditButtonClicked(selectedUser,position);
        }
    }

    public interface OnEditButtonClicked{
        public void onEditButtonClicked(User user, int position);
    }

    void editUserAt(User user, int position){
        usersList.set(position,user);
        notifyDataSetChanged();
    }

    void addNewUser(User user){
        usersList.add(user);
        notifyDataSetChanged();
    }

}
