<template>
  <view class="page">
	<uv-navbar
		:fixed="true"
	  	bgColor="#ffffff"
	  	title="我的积分"
	  	left-arrow
	  	:placeholder="true"
	  @leftClick="uni.switchTab({url:'/pages/mine/mine'})"/>

    <view class="top-box">
      <view class="lab">当前积分</view>
      <view class="num">{{point}}</view>
    </view>

    <view class="menu-row">
      <view class="menu-item" @click="uni.navigateTo({url:'/pages/components/pages/pointShop/pointShop'})">
        <uv-icon name="gift-fill" size="32"></uv-icon>
        <text>积分商城</text>
      </view>
      <view class="menu-item" @click="uni.navigateTo({url:'/pages/components/pages/pointRule/pointRule'})">
        <uv-icon name="file-text" size="32"></uv-icon>
        <text>积分规则</text>
      </view>
    </view>

    <view class="log-title">积分记录</view>

    <view v-if="logList.length>0">
      <view class="log-item" v-for="item in logList" :key="item.id">
        <view>
          <view>{{item.remark}}</view>
          <view class="time">{{item.created_at}}</view>
        </view>
        <view :class="item.type===1?'inc':'dec'">
          {{item.type===1?'+':'-'}}{{item.point}}
        </view>
      </view>
    </view>
    <view v-else class="empty">
      <uv-empty mode="list" text="暂无相关内容~"></uv-empty>
    </view>
  </view>
</template>

<script setup>
import {ref,onMounted} from 'vue'
import { useMainStore } from '@/store/store'
import { storeToRefs } from 'pinia'
import { onLoad,onShow} from '@dcloudio/uni-app'
import { userGetUserInfo, getPointLogList } from '@/api/user.js'

const main = useMainStore()
const { member } = storeToRefs(main)
const point = ref(0)
const logList = ref([])

const loadData = async ()=>{
  const token = uni.getStorageSync('token')
  //积分信息
  let res = await userGetUserInfo()
  console.log("__________-res=",res)
  if(res.integral){
    point.value = res.integral
  }else{
	  point.value = 0
  }
  //积分明细
  let dataList = await getPointLogList()
  if(dataList){
    logList.value = dataList
  }
}
onMounted(loadData)
</script>

<style scoped>
.page{background:#fff;min-height:100vh}
.nav-bar{display:flex;align-items:center;justify-content:space-between;padding:20rpx 30rpx}
.nav-title{font-size:34rpx;font-weight:500}
.top-box{text-align:center;padding:60rpx 20rpx}
.lab{font-size:36rpx;color:#333}
.num{font-size:80rpx;color:#e6a23c;margin-top:20rpx}
.menu-row{display:flex;border-top:1rpx #eee solid;border-bottom:1rpx #eee solid}
.menu-item{flex:1;text-align:center;padding:30rpx 0;display:flex;flex-direction:column;align-items:center}
.log-title{font-size:36rpx;padding:40rpx 30rpx 20rpx;font-weight:bold}
.log-item{display:flex;justify-content:space-between;padding:25rpx 30rpx;border-bottom:1rpx #f4f4f4}
.time{font-size:24rpx;color:#999;margin-top:8rpx}
.inc{color:#f56c6c}
.dec{color:#666}
.empty{margin-top:150rpx}
</style>