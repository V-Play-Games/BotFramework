/*
 * Copyright 2021 Vaibhav Nargwani
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.vpg.bot.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class ClassFilter {
    private final List<String> disabled = new ArrayList<>();
    private final List<String> enabled = new ArrayList<>();

    public ClassFilter disable(Class<?>... classes) {
        return setEnabled(false, classes);
    }

    public ClassFilter disable(String... classes) {
        return setEnabled(false, classes);
    }

    public ClassFilter enable(Class<?>... classes) {
        return setEnabled(true, classes);
    }

    public ClassFilter enable(String... classes) {
        return setEnabled(true, classes);
    }

    public ClassFilter setEnabled(boolean enable, Class<?>... classes) {
        return setEnabled(enable, Arrays.stream(classes).map(Class::getName).toArray(String[]::new));
    }

    public ClassFilter setEnabled(boolean enable, String... classes) {
        List<String> list = enable ? enabled : disabled;
        List<String> other = enable ? disabled : enabled;
        for (String clazz : classes) {
            String pattern = clazz.replaceAll("\\.", "\\.");
            if (other.contains(pattern)) {
                other.remove(pattern);
            } else {
                list.add(pattern);
            }
        }
        return this;
    }

    public Predicate<Class<?>> asPredicate() {
        return clazz -> Pattern.matches(String.join("|", enabled), clazz.getName())
            && !Pattern.matches(String.join("|", disabled), clazz.getName());
    }
}
