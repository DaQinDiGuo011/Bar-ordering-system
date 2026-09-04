<template>
  <view class="tournament-page">
    <!-- 背景层 -->
    <view class="bg-layer"></view>

    <view class="content-wrap">
      <view class="main-container">
        <!-- 左侧区域 -->
        <view class="col-left">
          <view class="logo-wrap">
            <image class="logo-img" src="/static/logo.jpg" mode="aspectFit"></image>
          </view>

          <view class="panel-item">
            <view class="label">当前级别</view>
            <view class="label-en">Current level</view>
            <view class="val-big">{{ displayLevel }}</view>
          </view>
          <view class="panel-item">
            <view class="label">参赛人数</view>
            <view class="label-en">Entrants</view>
            <view class="val-big">{{ playerCount }}/{{ totalPlayer }}</view>
          </view>

			<view class="pause-btn-wrap">
		      <view class="pause-btn" :class="{pauseActive: isPaused}" @click="togglePause">
		        {{ isPaused ? '开始' : '暂停' }}
		      </view>
		    </view>

			 <view class="level-btn-wrap">
			   <view
			     class="level-btn btn-prev"
			     :class="{disabled: gameOver}"
			     @click="prevLevel"
			   >上一级</view>
			   <view
			     class="level-btn btn-next"
			     :class="{disabled: gameOver}"
			     @click="nextManualLevel"
			   >下一级</view>
			 </view>
        </view>

        <!-- 垂直分割线 -->
        <view class="divider-vertical"></view>

        <!-- 中间区域 -->
        <view class="col-center">
          <view class="header-row">
            <view class="tournament-title">MTT猎人赛</view>
            <!-- 时间放在标题下方，换行 -->
            <view class="current-time">{{ nowTime }}</view>
          </view>
          <view class="center-timer">
            <view
              class="time-text"
              :class="{ breakTimer: blindLevelList[currentIndex]?.type === 'break', gameOver: gameOver }"
            >
              {{ gameOver ? '结束' : (blindLevelList[currentIndex]?.type === 'break' ? '休息 ' + formatTime(timerSecond) : formatTime(timerSecond)) }}
            </view>
          </view>
          <view class="bottom-blind-wrap">
            <view class="blind-left-col">
              <view class="blind-item">
                <view class="label">基础分</view>
                <view class="label-en">BLINDS</view>
              </view>
              <view class="blind-item">
                <view class="label">预置分</view>
                <view class="label-en">ANTE</view>
              </view>
              <view class="blind-item">
                <view class="label">下级别</view>
                <view class="label-en">NEXT LEVEL</view>
              </view>
            </view>
            <view class="blind-right-val">
              <view class="val-item">{{ blindCur }}</view>
              <view class="val-item">{{ anteCur }}</view>
              <view class="val-item">{{ blindNext }}</view>
            </view>
          </view>
        </view>

        <!-- 垂直分割线 -->
        <view class="divider-vertical"></view>

        <!-- 右侧区域 -->
        <view class="col-right">
          <view class="col-item">
            <view class="label">下次休息</view>
            <view class="label-en">Next break</view>
            <view class="val-big">{{ hasNextBreak ? formatTime(breakSecond) : '—' }}</view>
          </view>
          <view class="col-item">
            <view class="label">平均记分牌</view>
            <view class="label-en">Avg chips</view>
            <view class="val-big">{{ avgChips }}</view>
          </view>
          <view class="col-item">
            <view class="label">总记分牌</view>
            <view class="label-en">Total chips</view>
            <view class="val-big">{{ totalChips }}</view>
          </view>
        </view>
      </view>

      <view class="footer-text">预祝参赛运动员</view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'

// ====================== 级别配置数组【type:game游戏类型 / break休息类型】 ======================
const blindLevelList = ref([
  { type: 'game', blind: "200/400", ante: "400", duration: 10 },
  { type: 'game', blind: "400/800", ante: "800", duration: 10 },
  { type: 'break', blind: "", ante: "", duration: 8 }, //休息
  { type: 'game', blind: "500/1000", ante: "1000", duration: 12 },
  { type: 'game', blind: "800/1600", ante: "1600", duration: 12 },
  { type: 'break', blind: "", ante: "", duration: 8 }, //休息
  { type: 'game', blind: "1000/2000", ante: "2000", duration: 15 }
])

const playerCount = ref(27)
const totalPlayer = ref(51)
const showSignBtn = ref(true)

