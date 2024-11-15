<template>
    <!-- PV 折线图容器 -->
    <div id="barChat2" class="overflow-x-auto w-full min-h-[300px]"></div>
</template>

<script setup>
import * as echarts from 'echarts'
import { onMounted, watch } from 'vue'

// 对外暴露的属性值
const props = defineProps({
    value: { // 属性值名称
        type: Object, // 类型为对象
        default: null // 默认为 null
    }
})

// 初始化柱状图
function initBarChat() {
    var chartDom = document.getElementById('barChat2');
    var myChart = echarts.init(chartDom);
    var option;

    const xData = props.value.xData
    const seriesData = props.value.seriesData

    // option
    option = {
        backgroundColor: '#1e293b',
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        legend: {
            data: ['line', 'bar'],
            textStyle: {
                color: '#ccc'
            }
        },
        xAxis: {
            data: xData,
            axisLine: {
                lineStyle: {
                    color: '#ccc'
                }
            }
        },
        yAxis: {
            splitLine: { show: false },
            axisLine: {
                lineStyle: {
                    color: '#ccc'
                }
            }
        },
        series: [
            {
                name: '用户访问',
                type: 'bar',
                smooth: true,
                barWidth: 50,
                barGap: '20%',
                barCategoryGap: 30,
                barAlign: 'center',
                barBorderRadius: [5, 5, 0, 0],
                itemStyle: {
                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                        { offset: 0, color: '#83bff6' },
                        { offset: 0.5, color: '#188df0' },
                        { offset: 1, color: '#188df0' }
                    ]),

                },
                emphasis: {
                    itemStyle: {
                        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                            { offset: 0, color: '#2378f7' },
                            { offset: 0.7, color: '#2378f7' },
                            { offset: 1, color: '#83bff6' }
                        ])
                    }
                },
                showAllSymbol: true,
                symbol: 'emptyCircle',
                symbolSize: 15,
                data: seriesData,
                markPoint: {
                    data: [
                        { type: 'max', name: 'Max' },
                        { type: 'min', name: 'Min' }
                    ]
                },
                markLine: {
                    data: [{ type: 'average', name: 'Avg' }]
                }
            }

        ]
    };

    option && myChart.setOption(option);


}

// onMounted(() => {
//     initBarChat()
// })

// 侦听属性, 监听 props.value 的变化，一旦 props.value 发生变化，就调用 initLineChat 初始化折线图
watch(() => props.value, () => initBarChat())
</script>