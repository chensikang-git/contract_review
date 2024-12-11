package com.swufe.ruleservice.dto.req;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RuleTableRecordUpdateDTO {

    private Long id;

    private String name;

    private String description;

}
