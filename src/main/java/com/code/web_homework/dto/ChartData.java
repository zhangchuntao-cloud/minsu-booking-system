package com.code.web_homework.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 图表数据封装。
 * <p>
 * 后端把统计结果整理成「标签数组 + 数值数组」的结构，
 * 前端 JavaScript 直接交给 Chart.js 渲染，无需再做转换。
 */
public class ChartData implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 数据系列名称，作为图例显示 */
    private String name;
    /** 横轴标签 */
    private List<String> labels = new ArrayList<>();
    /** 纵轴数值 */
    private List<BigDecimal> values = new ArrayList<>();

    public ChartData() {
    }

    public ChartData(String name) {
        this.name = name;
    }

    /** 追加一组「标签-数值」 */
    public void add(String label, BigDecimal value) {
        this.labels.add(label);
        this.values.add(value == null ? BigDecimal.ZERO : value);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public List<BigDecimal> getValues() {
        return values;
    }

    public void setValues(List<BigDecimal> values) {
        this.values = values;
    }
}
