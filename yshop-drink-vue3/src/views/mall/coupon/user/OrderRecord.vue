<template>
  <el-drawer v-model="drawer" :title="dialogTitle" size="80%">
    <el-table :data="tableData" style="width: 100%">
      <el-table-column label="id" align="center" prop="id" />
      <el-table-column label="店铺名称" align="center" prop="shopName" width="120" />
      <el-table-column label="优惠券名称" align="center" prop="title" width="150" />
      <el-table-column label="消费多少可用" align="center" prop="least" width="120" />
      <el-table-column label="优惠券金额" align="center" prop="value" width="110" />
      <el-table-column label="可用类型" align="center" prop="type">
        <template #default="scope">
          <span v-if="scope.row.type == 1">自取</span>
          <span v-else-if="scope.row.type == 2">外卖</span>
          <span v-else>通用</span>
         </template>
      </el-table-column>
      <el-table-column label="用户昵称" align="center" prop="userId" />
      <el-table-column label="是否使用" align="center" prop="status">
        <template #default="scope">
          <span v-if="scope.row.status == 1">已使用</span>
          <span v-if="scope.row.status == 0">未使用</span>
         </template>
      </el-table-column>
      <el-table-column
        label="领取时间"
        align="center"
        prop="createTime"
        :formatter="dateFormatter"
        width="170"
      />
      <el-table-column label="操作" align="center">
        <template #default="scope">
          <el-button
            v-if="scope.row.status == 0"
            link
            type="danger"
            @click="handleDelete(scope.row)"
          >
            移除
          </el-button>
          <el-button
            v-if="scope.row.status == 0"
            link
            type="primary"
            @click="verification(scope.row)"
            
          >
            核销
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-drawer>

</template>
<script setup lang="ts">
import * as UserApi from '@/api/mall/coupon/user'
import { dateFormatter } from '@/utils/formatTime'
import { ElMessage } from 'element-plus'
const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗
const dialogTitle = ref('') // 弹窗的标题
const drawer = ref(false)
const tableData = ref([])
/** 打开弹窗 */
const open = async (type: string, id?: number) => {
  drawer.value = true
  dialogTitle.value = t('action.' + type)
  console.log(id)
  getList(id)
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 删除按钮操作 */
const handleDelete = async (data) => {
  try {
    console.log("data-------",data)
    if(data.status == 1){
      ElMessage.warning('该优惠券已使用，不能核销')
      return
    }
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await UserApi.deleteUser(data.id)
    message.success(t('common.delSuccess'))
    // 刷新列表
   getList(data.couponId)
  } catch {}
}
const verification = async (data) => {
  try {
    console.log("data-------",data)
    if(data.status == 1){
      ElMessage.warning('该优惠券已使用，不能移除')
      return
    }
    
    // 删除的二次确认
    await message.confirm('确认要核销该优惠券')
    let param = Object.assign({}, data)
    param.status = 1
    // 发起删除
    await UserApi.updateUser(param)
    message.success(t('common.updateSuccess'))
    // 刷新列表
    getList(data.couponId)
  } catch {}
}
const getList = async(id) => {
  tableData.value = await UserApi.getUserList(id)
}
</script>
<style scoped>
</style>