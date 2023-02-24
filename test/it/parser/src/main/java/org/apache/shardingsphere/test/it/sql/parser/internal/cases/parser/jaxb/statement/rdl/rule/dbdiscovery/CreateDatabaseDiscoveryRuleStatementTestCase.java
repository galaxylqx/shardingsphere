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

package org.apache.shardingsphere.test.it.sql.parser.internal.cases.parser.jaxb.statement.rdl.rule.dbdiscovery;

import lombok.Getter;
import org.apache.shardingsphere.test.it.sql.parser.internal.cases.parser.jaxb.segment.impl.distsql.rdl.ExpectedDatabaseDiscoveryRule;
import org.apache.shardingsphere.test.it.sql.parser.internal.cases.parser.jaxb.SQLParserTestCase;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.LinkedList;
import java.util.List;

/**
 * Create database discovery rule statement test case.
 */
@Getter
public final class CreateDatabaseDiscoveryRuleStatementTestCase extends SQLParserTestCase {
    
    @XmlElement(name = "rule")
    private final List<ExpectedDatabaseDiscoveryRule> rules = new LinkedList<>();
    
    @XmlAttribute(name = "if-not-exists")
    private boolean ifNotExists;
}
