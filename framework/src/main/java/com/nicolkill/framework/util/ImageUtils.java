package com.nicolkill.framework.util;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Clase de utilidad que nos da herramientas para manejar imágenes
 *
 * Created by Nicol Acosta on 10/12/16.
 * nicol@parkiller.com
 */
public class ImageUtils {

    private static final String TAG = ImageUtils.class.getSimpleName();

    private static final float ROTATION_90 = 90;
    private static final float ROTATION_180 = ROTATION_90 * 2;

    /**
     * Analiza la foto y la rota si es necesario
     * @param photoPath ruta de la foto
     * @return misma ruta de la foto
     * @throws IOException si hay un error en la lectura o escritura del archivo
     */
    public static String rotateIfNecesary(String photoPath) throws IOException {
        ExifInterface ei = new ExifInterface(photoPath);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        Bitmap bitmap;
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
            case ExifInterface.ORIENTATION_ROTATE_180:
                bitmap = getBitmapFromPath(photoPath);
                bitmap = rotateImage(bitmap, ROTATION_90);
                return saveBitmap(photoPath, bitmap);
            case ExifInterface.ORIENTATION_ROTATE_270:
                bitmap = getBitmapFromPath(photoPath);
                bitmap = rotateImage(bitmap, ROTATION_180);
                return saveBitmap(photoPath, bitmap);
        }
        return photoPath;
    }

    /**
     * Rota la imagen enviada como parámetro en el ángulo enviado como parámetro
     * @param source imagen
     * @param angle ángulo
     * @return imagen con la rotación aplicada
     */
    private static Bitmap rotateImage(Bitmap source, float angle) {
        Bitmap retVal;
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        retVal = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        return retVal;
    }

    /**
     * Obtiene un bitmap de la ruta enviada como parámetro
     * @param photoPath ruta
     * @return bitmap del archivo
     */
    private static Bitmap getBitmapFromPath(String photoPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        return BitmapFactory.decodeFile(photoPath, options);
    }

    /**
     * Guarda una imagen en la ruta enviada como parámetro
     * @param photoPath ruta del archivo
     * @param bitmap imagen
     * @return ruta del archivo ya guardado
     * @throws IOException si hay un error en la lectura o escritura del archivo
     */
    private static String saveBitmap(String photoPath, Bitmap bitmap) throws IOException {
        FileOutputStream out = new FileOutputStream(photoPath);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        out.close();
        return photoPath;
    }

    /**
     * Obtiene el código en base64 de la imagen (objeto file) enviada como parámetro
     * @param pictureFile archivo
     * @return base 64 de la imagen
     */
    public static String getBase64Image(File pictureFile) {
        Bitmap bm = getBitmapFromPath(pictureFile.getAbsolutePath());
        return getBase64Image(bm);
    }

    /**
     * Obtiene el código en base 64 de la imagen enviada como parámetro
     * @param bitmap imagen
     * @return base 64 de la imagen
     */
    public static String getBase64Image(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] byteArray = baos.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

}

