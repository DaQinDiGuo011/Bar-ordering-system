<template>
	<uv-navbar
	  :fixed="true"
	  	  bgColor="#ffffff"
	  	  :title="title"
	  	  left-arrow
	  	  :placeholder="true"
	  @leftClick="$onClickLeft"
	/>
	<view class="userinfo-page d-flex flex-column">
		<view class="flex-fill userinfo-page__form">
			<list-cell :hover="false">
				<view class="userinfo-page__field w-100 d-flex align-items-center">
					<view class="userinfo-page__label">头像</view>
					<view class="userinfo-page__input flex-fill">
						<view class="userinfo-page__field userinfo-page__field--relative w-100 d-flex align-items-center">
							<view class="userinfo-page__avatar-row d-flex align-items-center">
								<view class="mr-1">
									<uv-avatar :lazy-load="true" :src="avatarUrl" size="60px" ></uv-avatar>
								</view>
								<view class="flex-1 userinfo-page__change-avatar">
									<button size='mini' open-type="chooseAvatar" @chooseavatar="onChooseAvatar" type="success">更改头像</button>
								</view>
							</view>
						</view>
					</view>
				</view>
				
			</list-cell>
			<list-cell :hover="false">
				<view class="userinfo-page__field w-100 d-flex align-items-center">
					<view class="userinfo-page__label">昵称</view>
					<view class="userinfo-page__input flex-fill">
						<input type="nickname" placeholder="请填写昵称" placeholder-class="text-color-assist font-size-base" 
						v-model="member.nickname">
					</view>
				</view>
			</list-cell>
			<list-cell :hover="false">
				<view class="userinfo-page__field userinfo-page__field--relative w-100 d-flex align-items-center">
					<view class="userinfo-page__label">手机号码</view>
					<view class="userinfo-page__input flex-fill">
						<input type="text" v-model="member.mobile" disabled>
					</view>
				</view>
			</list-cell>
			<list-cell :hover="false">
				<view class="userinfo-page__field w-100 d-flex align-items-center">
					<view class="userinfo-page__label">性别</view>
					<view class="userinfo-page__input flex-fill">
						<view class="userinfo-page__radio-group">
							<view
								class="userinfo-page__radio"
								:class="{ 'userinfo-page__radio--checked': member.gender == '0' }"
								@tap="member.gender=0"
							>先生</view>
							<view
								class="userinfo-page__radio"
								:class="{ 'userinfo-page__radio--checked': member.gender == '1' }"
								@tap="member.gender=1"
							>女士</view>
						</view>
					</view>
				</view>
			</list-cell>
			<list-cell :hover="false" :arrow="!member.birthday">
				<view class="userinfo-page__field w-100 d-flex align-items-center">
					<view class="userinfo-page__label">生日</view>
					<view class="userinfo-page__input flex-fill">
						<picker mode="date" :value="member.birthday" :start="startDate" :end="endDate" @change="handleDateChange">
							{{member.birthday ? member.birthday : '无'}}
						</picker>
					</view>
				</view>
			</list-cell>
			<list-cell :hover="false" last>
				<view class="userinfo-page__field w-100 d-flex align-items-center">
					<view class="userinfo-page__label">加入时间</view>
					<view class="userinfo-page__input flex-fill">
						<input type="text" v-model="member.createTime" disabled>
					</view>
				</view>
			</list-cell>
		</view>
		<view class="userinfo-page__actions d-flex align-items-center just-content-center">
			<button type="primary" class="userinfo-page__save-btn" @tap="save">保存</button>
		</view>
		<uv-popup
		      ref="showPrivacyPopupRef"
		      mode="bottom"
		      border-radius="24rpx"
		      width="86%"
		      :closeOnClickOverlay="false"
			  z-index="9999"
		    >
		      <view class="privacy-box">
		        <text class="popup-title">用户隐私保护提示</text>
		        <view class="popup-content">
		          <text>在你使用 TheOne Bar618 服务之前，请仔细阅读</text>
		          <text 
		            class="link-text"
		            @click="goToPrivacyDoc"
		          >《TheOne Bar618小程序隐私保护指引》</text>
		          <text>如你同意该指引，请点击“同意”开始使用本小程序。</text>
		        </view>
		        <view class="btn-row">
		          <uv-button
		            class="btn-refuse"
		            type="default"
					color="#cecece"
		            @click="handleRefuse"
		          >拒绝</uv-button>
		          <uv-button
		            class="btn-accept"
		            type="primary"
		            @click="handleAccept"
		          >同意</uv-button>
		        </view>
		      </view>
		</uv-popup>
		
		<!-- 2.底部选择菜单 uv-action-sheet -->
		  <uv-action-sheet
		    ref="showAvatarSheetRef"
		    :actions="avatarActionList"
			:safeAreaInsetBottom="false"
			custom-style="bottom: 0 !important;"
		    cancel-text="取消"
		    @select="onSheetSelect"
			z-index="9999"
		  />
		  
		  <updateImage ref="updateImageRef" @updageImage="updageImage"></updateImage>
	</view>
</template>

