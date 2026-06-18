export const LANGUAGE_OPTIONS = [
  { code: 'zh', label: '中文' },
  { code: 'en', label: 'English' },
  { code: 'ja', label: '日本語' },
  { code: 'ko', label: '한국어' },
  { code: 'fr', label: 'Français' },
  { code: 'de', label: 'Deutsch' },
  { code: 'es', label: 'Español' },
]

export function getLanguageLabel(code) {
  const opt = LANGUAGE_OPTIONS.find(o => o.code === code)
  return opt ? opt.label : code
}

export function parseLanguagesData(languagesJson) {
  if (!languagesJson) {
    return { langs: ['zh'], defaultLang: 'zh', currentLang: 'zh', contents: {} }
  }
  try {
    const data = JSON.parse(languagesJson)
    return {
      langs: data.langs || ['zh'],
      defaultLang: data.defaultLang || data.langs?.[0] || 'zh',
      currentLang: data.currentLang || data.langs?.[0] || 'zh',
      contents: data.contents || {},
    }
  } catch {
    return { langs: ['zh'], defaultLang: 'zh', currentLang: 'zh', contents: {} }
  }
}

export function serializeLanguagesData({ langs, defaultLang, currentLang, contents }) {
  return JSON.stringify({ langs, defaultLang, currentLang, contents })
}
