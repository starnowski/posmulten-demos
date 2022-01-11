package com.github.starnowski.posmulten.demos.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@EqualsAndHashCode(of = "name")
public class TenantDto {

    private String name;
}
