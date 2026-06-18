import { ENTRY_HEADER_PATTERN } from './resume-entries'

const MD_SYNTAX_PATTERNS = [
  { regex: /\*\*[^*]+\*\*/, label: '粗体 **text**' },
  { regex: /\[[^\]]+\]\([^)]+\)/, label: '链接 [text](url)' },
  { regex: /^#{1,3}\s/, label: '标题 # heading' },
  { regex: /`[^`]+`/, label: '行内代码 `code`' },
]

function detectMarkdownSyntax(content) {
  const found = []
  for (const { regex, label } of MD_SYNTAX_PATTERNS) {
    if (regex.test(content)) {
      found.push(label)
    }
  }
  return found
}

function splitEntries(content) {
  return content.split(/\n{2,}/).map(p => p.trim()).filter(Boolean)
}

function countSeparators(line) {
  return (line.match(/[·\-—–]/g) || []).length
}

function validatePersonal(content) {
  const warnings = []
  const lines = content.split('\n').filter(l => l.trim())

  const hasColon = lines.some(l => /[:：]/.test(l))
  if (!hasColon && lines.length > 0) {
    warnings.push({ level: 'warn', message: '个人信息建议使用"字段：内容"格式（如 姓名：张三）' })
  }

  const fields = content.split(/\n\s*\n/).filter(p => p.trim())
  if (fields.length < 2 && lines.length < 3) {
    warnings.push({ level: 'info', message: '个人信息字段较少，建议补充联系方式等基本信息' })
  }

  return warnings
}

function validateEducation(content) {
  const warnings = []
  const entries = splitEntries(content)

  for (let i = 0; i < entries.length; i++) {
    const firstLine = entries[i].split('\n')[0]
    if (!/\d{4}/.test(firstLine)) {
      warnings.push({ level: 'warn', message: `第 ${i + 1} 条教育经历首行未包含年份，建议格式：20xx-20xx · 学校 · 专业 · 学历` })
    } else if (countSeparators(firstLine) < 2) {
      warnings.push({ level: 'info', message: `第 ${i + 1} 条教育经历信息可能不完整，建议包含学校、专业、学历` })
    }
  }

  return warnings
}

function validateWork(content) {
  const warnings = []
  const entries = splitEntries(content)

  for (let i = 0; i < entries.length; i++) {
    const lines = entries[i].split('\n')
    const firstLine = lines[0]

    if (!/\d{4}/.test(firstLine)) {
      warnings.push({ level: 'warn', message: `第 ${i + 1} 条工作经历首行未包含年份，建议格式：20xx-至今 · 公司 · 职位` })
    } else if (countSeparators(firstLine) < 1) {
      warnings.push({ level: 'info', message: `第 ${i + 1} 条工作经历建议用分隔符区分公司与职位（如 · 或 -）` })
    }

    const hasBullets = lines.slice(1).some(l => /^\s*[-•]/.test(l))
    if (!hasBullets && lines.length <= 1) {
      warnings.push({ level: 'info', message: `第 ${i + 1} 条工作经历暂无描述内容，建议用 "- " 开头列出工作职责` })
    }
  }

  return warnings
}

function validateProject(content) {
  const warnings = []
  const entries = splitEntries(content)

  for (let i = 0; i < entries.length; i++) {
    const lines = entries[i].split('\n')
    const firstLine = lines[0]

    if (!ENTRY_HEADER_PATTERN.test(firstLine)) {
      warnings.push({ level: 'warn', message: `第 ${i + 1} 个项目首行无法被识别为条目标题，建议以年份或项目名开头` })
    }

    const entryText = entries[i]
    if (!/技术栈|技术|Tech/i.test(entryText)) {
      warnings.push({ level: 'info', message: `第 ${i + 1} 个项目未标注技术栈，建议注明使用的技术` })
    }
  }

  return warnings
}

function validateSkill(content) {
  const warnings = []

  if (/\[[^\]]+\]\([^)]+\)/.test(content)) {
    warnings.push({ level: 'warn', message: '链接语法 [text](url) 在简历模板中不会渲染为可点击链接' })
  }

  return warnings
}

export function validateModuleContent(content, type) {
  if (!content || !content.trim()) return []

  const warnings = []

  switch (type) {
    case 'personal':
      warnings.push(...validatePersonal(content))
      break
    case 'education':
      warnings.push(...validateEducation(content))
      break
    case 'work':
      warnings.push(...validateWork(content))
      break
    case 'project':
      warnings.push(...validateProject(content))
      break
    case 'skill':
      warnings.push(...validateSkill(content))
      break
  }

  const mdSyntax = detectMarkdownSyntax(content)
  if (mdSyntax.length > 0) {
    warnings.push({
      level: 'warn',
      message: `检测到 Markdown 格式（${mdSyntax.join('、')}），在最终简历模板中将以原始文本显示`,
    })
  }

  return warnings
}
