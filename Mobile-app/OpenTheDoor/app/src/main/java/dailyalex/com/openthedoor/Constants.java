package dailyalex.com.openthedoor;

/**
 * Created by alex on 18.03.2018.
 */

public class Constants {
    public static final String LOGIN_USER_COMMAND                       = "login_user";
    public static final String LOGIN_USER_RESPONSE_OK                   = "login_ok";
    public static final String LOGIN_USER_RESPONSE_FAILED               = "login_failed";
    public static final String HOME_OPEN_DOOR_BUTTON                    = "home_open";
    public static final String HOME_OPEN_DOOR_BUTTON_RESPONSE_OK        = "home_open_ok";
    public static final String HOME_OPEN_DOOR_BUTTON_RESPONSE_FAILED    = "home_open_failed";
    public static final String HOME_OPEN_DOOR_FINGERPRINT               = "home_fingerprint";
    public static final String HOME_OPEN_DOOR_FINGERPRINT_RESPONSE_OK   = "home_fingerprint_ok";
    public static final String HOME_START_STREAMING                     = "home_start_stream";
    public static final String HOME_STOP_STREAMING                      = "home_stop_stream";
    public static final String ADMINISTRATOR_GET_USERS                  = "administrator_get_users";
    public static final String ADMINISTRATOR_USERS_CHANGED              = "administrator_users_changed";
    public static final String ADMINISTRATOR_LOGIN_COMMAND              = "administrator_login";
    public static final String ADMINISTRATOR_LOGIN_RESPONSE_OK          = "administrator_login_ok";
    public static final String ADMINISTRATOR_LOGIN_RESPONSE_FAILED      = "administrator_login_failed";
    public static final String TCP_BROADCAST_FILTER                     = "tcp_broadcast_filter";
    public static final String XML_VERSION                              = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    public static final String SERVER_DOWN                              = "server_down";
    public static final String CONNECTION_LOST                          = "connection_lost";
    public static final String CONNECTION_ESTABLISHED                   = "connection_established";
    public static final String FINGERPRINT_AUTH_SUCCESS                 = "success";
    public static final String FINGERPRINT_AUTH_FAILED                  = "failed";
    public static final String MESSAGE_STOP_FLAG                        = "message_stop";
    public static final String LOGS_LOGIN_COMMAND            			= "logs_login";
    public static final String LOGS_LOGIN_RESPONSE_OK            		= "logs_login_ok";
    public static final String LOGS_LOGIN_RESPONSE_FAILED     			= "logs_login_failed";
    public static final String LOGS_GET_LOG_LIST     		        	= "logs_get_log_list";
    public static final String HEADER_RECEIVED                          = "header_received";
    public static final String IMAGE_RECEIVED                           = "image_received";
    public static final String USER_LOGGED_IN_ACTION_TYPE               = "user_logged_in";
    public static final String USER_CHANGED_ACTION_TYPE                 = "user_changed";
    public static final String ADMIN_LOGGED_IN_ACTION_TYPE              = "admin_logged_in";
    public static final String DOOR_OPEN_ACTION_TYPE                    = "door_open";
    public static final int REQUEST_CODE_LOGOUT                         = 100;


//    public static final String SERVER_ADDRESS                         = "192.168.43.194"; //ANDROID laptop
//    public static final String SERVER_ADDRESS                         = "192.168.43.253"; //Android placa

//    public static final String SERVER_ADDRESS                           = "192.168.0.10"; //WIFI laptop
//    public static final String SERVER_ADDRESS                           = "192.168.0.178"; //WIFI placa

    public static final String SERVER_ADDRESS                           = "192.168.0.18"; //CABLU laptop
//    public static final String SERVER_ADDRESS                           = "192.168.0.87"; //CABLU placa


    public static final int TCP_PORT                                    = 3600;
    public static final int UDP_PORT                                    = 3603;
    public static final int UDP_BUFFER                                  = 516;
}
