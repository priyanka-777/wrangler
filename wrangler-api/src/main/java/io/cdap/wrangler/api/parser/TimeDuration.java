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
 * The TimeDuration class wraps the primitive type {@code Long} for time duration values.
 * An object of type {@code TimeDuration} contains the value in nanoseconds and provides methods
 * to parse the string input and retrieve the canonical value in nanoseconds.
 *
 * @see TimeDurationList
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
public class TimeDuration implements Token {
  /**
   * The value of the time duration in nanoseconds.
   */
  private Long value;

  /**
   * Allocates a {@code TimeDuration} object representing the given time duration string.
   * The constructor will parse the string and convert it to the canonical value in nanoseconds.
   *
   * @param value the time duration value in the format like "100ms", "5s", etc.
   */
  public TimeDuration(String value) {
    this.value = parseTimeDuration(value);
  }

  /**
   * Parses the time duration string and returns the value in nanoseconds.
   * Handles units like ms, s, m, h, and converts them to nanoseconds.
   *
   * @param value the time duration value as a string.
   * @return the value in nanoseconds.
   */
  private Long parseTimeDuration(String value) {
    long nanos = 0;
    if (value.endsWith("ms")) {
      nanos = Long.parseLong(value.substring(0, value.length() - 2)) * 1000000;
    } else if (value.endsWith("s")) {
      nanos = Long.parseLong(value.substring(0, value.length() - 1)) * 1000000000;
    } else if (value.endsWith("m")) {
      nanos = Long.parseLong(value.substring(0, value.length() - 1)) * 60000000000L;
    } else if (value.endsWith("h")) {
      nanos = Long.parseLong(value.substring(0, value.length() - 1)) * 3600000000000L;
    } else {
      nanos = Long.parseLong(value);  // assume seconds if no unit
    }
    return nanos;
  }

  /**
   * Returns the value of this {@code TimeDuration} object as a long representing nanoseconds.
   *
   * @return the nanosecond value of this object.
   */
  @Override
  public Long value() {
    return value;
  }

  /**
   * Returns the type of this {@code TimeDuration} object as a {@code TokenType}
   * enum.
   *
   * @return the enumerated {@code TokenType} of this object.
   */
  @Override
  public TokenType type() {
    return TokenType.TIME_DURATION;
  }

  /**
   * Returns the members of this {@code TimeDuration} object as a {@code JsonElement}.
   *
   * @return Json representation of this {@code TimeDuration} object as {@code JsonElement}
   */
  @Override
  public JsonElement toJson() {
    JsonObject object = new JsonObject();
    object.addProperty("type", TokenType.TIME_DURATION.name());
    object.addProperty("value", value);
    return object;
  }
}
