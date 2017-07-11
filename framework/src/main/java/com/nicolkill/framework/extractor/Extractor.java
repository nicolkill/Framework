package com.nicolkill.framework.extractor;

import android.app.Application;
import android.view.View;

import com.nicolkill.framework.extractor.presenter.PresenterLifecycle;
import com.nicolkill.framework.extractor.view.BindLayout;
import com.nicolkill.framework.extractor.view.BindOnClick;
import com.nicolkill.framework.extractor.view.BindOnLongClick;
import com.nicolkill.framework.extractor.view.BindView;
import com.nicolkill.framework.interfaces.ApplicationScreen;
import com.nicolkill.framework.mvp.Presenter;
import com.nicolkill.framework.mvp.PresenterView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;

/**
 * Created by nicolkill on 6/2/17.
 */

public class Extractor {

    private static final String TAG = Extractor.class.getSimpleName();

    public static Presenter createPresenter(ApplicationScreen object) {
        Class<? extends Presenter> clazz = (Class) ((ParameterizedType) object.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        Class<? extends PresenterView> view = null;
        for (Class c: object.getClass().getInterfaces()) {
            if (PresenterView.class.isAssignableFrom(c)) {
                view = c;
            }
        }
        if (view == null) {
            throw new IllegalStateException("You need implements a PresenterView interface");
        }
        try {
            return clazz.getConstructor(
                    Application.class,
                    view
            ).newInstance(
                    object.getApplication(),
                    object
            );
        } catch (Exception e) {
            object.showDebugError(e);
            throw new IllegalStateException("You need set a default constructor");
        }
    }

    public static int getScreenLayout(ApplicationScreen object) {
        Class clazz = object.getClass();
        if (clazz.isAnnotationPresent(BindLayout.class)) {
            return ((BindLayout) clazz.getAnnotation(BindLayout.class)).value();
        } else {
            throw new IllegalStateException("The object has no pressent BindLayout annotation");
        }
    }

    public static void bindFields(ApplicationScreen object) throws IllegalAccessException {
        Field fields[] = object.getClass().getDeclaredFields();
        for (Field field: fields) {
            if (field.isAnnotationPresent(BindView.class)) {
                int id = field.getAnnotation(BindView.class).value();
                if (id == BindView.NO_ID) {
                    id = object.getApplication().getResources().getIdentifier(field.getName(), "id", object.getApplication().getPackageName());
                }
                if (id == BindView.NO_ID) {
                    throw new IllegalArgumentException("The view " + field.getName() + " need be named like its id or add id on BindView(R.id.ofYourView)");
                }
                field.setAccessible(true);
                field.set(object, object.findView(id));
            }
        }
    }

    public static void bindMethods(final ApplicationScreen object) {
        Method methods[] = object.getClass().getDeclaredMethods();
        for (final Method method: methods) {
            if (method.isAnnotationPresent(BindOnClick.class)) {
                object.findView(method.getAnnotation(BindOnClick.class).value()).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        invoke(method, object, view);
                    }
                });
            }
            if (method.isAnnotationPresent(BindOnLongClick.class)) {
                object.findView(method.getAnnotation(BindOnLongClick.class).value()).setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        invoke(method, object, view);
                        return true;
                    }
                });
            }
        }
    }

    private static void invoke(Method method, ApplicationScreen object, View view) {
        try {
            Class parameters[] = method.getParameterTypes();
            boolean hasView = false;
            if (parameters.length == 1) {
                hasView = parameters[0].equals(View.class);
                if (!hasView) {
                    throw new IllegalStateException(method.getName() + " method can only have View parameter");
                }
            }
            method.setAccessible(true);
            if (hasView) {
                method.invoke(object, view);
            } else {
                method.invoke(object);
            }
        } catch (Exception e) {
            object.showDebugError(e);
        }
    }

    public static HashMap<PresenterLifecycle.Event, Method> getLifecycleEvents(Presenter presenter) {
        HashMap<PresenterLifecycle.Event, Method> lifecycleMethods = new HashMap<>();
        Method methods[] = presenter.getClass().getDeclaredMethods();
        for (Method method: methods) {
            if (method.isAnnotationPresent(PresenterLifecycle.class)) {
                method.setAccessible(true);
                PresenterLifecycle.Event event = method.getAnnotation(PresenterLifecycle.class).value();
                if (lifecycleMethods.containsKey(event)) {
                    throw new IllegalStateException("Only can be one metod annotated with " + event.name() + " lifecycle event");
                }
                if (method.getParameterTypes().length > 0) {
                    throw new IllegalStateException("The method " + method.getName() + " cant be any parameter");
                }
                switch (event) {
                    case ON_ANY:
                        lifecycleMethods.put(PresenterLifecycle.Event.ON_ANY, method);
                        break;
                    case ON_CREATE:
                        lifecycleMethods.put(PresenterLifecycle.Event.ON_CREATE, method);
                        break;
                    case ON_START:
                        lifecycleMethods.put(PresenterLifecycle.Event.ON_START, method);
                        break;
                    case ON_RESUME:
                        lifecycleMethods.put(PresenterLifecycle.Event.ON_RESUME, method);
                        break;
                    case ON_PAUSE:
                        lifecycleMethods.put(PresenterLifecycle.Event.ON_PAUSE, method);
                        break;
                    case ON_STOP:
                        lifecycleMethods.put(PresenterLifecycle.Event.ON_STOP, method);
                        break;
                    case ON_DESTROY:
                        lifecycleMethods.put(PresenterLifecycle.Event.ON_DESTROY, method);
                        break;
                }
            }
        }
        return lifecycleMethods;
    }

}
