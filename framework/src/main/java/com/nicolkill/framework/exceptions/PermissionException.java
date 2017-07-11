package com.nicolkill.framework.exceptions;

import java.util.ArrayList;

/**
 * Excepción que nos ayuda a identificar un error con referencia a los permisos
 *
 * Si se obtiene esta excepción es por que el usuario ha rechazado los permisos solicitados
 *
 * Created by Nicol Acosta on 10/7/16.
 * nicol@parkiller.com
 */
public class PermissionException extends Exception {

    private static final String TAG = PermissionException.class.getSimpleName();

    private ArrayList<String> mRejectedPermissions;
    private ArrayList<String> mBlockedPermissions;

    public PermissionException(ArrayList<String> rejectedPermissions, ArrayList<String> blockedPermissions) {
        super("Permissions rejected: "+rejectedPermissions.toString() + ", permissions blocked: " + blockedPermissions.toString());
        mRejectedPermissions = rejectedPermissions;
        mBlockedPermissions = blockedPermissions;
    }

    /**
     * Obtiene los permisos rechazados por el usuario
     * @return permisos
     */
    public ArrayList<String> getRejectedPermissions() {
        return mRejectedPermissions;
    }

    /**
     * Obtiene los permisos rechazados por el usuario a los que ha marcado "no volver a preguntar"
     * @return permisos
     */
    public ArrayList<String> getBlockedPermissions() {
        return mBlockedPermissions;
    }
}
