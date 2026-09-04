<template>
  <view class="container">
    <!-- 头部 -->
    <view class="header">
      <view class="logo">THE ◎ ONE</view>
      <view class="top-center">
        <view class="bar-name">THEONEBAR</view>
        <view class="level-label">LEVEL <text>{{ currentLv + 1 }}</text></view>
      </view>
      <view class="placeholder-right"></view>
    </view>

    <!-- 倒计时主体 -->
    <view class="timer-area">
		<template v-if="isGameStart">
			<!-- 游戏进行中：大字体 -->
			<view class="count-text">{{ timeDisplay }}</view>
		</template>
		<template v-else>
			<!-- 开赛倒计时：标题+数字分开 -->
			<view class="count-wrap">
			  <view class="count-title">开赛倒计时</view>
			  <view class="count-num">{{ formatHms(waitRemainSec) }}</view>
			</view>
		</template>
	</view>

    <view class="divider-bar"></view>

    <!-- 操作行 -->
    <view class="operate-row">
      <view class="click-item" @click="togglePause">{{ pauseText }}</view>
      <view class="btn-group">
        <view class="click-item" @click="prevLevel">◀ Prev Level</view>
        <view class="click-item" @click="nextLevel">Next Level ▶</view>
      </view>
    </view>

    <!-- 底部整行：【左侧奖品】｜【盲注面板】｜【右侧统计信息】 -->
    <view class="bottom-wrap">
      <!-- 左侧奖品区域 -->
      <view class="prize-panel">
        <view class="panel-title">Prize</view>
        <view class="prize-content">{{ prizeText }}</view>
      </view>

      <!-- 中间盲注面板 -->
      <view class="blind-wrap">
        <view class="blind-panel">
          <view class="panel-title">Blinds</view>
          <view class="blind-num">{{ currBlind }}</view>
          <view class="ante-line">Ante: <text>{{ currAnte }}</text></view>
        </view>
        <view class="blind-panel">
          <view class="panel-title">Next Blinds</view>
          <view class="blind-num">{{ nextBlind }}</view>
          <view class="ante-line">Ante: <text>{{ nextAnte }}</text></view>
        </view>
      </view>

      <!-- 右侧统计面板：人数 / 总积分 / 每人积分 -->
      <view class="stat-panel">
        <view class="stat-row">
          <text class="stat-label">Players:</text>
          <text class="stat-value">{{ playerCount }}</text>
        </view>
        <view class="stat-row">
          <text class="stat-label">Total Points:</text>
          <text class="stat-value">{{ totalPoints }}</text>
        </view>
        <view class="stat-row">
          <text class="stat-label">Per Person:</text>
          <text class="stat-value">{{ perPersonPoints }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onUnmounted } from 'vue'

// =====================【参数配置区｜自行修改】====================
// blind：盲注值，ante：前注，levelTime：本等级倒计时【秒】
const levelConfig = ref([
  { blind: "50/100", ante: "-", levelTime: 8 * 60 },
  { blind: "80/160", ante: "-", levelTime: 12 * 60 },
  { blind: "160/320", ante: "-", levelTime: 15 * 60 },
  { blind: "320/640", ante: "20", levelTime: 18 * 60 },
  { blind: "600/1200", ante: "30", levelTime: 20 * 60 }
])

// 新增业务数据
const prizeText = ref("冠军奖品：手表") //奖品内容
const playerCount = ref(28) //总人数
const totalPoints = ref(28000) //总积分
const perPersonPoints = ref(1000) //每人积分
// =================================================================

const currentLv = ref(0)
const totalSeconds = ref(0)
let timerInterval = ref(null)
let waitTimeInterval = ref(null)

const isPause = ref(false)
const isGameStart = ref(false)
const waitRemainSec = ref(0)

const timeDisplay = ref("00:00")
const currBlind = ref("0/0")
const currAnte = ref("-")
const nextBlind = ref("0/0")
const nextAnte = ref("-")
const pauseText = ref("▶ Start timer")

// 游戏内等级倒计时：分:秒 mm:ss
const formatTime = (s) => {
  const m = Math.floor(s / 60)
  const sec = s % 60
  return String(m).padStart(2, "0") + ":" + String(sec).padStart(2, "0")
}

// 开赛倒计时：时:分:秒 HH:mm:ss
const formatHms = (totalSec) => {
  const h = Math.floor(totalSec / 3600)
  const m = Math.floor((totalSec % 3600) / 60)
  const s = totalSec % 60
  return [h, m, s].map(n => String(n).padStart(2, "0")).join(":")
}

const updateTime = () => {
	if(isGameStart.value){
      timeDisplay.value = formatTime(totalSeconds.value)
    }else{
      timeDisplay.value = "开赛倒计时：" + formatHms(waitRemainSec.value)
    }
}

// 加载当前等级数据
const loadLevel = () => {
  const data = levelConfig.value[currentLv.value]
  totalSeconds.value = data.levelTime
  currBlind.value = data.blind
  currAnte.value = data.ante

  if (currentLv.value + 1 < levelConfig.value.length) {
    const nextData = levelConfig.value[currentLv.value + 1]
    nextBlind.value = nextData.blind
    nextAnte.value = nextData.ante
  } else {
    nextBlind.value = "FINAL"
    nextAnte.value = "-"
  }
  updateTime()
  startTimer()
}

