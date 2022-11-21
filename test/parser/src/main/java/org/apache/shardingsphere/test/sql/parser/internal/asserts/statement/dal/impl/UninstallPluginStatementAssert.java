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

package org.apache.shardingsphere.test.sql.parser.internal.asserts.statement.dal.impl;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.shardingsphere.sql.parser.sql.dialect.statement.mysql.dal.MySQLUninstallPluginStatement;
import org.apache.shardingsphere.test.sql.parser.internal.asserts.SQLCaseAssertContext;
import org.apache.shardingsphere.test.sql.parser.internal.cases.parser.domain.statement.dal.UninstallPluginStatementTestCase;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Uninstall plugin statement assert.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UninstallPluginStatementAssert {
    
    /**
     * Assert uninstall plugin statement is correct with expected uninstall plugin statement test case.
     *
     * @param assertContext assert context
     * @param actual actual uninstall plugin statement
     * @param expected expected uninstall plugin statement test case
     */
    public static void assertIs(final SQLCaseAssertContext assertContext, final MySQLUninstallPluginStatement actual, final UninstallPluginStatementTestCase expected) {
        assertThat(assertContext.getText("Actual plugin name does not match: "), actual.getPluginName(), is(expected.getPlugin().getName()));
    }
}
