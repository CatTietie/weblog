<template>
    <!-- PV 折线图容器 -->
    <div id="radarChat" class="overflow-x-auto w-full h-80"></div>
</template>

<script setup>

import * as echarts from 'echarts'
import { onMounted, watch } from 'vue'

// 定义 props 并设置默认值
const props = defineProps({
    mobileInfo: {
        type: Object,
        default: null
    },
    deskInfo: {
        type: Object,
        default: null
    }
});

// 初始化折线图
function initRadarChat() {
    var chartDom = document.getElementById('radarChat');
    var myChart = echarts.init(chartDom);
    var option;

    const mobileInfoData = props.mobileInfo
    const deskInfoData = props.deskInfo

    option = {
        tooltip: {
            trigger: 'item'
        },
        legend: {
            orient: 'vertical',
            left: '10%',
            top: '5%',
            data: ['移动端', '桌面端']
        },
        radar: {
            // 控制雷达图的半径，让其变大，这里可以根据实际需求调整具体数值
            radius: 130,
            // 调整圆心位置，通过设置 left 和 top 的值来让其整体向右、向下移动（示例中向右移动了一定距离），单位为像素，可按需调整
            center: ['60%', '50%'],
            // shape: 'circle',
            indicator: [
                { name: "Android", max: mobileInfoData.max },
                { name: "iOS", max: mobileInfoData.max },
                { name: "Linux", max: mobileInfoData.max },
                { name: "macOS", max: mobileInfoData.max },
                { name: "Windows", max: mobileInfoData.max }
            ]
        },
        series: [
            {
                name: '操作系统分布',
                type: 'radar',
                data: [
                    {
                        value: deskInfoData.seriesData,
                        name: '桌面端'
                    },
                    {
                        value: mobileInfoData.seriesData,
                        name: '移动端'
                    }
                ]
            }
        ]
    };

    option && myChart.setOption(option);

}
// onMounted(() => {
//     initRadarChat();
// })


// 侦听属性, 监听 props.value 的变化，一旦 props.value 发生变化，就调用 initLineChat 初始化折线图
watch([() => props.mobileInfo, () => props.deskInfo], () => {
    initRadarChat();
});
</script>
