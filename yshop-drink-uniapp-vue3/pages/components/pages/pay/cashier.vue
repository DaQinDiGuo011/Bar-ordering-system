<template>
	<uv-navbar
		:fixed="true"
		bgColor="#ffffff"
		:title="title"
		left-arrow
		:placeholder="true"
	  @leftClick="$onClickLeft"
	/>
  <view class="cashier-page">
    <!-- 返回箭头 -->
   
    <!-- 顶部绿色头部区域 -->
    <view class="header">
      <view class="price">¥{{ payAmount }}</view>
      <view class="pay-type-text">储值支付</view>
    </view>

    <!-- 支付方式列表 -->
    <view class="pay-list">
      <view 
        class="pay-item"
        :class="{selected: selectPayType === 'wechat'}"
        @click="selectPayType = 'wechat'"
      >
        <uv-icon name="weixin-circle-fill" size="44" color="#07C160"></uv-icon>
        <view class="pay-name">微信支付</view>
        <view class="tip-text">更方便，更快捷</view>
        <view class="check-icon">
          <uv-icon v-if="selectPayType === 'wechat'" name="checkmark-circle-fill" size="36" color="#62A850"></uv-icon>
        </view>
      </view>
    </view>

    <!-- 底部确认支付按钮 -->
    <view class="bottom-fixed">
      <uv-button
        block
        size="large"
        color="#62A850"
        @click="submitPay"
      >确认支付</uv-button>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { balanceGetMoneyList, createPayOrder } from '@/api/user.js'
import { onLoad} from '@dcloudio/uni-app'
import { useMainStore } from '@/store/store'
import { storeToRefs } from 'pinia'
const main = useMainStore()


// 定义变量
const payAmount = ref("0.00")
const packageId = ref("")
const selectPayType = ref('wechat')

// ========== 核心：接收上一页参数 ==========
onLoad((options) => {
  console.log("接收页面参数：", options)
  // 金额赋值
  if (options.amount) {
    payAmount.value = options.amount
  }
  
})

// 提交支付
async function submitPay() {
  uni.showLoading('发起支付...')
  try {
    const res = await createPayOrder({
		// rechargeAmount:0.01,
      rechargeAmount: payAmount.value,
      // packageId: packageId.value,
	  openId: main.openid
    })
    uni.hideLoading()
	console.log("------=====res============",res)
    if(res.orderNo) {
      const payInfo = res.payParams.data
      uni.requestPayment({
        provider: 'wxpay',
        timeStamp: payInfo.timeStamp,
        nonceStr: payInfo.nonceStr,
        package: payInfo.package,
        signType: payInfo.signType,
        paySign: payInfo.paySign,
        success() {
          uni.showToast('支付成功')
          setTimeout(()=>{
            uni.switchTab({
            	url: '/pages/mine/mine'
            });
          },1200)
        },
        fail(err) {
          if(err.errMsg !== 'requestPayment:fail cancel'){
            uni.showToast('支付失败')
          }
        }
      })
    } else {
      uni.showToast("创建订单失败")
    }
  } catch (e) {
    uni.hideLoading()
    uni.showToast('网络异常')
  }
}
</script>

<style scoped>
.cashier-page {
  background-color: #ffffff;
  min-height: 100vh;
}
.back-btn {
  position: fixed;
  top: 30rpx;
  left: 30rpx;
  z-index: 99;
}
.header {
  background-color: #62A850;
  text-align: center;
  padding: 80rpx 20rpx 60rpx;
  color: #fff;
}
.price {
  font-size: 90rpx;
  font-weight: bold;
}
.pay-type-text {
  font-size: 32rpx;
  margin-top: 16rpx;
}
.pay-list {
  margin-top: 30rpx;
}
.pay-item {
  display: flex;
  align-items: center;
  padding: 36rpx 30rpx;
  position: relative;
  border-bottom: 1rpx solid #eee;
  padding-bottom: 40px;
}
.pay-name {
  font-size: 34rpx;
  margin-left: 20rpx;
}
.tip-text {
  position: absolute;
  left: 96rpx;
  top: 116rpx;
  font-size: 26rpx;
  color: #999;
  display: block;
  background: #f5f5f5;
  padding: 6rpx 16rpx;
  border-radius: 8rpx;
}
.check-icon {
  position: absolute;
  right: 30rpx;
}
.bottom-fixed {
  position: fixed;
  width: 100%;
  bottom: 60rpx;
  padding: 0 40rpx;
  box-sizing: border-box;
}
</style>