<template>
<view class="page">
  <!-- 导航栏 -->
  <uv-navbar
	  :fixed="true"
	  bgColor="#ffffff"
	  title="我的寄存"
	  left-arrow
	  :placeholder="true"
      @leftClick="onClickLeft"/>

  <!-- 一级Tab：寄存记录 / 领取记录 -->
  <view class="head-tab">
    <view class="tab-item" :class="{active: mainTab === 'storeRecord'}" @click="mainTab='storeRecord';filterStatus=0;loadData(true)">
      寄存记录
      <view v-if="mainTab === 'storeRecord'" class="tab-line"></view>
    </view>
    <view class="tab-item" :class="{active: mainTab === 'receiveRecord'}" @click="mainTab='receiveRecord';filterStatus=4;loadData(true)">
      领取记录
      <view v-if="mainTab === 'receiveRecord'" class="tab-line"></view>
    </view>
  </view>

  <!-- 寄存记录筛选栏 -->
  <scroll-view scroll-x v-if="mainTab === 'storeRecord'" class="filter-scroll">
    <view class="filter-tab">
      <view class="filter-item" :class="{active: filterStatus == 0}" @click="filterStatus=0;loadData(true)">寄存记录</view>
      <view class="filter-item" :class="{active: filterStatus === 1}" @click="filterStatus=1;loadData(true)">待付款</view>
      <view class="filter-item" :class="{active: filterStatus === 2}" @click="filterStatus=2;loadData(true)">存储中</view>
      <view class="filter-item" :class="{active: filterStatus === 3}" @click="filterStatus=3;loadData(true)">已失效</view>
    </view>
  </scroll-view>

  <!-- 领取记录筛选栏 -->
  <scroll-view scroll-x v-if="mainTab === 'receiveRecord'" class="filter-scroll">
    <view class="filter-tab">
      <view class="filter-item" :class="{active: filterStatus == 4}" @click="filterStatus=4;loadData(true)">领取记录</view>
      <view class="filter-item" :class="{active: filterStatus === 5}" @click="filterStatus=5;loadData(true)">领取中</view>
      <view class="filter-item" :class="{active: filterStatus === 6}" @click="filterStatus=6;loadData(true)">已完成</view>
    </view>
  </scroll-view>

  <!-- 列表区域 -->
  <scroll-view scroll-y class="list-scroll" refresher-enabled :refresher-triggered="isRefreshing"
  @refresherrefresh="onScrollViewPullDown" @scrolltolower="onScrollToLower">
    <view v-if="listData.length === 0 && !loading" class="empty">
      <image class="empty-img" src="/static/images/nodata.jpg" mode="aspectFit"></image>
      <text class="empty-txt">暂无相关内容~</text>
    </view>

    <view v-for="item in listData" :key="item.id" class="record-card">
		<view class="card-main">
		    <image class="prod-img" :src="item.image || '/static/images/default_goods.png'" mode="aspectFill"></image>
			<view class="card-info">
				<view class="card-row-no">
				  <text class="no-text">寄存单号：{{item.storeNo}}</text>
				  <text class="status-text">{{getStatusText(item.storeStatus)}}</text>
				</view>
				<view class="card-row">
				  <text>酒水名称：{{item.storeName}}</text>
				</view>
				<view class="card-row">
				  <text>规格：{{item.spec}}</text>
				</view>
				<view class="card-row">
				  <text>寄存数量：{{item.num}}瓶</text>
				</view>
				<view class="card-row" v-if="item.storeStatus == 4 || item.storeStatus == 5">
				  <text>领取时间：{{formatDateTime(item.receiveTime) }}</text>
				</view>
				<view class="card-row">
				  <text>寄存时间：{{formatDateTime(item.createTime) }}</text>
				</view>
			</view>
		</view>
	  <!-- ==========新增领取按钮：仅存储中storeStatus=2显示========== -->
	  <view v-if="item.storeStatus === 1" class="card-btn-row">
	  	<view class="bnt-cancel" @click="cancelPayFnt(item)">取消支付</view>
	  </view>
		<view v-if="item.storeStatus === 2" class="card-btn-row">
			<view class="btn-receive" @click="openReceivePopup(item)">领取</view>
		</view>
    </view>
  
	<view v-if="loading" class="loading-tip">
	  <text class="tip-text">加载中...</text>
	</view>
	<!-- 没有更多 -->
	<view v-if="noMore && listData.length>0" class="nomore-tip">
	  <text class="tip-text">没有更多数据了</text>
	</view>
  </scroll-view>
  
  
  <!-- 领取数量弹窗 -->
    <uv-popup ref="popupRef" mode="center" :round="24" teleport>
      <view class="popup-wrap">
        <view class="popup-title">选择领取数量</view>
        <view class="popup-desc">当前寄存共 {{currentItem.num}} 瓶</view>
        <input class="popup-input" v-model.number="receiveNum" type="number" placeholder="请输入领取瓶数" />
        
		<view class="scan-btn-wrap">
		  <view 
			class="btn-scan" 
			:class="{scanned: deskNumb}"
			@click="handleScanCode"
		  >
			{{ deskNumb ? '✅桌号' + deskNumb : '📷扫码桌号' }}
		  </view>
		  <view v-if="!deskNumb" class="scan-tip">请扫桌面小程序码选择桌号</view>
		</view>
		<view class="popup-buttons">
          <view class="btn-cancel" @click="handlePopupClose">取消</view>
          <view class="btn-confirm" @click="submitReceive">确认领取</view>
        </view>
      </view>
    </uv-popup>
