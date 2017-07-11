package com.nicolkill.framework.result;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.nicolkill.framework.interfaces.ApplicationScreen;
import com.nicolkill.framework.exceptions.PermissionException;
import com.nicolkill.framework.result.base.ResultExecuter;
import com.nicolkill.framework.result.base.ResultManager;
import com.nicolkill.framework.result.base.ShadowSupportFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nicolkill on 6/23/17.
 */

public class PermissionManager extends ResultManager<HashMap<String, Boolean>> {

    private String[] mRequestedPermissions;

    public PermissionManager(
            ApplicationScreen screen,
            @NonNull final String[] requestedPermissions
    ) {
        super(screen);
        mRequestedPermissions = requestedPermissions;
    }

    @Override
    public ResultExecuter<HashMap<String, Boolean>> getResultExecuter() {
        return new PermissionExecuter();
    }

    private class PermissionExecuter extends ResultExecuter<HashMap<String, Boolean>> {

        @Override
        public void execute(
                ShadowSupportFragment fragment,
                int requestCode
        ) {
            mRequestCode = requestCode;
            HashMap<String, Boolean> grantedPermissions = new HashMap<>();
            for (String permission: mRequestedPermissions) {
                grantedPermissions.put(permission, ContextCompat.checkSelfPermission(fragment.getContext(), permission) == PackageManager.PERMISSION_GRANTED);
            }
            if (grantedPermissions.containsValue(false)) {
                ArrayList<String> permissionsToCheck = new ArrayList<>();
                for (Map.Entry<String, Boolean> pair: grantedPermissions.entrySet()) {
                    if (!pair.getValue()) {
                        permissionsToCheck.add(pair.getKey());
                    }
                }
                fragment.requestPermission(permissionsToCheck.toArray(new String[permissionsToCheck.size()]), requestCode);
            } else {
                mCallbackResponse.sendValue(grantedPermissions);
            }
        }

        @Override
        public void onActivityResult(ShadowSupportFragment fragment, int requestCode, int resultCode, Intent data) {
        }

        @Override
        public void onRequestPermissionsResult(
                ShadowSupportFragment fragment,
                int requestCode,
                @NonNull String[] permissions,
                @NonNull int[] grantResults
        ) {
            if (requestCode == mRequestCode) {
                HashMap<String, Boolean> grantedPermissions = new HashMap<>();
                for (int i = 0; i < permissions.length; i++) {
                    grantedPermissions.put(permissions[i], grantResults[i] == PackageManager.PERMISSION_GRANTED);
                }
                if (grantedPermissions.containsValue(false)) {
                    ArrayList<String> rejectedPermissions = new ArrayList<>();
                    ArrayList<String> blockedPermissions = new ArrayList<>();
                    for (Map.Entry<String, Boolean> pair: grantedPermissions.entrySet()) {
                        if (!pair.getValue()) {
                            rejectedPermissions.add(pair.getKey());
                            if (!fragment.shouldShowPermissionRationale(pair.getKey())) {
                                blockedPermissions.add(pair.getKey());
                            }
                        }
                    }
                    mCallbackResponse.error(new PermissionException(rejectedPermissions, blockedPermissions));
                } else {
                    mCallbackResponse.sendValue(grantedPermissions);
                }
            }
        }

    }

}
