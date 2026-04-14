package quvoncuz.dto.click;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClickResponse {
    private Long click_trans_id;
    private String merchant_trans_id;
    private Integer merchant_prepare_id;
    private int error;
    private String error_note;
}
