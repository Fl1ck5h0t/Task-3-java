package org.example.pojo;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Jacksonized
public class Currency {
    private Long id;
    private Long resourceId;
    private String name;
    private Integer count;
}
