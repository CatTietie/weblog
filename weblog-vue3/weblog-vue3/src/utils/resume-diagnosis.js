import { ENTRY_HEADER_PATTERN } from './resume-entries'

const ACTION_VERBS = [
  '负责', '主导', '设计', '开发', '实现', '优化', '搭建', '重构',
  '维护', '推动', '带领', '参与', '协调', '管理', '制定', '完成',
  '解决', '提升', '降低', '建设',
]

const DOMAIN_KEYWORDS = [
  '架构', '微服务', '分布式', '高并发', '高可用', '性能', '安全',
  '测试', '部署', 'CI/CD', '敏捷', '需求', '用户', '产品',
  '数据', '算法', 'API', '运维',
]

const TECH_PATTERN = /[A-Z][a-z]+(?:\.[a-z]+)?|[A-Z]{2,}|Vue|React|Angular|Spring|Docker|K8s|Kubernetes|MySQL|Redis|PostgreSQL|MongoDB|Linux|Git|AWS|Python|Java|Go|Node|TypeScript|Webpack|Nginx|Kafka|RabbitMQ|ElasticSearch/g

const QUANTIFICATION_PATTERNS = [
  /\d+[%％]/,
  /\d+\s*[万亿千百]+/,
  /提升.*?\d/,
  /降低.*?\d/,
  /减少.*?\d/,
  /增长.*?\d/,
  /覆盖.*?\d/,
  /服务.*?\d/,
  /完成.*?\d/,
  /\d+\s*(人|个|次|天|月|年|台|套|条|篇|家|项)/,
]