</view>

</template>

<script setup>
import {ref,onMounted,nextTick} from 'vue'
import {getMyStoreApi,receiveStoreApi,cancelPay} from '@/api/wallet.js'
import { formatDateTime } from '@/utils/util'
import { onPullDownRefresh,onLoad,onReachBottom} from '@dcloudio/uni-app'
import { storeToRefs } from 'pinia'
import { useMainStore } from '@/store/store'

const isRefreshing = ref(false)
const main = useMainStore()
const { store} = storeToRefs(main)
// 一级Tab
const mainTab = ref('storeRecord')
// 寄存状态筛选
const filterStatus = ref(0)
// 领取子筛选
const subFilter = ref(null)
const listData = ref([])
const userId = ref(1)

const popupRef = ref(null)
const currentItem = ref({})
const receiveNum = ref('')
const deskNumb = ref("")
const comeType = ref("")
// 状态文字转换
const getStatusText = (status)=>{
  const map = {
    1:'待支付',
    2:'存储中',
    3:'已失效',
    4:'领取中',
	5:'已领取'
  }
  return map[status] || '未知状态'
}
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)
const noMore = ref(false)
//打开领取弹窗
const openReceivePopup = (item)=>{
	deskNumb.value = ''
	currentItem.value = {...item}
	receiveNum.value = ''
  
	nextTick(()=>{
		popupRef.value.open()
	})
}
const cancelPayFnt = async(info)=>{
	uni.showLoading({
		title: '取消中'
	})			
	console.log('info---',info.storeNo)
	await cancelPay({storeNo:info.storeNo})
	
	await loadData(true)
	uni.hideLoading()
}
const onScrollToLower = async ()=>{
  if(loading.value || noMore.value) return
  page.value++
  await loadData(false)
}
const onScrollViewPullDown = async ()=>{
  isRefreshing.value = true
  await loadData(true)
  isRefreshing.value = false
}
const handlePopupClose = ()=>{
  nextTick(()=>{
  	popupRef.value.close()
  })
}

const handleScanCode = ()=>{
	try {
	    uni.scanCode({
			// onlyFromCamera：true 只允许相机扫码；false 可选择相册图片识别二维码
			onlyFromCamera: true,
			scanType: ['qrCode', 'miniProgram'] ,// 同时支持条形码、二维码
			success: (res) => {
				if (!res.path || !res.path.includes('menu?scene=')){
					uni.showToast({
					   title: '扫码失败，请重试或者联系工作人员',
					   icon: 'none'
					})
				}else{
					deskNumb.value = res.path.split('menu?scene=')[1]
				}
	
			},
			fail: (err) => {
			    console.error('扫码失败：', err)
			    uni.showToast({ title: '识别失败', icon: 'none' })
			}
		})
	
	} catch (err) {
		console.log('扫码失败/用户取消', err)
		uni.showToast({
			title: '已取消扫码',
			icon: 'none'
		})
	}
}
// 点击领取
const submitReceive = async ()=>{
  const maxNum = currentItem.value.num
  const num = Number(receiveNum.value)

  if(!num || num <=0){
    uni.showToast({title:'请输入有效领取数量',icon:'none'})
    return
  }
  if(num > maxNum){
    uni.showToast({title:`最多可领取${maxNum}瓶`,icon:'none'})
    return
  }
  if(deskNumb.value == ''){
	  uni.showToast({title:`请扫码桌号`,icon:'none'})
	  return
  }

    handlePopupClose()

  uni.showModal({
    title:'确认领取',
    content:`确定领取 ${num} 瓶【${currentItem.value.storeName}】吗？`,
    success: async (res)=>{
      if(res.confirm){
        // 此处调用你的领取接口
        const resApi = await receiveStoreApi({
			id:currentItem.value.id,
			productId: currentItem.value.productId,
			spec: currentItem.value.spec,
			deskNumber: deskNumb.value,
			shopId: store.value.id,
			shopName: store.value.name,
			num: num
        })
        if(resApi.code === "200"){
			uni.showToast({title:'领取申请成功'})
			loadData(true)
        }else{
			uni.showToast({title:resApi.msg})
		}
      }
    }
  })
}

