package com.ariplaza.stockmaster.util;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

public class ResponseUtil {
    public static ResponseEntity<?> success(String message){
        return ResponseEntity.ok(Map.of(
                "code",200,
                "message",message
        ));
    }
    public static ResponseEntity<?> error(int code,String message){
        return ResponseEntity.status(code).body(Map.of(
                "code", code,
                "message", message
        ));
    }
}
