<template>
  <view class="page">
    <!-- 顶部导航 -->
	<uv-navbar
			:fixed="true"
		  	  bgColor="#ffffff"
		  	  title="券包购买记录"
		  	  left-arrow
		  	  :placeholder="true"
		  @leftClick="$onClickLeft"/>


    <!-- 列表区域 -->
    <view v-if="list.length > 0" class="content">
      <view class="item" v-for="item in list" :key="item.id">
        <view class="item-top">
          <text class="name">{{item.packageName}}</text>
          <text class="price">¥{{item.payPrice}}</text>
        </view>
        <view class="item-bottom">
          <text>订单号：{{item.orderNo}}</text>
          <text>{{statusText(item.status)}}</text>
        </view>
      </view>
    </view>

    <!-- 空状态（截图样式） -->
    <view v-else class="empty-box">
      <uv-empty mode="list" text="暂无相关内容~"></uv-empty>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
const list = ref([])

// 获取订单列表
const getList = async () => {
  // const res = await uni.request({
  //   url: '/coupon/order/list',
  //   method: 'GET',
  //   header: {
  //     token: uni.getStorageSync('token')
  //   }
  // })
  // if(res.data.code === 200){
  //   list.value = res.data.data
  // }
}

// 状态文字转换
const statusText = (status) => {
  if(status === 0) return '待支付'
  if(status === 1) return '已支付'
  if(status === 2) return '已取消'
  return '未知'
}

onMounted(()=>{
  getList()
})
</script>

<style scoped>
.page{
  background-color: #f5f5f5;
  min-height: 100vh;
}
.nav-bar{
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx 30rpx;
  background: #fff;
}
.nav-title{
  font-size: 34rpx;
  font-weight: 500;
}
.empty-box{
  margin-top: 200rpx;
}
.content{
  padding: 20rpx;
}
.item{
  background: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
}
.item-top{
  display: flex;
  justify-content: space-between;
  margin-bottom: 16rpx;
}
.name{
  font-size: 32rpx;
}
.price{
  color: #e64340;
  font-size: 32rpx;
}
.item-bottom{
  display: flex;
  justify-content: space-between;
  font-size: 26rpx;
  color: #999;
}
</style>