<script setup>
import {
  ref,
  computed, nextTick
} from 'vue'
import { useMainStore } from '@/store/store'
import { storeToRefs } from 'pinia'
import { onLoad,onShow} from '@dcloudio/uni-app'
import { formatDateTime } from '@/utils/util'
import {
  userEdit
} from '@/api/user'
import { VUE_APP_UPLOAD_URL } from '@/config';
import updateImage from '@/pages/login/updateImage.vue'
const main = useMainStore()
const { openid, lang } = storeToRefs(main)
const updateImageRef = ref(null)
const title = ref('用户设置')
//const currentDate = ref('')
const date = ref('')
const member = ref({})
const showPrivacyPopupRef = ref(null)
// 头像底部菜单
const showAvatarSheetRef = ref(null)
const avatarUrl = ref(null)
// 底部菜单选项
const avatarActionList = ref([
  {
    name: '用微信头像',
	id: '1'
  },
  {
    name: '从相册选择',
	id: '2'
  },
  {
    name: '拍照',
	id: '3'
  }
])
onLoad(() => {
	member.value = main.member;
	//this.$util
	member.value.createTime = formatDateTime(member.value.createTime);
	avatarUrl.value =  member.value.avatar
})	
onShow(() => {
	date.value = getDate({format: true})
})
// 点击头像打开弹窗
const openPrivacyPopup = () => {
  
  nextTick(()=>{
  	updateImageRef.value.openPrivacyPopup()
  })
}
const onChooseAvatar = async (e) => {
	console.log('拿到临时头像路径', e)
	// main.SET_IMAGE_PRIVATE_FLAG(true)
	uploadImage(e.detail.avatarUrl)
	
}
const uploadImage = (filePath) => {
	uni.showLoading({title:'上传中...'})
	uni.uploadFile({
		url: VUE_APP_UPLOAD_URL, 
		filePath: filePath,
		name: 'file',
		header: {
			Authorization: 'Bearer ' + member.value.accessToken,
			lang: lang.value,
			// 'content-type': 'application/x-www-form-urlencoded;charset=UTF-8'
		},
		formData: {
			path: 'user' // 这里传你要存储的子目录，例如 shop / avatar
		},
		success(uploadFileResult){
			console.log('uploadFileResult:',uploadFileResult)
			if (uploadFileResult) {
				const upload = JSON.parse(uploadFileResult.data);
				console.log('upload:',upload.data)
				avatarUrl.value = upload.data
				// member.value.avatar = upload.data;
				main.SET_AVATAR_URL_TEMP(upload.data)
			}
			console.log("---------member==",member)
			uni.hideLoading()
		}, 
		fail(e){
			console.log(e)
			uni.showToast({title:'上传失败，请联系工作人员',icon:'none'})
			uni.hideLoading()
		}
	});
}
// 跳转隐私协议页面
const goToPrivacyDoc = () => {
  uni.navigateTo({
    url: '/pages/privacy/privacy' // 你的隐私协议页面路径
  })
}
const updageImage = (imageUrl)=>{
		avatarUrl.value = imageUrl
	}
// 拒绝隐私协议
const handleRefuse = () => {
  uni.showToast({
    title: '您拒绝隐私协议，无法使用授权功能',
    icon: 'none'
  })
  nextTick(()=>{
  	showPrivacyPopupRef.value.close()
  })
}

// 同意隐私协议
const handleAccept = () => {
  nextTick(()=>{
  	showPrivacyPopupRef.value.close()
  })
  // =========在这里写【微信头像昵称授权登录逻辑】=========
  console.log("用户同意隐私，可以发起微信授权")
  setTimeout(()=>{
      showAvatarSheetRef.value.open()
    }, 300)
  // 调用你之前写好的 wxLogin() 登录函数
}
const startDate = computed(() => { 
	return getDate('start');
})
const endDate = computed(() => { 
	return getDate('end');
})
// 底部菜单选中回调
const onSheetSelect = async (item) => {
  switch(item.id){
    case '1':
      // 调用微信授权获取微信头像（你的wxLogin相关逻辑）
      getWechatAvatar()
      break
    case '2':
      chooseImageFromAlbum()
      break
    case '3':
      takePhoto()
      break
  }
}
// =========功能方法=========
// 使用微信头像
const getWechatAvatar = () => {
  // 这里调用 uni.getUserProfile 获取微信头像昵称
  uni.createSelectorQuery()
      .select('[open-type="chooseAvatar"]')
      .node()
      .exec((res) => {
		  console.log('---==',res)
        if(res[0]?.node){
          res[0].node.click()
        }
      })
}

// 相册选择
const chooseImageFromAlbum = () => {
  uni.chooseImage({
    count:1,
    sourceType:['album'],
    success: (res)=>{
      const tempPath = res.tempFilePaths[0]
	  console.log("-------------temp=", tempPath)
      chooseavatar(tempPath)
    }
  })
}

