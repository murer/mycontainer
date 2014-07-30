/*
 * Copyright 2008 Whohoo Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable
 * law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

package com.googlecode.mycontainer.util.cpscanner;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ListScannerListener extends ClassScannerListener {

    private final List<Class<?>> classes = new ArrayList<Class<?>>();

    private final List<URL> resources = new ArrayList<URL>();

    public void classFound(URL base, Class<?> clazz) {
        classes.add(clazz);
    }

    @Override
    public void resourceFound(URL base, URL resource) {
        if (isClass(resource)) {
            super.resourceFound(base, resource);
        } else {
            resources.add(resource);
        }
    }

    public List<Class<?>> getClasses() {
        return classes;
    }

    public List<URL> getResources() {
        return resources;
    }

}
