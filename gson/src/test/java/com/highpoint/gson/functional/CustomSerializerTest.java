/*
 * Copyright (C) 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.highpoint.gson.functional;

import com.highpoint.gson.Gson;
import com.highpoint.gson.GsonBuilder;
import com.highpoint.gson.JsonArray;
import com.highpoint.gson.JsonElement;
import com.highpoint.gson.JsonObject;
import com.highpoint.gson.JsonSerializationContext;
import com.highpoint.gson.JsonSerializer;

import com.highpoint.gson.common.TestTypes;
import junit.framework.TestCase;

import java.lang.reflect.Type;

/**
 * Functional Test exercising custom serialization only.  When test applies to both
 * serialization and deserialization then add it to CustomTypeAdapterTest.
 *
 * @author Inderjeet Singh
 */
public class CustomSerializerTest extends TestCase {

   public void testBaseClassSerializerInvokedForBaseClassFields() {
     Gson gson = new GsonBuilder()
         .registerTypeAdapter(TestTypes.Base.class, new TestTypes.BaseSerializer())
         .registerTypeAdapter(TestTypes.Sub.class, new TestTypes.SubSerializer())
         .create();
     TestTypes.ClassWithBaseField target = new TestTypes.ClassWithBaseField(new TestTypes.Base());
     JsonObject json = (JsonObject) gson.toJsonTree(target);
     JsonObject base = json.get("base").getAsJsonObject();
     assertEquals(TestTypes.BaseSerializer.NAME, base.get(TestTypes.Base.SERIALIZER_KEY).getAsString());
   }

   public void testSubClassSerializerInvokedForBaseClassFieldsHoldingSubClassInstances() {
     Gson gson = new GsonBuilder()
         .registerTypeAdapter(TestTypes.Base.class, new TestTypes.BaseSerializer())
         .registerTypeAdapter(TestTypes.Sub.class, new TestTypes.SubSerializer())
         .create();
     TestTypes.ClassWithBaseField target = new TestTypes.ClassWithBaseField(new TestTypes.Sub());
     JsonObject json = (JsonObject) gson.toJsonTree(target);
     JsonObject base = json.get("base").getAsJsonObject();
     assertEquals(TestTypes.SubSerializer.NAME, base.get(TestTypes.Base.SERIALIZER_KEY).getAsString());
   }

   public void testSubClassSerializerInvokedForBaseClassFieldsHoldingArrayOfSubClassInstances() {
     Gson gson = new GsonBuilder()
         .registerTypeAdapter(TestTypes.Base.class, new TestTypes.BaseSerializer())
         .registerTypeAdapter(TestTypes.Sub.class, new TestTypes.SubSerializer())
         .create();
     TestTypes.ClassWithBaseArrayField target = new TestTypes.ClassWithBaseArrayField(new TestTypes.Base[] {new TestTypes.Sub(), new TestTypes.Sub()});
     JsonObject json = (JsonObject) gson.toJsonTree(target);
     JsonArray array = json.get("base").getAsJsonArray();
     for (JsonElement element : array) {
       JsonElement serializerKey = element.getAsJsonObject().get(TestTypes.Base.SERIALIZER_KEY);
      assertEquals(TestTypes.SubSerializer.NAME, serializerKey.getAsString());
     }
   }

   public void testBaseClassSerializerInvokedForBaseClassFieldsHoldingSubClassInstances() {
     Gson gson = new GsonBuilder()
         .registerTypeAdapter(TestTypes.Base.class, new TestTypes.BaseSerializer())
         .create();
     TestTypes.ClassWithBaseField target = new TestTypes.ClassWithBaseField(new TestTypes.Sub());
     JsonObject json = (JsonObject) gson.toJsonTree(target);
     JsonObject base = json.get("base").getAsJsonObject();
     assertEquals(TestTypes.BaseSerializer.NAME, base.get(TestTypes.Base.SERIALIZER_KEY).getAsString());
   }

   public void testSerializerReturnsNull() {
     Gson gson = new GsonBuilder()
       .registerTypeAdapter(TestTypes.Base.class, new JsonSerializer<TestTypes.Base>() {
         public JsonElement serialize(TestTypes.Base src, Type typeOfSrc, JsonSerializationContext context) {
           return null;
         }
       })
       .create();
       JsonElement json = gson.toJsonTree(new TestTypes.Base());
       assertTrue(json.isJsonNull());
   }
}
