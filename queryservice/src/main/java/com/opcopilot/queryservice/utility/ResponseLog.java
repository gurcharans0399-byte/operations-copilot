package com.opcopilot.queryservice.utility;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ResponseLog {
    private String model;
    private String totalTokens;
    private String promptTokens;
    private String completionTokens;
    private List<ResponseResultLog> resultLog;
}
