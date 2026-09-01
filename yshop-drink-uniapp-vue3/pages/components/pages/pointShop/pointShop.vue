<template>
<view class="page">
    <uv-navbar
		:fixed="true"
		bgColor="#ffffff"
		title="积分商城"
		left-arrow
		:placeholder="true"
  	  @leftClick="$onClickLeft"/>

	<view class="header-wrap">
        <view class="left-info">
			  <view class="title">可用积分</view>
			  <view class="point-num">{{ member.integral? member.integral: 0}}</view>
			  <view class="link-row">
					<text class="link-text" @click="goPointDetail">积分明细</text>
					<text class="line">|</text>
					<text class="link-text" @click="goExchangeRecord">兑换记录</text>
			  </view>
        </view>
        <view class="empty-img">
          <image src="/static/images/mine/point-shop.jpg" mode="aspectFit" style="height: 127px;"></image>
        </view>
      </view>

   <view class="tab-wrap">
	<view class="tab-item active">全部</view>
  </view>

  <view v-if="goodsList?.length >0" class="goods-wrap">
    <view class="goods-item" v-for="item in goodsList" :key="item.id" @click="exchangeClick(item)">
      <image :src="item.image" mode="aspectFill"></image>
      <view class="g-name">{{item.name}}</view>
      <view class="g-need">{{item.needPoint}}积分</view>
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
import { getPointGoodsList, exchangePoint } from '@/api/user.js'

const main = useMainStore()
const { member } = storeToRefs(main)
const goodsList = ref([])

const load = async ()=>{

  let res = await getPointGoodsList({})
  if(res) goodsList.value = res
}

// 跳转：积分明细
const goPointDetail = () => {
  uni.navigateTo({ url: '/pages/components/pages/myPoint/myPoint' })
}

// 跳转：兑换记录
const goExchangeRecord = () => {
  uni.navigateTo({ url: '/pages/components/pages/pointExchangeRecord/pointExchangeRecord' })
}
const exchangeClick = async (item)=>{
  const confirm = await uni.showModal({title:'确认兑换',content:`消耗${item.needPoint}积分兑换【${item.name}】`})
  if(confirm.confirm){
    const res = await uni.request({
      url:'/point/exchange',
      method:'POST',
      data:{goodsId:item.id}
    })
    if(res.data.code===200){
      uni.showToast({title:'兑换成功'})
      load()
    }else{
      uni.showToast({title:res.data.msg,icon:'none'})
    }
  }
}
onMounted(load)
</script>

<style scoped>
.page{background:#fff;min-height:100vh}
.nav-bar{display:flex;align-items:center;justify-content:space-between;padding:20rpx 30rpx}
.nav-title{font-size:34rpx;font-weight:500}
.header-wrap {
  display: flex;
  padding: 40rpx 30rpx;
  align-items: center;
  justify-content: space-between;
}
.left-info {
  flex-shrink: 0;
}
.title {
  font-size: 32rpx;
  color: #333;
}
.point-num {
  font-size: 80rpx;
  font-weight: bold;
  margin: 16rpx 0;
}
.link-row {
  display: flex;
  align-items: center;
}
.link-text {
  font-size: 30rpx;
  color: #333;
}
.line {
  margin: 0 20rpx;
  color: #333;
}
.empty-img {
  width: 320rpx;
  height: 127px;
}

.tab-wrap {
  padding: 0 30rpx;
  border-bottom: 1rpx solid #eee;
}
.tab-item {
  display: inline-block;
  font-size: 34rpx;
  padding: 20rpx 0;
  position: relative;
}
.tab-item.active {
  color: #e94368;
}
.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: -1rpx;
  left: 0;
  width: 100%;
  height: 4rpx;
  background: #e94368;
  border-radius: 2rpx;
}
.point-top{
	padding:30rpx;
	}
.lab{font-size:32rpx;color:#333}
.tab-row{display:flex;align-items:center;padding:20rpx 30rpx;font-size:34rpx}
.line{margin:0 20rpx}
.tag{font-size:34rpx;color:#e64398;padding:20rpx 30rpx;border-bottom:3rpx #e64398;width:80rpx}
.goods-wrap{display:grid;grid-template-columns:1fr 1fr;padding:20rpx;gap:20rpx}
.goods-item{border:1rpx #eee solid;border-radius:16rpx;overflow:hidden}
.goods-item image{width:100%;height:240rpx}
.g-name{padding:10rpx 16rpx;font-size:28rpx}
.g-need{padding:0 16rpx 16rpx;color:#e64398;font-size:26rpx}
.empty{margin-top:150rpx}
</style>