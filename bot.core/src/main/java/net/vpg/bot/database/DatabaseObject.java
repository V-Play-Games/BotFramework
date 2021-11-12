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

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.vpg.bot.entities.Entity;
import net.vpg.bot.framework.Bot;
import net.vpg.bot.framework.Util;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public abstract class DatabaseObject implements Entity {
    protected final String id;
    protected final DataObject data;
    protected final Bson filter;
    protected final Bot bot;
    private MongoCollection<Document> collection; // lazy init

    protected DatabaseObject(DataObject data, Bot bot) {
        this(data.getString("id"), data, bot);
    }

    protected DatabaseObject(String id, Bot bot) {
        this(id, DataObject.empty().put("id", id), bot);
    }

    private DatabaseObject(String id, DataObject data, Bot bot) {
        this.id = id;
        this.data = data;
        this.bot = bot;
        this.filter = Filters.eq("id", id);
    }

    public abstract String getCollectionName();

    public Document toDocument() {
        return Util.toDocument(toData());
    }

    public MongoCollection<Document> getCollection() {
        return collection == null
            ? (collection = bot.isDatabaseEnabled() ? bot.getDatabase().getCollection(getCollectionName()) : null)
            : collection;
    }

    public void computeOnCollection(Consumer<MongoCollection<Document>> action) {
        getCollection();
        if (collection != null)
            action.accept(collection);
    }

    @Override
    public String getId() {
        return id;
    }

    public void ensureInserted() {
        computeOnCollection(c -> {
            if (c.find(filter).first() == null) {
                c.insertOne(this.toDocument());
            }
        });
    }

    public void update() {
        getCollection().replaceOne(filter, this.toDocument());
    }

    public void update(String field, Object value) {
        data.put(field, value);
        getCollection().updateOne(filter, Updates.set(field, value));
    }

    public void delete() {
        getCollection().deleteOne(filter);
    }

    @Nonnull
    @Override
    public final DataObject toData() {
        return data;
    }
}
