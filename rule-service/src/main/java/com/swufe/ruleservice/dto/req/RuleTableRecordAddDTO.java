package com.swufe.ruleservice.dto.req;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RuleTableRecordAddDTO {

    private String name;

    private String description;

}
