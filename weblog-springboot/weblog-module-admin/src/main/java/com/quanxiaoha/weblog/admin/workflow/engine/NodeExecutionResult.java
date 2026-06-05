package com.quanxiaoha.weblog.admin.workflow.engine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NodeExecutionResult {

    private String status;

    private String branchResult;

    private String outputData;

    private String errorMessage;

    private boolean shouldPause;

    public static NodeExecutionResult success(String outputData) {
        return NodeExecutionResult.builder()
                .status("SUCCESS")
                .outputData(outputData)
                .build();
    }

    public static NodeExecutionResult branch(boolean result) {
        return NodeExecutionResult.builder()
                .status("SUCCESS")
                .branchResult(result ? "true" : "false")
                .build();
    }

    public static NodeExecutionResult pause() {
        return NodeExecutionResult.builder()
                .status("SUCCESS")
                .shouldPause(true)
                .build();
    }

    public static NodeExecutionResult fail(String error) {
        return NodeExecutionResult.builder()
                .status("FAILED")
                .errorMessage(error)
                .build();
    }
}
