/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package ch.qos.logback.classic.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import ch.qos.logback.core.util.EnvUtil;
import ch.qos.logback.core.util.Loader;

/**
 * @author Ceki G&uuml;lc&uuml;
 */
public class ClassicEnvUtil {

    /*
     * Used to replace the ClassLoader that the ServiceLoader uses for unit testing.
     * We need this to mock the resources the ServiceLoader attempts to load from
     * /META-INF/services thus keeping the projects src/test/resources clean (see
     * src/test/resources/README.txt).
     */
    //static ClassLoader testServiceLoaderClassLoader = null;

    static public boolean isGroovyAvailable() {
        return EnvUtil.isClassAvailable(ClassicEnvUtil.class, "groovy.lang.Binding");
    }
//
//    private static ClassLoader getServiceLoaderClassLoader() {
//        return testServiceLoaderClassLoader == null ? Loader.getClassLoaderOfClass(ClassicEnvUtil.class)
//                : testServiceLoaderClassLoader;
//    }

    public static <T> List<T> loadFromServiceLoader(Class<T> c, ClassLoader classLoader) {
        ServiceLoader<T> loader = ServiceLoader.load(c, classLoader);
        List<T> listOfT = new ArrayList<>();
        Iterator<T> it = loader.iterator();
        while(it.hasNext()) {
            T t = it.next();
            listOfT.add(t);
        }
        return listOfT;
    }

}
