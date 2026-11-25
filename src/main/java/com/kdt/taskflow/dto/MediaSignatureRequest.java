package com.kdt.taskflow.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

public record MediaSignatureRequest(
        @JsonAlias({"folder", "asset_folder", "assetFolder"})
        String folder
) {
}
