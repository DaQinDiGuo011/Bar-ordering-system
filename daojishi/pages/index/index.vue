<template>
  <view class="container">
    <!-- 头部 -->
    <view class="header">
      <view class="logo">THE ◎ ONE</view>
      <view class="placeholder-right"></view>
    </view>

    <!-- 主视觉纸牌大图区域 -->
    <view class="banner-wrap">
      <image
        class="banner-img"
        src="/static/index_bg.jpg"
        mode="aspectFill"
      />
      <view class="banner-mask"></view>
      <view class="banner-text">纸牌积分计时器</view>
    </view>

    <!-- 积分信息卡片 -->
    <view class="info-card">
      <view class="info-row">
        <view class="info-item">
          <view class="info-label">当前日期</view>
          <view class="info-val">{{ currentDate }}</view>
        </view>
        <view class="info-item">
          <view class="info-label">当前时间</view>
          <view class="info-val">{{ currentTime }}</view>
        </view>
      </view>
    </view>

    <!-- 分割线 -->
    <view class="divider-bar"></view>

    <!-- 【整合容器：选项卡+按钮全部放在这个框里面】 -->
    <view class="operate-box">
      <view class="tab-title">请选择游戏区域</view>
      <view class="tab-row">
        <view
          class="tab-item"
          :class="{ active: selectZone === 'A' }"
          @click="selectZone = 'A'"
        >A区</view>
        <view
          class="tab-item"
          :class="{ active: selectZone === 'B' }"
          @click="selectZone = 'B'"
        >B区</view>
        <view
          class="tab-item"
          :class="{ active: selectZone === 'C' }"
          @click="selectZone = 'C'"
        >C区</view>
      </view>
      <!-- 进入游戏按钮，缩小尺寸 -->
      <view class="btn-wrap">
        <button class="start-btn" @click="goCountDown">进入游戏</button>
      </view>
    </view>

    <!-- 底部快捷统计区域 -->
    <view class="bottom-stat">
      <view class="stat-item">
        <text class="stat-title">盲注倒计时</text>
        <text class="stat-desc">等级盲注自动切换</text>
      </view>
      <view class="stat-item">
        <text class="stat-title">积分记录</text>
        <text class="stat-desc">对局数据统计</text>
      </view>
    </view>

  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'

// 选中区域：''未选择 / A / B / C
const selectZone = ref('')
const currentDate = ref('')
const currentTime = ref('')

// 更新本地时间
function updateDateTime() {
  const now = new Date()
  currentDate.value = `${now.getFullYear()}-${String(now.getMonth()+1).padStart(2,'0')}-${String(now.getDate()).padStart(2,'0')}`
  currentTime.value = `${String(now.getHours()).padStart(2,'0')}:${String(now.getMinutes()).padStart(2,'0')}:${String(now.getSeconds()).padStart(2,'0')}`
}

onMounted(()=>{
  updateDateTime()
  setInterval(updateDateTime,1000)
})

const goCountDown = ()=>{
  // 校验：必须先选A/B/C
  if(!selectZone.value){
    uni.showToast({
      title:'请先选择游戏区域',
      icon:'none'
    })
    return
  }
  // 将选中的区域通过url参数传给倒计时页面
  uni.navigateTo({
    // url:`/pages/countdown/countdown?zone=${selectZone.value}`
	url:`/pages/game/game?zone=${selectZone.value}`
  })
}
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
  padding: 30rpx;
  box-sizing: border-box;
}

/* 顶部头部 */
.header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom:40rpx;
}
.logo {
  font-size: 45rpx;
  font-weight: bold;
  color:#d4af37;
}
.placeholder-right {
  width: 120rpx;
}

/* banner纸牌大图 */
.banner-wrap{
  width:100%;
  height:360rpx;
  border-radius:20rpx;
  overflow:hidden;
  position:relative;
  margin-bottom:40rpx;
}
.banner-img{
  width:100%;
  height:100%;
}
.banner-mask{
  position:absolute;
  left:0;
  top:0;
  width:100%;
  height:100%;
  background:rgba(0,0,0,0.45);
}
.banner-text{
  position:absolute;
  left:0;
  top:0;
  width:100%;
  height:100%;
  display:flex;
  align-items:center;
  justify-content:center;
  font-size:52rpx;
  font-weight:bold;
  color:#ffffff;
  letter-spacing:4rpx;
}

/* 信息卡片 */
.info-card{
  background:#121212;
  border-radius:16rpx;
  padding:40rpx 20rpx;
}
.info-row{
  display:flex;
  justify-content: space-around;
}
.info-item{
  text-align:center;
}
.info-label{
  font-size:28rpx;
  color:#aaa;
  margin-bottom:12rpx;
}
.info-val{
  font-size:44rpx;
  font-weight:bold;
  color:#d4af37;
}

.divider-bar {
  height: 2rpx;
  background-color: #2a2a2a;
  margin:40rpx 0;
}

/* ========== 整合框：选项卡+按钮全部放在这里 ========== */
.operate-box{
  background:#121212;
  border-radius:16rpx;
  padding:32rpx;
  margin-bottom:40rpx;
}
.tab-title{
  font-size:28rpx;
  color:#cccccc;
  margin-bottom:20rpx;
}
.tab-row{
  display:flex;
  gap:16rpx;
  margin-bottom:28rpx;
}
.tab-item{
  flex:1;
  height:72rpx;
  line-height:72rpx;
  text-align:center;
  background:#1a1a1a;
  border:2rpx solid #333;
  border-radius:10rpx;
  font-size:30rpx;
}
.tab-item.active{
  border-color:#d4af37;
  background:#2b2210;
  color:#d4af37;
}

.btn-wrap{
  width:100%;
}
.start-btn{
  width:100%;
  height:88rpx;
  line-height:88rpx;
  background:linear-gradient(90deg,#946b2d,#d4af37);
  color:#000;
  font-size:34rpx;
  font-weight:bold;
  border:none;
  border-radius:12rpx;
}
.start-btn::after{
  border:none;
}

/* 底部双统计 */
.bottom-stat{
  display:flex;
  gap:20rpx;
  margin-top:40rpx;
}
.stat-item{
  flex:1;
  background:#121212;
  border-radius:16rpx;
  padding:36rpx 20rpx;
}
.stat-title{
  display:block;
  font-size:34rpx;
  font-weight:bold;
  margin-bottom:10rpx;
}
.stat-desc{
  font-size:26rpx;
  color:#999;
}
</style>
