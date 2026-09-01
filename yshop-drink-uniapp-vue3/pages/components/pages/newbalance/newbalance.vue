<template>
<view class="page">
  <!-- 自定义导航 -->
	<uv-navbar
		:fixed="true"
	  	bgColor="#ffffff"
	  	title="余额"
	  	left-arrow
	  	:placeholder="true"
	  @leftClick="$onClickLeft"/>
  <view class="balance-top">
    <view class="balance-label">可用余额</view>
    <view class="balance-num">¥{{balance}}</view>
    <view class="btn-recharge" @click="goRecharge">立即充值</view>
  </view>

  <view class="divider"></view>

  <view class="menu-list">
    <view class="menu-item" @click="uni.navigateTo({url:'/pages/components/pages/payQrcode/payQrcode'})">
      <view class="menu-left">扫码支付</view>
      <view class="menu-right">
        <text class="tip">可使用钱包直接支付</text>
        <uv-icon name="arrow-right" size="30"></uv-icon>
      </view>
    </view>
    <view class="menu-item" @click="uni.navigateTo({url:'/pages/components/pages/balanceLog/balanceLog'})">
      <view class="menu-left">消费记录</view>
      <view class="menu-right">
        <uv-icon name="arrow-right" size="30"></uv-icon>
      </view>
    </view>
    <view class="menu-item" @click="uni.navigateTo({url:'/pages/components/pages/storageRule/storageRule'})">
      <view class="menu-left">储值说明</view>
      <view class="menu-right">
        <uv-icon name="arrow-right" size="30"></uv-icon>
      </view>
    </view>
    <view class="menu-item" @click="uni.navigateTo({url:'/pages/components/pages/exchangeCode/exchangeCode'})">
      <view class="menu-left">兑换储值</view>
      <view class="menu-right">
        <uv-icon name="arrow-right" size="30"></uv-icon>
      </view>
    </view>
  </view>
</view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { balanceGetMoneyList } from '@/api/user'
import { useMainStore } from '@/store/store'
import { storeToRefs } from 'pinia'


const main = useMainStore()
const { member,loginValueFlag } = storeToRefs(main)

const balance = ref(0)

// 校验登录
function checkLogin() {
  if (!loginValueFlag.value) {
    uni.navigateTo({url:'/pages/components/pages/login/login'})
    return false
  }
  return true
}

const getInfo = async () => {
  // if(!checkLogin()) return
  // const res = await balanceGetMoneyList()
  // if(res.code === 200){
  //   balance.value = res.data.balance
  // }
}

// 跳转充值页面（你后续自行开发充值页）
const goRecharge = () => {
	
	uni.navigateTo({url:'/pages/components/pages/recharge/recharge'})
  // uni.showToast({title:'充值页面待开发',icon:'none'})
}

onMounted(()=>{
  getInfo()
})
</script>

<style scoped>
page {background:#fff;}
.page {min-height:100vh;}
.nav-bar {
  display:flex;
  align-items:center;
  justify-content:space-between;
  padding:30rpx;
}
.nav-title {font-size:38rpx;font-weight:500;}
.balance-top {
  text-align:center;
  padding:80rpx 40rpx;
}
.balance-label {font-size:40rpx;color:#333;}
.balance-num {
  font-size:80rpx;
  color:#e494af;
  font-weight:500;
  margin-top:20rpx;
}
.btn-recharge {
  width:600rpx;
  line-height:90rpx;
  background:#e494af;
  color:#fff;
  border-radius:6rpx;
  font-size:38rpx;
  margin:60rpx auto 0;
}
.divider {height:20rpx;background:#f5f5f5;}
.menu-list {padding:0 30rpx;}
.menu-item {
  display:flex;
  justify-content:space-between;
  align-items:center;
  padding:40rpx 0;
  border-bottom:1rpx solid #eee;
}
.menu-left {font-size:38rpx;}
.menu-right {display:flex;align-items:center;gap:16rpx;}
.tip {font-size:32rpx;color:#aaa;}
</style>