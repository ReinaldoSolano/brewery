package br.com.rsscloud.rest.dto;

import jakarta.validation.constraints.NotBlank;

public class BeerRequest {
    @NotBlank(message = "Name is required")
    public String name;

    @NotBlank(message = "Type is required")
    public String type;
}
