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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.cdap.wrangler.api.annotations.PublicEvolving;

/**
 * The ByteSize class wraps the primitive type {@code Long} for byte size values.
 * An object of type {@code ByteSize} contains the value in bytes and provides methods
 * to parse the string input and retrieve the canonical value in bytes.
 *
 * @see ByteSizeList
 * @see ColumnName
 * @see ColumnNameList
 * @see DirectiveName
 * @see Numeric
 * @see NumericList
 * @see Properties
 * @see Ranges
 * @see Expression
 * @see Text
 * @see TextList
 * @see Identifier
 */
@PublicEvolving
public class ByteSize implements Token {
  /**
   * The value of the byte size in bytes.
   */
  private Long value;

  /**
   * Allocates a {@code ByteSize} object representing the given byte size string.
   * The constructor will parse the string and convert it to the canonical value in bytes.
   *
   * @param value the byte size value in the format like "10KB", "200MB", etc.
   */
  public ByteSize(String value) {
    this.value = parseByteSize(value);
  }

  /**
   * Parses the byte size string and returns the value in bytes.
   * Handles units like KB, MB, GB, etc., and converts them to bytes.
   *
   * @param value the byte size value as a string.
   * @return the value in bytes.
   */
  private Long parseByteSize(String value) {
    long bytes = 0;
    if (value.endsWith("KB")) {
      bytes = Long.parseLong(value.substring(0, value.length() - 2)) * 1024;
    } else if (value.endsWith("MB")) {
      bytes = Long.parseLong(value.substring(0, value.length() - 2)) * 1024 * 1024;
    } else if (value.endsWith("GB")) {
      bytes = Long.parseLong(value.substring(0, value.length() - 2)) * 1024 * 1024 * 1024;
    } else if (value.endsWith("TB")) {
      bytes = Long.parseLong(value.substring(0, value.length() - 2)) * 1024 * 1024 * 1024 * 1024;
    } else {
      bytes = Long.parseLong(value);  // assume bytes if no unit
    }
    return bytes;
  }

  /**
   * Returns the value of this {@code ByteSize} object as a long representing bytes.
   *
   * @return the byte value of this object.
   */
  @Override
  public Long value() {
    return value;
  }

  /**
   * Returns the type of this {@code ByteSize} object as a {@code TokenType}
   * enum.
   *
   * @return the enumerated {@code TokenType} of this object.
   */
  @Override
  public TokenType type() {
    return TokenType.BYTE_SIZE;
  }

  /**
   * Returns the members of this {@code ByteSize} object as a {@code JsonElement}.
   *
   * @return Json representation of this {@code ByteSize} object as {@code JsonElement}
   */
  @Override
  public JsonElement toJson() {
    JsonObject object = new JsonObject();
    object.addProperty("type", TokenType.BYTE_SIZE.name());
    object.addProperty("value", value);
    return object;
  }
}
