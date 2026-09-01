<template>
  <view class="page">
	  <uv-navbar
		:fixed="true"
		  bgColor="#ffffff"
		  title="我的卡券"
		  left-arrow
		  :placeholder="true"
	    @leftClick="$onClickLeft"/>
    <!-- 搜索框 -->
    <view class="search-wrap">
      <uv-search
        placeholder="搜索优惠券"
        v-model="keyword"
        :show-action="false"
      />
    </view>
	
    <!-- 顶部Tab：全部/未使用/已使用/已失效 -->
    

    <view class="flex-row">
      <!-- 左侧分类栏 -->
      <view class="left-cate">
        <view
          v-for="(item,index) in cateList"
          :key="index"
          class="cate-item"
          :class="{active:currentCate === index}"
          @click="currentCate = index; loadData()"
        >
          {{item.name}}（{{item.count}}）
        </view>
      </view>
	
      <!-- 右侧卡券列表区域 -->
      <view class="right-content">
		  <view class="top-tab-wrap">
			<view
			  v-for="(item,index) in topTabList"
			  :key="index"
			  class="top-tab-item"
			  :class="{active:currentTopTab === index}"
			  @click="currentTopTab = index; loadData()"
			>
			  {{item.name}}
			</view>
		  </view>
        <view v-if="couponList.length > 0" class="coupon-list">
          <!-- 后续这里放置卡券卡片 -->
          <view class="coupon-card" v-for="item in couponList" :key="item.id">
            {{item.title}}
          </view>
        </view>
        <view v-else class="empty-box">
          <uv-empty mode="list" text="暂无相关内容~"></uv-empty>
        </view>
      </view>
    </view>

    <!-- 底部固定按钮 -->
    <view class="bottom-btn">
      <uv-button type="primary" color="#e08fb0" block @click="goExchangeCoupon">兑换卡券</uv-button>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getUserCouponList } from '@/api/user.js'

// 搜索关键词
const keyword = ref('')
// 顶部tab
const currentTopTab = ref(1)
const topTabList = ref([
  {name:'未使用'},
  {name:'已使用'},
  {name:'已失效'}
])
// 左侧分类
const currentCate = ref(0)
const cateList = ref([
	{name:'全部', count:0},
  {name:'外卖', count:0},
  {name:'自提', count:0},
  {name:'堂食', count:0},
  {name:'指定', count:0},
  {name:'快递', count:0},
])

// 卡券列表
const couponList = ref([])

onMounted(()=>{
  loadData()
})

// 加载卡券数据
const loadData = async ()=>{
  try{
    const params = {
      status: currentTopTab.value, //0全部 1未使用 2已使用 3已失效
      type: currentCate.value,
      keyword: keyword.value
    }
    const res = await getUserCouponList(params)
	console.log("res---",res)
    couponList.value = res.records || []
    // 更新tab数量（后端返回统计数字）
    topTabList.value = res.topTabStat
    cateList.value = res.cateStat
  }catch(err){
    couponList.value = []
  }
}

// 跳转兑换卡券页面
const goExchangeCoupon = ()=>{
  uni.navigateTo({url:"/pages/components/pages/exchangeCode/exchangeCode"})
}
</script>

<style scoped>
page{
  background-color: #f5f5f5;
}
.page{
  min-height: 100vh;
  padding-bottom: 120rpx;
}
.search-wrap{
  padding: 24rpx 30rpx;
  background: #fff;
}

/* 顶部Tab */
.top-tab-wrap{
  display: flex;
  background-color: #f5f5f5;
  padding: 0 20rpx;
  margin-bottom: 60rpx;
}
.top-tab-item{
  padding: 5px 24rpx;
  font-size: 32rpx;
  color: #666;
  border-radius: 50rpx;
  border: 2rpx solid #666;
  margin:16rpx 8rpx;
}
.top-tab-item.active{
  color: #dd3a4a;
  font-weight: 500;
  border: 2rpx solid #dd3a4a;
  border-radius: 50rpx;
  margin:16rpx 8rpx;
  padding:5rpx 24rpx;
}

.flex-row{
  display: flex;
  margin-top: 1rpx;
}

/* 左侧分类 */
.left-cate{
  width: 200rpx;
  background-color: #fff;
}
.cate-item{
  padding: 32rpx 20rpx;
  font-size: 32rpx;
  color:#333;
}
.cate-item.active{
  background-color: #f5f5f5 ;
}

/* 右侧内容区 */
.right-content{
  flex: 1;
  min-height: calc(100vh - 360rpx);
  background-color: #f5f5f5;
  /* padding-top: 120rpx; */
}
.empty-box{
  text-align: center;
}

/* 底部按钮固定 */
.bottom-btn{
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  padding:20rpx 30rpx;
  background-color: #fff;
}
</style>