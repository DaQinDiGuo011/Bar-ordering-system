/**
 * 播放叮咚提示音
 */
function playDingSound() {
  try {
    const audioCtx = new (window.AudioContext || (window as any).webkitAudioContext)()
    const oscillator = audioCtx.createOscillator()
    const gainNode = audioCtx.createGain()
    oscillator.connect(gainNode)
    gainNode.connect(audioCtx.destination)

    oscillator.type = 'sine'
    oscillator.frequency.value = 880
    gainNode.gain.value = 0.2

    oscillator.start()
    oscillator.stop(audioCtx.currentTime + 0.2)
  } catch (e) {
    console.warn('叮咚音效失败', e)
  }
}

/**
 * 语音朗读文本
 * @param text 需要朗读的消息
 */
function speechReadText(text: string) {
  if (!('speechSynthesis' in window)) return
  // 停止上一条朗读，防止多条消息堆积
  window.speechSynthesis.cancel()

  const utter = new SpeechSynthesisUtterance(text)
  utter.lang = 'zh‑CN' //中文
  utter.rate = 1.0 //语速
  utter.volume = 0.8
  // 可选：切换人声 utter.voice = xxx

  window.speechSynthesis.speak(utter)
}

/**
 * 组合：先叮咚，间隔再朗读消息
 * @param msg 要播报的消息文本
 */
export function dingThenSpeak(msg: string) {
  playDingSound()
  // 叮咚响完，延迟350ms再读文字
  setTimeout(() => {
    speechReadText(msg)
  }, 350)
}

/**
 * 唤醒音频（必须绑定登录/页面点击，浏览器安全策略）
 * 用户点击的时候调用一次，解锁音频&语音
 */
export function wakeAudioAndSpeech() {
  //唤醒AudioContext
  const ctx = new (window.AudioContext || (window as any).webkitAudioContext)()
  ctx.close()
  //空朗读唤醒语音
  const ut = new SpeechSynthesisUtterance('')
  window.speechSynthesis.speak(ut)
}
