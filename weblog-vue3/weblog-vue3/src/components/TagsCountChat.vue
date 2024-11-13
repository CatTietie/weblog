<template>
    <!-- PV 折线图容器 -->
    <div id="pieChat2" class="overflow-x-auto w-full h-60"></div>
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
    const data = props.value.map(item => ({
        value: parseInt(item.value, 10),
        name: item.name
    }));

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
        tooltip: {
            trigger: 'item'
        },
        legend: {
            orient: 'vertical',
            left: 'left'
        },
        series: [
            {
                name: '文章数',
                type: 'pie',
                radius: ['40%', '90%'],
                data: data,
                itemStyle: {
                    borderRadius: 3,
                    borderColor: '#fff',
                    borderWidth: 2
                },
                emphasis: {
                    itemStyle: {
                        shadowBlur: 10,
                        shadowOffsetX: 0,
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
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