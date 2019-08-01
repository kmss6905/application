package com.example.application;

import java.util.UUID;

/**
 * 고유한 값으로 vod 이름을 정한다.
 */
public class UuidTest {
    // 랜덤 고유키 생성
    UUID uuid = UUID.randomUUID();

    // 하이픈 제외
    String UnicVodString = UUID.randomUUID().toString().replace("-", "");


    public String getUnicVodString() {
        return UnicVodString;
    }
}
