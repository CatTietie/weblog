<template>
    <!-- PV 折线图容器 -->
    <div id="pieChat2" class="overflow-x-auto w-full h-60"></div>
</template>

<script setup>
import * as echarts from 'echarts'
import { onMounted, watch } from 'vue'

// 对外暴露的属性值
// const props = defineProps({
//     value: { // 属性值名称
//         type: Object, // 类型为对象
//         default: null // 默认为 null
//     }
// })

// 初始化折线图
function initPieChat() {
    var chartDom = document.getElementById('pieChat2');
    var myChart = echarts.init(chartDom);
    var option;

    // 从 props.value 中获取日期集合和 pv 访问量集合
    // const pvDates = props.value.pvDates
    // const pvCounts = props.value.pvCounts

    option = {
        legend: {
            orient: 'vertical',
            left: 'left',
            right: 'right',
            top: 'middle'
        },
        toolbox: {
            show: true,
            feature: {
                mark: { show: true },
                dataView: { show: true, readOnly: false },
                restore: { show: true },
                saveAsImage: { show: true }
            }
        },
        series: [
            {
                name: 'Nightingale Chart',
                type: 'pie',
                radius: [40, 100],
                center: ['60%', '60%'],
                roseType: 'area',
                itemStyle: {
                    borderRadius: 8
                },
                data: [
                    { value: 40, name: 'rose 1' },
                    { value: 38, name: 'rose 2' },
                    { value: 32, name: 'rose 3' },
                    { value: 30, name: 'rose 4' },
                    { value: 28, name: 'rose 5' },
                    { value: 26, name: 'rose 6' },
                    { value: 22, name: 'rose 7' },
                    { value: 18, name: 'rose 8' }
                ]
            }
        ]
    };

    option && myChart.setOption(option);
}

onMounted(() => {
    initPieChat()
})

// 侦听属性, 监听 props.value 的变化，一旦 props.value 发生变化，就调用 initLineChat 初始化折线图
// watch(() => props.value, () => initPieChat())
</script>