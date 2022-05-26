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

package org.apache.shardingsphere.proxy.backend.text.admin.mysql.executor;

import org.apache.shardingsphere.infra.config.props.ConfigurationProperties;
import org.apache.shardingsphere.infra.database.type.dialect.MySQLDatabaseType;
import org.apache.shardingsphere.infra.federation.optimizer.context.OptimizerContext;
import org.apache.shardingsphere.infra.metadata.ShardingSphereMetaData;
import org.apache.shardingsphere.infra.metadata.database.ShardingSphereDatabase;
import org.apache.shardingsphere.infra.metadata.database.rule.ShardingSphereRuleMetaData;
import org.apache.shardingsphere.infra.metadata.user.Grantee;
import org.apache.shardingsphere.mode.manager.ContextManager;
import org.apache.shardingsphere.mode.metadata.MetaDataContexts;
import org.apache.shardingsphere.mode.metadata.persist.MetaDataPersistService;
import org.apache.shardingsphere.proxy.backend.context.ProxyContext;
import org.apache.shardingsphere.proxy.backend.session.ConnectionSession;
import org.apache.shardingsphere.proxy.backend.util.ProxyContextRestorer;
import org.apache.shardingsphere.sql.parser.sql.dialect.statement.mysql.dal.MySQLShowCreateDatabaseStatement;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class ShowCreateDatabaseExecutorTest extends ProxyContextRestorer {
    
    private static final String DATABASE_PATTERN = "db_%s";
    
    @Before
    public void setUp() {
        Map<String, ShardingSphereDatabase> databaseMap = getDatabaseMap();
        MetaDataContexts metaDataContexts = new MetaDataContexts(mock(MetaDataPersistService.class),
                new ShardingSphereMetaData(databaseMap, mock(ShardingSphereRuleMetaData.class), new ConfigurationProperties(new Properties())), mock(OptimizerContext.class));
        ContextManager contextManager = mock(ContextManager.class, RETURNS_DEEP_STUBS);
        when(contextManager.getMetaDataContexts()).thenReturn(metaDataContexts);
        ProxyContext.init(contextManager);
    }
    
    private Map<String, ShardingSphereDatabase> getDatabaseMap() {
        Map<String, ShardingSphereDatabase> result = new HashMap<>(10, 1);
        for (int i = 0; i < 10; i++) {
            ShardingSphereDatabase database = mock(ShardingSphereDatabase.class, RETURNS_DEEP_STUBS);
            when(database.getResource().getDatabaseType()).thenReturn(new MySQLDatabaseType());
            result.put(String.format(DATABASE_PATTERN, i), database);
        }
        return result;
    }
    
    @Test
    public void assertExecute() throws SQLException {
        MySQLShowCreateDatabaseStatement statement = new MySQLShowCreateDatabaseStatement();
        statement.setSchemaName("db_0");
        ShowCreateDatabaseExecutor showCreateDatabaseExecutor = new ShowCreateDatabaseExecutor(statement);
        showCreateDatabaseExecutor.execute(mockConnectionSession());
        assertThat(showCreateDatabaseExecutor.getQueryResultMetaData().getColumnCount(), is(2));
        int count = 0;
        while (showCreateDatabaseExecutor.getMergedResult().next()) {
            assertThat(showCreateDatabaseExecutor.getMergedResult().getValue(1, Object.class), is(String.format(DATABASE_PATTERN, count)));
            count++;
        }
    }
    
    private ConnectionSession mockConnectionSession() {
        ConnectionSession result = mock(ConnectionSession.class);
        when(result.getGrantee()).thenReturn(new Grantee("root", ""));
        when(result.getDatabaseName()).thenReturn(String.format(DATABASE_PATTERN, 0));
        return result;
    }
}
