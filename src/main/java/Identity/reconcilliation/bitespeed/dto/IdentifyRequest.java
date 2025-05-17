package Identity.reconcilliation.bitespeed.dto;

import lombok.Data;

@Data  // mapping incoming request 
public class IdentifyRequest {
    private String email;
    private String phoneNumber;
}
