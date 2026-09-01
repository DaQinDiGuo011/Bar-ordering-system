<template>
	<uv-navbar
	  :fixed="true"
	  bgColor="#ffffff"
	  title="隐私协议"
	  left-arrow
	  :placeholder="true"
	  @leftClick="uni.navigateBack()"
	/>
  <view class="page">
    <!-- 导航栏 fixed=false 正常文档流 -->
    
    <!-- 直接内容，不套scroll-view，使用页面滚动 -->
    <view class="content">
      <view class="head-title">用户隐私保护指引</view>
      <view class="date">生效日期：2025年08月05日</view>

      <view class="section">
        <view class="sec-title">一、信息收集声明</view>
        <view class="paragraph">我们仅收集实现小程序核心功能所必需的信息：</view>
        <view class="sub-title">开发者处理的信息</view>
        <view class="dot-item">
          为了用户信息（微信昵称、头像）用于用户登录后显示用户昵称和用户头像，开发者将在获取你的明示同意后，收集你的微信昵称、头像。
        </view>
        <view class="dot-item">
          为了位置信息方便配送，开发者将在获取你的明示同意后，收集你的位置信息。
        </view>
        <view class="dot-item">
          为了手机号码配送人员联系，开发者将在获取你的明示同意后，收集你的手机号。
        </view>
        <view class="dot-item">
          开发者收集你选中的照片或视频信息，用于选中的照片或视频信息上传图片评价。
        </view>
        <view class="dot-item">
          开发者获取你选择的位置信息，用于选择配送的地址。
        </view>
      </view>

      <view class="section">
        <view class="sec-title">二、信息使用规则</view>
        <view class="paragraph">最小必要原则：拒绝强制收集通讯录、身份证号等非必要信息</view>
        <view class="paragraph">第三方共享：如涉及支付服务，将明确披露合作方名称及数据用途</view>
        <view class="paragraph">儿童保护：14岁以下用户需监护人单独同意</view>
      </view>

      <view class="section">
        <view class="sec-title">三、用户权利保障</view>
        <view class="sub-title">权限管理：</view>
        <view class="paragraph">
          关于收集你的位置信息，你可以通过以下路径：小程序主页右上角“…”→“设置”—点击特定信息—点击“不允许”，撤回对开发者的授权。
        </view>
      </view>

      <view class="section">
        <view class="sec-title">四、数据安全措施</view>
        <view class="paragraph">采用AES-256加密存储用户数据</view>
        <view class="paragraph">建立隐私影响评估制度，每年至少两次安全审计</view>
      </view>

      <view class="section">
        <view class="sec-title">五、协议更新</view>
        <view class="paragraph">重大变更将通过弹窗重新获取同意</view>
      </view>
    </view>
  </view>
</template>

<script setup>
</script>

<style scoped>
page {
  background-color: #ffffff;
}
.content {
  padding: 30rpx 40rpx 80rpx;
}
.head-title {
  font-size: 38rpx;
  font-weight: 500;
  margin-bottom: 20rpx;
}
.date {
  font-size: 32rpx;
  color: #333;
  margin-bottom: 40rpx;
}
.section {
  margin-bottom: 40rpx;
}
.sec-title {
  font-size: 36rpx;
  font-weight: 500;
  margin: 30rpx 0 16rpx;
}
.sub-title {
  font-size: 34rpx;
  margin: 24rpx 0 12rpx;
}
.paragraph {
  font-size: 32rpx;
  line-height: 1.8;
  color: #222;
  margin-bottom: 12rpx;
}
.dot-item {
  font-size: 32rpx;
  line-height: 1.8;
  color: #222;
  margin-bottom: 16rpx;
  padding-left: 24rpx;
  position: relative;
}
.dot-item::before {
  content: "·";
  position: absolute;
  left: 0;
}
</style>