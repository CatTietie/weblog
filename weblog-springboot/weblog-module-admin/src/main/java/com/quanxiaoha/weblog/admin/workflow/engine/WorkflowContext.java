package com.quanxiaoha.weblog.admin.workflow.engine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowContext {

    private Map<String, Object> variables;

    private boolean debugMode;

    public Object getVariable(String key) {
        return variables != null ? variables.get(key) : null;
    }

    public void setVariable(String key, Object value) {
        if (variables == null) {
            variables = new HashMap<>();
        }
        variables.put(key, value);
    }

    public String getStringVariable(String key) {
        Object val = getVariable(key);
        return val != null ? val.toString() : "";
    }
}
