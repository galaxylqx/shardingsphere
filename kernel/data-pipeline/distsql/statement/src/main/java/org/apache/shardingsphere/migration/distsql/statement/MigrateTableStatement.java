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

package org.apache.shardingsphere.migration.distsql.statement;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.shardingsphere.distsql.parser.statement.ral.pipeline.migration.UpdatableMigrationRALStatement;
import org.apache.shardingsphere.migration.distsql.statement.pojo.SourceTargetEntry;

import java.util.List;

/**
 * Migrate table statement.
 */
@RequiredArgsConstructor
@Getter
public final class MigrateTableStatement extends UpdatableMigrationRALStatement {
    
    private final List<SourceTargetEntry> sourceTargetEntries;
    
    private final String targetDatabaseName;
}
