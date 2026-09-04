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
                  <view class="val-big">{{ currentIndex + 1 }}</view>
                </view>
                <view class="panel-item">
                  <view class="label">参赛人数</view>
                  <view class="label-en">Entrants</view>
                  <view class="val-big">{{ playerCount }}/{{ totalPlayer }}</view>
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
                  <view class="time-text">{{ gameOver ? '结束' : formatTime(timerSecond) }}</view>
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
                  <view class="val-big">{{ formatTime(breakSecond) }}</view>
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

import { ref, onMounted, onUnmounted } from 'vue'

// ====================== 级别配置数组【在这里配置所有盲注级别】 ======================
// blind:基础分, ante:预置分, duration:本级别倒计时(秒)
const blindLevelList = ref([
  { blind: "200/400", ante: "400", duration: 10 },
  { blind: "400/800", ante: "800", duration: 10  },
  { blind: "500/1000", ante: "1000", duration: 12  },
  { blind: "800/1600", ante: "1600", duration: 12 },
  { blind: "1000/2000", ante: "2000", duration: 15}
])

const playerCount = ref(27)
const totalPlayer = ref(51)
const showSignBtn = ref(true)

//级别索引
const currentIndex = ref(0)
//新增：比赛结束标记
const gameOver = ref(false)

//中间
const nowTime = ref('')
const timerSecond = ref(0)
const blindCur = ref('')
const anteCur = ref('')
const blindNext = ref('')

//右侧
const breakSecond = ref(1*3600 + 10*60 +18) //下次休息 1时10分18秒
const avgChips = ref(61111)
const totalChips = ref(1650000)

//定时器
let clockTimer = null
let timer = null

//全屏状态
const isFullScreen = ref(false)

//初始化盲注数据
function initBlindData(){
  const list = blindLevelList.value
  if(list.length ===0) return
  gameOver.value = false
  const item = list[currentIndex.value]
  blindCur.value = item.blind
  anteCur.value = item.ante
  timerSecond.value = item.duration
  //设置下一级
  if(currentIndex.value +1 < list.length){
    blindNext.value = list[currentIndex.value+1].blind
  }else{
    blindNext.value = "-"
  }
}

//级别切换，倒计时结束触发
function nextLevel(){
  const list = blindLevelList.value
  // 先判断：已经是最后一条，直接停止，标记比赛结束
  if(currentIndex.value >= list.length -1){
    timerSecond.value = 0
    blindNext.value = "-"
    gameOver.value = true
    return
  }

  // 可以切换，索引+1
  currentIndex.value++

  //把下级别赋值给当前
  const currItem = list[currentIndex.value]
  blindCur.value = currItem.blind
  anteCur.value = currItem.ante
  timerSecond.value = currItem.duration

  //再设置新的下一级
  if(currentIndex.value +1 < list.length){
    blindNext.value = list[currentIndex.value+1].blind
  }else{
    blindNext.value = "-"
  }
}

/**
 * 秒格式化 00:00:00
 * @param {Number} seconds 总秒数
 */
const formatTime = (seconds) => {
  if(seconds <=0) return '00:00:00'
  const h = Math.floor(seconds / 3600)
  const m = Math.floor((seconds % 3600)/60)
  const s = seconds % 60
  const pad = n=> String(n).padStart(2,'0')
  return `${pad(h)}:${pad(m)}:${pad(s)}`
}

//更新北京时间
const updateClock = ()=>{
  const date = new Date()
  const h = String(date.getHours()).padStart(2,'0')
  const m = String(date.getMinutes()).padStart(2,'0')
  const s = String(date.getSeconds()).padStart(2,'0')
  nowTime.value = `${h}:${m}:${s}`
}

//倒计时每秒递减
const tick = ()=>{
  //主级别倒计时
  if(timerSecond.value >0){
    timerSecond.value--
    //倒计时归零，执行切换级别
    if(timerSecond.value === 0){
      nextLevel()
    }
  }
  //下次休息倒计时
  if(breakSecond.value >0){
    breakSecond.value--
  }
}

onMounted(()=>{
  //初始化盲注
  initBlindData()

  // #ifdef APP-PLUS
  const currentWebview = plus.webview.currentWebview()
  //页面打开自动全屏：隐藏状态栏、隐藏原生导航栏
  plus.navigator.setFullscreen(true)
  currentWebview.setTitleNView({visible:false})
  isFullScreen.value = true

  //监听系统返回/按键退出全屏，同步导航栏状态
  plus.globalEvent.addEventListener('fullscreenchange', (e)=>{
    isFullScreen.value = e.isFullscreen
    if(!isFullScreen.value){
      currentWebview.setTitleNView({visible:true})
    }
  })
  // #endif

  updateClock()
  clockTimer = setInterval(updateClock,1000)
  timer = setInterval(tick,1000)
})

onUnmounted(()=>{
  clearInterval(timer)
  clearInterval(clockTimer)

  // #ifdef APP-PLUS
  const currentWebview = plus.webview.currentWebview()
  //页面销毁强制退出全屏，恢复状态栏、导航栏
  plus.navigator.setFullscreen(false)
  currentWebview.setTitleNView({visible:true})
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

/* 重点修改：删除gap，顶部对齐，overflow防止溢出 */
.main-container{
  display:flex;
  flex:1;
  /* gap:2vw; 删除 */
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
  /* 高度自适应，大屏限制最大高度 */
  max-height:14vw;
}
.divider-vertical {
  width: 2rpx;
  background-color: rgba(255,255,255,0.35);
  height: 100%;
  flex-shrink: 0;
  margin:0 2vw; /* 使用分割线的margin实现左右间隔，替代gap */
}

/* ========== 大屏横屏 >=768px ========== */
@media screen and (min-width:768px) {
  /* 侧边栏收窄，参考效果图 18% */
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
  /* 限制最大字体，防止无限放大挤压右侧 */
  .time-text{
    font-size:clamp(80px,12vw,160px);
    font-weight:bold;
    letter-spacing:0.2vw;
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
}
</style>
