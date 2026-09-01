import { watch, ref } from 'vue'
import { useWebSocket } from '@vueuse/core'
import { ElNotification, ElMessageBox } from 'element-plus'
import { getAccessToken } from '@/utils/auth'
import { wakeAudioAndSpeech, dingThenSpeak} from './soundPrompts'
import { addUnhandledOrder, getUnhandledCount } from './handleOrderNotice'

let ws: ReturnType<typeof useWebSocket> | null = null
let retryTimer: number | null = null
const retryCount = ref(0)
const MAX_RETRY = 5 //最大重试次数

const buildUrl = () => {
  // ✅开发环境返回相对路径，走vite代理
  console.log("-----import.meta.env.DEV---------",import.meta.env.DEV)
  console.log("-----import.meta.env.VITE_BASE_URL---------",import.meta.env.VITE_BASE_URL)
  // if (import.meta.env.DEV) {
  //   return `/infra/ws?token=${getAccessToken()}`
  // }

  // 生产环境替换协议
  // return (import.meta.env.VITE_BASE_URL + '/infra/ws')
  //   .replace('http', 'wws') + '?token=' + getAccessToken()

  const token = getAccessToken()
  const path = `/infra/ws?token=${token}`
  if (import.meta.env.DEV) {
    // 读取当前浏览器location，带上正确host+端口
    const loc = window.location
    console.log("loc.host =", loc.host)
    return `${loc.protocol === 'https:' ? 'wss:' : 'ws:'}//${loc.host}${path}`
  } else {
    //生产环境
    return (import.meta.env.VITE_BASE_URL + '/infra/ws')
      .replace('http://','ws://')
      .replace('https://','wss://')
      + `?token=${token}`
  }
}

// 手动重连逻辑
const manualReconnect = () => {
  // 没有token，直接停止重连
  if (!getAccessToken()) {
    closeGlobalWebSocket()
    return
  }
  if (retryCount.value >= MAX_RETRY) {
    console.warn('ws达到最大重试次数，停止重连')
    ws = null
    return
  }
  retryCount.value += 1
  // 延时重连
  retryTimer = window.setTimeout(() => {
    initGlobalWebSocket()
  }, 3000)
}
let currentNotifyInstance = null
export const initGlobalWebSocket = () => {
  // 清除旧定时器
  if (retryTimer) {
    clearTimeout(retryTimer)
    retryTimer = null
  }
  // 已经存在实例直接返回
  if (ws || !getAccessToken()) return
  console.log("------buildUrl()---------",buildUrl())
  wakeAudioAndSpeech()
  ws = useWebSocket(buildUrl(), {
    autoReconnect: false,
    heartbeat: false,
    // ✅【关键修复】初始化配置直接传入onClose，不要实例后再绑定
    onClose: () => {
      // ws = null
      manualReconnect()
    },
    onError: (err) => {
      console.error('ws发生错误', err)
    },
    onMessage:(rawData, wsInstance)=>{
      
      const data = wsInstance.data
      console.log(wsInstance,"----websocket notice -----", data)
      if (!data || data === 'pong') return
      try {
        const message = JSON.parse(data)
        
        if (message.type !== 'notice-push') return
        const content = JSON.parse(message.content)
        const readStr = "有新订单了，请到工作台及时处理";
        // if(content.content){
        //   addUnhandledOrder(content.content)
        //   const unHandleCount = getUnhandledCount()
        //   readStr = `系统通知，当前还有${unHandleCount}条带处理订单，请到工作台及时处理`
        // }
        dingThenSpeak(readStr)
        // const notifyInstance = ElNotification({
        //   title: content.title || '系统通知',
        //   type: content.level === 'error' ? 'error' : 'success',
        //   duration: 0, // 关键：禁止自动超时关闭
        //   message: h('div', { style: 'display:flex;justify-content:space-between;align-items:center;gap:12px;' }, [
        //     h('span', content.content || content.title),
        //     h(ElButton, {
        //       size: 'small',
        //       type: 'primary',
        //       onClick: () => {
        //         // 使用拿到的实例 close()
        //         notifyInstance.close()
        //       }
        //     }, { default: () => '已读' })
        //   ])
        // })

        // 如果存在上一条通知，先关闭旧通知
        if(currentNotifyInstance){
          currentNotifyInstance.close()
          currentNotifyInstance = null
        }

        // 创建新通知，新通知不自动关闭 duration:0
        currentNotifyInstance = ElNotification({
          title: '系统通知',
          message: readStr,
          type: content.level === 'error' ? 'error' : 'success',
          duration: 0, // 新消息不自动关闭
        })
        if(content.important){
          ElMessageBox.alert(content.content || content.title, content.title || '系统通知')
        }
      } catch (e) {
        console.warn('ws消息解析失败', e)
      }
    }
  })

  // watch(ws.data, (data) => {
  //   console.log("----websocket notice ------", data)
  //   if (!data || data === 'pong') return
  //   try {
  //     const message = JSON.parse(data)
  //     if (message.type !== 'notice-push') return
  //     const content = JSON.parse(message.content)

  //     // 普通消息：右上角通知
  //     ElNotification({
  //       title: content.title || '系统通知',
  //       message: content.content || content.title,
  //       type: content.level === 'error' ? 'error' : 'success',
  //       duration: 5000
  //     })

  //     // 重要消息：居中弹框
  //     if (content.important) {
  //       ElMessageBox.alert(content.content || content.title, content.title || '系统通知')
  //     }
  //   } catch (e) {
  //     console.warn('ws消息解析失败', e)
  //   }
  // })
  // ws.open()
}

export const closeGlobalWebSocket = async () => {
  // 清除重连定时器
  if (retryTimer) {
    clearTimeout(retryTimer)
    retryTimer = null
  }
  retryCount.value = 0
  if (ws) {
    try {
      // close是Promise，捕获stop异常，消除 Uncaught (in promise) stop
      await ws.close()
    } catch (err) {
      // ws已经关闭时抛出stop，直接忽略
    }
    ws = null
  }
}