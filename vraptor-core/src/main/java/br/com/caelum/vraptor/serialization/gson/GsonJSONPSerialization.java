/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package br.com.caelum.vraptor.serialization.gson;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.deserialization.gson.VRaptorGsonBuilder;
import br.com.caelum.vraptor.interceptor.TypeNameExtractor;
import br.com.caelum.vraptor.serialization.JSONPSerialization;
import br.com.caelum.vraptor.serialization.JSONSerialization;
import br.com.caelum.vraptor.serialization.ProxyInitializer;
import br.com.caelum.vraptor.serialization.Serializee;
import br.com.caelum.vraptor.serialization.SerializerBuilder;
import br.com.caelum.vraptor.view.ResultException;

/**
 * Gson implementation for JSONPSerialization
 *
 * @author Otávio Scherer Garcia
 */
public class GsonJSONPSerialization implements JSONPSerialization {
    
    protected final HttpServletResponse response;
    protected final TypeNameExtractor extractor;
    protected final ProxyInitializer initializer;
    protected final VRaptorGsonBuilder builder;
    protected final Serializee serializee;

    public GsonJSONPSerialization(HttpServletResponse response, TypeNameExtractor extractor,
            ProxyInitializer initializer,  VRaptorGsonBuilder builder, Serializee serializee) {
        this.response = response;
        this.extractor = extractor;
        this.initializer = initializer;
        this.builder = builder;
        this.serializee = serializee;
    }
    
    @Override
    public JSONSerialization withCallback(final String callbackName) {
        return new GsonJSONSerialization(response, extractor, initializer, builder, serializee) {
            @Override
            protected SerializerBuilder getSerializer() {
                try {
                    return new GsonSerializer(builder, response.getWriter(), extractor, initializer, serializee) {
                        @Override
                        protected void write(Writer writer, String json)
                            throws IOException {
                            writer.write(callbackName + "(" + json + ")");
                        }
                    };
                } catch (IOException e) {
                    throw new ResultException("Unable to serialize data", e);
                }
            }
        };
    }

}