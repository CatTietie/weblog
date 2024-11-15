<template>
    <!-- PV 折线图容器 -->
    <div id="pieChat2" class="overflow-x-auto w-full min-h-[300px]"></div>
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
function initPieChat() {
    var chartDom = document.getElementById('pieChat2');
    var myChart = echarts.init(chartDom);
    var option;
    // 处理接收到的数据

    const data = props.value

    option = {
        tooltip: {
            trigger: 'item',
            // 调整提示框背景色、边框、字体等样式，使其更美观
            backgroundColor: 'rgba(255, 255, 255, 0.9)',
            borderColor: '#ccc',
            borderWidth: 1,
            padding: 10,
            textStyle: {
                fontSize: 14,
                color: '#333'
            },
            // 格式化提示框显示内容，展示更详细的信息，比如百分比（假设 data 中的每个元素包含 value 和 name 属性）
            formatter: function (params) {
                return `${params.name} : ${params.value} (${((params.value / option.series[0].data.reduce((sum, item) => sum + item.value, 0)) * 100).toFixed(2)}%)`;
            }
        },
        legend: {
            orient: 'vertical',
            left: 'left',
            // 设置图例文字样式
            textStyle: {
                fontSize: 14,
                color: '#666'
            },
            // 调整图例项之间的间距
            itemGap: 15,
            // 设置图例项的宽度和高度，使其布局更规整
            itemWidth: 12,
            itemHeight: 12,
            // 当鼠标悬停在图例项上时的样式
            hoverLink: true,
            // 鼠标悬停时的文字样式变化
            selected: {
                textStyle: {
                    color: '#1890ff'
                }
            }
        },
        series: [
            {
                name: '访问次数',
                type: 'pie',
                radius: '50%',
                data: data,
                emphasis: {
                    itemStyle: {
                        shadowBlur: 10,
                        shadowOffsetX: 0,
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                    }
                },
                // 自定义饼图颜色，这里可以根据喜好或业务需求替换颜色数组
                itemStyle: {
                    normal: {
                        color: function (params) {
                            const colorList = ['#1890ff', '#ff4d4f', '#2fc25b', '#faca2b', '#7265e6'];
                            return colorList[params.dataIndex % colorList.length];
                        }
                    }
                },
                // 设置标签样式，这里展示在外部（position: 'outside'），并调整字体等样式，也可设置为 'inside' 等其他位置
                label: {
                    show: true,
                    position: 'outside',
                    formatter: '{b} : {d}%',
                    textStyle: {
                        fontSize: 14,
                        color: '#333'
                    }
                },
                // 设置标签引导线样式（如果标签在外部时，引导线连接标签和扇区）
                labelLine: {
                    show: true,
                    length: 10,
                    length2: 10,
                    smooth: false,
                    lineStyle: {
                        color: '#ccc'
                    }
                },
                // 调整扇区之间的间隔，数值可根据实际效果调整
                avoidLabelOverlap: true,
                labelLayout: {
                    hideOverlap: true
                },
                startAngle: 90,
                clockwise: false,
                radius:  '70%',
                roseType: 'radius'
            }
        ]
    };

    option && myChart.setOption(option);
}

// onMounted(() => {
//     initPieChat()
// })

// 侦听属性, 监听 props.value 的变化，一旦 props.value 发生变化，就调用 initLineChat 初始化折线图
watch(() => props.value, () => initPieChat())
</script>