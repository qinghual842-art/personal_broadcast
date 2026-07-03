import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

// Import only the icons actually used across the project
import {
  Search, Setting, User, Lock, ArrowDown, View, SwitchButton,
  Cpu, UserFilled, Plus, Edit, Delete, Loading, InfoFilled, Message
} from '@element-plus/icons-vue'

import App from './App.vue'
import router from './router'
import './assets/styles/global.scss'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)
app.use(ElementPlus)

// Register only actually-used icons to avoid 300+ unnecessary global components
const usedIcons = {
  Search, Setting, User, Lock, ArrowDown, View, SwitchButton,
  Cpu, UserFilled, Plus, Edit, Delete, Loading, InfoFilled, Message
}
for (const [key, component] of Object.entries(usedIcons)) {
  app.component(key, component)
}

// Global error handler for uncaught Vue errors
app.config.errorHandler = (err, instance, info) => {
  console.error('[App Error]', err, info)

  // In production, you could send this to an error tracking service
  // In development, let Vue's default behavior handle it too
}

app.mount('#app')
