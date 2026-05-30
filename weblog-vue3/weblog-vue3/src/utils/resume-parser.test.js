import assert from 'node:assert'
import { parseResume, serializeResume, detectType, getTypeLabel, getDefaultContent } from './resume-parser.js'

const SAMPLE_MD = `# 小博简历

## 个人信息
姓名：你的姓名

电话：13800138000

邮箱：example@email.com

## 教育经历
2016-2020 · XX大学 · 计算机科学 · 本科

2012-2016 · XX高中

## 工作经历
2020-至今 · 某某科技有限公司 · 前端开发

负责公司核心产品的前端架构设计

## 项目经历
博客系统 - 技术栈：Vue3 + Spring Boot + MySQL

独立开发全栈博客平台

## 技能
JavaScript, TypeScript, Vue3, React

Node.js, Spring Boot, MySQL

## 其他
英语六级，可流畅阅读英文文档
`

// Test: parse module count
const result = parseResume(SAMPLE_MD)
assert.strictEqual(result.name, '小博简历')
assert.strictEqual(result.modules.length, 6)

// Test: type detection
assert.strictEqual(result.modules[0].type, 'personal')
assert.strictEqual(result.modules[1].type, 'education')
assert.strictEqual(result.modules[2].type, 'work')
assert.strictEqual(result.modules[3].type, 'project')
assert.strictEqual(result.modules[4].type, 'skill')
assert.strictEqual(result.modules[5].type, 'other')

// Test: module titles
assert.strictEqual(result.modules[0].title, '个人信息')
assert.strictEqual(result.modules[5].title, '其他')

// Test: content preserved
assert.ok(result.modules[0].content.includes('姓名：你的姓名'))
assert.ok(result.modules[4].content.includes('JavaScript'))

// Test: each module has unique id
const ids = result.modules.map(m => m.id)
assert.strictEqual(new Set(ids).size, 6)

// Test: roundtrip (serialize then re-parse)
const serialized = serializeResume(result)
const reparsed = parseResume(serialized)
assert.strictEqual(reparsed.name, result.name)
assert.strictEqual(reparsed.modules.length, result.modules.length)
for (let i = 0; i < result.modules.length; i++) {
  assert.strictEqual(reparsed.modules[i].title, result.modules[i].title)
  assert.strictEqual(reparsed.modules[i].type, result.modules[i].type)
  assert.strictEqual(reparsed.modules[i].content, result.modules[i].content)
}

// Test: empty input
const empty = parseResume('')
assert.strictEqual(empty.name, '')
assert.strictEqual(empty.modules.length, 0)

// Test: no h1
const noH1 = parseResume('## 技能\nVue3, React')
assert.strictEqual(noH1.name, '')
assert.strictEqual(noH1.modules.length, 1)
assert.strictEqual(noH1.modules[0].title, '技能')
assert.strictEqual(noH1.modules[0].content, 'Vue3, React')

// Test: detectType
assert.strictEqual(detectType('个人信息'), 'personal')
assert.strictEqual(detectType('我的教育经历'), 'education')
assert.strictEqual(detectType('随便什么'), 'other')

// Test: getTypeLabel
assert.strictEqual(getTypeLabel('personal'), '个人信息')
assert.strictEqual(getTypeLabel('unknown'), '其他')

// Test: getDefaultContent returns non-empty string
assert.ok(getDefaultContent('personal').length > 0)
assert.ok(getDefaultContent('education').length > 0)
assert.ok(getDefaultContent('other').length > 0)

console.log('All tests passed!')
