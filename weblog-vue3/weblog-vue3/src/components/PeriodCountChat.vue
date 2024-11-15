<template>
    <!-- PV 折线图容器 -->
    <div id="periodBar" class="overflow-x-auto w-full min-h-[500px]"></div>
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
    var chartDom = document.getElementById('periodBar');
    var myChart = echarts.init(chartDom);
    var option;

    const mobileData = props.value.mobile
    const deskData = props.value.desk
    const backData = props.value.back
    const frontData = props.value.front


    option = {
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'cross',
                crossStyle: {
                    color: '#999'
                }
            },
            formatter: function (params) {
                let tip = '';
                params.forEach((item) => {
                    if (item.seriesName === '移动端' || item.seriesName === '桌面端') {
                        tip += `${item.seriesName}：${item.value}次<br>`;
                    } else {
                        tip += `${item.seriesName}：${item.value}次<br>`;
                    }
                });
                return tip;
            },
            extraCssText: 'box-shadow: 0 0 5px rgba(0,0,0,0.2); border-radius: 5px; background-color: #fff;'
        },
        toolbox: {
            feature: {
                dataView: { show: true, readOnly: false },
                magicType: { show: true, type: ['line', 'bar'] },
                restore: { show: true },
                saveAsImage: { show: true }
            }
        },
        legend: {
        },
        xAxis: [
            {
                type: 'category',
                data: ['深夜（0:00 - 6:00）', '早间（6:00 - 10:00）', '日间（10:00 - 16:00）', '午后（16:00 - 20:00）', '晚间（20:00 - 24:00）'],
                axisPointer: {
                    type: 'shadow'
                }
            }
        ],
        yAxis: [
            {
                type: 'value',
                name: '访问量'
            }
        ],
        series: [
            {
                name: '移动端',
                type: 'bar',
                data: mobileData,
                itemStyle: {
                    color: '#1890ff' // 设置移动端柱子的颜色
                },
                barWidth: 50,
                barGap: '30%'
            },
            {
                name: '桌面端',
                type: 'bar',
                data: deskData,
                itemStyle: {
                    color: '#ff4d4f' // 设置桌面端柱子的颜色
                },
                barWidth: 50,
                barGap: '30%'
            },
            {
                name: '访问前台',
                type: 'line',
                data: frontData,
                // 设置线条样式，这里简单示例设置线条粗细、颜色渐变以及虚线样式，可按需调整参数
                lineStyle: {
                    width: 2,
                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                        { offset: 1, color: '#87CEFA' },
                        { offset: 0, color: '#1E90FF' }
                    ]),
                    dashArray: [5, 5]
                }
            },
            {
                name: '访问后台',
                type: 'line',
                data: backData,
                lineStyle: {
                    width: 2,
                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                        { offset: 1, color: '#FFB6C1' },
                        { offset: 0, color: '#FF69B4' }
                    ]),
                    dashArray: [5, 5]
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