<template>
<view class="page">
	<uv-navbar
		:fixed="true"
	  	bgColor="#ffffff"
	  	title="余额明细"
	  	left-arrow
	  	:placeholder="true"
	  @leftClick="$onClickLeft"/>
  <view class="tab-header">
    <view 
      class="tab-item" 
      :class="{active:activeTab===0}"
      @click="activeTab=0"
    >支出</view>
    <view 
      class="tab-item" 
      :class="{active:activeTab===1}"
      @click="activeTab=1"
    >收入</view>
  </view>

  <view class="stat-text">
    {{activeTab===0 ? `我的余额:¥${balance} 累计支出:¥${payTotal}` : `累计充值:¥${rechargeTotal} 累计赠送:¥${giftTotal}`}}
  </view>

  <view class="empty" v-if="list.length===0">
    <view class="empty-text">暂无相关内容~</view>
  </view>
  <scroll-view scroll-y v-else>
    <view v-for="item in list" :key="item.id" class="log-item">
      <view>{{item.remark}}</view>
      <view>{{item.money}}</view>
    </view>
  </scroll-view>
</view>
</template>

<script setup>
import {ref,onMounted,watch} from 'vue'
import { getWalletLogList } from '@/api/wallet'

import { useMainStore } from '@/store/store'
import { storeToRefs } from 'pinia'


const main = useMainStore()
const { member,loginValueFlag } = storeToRefs(main)

const activeTab = ref(0)
const balance = ref(0)
const payTotal = ref(0)
const rechargeTotal = ref(0)
const giftTotal = ref(0)
const list = ref([])

function checkLogin() {
    if (!loginValueFlag.value) {
	  uni.navigateTo({url:'/pages/components/pages/login/login'})
	  return false
	}
    return true
}

const loadLog = async ()=>{
  if(!checkLogin()) return
  const res = await getWalletLogList({type:activeTab.value})
  console.log("--------------res=",res)
  if(res.code===200){
    list.value = res.data.list
    balance.value = res.data.balance
    payTotal.value = res.data.payTotal
    rechargeTotal.value = res.data.rechargeTotal
    giftTotal.value = res.data.giftTotal
  }
}

watch(activeTab,()=>loadLog())
onMounted(()=>loadLog())
</script>

<style scoped>
page {background:#f7f7f7;}
.nav-bar {
  display:flex;
  align-items:center;
  justify-content:space-between;
  padding:30rpx;
  background:#fff;
}
.nav-title {font-size:38rpx;font-weight:500;}
.tab-header {
  display:flex;
  background:#fff;
}
.tab-item {
  flex:1;
  text-align:center;
  font-size:40rpx;
  padding:30rpx 0;
  position:relative;
}
.tab-item.active::after {
  content:"";
  width:120rpx;
  height:6rpx;
  background:#e494af;
  position:absolute;
  bottom:0;
  left:50%;
  transform:translateX(-50%);
}
.stat-text {
  text-align:center;
  font-size:34rpx;
  color:#999;
  padding:30rpx 0;
}
.empty {
  padding-top:200rpx;
  text-align:center;
}
.empty-text {font-size:36rpx;color:#999;}
.log-item {
  display:flex;
  justify-content:space-between;
  padding:30rpx;
  background:#fff;
  margin-bottom:10rpx;
}
</style>