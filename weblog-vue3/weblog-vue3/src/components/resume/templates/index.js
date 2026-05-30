import ClassicTemplate from './ClassicTemplate.vue'
import ModernTemplate from './ModernTemplate.vue'
import SimpleTemplate from './SimpleTemplate.vue'

export const TEMPLATES = [
  {
    id: 'default',
    name: '经典左右分栏',
    component: ClassicTemplate,
  },
  {
    id: 'modern',
    name: '现代全宽色块',
    component: ModernTemplate,
  },
  {
    id: 'simple',
    name: '简洁单栏',
    component: SimpleTemplate,
  },
]

export function getTemplateById(id) {
  return TEMPLATES.find(t => t.id === id) || TEMPLATES[0]
}