//数组下标（包含休息，用于程序流转）
const currentIndex = ref(0)
//新增：比赛结束标记
const gameOver = ref(false)
//是否存在后续休息节点
const hasNextBreak = ref(false)

//中间
const nowTime = ref('')
const timerSecond = ref(0)
const blindCur = ref('')
const anteCur = ref('')
const blindNext = ref('')

//下次休息倒计时秒数
const breakSecond = ref(0)
const avgChips = ref(61111)
const totalChips = ref(1650000)

//定时器
let clockTimer = null
let timer = null

//全屏状态
const isFullScreen = ref(false)
//新增暂停状态
const isPaused = ref(false)

//切换暂停/开始
function togglePause() {
  // 如果是比赛结束状态：点击“开始”清除结束标记，恢复比赛
    if(gameOver.value){
      // gameOver.value = false
      return
    }
    isPaused.value = !isPaused.value
}
/**
 * 计算显示的级别数字：只统计game，break休息不计入级别
 */
const displayLevel = computed(() => {
  let count = 0
  for (let i = 0; i <= currentIndex.value; i++) {
    if (blindLevelList.value[i].type === 'game') {
      count++
    }
  }
  return count
})

/**
 * 查找下一个休息节点索引，从startIndex往后找
 */
function findNextBreakIndex(startIndex) {
  const list = blindLevelList.value
  for (let i = startIndex; i < list.length; i++) {
    if (list[i].type === 'break') {
      return i
    }
  }
  return -1
}

/**
 * 查找往后第一个game节点，跳过break
 */
function findNextGameIndex(startIndex) {
  const list = blindLevelList.value
  for (let i = startIndex; i < list.length; i++) {
    if (list[i].type === 'game') {
      return i
    }
  }
  return -1
}

//初始化盲注数据
function initBlindData() {
  const list = blindLevelList.value
  if (list.length === 0) return
  gameOver.value = false
  const item = list[currentIndex.value]

  //游戏类型赋值盲注，休息基础分改为横杠
  if (item.type === 'game') {
    blindCur.value = item.blind
    anteCur.value = item.ante
  } else {
    blindCur.value = '—'
    anteCur.value = '—'
  }
  timerSecond.value = item.duration

  //【修复】下级别：跳过所有break，直接取后面第一个game
  const nextGameIdx = findNextGameIndex(currentIndex.value + 1)
  if (nextGameIdx > -1) {
    blindNext.value = list[nextGameIdx].blind
  } else {
    blindNext.value = '—'
  }

  // ==========【修复下次休息时间逻辑】只累加game，忽略break的时长 ==========
  let searchStart = currentIndex.value
  // 如果当前本身就是break，从下一位开始搜索下一次休息
  if (list[currentIndex.value].type === 'break') {
    searchStart = currentIndex.value + 1
  }
  const breakIdx = findNextBreakIndex(searchStart)
  if (breakIdx > -1) {
    hasNextBreak.value = true
    let sum = 0
    //只累加game类型，break跳过不计入倒计时
    for (let i = searchStart; i <= breakIdx; i++) {
      if(list[i].type === 'game'){
        sum += list[i].duration
      }
    }
    breakSecond.value = sum
  } else {
    hasNextBreak.value = false
    breakSecond.value = 0
  }
}

//级别切换，倒计时结束触发
function nextLevel() {
  const list = blindLevelList.value
  if (currentIndex.value >= list.length - 1) {
    timerSecond.value = 0
    blindNext.value = "—"
    gameOver.value = true
    return
  }
  currentIndex.value++
  initBlindData()
}

/**
 * 秒格式化 00:00:00
 */
const formatTime = (seconds) => {
  if (seconds <= 0) return '00:00:00'
  const h = Math.floor(seconds / 3600)
  const m = Math.floor((seconds % 3600) / 60)
  const s = seconds % 60
  const pad = n => String(n).padStart(2, '0')
  return `${pad(h)}:${pad(m)}:${pad(s)}`
}

//更新北京时间
const updateClock = () => {
  const date = new Date()
  const h = String(date.getHours()).padStart(2, '0')
  const m = String(date.getMinutes()).padStart(2, '0')
  const s = String(date.getSeconds()).padStart(2, '0')
  nowTime.value = `${h}:${m}:${s}`
}

