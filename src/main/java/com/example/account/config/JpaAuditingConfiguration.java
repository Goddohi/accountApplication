package com.example.account.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration //자동으로 빈으로 등록
@EnableJpaAuditing
public class JpaAuditingConfiguration {
}
