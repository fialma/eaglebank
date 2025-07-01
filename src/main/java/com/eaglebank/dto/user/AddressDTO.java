package com.eaglebank.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Data
public class AddressDTO {

    @NotBlank(message = "At least one line of address is required")
    private String line1;

    private String line2;

    private String line3;

    @NotBlank(message = "Town is required")
    private String town;

    @NotBlank(message = "County is required")
    private String county;

    @NotBlank(message = "Postcode is required")
    private String postcode;
}
