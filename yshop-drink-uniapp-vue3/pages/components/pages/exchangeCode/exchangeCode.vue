<template>
<view class="page">
	<uv-navbar
		:fixed="true"
	  	bgColor="#ffffff"
	  	title="兑换卡券"
	  	left-arrow
	  	:placeholder="true"
	  @leftClick="$onClickLeft"/>
  <view class="input-wrap">
    <uv-input 
      v-model="code" 
      placeholder="请输入兑换码"
      border="none"
    ></uv-input>
    <view class="icon-group">
      <uv-icon name="scan" size="40rpx"></uv-icon>
      <uv-icon name="close-circle" size="40rpx" @click="code=''"></uv-icon>
    </view>
  </view>
  <view class="record-link" @click="uni.navigateTo({url:'/pages/components/pages/couponRecord/couponRecord'})">
    购买记录 >
  </view>

  <view class="bottom-btn">
    <view class="btn-exchange" :class="{disabled:!code}" @click="exchange">立即兑换</view>
  </view>
</view>
</template>

<script setup>
import {ref} from 'vue'
import { useMainStore } from '@/store/store'
import { storeToRefs } from 'pinia'
import { getWalletExchange } from '@/api/wallet'


const main = useMainStore()
const { member,loginValueFlag } = storeToRefs(main)

const code = ref('')

function checkLogin() {
    if (!loginValueFlag.value) {
        uni.navigateTo({url:'/pages/components/pages/login/login'})
        return false
    }
    return true
}

const exchange = async ()=>{
  if(!checkLogin()) return
  if(!code.value){
    uni.showToast({title:'请输入兑换码',icon:'none'})
    return
  }
  const res = await getWalletExchange({code:code.value})
  console.log("--------------res-", res)
  if(res.code==="200"){
    uni.showToast({title:'兑换成功'})
    code.value = ''
  }else{
    uni.showToast({title:res.msg,icon:'none'})
  }
}
</script>

<style scoped>
page {background:#f7f7f7;min-height:100vh;}
.nav-bar {
  display:flex;
  align-items:center;
  justify-content:space-between;
  padding:30rpx;
  background:#fff;
}
.nav-title {font-size:38rpx;font-weight:500;}
.input-wrap {
  display:flex;
  align-items:center;
  background:#fff;
  padding:0 30rpx;
}
.icon-group {display:flex;gap:20rpx;}
.record-link {
  text-align:center;
  padding:30rpx 0;
  font-size:34rpx;color:#999;
}
.bottom-btn {
  position:fixed;
  bottom:60rpx;
  left:40rpx;
  right:40rpx;
}
.btn-exchange {
  width:100%;
  text-align:center;
  line-height:96rpx;
  border-radius:99rpx;
  background:#e494af;
  color:#fff;
  font-size:38rpx;
}
.btn-exchange.disabled {
  background:#aaa;
}
</style>