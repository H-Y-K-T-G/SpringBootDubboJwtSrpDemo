package org.librazy.demo.dubbo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * JPA 数据库加密参数
 */
@Configuration
public class JpaCryptoConfig {

    @Value("${encryption.algorithm}")
    private String algorithm;

    @Value("${encryption.key}")
    private byte[] key;

    @PostConstruct
    private void setConverterParams() {
        JpaCryptoConverter.setAlgorithm(algorithm);
        JpaCryptoConverter.setKey(key);
    }
}
