/*
 * Copyright (c) 2013. wyouflf (wyouflf@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.basedemo.annotation.diy;

import android.app.Activity;
import android.view.View;

import com.example.basedemo.annotation.view.EventListenerManager;
import com.example.basedemo.annotation.view.ViewInjectInfo;
import com.example.basedemo.annotation.view.annotation.event.EventBase;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


public class ViewUtils {

    private ViewUtils() {
    }

    public static void inject(Activity activity) {
        injectObject(activity, new ViewFinder(activity));
        injectEvent(activity);
    }

    @SuppressWarnings("ConstantConditions")
    private static void injectObject(Object handler, ViewFinder finder) {

        Class<?> handlerType = handler.getClass();

        // inject view
        Field[] fields = handlerType.getDeclaredFields();
        if (fields != null && fields.length > 0) {
            for (Field field : fields) {
                ViewInject viewInject = field.getAnnotation(ViewInject.class);
                if (viewInject != null) {
                    try {
                        View view = finder.findViewById(viewInject.value(), viewInject.parentId());
                        if (view != null) {
                            field.setAccessible(true);
                            field.set(handler, view);
                        }
                    } catch (Throwable e) {
//                        ILog.e(ViewUtils.class,e.getMessage(), e);
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static void injectEvent(final Activity activity) {
        Class<? extends Activity> clazz = activity.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        for (final Method method : methods) {
            OnClick click = method.getAnnotation(OnClick.class);
            if (click != null) {

                int[] viewId = click.value();
                method.setAccessible(true);
                Object listener = Proxy.newProxyInstance(View.OnClickListener.class.getClassLoader(),
                        new Class[]{View.OnClickListener.class}, new InvocationHandler() {
                            @Override
                            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                                return method.invoke(activity, args);
                            }
                        });

                try {
                    for (int id : viewId) {
                        View v = activity.findViewById(id);
                        Method setClickListener = v.getClass().getMethod("setOnClickListener", View.OnClickListener.class);
                        setClickListener.invoke(v, listener);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }




}
