/*
 * Copyright © 2019 Cask Data, Inc.
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
package io.cdap.directives.aggregates;

import io.cdap.wrangler.api.Token;
import io.cdap.wrangler.api.parser.ByteSize;
import io.cdap.wrangler.api.parser.TimeDuration;
import io.cdap.wrangler.api.TransientVariableScope;
import io.cdap.wrangler.api.DefaultTransientStore;
import io.cdap.wrangler.api.IncrementTransientVariable;
import io.cdap.wrangler.api.SetTransientVariable;
import io.cdap.wrangler.api.ExecutorContext;
import io.cdap.wrangler.api.Row;
import io.cdap.wrangler.api.Directive;
import io.cdap.wrangler.api.parser.Numeric;
import io.cdap.wrangler.api.parser.TokenType;

import java.util.List;

public class ByteSizeTimeDurationAggregateDirective implements Directive {

    private String byteSizeColumn;
    private String timeDurationColumn;
    private String targetByteSizeColumn;
    private String targetTimeDurationColumn;
    private String byteSizeUnit;
    private String timeDurationUnit;
    private String aggregationType;

    public ByteSizeTimeDurationAggregateDirective(String byteSizeColumn, String timeDurationColumn,
                                                 String targetByteSizeColumn, String targetTimeDurationColumn,
                                                 String byteSizeUnit, String timeDurationUnit, String aggregationType) {
        this.byteSizeColumn = byteSizeColumn;
        this.timeDurationColumn = timeDurationColumn;
        this.targetByteSizeColumn = targetByteSizeColumn;
        this.targetTimeDurationColumn = targetTimeDurationColumn;
        this.byteSizeUnit = byteSizeUnit;
        this.timeDurationUnit = timeDurationUnit;
        this.aggregationType = aggregationType; // total or average
    }

    @Override
    public UsageDefinition define() {
        UsageDefinition.Builder builder = UsageDefinition.builder("aggregate-byte-size-time-duration");
        builder.define("byteSizeColumn", TokenType.COLUMN_NAME);
        builder.define("timeDurationColumn", TokenType.COLUMN_NAME);
        builder.define("targetByteSizeColumn", TokenType.COLUMN_NAME);
        builder.define("targetTimeDurationColumn", TokenType.COLUMN_NAME);
        builder.define("byteSizeUnit", TokenType.STRING);  // Optional unit type (e.g., MB)
        builder.define("timeDurationUnit", TokenType.STRING);  // Optional unit type (e.g., seconds)
        builder.define("aggregationType", TokenType.STRING);  // Aggregation type (total/average)
        return builder.build();
    }

    @Override
    public void initialize(Arguments args) {
        // Initialize column names, units, and aggregation type from arguments
        this.byteSizeColumn = ((Identifier) args.value("byteSizeColumn")).value();
        this.timeDurationColumn = ((Identifier) args.value("timeDurationColumn")).value();
        this.targetByteSizeColumn = ((Identifier) args.value("targetByteSizeColumn")).value();
        this.targetTimeDurationColumn = ((Identifier) args.value("targetTimeDurationColumn")).value();
        this.byteSizeUnit = ((Text) args.value("byteSizeUnit")).value();
        this.timeDurationUnit = ((Text) args.value("timeDurationUnit")).value();
        this.aggregationType = ((Text) args.value("aggregationType")).value();
    }

    @Override
    public List<Row> execute(List<Row> rows, ExecutorContext context) {
        DefaultTransientStore store = new DefaultTransientStore();
        store.put("totalByteSize", 0L);  // Initialize total byte size
        store.put("totalTimeDuration", 0L);  // Initialize total time duration

        // Loop through the rows and perform aggregation
        for (Row row : rows) {
            // Read byte size value from the source column
            ByteSize byteSize = (ByteSize) row.getValue(byteSizeColumn);
            // Read time duration value from the source column
            TimeDuration timeDuration = (TimeDuration) row.getValue(timeDurationColumn);

            // Convert to canonical units (bytes for byte size, nanoseconds for time duration)
            long byteSizeValue = byteSize.value();
            long timeDurationValue = timeDuration.value();

            // Increment the totals using IncrementTransientVariable
            IncrementTransientVariable incrementByteSize = new IncrementTransientVariable("totalByteSize");
            incrementByteSize.increment(byteSizeValue);

            IncrementTransientVariable incrementTimeDuration = new IncrementTransientVariable("totalTimeDuration");
            incrementTimeDuration.increment(timeDurationValue);
        }

        // Finalize the totals
        long finalByteSize = store.get("totalByteSize");
        long finalTimeDuration = store.get("totalTimeDuration");

        // Perform unit conversions (if specified in the arguments)
        if (byteSizeUnit.equals("MB")) {
            finalByteSize = finalByteSize / (1024 * 1024);
        } else if (byteSizeUnit.equals("GB")) {
            finalByteSize = finalByteSize / (1024 * 1024 * 1024);
        }

        if (timeDurationUnit.equals("seconds")) {
            finalTimeDuration = finalTimeDuration / 1000000000L;
        } else if (timeDurationUnit.equals("minutes")) {
            finalTimeDuration = finalTimeDuration / 60000000000L;
        }

        // Set the final aggregated values
        SetTransientVariable setByteSize = new SetTransientVariable("totalByteSize", finalByteSize);
        setByteSize.execute();

        SetTransientVariable setTimeDuration = new SetTransientVariable("totalTimeDuration", finalTimeDuration);
        setTimeDuration.execute();

        // Create a new Row containing the aggregated results
        Row resultRow = new Row();
        resultRow.add(targetByteSizeColumn, finalByteSize);
        resultRow.add(targetTimeDurationColumn, finalTimeDuration);

        return ImmutableList.of(resultRow);  // Return the aggregated row
    }

    @Override
    public TokenType type() {
        return TokenType.CUSTOM;  // Custom directive type for aggregation
    }
}
