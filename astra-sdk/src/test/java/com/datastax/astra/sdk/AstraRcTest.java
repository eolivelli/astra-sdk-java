/*
 * Copyright DataStax, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datastax.astra.sdk;

import com.dtsx.astra.sdk.utils.AstraRc;
import org.junit.jupiter.api.Assertions;

import java.io.File;

/**
 * Create Astrarc to execute test locally.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class AstraRcTest {
    
    /** Could be reused for tests. */
    public File tmpAstraRC = new File(System.getProperty("java.io.tmpdir") + File.separator + ".astrarc");

    //@Test
    //@DisplayName("Create .astraRC without clientId/clientSecret")
    public void should_create_astraRc_File() {
        // Given
        tmpAstraRC.delete();
        Assertions.assertFalse(tmpAstraRC.exists());
        // When
        AstraRc arc = new AstraRc(tmpAstraRC.getAbsolutePath());
        arc.createSectionWithToken("default", "ABC");
        arc.save();
        // Then
        Assertions.assertTrue(tmpAstraRC.exists());
    }
    
    

}
