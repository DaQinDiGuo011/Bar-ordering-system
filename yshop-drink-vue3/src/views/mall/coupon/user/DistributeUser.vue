<template>
  <el-dialog
    v-model="dialogVisible"
    title="分配用户"
    width="700px"
    destroy-on-close
    @close="handleClose"
  >
    <!-- 用户搜索栏 -->
    <el-form :model="userQuery" inline label-width="80px">
      <el-form-item label="用户昵称">
        <el-input
          v-model="userQuery.nickname"
          placeholder="请输入用户昵称"
          clearable
          class="!w-220px"
          @keyup.enter="getUserList"
        />
      </el-form-item>
      <el-form-item label="手机号">
        <el-input
          v-model="userQuery.mobile"
          placeholder="请输入手机号"
          clearable
          class="!w-220px"
          @keyup.enter="getUserList"
        />
      </el-form-item>
      <el-form-item>
        <el-button @click="getUserList"><Icon icon="ep:search" />搜索</el-button>
        <el-button @click="resetUserQuery"><Icon icon="ep:refresh" />重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 表格：参考示例 highlight‑current‑row + @current‑change，type="radio"只做展示 -->
    <el-table
      v-loading="userLoading"
      :data="userList"
      border
      highlight-current-row
      @current-change="handleCurrentChange"
      style="margin-top:10px;height:350px;"
    >
      <el-table-column type="radio" width="60" />
      <el-table-column label="用户ID" prop="id" align="center" width="100"/>
      <el-table-column label="昵称" prop="nickname" align="center"/>
      <el-table-column label="手机号" prop="mobile" align="center"/>
    </el-table>

    <!-- 用户分页 -->
    <Pagination
      :total="userTotal"
      v-model:page="userQuery.pageNo"
      v-model:limit="userQuery.pageSize"
      @pagination="getUserList"
    />

    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="confirmSelectUser" :loading="submitLoading">确认分配</el-button>
    </template>
  </el-dialog>


  <el-dialog v-model="pwdDialogVisible" title="分配确认" width="420px">
    <div>确定要将优惠券分配给用户【{{ currentSelectRow.nickname }}】吗？</div>
    <el-form :model="pwdForm" label-width="80px" class="mt-4">
      <el-form-item label="操作密码" prop="pwd" :rules="[{required:true,message:'请输入密码',trigger:'blur'}]">
        <el-input
          v-model="pwdForm.pwd"
          :type="pwdShow ? 'text' : 'password'"
          placeholder="请输入操作密码"
          show-password
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="pwdDialogVisible=false">取消</el-button>
      <el-button type="primary" @click="submitAlloc">确定分配</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useMessage } from '@/hooks/web/useMessage'
import * as CouponApi from '@/api/mall/coupon/'
import * as UserApi from '@/api/member/user'
import { ElMessageBox } from 'element-plus'
const emit = defineEmits(['success'])
const message = useMessage()
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用

const dialogVisible = ref(false)
const submitLoading = ref(false)
const userLoading = ref(false)
const pwdDialogVisible = ref(false)
const pwdShow = ref(false)
const pwdForm = ref({
  pwd: ''
})

// 当前操作优惠券id
const couponId = ref<number | undefined>()

// 用户查询条件
const userQuery = reactive({
  pageNo: 1,
  pageSize: 10,
  nickname: '',
  mobile: ''
})
const userList = ref<any[]>([])
const userTotal = ref(0)
const formData = ref({
  id: undefined,
  shopId: undefined,
  shopName: undefined,
  title: undefined,
  isSwitch: undefined,
  least: undefined,
  value: undefined,
  startTime: undefined,
  endTime: undefined,
  weigh: undefined,
  type: undefined,
  exchangeCode: undefined,
  receive: undefined,
  distribute: undefined,
  score: 0,
  instructions: undefined,
  userId: undefined,
  image: "",
  limit: undefined
})

// 保存表格当前选中行（重点！参考你的酒水寄存 currentSelectMemberRow）
const currentSelectRow = ref<any>(null)

/** 打开弹窗，父组件调用 open(type,id) */
const open = async (_type: string, id: number) => {
  couponId.value = id
  dialogVisible.value = true
  if (id) {
    formLoading.value = true
    try {
      formData.value = await CouponApi.getCoupon(id)
      formData.value.shopId = Number(formData.value.shopId)
    } finally {
      formLoading.value = false
    }
  }
  resetSelect()
  getUserList()
}

/** 获取用户列表 */
const getUserList = async () => {
  userLoading.value = true
  try {
    const res = await UserApi.getUserPage(userQuery)
    userList.value = res.list
    userTotal.value = res.total
  } finally {
    userLoading.value = false
  }
}

/** 表格行选中变化 */
const handleCurrentChange = (row: any) => {
  console.log('选中行：', row)
  currentSelectRow.value = row
}

/** 确认分配按钮 */
const confirmSelectUser = async () => {
  if (!currentSelectRow.value) {
    message.warning('请选择一个用户')
    return
  }
  pwdForm.value.pwd = ''
  pwdDialogVisible.value = true
  // 二次确认弹窗
  // await ElMessageBox.confirm(
  //   `确定要将优惠券分配给用户【${currentSelectRow.value.nickname}】吗？`,
  //   '分配确认',
  //   {
  //     confirmButtonText: '确定分配',
  //     cancelButtonText: '取消',
  //     type: 'warning'
  //   }
  // )



  
}
async function submitAlloc(){
  if(!pwdForm.value.pwd){
    ElMessage.warning('请输入操作密码')
    return
  }
  const password = pwdForm.value.pwd
  
  submitLoading.value = true
  try {
    console.log('优惠券id', couponId.value, '分配用户id', currentSelectRow.value.id)
    const data = { ...formData.value }
    data.userId = currentSelectRow.value.id
    data.pwd = password
    await CouponApi.distributeUserCoupon(data)
    message.success('分配成功')
    emit('success')
    dialogVisible.value = false
  } catch (e) {
    console.error(e)
    if (!(e instanceof Error && e?.name === 'CanceledError')) {
      console.error(e)
    }
  } finally {
    submitLoading.value = false
    pwdDialogVisible.value = false
  }
  
}
/** 重置选中状态（弹窗关闭/重新打开调用） */
const resetSelect = () => {
  currentSelectRow.value = null
}

/** 重置搜索条件 */
const resetUserQuery = () => {
  userQuery.nickname = ''
  userQuery.mobile = ''
  userQuery.pageNo = 1
  getUserList()
}

/** 弹窗关闭 */
const handleClose = () => {
  resetSelect()
}

defineExpose({ open })
</script>