const chooseavatar = (e) => {
	console.log('detal:',e);
	let url = e.startsWith('blob:') ? e.slice(5) : e;
	console.log('----------res=',url)
	uni.uploadFile({
		url: VUE_APP_UPLOAD_URL, 
		filePath: e,
		name: 'file',
		header: {
			Authorization: 'Bearer ' + member.value.accessToken,
			lang: lang.value,
			// 'content-type': 'application/x-www-form-urlencoded;charset=UTF-8'
		},
		success(uploadFileResult){
			console.log('uploadFileResult:',uploadFileResult)
			if (uploadFileResult) {
				const upload = JSON.parse(uploadFileResult.data);
				console.log('upload:',upload.data)
				member.value.avatar = upload.data;
			}
			console.log("---------member==",member)
		}, 
		fail(e){
			console.log('网络链接错误');
			console.log(e)
		}
	});
}

// 拍照
const takePhoto = () => {
  uni.chooseImage({
    count:1,
    sourceType:['camera'],
    success: (res)=>{
      const tempPath = res.tempFilePaths[0]
      // 上传图片逻辑
    }
  })
}
const getDate = (type) => {
	const date = new Date();
	let year = date.getFullYear();
	let month = date.getMonth() + 1;
	let day = date.getDate();

	if (type === 'start') {
		year = year - 60;
	} else if (type === 'end') {
		year = year + 2;
	}
	month = month > 9 ? month : '0' + month;;
	day = day > 9 ? day : '0' + day;
	return `${year}-${month}-${day}`;
}
const handleDateChange = (e) => {
	member.value.birthday = e.detail.value
}
const save = async() => {
	let data = await userEdit({
		nickname: member.value.nickname,
		mobile: member.value.mobile,
		gender: member.value.gender,
		birthday: member.value.birthday,
		avatar: avatarUrl.value
	});
	if (data) {
		member.value.avatar = avatarUrl.value
		const member2 = Object.assign(main.member, member.value)
		main.SET_MEMBER(member2)
		uni.showToast({title: "保存成功"})
		uni.switchTab({url:'/pages/mine/mine'})
	}
	
}
	
</script>

<style lang="scss" scoped>
// 用户设置页局部 token（与 uni.scss 全局变量配合）
$userinfo-padding-y: 20rpx;
$userinfo-padding-x: $spacing-row-lg;
$userinfo-form-radius: 8rpx;
$userinfo-label-width: 160rpx;
$userinfo-radio-gap: 10rpx;
$userinfo-radio-padding-y: 10rpx;
$userinfo-radio-padding-x: 30rpx;
$userinfo-radio-border-width: 2rpx;
$userinfo-avatar-btn-height: 60rpx;
$userinfo-save-btn-width: 90%;
$userinfo-save-btn-radius: 50rem;

page {
	height: 100%;
}

.userinfo-page {
	--userinfo-padding-y: #{$userinfo-padding-y};
	--userinfo-padding-x: #{$userinfo-padding-x};
	--userinfo-label-width: #{$userinfo-label-width};
	--userinfo-radio-gap: #{$userinfo-radio-gap};
	--userinfo-avatar-btn-height: #{$userinfo-avatar-btn-height};
	--userinfo-save-btn-width: #{$userinfo-save-btn-width};

	padding: var(--userinfo-padding-y) var(--userinfo-padding-x);

	&__form {
		border-radius: $userinfo-form-radius;
	}

	&__field {
		&--relative {
			position: relative;
		}
	}

	&__label {
		width: var(--userinfo-label-width);
		font-size: $font-size-base;
		color: $text-color-base;
	}

	&__radio-group {
		display: flex;
		justify-content: flex-start;
		gap: var(--userinfo-radio-gap);
	}

	&__radio {
		padding: $userinfo-radio-padding-y $userinfo-radio-padding-x;
		border-radius: $border-radius-base;
		border: $userinfo-radio-border-width solid $text-color-assist;
		color: $text-color-assist;
		font-size: $font-size-base;

		&--checked {
			background-color: $color-primary;
			color: $text-color-white;
			border-color: $color-primary;
		}
	}

	&__actions {
		height: calc((100vh - var(--userinfo-padding-y) * 2) / 2);
	}

	&__save-btn {
		width: var(--userinfo-save-btn-width);
		border-radius: $userinfo-save-btn-radius !important;
		font-size: $font-size-lg;
	}

	&__change-avatar {
		right: 0;
		height: var(--userinfo-avatar-btn-height);

		button {
			background-color: $color-primary;
			color: $text-color-white;
		}
	}
	/* 弹窗样式 */
	.privacy-box {
	  padding: 60rpx 40rpx;
	}
	.popup-title {
	  font-size: 42rpx;
	  font-weight: 500;
	  display: block;
	  margin-bottom: 30rpx;
	}
	.popup-content {
	  font-size: 32rpx;
	  color: #666;
	  line-height: 1.7;
	}
	.link-text {
	  color: #3678d8;
	}
	.btn-row {
	  display: flex;
	  margin-top: 60rpx;
	  gap: 30rpx;
	  justify-content: center;
	}
	.btn-refuse, .btn-accept {
	  flex: 1;
	  height: 88rpx;
	  border-radius: 16rpx;
	}
}
</style>
