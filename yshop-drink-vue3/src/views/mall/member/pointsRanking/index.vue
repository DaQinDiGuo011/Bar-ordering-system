<template>
  <ContentWrap>
    <!-- 无搜索条件，直接展示排行榜 -->
    <el-card shadow="never">
      <template #header>
        <div class="text-lg font-medium">积分排行榜</div>
      </template>

      <el-table v-loading="loading" :data="list" border>
        <el-table-column label="排行名次" align="center" prop="rankNo" width="120">
          <template #default="scope">
            <el-tag v-if="scope.row.rankNo === 1" type="danger">第{{ scope.row.rankNo }}名</el-tag>
            <el-tag v-else-if="scope.row.rankNo === 2" type="warning">第{{ scope.row.rankNo }}名</el-tag>
            <el-tag v-else-if="scope.row.rankNo === 3" type="success">第{{ scope.row.rankNo }}名</el-tag>
            <span v-else>{{ scope.row.rankNo }}</span>
          </template>
        </el-table-column>

        <el-table-column label="用户头像" align="center" prop="avatar" width="120">
          <template #default="scope">
            <el-image
              style="width: 50px; height: 50px; border-radius: 50%;"
              :src="scope.row.avatar"
              fit="cover"
            />
          </template>
        </el-table-column>

        <el-table-column label="用户昵称" align="center" prop="nickname" />
        <el-table-column label="手机号码" align="center" prop="mobile" />
        <el-table-column label="剩余积分" align="center" prop="integral" />
      </el-table>

      
    </el-card>
  </ContentWrap>
</template>

<script setup lang="ts" name="IntegralRank">
import * as UserApi from '@/api/member/user'

const loading = ref(true)
const total = ref(0)
const list = ref<any[]>([])

/** 查询积分排行榜分页 */
const getList = async () => {
  loading.value = true
  try {
    const data = await UserApi.getIntegralRankPage()
    list.value = data
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  getList()
})
</script>