/*
 * Copyright © 2017-2019 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package io.cdap.wrangler.api.parser;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class ByteSizeTest {

    @Test
    public void testParseByteSizeKB() {
        ByteSize byteSize = new ByteSize("10KB");
        assertEquals(10240, byteSize.value());  // 10KB = 10240 bytes
    }

    @Test
    public void testParseByteSizeMB() {
        ByteSize byteSize = new ByteSize("1.5MB");
        assertEquals(1572864, byteSize.value());  // 1.5MB = 1572864 bytes
    }

    @Test
    public void testParseByteSizeGB() {
        ByteSize byteSize = new ByteSize("2GB");
        assertEquals(2147483648L, byteSize.value());  // 2GB = 2147483648 bytes
    }

    @Test
    public void testParseByteSizeTB() {
        ByteSize byteSize = new ByteSize("1TB");
        assertEquals(1099511627776L, byteSize.value());  // 1TB = 1099511627776 bytes
    }

    @Test
    public void testInvalidByteSize() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ByteSize("10PB");  // Unsupported unit
        });
    }

    @Test
    public void testToJson() {
        ByteSize byteSize = new ByteSize("1GB");
        String expectedJson = "{\"type\":\"BYTE_SIZE\",\"value\":1073741824}";
        assertEquals(expectedJson, byteSize.toJson().toString());
    }
}