// 启动计时器
const startTimer = () => {
  clearInterval(timerInterval.value)
  timerInterval.value = setInterval(() => {
    if (isPause.value) return
    totalSeconds.value--
    updateTime()
    if (totalSeconds.value <= 0) {
      levelEnd()
    }
  }, 1000)
}

// 等级结束自动切下一级
const levelEnd = () => {
  clearInterval(timerInterval.value)
  currentLv.value++
  if (currentLv.value >= levelConfig.value.length) {
    timeDisplay.value = "FINISHED"
    return
  }
  loadLevel()
}

// ==========【核心修改】togglePause逻辑 ==========
const togglePause = () => {
  // 未开赛：点击直接手动开赛
  if (!isGameStart.value) {
    clearInterval(waitTimeInterval.value)
    isGameStart.value = true
    isPause.value = false
    pauseText.value = "⏸ Pause timer"
    loadLevel()
    return
  }
  // 已开赛：正常暂停/继续
  isPause.value = !isPause.value
  pauseText.value = isPause.value ? "▶ Continue timer" : "⏸ Pause timer"
}

// 上一级
const prevLevel = () => {
  if (currentLv.value > 0) {
    currentLv.value--
    loadLevel()
  }
}

// 下一级
const nextLevel = () => {
  if (currentLv.value < levelConfig.value.length - 1) {
    currentLv.value++
    loadLevel()
  }
}

// 计算距离目标时间还剩多少秒
const calcWaitSeconds = () => {
  const now = new Date()
  const target = new Date()
  target.setHours(17,0,0,0)
  let diff = Math.floor((target.getTime() - now.getTime()) / 1000)
  if(diff < 0){
	  target.setDate(target.getDate() + 1)
	  diff = Math.floor((target.getTime() - now.getTime()) / 1000)
  }
  return diff
}

const isTargetTimeReached = () => {
  const now = new Date()
  const hour = now.getHours()
  const minute = now.getMinutes()
  return hour > 17 || (hour === 17 && minute >= 0)
}

const waitUntilTargetTime = () => {
  if(isTargetTimeReached()){
	  isGameStart.value = true
    pauseText.value = "⏸ Pause timer"
    loadLevel()
    return
  }
  waitTimeInterval.value = setInterval(()=>{
    if(isTargetTimeReached()){
          clearInterval(waitTimeInterval.value)
          isGameStart.value = true
          pauseText.value = "⏸ Pause timer"
          loadLevel()
        }else{
          waitRemainSec.value = calcWaitSeconds()
          updateTime()
        }
  },1000)
}

waitUntilTargetTime()

onUnmounted(() => {
  clearInterval(timerInterval.value)
  clearInterval(waitTimeInterval.value)
})
</script>


<style scoped>
.container {
  width: 100%;
  min-height: 100vh;
  background-color: #000000;
  color: #ffffff;
  font-family: Arial, Helvetica, sans-serif;
  display: flex;
  flex-direction: column;
  padding: 2%;
}

/* 顶部头部 */
.header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}
.logo {
  font-size: 45rpx;
  font-weight: bold;
}
.top-center {
  text-align: center;
}
.bar-name {
  font-size: 50rpx;
  font-weight: bold;
}
.level-label {
  font-size: 36rpx;
}
.placeholder-right {
  width: 120rpx;
}

/* 中间倒计时 */
.timer-area {
  flex-grow: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}
/* 游戏进行中大字体 */
.count-text {
  font-size: 200rpx;
  font-weight: bold;
}
/* 开赛倒计时 拆分布局 */
.count-wrap{
  display:flex;
  flex-direction:column;
  align-items:center;
}
.count-title{
  font-size:80rpx;
  font-weight:bold;
  margin-bottom:20rpx;
}
.count-num{
  font-size:130rpx;
  font-weight:bold;
}

.divider-bar {
  height: 14rpx;
  background-color: #222222;
  margin: 10rpx 0;
}

/* 操作栏 */
.operate-row {
  display: flex;
  justify-content: space-between;
  font-size: 30rpx;
  color: #ff3830;
  padding: 8rpx 0;
  align-items: center;
}
.click-item {
  cursor: pointer;
}
.btn-group {
  display: flex;
  gap: 30rpx;
}

/* 底部整行布局：奖品｜盲注｜统计 */
.bottom-wrap{
  display:flex;
  background:#222;
  gap:1rpx;
}

/* 左侧奖品面板 */
.prize-panel{
  width:220rpx;
  background:#0a0a0a;
  text-align:center;
  padding:30rpx 10rpx;
}
.prize-content{
  font-size:32rpx;
  margin-top:20rpx;
  line-height:1.5;
}

/* 中间盲注面板 */
.blind-wrap {
  flex:1;
  display: flex;
  gap: 1rpx;
  background-color: #222222;
}
.blind-panel {
  flex: 1;
  background-color: #0a0a0a;
  text-align: center;
  padding: 30rpx 0;
}
.panel-title {
  font-size: 30rpx;
  margin-bottom: 10rpx;
}
.blind-num {
  font-size: 95rpx;
  font-weight: bold;
}
.ante-line {
  font-size: 30rpx;
  margin-top: 12rpx;
}

/* 右侧统计面板 */
.stat-panel{
  width:260rpx;
  background:#0a0a0a;
  padding:30rpx 10rpx;
}
.stat-row{
  display:flex;
  justify-content:space-between;
  margin-bottom:20rpx;
  font-size:30rpx;
}
.stat-label{
  color:#cccccc;
}
.stat-value{
  font-weight:bold;
}
</style>

