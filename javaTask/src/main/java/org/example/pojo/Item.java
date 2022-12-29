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
public class Item {
    private Long id;
    private Integer count;
    private Integer level;
    private Long resourceId;
}
