package com.quanxiaoha.weblog.admin.workflow.engine.executors;

import com.alibaba.fastjson2.JSONObject;
import com.quanxiaoha.weblog.admin.workflow.engine.NodeExecutionResult;
import com.quanxiaoha.weblog.admin.workflow.engine.NodeExecutor;
import com.quanxiaoha.weblog.admin.workflow.engine.TemplateResolver;
import com.quanxiaoha.weblog.admin.workflow.engine.WorkflowContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ConditionNodeExecutor implements NodeExecutor {

    @Autowired
    private TemplateResolver templateResolver;

    @Override
    public NodeExecutionResult execute(JSONObject nodeData, WorkflowContext context) {
        String field = nodeData.getString("field");
        String operator = nodeData.getString("operator");
        String rawValue = nodeData.getString("value");

        // 对期望值进行模板解析，支持 {{readNum}} 等变量引用
        String resolvedValue = templateResolver.resolve(rawValue, context);

        // 获取上下文中的实际值（也支持通过字段名直接取值或模板解析）
        Object actualValue = context.getVariable(field);
        if (actualValue == null) {
            // 尝试将 field 本身作为模板解析（支持 {{readNum}} 写法作为字段）
            String resolvedField = templateResolver.resolve(field, context);
            if (!resolvedField.equals(field)) {
                actualValue = resolvedField;
            }
        }

        boolean result = evaluate(actualValue, operator, resolvedValue);
        log.debug("条件判断: {}={} {} {} => {}", field, actualValue, operator, resolvedValue, result);
        return NodeExecutionResult.branch(result);
    }

    private boolean evaluate(Object actual, String operator, String expected) {
        if (actual == null) {
            return "NOT_EQUALS".equals(operator);
        }
        String actualStr = actual.toString();

        switch (operator) {
            case "EQUALS":
                return actualStr.equals(expected);
            case "NOT_EQUALS":
                return !actualStr.equals(expected);
            case "CONTAINS":
                return actualStr.contains(expected);
            case "NOT_CONTAINS":
                return !actualStr.contains(expected);
            case "GT":
            case "LT":
            case "GTE":
            case "LTE":
                return compareNumeric(actualStr, operator, expected);
            default:
                log.warn("未知的条件操作符: {}", operator);
                return false;
        }
    }

    private boolean compareNumeric(String actualStr, String operator, String expected) {
        Double actualNum = parseNumber(actualStr);
        Double expectedNum = parseNumber(expected);

        if (actualNum == null || expectedNum == null) {
            log.warn("数值比较失败，无法解析为数字: actual={}, expected={}", actualStr, expected);
            return false;
        }

        switch (operator) {
            case "GT":
                return actualNum > expectedNum;
            case "LT":
                return actualNum < expectedNum;
            case "GTE":
                return actualNum >= expectedNum;
            case "LTE":
                return actualNum <= expectedNum;
            default:
                return false;
        }
    }

    private Double parseNumber(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String getNodeType() {
        return "condition";
    }
}
