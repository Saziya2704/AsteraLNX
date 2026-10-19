package com.newasteralnx.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProofOfDeliveryRequest {

    @Size(max = 100)
    private String receivedBy;

    @Size(max = 500)
    private String remarks;

    /** Optional outcome note, e.g. DELIVERED, PARTIAL, REFUSED */
    @Size(max = 30)
    private String outcome;
}
