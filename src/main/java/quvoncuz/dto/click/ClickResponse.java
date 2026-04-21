package quvoncuz.dto.click;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClickResponse {
    @JsonProperty("click_trans_id")
    private Long clickTransId;
    @JsonProperty("merchant_trans_id")
    private String merchantTransId;
    @JsonProperty("merchant_prepare_id")
    private Integer merchantPrepareId;
    private int error;
    @JsonProperty("error_note")
    private String errorNote;
}
