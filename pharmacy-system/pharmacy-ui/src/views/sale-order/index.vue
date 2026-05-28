<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>销售订单</span>
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
        <el-table-column prop="orderId" label="订单ID" width="100" />
        <el-table-column prop="custName" label="客户" min-width="140" />
        <el-table-column prop="userRealName" label="操作员" width="120" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column prop="totalPrice" label="总金额" width="120" />
        <el-table-column prop="payType" label="支付方式" width="100" />
        <el-table-column prop="orderType" label="订单类型" width="100" />
        <el-table-column prop="payStatus" label="支付状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.payStatus === 1 ? 'success' : 'danger'">
              {{ row.payStatus === 1 ? '已支付' : '待支付' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="success" size="small" :disabled="row.payStatus === 1" @click="handleOutbound(row)">出库</el-button>
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
            <el-form-item label="客户" prop="custId">
              <el-select v-model="formData.custId" placeholder="请选择客户" style="width: 100%">
                <el-option
                  v-for="item in customerOptions"
                  :key="item.custId"
                  :label="item.custName"
                  :value="item.custId"
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
            <el-form-item label="总金额" prop="totalPrice">
              <el-input v-model="formData.totalPrice" placeholder="请输入总金额" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="支付方式" prop="payType">
              <el-input v-model="formData.payType" placeholder="请输入支付方式" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="订单类型" prop="orderType">
              <el-input v-model="formData.orderType" placeholder="请输入订单类型" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="支付状态" prop="payStatus">
              <el-radio-group v-model="formData.payStatus">
                <el-radio :label="1">已支付</el-radio>
                <el-radio :label="0">待支付</el-radio>
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
import { getSaleOrderList, addSaleOrder, updateSaleOrder, deleteSaleOrder, batchDeleteSaleOrder, outboundSaleOrder, exportSaleOrder } from '@/api/sale-order'
import { getCustomerList } from '@/api/customer'
import { getUserList } from '@/api/user'
import { downloadBlob } from '@/utils/download'

const router = useRouter()
const loading = ref(false)
const tableData = ref([])
const multipleSelection = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增销售订单')
const formRef = ref(null)
const customerOptions = ref([])
const userOptions = ref([])

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const formData = reactive({
  orderId: null,
  custId: null,
  userId: null,
  totalPrice: null,
  payType: '',
  orderType: null,
  payStatus: 0,
  remark: ''
})

const formRules = {
  custId: [{ required: true, message: '请选择客户', trigger: 'change' }],
  userId: [{ required: true, message: '请选择操作员', trigger: 'change' }]
}

const fetchOptions = async () => {
  try {
    const [customerRes, userRes] = await Promise.all([
      getCustomerList({ current: 1, size: 1000 }),
      getUserList({ current: 1, size: 1000 })
    ])
    if (customerRes.code === 200) {
      customerOptions.value = customerRes.data.records
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
    const res = await getSaleOrderList({
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
  router.push('/sale-order/form')
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑销售订单'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = formData.orderId ? await updateSaleOrder(formData) : await addSaleOrder(formData)
        if (res.code === 200) {
          ElMessage.success(formData.orderId ? '更新成功' : '添加成功')
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
  ElMessageBox.confirm('确定要删除该销售订单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deleteSaleOrder(row.orderId)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        fetchData()
      }
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const handleOutbound = (row) => {
  ElMessageBox.confirm('确认将该销售单出库吗？系统会校验库存并自动扣减，且不能重复出库。', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await outboundSaleOrder(row.orderId)
      if (res.code === 200) {
        ElMessage.success('出库成功')
        fetchData()
      } else {
        ElMessage.error(res.message || '出库失败')
      }
    } catch (error) {
      ElMessage.error(error?.response?.data?.message || '出库失败')
    }
  }).catch(() => {})
}

const handleExport = async () => {
  try {
    const blob = await exportSaleOrder()
    downloadBlob(blob, '销售订单数据.xlsx')
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
      const ids = multipleSelection.value.map(item => item.orderId)
      const res = await batchDeleteSaleOrder(ids)
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
    path: '/sale-item',
    query: { order_id: row.orderId }
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
