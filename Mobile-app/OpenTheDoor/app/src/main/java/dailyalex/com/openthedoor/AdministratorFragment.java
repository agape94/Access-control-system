package dailyalex.com.openthedoor;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


/**
 * Created by alex on 25.02.2018.
 */

public class AdministratorFragment extends Fragment
                                    implements TcpObserver{

    public static final String TAG = "ADMIN_FRAGMENT";
    ListView usersListView;
    ArrayList<User> usersList;
    private static UsersCustomAdapter adapter;
    String mXmlContent;

    //==============================================================================================
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.administrator_layout, null, false);
    }
    //==============================================================================================
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        getActivity().setTitle("Administrator");
        usersListView = (ListView)view.findViewById(R.id.usersList);
        usersList = new ArrayList<User>();
        mXmlContent = new String();
        TcpClient.getInstance().Attach(this);
        TcpClient.getInstance().sendMessageToServer(Constants.ADMINISTRATOR_GET_USERS);

        adapter = new UsersCustomAdapter(usersList, getActivity(), new UsersCustomAdapter.OnEditButtonClicked() {
            @Override
            public void onEditButtonClicked(User user, int position) {
                showEditDialog(user,position);
            }
        });
        usersListView.setAdapter(adapter);

        usersListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                showYesNoDialog(i, view);
                return true;
            }
        });
    }
    //==============================================================================================
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.administrator_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    //==============================================================================================
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.add_user:
                showAddDialog();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    //==============================================================================================
    void showEditDialog(final User user, final int position){
        final AlertDialog dialog;
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        final View dialogView = getLayoutInflater().inflate(R.layout.edit_add_user_dialog_layout, null);
        mBuilder.setView(dialogView);
        dialog = mBuilder.create();

        final EditText etFirstName, etLastName, etUsername, etPassword;
        final CheckBox cbIsAdministrator;
        final Button btnCancelEdit, btnOkEdit;

        etFirstName = (EditText)dialogView.findViewById(R.id.etFirstName);
        etLastName = (EditText)dialogView.findViewById(R.id.etLastName);
        etUsername = (EditText)dialogView.findViewById(R.id.etUsername);
        etPassword = (EditText)dialogView.findViewById(R.id.etPassword);
        cbIsAdministrator = (CheckBox)dialogView.findViewById(R.id.cbIsAdministrator);
        btnCancelEdit = (Button)dialogView.findViewById(R.id.btnCancelEdit);
        btnOkEdit = (Button)dialogView.findViewById(R.id.btnOkEdit);

        etFirstName.setText(user.getFirstName());
        etLastName.setText(user.getLastName());
        etUsername.setText(user.getUsername());
        etPassword.setText(user.getPassword());
        cbIsAdministrator.setChecked(user.isAdministrator());

        btnCancelEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnOkEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!etFirstName.getText().toString().matches("") &&
                        !etLastName.getText().toString().matches("") &&
                        !etUsername.getText().toString().matches("") &&
                        !etPassword.getText().toString().matches("")) {
                    usersList.get(position).setFirstName(etFirstName.getText().toString());
                    usersList.get(position).setLastName(etLastName.getText().toString());
                    usersList.get(position).setUsername(etUsername.getText().toString());
                    usersList.get(position).setPassword(etPassword.getText().toString());
                    usersList.get(position).setIsAdministrator(cbIsAdministrator.isChecked());

                    adapter.editUserAt(usersList.get(position), position);

                    Toast.makeText(view.getContext(), "User " + etFirstName.getText().toString() + " " +
                            etLastName.getText().toString() + " edited successfully", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    new XmlHandler().writeUsersXml();
                    TcpClient.getInstance().sendMessageToServer(Constants.ADMINISTRATOR_USERS_CHANGED + " " + mXmlContent);

                }else
                {
                    Toast.makeText(view.getContext(), "Missing information", Toast.LENGTH_LONG).show();
                }
            }
        });
        dialog.show();
    }
    @Override
    public void onStop() {
        super.onStop();
        TcpClient.getInstance().Detach(this);
    }
    //==============================================================================================
    void showAddDialog(){
        final AlertDialog dialog;
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        final View dialogView = getLayoutInflater().inflate(R.layout.edit_add_user_dialog_layout, null);
        mBuilder.setView(dialogView);
        dialog = mBuilder.create();

        final EditText etFirstName, etLastName, etUsername, etPassword;
        final CheckBox cbIsAdministrator;
        final Button btnCancelEdit, btnOkEdit;

        etFirstName = (EditText)dialogView.findViewById(R.id.etFirstName);
        etLastName = (EditText)dialogView.findViewById(R.id.etLastName);
        etUsername = (EditText)dialogView.findViewById(R.id.etUsername);
        etPassword = (EditText)dialogView.findViewById(R.id.etPassword);
        cbIsAdministrator = (CheckBox)dialogView.findViewById(R.id.cbIsAdministrator);
        btnCancelEdit = (Button)dialogView.findViewById(R.id.btnCancelEdit);
        btnOkEdit = (Button)dialogView.findViewById(R.id.btnOkEdit);

        btnCancelEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnOkEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                User user = new User();

                if(!etFirstName.getText().toString().matches("") &&
                   !etLastName.getText().toString().matches("") &&
                   !etUsername.getText().toString().matches("") &&
                   !etPassword.getText().toString().matches("")) {

                    //TcpClient.getInstance().sendMessageToServer(Constants.ADMINISTRATOR_USERS_CHANGED);

                    user.setFirstName(etFirstName.getText().toString());
                    user.setLastName(etLastName.getText().toString());
                    user.setUsername(etUsername.getText().toString());
                    user.setPassword(etPassword.getText().toString());
                    user.setIsAdministrator(cbIsAdministrator.isChecked());

                    usersList.add(user);
                    adapter.notifyDataSetChanged();
//                    Toast.makeText(getActivity(),"User " + etFirstName.getText().toString()+" "+
//                            etLastName.getText().toString()+" added successfully",Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    new XmlHandler().writeUsersXml();
                    TcpClient.getInstance().sendMessageToServer(Constants.ADMINISTRATOR_USERS_CHANGED + " " + mXmlContent);
                }else
                {
                    Toast.makeText(view.getContext(), "Missing information", Toast.LENGTH_LONG).show();
                }
            }
        });
        dialog.show();
    }
    //==============================================================================================
    void showYesNoDialog(final int position, final View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Confirm");
        builder.setMessage("Are you sure you want to delete user " + "\"" + usersList.get(position).getFirstName()
                +" "+ usersList.get(position).getLastName()+ "\"" + "?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                Snackbar.make(view,"User "+ "\"" + usersList.get(position).getFirstName()
                        +" "+ usersList.get(position).getLastName()+ "\"" +" deleted!",Snackbar.LENGTH_SHORT).show();
                usersList.remove(position);
                adapter.notifyDataSetChanged();
                dialog.dismiss();
                new XmlHandler().writeUsersXml();
                TcpClient.getInstance().sendMessageToServer(Constants.ADMINISTRATOR_USERS_CHANGED + " " + mXmlContent);
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
    //==============================================================================================

    //==============================================================================================
    @Override
    public void updateTcp(String message) {
        if(message.contains(Constants.XML_VERSION)){
            new XmlHandler().parseUsersXml(message);
        }
    }
    //==============================================================================================
    private class XmlHandler{
        private String  XML_VERSION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
                        ROOT_NODE_START = "<users_document>",
                        ROOT_NODE_END = "</users_document>",
                        USER_NODE_START = "<user>",
                        USER_NODE_END = "</user>",
                        FIRST_NAME_START = "<first_name>",
                        FIRST_NAME_END = "</first_name>",
                        LAST_NAME_START = "<last_name>",
                        LAST_NAME_END = "</last_name>",
                        USERNAME_START = "<username>",
                        USERNAME_END = "</username>",
                        PASSWORD_START = "<password>",
                        PASSWORD_END = "</password>",
                        ADMINISTRATOR_START = "<administrator>",
                        ADMINISTRATOR_END = "</administrator>";
        //==============================================================================================
        void parseUsersXml(String usersXml){
            try {
                InputStream is = new ByteArrayInputStream(usersXml.getBytes("UTF-8"));

                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(is);

                Element element=doc.getDocumentElement();
                element.normalize();

                NodeList nList = doc.getElementsByTagName("user");

                for (int i=0; i<nList.getLength(); i++) {

                    Node node = nList.item(i);

                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element2 = (Element) node;
                        User user = new User();
                        user.setFirstName(getValue("first_name", element2));
                        user.setLastName(getValue("last_name", element2));
                        user.setUsername(getValue("username", element2));
                        user.setPassword(getValue("password", element2));
                        user.setIsAdministrator(string2Bool(getValue("administrator", element2)));
                        usersList.add(user);
                        adapter.notifyDataSetChanged();
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
        void writeUsersXml(){
            mXmlContent = "";
            mXmlContent = mXmlContent.concat(XML_VERSION + "\n" + ROOT_NODE_START + "\n");
            for(int i = 0 ; i < usersList.size() ; i++){
                User user = usersList.get(i);
                mXmlContent = mXmlContent.concat("\t"+USER_NODE_START + "\n"+
                                                "\t\t"+FIRST_NAME_START + user.getFirstName() + FIRST_NAME_END+"\n"+
                                                "\t\t"+LAST_NAME_START + user.getLastName() + LAST_NAME_END+"\n"+
                                                "\t\t"+USERNAME_START + user.getUsername() + USERNAME_END+"\n"+
                                                "\t\t"+PASSWORD_START + user.getPassword() + PASSWORD_END+"\n"+
                                                "\t\t"+ADMINISTRATOR_START + bool2string(user.isAdministrator()) + ADMINISTRATOR_END+"\n"+
                                                "\t"+USER_NODE_END + "\n");
            }
            mXmlContent = mXmlContent.concat(ROOT_NODE_END);
        }
        private String getValue(String tag, Element element) {
            NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
            Node node = nodeList.item(0);
            return node.getNodeValue();
        }
    }
    //==============================================================================================
}
