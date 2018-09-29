package dailyalex.com.openthedoor;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.fingerprint.FingerprintManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import static android.content.Context.FINGERPRINT_SERVICE;
import static android.content.Context.KEYGUARD_SERVICE;

/**
 * Created by alex on 25.02.2018.
 */

public class HomeFragment extends Fragment
                          implements TcpObserver,UdpObserver{

    public static final String TAG = "HOME_FRAGMENT";
    AlertDialog dialog;
    private TextView doorStatus;
    FingerprintChecker mFingerprintChecker;
    private Button btnUnlock, streamStartButton;
    ImageView imageView;
    private static User mUser;
    boolean streaming = false;

    static{
        System.loadLibrary("opencv_java3");
    }

    //==============================================================================================

    void openDoorAndCloseAfter(int miliseconds){
        doorStatus.setText("UNLOCKED");
        doorStatus.setTextColor(Color.GREEN);

        new CountDownTimer(miliseconds, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                doorStatus.setText("LOCKED");
                doorStatus.setTextColor(Color.RED);
            }
        }.start();
    }

    //==============================================================================================

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_fragment_layout, null, false);
    }

    //==============================================================================================

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Home");
        doorStatus = (TextView) view.findViewById(R.id.etDoorStatus);
        btnUnlock = (Button)view.findViewById(R.id.btnUnlock);
        streamStartButton = (Button)view.findViewById(R.id.btnStartStream);
        TcpClient.getInstance().Attach(this);
        UdpClient.getInstance().Attach(this);
        imageView = (ImageView)view.findViewById(R.id.framesImageView);
        mUser = MainActivity.getCurrentUser();

        btnUnlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = null;
                showLoginDialog();
                //Checking fingerprint hardware.....
                mFingerprintChecker.testFingerprintHardwareAndStartAuthetification(dialog);
            }
        });
        streamStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!streaming) {
                    streaming = true;
                    TcpClient.getInstance().sendMessageToServer(Constants.HOME_START_STREAMING);
                    UdpClient.getInstance().sendToUdp(Constants.HOME_START_STREAMING);
                    streamStartButton.setText("STOP STREAM");
                    streamStartButton.setTextColor(Color.RED);
                }else if(streaming){
                    streaming = false;
                    TcpClient.getInstance().sendMessageToServer(Constants.HOME_STOP_STREAMING);
                    UdpClient.getInstance().sendToUdp(Constants.HOME_STOP_STREAMING);
                    streamStartButton.setText("START STREAM");
                    streamStartButton.setTextColor(Color.BLACK);
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        TcpClient.getInstance().Detach(this);
    }

    @Override
    public void updateTcp(String message) {

        if(message.equals(Constants.HOME_OPEN_DOOR_BUTTON_RESPONSE_OK) ||
                message.equals(Constants.HOME_OPEN_DOOR_FINGERPRINT_RESPONSE_OK)){
            dialog.dismiss();
            dialog = null;
            openDoorAndCloseAfter(10000);

        }else if(message.equals(Constants.HOME_OPEN_DOOR_BUTTON_RESPONSE_FAILED)){
            Toast.makeText(getActivity(),"Wrong password!", Toast.LENGTH_LONG);
        }
    }

    @Override
    public void updateUdp(final byte[] image) {
        final Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
//        imageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, 640, 480, false));
//        Bitmap bmp = arrayToBitmap(image,640,480);

        Utils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageView.setImageBitmap(bmp);
            }
        });
    }

    //==============================================================================================

    private class FingerprintChecker {
        private Cipher cipher;
        private KeyStore keyStore;
        private KeyGenerator keyGenerator;
        private FingerprintManager.CryptoObject cryptoObject;
        private FingerprintManager fingerprintManager;
        private KeyguardManager keyguardManager;
        private static final String KEY_NAME = "yourKey";

        void testFingerprintHardwareAndStartAuthetification(final AlertDialog dialog) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //Get an instance of KeyguardManager and FingerprintManager//
                keyguardManager =
                        (KeyguardManager) getActivity().getSystemService(KEYGUARD_SERVICE);
                fingerprintManager =
                        (FingerprintManager) getActivity().getSystemService(FINGERPRINT_SERVICE);

                //Check whether the device has a fingerprint sensor//
                if (!fingerprintManager.isHardwareDetected()) {
                    // If a fingerprint sensor isn’t available, then inform the user that they’ll be unable to use your app’s fingerprint functionality//
                    Toast.makeText(getActivity(),"Your device doesn't support fingerprint authentication",Toast.LENGTH_SHORT).show();
                }
                //Check whether the user has granted your app the USE_FINGERPRINT permission//
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                    // If your app doesn't have this permission, then display the following text//
                    Toast.makeText(getActivity(),"Please enable the fingerprint permission",Toast.LENGTH_SHORT).show();
                }

                //Check that the user has registered at least one fingerprint//
                if (!fingerprintManager.hasEnrolledFingerprints()) {
                    // If the user hasn’t configured any fingerprints, then display the following message//
                    Toast.makeText(getActivity(),"No fingerprint configured. Please register at least one fingerprint in your device's Settings",Toast.LENGTH_SHORT).show();
                }

                //Check that the lockscreen is secured//
                if (!keyguardManager.isKeyguardSecure()) {
                    // If the user hasn’t secured their lockscreen with a PIN password or pattern, then display the following text//
                    Toast.makeText(getActivity(),"Please enable lockscreen security in your device's Settings",Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        generateKey();
                    } catch (FingerprintChecker.FingerprintException e) {
                        e.printStackTrace();
                    }
                    if (initCipher()) {
                        //If the cipher is initialized successfully, then create a CryptoObject instance//
                        cryptoObject = new FingerprintManager.CryptoObject(cipher);

                        // Here, I’m referencing the FingerprintHandler class that we’ll create in the next section. This class will be responsible
                        // for starting the authentication process (via the startAuth method) and processing the authentication process events//
                        final FingerprintHandler helper = new FingerprintHandler(getActivity(), new FingerprintHandler.OnFingerprintEvent() {
                            @Override
                            public void onAuthentificationEventOccured(String eventName) {

                                if (eventName.equals(Constants.FINGERPRINT_AUTH_SUCCESS)) {
                                    TcpClient.getInstance().sendMessageToServer(Constants.HOME_OPEN_DOOR_FINGERPRINT+" "+ mUser.getUsername() +" "+ mUser.getPassword());
                                    dialog.dismiss();
                                } else if (eventName.equals(Constants.FINGERPRINT_AUTH_FAILED)) {

                                }
                            }
                        });
                        helper.startAuth(fingerprintManager, cryptoObject);
                    }
                }
            }
        }

        private void generateKey() throws FingerprintException {
            try {
                // Obtain a reference to the Keystore using the standard Android keystore container identifier (“AndroidKeystore”)//
                keyStore = KeyStore.getInstance("AndroidKeyStore");

                //Generate the key//
                keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

                //Initialize an empty KeyStore//
                keyStore.load(null);

                //Initialize the KeyGenerator//
                keyGenerator.init(new

                        //Specify the operation(s) this key can be used for//
                        KeyGenParameterSpec.Builder(KEY_NAME,
                        KeyProperties.PURPOSE_ENCRYPT |
                                KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)

                        //Configure this key so that the user has to confirm their identity with a fingerprint each time they want to use it//
                        .setUserAuthenticationRequired(true)
                        .setEncryptionPaddings(
                                KeyProperties.ENCRYPTION_PADDING_PKCS7)
                        .build());

                //Generate the key//
                keyGenerator.generateKey();

            } catch (KeyStoreException
                    | NoSuchAlgorithmException
                    | NoSuchProviderException
                    | InvalidAlgorithmParameterException
                    | CertificateException
                    | IOException exc) {
                exc.printStackTrace();
                throw new FingerprintChecker.FingerprintException(exc);
            }
        }

        public boolean initCipher() {
            try {
                //Obtain a cipher instance and configure it with the properties required for fingerprint authentication//
                cipher = Cipher.getInstance(
                        KeyProperties.KEY_ALGORITHM_AES + "/"
                                + KeyProperties.BLOCK_MODE_CBC + "/"
                                + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            } catch (NoSuchAlgorithmException |
                    NoSuchPaddingException e) {
                throw new RuntimeException("Failed to get Cipher", e);
            }

            try {
                keyStore.load(null);
                SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                        null);
                cipher.init(Cipher.ENCRYPT_MODE, key);
                //Return true if the cipher has been initialized successfully//
                return true;
            } catch (KeyPermanentlyInvalidatedException e) {

                //Return false if cipher initialization failed//
                return false;
            } catch (KeyStoreException | CertificateException
                    | UnrecoverableKeyException | IOException
                    | NoSuchAlgorithmException | InvalidKeyException e) {
                throw new RuntimeException("Failed to init Cipher", e);
            }
        }

        private class FingerprintException extends Exception {
            public FingerprintException(Exception e) {
                super(e);
            }
        }
    }

    //==============================================================================================

    void showLoginDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        View mView = getLayoutInflater().inflate(R.layout.dialog_login, null);

        doorStatus.setTextColor(Color.RED);
        final EditText mPassword = (EditText) mView.findViewById(R.id.etDialogPassword);
        final Button btnLogin = (Button) mView.findViewById(R.id.btnLogin);

        mBuilder.setView(mView);
        dialog = mBuilder.create();

        EditText etUsername = (EditText) mView.findViewById(R.id.etDialogUsername) ;
        etUsername.setVisibility(View.GONE);

        mFingerprintChecker = new FingerprintChecker();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPassword.getText().toString() != null) {
                    TcpClient.getInstance().sendMessageToServer(Constants.HOME_OPEN_DOOR_BUTTON +" "+ mUser.getUsername() +" "+ mPassword.getText().toString());
                    //TODO: de trimis si username-ul odata cu parola introdusa de utilizator in UI (username din memorie)
                }
            }
        });
        dialog.show();
    }
//    Bitmap arrayToBitmap(byte[] image, int width, int height){
//        int[] RGBimage = new int[image.length];
//        for (int i = 0; i<image.length; i++){
//            Color pixel;
//            RGBimage[i++] = (int)pixel;
//        }
//
//        Bitmap createdBitmap = Bitmap.createBitmap(RGBimage,width,height,Bitmap.Config.ARGB_8888);
//        return createdBitmap;
//    }

}