//倒计时每秒递减
const tick = () => {
  //暂停直接返回，不处理倒计时
  if(isPaused.value) return

  const list = blindLevelList.value
  if (timerSecond.value > 0) {
    timerSecond.value--
    if (timerSecond.value === 0) {
      nextLevel()
    }
  }
  // 重点：当前不是break休息状态，才扣下次休息倒计时
  if (hasNextBreak.value && breakSecond.value > 0 && list[currentIndex.value].type !== 'break') {
    breakSecond.value--
  }
}
/**
 * 手动上一级
 */
function prevLevel() {
  if(gameOver.value) return
  if (currentIndex.value <= 0) return
  currentIndex.value--
  initBlindData()
}

/**
 * 手动下一级
 */
function nextManualLevel() {
  if(gameOver.value) return
  const maxIndex = blindLevelList.value.length -1
  if(currentIndex.value >= maxIndex){
    gameOver.value = true
    return
  }
  currentIndex.value++
  initBlindData()
}
onMounted(() => {
  initBlindData()

  // #ifdef APP-PLUS
  const currentWebview = plus.webview.currentWebview()
  plus.navigator.setFullscreen(true)
  currentWebview.setTitleNView({ visible: false })
  isFullScreen.value = true

  plus.globalEvent.addEventListener('fullscreenchange', (e) => {
    isFullScreen.value = e.isFullscreen
    if (!isFullScreen.value) {
      currentWebview.setTitleNView({ visible: true })
    }
  })
  // #endif

  updateClock()
  clockTimer = setInterval(updateClock, 1000)
  timer = setInterval(tick, 1000)
})

onUnmounted(() => {
  clearInterval(timer)
  clearInterval(clockTimer)

  // #ifdef APP-PLUS
  const currentWebview = plus.webview.currentWebview()
  plus.navigator.setFullscreen(false)
  currentWebview.setTitleNView({ visible: true })
  plus.globalEvent.removeEventListener('fullscreenchange')
  // #endif
})
</script>

<style scoped>
:root {
  margin: 0;
  padding: 0;
}
page {
  margin: 0 !important;
  padding: 0 !important;
}
.time-text.gameOver {
	color: #ff2c2c !important;
	text-shadow: 0 0 24px #ff2c2c;
}
.tournament-page {
  width: 100vw;
  height: 100vh;
  position: relative;
  background: #000000;
  color: #ffffff;
  overflow: hidden;
}
.bg-layer{
  position:absolute;
  left:0;
  top:0;
  width:100%;
  height:100%;
  background:#000000;
  opacity:1;
  z-index:1;
}

.content-wrap{
  position:relative;
  z-index:2;
  width:100%;
  height:100vh;
  box-sizing: border-box;
  display:flex;
  flex-direction:column;
  padding: 2vh 2vw;
}

