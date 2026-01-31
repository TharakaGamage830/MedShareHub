package com.medshare.hub;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class HashGenTest {
    @Test
    public void generateHash() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        System.out.println("HASH_START:" + encoder.encode("MedShare@2026") + ":HASH_END");
    }
}
