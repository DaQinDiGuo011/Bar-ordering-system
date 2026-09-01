<template>
  <div class="page-container">
    <el-card shadow="never">
      <el-form :model="queryForm" inline>
        <el-form-item label="密码类型编码">
          <el-input v-model="queryForm.passwordType" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
          <el-button type="success" @click="openDialog()">新增配置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" border style="margin-top:10px" v-loading="loading">
        <el-table-column label="ID" prop="id" width="80" />
        <el-table-column label="密码类型编码" prop="passwordType" />
        <el-table-column label="类型名称" prop="passwordName" />
        <el-table-column label="密码/密钥值" prop="passwordValue" show-overflow-tooltip />
        <el-table-column label="是否生效" prop="enabled">
          <template #default="scope">
            <el-tag :type="scope.row.enabled ===1 ? 'success' : 'danger'">
              {{ scope.row.enabled ===1 ? '生效' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="备注" prop="remark" />
        <el-table-column label="创建时间" prop="createTime" width="180" />
        <el-table-column label="创建人" prop="creator" width="120" />
        <el-table-column label="更新时间" prop="updateTime" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button link type="primary" @click="openDialog(scope.row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="page.pageNum"
        v-model:page-size="page.pageSize"
        :total="page.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadData"
        @current-change="loadData"
        style="margin-top:12px"
      />
    </el-card>

    <!-- 弹窗新增/编辑 -->
    <el-dialog v-model="dialogVisible" title="密码密钥配置" width="650px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="密码类型编码" prop="passwordType">
          <el-input v-model="form.passwordType" placeholder="例如：API_KEY、EXCHANGE_PWD" :disabled="!!form.id"/>
        </el-form-item>
        <el-form-item label="类型展示名称" prop="passwordName">
          <el-input v-model="form.passwordName" placeholder="如：接口访问密钥"/>
        </el-form-item>
        <el-form-item label="密码/密钥值" prop="passwordValue">
          <el-input v-model="form.passwordValue" type="textarea" :rows="4" placeholder="填写密码或者密钥"/>
        </el-form-item>
        <el-form-item label="是否生效" prop="enabled">
          <el-radio-group v-model="form.enabled">
            <el-radio :value="1" label="生效"/>
            <el-radio :value="0" label="禁用"/>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref,reactive,onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getListByPage,createParam,updateParam } from '@/api/system/busPermis'

const loading = ref(false)
const dialogVisible = ref(false)
const formRef = ref(null)

const queryForm = reactive({
  passwordType: ''
})

const page = reactive({
  pageNum:1,
  pageSize:10,
  total:0
})
const tableData = ref([])

const form = reactive({
  id: null,
  passwordType:'',
  passwordName:'',
  passwordValue:'',
  enabled:1,
  remark:''
})

const rules = {
  passwordType: [{required:true,message:'请输入密码类型编码',trigger:'blur'}],
  passwordName: [{required:true,message:'请输入类型名称',trigger:'blur'}],
  passwordValue: [{required:true,message:'请填写密码/密钥值',trigger:'blur'}],
  enabled: [{required:true,message:'请选择是否生效',trigger:'change'}]
}

//加载列表
async function loadData(){
  loading.value = true
  const res = await getListByPage({
    pageNum: page.pageNum,
    pageSize: page.pageSize,
    ...queryForm
  })
  tableData.value = res.list
  page.total = res.total
  loading.value = false
}

function resetQuery(){
  queryForm.passwordType = ''
  page.pageNum =1
  loadData()
}

//打开弹窗
function openDialog(row = null){
  formRef.value?.resetFields()
  if(row){
    Object.assign(form,row)
  }else{
    form.id = null
    form.passwordType = ''
    form.passwordName = ''
    form.passwordValue = ''
    form.enabled =1
    form.remark = ''
  }
  dialogVisible.value = true
}

//提交
async function submitForm(){
  await formRef.value.validate()
  if(form.id){
    await updateParam(form)
  }else{
    await createParam(form)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  loadData()
}


onMounted(()=>{
  loadData()
})
</script>