.main-container{
  display:flex;
  flex:1;
  align-items: flex-start;
  overflow: hidden;
}
.col-left, .col-right{
  display:flex;
  flex-direction:column;
  justify-content:flex-start;
}
.col-left{
  text-align:right;
  padding-right:1vw;
}
.col-right{
  text-align:left;
  padding-left:1vw;
}
.col-center{
  flex:1;
  display:flex;
  flex-direction:column;
  text-align:center;
  justify-content:flex-start;
}
.logo-wrap {
  width:100%;
  margin-bottom:3vh;
}
.logo-img {
  width:100%;
  max-height:14vw;
}
.divider-vertical {
  width: 2rpx;
  background-color: rgba(255,255,255,0.35);
  height: 100%;
  flex-shrink: 0;
  margin:0 2vw;
}
.pause-btn-wrap{
  margin-bottom:3vh;
}
.pause-btn{
  display:inline-block;
  padding:1.2vh 2vw;
  background:#2185d0;
  color:#fff;
  border-radius:8rpx;
  font-weight:bold;
  cursor:pointer;
}
.pause-btn.pauseActive{
  background:#27ae60;
}
/*手动级别按钮组*/
.level-btn-wrap{
  display:flex;
  gap:2vw;
  justify-content:flex-end;
  margin-bottom:3vh;
}
.level-btn{
  padding:1.2vh 2vw;
  border-radius:8rpx;
  font-weight:bold;
  cursor:pointer;
  color:#fff;
  display:inline-block;
}
.btn-prev{
  background:#9c27b0;
}
.btn-next{
  background:#f57c00;
}
/* ========== 大屏横屏 >=768px ========== */
@media screen and (min-width:768px) {
  .col-left{
    width:18%;
    flex-shrink:0;
  }
  .col-right{
    width:18%;
    flex-shrink:0;
  }

  .panel-item{
    margin:2vh 0;
  }
  .col-item{
    margin:2vh 0;
  }

  .header-row{
    margin-bottom:2vh;
  }
  .tournament-title{
    font-size:5vw;
    font-weight:bold;
  }
  .current-time{
    font-size:1.6vw;
    margin-top:0.6vh;
    color:#eeeeee;
  }

  .center-timer{
    margin:2vh 0;
  }
  .time-text{
    font-size:clamp(80px,12vw,160px);
    font-weight:bold;
    letter-spacing:0.2vw;
    color:#ffffff;
  }
  /* 休息倒计时样式：变小、橙色 */
  .time-text.breakTimer{
    font-size:clamp(40px,6vw,80px);
    color:#ff9500;
  }
   .time-text.gameOver {
       font-size: clamp(100px,14vw,180px) !important;
     }
  .bottom-blind-wrap{
    display:flex;
    justify-content:center;
    gap:6vw;
    margin-top:3vh;
  }
  .blind-left-col{
    text-align:left;
  }
  .blind-right-val{
    text-align:right;
  }
  .blind-item{
    margin-bottom:2vh;
  }
  .val-item{
    margin-bottom:2vh;
    font-size:clamp(28px,4vw,56px);
    font-weight:bold;
  }

  .label{
    font-size:clamp(18px,2.2vw,32px);
    font-weight:bold;
  }
  .label-en{
    font-size:clamp(12px,1.4vw,20px);
    color:#cccccc;
    margin-top:0.3vh;
  }
  .val-big{
    font-size:clamp(28px,4vw,56px);
    font-weight:bold;
    margin-top:0.4vh;
  }

  .footer-text{
    text-align:right;
    font-size:clamp(12px,1.4vw,20px);
    padding-top:1vh;
  }
  .pause-btn{
      font-size:clamp(18px,2vw,26px);
    }
	.level-btn{
	    font-size:clamp(18px,2vw,26px);
	  }
}

/* ========== 手机竖屏 <767px ========== */
@media screen and (max-width:767px) {
  .col-left{
    width:24%;
    flex-shrink:0;
  }
  .col-right{
    width:24%;
    flex-shrink:0;
  }

  .panel-item{
    margin:1.4vh 0;
  }
  .col-item{
    margin:1.4vh 0;
  }

  .header-row{
    margin-bottom:1.4vh;
  }
  .tournament-title{
    font-size:clamp(22px,5vw,32px);
    font-weight:bold;
  }
  .current-time{
    font-size:clamp(11px,2.6vw,15px);
    margin-top:0.4vh;
    color:#eeeeee;
  }

  .center-timer{
    margin:1.4vh 0;
  }
  .time-text{
    font-size:clamp(32px,10vw,60px);
    font-weight:bold;
    letter-spacing:0.2vw;
    color:#ffffff;
  }
  .time-text.breakTimer{
    font-size:clamp(20px,5vw,36px);
    color:#ff9500;
  }
  .time-text.gameOver {
    font-size: clamp(40px,12vw,72px) !important;
  }
  .bottom-blind-wrap{
    display:flex;
    justify-content:center;
    gap:4vw;
    margin-top:1.4vh;
  }
  .blind-left-col{
    text-align:left;
  }
  .blind-right-val{
    text-align:right;
  }
  .blind-item{
    margin-bottom:1.4vh;
  }
  .val-item{
    margin-bottom:1.4vh;
    font-size:clamp(18px,4.2vw,26px);
    font-weight:bold;
  }

  .label{
    font-size:clamp(14px,3.4vw,20px);
    font-weight:bold;
  }
  .label-en{
    font-size:clamp(10px,2.2vw,13px);
    color:#cccccc;
    margin-top:0.2vh;
  }
  .val-big{
    font-size:clamp(18px,4.2vw,26px);
    font-weight:bold;
    margin-top:0.4vh;
  }

  .footer-text{
    text-align:right;
    font-size:clamp(10px,2.2vw,13px);
    padding-top:0.6vh;
  }
  .pause-btn{
      font-size:clamp(14px,3vw,18px);
    }
	.level-btn{
	    font-size:clamp(14px,3vw,18px);
	  }
}
</style>
