<template>
    <!-- PV 折线图容器 -->
    <div id="radarChat" class="overflow-x-auto w-full min-h-[320px]"></div>
</template>

<script setup>

import * as echarts from 'echarts'
import { onMounted, watch } from 'vue'

// 对外暴露的属性值
const props = defineProps({
    value: { // 属性值名称
        type: Array, // 类型为对象
        default: () => [] // 默认为 null
    }
})

// 初始化折线图
function initRadarChat() {
    var chartDom = document.getElementById('radarChat');
    var myChart = echarts.init(chartDom);
    var option;
    // 处理接收到的数据
    const data = props.value.map(item => ({
        value: parseInt(item.value, 10),
        name: item.name
    }));
    option = {
        legend: {
            orient: 'vertical',
            left: '10%',
            top: '5%',
            data: ['Allocated Budget', 'Actual Spending']
        },
        radar: {
            // 控制雷达图的半径，让其变大，这里可以根据实际需求调整具体数值
            radius: 130,
            // 调整圆心位置，通过设置 left 和 top 的值来让其整体向右、向下移动（示例中向右移动了一定距离），单位为像素，可按需调整
            center: ['60%', '50%'],
            // shape: 'circle',
            indicator: [
                { name: 'Sales', max: 6500 },
                { name: 'Administration', max: 16000 },
                { name: 'Information Technology', max: 30000 },
                { name: 'Customer Support', max: 38000 },
                { name: 'Development', max: 52000 }
            ]
        },
        series: [
            {
                name: 'Budget vs spending',
                type: 'radar',
                data: [
                    {
                        value: [4200, 3000, 20000, 35000, 50000, 18000],
                        name: 'Allocated Budget'
                    },
                    {
                        value: [5000, 14000, 28000, 26000, 42000, 21000],
                        name: 'Actual Spending'
                    }
                ]
            }
        ]
    };

    option && myChart.setOption(option);

}
onMounted(() => {
    initRadarChat();
})


// 侦听属性, 监听 props.value 的变化，一旦 props.value 发生变化，就调用 initLineChat 初始化折线图
watch(() => props.value, () => initRadarChat())
</script>
