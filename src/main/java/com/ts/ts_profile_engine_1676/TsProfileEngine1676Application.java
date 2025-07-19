package com.ts.ts_profile_engine_1676;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class TsProfileEngine1676Application {

    public static void main(String[] args) {
        SpringApplication.run(TsProfileEngine1676Application.class, args);
        log.info("ts-profile-engine-1676 started on PORT: 1676");
    }
}
