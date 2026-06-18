<template>
    <div ref="chartRef" class="w-full h-64"></div>
</template>

<script setup>
import { ref, watch, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'

const props = defineProps({
    dates: { type: Array, default: () => [] },
    counts: { type: Array, default: () => [] }
})

const chartRef = ref(null)
let chart = null

function renderChart() {
    if (!chartRef.value || props.dates.length === 0) return

    if (!chart) {
        chart = echarts.init(chartRef.value)
    }

    const option = {
        tooltip: {
            trigger: 'axis',
            axisPointer: { type: 'cross' }
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        xAxis: {
            type: 'category',
            boundaryGap: false,
            data: props.dates,
            axisLabel: {
                interval: Math.floor(props.dates.length / 7)
            }
        },
        yAxis: {
            type: 'value',
            minInterval: 1
        },
        series: [{
            name: '投递数',
            type: 'line',
            smooth: true,
            symbol: 'circle',
            symbolSize: 6,
            lineStyle: { width: 2, color: '#409EFF' },
            itemStyle: { color: '#409EFF' },
            areaStyle: {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                    { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
                    { offset: 1, color: 'rgba(64, 158, 255, 0.02)' }
                ])
            },
            data: props.counts
        }]
    }

    chart.setOption(option)
}

function handleResize() {
    chart && chart.resize()
}

onMounted(() => {
    renderChart()
    window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
    window.removeEventListener('resize', handleResize)
    if (chart) {
        chart.dispose()
        chart = null
    }
})

watch(() => [props.dates, props.counts], () => {
    renderChart()
}, { deep: true })
</script>
