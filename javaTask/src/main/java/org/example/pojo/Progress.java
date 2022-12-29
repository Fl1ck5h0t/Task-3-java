package org.example.pojo;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Jacksonized
public class Progress {
    private Long id;
    @ToString.Exclude
    @JsonIdentityReference(alwaysAsId = true)
    private Long playerId;
    private Long resourceId;
    private Integer score;
    private Integer maxScore;
}
