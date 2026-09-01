<template>
<view class="page">
  <uv-navbar
		:fixed="true"
		bgColor="#ffffff"
		title="积分规则"
		left-arrow
		:placeholder="true"
  	  @leftClick="$onClickLeft"/>

  <view class="content">
    <text>积分可享受积分商城换购活动商品</text>
  </view>
</view>
</template>

<script setup>

</script>

<style scoped>
.page{background:#fff;min-height:100vh}
.nav-bar{display:flex;align-items:center;justify-content:space-between;padding:20rpx 30rpx}
.nav-title{font-size:34rpx;font-weight:500}
.content{padding:30rpx;font-size:32rpx}
</style>