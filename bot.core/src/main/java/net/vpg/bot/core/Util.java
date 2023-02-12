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

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.bson.Document;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Util {
    public static Pattern SPACE = Pattern.compile("\\s");
    public static Pattern SPACE_WITH_LINE = Pattern.compile("[\\r\\n\\s]");
    private static Random random;

    private Util() {
        // Utility Class
    }

    public static String toString(long ms) {
        ms /= 1000;
        long hr = ms / 3600;
        long min = (ms % 3600) / 60;
        long sec = ms % 60;
        return (hr == 0 ? "" : (hr < 10 ? "0" : "") + hr + ":") + (min < 10 ? "0" : "") + min + ":" + (sec < 10 ? "0" : "") + sec;
    }

    public static File makeFileOf(Object toBeWritten, String fileName) {
        File file = new File(fileName);
        try (PrintStream stream = new PrintStream(file)) {
            stream.println(toBeWritten);
        } catch (FileNotFoundException e) {
            // ignore
        }
        file.deleteOnExit();
        return file;
    }

    public static String getMethod(String s) {
        return s.substring(0, s.indexOf(':'));
    }

    public static String getArgs(String s) {
        return s.substring(s.indexOf(':') + 1);
    }

    public static String toProperCase(String a) {
        String[] b = a.split(" ");
        for (int i = 0; i < b.length; i++) {
            String s = b[i];
            b[i] = s.length() > 1 ? s.toUpperCase().charAt(0) + s.substring(1).toLowerCase() : s.toUpperCase();
        }
        return String.join(" ", b);
    }

    public static boolean containsAny(String b, String... a) {
        for (String s : a) if (b.contains(s)) return true;
        return false;
    }

    public static DataObject toDataObject(Document document) {
        DataObject data = DataObject.empty();
        document.forEach(data::put);
        return data;
    }

    public static Document toDocument(DataObject data) {
        Document document = new Document();
        data.toMap().forEach(document::put);
        return document;
    }

    public static <T> T getRandom(List<T> list) {
        return getRandom(list, getRandom());
    }

    public static <T> T getRandom(T[] array) {
        return getRandom(array, getRandom());
    }

    public static <T> T getRandom(List<T> list, Random random) {
        return list.get(random.nextInt(list.size()));
    }

    public static <T> T getRandom(T[] array, Random random) {
        return array[random.nextInt(array.length)];
    }

    private static Random getRandom() {
        return random == null ? random = new Random() : random;
    }

    public static <T extends GenericEvent> EventListener subscribeTo(Class<T> eventType, Consumer<T> action) {
        return e -> {
            if (eventType.isAssignableFrom(e.getClass())) {
                action.accept(eventType.cast(e));
            }
        };
    }

    public static <T, K> Collector<T, ?, Map<K, T>> groupingBy(Function<? super T, ? extends K> keyMapper) {
        return Collectors.toMap(keyMapper, UnaryOperator.identity());
    }

    public static <T, K> Map<K, T> group(List<T> list, Function<? super T, ? extends K> keyMapper) {
        return list.stream().collect(groupingBy(keyMapper));
    }

    public static String getProcessId(JDA jda) {
        return System.nanoTime() + "-" + jda.getShardInfo().getShardId();
    }
}
