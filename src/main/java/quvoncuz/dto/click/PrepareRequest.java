package quvoncuz.dto.click;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrepareRequest {
    private Long click_trans_id;
    private Integer service_id;
    private Long click_paydoc_id;
    private String merchant_trans_id;
    private String amount;
    private int action;
    private int error;
    private String error_note;
    private String sign_time;
    private String sign_string;
}
