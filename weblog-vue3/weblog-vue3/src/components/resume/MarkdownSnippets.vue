<template>
    <DropdownToolbar
        title="插入模板片段"
        :visible="visible"
        @onChange="visible = $event"
    >
        <template #trigger>
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none"
                stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M14.5 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7.5L14.5 2z"/>
                <polyline points="14 2 14 8 20 8"/>
                <line x1="16" y1="13" x2="8" y2="13"/>
                <line x1="16" y1="17" x2="8" y2="17"/>
                <line x1="10" y1="9" x2="8" y2="9"/>
            </svg>
        </template>
        <template #overlay>
            <div class="snippet-panel">
                <div v-if="typeSnippets.length" class="snippet-group">
                    <div class="snippet-group-title">{{ groupTitle }}</div>
                    <div
                        v-for="item in typeSnippets"
                        :key="item.label"
                        class="snippet-item"
                        @click="insertSnippet(item.text)"
                    >
                        <span class="snippet-label">{{ item.label }}</span>
                        <span class="snippet-desc">{{ item.desc }}</span>
                    </div>
                </div>
                <div class="snippet-group">
                    <div class="snippet-group-title">通用格式</div>
                    <div
                        v-for="item in commonSnippets"
                        :key="item.label"
                        class="snippet-item"
                        @click="insertSnippet(item.text)"
                    >
                        <span class="snippet-label">{{ item.label }}</span>
                        <span class="snippet-desc">{{ item.desc }}</span>
                    </div>
                </div>
            </div>
        </template>
    </DropdownToolbar>
</template>

<script setup>
import { ref, computed } from 'vue'
import { DropdownToolbar } from 'md-editor-v3'

const props = defineProps({
    type: { type: String, default: 'other' },
    insert: { type: Function, default: null }
})

const visible = ref(false)

const SNIPPETS_MAP = {
    personal: {
        title: '个人信息',
        items: [
            { label: '键值对', desc: '字段：内容', text: '字段：内容\n\n' },
            { label: '联系方式', desc: '电话 / 邮箱 / 地址', text: '电话：138xxxx0000\n\n邮箱：example@mail.com\n\n所在地：城市' },
            { label: '个人链接', desc: 'GitHub / 作品集', text: 'GitHub：https://github.com/yourname\n\n个人网站：https://yoursite.com' },
        ]
    },
    education: {
        title: '教育经历',
        items: [
            { label: '教育条目', desc: '时间 · 学校 · 专业 · 学历', text: '20xx-20xx · 学校名称 · 专业名称 · 本科/硕士' },
            { label: '课程/成绩', desc: '主修课程、GPA', text: '\n\n主修课程：课程1、课程2、课程3\n\nGPA：x.xx / 4.0' },
            { label: '荣誉奖项', desc: '在校获奖情况', text: '\n\n- 20xx年 奖项名称\n- 20xx年 奖项名称' },
        ]
    },
    work: {
        title: '工作经历',
        items: [
            { label: '工作条目', desc: '时间 · 公司 · 职位 + 描述', text: '20xx-至今 · 公司名称 · 职位名称\n\n- 负责XXX系统的设计与开发\n- 优化了XXX，提升了XX%性能' },
            { label: '职责列表', desc: '列举工作职责', text: '- 负责XXX模块的设计与开发\n- 参与XXX项目的需求分析\n- 维护XXX系统的稳定运行' },
            { label: '业绩描述', desc: '量化成果', text: '- 将XXX性能提升了XX%\n- 带领X人团队完成了XXX项目\n- 服务覆盖XX万用户' },
        ]
    },
    project: {
        title: '项目经历',
        items: [
            { label: '项目条目', desc: '项目名 + 技术栈 + 描述', text: '项目名称 - 技术栈：Vue3, Spring Boot\n\n- 项目背景与目标描述\n- 个人职责与贡献\n- 项目成果与数据' },
            { label: '技术栈', desc: '使用的技术列表', text: '技术栈：技术1, 技术2, 技术3' },
            { label: '项目成果', desc: '量化结果', text: '\n\n项目成果：\n- 实现了XXX功能\n- 性能提升XX%\n- 用户量达到XX万' },
        ]
    },
    skill: {
        title: '技能',
        items: [
            { label: '技能标签', desc: '逗号分隔技能', text: '技能1, 技能2, 技能3, 技能4' },
            { label: '技能分类', desc: '按类别归组', text: '**前端**：Vue, React, TypeScript\n\n**后端**：Java, Spring Boot, MySQL\n\n**工具**：Git, Docker, Linux' },
            { label: '熟练度标注', desc: '标明掌握程度', text: '- 精通：技能A, 技能B\n- 熟练：技能C, 技能D\n- 了解：技能E, 技能F' },
        ]
    },
    other: {
        title: '其他',
        items: [
            { label: '自我评价', desc: '个人总结', text: '具备X年XXX经验，擅长XXX，熟悉XXX领域。' },
            { label: '兴趣爱好', desc: '业余爱好', text: '阅读、开源贡献、技术博客写作' },
            { label: '证书/语言', desc: '资格证书', text: '- CET-6\n- XXX职业资格证书\n- 普通话二级甲等' },
        ]
    },
}

const commonSnippets = [
    { label: '加粗', desc: '**粗体文字**', text: '**文本**' },
    { label: '无序列表', desc: '- 列表项', text: '- 内容\n- 内容\n- 内容' },
    { label: '链接', desc: '[显示文本](URL)', text: '[链接文本](https://example.com)' },
    { label: '分隔线', desc: '水平分割线', text: '\n---\n' },
]

const typeSnippets = computed(() => {
    return SNIPPETS_MAP[props.type]?.items || SNIPPETS_MAP.other.items
})

const groupTitle = computed(() => {
    return SNIPPETS_MAP[props.type]?.title || '其他'
})

function insertSnippet(text) {
    if (props.insert) {
        props.insert(() => ({
            targetValue: text,
            select: false,
            deviationStart: 0,
            deviationEnd: 0,
        }))
    }
    visible.value = false
}
</script>

<style scoped>
.snippet-panel {
    padding: 8px 4px;
    min-width: 240px;
    max-height: 320px;
    overflow-y: auto;
    overflow-x: visible;
}

.snippet-group {
    padding: 0 8px;
}

.snippet-group + .snippet-group {
    margin-top: 8px;
    padding-top: 8px;
    border-top: 1px solid #e5e7eb;
}

.snippet-group-title {
    font-size: 12px;
    color: #6b7280;
    padding: 2px 4px 4px;
    font-weight: 500;
}

.snippet-item {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 6px 8px;
    border-radius: 4px;
    cursor: pointer;
    transition: background-color 0.15s;
}

.snippet-item:hover {
    background-color: #f3f4f6;
}

.snippet-label {
    font-size: 13px;
    color: #1f2937;
    font-weight: 500;
    white-space: nowrap;
    flex-shrink: 0;
}

.snippet-desc {
    font-size: 12px;
    color: #9ca3af;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

:deep(.dark) .snippet-group + .snippet-group {
    border-color: #374151;
}

:deep(.dark) .snippet-item:hover {
    background-color: #374151;
}

:deep(.dark) .snippet-label {
    color: #f3f4f6;
}

:deep(.dark) .snippet-group-title {
    color: #9ca3af;
}
</style>

<style>
.md-editor .md-editor-dropdown:not(.md-editor-dropdown-hidden) {
    overflow: visible !important;
    left: 0 !important;
}
</style>