// 加载列表
const loadData = async (reset=false)=>{
	if(reset){
		page.value = 1
		noMore.value = false
	}
	if(loading.value) return
	loading.value = true
	let params = {
		userId: userId.value,
		type: mainTab.value,
		status: filterStatus.value??0,
		page: page.value,
		pageSize: pageSize.value
	}
	const res = await getMyStoreApi(params)
	loading.value = false
	  
	if(reset){
		listData.value = res.list || []
	}else{
		listData.value.push(...(res.list || []))
	}
	total.value = res.total || 0
	// 判断是否没有更多
	if(listData.value.length >= total.value){
		noMore.value = true
	}
}
const onClickLeft = () => {
  if(comeType.value === 'pay'){
    // type=pay，跳转到订单列表页
    uni.switchTab({
      url:'/pages/mine/mine'
    })
  }else{
    // 其他情况返回上一页
    uni.navigateBack()
  }
}
// onPullDownRefresh(() => {
// 	// current.value = -1
// 	loadData()
// })

onLoad((options => {
	comeType.value = options.type
	if(options.result == 's'){
		
	}else{
		
	}
	if(options.type == 'pay'){
		
	}
}))
onMounted((options)=>{
	
	loadData(true)
})
</script>

<style scoped>
page{background:#f7f7f7;height:100%;}
.page{
	height:100vh;
	display:flex;
	flex-direction:column;
	overflow:hidden; /*页面禁止滚动*/
}
.nav-bar{
  display:flex;align-items:center;
  padding:20rpx 30rpx;
}
.nav-title{font-size:38rpx;font-weight:500;flex:1;text-align:center;}

.head-tab{display:flex;flex-shrink:0;}
.tab-item{
  flex:1;text-align:center;font-size:40rpx;padding:20rpx 0;position:relative;
}
.tab-item.active{font-weight:bold;}
.tab-line{
  position:absolute;bottom:0;left:50%;transform:translateX(-50%);
  width:100rpx;height:6rpx;background:#e86890;
}

.filter-scroll{white-space:nowrap;}
.filter-tab{display:flex;padding:20rpx;gap:20rpx;}
.filter-item{
  padding:12rpx 30rpx;border-radius:99rpx;font-size:32rpx;
  background:#f0f0f0;white-space:nowrap;
}
.filter-item.active{background:#e86890;color:#fff;}

.list-scroll{
	flex:1;
	height:0; 
}
.empty{margin-top:160rpx;display:flex;flex-direction:column;align-items:center;}
.empty-img{width:320rpx;height:320rpx;}
.empty-txt{font-size:36rpx;color:#999;margin-top:30rpx;}

.record-card{
  margin:20rpx 30rpx;
  background:#fff;border-radius:20rpx;padding:30rpx;
}
.card-main{
  display:flex;
  gap:24rpx;
}
.prod-img{
  width:160rpx;
  height:160rpx;
  border-radius:12rpx;
  flex-shrink:0;
}
.card-info{
  flex:1;
}
.card-row-no{
  margin-top:16rpx;
  font-size:34rpx;
  display:flex;
  gap:16rpx;
}
.no-text{
  flex:1;
  word-break:break-all;
}
.card-row{margin-top:16rpx;font-size:34rpx;display:flex;justify-content:space-between;}
.status-text{
	color:#e86890;
	flex-shrink:0;
}

.card-btn-row{
  margin-top:30rpx;
  display:flex;
  justify-content:flex-end;
}
.bnt-cancel{
	padding:14rpx 40rpx;
	background:#b00000;
	color:#fff;
	border-radius:99rpx;
	font-size:30rpx;
}
.btn-receive{
  padding:14rpx 40rpx;
  background:#e86890;
  color:#fff;
  border-radius:99rpx;
  font-size:30rpx;
}


/*弹窗样式*/
.popup-wrap{
  width:520rpx;
  padding:40rpx;
  background:#fff;
  border-radius:24rpx;
}
.popup-title{
  text-align:center;
  font-size:36rpx;
  font-weight:bold;
  margin-bottom:20rpx;
}
.popup-desc{
  text-align:center;
  font-size:30rpx;
  color:#666;
  margin-bottom:30rpx;
}
.popup-input{
  border:1rpx solid #ddd;
  border-radius:12rpx;
  height:88rpx;
  padding:0 24rpx;
  font-size:32rpx;
}
.popup-buttons{
  display:flex;
  margin-top:40rpx;
  gap:24rpx;
}
.btn-cancel{
  flex:1;
  height:84rpx;
  line-height:84rpx;
  text-align:center;
  background:#f2f2f2;
  border-radius:12rpx;
  font-size:32rpx;
}
.btn-confirm{
  flex:1;
  height:84rpx;
  line-height:84rpx;
  text-align:center;
  background:#e86890;
  color:#fff;
  border-radius:12rpx;
  font-size:32rpx;
}
.scan-btn-wrap{
  margin:30rpx 0;
}
.btn-scan{
  width:100%;
  height:84rpx;
  line-height:84rpx;
  text-align:center;
  border:1rpx dashed #e86890;
  border-radius:12rpx;
  font-size:32rpx;
  color:#e86890;
}
.btn-scan.scanned{
  background:#fef0f5;
}
.scan-tip{
  font-size:24rpx;
  color:#da2616;
  text-align:center;
  margin-top:12rpx;
}
.loading-tip,.nomore-tip{
  padding:30rpx 0;
  text-align:center;
}
.tip-text{
  font-size:28rpx;
  color:#999;
}
</style>