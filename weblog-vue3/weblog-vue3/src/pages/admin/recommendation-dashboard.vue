<template>
    <div class="p-4">
        <!-- 汇总卡片 -->
        <div class="grid grid-cols-3 gap-4 mb-6">
            <el-card shadow="hover">
                <div class="text-center">
                    <div class="text-2xl font-bold text-blue-600">{{ dashboard.totalRecommends || 0 }}</div>
                    <div class="text-sm text-gray-500 mt-1">{{ t('recommendation.totalRecommends') }}</div>
                </div>
            </el-card>
            <el-card shadow="hover">
                <div class="text-center">
                    <div class="text-2xl font-bold text-green-600">{{ dashboard.totalClicks || 0 }}</div>
                    <div class="text-sm text-gray-500 mt-1">{{ t('recommendation.totalClicks') }}</div>
                </div>
            </el-card>
            <el-card shadow="hover">
                <div class="text-center">
                    <div class="text-2xl font-bold text-orange-600">{{ dashboard.averageCtr || 0 }}%</div>
                    <div class="text-sm text-gray-500 mt-1">{{ t('recommendation.averageCtr') }}</div>
                </div>
            </el-card>
        </div>

        <!-- CTR 折线图 -->
        <el-card shadow="hover" class="mb-6">
            <template #header>
                <span class="font-bold">{{ t('recommendation.dailyCtr') }}</span>
            </template>
            <div ref="ctrChartRef" style="height: 350px;"></div>
        </el-card>
    </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useI18n } from 'vue-i18n'
import * as echarts from 'echarts'
import { getRecommendDashboard } from '@/api/admin/recommendation'

const { t } = useI18n()
const ctrChartRef = ref(null)
const dashboard = ref({})

onMounted(() => {
    loadDashboard()
})

function loadDashboard() {
    getRecommendDashboard().then(res => {
        if (res.success && res.data) {
            dashboard.value = res.data
            nextTick(() => {
                renderCtrChart(res.data.dailyCtr || [])
            })
        }
    })
}

function renderCtrChart(dailyCtr) {
    if (!ctrChartRef.value) return
    const chart = echarts.init(ctrChartRef.value)

    const dates = dailyCtr.map(item => item.date)
    const ctrValues = dailyCtr.map(item => item.ctr)
    const clickValues = dailyCtr.map(item => item.clicks)

    chart.setOption({
        tooltip: {
            trigger: 'axis',
            axisPointer: { type: 'cross' }
        },
        legend: {
            data: ['CTR (%)', t('recommendation.totalClicks')]
        },
        xAxis: {
            type: 'category',
            data: dates,
            axisLabel: {
                rotate: 45,
                fontSize: 10
            }
        },
        yAxis: [
            {
                type: 'value',
                name: 'CTR (%)',
                axisLabel: { formatter: '{value}%' }
            },
            {
                type: 'value',
                name: t('recommendation.totalClicks')
            }
        ],
        series: [
            {
                name: 'CTR (%)',
                type: 'line',
                data: ctrValues,
                smooth: true,
                itemStyle: { color: '#409EFF' }
            },
            {
                name: t('recommendation.totalClicks'),
                type: 'bar',
                yAxisIndex: 1,
                data: clickValues,
                itemStyle: { color: '#67C23A' }
            }
        ]
    })

    window.addEventListener('resize', () => chart.resize())
}
</script>
