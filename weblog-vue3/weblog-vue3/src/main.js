import '@/assets/main.css'
import 'animate.css';
import 'nprogress/nprogress.css'

import { createApp } from 'vue'
import pinia from '@/stores'
import App from '@/App.vue'
import router from '@/router'
import i18n from '@/i18n'
import '@/permission'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import 'viewerjs/dist/viewer.css'
import VueViewer from 'v-viewer'

const app = createApp(App)

app.use(router)
app.use(pinia)
app.use(i18n)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

app.use(VueViewer)

app.mount('#app')
