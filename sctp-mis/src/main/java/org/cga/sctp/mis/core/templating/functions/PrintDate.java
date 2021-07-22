package org.cga.sctp.mis.core.templating.functions;

import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import org.cga.sctp.mis.core.templating.PebbleFunctionImpl;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PrintDate extends PebbleFunctionImpl {
    public PrintDate() {
        super("printDate", List.of("date"));
    }

    @Override
    public Object execute(Map<String, Object> args, PebbleTemplate self, EvaluationContext context, int lineNumber) {
        final Object arg = args.get("date");
        if (arg == null) {
            return null;
        }
        if (arg instanceof Temporal t) {
            if (t instanceof ZonedDateTime) {
                return DateTimeFormatter.ofPattern("E, LLL d yyyy HH:mm v").withLocale(Locale.US).format(t);
            } else if (t instanceof LocalDate) {
                return DateTimeFormatter.ofPattern("E, LLL d yyyy").withLocale(Locale.US).format(t);
            } else {
                return DateTimeFormatter.ofPattern("E, LLL d yyyy HH:mm").withLocale(Locale.US).format(t);
            }
        }
        throw new IllegalArgumentException("Date must be a temporal type at line number " + lineNumber);
    }
}
