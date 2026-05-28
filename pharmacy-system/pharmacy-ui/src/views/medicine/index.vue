<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>药品管理</span>
        </div>
      </template>

      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="药品名称">
          <el-input v-model="queryForm.medName" placeholder="请输入药品名称" clearable />
        </el-form-item>
        <el-form-item label="药品类型">
          <el-select v-model="queryForm.medType" placeholder="请选择药品类型" clearable style="width: 150px">
            <el-option label="中药" value="中药" />
            <el-option label="西药" value="西药" />
            <el-option label="器械" value="器械" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable style="width: 120px">
            <el-option label="在售" :value="1" />
            <el-option label="停售" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="table-toolbar">
        <el-button type="primary" @click="handleAdd">新增</el-button>
        <el-button type="success" @click="handleExport">导出Excel</el-button>
        <el-button @click="handleDownloadTemplate">下载模板</el-button>
        <el-button @click="triggerImport">导入Excel</el-button>
        <input
          ref="importInputRef"
          type="file"
          accept=".xlsx,.xls"
          style="display: none"
          @change="handleImportChange"
        />
        <el-button type="danger" :disabled="multipleSelection.length === 0" @click="handleBatchDelete">批量删除</el-button>
      </div>

      <el-table
        v-loading="loading"
        :data="tableData"
        @selection-change="handleSelectionChange"
        border
        stripe
        style="width: 100%"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="medId" label="ID" width="80" />
        <el-table-column prop="medName" label="药品名称" min-width="150" />
        <el-table-column prop="medAlias" label="别名" width="120" />
        <el-table-column prop="medType" label="类型" width="100" />
        <el-table-column prop="spec" label="规格" width="120" />
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column prop="dosageForm" label="剂型" width="100" />
        <el-table-column prop="manufacturer" label="生产厂家" min-width="150" />
        <el-table-column prop="retailPrice" label="零售价" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '在售' : '停售' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="fetchData"
        @current-change="fetchData"
      />
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="900px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="药品名称" prop="medName">
              <el-input v-model="formData.medName" placeholder="请输入药品名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="别名" prop="medAlias">
              <el-input v-model="formData.medAlias" placeholder="请输入别名" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="药品类型" prop="medType">
              <el-select v-model="formData.medType" placeholder="请选择药品类型" style="width: 100%">
                <el-option label="中药" value="中药" />
                <el-option label="西药" value="西药" />
                <el-option label="器械" value="器械" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="规格" prop="spec">
              <el-input v-model="formData.spec" placeholder="请输入规格" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="单位" prop="unit">
              <el-input v-model="formData.unit" placeholder="请输入单位" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="剂型" prop="dosageForm">
              <el-input v-model="formData.dosageForm" placeholder="请输入剂型" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="生产厂家" prop="manufacturer">
              <el-input v-model="formData.manufacturer" placeholder="请输入生产厂家" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="批准文号" prop="approvalNo">
              <el-input v-model="formData.approvalNo" placeholder="请输入批准文号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="零售价" prop="retailPrice">
              <el-input-number v-model="formData.retailPrice" :min="0" :precision="2" :step="0.1" style="width: 100%" placeholder="零售价" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="采购价" prop="purchasePrice">
              <el-input-number v-model="formData.purchasePrice" :min="0" :precision="2" :step="0.1" style="width: 100%" placeholder="采购价" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="是否处方药" prop="isRx">
              <el-radio-group v-model="formData.isRx">
                <el-radio :label="1">是</el-radio>
                <el-radio :label="0">否</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="formData.status">
                <el-radio :label="1">在售</el-radio>
                <el-radio :label="0">停售</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="formData.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMedicineList, addMedicine, updateMedicine, deleteMedicine, exportMedicine, downloadMedicineTemplate, importMedicine } from '@/api/medicine'
import { downloadBlob } from '@/utils/download'

const loading = ref(false)
const tableData = ref([])
const multipleSelection = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增药品')
const formRef = ref(null)
const importInputRef = ref(null)

