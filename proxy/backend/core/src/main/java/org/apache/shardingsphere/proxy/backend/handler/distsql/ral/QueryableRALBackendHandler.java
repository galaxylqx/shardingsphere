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

package org.apache.shardingsphere.proxy.backend.handler.distsql.ral;

import org.apache.shardingsphere.infra.exception.dialect.exception.syntax.database.NoDatabaseSelectedException;
import org.apache.shardingsphere.infra.exception.dialect.exception.syntax.database.UnknownDatabaseException;
import org.apache.shardingsphere.distsql.handler.ral.query.DatabaseRequiredQueryableRALExecutor;
import org.apache.shardingsphere.distsql.handler.ral.query.InstanceContextRequiredQueryableRALExecutor;
import org.apache.shardingsphere.distsql.handler.ral.query.MetaDataRequiredQueryableRALExecutor;
import org.apache.shardingsphere.distsql.handler.ral.query.QueryableRALExecutor;
import org.apache.shardingsphere.distsql.parser.statement.ral.QueryableRALStatement;
import org.apache.shardingsphere.infra.merge.result.MergedResult;
import org.apache.shardingsphere.infra.merge.result.impl.local.LocalDataMergedResult;
import org.apache.shardingsphere.infra.merge.result.impl.local.LocalDataQueryResultRow;
import org.apache.shardingsphere.infra.exception.core.ShardingSpherePreconditions;
import org.apache.shardingsphere.infra.util.spi.type.typed.TypedSPILoader;
import org.apache.shardingsphere.proxy.backend.context.ProxyContext;
import org.apache.shardingsphere.proxy.backend.handler.distsql.ral.queryable.executor.ConnectionSessionRequiredQueryableRALExecutor;
import org.apache.shardingsphere.proxy.backend.response.data.QueryResponseCell;
import org.apache.shardingsphere.proxy.backend.response.data.QueryResponseRow;
import org.apache.shardingsphere.proxy.backend.response.header.ResponseHeader;
import org.apache.shardingsphere.proxy.backend.response.header.query.QueryHeader;
import org.apache.shardingsphere.proxy.backend.response.header.query.QueryResponseHeader;
import org.apache.shardingsphere.proxy.backend.session.ConnectionSession;
import org.apache.shardingsphere.sql.parser.sql.common.segment.generic.DatabaseSegment;
import org.apache.shardingsphere.sql.parser.sql.common.statement.available.FromDatabaseAvailable;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Queryable RAL backend handler.
 *
 * @param <T> type of queryable RAL statement
 */
public final class QueryableRALBackendHandler<T extends QueryableRALStatement> implements RALBackendHandler {
    
    private final T sqlStatement;
    
    private final ConnectionSession connectionSession;
    
    private List<QueryHeader> queryHeaders;
    
    private MergedResult mergedResult;
    
    @SuppressWarnings("unchecked")
    public QueryableRALBackendHandler(final QueryableRALStatement sqlStatement, final ConnectionSession connectionSession) {
        this.sqlStatement = (T) sqlStatement;
        this.connectionSession = connectionSession;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public ResponseHeader execute() {
        QueryableRALExecutor<T> executor = TypedSPILoader.getService(QueryableRALExecutor.class, sqlStatement.getClass().getName());
        mergedResult = getMergedResult(executor);
        queryHeaders = createQueryHeader(executor.getColumnNames());
        return new QueryResponseHeader(queryHeaders);
    }
    
    private MergedResult getMergedResult(final QueryableRALExecutor<T> executor) {
        if (executor instanceof InstanceContextRequiredQueryableRALExecutor) {
            return getMergedResultByInstanceContextRequiredExecutor((InstanceContextRequiredQueryableRALExecutor<T>) executor);
        }
        if (executor instanceof MetaDataRequiredQueryableRALExecutor) {
            return getMergedResultByMetaDataRequiredExecutor((MetaDataRequiredQueryableRALExecutor<T>) executor);
        }
        if (executor instanceof DatabaseRequiredQueryableRALExecutor) {
            return getMergedResultByDatabaseRequiredExecutor((DatabaseRequiredQueryableRALExecutor<T>) executor);
        }
        if (executor instanceof ConnectionSessionRequiredQueryableRALExecutor) {
            return getMergedResultByConnectionSessionRequiredExecutor((ConnectionSessionRequiredQueryableRALExecutor<T>) executor);
        }
        return createMergedResult(executor.getRows(sqlStatement));
    }
    
    private MergedResult getMergedResultByInstanceContextRequiredExecutor(final InstanceContextRequiredQueryableRALExecutor<T> executor) {
        return createMergedResult(executor.getRows(ProxyContext.getInstance().getContextManager().getInstanceContext(), sqlStatement));
    }
    
    private MergedResult getMergedResultByMetaDataRequiredExecutor(final MetaDataRequiredQueryableRALExecutor<T> executor) {
        return createMergedResult(executor.getRows(ProxyContext.getInstance().getContextManager().getMetaDataContexts().getMetaData(), sqlStatement));
    }
    
    private MergedResult getMergedResultByDatabaseRequiredExecutor(final DatabaseRequiredQueryableRALExecutor<T> executor) {
        String databaseName = getDatabaseName(connectionSession, sqlStatement);
        checkDatabaseName(databaseName);
        return createMergedResult(executor.getRows(ProxyContext.getInstance().getDatabase(databaseName), sqlStatement));
    }
    
    private MergedResult getMergedResultByConnectionSessionRequiredExecutor(final ConnectionSessionRequiredQueryableRALExecutor<T> executor) {
        return createMergedResult(executor.getRows(ProxyContext.getInstance().getContextManager().getMetaDataContexts().getMetaData(), connectionSession, sqlStatement));
    }
    
    private List<QueryHeader> createQueryHeader(final Collection<String> columnNames) {
        return columnNames.stream().map(each -> new QueryHeader("", "", each, each, Types.CHAR, "CHAR", 255, 0, false, false, false, false)).collect(Collectors.toList());
    }
    
    private MergedResult createMergedResult(final Collection<LocalDataQueryResultRow> rows) {
        return new LocalDataMergedResult(rows);
    }
    
    private String getDatabaseName(final ConnectionSession connectionSession, final T sqlStatement) {
        Optional<DatabaseSegment> databaseSegment = sqlStatement instanceof FromDatabaseAvailable ? ((FromDatabaseAvailable) sqlStatement).getDatabase() : Optional.empty();
        return databaseSegment.isPresent() ? databaseSegment.get().getIdentifier().getValue() : connectionSession.getDatabaseName();
    }
    
    private void checkDatabaseName(final String databaseName) {
        ShardingSpherePreconditions.checkNotNull(databaseName, NoDatabaseSelectedException::new);
        ShardingSpherePreconditions.checkState(ProxyContext.getInstance().databaseExists(databaseName), () -> new UnknownDatabaseException(databaseName));
    }
    
    @Override
    public boolean next() throws SQLException {
        return null != mergedResult && mergedResult.next();
    }
    
    @Override
    public QueryResponseRow getRowData() throws SQLException {
        List<QueryResponseCell> cells = new ArrayList<>(queryHeaders.size());
        for (int i = 0; i < queryHeaders.size(); i++) {
            cells.add(new QueryResponseCell(queryHeaders.get(i).getColumnType(), mergedResult.getValue(i + 1, Object.class)));
        }
        return new QueryResponseRow(cells);
    }
}
