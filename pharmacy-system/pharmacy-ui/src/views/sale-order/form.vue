<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ isEdit ? '编辑销售单' : '新增销售单' }}</span>
          <el-button @click="goBack">返回</el-button>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="120px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="客户" prop="custId">
              <el-select v-model="formData.custId" placeholder="请选择客户" style="width: 100%" clearable>
                <el-option
                  v-for="item in customerList"
                  :key="item.custId"
                  :label="item.custName"
                  :value="item.custId"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="支付方式" prop="payType">
              <el-select v-model="formData.payType" placeholder="请选择支付方式" style="width: 100%">
                <el-option label="现金" value="现金" />
                <el-option label="微信" value="微信" />
                <el-option label="支付宝" value="支付宝" />
                <el-option label="银行卡" value="银行卡" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="订单类型" prop="orderType">
              <el-select v-model="formData.orderType" placeholder="请选择订单类型" style="width: 100%">
                <el-option label="普通订单" :value="1" />
                <el-option label="VIP订单" :value="2" />
              </el-select>
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
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="总金额">
              <el-input v-model="totalPrice" disabled style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="formData.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>

      <el-divider content-position="left">销售明细</el-divider>

      <div class="item-toolbar">
        <el-button type="primary" @click="showItemDialog">添加明细</el-button>
      </div>

      <el-table :data="formData.items" border stripe style="width: 100%">
        <el-table-column prop="medicineName" label="药品名称" width="150" />
        <el-table-column prop="batchNo" label="批号" width="120" />
        <el-table-column prop="quantity" label="数量" width="100" />
        <el-table-column prop="unitPrice" label="单价" width="100">
          <template #default="{ row }">
            {{ Number(row.unitPrice).toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column label="小计" width="100">
          <template #default="{ row }">
            {{ Number(row.totalPrice).toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ $index }">
            <el-button type="danger" size="small" @click="removeItem($index)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="submit-area">
        <el-button type="primary" size="large" @click="handleSubmit" :loading="submitLoading">保存</el-button>
        <el-button size="large" @click="goBack">取消</el-button>
      </div>
    </el-card>

    <el-dialog
      v-model="itemDialogVisible"
      title="添加明细"
      width="600px"
      destroy-on-close
    >
      <el-form
        ref="itemFormRef"
        :model="itemFormData"
        :rules="itemFormRules"
        label-width="100px"
      >
        <el-form-item label="药品" prop="medId">
          <el-select
            v-model="itemFormData.medId"
            placeholder="请选择药品"
            filterable
            style="width: 100%"
            @change="handleMedicineChange"
          >
            <el-option
              v-for="item in medicineList"
              :key="item.medId"
              :label="`${item.medName}（${item.spec || '无规格'}）`"
              :value="item.medId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="批号" prop="batchNo">
          <el-input v-model="itemFormData.batchNo" placeholder="请输入批号（选填）" />
        </el-form-item>
        <el-form-item label="数量" prop="quantity">
          <el-input-number v-model="itemFormData.quantity" :min="1" @change="calculateItemTotal" style="width: 100%" />
        </el-form-item>
        <el-form-item label="单价" prop="unitPrice">
          <el-input-number v-model="itemFormData.unitPrice" :min="0" :precision="2" @change="calculateItemTotal" style="width: 100%" />
        </el-form-item>
        <el-form-item label="小计">
          <el-input :value="Number(itemFormData.totalPrice).toFixed(2)" disabled />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="itemDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="addItem">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createSaleOrder } from '@/api/sale-order'
import { getCustomerList } from '@/api/customer'
import { getMedicineList } from '@/api/medicine'

const router = useRouter()
const route = useRoute()

const formRef = ref(null)
const itemFormRef = ref(null)
const submitLoading = ref(false)
const itemDialogVisible = ref(false)
const customerList = ref([])
const medicineList = ref([])

const isEdit = computed(() => !!route.query.id)

const formData = reactive({
  custId: null,
  payType: '',
  orderType: 1,
  payStatus: 0,
  remark: '',
  items: []
})

const itemFormData = reactive({
  medId: null,
  medicineName: '',
  batchNo: '',
  quantity: 1,
  unitPrice: 0,
  totalPrice: 0
})

const formRules = {
  payType: [{ required: true, message: '请选择支付方式', trigger: 'change' }],
  orderType: [{ required: true, message: '请选择订单类型', trigger: 'change' }]
}

const itemFormRules = {
  medId: [{ required: true, message: '请选择药品', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入数量', trigger: 'blur' }],
  unitPrice: [{ required: true, message: '请输入单价', trigger: 'blur' }]
}

const totalPrice = computed(() => {
  return formData.items.reduce((sum, item) => sum + (parseFloat(item.totalPrice) || 0), 0).toFixed(2)
})

const fetchCustomerList = async () => {
  try {
    const res = await getCustomerList({ current: 1, size: 1000 })
    if (res.code === 200) {
      customerList.value = res.data.records
    }
  } catch (error) {
    ElMessage.error('获取客户列表失败')
  }
}

const fetchMedicineList = async () => {
  try {
    const res = await getMedicineList({ current: 1, size: 1000, status: 1 })
    if (res.code === 200) {
      medicineList.value = res.data.records
    }
  } catch (error) {
    ElMessage.error('获取药品列表失败')
  }
}

const calculateItemTotal = () => {
  itemFormData.totalPrice = (itemFormData.quantity * itemFormData.unitPrice)
}

const handleMedicineChange = (medId) => {
  const medicine = medicineList.value.find(item => item.medId === medId)
  if (!medicine) return
  itemFormData.medicineName = medicine.medName
  itemFormData.unitPrice = Number(medicine.retailPrice || 0)
  calculateItemTotal()
}

const showItemDialog = () => {
  Object.assign(itemFormData, {
    medId: null,
    medicineName: '',
    batchNo: '',
    quantity: 1,
    unitPrice: 0,
    totalPrice: 0
  })
  itemDialogVisible.value = true
}

const addItem = async () => {
  if (!itemFormRef.value) return
  await itemFormRef.value.validate(async (valid) => {
    if (valid) {
      formData.items.push({ ...itemFormData })
      itemDialogVisible.value = false
      ElMessage.success('添加成功')
    }
  })
}

const removeItem = (index) => {
  formData.items.splice(index, 1)
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      if (formData.items.length === 0) {
        ElMessage.warning('请至少添加一条明细')
        return
      }
      submitLoading.value = true
      try {
        const submitData = {
          ...formData,
          totalPrice: parseFloat(totalPrice.value),
          userId: 1
        }
        const res = await createSaleOrder(submitData)
        if (res.code === 200) {
          ElMessage.success('保存成功')
          router.push('/sale-order')
        } else {
          ElMessage.error(res.message || '保存失败')
        }
      } catch (error) {
        ElMessage.error(error?.response?.data?.message || '保存失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const goBack = () => {
  router.push('/sale-order')
}

onMounted(() => {
  fetchCustomerList()
  fetchMedicineList()
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

.item-toolbar {
  margin-bottom: 20px;
}

.submit-area {
  margin-top: 30px;
  text-align: center;
}
</style>
