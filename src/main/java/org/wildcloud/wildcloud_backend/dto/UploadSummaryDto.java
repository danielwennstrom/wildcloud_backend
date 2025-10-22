package org.wildcloud.wildcloud_backend.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UploadSummaryDto {
    List<UploadResultDto> successes;
    List<UploadResultDto> failures;
}
