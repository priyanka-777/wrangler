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
 * Token that represents a time duration like 100ms, 2s, 3min, etc.
 */
@PublicEvolving
public class TimeDuration implements Token {
  private final long nanoseconds;

  public TimeDuration(String value) {
    this.nanoseconds = parseToNanos(value.trim().toLowerCase());
  }

  private long parseToNanos(String value) {
    if (value.endsWith("ms")) {
      return (long) (Double.parseDouble(value.replace("ms", "")) * 1_000_000);
    } else if (value.endsWith("s")) {
      return (long) (Double.parseDouble(value.replace("s", "")) * 1_000_000_000);
    } else if (value.endsWith("min")) {
      return (long) (Double.parseDouble(value.replace("min", "")) * 60 * 1_000_000_000L);
    } else if (value.endsWith("h")) {
      return (long) (Double.parseDouble(value.replace("h", "")) * 3600 * 1_000_000_000L);
    } else if (value.endsWith("d")) {
      return (long) (Double.parseDouble(value.replace("d", "")) * 86400 * 1_000_000_000L);
    } else {
      throw new IllegalArgumentException("Unsupported time unit: " + value);
    }
  }

  @Override
  public Long value() {
    return Long.valueOf(nanoseconds);  
  }

  @Override
  public TokenType type() {
    return TokenType.TIME_DURATION;
  }

  @Override
  public JsonElement toJson() {
    JsonObject object = new JsonObject();
    object.addProperty("type", TokenType.TIME_DURATION.name());
    object.addProperty("value", nanoseconds);
    return object;
  }
}
