package org.wildcloud.wildcloud_backend.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UploadSummary {
    List<UploadResult> successes;
    List<UploadResult> failures;
}
