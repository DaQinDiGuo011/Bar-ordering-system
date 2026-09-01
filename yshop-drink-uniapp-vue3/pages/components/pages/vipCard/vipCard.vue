<template>
  <view class="page">
	  <uv-navbar
		  :fixed="true"
		  bgColor="#ffffff"
		  title="会员卡名称"
		  left-arrow
		  :placeholder="true"
	  	  @leftClick="$onClickLeft"/>
    <!-- 顶部背景 -->
    <view class="header-bg">
      <!-- 横向滑动会员卡 -->
      <scroll-view class="card-scroll" scroll-x>
        <view class="card-wrap">
          <view 
            class="vip-card"
            v-for="(item,index) in vipList"
            :key="index"
            :style="{backgroundColor:item.color}"
          >
            <view class="card-top">
              <text class="card-title">{{item.levelName}}</text>
              <view class="tag">{{item.levelName}}</view>
            </view>
            <view class="growth">
              <text class="num">{{item.needGrowth}}</text>
              <text class="text">成长值</text>
            </view>
          </view>
        </view>
      </scroll-view>
		<!-- <uv-swiper
			:list="vipList"
			:autoplay="false"
			:loop="true"
			:indicator-dots="false"
			previous-margin="60rpx"
			next-margin="60rpx"
			bgColor="#e588a2"
			style="height: 360rpx;"
			@change="onSwiperChange"
		  >
			  
		        <swiper-item v-for="(item,index) in vipList" :key="index" style="border: 1px solid black; height: 100px;"">
					
		          <view class="item-wrap">
		            <view
		              class="vip-card"
		              :style="{backgroundColor:item.color}"
		            >
		              <view class="card-top">
		                <text class="card-title">{{item.levelName}}</text>
		                <view class="tag">{{item.levelName}}</view>
		              </view>
		              <view class="growth">
		                <text class="num">{{item.needGrowth ?? 0}}</text>
		                <text class="text">成长值</text>
		              </view>
		            </view>
		          </view>
		        </swiper-item>
	  </uv-swiper> -->
    </view>

    <view class="content">
      <view class="cell" @click="goRight('rights')">
        <text class="cell-text">会员权益</text>
        <!-- <uv-icon name="arrow-right" size="32"></uv-icon> -->
      </view>
      <view class="cell" @click="goRight('gift')">
        <text class="cell-text">升级礼包</text>
        <!-- <uv-icon name="arrow-right" size="32"></uv-icon> -->
      </view>
    </view>

    <view class="bottom-btn">
      <uv-button 
        block 
        color="#e588a2"
        size="large"
        @click="goOpenCard"
      >立即开卡</uv-button>
    </view>
  </view>
</template>

<script setup>
import {ref,onMounted} from 'vue'
import {getVipInfo} from '@/api/vipinfo.js'

const userGrowth = ref(0)
const vipList = ref([
  {levelName:"VIP1",color:"#cc9f54"},
  {levelName:"VIP2",color:"#4682bf"},
  {levelName:"VIP3",color:"#9854cc"},
])
const currentVipIndex = ref(0)

onMounted(async ()=>{
  await loadVipData()
})

const loadVipData = async ()=>{
  const res = await getVipInfo()
  userGrowth.value = res.growthValue
  // vipList.value = res.vipList
}
// 卡片切换回调
const onSwiperChange = (index) => {
  currentVipIndex.value = index
}
// 跳转开卡页面
const goOpenCard = ()=>{
  uni.navigateTo({url:"/pages/components/pages/vipOpen/vipOpen"})
}
const goRight = (type)=>{
  // uni.showToast({title:"页面开发中",icon:"none"})
}
</script>

<style scoped>
page{
  background:#f5f5f5;
}
.header-bg{
  background:#e588a2;
  padding:60rpx 20rpx 80rpx;
}
.card-scroll{
  width:100%;
}
.card-wrap{
  display:flex;
  gap:20rpx;
  display:flex;
	padding:0 20rpx;
}
.item-wrap{
  height:100%;
  display:flex;
  justify-content:center;
  align-items:center;
}
.vip-card{
  width:620rpx;
  height:360rpx;
  border-radius:24rpx;
  flex-shrink:0;
  padding:40rpx;
  color:#fff;
  position:relative;
}
.card-top{
  display:flex;
  justify-content:space-between;
  align-items:center;
}
.card-title{
  font-size:48rpx;
  font-weight:bold;
}
.tag{
  border:2rpx solid #fff;
  border-radius:100rpx;
  padding:8rpx 24rpx;
  font-size:28rpx;
}
.growth{
  margin-top:60rpx;
  display:flex;
  align-items:baseline;
}
.num{
  font-size:80rpx;
  font-weight:bold;
}
.text{
  font-size:32rpx;
  margin-left:16rpx;
}
.content{
  padding:30rpx;
}
.cell{
  background:#fff;
  border-radius:20rpx;
  display:flex;
  justify-content:space-between;
  align-items:center;
  padding:40rpx 30rpx;
  margin-bottom:24rpx;
}
.cell-text{
  font-size:36rpx;
  font-weight:500;
}
.bottom-btn{
  padding:0 30rpx;
  margin-top:80rpx;
}
</style>