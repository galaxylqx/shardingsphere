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

package org.apache.shardingsphere.test.it.data.pipeline.core.ingest.position;

import org.apache.shardingsphere.data.pipeline.api.ingest.position.PrimaryKeyPositionFactory;
import org.apache.shardingsphere.data.pipeline.api.ingest.position.UnsupportedKeyPosition;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNull;

public final class UnsupportedKeyPositionTest {
    
    @Test
    public void assertInit() {
        UnsupportedKeyPosition position = (UnsupportedKeyPosition) PrimaryKeyPositionFactory.newInstance("u,,");
        assertNull(position.getBeginValue());
        assertNull(position.getEndValue());
    }
    
    @Test
    public void assertToString() {
        assertThat(new UnsupportedKeyPosition().toString(), is("u,,"));
    }
}
