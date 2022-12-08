package org.course.stockstrader.reactor.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JExchangeServiceEuronext extends JExchangeService{

    public JExchangeServiceEuronext(@Value("${remote.service.url}") String baseUrl) {
        super( baseUrl, "euronext");
    }
}
