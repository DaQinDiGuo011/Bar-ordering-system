<template>
	<uv-navbar
		:fixed="true"
		bgColor="#ffffff"
		:title="title"
		left-arrow
		:placeholder="true"
	  @leftClick="$onClickLeft"
	/>
  <view class="recharge-page">
    <!-- 顶部粉色头部 -->
	
    <view class="header">
      <view class="header-info">
        <uv-avatar size="70" shape="circle" :src="member.avatar ? member.avatar : '/static/images/mine/default.png'"></uv-avatar>
        <view class="info-right">
          <view class="balance-text">余额：¥{{ member.nowMoney }}</view>
          <view class="link-row" style="color:blue;">
            <text class="link" @click="goTradeRecord">交易记录</text>
            <!-- <text class="link" @click="goExchange">兑换储值</text> -->
          </view>
        </view>
      </view>
    </view>

    <view class="content">
      <!-- 充值套餐区域 -->
      <view class="package-wrap">
        <view class="package-grid">
          <!-- 套餐选项 -->
          <view
            v-for="(item, index) in packageList"
            :key="index"
            class="package-item"
            :class="{active: selectType === index}"
            @click="selectPackage(index)"
          >
            <view class="price">{{ item.amount }} 元</view>
            <view class="gift" v-if="item.giftAmount && item.giftAmount > 0">赠送:{{ item.giftAmount }}元</view>
            <!-- <view class="gift">赠送:{{ item.growValue }}成长值</view> -->
          </view>
          <!-- 自定义金额 -->
          <view
            class="package-item"
            :class="{active: selectType === -1}"
            @click="selectCustom"
          >
            <view class="price input-row">
              <uv-input
                v-model="customAmount"
                placeholder="请输入"
                type="number"
                border="none"
                @focus="selectCustom()"
              ></uv-input>
              <text>元</text>
            </view>
            <view class="gift">其他金额</view>
          </view>
        </view>
      </view>

      <!-- 选中套餐预览信息 -->
      <view class="preview-card" v-if="currentSelect && currentSelect.giftAmount && currentSelect.giftAmount > 0">
        <view class="preview-item">赠送：{{ currentSelect.giftAmount }}元</view>
        <!-- <view class="preview-item">赠送：{{ currentSelect.growValue }}成长值</view>
        <view class="preview-item">赠送等级：{{ currentSelect.vipLevel }}</view> -->
      </view>

      <!-- 使用说明 -->
      <view class="desc-wrap">
        <view class="desc-title">使用说明</view>
        <view class="desc-text">会员储值可享受优惠活动</view>
      </view>
    </view>

    <!-- 底部提交按钮 -->
    <view class="bottom-btn">
      <uv-button
        type="primary"
        color="#f4a8c0"
        size="large"
        block
        @click="submitRecharge"
      >立即储值</uv-button>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { balanceGetMoneyList } from '@/api/user.js'
import { useMainStore } from '@/store/store'
import { storeToRefs } from 'pinia'

const main = useMainStore()
const { member} = storeToRefs(main)
// 用户信息
const userInfo = ref({
  avatar: '/static/avatar.png',
  balance: '0.00'
})
// 套餐列表
const packageList = ref([])
// -1=自定义金额，>=0为套餐下标
const selectType = ref(0)
// 自定义金额
const customAmount = ref('')
const title = ref('充值中心')

// 当前选中套餐信息
const currentSelect = computed(() => {
  if (selectType.value === -1) {
    if (!customAmount.value) return null
    // 自定义金额可自行后端计算赠送规则，这里示例
    return {
      amount: customAmount.value,
      giftAmount: '0.00',
      growValue: 0,
      vipLevel: '普通会员'
    }
  } else {
    return packageList.value[selectType.value]
  }
})

// 获取套餐列表
async function loadPackage() {
  const res = await balanceGetMoneyList()
  if(res) {
    packageList.value = res
  }
}

// 选择套餐
function selectPackage(index) {
  selectType.value = index
}

// 选中自定义
function selectCustom() {
  selectType.value = -1
}

// 跳转交易记录
function goTradeRecord() {
  uni.navigateTo({url: '/pages/components/pages/balanceLog/balanceLog'})
}
// 跳转兑换储值
function goExchange() {
  uni.navigateTo({url: '/pages/components/pages/exchangeCode/exchangeCode'})
}

// 提交储值，创建订单
async function submitRecharge() {
  if(!currentSelect.value) {
    return uni.$uv.toast('请选择充值金额')
  }
  let payAmount
  let packageId
  if(selectType.value === -1) {
    payAmount = customAmount.value
    if(Number(payAmount) <= 0) {
      return uni.$uv.toast('请输入正确金额')
    }
  } else {
	packageId = packageList.value[selectType.value].id
    payAmount = currentSelect.value.amount
  }
  console.log("===========",payAmount)
  uni.navigateTo({url: '/pages/components/pages/pay/cashier?amount='+ payAmount + '&packageId=' + packageId})
  // uni.$uv.showLoading()
  // const res = await balanceRecharge({
  //   rechargeAmount: payAmount,
  //   packageId: selectType.value >=0 ? packageList.value[selectType.value].id : null
  // })
  // uni.$uv.hideLoading()
  // if(res.code === 200) {
  //   // 拉起支付（微信/支付宝）
  //   uni.$uv.toast('创建订单成功，唤起支付')
  //   // pay(res.data.payParams)
  // } else {
  //   uni.$uv.toast(res.msg)
  // }
}

onMounted(()=>{
  loadPackage()
})
</script>

<style scoped>
.recharge-page {
  background-color: #f8f8f8;
  min-height: 100vh;
}
.header {
  background-color: #f4a8c0;
  padding: 40rpx 30rpx;
}
.header-info {
  display: flex;
  align-items: center;
}
.info-right {
  margin-left: 20rpx;
  color: #fff;
}
.balance-text {
  font-size: 36rpx;
  font-weight: 500;
}
.link-row {
  display: flex;
  margin-top: 16rpx;
}
.link {
  font-size: 28rpx;
  margin-right: 30rpx;
}
.content {
  padding: 30rpx;
}
.package-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24rpx;
}
.package-item {
  background: #fff;
  border-radius: 16rpx;
  padding: 30rpx 20rpx;
  text-align: center;
  border: 2rpx solid #fff;
}
.package-item.active {
  border-color: #f4a8c0;
}
.price {
  font-size: 48rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 12rpx;
}
.price.input-row {
  display: flex;
  align-items: center;
  justify-content: center;
}
.gift {
  font-size: 26rpx;
  color: #666;
  line-height: 1.6;
}
.preview-card {
  background-color: #fce4ec;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-top: 30rpx;
}
.preview-item {
  font-size: 32rpx;
  color: #444;
  line-height: 2;
}
.desc-wrap {
  margin-top: 40rpx;
}
.desc-title {
  font-size: 36rpx;
  font-weight: bold;
  margin-bottom: 16rpx;
}
.desc-text {
  font-size: 30rpx;
  color: #555;
}
.bottom-btn {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  padding: 24rpx 30rpx;
  box-sizing: border-box;
  background: #fff;
}
</style>