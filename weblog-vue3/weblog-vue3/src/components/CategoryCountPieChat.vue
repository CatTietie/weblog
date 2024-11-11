<template>
    <!-- PV 折线图容器 -->
    <div id="pieChat1" class="overflow-x-auto w-full h-60"></div>
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
    var chartDom = document.getElementById('pieChat1');
    var myChart = echarts.init(chartDom);
    var option;

    // 从 props.value 中获取日期集合和 pv 访问量集合
    // const pvDates = props.value.pvDates
    // const pvCounts = props.value.pvCounts

    option = {
        tooltip: {
            trigger: 'item',
            itemStyle:{
                fontSize:10
            }
        },
        legend: {
            orient: 'vertical',
            left: 'left',
            right: 'right',
            top: 'middle'
        },
        series: [
            {
                name: 'Access From',
                type: 'pie',
                // 增大半径以放大扇形图
                radius: ['50%', '80%'],
                avoidLabelOverlap: false,
                itemStyle: {
                    borderRadius: 10,
                    borderColor: '#fff',
                    borderWidth: 2
                },
                label: {
                    show: false,
                    position: 'center'
                },
                emphasis: {
                    label: {
                        show: true,
                        fontSize: 20,
                        fontWeight: 'bold'
                    }
                },
                labelLine: {
                    show: false
                },
                data: [
                    { value: 1048, name: 'Search Engine' },
                    { value: 735, name: 'Direct' },
                    { value: 580, name: 'Email' },
                    { value: 484, name: 'Union Ads' },
                    { value: 300, name: 'Video Ads' }
                ],
                // 调整中心位置以实现往下移动
                center: ['60%', '60%'],
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