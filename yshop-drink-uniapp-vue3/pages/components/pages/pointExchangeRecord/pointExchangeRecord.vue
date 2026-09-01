<template>
<view class="page">
	<uv-navbar
	  :fixed="true"
	  bgColor="#ffffff"
	  title="兑换记录"
	  left-arrow
	  :placeholder="true"
	  @leftClick="$onClickLeft"/>

  <view class="tab-header">
    <view class="tab-item" :class="{active:tab===0}" @click="tab=0">未完成</view>
    <view class="tab-item" :class="{active:tab===1}" @click="tab=1">已完成</view>
  </view>

  <view v-if="list.length>0">
    <view class="item" v-for="item in list" :key="item.id">
      <view class="name">{{item.goodsName}}</view>
      <view class="info">
        <text>{{item.orderNo}}</text>
        <text>-{{item.usePoint}}积分</text>
      </view>
    </view>
  </view>
  <view v-else class="empty">
    <uv-empty mode="list" text="暂无相关内容~"></uv-empty>
  </view>
</view>
</template>

<script setup>
import {ref,watch,onMounted} from 'vue'

import { getPointExchangeList } from '@/api/user.js'

const tab = ref(0)
const list = ref([])

const loadList = async ()=>{
  const status = tab.value===0 ? 0 :1
  const res = await getPointExchangeList({"status": status})
  if(res) list.value = res
}
watch(tab,loadList)
onMounted(loadList)
</script>

<style scoped>
.page{background:#fff;min-height:100vh}
.nav-bar{display:flex;align-items:center;justify-content:space-between;padding:20rpx 30rpx}
.nav-title{font-size:34rpx;font-weight:500}
.tab-header{display:flex}
.tab-item{flex:1;text-align:center;padding:30rpx 0;font-size:36rpx;position:relative}
.tab-item.active::after{
  content:"";position:absolute;bottom:0;left:20%;width:60%;height:4rpx;background:#e64398
}
.item{padding:30rpx;border-bottom:1rpx #f4f4f4}
.name{font-size:32rpx}
.info{display:flex;justify-content:space-between;margin-top:12rpx;font-size:26rpx;color:#999}
.empty{margin-top:150rpx}
</style>