const EXPERIENCE_TYPES = ['education', 'work', 'project']

export const ENTRY_HEADER_PATTERN = /^(\d{4}|项目|[一-龥].*[-·—–])/

export function parseModuleEntries(content, type, moduleId) {
  if (!content || !content.trim()) {
    return [{ id: `${moduleId || 'empty'}-0`, text: '' }]
  }

  if (!EXPERIENCE_TYPES.includes(type)) {
    return [{ id: `${moduleId || 'single'}-0`, text: content }]
  }

  const paragraphs = content.split(/\n{2,}/)
  const entries = []
  let currentLines = []
  let entryIndex = 0

  for (let i = 0; i < paragraphs.length; i++) {
    const para = paragraphs[i].trim()
    if (!para) continue

    const isHeader = ENTRY_HEADER_PATTERN.test(para)

    if (isHeader && currentLines.length > 0) {
      entries.push({ id: `${moduleId || 'mod'}-${entryIndex++}`, text: currentLines.join('\n\n') })
      currentLines = [para]
    } else {
      currentLines.push(para)
    }
  }

  if (currentLines.length > 0) {
    entries.push({ id: `${moduleId || 'mod'}-${entryIndex}`, text: currentLines.join('\n\n') })
  }

  return entries.length > 0 ? entries : [{ id: `${moduleId || 'mod'}-0`, text: content }]
}

export function serializeEntries(entries) {
  return entries.map(e => e.text).join('\n\n')
}

export function isExperienceType(type) {
  return EXPERIENCE_TYPES.includes(type)
}
