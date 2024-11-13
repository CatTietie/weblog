<template>
    <!-- PV 折线图容器 -->
    <div id="barChat1" class="overflow-x-auto w-full h-60"></div>
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
    var chartDom = document.getElementById('barChat1');
    var myChart = echarts.init(chartDom);
    var option;


    const titles = props.value.xData
    const readNums = props.value.seriesData




    option = {
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
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
            data: titles
        },
        visualMap: {
            orient: 'horizontal',
            left: 'center',
            top:'10',
            min: 5,
            max: 50,
            text: ['High', 'Low'],
            // Map the score column to color
            dimension: 0,
            inRange: {
                color: ['#65B581', '#FFCE34', '#FD665F']
            }
        },
        series: [
            {
                name: '阅读量',
                type: 'bar',
                data: readNums,
            }
        ],
        legend: {
            show: 'true',
            left:'left',
            top:'5%',
            itemStyle: {
                color: '#d50909'
            }
        },
    };

    option && myChart.setOption(option);
}

// 侦听属性, 监听 props.value 的变化，一旦 props.value 发生变化，就调用 initLineChat 初始化折线图
watch(() => props.value, () => initBarChat())
</script>