package com.compiler.utils;

import java.io.IOException;
import java.util.Map;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class JSON {
    public static Gson builder = new GsonBuilder()
            .addSerializationExclusionStrategy(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes f) {
                    return f.getAnnotation(JsonIgnore.class) != null;
                }

                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    return false;
                }
            })
            .registerTypeAdapterFactory(new TypeAdapterFactory() {
                @Override
                public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
                    TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);

                    return new TypeAdapter<>() {
                        @Override
                        public void write(JsonWriter out, T value) throws IOException {
                            JsonElement elem = delegate.toJsonTree(value);

                            if (!elem.isJsonObject()) {
                                gson.toJson(elem, out);
                                return;
                            }

                            JsonObject orig = elem.getAsJsonObject();
                            JsonObject ordered = new JsonObject();

                            ordered.addProperty("_c", value.getClass().getSimpleName());
                            for (Map.Entry<String, JsonElement> e : orig.entrySet()) {
                                ordered.add(e.getKey(), e.getValue());
                            }

                            Streams.write(ordered, out);
                        }

                        @Override
                        public T read(JsonReader in) throws IOException {
                            JsonObject obj = Streams.parse(in).getAsJsonObject();
                            return delegate.fromJsonTree(obj);
                        }
                    };
                }
            })
            .setPrettyPrinting()
            .create();

    public static String serialize(Object obj) {
        return builder.toJson(obj);
    }
}
