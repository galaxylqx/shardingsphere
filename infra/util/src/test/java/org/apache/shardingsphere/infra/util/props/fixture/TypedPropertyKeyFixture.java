/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.infra.util.props.fixture;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.shardingsphere.infra.util.props.TypedPropertyKey;
import org.apache.shardingsphere.infra.util.spi.type.typed.fixture.TypedSPIFixture;

@RequiredArgsConstructor
@Getter
public enum TypedPropertyKeyFixture implements TypedPropertyKey {
    
    BOOLEAN_VALUE("boolean", String.valueOf(Boolean.FALSE), boolean.class),
    
    BOOLEAN_OBJECT_VALUE("Boolean", String.valueOf(Boolean.FALSE), Boolean.class),
    
    INT_VALUE("int", "10", int.class),
    
    INT_OBJECT_VALUE("Integer", "10", Integer.class),
    
    LONG_VALUE("long", "1000", long.class),
    
    LONG_OBJECT_VALUE("Long", "1000", Long.class),
    
    STRING_VALUE("String", "value", String.class),
    
    ENUM_VALUE("enum", TypedPropertyEnumFixture.FOO.name(), TypedPropertyEnumFixture.class),
    
    TYPED_SPI_VALUE("typed_spi", "TYPED.FIXTURE", TypedSPIFixture.class);
    
    private final String key;
    
    private final String defaultValue;
    
    private final Class<?> type;
}
