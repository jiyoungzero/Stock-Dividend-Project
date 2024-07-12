package org.example.stock.error.impl;
import org.example.stock.error.AbstractException;
import org.springframework.http.HttpStatus;

import java.net.http.HttpClient;

public class NoCompanyException extends AbstractException{

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public String getMessage() {
        return "존재하지 않는 회사명입니다.";
    }
}
