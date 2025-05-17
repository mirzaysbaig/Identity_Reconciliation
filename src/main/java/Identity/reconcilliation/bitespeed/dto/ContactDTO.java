package Identity.reconcilliation.bitespeed.dto;

import java.util.List;

import lombok.Data;

// for response body attributes needed
@Data
public class ContactDTO {
    private int primaryContatctId;
    private List<String> emails;
    private List<String> phoneNumbers;
    private List<Integer> secondaryContactIds;
}
