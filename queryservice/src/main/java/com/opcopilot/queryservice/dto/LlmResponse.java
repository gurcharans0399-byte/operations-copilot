package com.opcopilot.queryservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.opcopilot.queryservice.model.ActionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LlmResponse {
    private String response;
    private boolean manualActionRequired;

    @JsonIgnore
    private List<String> userAction;
    @JsonIgnore
    private ActionType proposedActionType;
}
