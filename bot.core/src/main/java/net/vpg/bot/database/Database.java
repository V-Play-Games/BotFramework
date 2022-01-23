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
package net.vpg.bot.database;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;
import net.vpg.bot.framework.Bot;
import org.bson.Document;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Database {
    static {
        ((Logger) LoggerFactory.getLogger("org.org.mongodb.driver.connection")).setLevel(Level.INFO);
        ((Logger) LoggerFactory.getLogger("org.mongodb.driver.cluster")).setLevel(Level.INFO);
        ((Logger) LoggerFactory.getLogger("org.mongodb.driver.protocol.command")).setLevel(Level.INFO);
    }

    private final Map<String, MongoCollection<Document>> collections = new HashMap<>();
    private final MongoClient client;
    private final MongoDatabase db;
    private final Bot bot;

    public Database(String connectionString, String dbName, Bot bot) {
        this.bot = bot;
        this.client = MongoClients.create(connectionString);
        this.db = client.getDatabase(dbName);
        db.listCollectionNames().forEach(this::registerCollection);
    }

    public Bot getBot() {
        return bot;
    }

    public MongoClient getClient() {
        return client;
    }

    public MongoDatabase getDB() {
        return db;
    }

    public MongoCollection<Document> getCollection(String name) {
        MongoCollection<Document> collection = collections.get(name);
        return collection == null ? registerCollection(name) : collection;
    }

    private MongoCollection<Document> registerCollection(String name) {
        MongoCollection<Document> collection = db.getCollection(name);
        collection.createIndex(Indexes.ascending("id"));
        collections.put(name, collection);
        return collection;
    }
}
