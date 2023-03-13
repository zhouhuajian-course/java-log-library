package ch.qos.logback.core.model.processor;

import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.spi.FilterReply;

public class DenyAllModelFilter implements ModelFilter {

    @Override
    public FilterReply decide(Model model) {
        return FilterReply.DENY;
    }

}
