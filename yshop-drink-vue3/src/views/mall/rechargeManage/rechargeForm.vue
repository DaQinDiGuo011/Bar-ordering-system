<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="充值金额" prop="amount">
        <el-input-number
          v-model="formData.amount"
          :min="0.01"
          :precision="2"
          :step="10"
          controls-position="right"
          class="!w-260px"
        />
      </el-form-item>
      <el-form-item label="赠送金额" prop="giftAmount">
        <el-input-number
          v-model="formData.giftAmount"
          :min="0"
          :precision="2"
          :step="10"
          controls-position="right"
          class="!w-260px"
        />
      </el-form-item>
      <el-form-item label="赠送成长值" prop="growValue">
        <el-input-number
          v-model="formData.growValue"
          :min="0"
          :step="10"
          controls-position="right"
          class="!w-260px"
        />
      </el-form-item>
      <el-form-item label="会员等级" prop="vipLevel">
        <el-input v-model="formData.vipLevel" placeholder="请输入会员等级" />
      </el-form-item>
      <el-form-item label="排序" prop="sort">
        <el-input-number
          v-model="formData.sort"
          :min="0"
          controls-position="right"
          class="!w-260px"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="formData.status">
          <el-radio :label="1">启用</el-radio>
          <el-radio :label="0">禁用</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="权限密码" prop="pwd">
        <el-input v-model="formData.pwd" placeholder="请输入权限密码" type="password" show-password />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts" name="RechargePackageForm">
import * as RechargeApi from '@/api/mall/rechargeCenter'


const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  amount: undefined,
  giftAmount: 0,
  growValue: 0,
  vipLevel: undefined,
  sort: 0,
  status: 1,
  pwd: undefined
})
const formRules = reactive({
  amount: [{ required: true, message: '充值金额不能为空', trigger: 'blur' }],
  pwd: [{ required: true, message: '请输入权限密码', trigger: 'blur' }]
})
const formRef = ref() // 表单 Ref

/** 打开弹窗 */
const open = (type: string, row?: any) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  // 修改时，回显数据
  if (row) {
    formData.value = { ...row }
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交表单 */
const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调
const submitForm = async () => {
  // 校验表单
  if (!formRef) return
  const valid = await formRef.value.validate()
  if (!valid) return
  // 提交请求
  formLoading.value = true
  try {
    const data = formData.value as unknown as RechargeApi.RechargePackageVO
    if (formType.value === 'create') {
      await RechargeApi.createRechargePackage(data)
      message.success(t('common.createSuccess'))
    } else {
      await RechargeApi.updateRechargePackage(data)
      message.success(t('common.updateSuccess'))
    }
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
const resetForm = () => {
  formData.value = {
    id: undefined,
    amount: undefined,
    giftAmount: 0,
    growValue: 0,
    vipLevel: undefined,
    sort: 0,
    status: 1,
    pwd: undefined
  }
  formRef.value?.resetFields()
}
</script>