const queryForm = reactive({
  medName: '',
  medType: '',
  status: null
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const formData = reactive({
  medId: null,
  medName: '',
  medAlias: '',
  medType: '',
  spec: '',
  unit: '',
  dosageForm: '',
  manufacturer: '',
  approvalNo: '',
  retailPrice: 0,
  purchasePrice: 0,
  isRx: 0,
  status: 1,
  remark: ''
})

const formRules = {
  medName: [{ required: true, message: '请输入药品名称', trigger: 'blur' }],
  medType: [{ required: true, message: '请选择药品类型', trigger: 'change' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size
    }
    if (queryForm.medName) {
      params.medName = queryForm.medName
    }
    if (queryForm.medType) {
      params.medType = queryForm.medType
    }
    // 处理 status 参数，直接传递数字（包括0）
    if (queryForm.status !== null && queryForm.status !== undefined) {
      params.status = queryForm.status
    }
    console.log('请求参数:', params)
    const res = await getMedicineList(params)
    if (res.code === 200) {
      tableData.value = res.data.records
      pagination.total = res.data.total
    }
  } catch (error) {
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  pagination.current = 1
  fetchData()
}

const handleReset = () => {
  queryForm.medName = ''
  queryForm.medType = ''
  queryForm.status = null
  handleQuery()
}

const handleSelectionChange = (selection) => {
  multipleSelection.value = selection
}

const handleAdd = () => {
  dialogTitle.value = '新增药品'
  Object.assign(formData, {
    medId: null,
    medName: '',
    medAlias: '',
    medType: '',
    spec: '',
    unit: '',
    dosageForm: '',
    manufacturer: '',
    approvalNo: '',
    retailPrice: 0,
    purchasePrice: 0,
    isRx: 0,
    status: 1,
    remark: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑药品'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = formData.medId ? await updateMedicine(formData) : await addMedicine(formData)
        if (res.code === 200) {
          ElMessage.success(formData.medId ? '更新成功' : '添加成功')
          dialogVisible.value = false
          fetchData()
        }
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该药品吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deleteMedicine(row.medId)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        fetchData()
      }
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const handleExport = async () => {
  try {
    const params = {}
    if (queryForm.medName) params.medName = queryForm.medName
    if (queryForm.medType) params.medType = queryForm.medType
    if (queryForm.status !== null && queryForm.status !== undefined) params.status = queryForm.status
    const blob = await exportMedicine(params)
    downloadBlob(blob, '药品数据.xlsx')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

const handleDownloadTemplate = async () => {
  try {
    const blob = await downloadMedicineTemplate()
    downloadBlob(blob, '药品导入模板.xlsx')
  } catch (error) {
    ElMessage.error('模板下载失败')
  }
}

const triggerImport = () => {
  importInputRef.value?.click()
}

const handleImportChange = async (event) => {
  const file = event.target.files?.[0]
  event.target.value = ''
  if (!file) return
  if (!/\.(xlsx|xls)$/i.test(file.name)) {
    ElMessage.warning('请选择 .xlsx 或 .xls 文件')
    return
  }
  try {
    const res = await importMedicine(file)
    if (res.code === 200) {
      const data = res.data || {}
      const errors = data.errors || []
      if (data.failCount > 0) {
        ElMessageBox.alert(errors.join('\n'), `导入完成：成功 ${data.successCount} 条，失败 ${data.failCount} 条`, {
          type: 'warning'
        })
      } else {
        ElMessage.success(`导入成功：${data.successCount} 条`)
      }
      fetchData()
    } else {
      ElMessage.error(res.message || '导入失败')
    }
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '导入失败')
  }
}

const handleBatchDelete = () => {
  ElMessageBox.confirm(`确定要删除选中的 ${multipleSelection.value.length} 条记录吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      for (const item of multipleSelection.value) {
        await deleteMedicine(item.medId)
      }
      ElMessage.success('批量删除成功')
      fetchData()
    } catch (error) {
      ElMessage.error('批量删除失败')
    }
  }).catch(() => {})
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.page-container {
  width: 100%;
}

.search-form {
  margin-bottom: 20px;
}

.table-toolbar {
  margin-bottom: 20px;
}

.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>
