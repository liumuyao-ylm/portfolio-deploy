<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>采购订单</span>
        </div>
      </template>

      <div class="table-toolbar">
        <el-button type="primary" @click="handleAdd">新增</el-button>
        <el-button type="success" @click="handleExport">导出Excel</el-button>
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
        <el-table-column prop="purchaseId" label="订单ID" width="100" />
        <el-table-column prop="supplierName" label="供应商" min-width="160" />
        <el-table-column prop="userRealName" label="操作员" width="120" />
        <el-table-column prop="purchaseTime" label="采购时间" width="180" />
        <el-table-column prop="totalAmount" label="总金额" width="120" />
        <el-table-column prop="payType" label="支付方式" width="100" />
        <el-table-column prop="purchaseStatus" label="采购状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.purchaseStatus === 1 ? 'success' : 'danger'">
              {{ row.purchaseStatus === 1 ? '完成' : '待处理' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="success" size="small" :disabled="row.purchaseStatus === 1" @click="handleInbound(row)">入库</el-button>
            <el-button type="primary" size="small" @click="handleViewDetail(row)">查看明细</el-button>
            <el-button type="warning" size="small" @click="handleEdit(row)">编辑</el-button>
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
      width="800px"
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
            <el-form-item label="供应商" prop="supplierId">
              <el-select v-model="formData.supplierId" placeholder="请选择供应商" style="width: 100%">
                <el-option
                  v-for="item in supplierOptions"
                  :key="item.supplierId"
                  :label="item.supplierName"
                  :value="item.supplierId"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="操作员" prop="userId">
              <el-select v-model="formData.userId" placeholder="请选择操作员" style="width: 100%">
                <el-option
                  v-for="item in userOptions"
                  :key="item.userId"
                  :label="item.realName"
                  :value="item.userId"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="采购时间" prop="purchaseTime">
              <el-date-picker
                v-model="formData.purchaseTime"
                type="datetime"
                placeholder="请选择采购时间"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="总金额" prop="totalAmount">
              <el-input v-model="formData.totalAmount" placeholder="请输入总金额" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="支付方式" prop="payType">
              <el-input v-model="formData.payType" placeholder="请输入支付方式" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="采购状态" prop="purchaseStatus">
              <el-radio-group v-model="formData.purchaseStatus">
                <el-radio :label="1">完成</el-radio>
                <el-radio :label="0">待处理</el-radio>
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
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPurchaseOrderList, addPurchaseOrder, updatePurchaseOrder, deletePurchaseOrder, batchDeletePurchaseOrder, inboundPurchaseOrder, exportPurchaseOrder } from '@/api/purchase-order'
import { getSupplierList } from '@/api/supplier'
import { getUserList } from '@/api/user'
import { downloadBlob } from '@/utils/download'

const router = useRouter()
const loading = ref(false)
const tableData = ref([])
const multipleSelection = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增采购订单')
const formRef = ref(null)
const supplierOptions = ref([])
const userOptions = ref([])

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const formData = reactive({
  purchaseId: null,
  supplierId: null,
  userId: null,
  purchaseTime: null,
  totalAmount: null,
  payType: '',
  purchaseStatus: 0,
  remark: ''
})

const formRules = {
  supplierId: [{ required: true, message: '请选择供应商', trigger: 'change' }],
  userId: [{ required: true, message: '请选择操作员', trigger: 'change' }]
}

const fetchOptions = async () => {
  try {
    const [supplierRes, userRes] = await Promise.all([
      getSupplierList({ current: 1, size: 1000 }),
      getUserList({ current: 1, size: 1000 })
    ])
    if (supplierRes.code === 200) {
      supplierOptions.value = supplierRes.data.records
    }
    if (userRes.code === 200) {
      userOptions.value = userRes.data.records
    }
  } catch (error) {
    ElMessage.error('获取基础数据失败')
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getPurchaseOrderList({
      current: pagination.current,
      size: pagination.size
    })
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

const handleSelectionChange = (selection) => {
  multipleSelection.value = selection
}

const handleAdd = () => {
  router.push('/purchase-order/form')
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑采购订单'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = formData.purchaseId ? await updatePurchaseOrder(formData) : await addPurchaseOrder(formData)
        if (res.code === 200) {
          ElMessage.success(formData.purchaseId ? '更新成功' : '添加成功')
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
  ElMessageBox.confirm('确定要删除该采购订单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deletePurchaseOrder(row.purchaseId)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        fetchData()
      }
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const handleInbound = (row) => {
  ElMessageBox.confirm('确认将该采购单入库吗？入库后会自动增加库存，且不能重复入库。', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await inboundPurchaseOrder(row.purchaseId)
      if (res.code === 200) {
        ElMessage.success('入库成功')
        fetchData()
      } else {
        ElMessage.error(res.message || '入库失败')
      }
    } catch (error) {
      ElMessage.error(error?.response?.data?.message || '入库失败')
    }
  }).catch(() => {})
}

const handleExport = async () => {
  try {
    const blob = await exportPurchaseOrder()
    downloadBlob(blob, '采购订单数据.xlsx')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

const handleBatchDelete = () => {
  ElMessageBox.confirm(`确定要删除选中的 ${multipleSelection.value.length} 条记录吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const ids = multipleSelection.value.map(item => item.purchaseId)
      const res = await batchDeletePurchaseOrder(ids)
      if (res.code === 200) {
        ElMessage.success('批量删除成功')
        fetchData()
      }
    } catch (error) {
      ElMessage.error('批量删除失败')
    }
  }).catch(() => {})
}

const handleViewDetail = (row) => {
  router.push({
    path: '/purchase-item',
    query: { purchase_id: row.purchaseId }
  })
}

onMounted(() => {
  fetchOptions()
  fetchData()
})
</script>

<style scoped>
.page-container {
  width: 100%;
}

.table-toolbar {
  margin-bottom: 20px;
}

.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>
