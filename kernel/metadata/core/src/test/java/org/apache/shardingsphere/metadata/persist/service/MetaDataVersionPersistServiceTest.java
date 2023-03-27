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

package org.apache.shardingsphere.metadata.persist.service;

import org.apache.shardingsphere.metadata.persist.node.DatabaseMetaDataNode;
import org.apache.shardingsphere.mode.spi.PersistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MetaDataVersionPersistServiceTest {
    
    private PersistRepository repository;
    
    private MetaDataVersionPersistService metaDataVersionPersistService;
    
    @BeforeEach
    void setUp() {
        repository = mock(PersistRepository.class);
        when(repository.getDirectly(contains("foo_db"))).thenReturn("1");
        metaDataVersionPersistService = new MetaDataVersionPersistService(repository);
    }
    
    @Test
    void assertGetActiveVersion() {
        Optional<String> actual = metaDataVersionPersistService.getActiveVersion("foo_db");
        assertTrue(actual.isPresent());
        assertThat(actual.get(), is("1"));
    }
    
    @Test
    void assertIsActiveVersion() {
        assertTrue(metaDataVersionPersistService.isActiveVersion("foo_db", "1"));
    }
    
    @Test
    void assertIsNotActiveVersionWithNotExistedDatabase() {
        assertFalse(metaDataVersionPersistService.isActiveVersion("bar_db", "1"));
    }
    
    @Test
    void assertIsNotActiveVersionWithNotExistedVersion() {
        assertFalse(metaDataVersionPersistService.isActiveVersion("foo_db", "2"));
    }
    
    @Test
    void assertCreateNewVersionWithoutExistedActiveVersion() {
        assertFalse(metaDataVersionPersistService.createNewVersion("bar_db").isPresent());
    }
    
    @Test
    void assertCreateNewVersionWithExistedActiveVersion() {
        Optional<String> actual = metaDataVersionPersistService.createNewVersion("foo_db");
        assertTrue(actual.isPresent());
        assertThat(actual.get(), is("2"));
        verify(repository).persist("/metadata/foo_db/versions/2/rules", "1");
        verify(repository).persist("/metadata/foo_db/versions/2/data_sources", "1");
    }
    
    @Test
    void assertPersistActiveVersionWhenExisted() {
        metaDataVersionPersistService.persistActiveVersion("foo_db", "2");
        verify(repository).persist(DatabaseMetaDataNode.getActiveVersionPath("foo_db"), "2");
    }
    
    @Test
    void assertPersistActiveVersionWithNotExistedDatabase() {
        metaDataVersionPersistService.persistActiveVersion("bar_db", "2");
        verify(repository, times(0)).persist(DatabaseMetaDataNode.getActiveVersionPath("bar_db"), "2");
    }
    
    @Test
    void assertDeleteVersion() {
        metaDataVersionPersistService.deleteVersion("foo_db", "1");
        verify(repository).delete(DatabaseMetaDataNode.getDatabaseVersionPath("foo_db", "1"));
    }
}
