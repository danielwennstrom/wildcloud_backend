package org.wildcloud.wildcloud_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class AttachmentDto {
    @JsonProperty("att_name")
    String filename;
    @JsonProperty("att_url")
    String url;
}
