<template>
  <ContentWrap>
    <el-form
      class="-mb-15px"
      :model="queryParams"
      ref="queryFormRef"
      :inline="true"
      label-width="80px"
    >
      <el-form-item label="订单号" prop="orderNo">
        <el-input
          v-model="queryParams.orderNo"
          placeholder="请输入订单号"
          clearable
          @keyup.enter="handleQuery"
          class="!w-220px"
        />
      </el-form-item>
      <el-form-item label="手机号" prop="mobile">
        <el-input
          v-model="queryParams.mobile"
          placeholder="请输入手机号"
          clearable
          @keyup.enter="handleQuery"
          class="!w-200px"
        />
      </el-form-item>
      <el-form-item label="支付状态" prop="payStatus">
        <el-select
          v-model="queryParams.payStatus"
          placeholder="请选择支付状态"
          clearable
          class="!w-160px"
        >
          <el-option label="待支付" :value="0" />
          <el-option label="已支付" :value="1" />
        </el-select>
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker
          v-model="queryParams.createTime"
          type="datetimerange"
          value-format="YYYY-MM-DD HH:mm:ss"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
        />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list">
      <el-table-column label="编号" align="center" prop="id" width="80" />
      <el-table-column label="订单号" align="center" prop="orderNo" min-width="180" />
      <el-table-column label="用户昵称" align="center" prop="nickname" min-width="120" />
      <el-table-column label="手机号" align="center" prop="mobile" min-width="120" />
      <el-table-column label="充值金额" align="center" prop="rechargeAmount" width="100" />
      <el-table-column label="赠送金额" align="center" prop="giftAmount" width="100" />
      <el-table-column label="赠送成长值" align="center" prop="giftGrowValue" width="100" />
      <el-table-column label="支付状态" align="center" prop="payStatus" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.payStatus === 1 ? 'success' : 'warning'">
            {{ scope.row.payStatus === 1 ? '已支付' : '待支付' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column
        label="支付时间"
        align="center"
        prop="payTime"
        :formatter="dateFormatter"
        min-width="160"
      />
      <el-table-column
        label="创建时间"
        align="center"
        prop="createTime"
        :formatter="dateFormatter"
        min-width="160"
      />
    </el-table>
    <Pagination
      :total="total"
      v-model:page="queryParams.pageNo"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </ContentWrap>
</template>

<script setup lang="ts" name="RechargeList">
import { dateFormatter } from '@/utils/formatTime'
import * as RechargeApi from '@/api/mall/rechargeCenter'


const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总数量
const list = ref([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  orderNo: undefined,
  mobile: undefined,
  payStatus: undefined,
  createTime: []
})
const queryFormRef = ref() // 搜索的表单

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await RechargeApi.getRechargeOrderPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value.resetFields()
  handleQuery()
}

onMounted(() => {
  getList()
})
</script>
