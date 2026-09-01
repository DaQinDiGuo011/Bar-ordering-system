<template>
	<uv-navbar
	  :fixed="true"
	  bgColor="#ffffff"
	  title=" "
	  left-arrow
	  :placeholder="true"
	  @leftClick="$onClickLeft"/>
<view class="page">
  <!-- 粉色头部 -->
  <view class="header">
    <!-- <uv-icon name="arrow-left" size="36" @click="uni.navigateBack()"></uv-icon> -->
  </view>
  
  <view class="user-info">
    <image class="avatar" :src="loginValueFlag ? member.avatar ? member.avatar : '/static/images/mine/default.png' : '/static/images/mine/default.png'"></image>
    <view class="username">{{ loginValueFlag ? member.nickname : '游客' }}</view>
  </view>

  <view class="pay-radio">
    <uv-radio v-model="useBalance" label="1" shape="circle">使用余额支付 (可用余额¥{{balance}})</uv-radio>
  </view>
  <view class="line"></view>

  <view class="qrcode-wrap">
    <view class="desc">此二维码可累计积分并付款</view>
    <!-- 实际项目使用qrcode组件生成会员码 -->
    <view class="qrcode-box">
      <image src="/static/demo_qr.png" style="width:480rpx;height:480rpx;"></image>
    </view>
    <view class="refresh-tip">会员码每30秒自动刷新一次</view>
  </view>
</view>
</template>

<script setup>
import {ref,onMounted} from 'vue'
import { balanceGetMoneyList } from '@/api/user'
import { useMainStore } from '@/store/store'
import { storeToRefs } from 'pinia'


const main = useMainStore()
const { member,loginValueFlag } = storeToRefs(main)

const userName = ref('妖空月')
const balance = ref(0)
const useBalance = ref('')

function checkLogin() {
  if (!loginValueFlag.value) {
      uni.navigateTo({url:'/pages/components/pages/login/login'})
      return false
    }
  return true
}

const loadData = async ()=>{
  if(!checkLogin()) return
  const res = await balanceGetMoneyList()
  if(res.code===200){
    balance.value = res.data.balance
  }
}

onMounted(()=>{
  loadData()
})
</script>

<style scoped>
page{background:#fff;}
.header {
  height:160rpx;
  background:#e494af;
  padding:30rpx;
}
.user-info {
  text-align:center;
  margin-top:-80rpx;
}
.avatar {
  width:160rpx;
  height:160rpx;
  border-radius:50%;
  border:6rpx solid #fff;
}
.username {
  font-size:40rpx;
  margin-top:20rpx;
}
.pay-radio {
  padding:60rpx 40rpx 30rpx;
}
.line {margin:0 40rpx;height:1rpx;background:#eee;}
.qrcode-wrap {
  margin-top:60rpx;
  text-align:center;
}
.desc {font-size:38rpx;margin-bottom:40rpx;}
.qrcode-box {
  width:480rpx;
  height:480rpx;
  margin:0 auto;
}
.refresh-tip {
  margin-top:40rpx;
  font-size:32rpx;color:#999;
}
</style>