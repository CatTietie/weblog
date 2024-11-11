<template>
    <!-- PV 折线图容器 -->
    <div id="barChat1" class="overflow-x-auto w-full h-60"></div>
</template>

<script setup>
import * as echarts from 'echarts'
import { watch } from 'vue'

// 对外暴露的属性值
const props = defineProps({
    value: { // 属性值名称
        type: Object, // 类型为对象
        default: null // 默认为 null
    }
})

// 初始化柱状图
function initBarChat() {
    var chartDom = document.getElementById('barChat1');
    var myChart = echarts.init(chartDom);
    var option;


    const titles = props.value.xData
    const readNums = props.value.seriesData

    // 定义渐变颜色
    const colors = [
        { offset: 0, color: '#FF0000' }, // 最深的红色
        { offset: 1, color: '#FFAAAA' }  // 最浅的红色
    ];

    // 生成每个柱子的渐变色
    const itemStyles = readNums.map((_, index) => {
        return {
            color: new echarts.graphic.LinearGradient(
                0, 0, 0, 1, // 渐变方向，从上到下
                [
                    { offset: 0, color: `hsl(0, 100%, ${60 - (index * 5)}%)` }, // 计算每个柱子的起始颜色
                    { offset: 1, color: `hsl(0, 100%, ${80 - (index * 5)}%)` }  // 计算每个柱子的结束颜色
                ]
            )
        };
    });




    option = {
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        xAxis: {
            type: 'value',
            boundaryGap: [0, 0.01]
        },
        yAxis: {
            type: 'category',
            data: titles
        },
        series: [
        {
                name: '阅读量',
                type: 'bar',
                data: readNums,
                itemStyle: {
                    normal: {
                        color: function(params) {
                            return itemStyles[params.dataIndex].color;
                        }
                    }
                }
            }
        ],
        legend: {
            show: 'true',
            itemStyle:{
                color:'#d50909'
            }
        },
    };

    option && myChart.setOption(option);
}

// 侦听属性, 监听 props.value 的变化，一旦 props.value 发生变化，就调用 initLineChat 初始化折线图
watch(() => props.value, () => initBarChat())
</script>