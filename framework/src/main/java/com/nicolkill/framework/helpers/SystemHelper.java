package com.nicolkill.framework.helpers;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;

import com.nicolkill.framework.interfaces.ApplicationScreen;
import com.nicolkill.framework.R;
import com.nicolkill.framework.interfaces.CallbackResponse;
import com.nicolkill.framework.result.ActivityResultManager;
import com.nicolkill.framework.result.PermissionManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by nicolkill on 7/11/17.
 */

public class SystemHelper extends AbstractHelper {

    private static final String TAG = SystemHelper.class.getSimpleName();

    private static final String ACTION_SMS = "sms";
    private static final String TELEPHONE_PREFIX = "tel:";
    private static final String PICTURE_SUFIX = ".jpg";
    private static final String KEY_TEMPORAL_FILENAME = "temp_file_name";
    private static final String RETURN_DATA = "return-data";

    /**
     * Código identificador de la operación "Seleccionar foto de la galería
     */
    private static final int CODE_SELECT_PICTURE = 1;
    /**
     * Código identificador de la operación "Validar permisos"
     */
    private static final int PERMISSIONS_VALIDATION = 2;

    public static SystemHelper mInstance;

    public static SystemHelper getInstance(Application application) {
        if (mInstance == null) {
            mInstance = new SystemHelper(application);
        }
        return mInstance;
    }

    private SystemHelper(Application application) {
        super(application);
    }

    public int getResourceStringFromName(String name) {
        return getResourceFromName(name, "string");
    }

    public int getResourceLayoutFromName(String name) {
        return getResourceFromName(name, "layout");
    }

    public int getResourceDrawableFromName(String name) {
        return getResourceFromName(name, "drawable");
    }

    public int getResourceIdFromName(String name) {
        return getResourceFromName(name, "id");
    }

    public int getResourceFromName(String name, String type) {
        return getApplication().getResources().getIdentifier(name, type, getApplication().getPackageName());
    }

    /**
     * Valida si el usuario ha cedido los permisos enviados como parámetro, si no han sido cedidos, los pregunta al usuario
     * y vuelve a validar si se han cedido
     * @param screen callback del sistema
     * @param callbackResponse callback de la operación
     * @param permissions permisos a validar
     */
    public void requestPermissions(ApplicationScreen screen, CallbackResponse<HashMap<String, Boolean>> callbackResponse, String... permissions) {
        new PermissionManager(screen, permissions)
                .setCallback(callbackResponse)
                .request(PERMISSIONS_VALIDATION);
    }

    /**
     * Obtiene el teléfono actual del dispositivo
     * @return teléfono del dispositivo
     */
    public String getPhone() {
        TelephonyManager telephonyManager = (TelephonyManager) getApplication().getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getLine1Number();
    }

    /**
     * Abre la pantalla de la aplicación de mensajería por defecto del sistema
     * @param phone teléfono a enviar el mensaje
     */
    public void sendMessage(String phone) {
        getApplication()
                .startActivity(
                        new Intent(Intent.ACTION_VIEW)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                .setData(Uri.fromParts(ACTION_SMS, phone, null ))
                );
    }

    /**
     * Envía a la aplicación de cámara por defecto y realiza una llamada al teléfono enviado como parámetro
     * @param phone teléfono a llamar
     */
    public void makeCall(String phone) {
        Uri uriPhone = Uri.parse(TELEPHONE_PREFIX + phone);
        Intent intent = new Intent()
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .setAction(Intent.ACTION_DIAL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
            if (ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                intent.setAction(Intent.ACTION_CALL);
            }
        }
        getApplication().startActivity(intent.setData(uriPhone));
    }

    public void openInUrlInBrowser(String url) {
        getApplication().startActivity(
                new Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        );
    }

    public void pictureFrom(ApplicationScreen wrapper, final CallbackResponse<File> callback) {
        new ActivityResultManager(wrapper, getPictureFromIntent())
                .setCallback(new CallbackResponse<HashMap<String, Object>>() {
                    @Override
                    public void sendValue(HashMap<String, Object> response) {
                        int requestCode = (int) response.get(ActivityResultManager.REQUEST_CODE);
                        int resultCode = (int) response.get(ActivityResultManager.RESULT_CODE);
                        Intent data = (Intent) response.get(ActivityResultManager.DATA);
                        if (resultCode != Activity.RESULT_OK) {
                            return;
                        }
                        if (requestCode == CODE_SELECT_PICTURE) {
                            File imageFile = new File(getAppFolder(), getSharedPreferences().getString(KEY_TEMPORAL_FILENAME, null));
                            boolean isCamera = (data == null || data.getData() == null  || data.getData().toString().contains(imageFile.toString()));
                            if (isCamera) {
                                getSharedPreferences().edit()
                                        .remove(KEY_TEMPORAL_FILENAME)
                                        .apply();
                            } else {
                                imageFile = new File(getPathFromUri(data.getData()));
                            }
                            callback.sendValue(imageFile);
                        }
                    }

                    @Override
                    public void error(Exception e) {
                    }
                })
                .request(CODE_SELECT_PICTURE);
    }

    private Intent getPictureFromIntent() {
        Intent chooserIntent = null;
        List<Intent> intentList = new ArrayList<>();
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        String filename = System.currentTimeMillis() + PICTURE_SUFIX;
        getSharedPreferences().edit()
                .putString(KEY_TEMPORAL_FILENAME, filename)
                .apply();
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePhotoIntent.putExtra(RETURN_DATA, true);
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(getAppFolder(), filename)));
        addIntentsToList(intentList, pickIntent);
        addIntentsToList(intentList, takePhotoIntent);
        if (intentList.size() > 0) {
            chooserIntent = Intent.createChooser(intentList.remove(intentList.size() - 1), getApplication().getString(R.string.new_picture));
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new Parcelable[]{}));
        }
        return chooserIntent;
    }

    private List<Intent> addIntentsToList(List<Intent> list, Intent intent) {
        List<ResolveInfo> resInfo = getApplication().getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resInfo) {
            String packageName = resolveInfo.activityInfo.packageName;
            Intent targetedIntent = new Intent(intent);
            targetedIntent.setPackage(packageName);
            list.add(targetedIntent);
        }
        return list;
    }

    /**
     * Obtiene la carpeta por defecto de parkiller para archivos o fotos
     * @return ruta de la carpeta de parkiller en la memoria
     */
    public String getAppFolder() {
        String appFolder;
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            appFolder = null;
        } else {
            File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "ParkillerCamera");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            appFolder = directory.getAbsolutePath();
        }
        return appFolder;
    }

    /**
     * Obtiene la ruta de un archivo a partir de su URI
     * @param uri uri del archivo
     * @return si se encuentra en la memoria, la ruta del archivo
     */
    public String getPathFromUri(Uri uri) {
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = getApplication().getContentResolver().query(uri, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return picturePath;
    }

}
