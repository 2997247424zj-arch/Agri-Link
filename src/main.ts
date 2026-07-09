import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import './styles/main.css'
import { installPerformanceMonitoring } from './utils/performance'

const app = createApp(App)

installPerformanceMonitoring()

app.use(createPinia())
app.use(router)

app.mount('#app')
