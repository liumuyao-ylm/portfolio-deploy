<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>销售明细</span>
          <el-tag v-if="currentOrderId" type="info">当前订单ID: {{ currentOrderId }}</el-tag>
        </div>
      </template>

      <div class="table-toolbar">
        <el-button type="primary" @click="handleAdd">新增</el-button>
        <el-button type="danger" :disabled="multipleSelection.length === 0" @click="handleBatchDelete">批量删除</el-button>
        <el-button @click="handleBack" v-if="currentOrderId">返回订单列表</el-button>
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
        <el-table-column prop="itemId" label="明细ID" width="100" />
        <el-table-column prop="orderId" label="订单ID" width="100" />
        <el-table-column prop="medName" label="药品名称" min-width="150" />
        <el-table-column prop="batchNo" label="批号" width="120" />
        <el-table-column prop="quantity" label="销售数量" width="100" />
        <el-table-column prop="unitPrice" label="销售单价" width="120" />
        <el-table-column prop="totalPrice" label="总金额" width="120" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
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
            <el-form-item label="订单ID" prop="orderId">
              <el-input v-model="formData.orderId" placeholder="请输入订单ID" :disabled="!!currentOrderId" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="药品" prop="medId">
              <el-select v-model="formData.medId" placeholder="请选择药品" style="width: 100%">
                <el-option
                  v-for="item in medicineOptions"
                  :key="item.medId"
                  :label="item.medName"
                  :value="item.medId"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="批号" prop="batchNo">
              <el-input v-model="formData.batchNo" placeholder="请输入批号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="销售数量" prop="quantity">
              <el-input v-model="formData.quantity" placeholder="请输入销售数量" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="销售单价" prop="unitPrice">
              <el-input v-model="formData.unitPrice" placeholder="请输入销售单价" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="总金额" prop="totalPrice">
              <el-input v-model="formData.totalPrice" placeholder="请输入总金额" />
            </el-form-item>
          </el-col>
        </el-row>
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
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getSaleItemList, addSaleItem, updateSaleItem, deleteSaleItem } from '@/api/sale-item'
import { getMedicineList } from '@/api/medicine'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const tableData = ref([])
const multipleSelection = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增销售明细')
const formRef = ref(null)
const currentOrderId = ref(null)
const medicineOptions = ref([])

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const formData = reactive({
  itemId: null,
  orderId: null,
  medId: null,
  batchNo: '',
  quantity: null,
  unitPrice: null,
  totalPrice: null
})

const formRules = {
  orderId: [{ required: true, message: '请输入订单ID', trigger: 'blur' }],
  medId: [{ required: true, message: '请选择药品', trigger: 'change' }]
}

const fetchOptions = async () => {
  try {
    const res = await getMedicineList({ current: 1, size: 1000 })
    if (res.code === 200) {
      medicineOptions.value = res.data.records
    }
  } catch (error) {
    ElMessage.error('获取药品列表失败')
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size
    }
    if (currentOrderId.value) {
      params.orderId = currentOrderId.value
    }
    const res = await getSaleItemList(params)
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
  dialogTitle.value = '新增销售明细'
  Object.assign(formData, {
    itemId: null,
    orderId: currentOrderId.value,
    medId: null,
    batchNo: '',
    quantity: null,
    unitPrice: null,
    totalPrice: null
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑销售明细'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = formData.itemId ? await updateSaleItem(formData) : await addSaleItem(formData)
        if (res.code === 200) {
          ElMessage.success(formData.itemId ? '更新成功' : '添加成功')
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
  ElMessageBox.confirm('确定要删除该销售明细吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deleteSaleItem(row.itemId)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        fetchData()
      }
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const handleBatchDelete = () => {
  ElMessageBox.confirm(`确定要删除选中的 ${multipleSelection.value.length} 条记录吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      for (const item of multipleSelection.value) {
        await deleteSaleItem(item.itemId)
      }
      ElMessage.success('批量删除成功')
      fetchData()
    } catch (error) {
      ElMessage.error('批量删除失败')
    }
  }).catch(() => {})
}

const handleBack = () => {
  router.push('/sale-order')
}

onMounted(() => {
  fetchOptions()
  if (route.query.order_id) {
    currentOrderId.value = parseInt(route.query.order_id)
  }
  fetchData()
})
</script>

<style scoped>
.page-container {
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.table-toolbar {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
}

.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>
