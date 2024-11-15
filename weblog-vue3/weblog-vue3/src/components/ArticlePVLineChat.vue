<template>
    <!-- PV 折线图容器 -->
    <div id="lineChat" class="overflow-x-auto w-full min-h-[300px]"></div>
</template>

<script setup>
import * as echarts from 'echarts'
import { onMounted, watch } from 'vue'

// 对外暴露的属性值
const props = defineProps({
    articlePVInfo: {
        type: Object,
        default: () => ({})
    },
    updateCount: {
        type: Object,
        default: () => ({})
    }
})

// 初始化折线图
function initLineChat() {
    var chartDom = document.getElementById('lineChat');
    var myChart = echarts.init(chartDom);
    var option;

    // 从 props 中获取日期集合和 pv 访问量集合
    const pvDates = props.articlePVInfo.pvDates || [];
    const pvCounts = props.articlePVInfo.pvCounts || [];

    const xData = props.updateCount.xData || [];
    const seriesData = props.updateCount.seriesData || [];

    option = {
        toolbox: {
            show: true,
            feature: {
                mark: { show: true },
                dataView: { show: true, readOnly: false },
                restore: { show: true },
                saveAsImage: { show: true }
            }
        },
        visualMap: [
            {
                show: false,
                type: 'continuous',
                seriesIndex: 0,
                min: 0,
                max: 40,
                inRange: {
                    color: ['#87CEFA', '#00BFFF', '#0078D7']
                }
            },
            {
                show: false,
                type: 'continuous',
                seriesIndex: 1,
                min: 0,
                max: 40,
                inRange: {
                    color: ['#65B581', '#FFCE34', '#FD665F']
                }
            }
        ],
        legend: {
            top: '1%',
            textStyle: {
                color: 'black', // 设置文字颜色，这里设置为黑色，你可以使用如'red'、'#333'等其他颜色值
                // 设置字体，例如'SimHei'（黑体），也可以是其他字体如'Arial'等，需确保字体在对应环境可用
                fontSize: 15, // 设置字号大小，单位为像素，可根据实际需求调整数值
                fontWeight: 'normal' // 设置文字粗细，可取值为'normal'（正常）、'bold'（加粗）等
            }
        },
        tooltip: {
            show: true,
            trigger: 'item',
        },
        xAxis: {
            type: 'category',
            data: pvDates
        },
        yAxis: {
            type: 'value'
        },
        series: [
            {
                data: pvCounts,
                type: 'line',
                name: 'PV统计',
                showSymbol: false,
            },
            {
                data: seriesData,
                type: 'line',
                name: '更新频率',
                showSymbol: false,
            }
        ]
    };

    option && myChart.setOption(option);
}

onMounted(async () => {
    // 模拟异步数据加载
    await new Promise((resolve) => setTimeout(resolve, 1000));
});

// 侦听属性, 监听 props.value 的变化，一旦 props.value 发生变化，就调用 initLineChat 初始化折线图
watch([() => props.articlePVInfo, () => props.updateCount], ([newArticlePVInfo, newUpdateCount]) => {
    if (newArticlePVInfo && Object.keys(newArticlePVInfo).length > 0 && newUpdateCount && Object.keys(newUpdateCount).length > 0) {
        initLineChat();
    }
}, { deep: true })
</script>