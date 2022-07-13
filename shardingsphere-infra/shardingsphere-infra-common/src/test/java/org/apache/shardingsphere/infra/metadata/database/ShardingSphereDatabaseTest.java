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

package org.apache.shardingsphere.infra.metadata.database;

import org.apache.shardingsphere.infra.config.mode.ModeConfiguration;
import org.apache.shardingsphere.infra.database.type.DatabaseType;
import org.apache.shardingsphere.infra.eventbus.EventBusContext;
import org.apache.shardingsphere.infra.instance.ComputeNodeInstance;
import org.apache.shardingsphere.infra.instance.InstanceContext;
import org.apache.shardingsphere.infra.instance.fixture.WorkerIdGeneratorFixture;
import org.apache.shardingsphere.infra.instance.metadata.InstanceMetaData;
import org.apache.shardingsphere.infra.lock.LockContext;
import org.apache.shardingsphere.infra.metadata.database.resource.ShardingSphereResource;
import org.apache.shardingsphere.infra.metadata.database.rule.ShardingSphereRuleMetaData;
import org.apache.shardingsphere.infra.rule.ShardingSphereRule;
import org.apache.shardingsphere.test.mock.MockedDataSource;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public final class ShardingSphereDatabaseTest {
    
    private final ModeConfiguration modeConfig = new ModeConfiguration("Memory", null, false);
    
    @Test
    public void assertIsComplete() {
        ShardingSphereResource resource = new ShardingSphereResource(Collections.singletonMap("ds", new MockedDataSource()));
        ShardingSphereRuleMetaData ruleMetaData = new ShardingSphereRuleMetaData(Collections.singleton(mock(ShardingSphereRule.class)));
        assertTrue(new ShardingSphereDatabase("foo_db", mock(DatabaseType.class), resource, ruleMetaData, Collections.emptyMap()).isComplete());
    }
    
    @Test
    public void assertIsNotCompleteWithoutRule() {
        ShardingSphereResource resource = new ShardingSphereResource(Collections.singletonMap("ds", new MockedDataSource()));
        ShardingSphereRuleMetaData ruleMetaData = new ShardingSphereRuleMetaData(Collections.emptyList());
        assertFalse(new ShardingSphereDatabase("foo_db", mock(DatabaseType.class), resource, ruleMetaData, Collections.emptyMap()).isComplete());
    }
    
    @Test
    public void assertIsNotCompleteWithoutDataSource() {
        ShardingSphereResource resource = new ShardingSphereResource(Collections.emptyMap());
        ShardingSphereRuleMetaData ruleMetaData = new ShardingSphereRuleMetaData(Collections.singleton(mock(ShardingSphereRule.class)));
        assertFalse(new ShardingSphereDatabase("foo_db", mock(DatabaseType.class), resource, ruleMetaData, Collections.emptyMap()).isComplete());
    }
    
    @Test
    public void assertReloadRules() {
        ShardingSphereResource resource = new ShardingSphereResource(Collections.singletonMap("ds", new MockedDataSource()));
        ShardingSphereRuleMetaData ruleMetaData = new ShardingSphereRuleMetaData(Collections.emptyList());
        ShardingSphereDatabase database = new ShardingSphereDatabase("foo_db", mock(DatabaseType.class), resource, ruleMetaData, Collections.emptyMap());
        Collection<ShardingSphereRule> rules = database.getRuleMetaData().getRules();
        assertTrue(rules.isEmpty());
        database.reloadRules(new InstanceContext(new ComputeNodeInstance(mock(InstanceMetaData.class)), new WorkerIdGeneratorFixture(Long.MIN_VALUE), modeConfig, mock(LockContext.class),
                new EventBusContext()));
        assertFalse(rules.isEmpty());
    }
}
