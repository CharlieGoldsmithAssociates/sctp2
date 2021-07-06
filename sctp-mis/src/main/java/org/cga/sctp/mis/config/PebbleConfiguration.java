package org.cga.sctp.mis.config;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.extension.Extension;
import com.mitchellbosecke.pebble.loader.FileLoader;
import org.cga.sctp.audit.LogMessagePrinterManager;
import org.cga.sctp.mis.core.templating.PebbleExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PebbleConfiguration {

    @Autowired
    ApplicationContext appContext;

    @Autowired
    private LogMessagePrinterManager printerManager;

    @Bean
    public Extension pebbleExtension() {
        return new PebbleExtension(appContext, printerManager);
    }
}
