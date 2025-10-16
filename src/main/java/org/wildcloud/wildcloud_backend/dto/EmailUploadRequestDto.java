package org.wildcloud.wildcloud_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class EmailUploadRequestDto {
    @JsonProperty("sender_field")
    List<SenderDto> sender;
    @JsonProperty("mail_attachments")
    List<AttachmentDto> attachments;
}

