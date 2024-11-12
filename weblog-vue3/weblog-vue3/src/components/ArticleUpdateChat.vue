<template>
    <!-- PV 折线图容器 -->
    <div id="barChat" class="overflow-x-auto w-full h-60"></div>
</template>

<script setup>
import * as echarts from 'echarts'
import { watch } from 'vue'

// 对外暴露的属性值
const props = defineProps({
    value: { // 属性值名称
        type: Object, // 类型为对象
        default: null // 默认为 null
    }
})

// 初始化柱状图
function initBarChat() {
    var chartDom = document.getElementById('barChat');
    var myChart = echarts.init(chartDom);
    var option;


    const xAxisData = props.value.xData.reverse()
    const seriesData = props.value.seriesData.reverse()



    option = {
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        legend: {
            show: true,
            left:'left',
            top:'5%',
            itemStyle: {
                color: '#0000FF' // 设置图例图标颜色为蓝色
            }
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        xAxis: {
            type: 'value',
            boundaryGap: [0, 0.01]
        },
        yAxis: {
            type: 'category',
            data: xAxisData
        },
        visualMap: {
            orient: 'horizontal',
            left: 'center',
            top: 10,
            min: 2,
            max: 30,
            text: ['High', 'Low'],
            // Map the score column to color
            dimension: 0,
            inRange: {
                color: ['#87CEFA', '#00BFFF', '#0078D7']
            }
        },
        series: [
            {
                name: '更新次数',
                type: 'bar',
                data: seriesData,
            }
        ]
    };

    option && myChart.setOption(option);
}

// 侦听属性, 监听 props.value 的变化，一旦 props.value 发生变化，就调用 initLineChat 初始化折线图
watch(() => props.value, () => initBarChat())
</script>