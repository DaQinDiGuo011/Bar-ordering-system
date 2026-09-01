<template>
<view class="page">
  <!-- 顶部导航 -->
  <uv-navbar
	  :fixed="true"
	  bgColor="#ffffff"
	  title="酒水寄存"
	  left-arrow
	  :placeholder="true"
      @leftClick="$onClickLeft"/>

  <!-- uv-tabs 顶部切换 -->
  <uv-tabs
    v-model="activeTab"
    :list="tabList"
    line-width="80rpx"
    line-color="#e86890"
    active-color="#303133"
    inactive-color="#606266"
	@change="changeTab"
  ></uv-tabs>

  <!-- Tab1：我要寄存 -->
  <view v-if="activeTab === 0" class="form-wrap">
    <view class="form-item">
      <text class="label">姓名</text>
      <input class="input" v-model="form.realName" placeholder="请输入姓名"/>
    </view>
    <view class="form-item">
      <text class="label">手机号</text>
      <input class="input" v-model="form.phone" placeholder="请输入手机号" type="number"/>
    </view>
    <view class="form-item" @click="openProductPicker">
      <text class="label">酒水品牌</text>
      <text class="val">{{form.productName || '请选择'}}</text>
      <uv-icon name="arrow-right" size="32"></uv-icon>
    </view>
    <view class="form-item">
      <text class="label">数量</text>
      <input class="input" v-model="form.num" placeholder="请输入数量" type="number"/>
    </view>
    <view class="form-item">
      <text class="label">备注</text>
      <input class="input" v-model="form.remark" placeholder="请输入备注信息"/>
    </view>

    <!-- 协议勾选 -->
    <view class="agree-box">
		 <uv-checkbox-group v-model="checkGroupValue">
		     <uv-checkbox name="agree" >
		       <view >阅读并同意</view>
		       
		     </uv-checkbox>
		   </uv-checkbox-group>
		   <view class="protocol" @click="openProtocol">《寄存协议》</view>
    </view>

    <!-- 提交按钮 -->
    <uv-button
      class="submit-btn"
      type="primary"
      @click="submitForm"
    >提交</uv-button>

    <!-- 商品选择弹窗 -->
    <uv-action-sheet
      :show="showPicker"
      :list="productList"
      key-name="store_name"
      @select="onSelectProduct"
      @close="showPicker=false"
    ></uv-action-sheet>
  </view>

  <!-- Tab2：我的寄存（列表区域，这里先预留位置，你直接粘贴之前mylist页面代码到这里） -->
  <view v-if="activeTab === 1" class="list-wrap">
    <text>我的寄存列表区域</text>
    <!-- 直接把 pages/winestore/mylist.vue 的模板代码粘贴到此处 -->
  </view>
</view>
</template>

<script setup>
import {ref, computed} from 'vue'
import {storeSubmitApi} from '@/api/wallet.js'

// Tab激活下标
const activeTab = ref(0)
const tabList = ref([
  {name:'我要寄存'},
  {name:'我的寄存'}
])

const form = ref({
  realName:'',
  phone:'',
  productId:'',
  productName:'',
  num:'',
  remark:''
})
const checkGroupValue = ref([]) // checkbox-group 必须绑定数组

const showPicker = ref(false)
const productList = ref([])
// 登录获取用户ID
const userId = ref(1)

const canSubmit = computed(()=>{
  return form.value.realName && form.value.phone && form.value.productId && form.value.num && checkGroupValue.value.includes('agree')
})

// 打开酒水选择
const openProductPicker = async ()=>{
  
}
const changeTab = (e) => {
	console.log(e,'-------============')
	if(e.index == 1){
		uni.navigateTo({url:'/pages/components/pages/winestoreMylist/winestoreMylist'})
	}
}
// 选中酒水
const onSelectProduct = (item)=>{
  form.value.productId = item.id
  form.value.productName = item.store_name
  showPicker.value = false
}

// 寄存协议
const openProtocol = ()=>{
  uni.navigateTo({url:'/pages/winestore/protocol'})
}

// =====表单校验函数=====
function validateForm() {
  if(!form.value.realName.trim()){
    uni.showToast({title:'请输入姓名',icon:'none'})
    return false
  }
  if(!form.value.phone.trim()){
    uni.showToast({title:'请输入手机号',icon:'none'})
    return false
  }
  // 手机号正则校验
  const phoneReg = /^1[3-9]\d{9}$/
  if(!phoneReg.test(form.value.phone)){
    uni.showToast({title:'手机号格式不正确',icon:'none'})
    return false
  }
  if(!form.value.productId){
    // uni.showToast({title:'请选择酒水品牌',icon:'none'})
    // return false
  }
  if(!form.value.num || Number(form.value.num) <= 0){
    uni.showToast({title:'数量必须大于0',icon:'none'})
    return false
  }
  if(!checkGroupValue.value.includes('agree')){
    uni.showToast({title:'请阅读并同意寄存协议',icon:'none'})
    return false
  }
  return true
}

// 提交表单
const submitForm = async ()=>{
	
	// 执行校验
	if(!validateForm()) return
	const data = {
    userId: userId.value,
    realName: form.value.realName,
    phone: form.value.phone,
    productId: form.value.productId,
    num: Number(form.value.num),
    remark: form.value.remark
  }
  const res = await storeSubmitApi(data)
  if(res.code === 200){
    uni.showToast({title:res.msg})
    // 清空表单
    form.value = {realName:'',phone:'',productId:'',productName:'',num:'',remark:''}
    checkGroupValue.value = []
    // 提交成功自动切换tab到【我的寄存】
    setTimeout(()=>{
      activeTab.value = 1
    },1200)
  }else{
    uni.showToast({title:res.msg,icon:'none'})
  }
}
</script>

<style scoped>
page{background:#f7f7f7;}

.form-wrap{padding:30rpx;}
.list-wrap{padding:30rpx;}
.form-item{
  display:flex;align-items:center;padding:30rpx 10rpx;
  border-bottom:1rpx solid #eee;
}
.label{font-size:36rpx;width:160rpx;}
.input{flex:1;font-size:34rpx;}
.val{flex:1;font-size:34rpx;color:#999;}

.agree-box{
  align-items:center;margin-top:40rpx;padding:10rpx;
}
.protocol{
	color:#e86890;
	margin-left:10rpx;
	}
.submit-btn{margin-top:60rpx;border-radius:99rpx;height:90rpx;}
</style>