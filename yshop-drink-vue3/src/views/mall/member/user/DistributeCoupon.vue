<template>
  <el-dialog
    v-model="dialogVisible"
    title="分配优惠券"
    width="900px"
    destroy-on-close
    @close="handleClose"
  >
    <el-form :model="couponQuery" inline label-width="80px">
      <el-form-item label="优惠券名称">
        <el-input
          v-model="couponQuery.title"
          placeholder="请输入优惠券名称"
          clearable
          class="!w-220px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" />搜索</el-button>
        <el-button @click="resetCouponQuery"><Icon icon="ep:refresh" />重置</el-button>
      </el-form-item>
    </el-form>

    <el-table
      v-loading="couponLoading"
      :data="couponList"
      border
      highlight-current-row
      @current-change="handleCurrentChange"
      style="margin-top:10px;height:360px;"
    >
      <el-table-column type="radio" width="60" />
      <el-table-column label="领券id" align="center" prop="id" width="100" />
      <el-table-column
        label="优惠券名称"
        align="center"
        prop="title"
        min-width="160"
        show-overflow-tooltip
      />
      <el-table-column label="满减门槛" align="center" prop="least" width="120">
        <template #default="scope">
          <span>满{{ scope.row.least }}可用</span>
        </template>
      </el-table-column>
      <el-table-column label="优惠券金额" align="center" prop="value" width="120">
        <template #default="scope">
          <span style="color:#f56c6c">¥{{ scope.row.value }}</span>
        </template>
      </el-table-column>
      <el-table-column
        label="兑换码"
        align="center"
        prop="exchangeCode"
        min-width="180"
        show-overflow-tooltip
      />
      <el-table-column
        label="有效期开始"
        align="center"
        prop="startTime"
        :formatter="dateFormatter"
        width="170"
      />
      <el-table-column
        label="有效期结束"
        align="center"
        prop="endTime"
        :formatter="dateFormatter"
        width="170"
      />
    </el-table>

    <Pagination
      :total="couponTotal"
      v-model:page="couponQuery.pageNo"
      v-model:limit="couponQuery.pageSize"
      @pagination="handlePageChange"
    />

    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="submitLoading" @click="confirmSelectCoupon">
        确认分配
      </el-button>
    </template>
  </el-dialog>

  <el-dialog v-model="pwdDialogVisible" title="分配确认" width="420px">
    <div>确定要将优惠券【{{ currentSelectRow?.title }}】分配给用户【{{ userNickname }}】吗？</div>
    <el-form :model="pwdForm" label-width="80px" class="mt-4">
      <el-form-item
        label="操作密码"
        :rules="[{ required: true, message: '请输入密码', trigger: 'blur' }]"
      >
        <el-input
          v-model="pwdForm.pwd"
          :type="pwdShow ? 'text' : 'password'"
          placeholder="请输入操作密码"
          show-password
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="pwdDialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="submitLoading" @click="submitAlloc">确定分配</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import * as CouponApi from '@/api/mall/coupon/'
import { useMessage } from '@/hooks/web/useMessage'
import { dateFormatter } from '@/utils/formatTime'

const emit = defineEmits(['success'])
const message = useMessage()

const dialogVisible = ref(false)
const pwdDialogVisible = ref(false)
const pwdShow = ref(false)
const submitLoading = ref(false)
const couponLoading = ref(false)
const currentSelectRow = ref<any>(null)
const userId = ref<number | undefined>()
const userNickname = ref('')
const pwdForm = ref({ pwd: '' })

const couponQuery = reactive({
  pageNo: 1,
  pageSize: 10,
  title: ''
})
const allCouponList = ref<any[]>([])
const couponList = ref<any[]>([])
const couponTotal = ref(0)

/** 打开弹窗，传入当前用户id和昵称 */
const open = (id: number, nickname?: string) => {
  userId.value = id
  userNickname.value = nickname || ''
  dialogVisible.value = true
  currentSelectRow.value = null
  couponQuery.pageNo = 1
  couponQuery.pageSize = 10
  couponQuery.title = ''
  getCouponList()
}

/** 查出所有未分配的一人一券，本地分页展示 */
const getCouponList = async () => {
  couponLoading.value = true
  try {
    const pageSize = 100
    const params = {
      pageNo: 1,
      pageSize,
      type: 4,
      title: couponQuery.title || undefined
    }
    const firstPage = await CouponApi.getCouponPage(params)
    const unassignedList = filterUnassigned(firstPage.list)
    const totalPages = Math.ceil(firstPage.total / pageSize)
    for (let pageNo = 2; pageNo <= totalPages; pageNo++) {
      const res = await CouponApi.getCouponPage({ ...params, pageNo })
      unassignedList.push(...filterUnassigned(res.list))
    }
    allCouponList.value = unassignedList
    couponTotal.value = unassignedList.length
    const maxPageNo = Math.max(1, Math.ceil(couponTotal.value / couponQuery.pageSize))
    if (couponQuery.pageNo > maxPageNo) {
      couponQuery.pageNo = 1
    }
    updatePageData()
  } finally {
    couponLoading.value = false
  }
}

/** 未分配：一人一券且未绑定用户 */
const filterUnassigned = (list: any[] = []) =>
  list.filter((item: any) => item.type === 4 && !item.userId)

const updatePageData = () => {
  const start = (couponQuery.pageNo - 1) * couponQuery.pageSize
  couponList.value = allCouponList.value.slice(start, start + couponQuery.pageSize)
}

const handleQuery = () => {
  couponQuery.pageNo = 1
  getCouponList()
}

const resetCouponQuery = () => {
  couponQuery.title = ''
  couponQuery.pageNo = 1
  getCouponList()
}

const handlePageChange = () => {
  updatePageData()
}

const handleCurrentChange = (row: any) => {
  currentSelectRow.value = row
}

const confirmSelectCoupon = () => {
  if (!currentSelectRow.value) {
    message.warning('请选择一个优惠券')
    return
  }
  pwdForm.value.pwd = ''
  pwdDialogVisible.value = true
}

const submitAlloc = async () => {
  if (!pwdForm.value.pwd) {
    message.warning('请输入操作密码')
    return
  }
  submitLoading.value = true
  try {
    const data = {
      ...currentSelectRow.value,
      userId: userId.value,
      pwd: pwdForm.value.pwd
    }
    await CouponApi.distributeUserCoupon(data)
    message.success('分配成功')
    emit('success')
    dialogVisible.value = false
    pwdDialogVisible.value = false
  } catch (e) {
    console.error(e)
  } finally {
    submitLoading.value = false
  }
}

const handleClose = () => {
  currentSelectRow.value = null
  pwdDialogVisible.value = false
}

defineExpose({ open })
</script>