const MD_SYNTAX_PATTERNS = [
  /\*\*[^*]+\*\*/,
  /\[[^\]]+\]\([^)]+\)/,
  /^#{1,3}\s/m,
  /`[^`]+`/,
]

function getModulesByTypes(modules, types) {
  return modules.filter(m => types.includes(m.type))
}

function getBulletLines(content) {
  return content.split('\n').filter(l => /^\s*[-•]/.test(l.trim()))
}

function splitEntries(content) {
  return content.split(/\n{2,}/).map(p => p.trim()).filter(Boolean)
}

function hasQuantification(line) {
  return QUANTIFICATION_PATTERNS.some(p => p.test(line))
}

// --- Dimension 1: Completeness ---

function scoreCompleteness(modules, coverData) {
  let score = 0
  const suggestions = []

  const types = modules.map(m => m.type)
  const hasPersonal = types.includes('personal')
  const hasEducation = types.includes('education')
  const hasWork = types.includes('work')
  const hasProject = types.includes('project')
  const hasSkill = types.includes('skill')

  if (hasPersonal) {
    score += 15
    const personal = modules.find(m => m.type === 'personal')
    const colonLines = personal.content.split('\n').filter(l => /[:：]/.test(l))
    if (colonLines.length >= 3) {
      score += 5
    } else {
      suggestions.push({ dimension: 'completeness', priority: 'medium', message: '个人信息字段较少，建议补充电话、邮箱、所在地等联系方式' })
    }
  } else {
    suggestions.push({ dimension: 'completeness', priority: 'high', message: '缺少个人信息模块，建议添加基本联系方式' })
  }

  if (hasEducation) {
    score += 15
  } else {
    suggestions.push({ dimension: 'completeness', priority: 'high', message: '缺少教育经历模块' })
  }

  if (hasWork) {
    score += 20
    const workModules = modules.filter(m => m.type === 'work')
    const allEntries = workModules.flatMap(m => splitEntries(m.content))
    if (allEntries.length >= 2) {
      score += 10
    } else {
      suggestions.push({ dimension: 'completeness', priority: 'medium', message: '工作经历较少，建议补充更多工作经历或丰富现有描述' })
    }
  } else {
    suggestions.push({ dimension: 'completeness', priority: 'high', message: '缺少工作经历模块，这是招聘方最关注的部分' })
  }

  if (hasProject) {
    score += 10
  } else {
    suggestions.push({ dimension: 'completeness', priority: 'medium', message: '建议添加项目经历以展示实践能力' })
  }

  if (hasSkill) {
    score += 10
  } else {
    suggestions.push({ dimension: 'completeness', priority: 'medium', message: '缺少技能模块，建议列出核心技术栈' })
  }

  const contentModules = modules.filter(m => m.content && m.content.trim())
  if (contentModules.length > 0) {
    const avgLen = contentModules.reduce((sum, m) => sum + m.content.trim().length, 0) / contentModules.length
    if (avgLen >= 50) {
      score += 10
    } else {
      suggestions.push({ dimension: 'completeness', priority: 'medium', message: '部分模块内容过于简短，建议丰富描述' })
    }
  }

  if (coverData && coverData.title) {
    score += 5
  }

  return {
    key: 'completeness',
    label: '完整性',
    score: Math.min(score, 100),
    weight: 0.30,
    suggestions,
  }
}

// --- Dimension 2: Quantification ---

function scoreQuantification(modules) {
  const suggestions = []
  const experienceModules = getModulesByTypes(modules, ['work', 'project'])

  if (experienceModules.length === 0) {
    suggestions.push({ dimension: 'quantification', priority: 'high', message: '缺少工作/项目经历，无法评估量化程度' })
    return { key: 'quantification', label: '量化程度', score: 0, weight: 0.25, suggestions }
  }

  const allContent = experienceModules.map(m => m.content).join('\n')
  const bulletLines = getBulletLines(allContent)

  if (bulletLines.length === 0) {
    suggestions.push({ dimension: 'quantification', priority: 'high', message: '工作/项目经历建议使用"- "开头的列表来描述职责和成果' })
    return { key: 'quantification', label: '量化程度', score: 10, weight: 0.25, suggestions }
  }

  const quantifiedCount = bulletLines.filter(l => hasQuantification(l)).length
  const ratio = quantifiedCount / bulletLines.length

  let score
  if (ratio >= 0.5) score = 100
  else if (ratio >= 0.3) score = 80
  else if (ratio >= 0.15) score = 60
  else if (ratio >= 0.05) score = 40
  else if (ratio > 0) score = 20
  else score = 0

  if (score < 60) {
    const workModules = modules.filter(m => m.type === 'work')
    const projectModules = modules.filter(m => m.type === 'project')

    if (workModules.length > 0 && getBulletLines(workModules.map(m => m.content).join('\n')).filter(l => hasQuantification(l)).length === 0) {
      suggestions.push({ dimension: 'quantification', priority: 'high', message: '工作经历中缺少量化数据，建议用数字描述成果（如：提升性能30%、服务10万用户）' })
    }
    if (projectModules.length > 0 && getBulletLines(projectModules.map(m => m.content).join('\n')).filter(l => hasQuantification(l)).length === 0) {
      suggestions.push({ dimension: 'quantification', priority: 'medium', message: '项目经历建议添加具体成果数据（如：处理日均100万请求）' })
    }
    if (suggestions.length === 0) {
      suggestions.push({ dimension: 'quantification', priority: 'medium', message: '量化描述偏少，建议更多使用数据来支撑工作成果' })
    }
  }

  return { key: 'quantification', label: '量化程度', score, weight: 0.25, suggestions }
}

// --- Dimension 3: Keyword Coverage ---

function scoreKeywords(modules) {
  const suggestions = []
  const allContent = modules.map(m => m.content).join('\n')

  if (!allContent.trim()) {
    suggestions.push({ dimension: 'keywords', priority: 'high', message: '简历内容为空，无法评估关键词覆盖' })
    return { key: 'keywords', label: '关键词覆盖', score: 0, weight: 0.25, suggestions }
  }

  let actionVerbScore = 0
  const foundVerbs = ACTION_VERBS.filter(v => allContent.includes(v))
  if (foundVerbs.length >= 5) actionVerbScore = 40
  else if (foundVerbs.length >= 3) actionVerbScore = 25
  else if (foundVerbs.length >= 1) actionVerbScore = 10

  if (foundVerbs.length < 3) {
    suggestions.push({ dimension: 'keywords', priority: 'medium', message: '建议使用更多动作动词（如：负责、主导、优化、搭建）来描述工作内容' })
  }

  let techScore = 0
  const techMatches = allContent.match(TECH_PATTERN) || []
  const uniqueTech = [...new Set(techMatches)]
  if (uniqueTech.length >= 5) techScore = 30
  else if (uniqueTech.length >= 3) techScore = 20
  else if (uniqueTech.length >= 1) techScore = 10

  if (uniqueTech.length < 3) {
    suggestions.push({ dimension: 'keywords', priority: 'medium', message: '技术关键词较少，建议在工作和项目经历中明确提及使用的技术' })
  }

  let domainScore = 0
  const foundDomain = DOMAIN_KEYWORDS.filter(k => allContent.includes(k))
  if (foundDomain.length >= 3) domainScore = 30
  else if (foundDomain.length >= 2) domainScore = 20
  else if (foundDomain.length >= 1) domainScore = 10

  if (foundDomain.length < 2) {
    suggestions.push({ dimension: 'keywords', priority: 'low', message: '缺少领域关键词，建议提及架构、性能、用户等方向性描述' })
  }

  const score = Math.min(actionVerbScore + techScore + domainScore, 100)

  return { key: 'keywords', label: '关键词覆盖', score, weight: 0.25, suggestions }
}

// --- Dimension 4: Format Standards ---

function scoreFormat(modules) {
  const suggestions = []
  let score = 0

  const totalContent = modules.map(m => m.content).join('\n').trim()
  if (!totalContent || modules.length === 0) {
    suggestions.push({ dimension: 'format', priority: 'high', message: '简历内容为空，无法评估格式规范' })
    return { key: 'format', label: '格式规范', score: 0, weight: 0.20, suggestions }
  }

  const experienceModules = getModulesByTypes(modules, ['education', 'work', 'project'])

  // Year format check
  if (experienceModules.length > 0) {
    let yearOk = true
    experienceModules.forEach((m) => {
      const entries = splitEntries(m.content)
      entries.forEach((entry) => {
        const firstLine = entry.split('\n')[0]
        if (!(/\d{4}/.test(firstLine))) {
          yearOk = false
        }
      })
    })
    if (yearOk) {
      score += 25
    } else {
      suggestions.push({ dimension: 'format', priority: 'medium', message: '部分经历条目首行缺少年份标注，建议格式：20xx-20xx · 公司/学校' })
    }
  } else {
    suggestions.push({ dimension: 'format', priority: 'medium', message: '缺少经历类模块（教育/工作/项目），无法评估时间格式' })
  }

  // Separator consistency
  if (experienceModules.length > 0) {
    const allFirstLines = experienceModules.flatMap(m =>
      splitEntries(m.content).map(e => e.split('\n')[0])
    )
    const sepCounts = { '·': 0, '-': 0, '—': 0, '–': 0, '|': 0 }
    allFirstLines.forEach(line => {
      Object.keys(sepCounts).forEach(sep => {
        if (line.includes(sep)) sepCounts[sep]++
      })
    })
    const usedSeps = Object.entries(sepCounts).filter(([, c]) => c > 0)
    if (usedSeps.length <= 1) {
      score += 20
    } else {
      suggestions.push({ dimension: 'format', priority: 'low', message: '分隔符不一致，建议统一使用 · 作为信息分隔符' })
    }
  }

  // No markdown syntax
  const allContent = modules.map(m => m.content).join('\n')
  const hasMd = MD_SYNTAX_PATTERNS.some(p => p.test(allContent))
  if (!hasMd) {
    score += 15
  } else {
    suggestions.push({ dimension: 'format', priority: 'medium', message: '检测到 Markdown 语法，简历模板中将以原始文本显示，建议移除' })
  }

  // Bullet consistency
  const workProject = getModulesByTypes(modules, ['work', 'project'])
  if (workProject.length > 0) {
    const wpContent = workProject.map(m => m.content).join('\n')
    const contentLines = wpContent.split('\n').filter(l => l.trim() && !/^\d{4}/.test(l.trim()) && !ENTRY_HEADER_PATTERN.test(l.trim()))
    const bulletLines = contentLines.filter(l => /^\s*[-•]/.test(l.trim()))
    const bulletRatio = contentLines.length > 0 ? bulletLines.length / contentLines.length : 1
    if (bulletRatio >= 0.6) {
      score += 20
    } else {
      suggestions.push({ dimension: 'format', priority: 'medium', message: '建议使用"- "开头的列表来描述工作职责和项目成果' })
    }
  }

  // Module titles standard
  const recognizedModules = modules.filter(m => m.type !== 'other')
  if (recognizedModules.length / modules.length >= 0.8) {
    score += 10
  } else {
    suggestions.push({ dimension: 'format', priority: 'low', message: '部分模块标题无法识别类型，建议使用标准名称（如：个人信息、教育经历、工作经历）' })
  }

  // Excessive empty lines check
  const hasExcessiveEmpty = modules.some(m => /\n{4,}/.test(m.content))
  if (!hasExcessiveEmpty) {
    score += 10
  } else {
    suggestions.push({ dimension: 'format', priority: 'low', message: '部分模块存在过多空行，建议清理多余空白' })
  }

  return { key: 'format', label: '格式规范', score: Math.min(score, 100), weight: 0.20, suggestions }
}

// --- Main export ---

function collectSuggestions(dimensions) {
  const priorityOrder = { high: 0, medium: 1, low: 2 }
  const all = dimensions.flatMap(d => d.suggestions)
  all.sort((a, b) => priorityOrder[a.priority] - priorityOrder[b.priority])
  return all.slice(0, 12)
}

export function diagnoseResume(modules, coverData) {
  const completeness = scoreCompleteness(modules, coverData)
  const quantification = scoreQuantification(modules)
  const keywords = scoreKeywords(modules)
  const format = scoreFormat(modules)

  const dimensions = [completeness, quantification, keywords, format]

  const overall = Math.round(
    completeness.score * completeness.weight +
    quantification.score * quantification.weight +
    keywords.score * keywords.weight +
    format.score * format.weight
  )

  const suggestions = collectSuggestions(dimensions)

  return { overall, dimensions, suggestions }
}
