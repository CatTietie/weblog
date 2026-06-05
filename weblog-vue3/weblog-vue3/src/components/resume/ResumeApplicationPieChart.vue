<template>
    <div ref="chartRef" class="w-full h-72"></div>
</template>

<script setup>
import { ref, watch, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'

const props = defineProps({
    data: { type: Array, default: () => [] }
})

const chartRef = ref(null)
let chart = null

const colorMap = {
    '已投递': '#909399',
    '面试中': '#E6A23C',
    '已录用': '#67C23A',
    '已拒绝': '#F56C6C',
    '已放弃': '#C0C4CC'
}

function renderChart() {
    if (!chartRef.value || props.data.length === 0) return

    if (!chart) {
        chart = echarts.init(chartRef.value)
    }

    const colors = props.data.map(item => colorMap[item.name] || '#909399')

    const seriesData = props.data.map(item => ({
        ...item,
        itemStyle: { color: colorMap[item.name] || '#909399' }
    }))

    const option = {
        color: colors,
        tooltip: {
            trigger: 'item',
            confine: true,
            formatter: '{b}: {c} ({d}%)'
        },
        legend: {
            orient: 'vertical',
            right: 10,
            top: 'middle',
            itemWidth: 12,
            itemHeight: 12,
            itemGap: 14,
            textStyle: { fontSize: 12 },
            formatter: (name) => {
                const item = props.data.find(d => d.name === name)
                return item ? `${name}  ${item.value}` : name
            }
        },
        series: [{
            type: 'pie',
            radius: ['40%', '68%'],
            center: ['35%', '50%'],
            avoidLabelOverlap: true,
            label: {
                show: false
            },
            emphasis: {
                label: {
                    show: true,
                    fontSize: 13,
                    fontWeight: 'bold',
                    formatter: '{b}\n{d}%'
                }
            },
            labelLine: { show: false },
            data: seriesData
        }]
    }

    chart.setOption(option, true)
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

watch(() => props.data, () => {
    renderChart()
}, { deep: true })
</script>
