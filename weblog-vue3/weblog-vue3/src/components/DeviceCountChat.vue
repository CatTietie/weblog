<template>
    <!-- PV 折线图容器 -->
    <div id="pieChat1" class="overflow-x-auto w-full h-80"></div>
</template>

<script setup>
import * as echarts from 'echarts'
import { onMounted, watch } from 'vue'

// 对外暴露的属性值
const props = defineProps({
    value: { // 属性值名称
        type: Array, // 类型为数组
        default: () => [] // 默认为 null
    }
})

// 初始化折线图
function initPieChat() {
    var chartDom = document.getElementById('pieChat1');
    var myChart = echarts.init(chartDom);
    var option;

    const data = props.value

    console.log("data:", data)

    option = {
        tooltip: {
            trigger: 'item',
            backgroundColor: 'rgba(255, 255, 255, 0.9)',
            borderColor: '#ccc',
            borderWidth: 1,
            padding: 10,
            textStyle: {
                fontSize: 14,
                color: '#333'
            },
            formatter: function (params) {
                let total = 0;
                option.series[0].data.forEach(item => total += item.value);
                let percentage = ((params.value / total) * 100).toFixed(2);
                return `${params.name}：${params.value}（占比${percentage}%）`;
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
                itemStyle: {
                    normal: {
                        color: function (params) {
                            const colorList = ['#66ccff', '#ff9999', '#99cc99', '#ffcc66', '#cc99ff'];
                            return colorList[params.dataIndex % colorList.length];
                        }
                    }
                },
                label: {
                    show: true,
                    position: 'inner',
                    distance: 15,
                    formatter: '{b}：{d}%',
                    textStyle: {
                        fontSize: 12,
                        color: '#333'
                    }
                },
                labelLine: {
                    show: true,
                    length: 8,
                    length2: 8,
                    lineStyle: {
                        color: '#ccc'
                    }
                }
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