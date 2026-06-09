package org.hik.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MatrixErrorResponse(@JsonProperty("errcode") String errCode,
                                  @JsonProperty("error") String error) {
}
