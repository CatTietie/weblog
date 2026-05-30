let idCounter = 0

export function generateId() {
  if (typeof crypto !== 'undefined' && crypto.randomUUID) {
    return crypto.randomUUID()
  }
  return `module-${Date.now()}-${++idCounter}`
}

const TYPE_KEYWORDS = [
  { keywords: ['个人信息', '基本信息', '联系方式', 'Personal', 'Contact', 'About'], type: 'personal' },
  { keywords: ['教育', 'Education'], type: 'education' },
  { keywords: ['工作经历', '工作经验', 'Work Experience', 'Employment', 'Experience'], type: 'work' },
  { keywords: ['项目', 'Project'], type: 'project' },
  { keywords: ['技能', '技术栈', 'Skill', 'Technical'], type: 'skill' },
]

export function detectType(title) {
  const t = title.trim().toLowerCase()
  for (const { keywords, type } of TYPE_KEYWORDS) {
    if (keywords.some(k => t.includes(k.toLowerCase()))) return type
  }
  return 'other'
}

const TYPE_LABELS = {
  personal: '个人信息',
  education: '教育经历',
  work: '工作经历',
  project: '项目经历',
  skill: '技能',
  other: '其他',
}

export function getTypeLabel(type) {
  return TYPE_LABELS[type] || '其他'
}

const DEFAULT_CONTENTS = {
  personal: '姓名：\n\n电话：\n\n邮箱：\n\n所在地：',
  education: '20xx-20xx · 学校名称 · 专业 · 学历',
  work: '20xx-至今 · 公司名称 · 职位\n\n工作内容描述',
  project: '项目名称 - 技术栈：\n\n项目描述',
  skill: '技能1, 技能2, 技能3',
  other: '内容',
}

export function getDefaultContent(type) {
  return DEFAULT_CONTENTS[type] || DEFAULT_CONTENTS.other
}

export function parseResume(markdown) {
  if (!markdown || !markdown.trim()) {
    return { name: '', modules: [] }
  }

  const lines = markdown.split('\n')
  let name = ''
  const modules = []
  let currentTitle = null
  let currentLines = []

  function flushModule() {
    if (currentTitle !== null) {
      const content = currentLines.join('\n').replace(/^\n+|\n+$/g, '')
      modules.push({
        id: generateId(),
        title: currentTitle,
        type: detectType(currentTitle),
        content,
      })
    }
    currentTitle = null
    currentLines = []
  }

  for (const line of lines) {
    const h1Match = line.match(/^# (.+)$/)
    const h2Match = line.match(/^## (.+)$/)

    if (h1Match && !name) {
      name = h1Match[1].trim()
    } else if (h2Match) {
      flushModule()
      currentTitle = h2Match[1].trim()
    } else if (currentTitle !== null) {
      currentLines.push(line)
    }
  }
  flushModule()

  return { name, modules }
}

export function serializeResume({ name, modules }) {
  let md = ''
  if (name) {
    md += `# ${name}\n\n`
  }
  for (const mod of modules) {
    md += `## ${mod.title}\n${mod.content}\n\n`
  }
  return md.replace(/\n{3,}$/g, '\n')
}

export function validateResumeMarkdown(content) {
  if (!content || !content.trim()) {
    return { valid: false, error: '文件内容为空' }
  }

  const parsed = parseResume(content)

  if (parsed.modules.length === 0) {
    return { valid: false, error: '未检测到有效的简历模块（需要 ## 标题）' }
  }

  const recognizedTypes = ['personal', 'education', 'work', 'project', 'skill']
  const hasRecognized = parsed.modules.some(m => recognizedTypes.includes(m.type))
  if (!hasRecognized) {
    return { valid: false, error: '未检测到标准简历模块（如"## 个人信息"、"## 教育经历"等）' }
  }

  return { valid: true, data: parsed }
}
