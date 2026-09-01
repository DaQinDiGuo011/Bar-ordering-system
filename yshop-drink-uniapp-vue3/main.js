/*
 * @Author: Gaoxs
 * @Date: 2023-04-07 15:12:06
 * @LastEditors: Gaoxs
 * @Description:
 */
import util from '@/utils'
import uviewPlus from 'uview-plus'

import App from './App'

import { createPinia } from 'pinia'

import { createSSRApp } from 'vue'

export function createApp() {
  const app = createSSRApp(App)
  app.use(util)
  app.use(uviewPlus)
  app.use(createPinia())
  return {
    app,
  }
}
