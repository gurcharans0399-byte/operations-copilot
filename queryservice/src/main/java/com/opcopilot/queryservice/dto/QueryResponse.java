package com.opcopilot.queryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class QueryResponse {
    private boolean manualActionRequired;
    private String queryResponse;
}
