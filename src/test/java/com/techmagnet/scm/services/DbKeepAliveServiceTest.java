package com.techmagnet.scm.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class DbKeepAliveServiceTest {

    private JdbcTemplate jdbcTemplate;
    private DbKeepAliveService service;

    @BeforeEach
    public void setup() {
        jdbcTemplate = Mockito.mock(JdbcTemplate.class);
        service = new DbKeepAliveService(jdbcTemplate);
        // ensure enabled and small retry for tests
        ReflectionTestUtils.setField(service, "enabled", true);
        ReflectionTestUtils.setField(service, "retries", 1);
    }

    @Test
    public void testKeepAliveSuccess() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class))).thenReturn(1);

        service.keepAlive();

        verify(jdbcTemplate, times(1)).queryForObject(anyString(), eq(Integer.class));
    }

    @Test
    public void testKeepAliveWithRetry() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class)))
                .thenThrow(new RuntimeException("db down"))
                .thenReturn(1);

        service.keepAlive();

        // should call twice (initial attempt + retry)
        verify(jdbcTemplate, times(2)).queryForObject(anyString(), eq(Integer.class));
    }

}

