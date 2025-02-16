package ru.tattoo.maxsim.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailDetails {

    private String recipient;
    private String name;
    private String msgBody;
    private String subject;
    private String attachment;
}