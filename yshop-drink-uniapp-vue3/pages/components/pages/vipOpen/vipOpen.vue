<template>
	<uv-navbar
		  :fixed="true"
		  bgColor="#ffffff"
		  title="会员卡名称-提交资料"
		  left-arrow
		  :placeholder="true"
		  @leftClick="$onClickLeft"/>
<view class="page">
	
  <view class="form-box">
    <view class="title">填写个人资料</view>
    <view class="tip">*请提供正确的个人资料与联系方式，以便获得最新会员优惠信息</view>

    <view class="form-item">
      <text class="label">姓名</text>
      <input class="input" v-model="form.name" placeholder="请输入姓名"/>
    </view>

    <view class="form-item">
      <text class="label">电话</text>
      <view class="phone-row">
        <input class="input phone-input" v-model="form.phone" placeholder="手机号"/>
        <button open-type="getPhoneNumber" @getphonenumber="onGetPhone" class="auto-btn">自动填写</button>
      </view>
    </view>

    <view class="form-item">
      <text class="label">性别</text>
      <view class="radio-row">
        <view class="radio" @click="form.gender=1">
          <view class="circle" :class="{active:form.gender===1}">
            <view v-if="form.gender===1" class="inner"></view>
          </view>
          <text>男</text>
        </view>
        <view class="radio" @click="form.gender=2">
          <view class="circle" :class="{active:form.gender===2}">
            <view v-if="form.gender===2" class="inner"></view>
          </view>
          <text>女</text>
        </view>
      </view>
    </view>

    <view class="form-item" @click="openBirthdatePick()">
      <text class="label">生日</text>
      <text class="date-txt" :class="form.birthday?'':'gray'">{{form.birthday||"请选择生日"}}</text>
    </view>
    <view class="warn-tip">*温馨提示：生日时间一旦保存后，将不能再次修改哦！</view>
  </view>

  <uv-datetime-picker
    ref="datetimePickerRef"
    v-model="showDatePicker"
    mode="date"
	:formatter="formatter"
    @confirm="selectBirthday"
  />

  <view class="submit-wrap">
    <uv-button block color="#57b861" @click="submit">提交</uv-button>
  </view>
</view>
</template>

<script setup>
import {ref, onMounted, nextTick} from 'vue'
import {openVipCard} from '@/api/vipinfo.js'
import { formatDateNumber } from '@/utils/util.js'

const showDatePicker = ref(false)
const form = ref({
  name:"",
  phone:"",
  gender:1,
  birthday:""
})
const datetimePickerRef = ref(null)
onMounted(async ()=>{
  // await onGetPhone()
})
const openBirthdatePick = ()=>{
	nextTick(()=>{
		datetimePickerRef.value.open()
	})
}
const formatter = (type, value) => {
				if (type === 'year') {
					return `${value}年`
				}
				if (type === 'month') {
					return `${value}月`
				}
				if (type === 'day') {
					return `${value}日`
				}
				return value
			}
// 小程序获取手机号回调
const onGetPhone = async (e)=>{
  if(!e.detail.code){
    uni.showToast({title:"获取手机号失败",icon:"none"})
    return
  }
  try{
    
    form.value.phone = "1498378445"
  }catch(err){
    uni.showToast({title:"手机号解密失败",icon:"none"})
  }
}

// 选择生日
const selectBirthday = (val)=>{
  form.value.birthday = formatDateNumber(val.value)
}
const validPhone = (phone) => {
  const reg = /^1[3-9]\d{9}$/
  return reg.test(phone)
}
// 提交开卡
const submit = async ()=>{
  if(!form.value.name){
    return uni.showToast({title:"请填写姓名",icon:"none"})
  }
  if(!form.value.phone){
    return uni.showToast({title:"请输入手机号",icon:"none"})
  }
  // 校验手机号格式
	if(!validPhone(form.value.phone)){
	  return uni.showToast({title:"手机号格式不正确",icon:"none"})
	}
  if(!form.value.birthday){
    return uni.showToast({title:"请选择生日",icon:"none"})
  }
  await openVipCard(form.value)
  uni.showToast({title:"开卡成功"})
  setTimeout(()=>{
    uni.navigateBack()
  },1200)
}
</script>

<style scoped>
page{
  background:#f5f5f5;
}
.page{
  padding:60rpx 30rpx;
}
.form-box{
  background:#fff;
  border-radius:20rpx;
  padding:50rpx 40rpx;
}
.title{
  font-size:44rpx;
  font-weight:bold;
  text-align:center;
  margin-bottom:20rpx;
}
.tip{
  font-size:28rpx;
  color:#666;
  line-height:1.6;
  margin-bottom:60rpx;
}
.form-item{
  display:flex;
  align-items:center;
  padding:30rpx 0;
  border-bottom:1rpx solid #eee;
}
.label{
  width:140rpx;
  font-size:34rpx;
}
.input{
  flex:1;
  font-size:34rpx;
}
.phone-row{
  flex:1;
  display:flex;
  align-items:center;
}
.phone-input{
  flex:1;
}
.auto-btn{
  background:#57b861;
  color:#fff;
  border-radius:12rpx;
  font-size:28rpx;
  padding:12rpx 24rpx;
  border:none;
}
.radio-row{
  display:flex;
  gap:60rpx;
}
.radio{
  display:flex;
  align-items:center;
  gap:12rpx;
}
.circle{
  width:40rpx;
  height:40rpx;
  border-radius:50%;
  border:2rpx solid #999;
  display:flex;
  align-items:center;
  justify-content:center;
}
.circle.active{
  border-color:#57b861;
  background:#57b861;
}
.inner{
  width:16rpx;
  height:16rpx;
  background:#fff;
  border-radius:50%;
}
.date-txt{
  flex:1;
  font-size:34rpx;
}
.gray{
  color:#aaa;
}
.warn-tip{
  margin-top:30rpx;
  font-size:26rpx;
  color:#888;
}
.submit-wrap{
  margin-top:80rpx;
}
</style>