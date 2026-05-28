export const storeInfo = {
  name: '安心乡镇药房',
  status: '营业中',
  hours: '08:00 - 21:30',
  phone: '0312-6688120',
  address: '河北省保定市清苑区安心镇中心街 26 号',
  scope: '中成药、化学药制剂、抗生素制剂、生化药品、医疗器械、保健用品',
  notice: '处方药请凭处方购买，特殊用药建议先咨询店内药师。'
}

export const categories = [
  '感冒发热',
  '肠胃用药',
  '外伤消毒',
  '慢病常备',
  '医疗器械'
]

export const medicines = [
  {
    id: 1,
    name: '复方氨酚烷胺胶囊',
    category: '感冒发热',
    type: '西药',
    dosage: '胶囊剂',
    spec: '10 粒/盒',
    price: 18.5,
    manufacturer: '华北制药股份有限公司',
    prescription: false,
    stockStatus: '有货',
    stockCount: 42,
    usage: '用于缓解普通感冒及流行性感冒引起的发热、头痛、鼻塞等症状。'
  },
  {
    id: 2,
    name: '布洛芬缓释胶囊',
    category: '感冒发热',
    type: '西药',
    dosage: '胶囊剂',
    spec: '0.3g*20 粒/盒',
    price: 26,
    manufacturer: '中美天津史克制药有限公司',
    prescription: false,
    stockStatus: '有货',
    stockCount: 25,
    usage: '用于缓解轻至中度疼痛，也可用于普通感冒或流感引起的发热。'
  },
  {
    id: 3,
    name: '阿莫西林胶囊',
    category: '常用抗菌',
    type: '抗生素',
    dosage: '胶囊剂',
    spec: '0.25g*24 粒/盒',
    price: 19.8,
    manufacturer: '石药集团中诺药业',
    prescription: true,
    stockStatus: '库存较少',
    stockCount: 6,
    usage: '适用于敏感菌所致感染，须凭处方并遵医嘱使用。'
  },
  {
    id: 4,
    name: '蒙脱石散',
    category: '肠胃用药',
    type: '西药',
    dosage: '散剂',
    spec: '3g*10 袋/盒',
    price: 16.8,
    manufacturer: '先声药业有限公司',
    prescription: false,
    stockStatus: '有货',
    stockCount: 31,
    usage: '用于成人及儿童急、慢性腹泻的辅助治疗。'
  },
  {
    id: 5,
    name: '碘伏消毒液',
    category: '外伤消毒',
    type: '消毒用品',
    dosage: '外用液体',
    spec: '100ml/瓶',
    price: 9.9,
    manufacturer: '山东利尔康医疗科技',
    prescription: false,
    stockStatus: '有货',
    stockCount: 58,
    usage: '用于皮肤、黏膜及小面积创面消毒。'
  },
  {
    id: 6,
    name: '硝苯地平控释片',
    category: '慢病常备',
    type: '西药',
    dosage: '片剂',
    spec: '30mg*7 片/盒',
    price: 32.5,
    manufacturer: '拜耳医药保健有限公司',
    prescription: true,
    stockStatus: '库存较少',
    stockCount: 4,
    usage: '用于高血压、冠心病等慢病治疗，须凭处方购买。'
  }
]

export const memberInfo = {
  name: '李先生',
  phone: '138****6721',
  level: '银卡会员',
  totalAmount: 1268.5,
  status: '正常'
}

export const consumptionRecords = [
  { id: 'S20260501001', date: '2026-05-01', item: '家庭常备药品', amount: 86.5, store: '安心乡镇药房' },
  { id: 'S20260418003', date: '2026-04-18', item: '慢病处方购药', amount: 215, store: '安心乡镇药房' },
  { id: 'S20260402008', date: '2026-04-02', item: '外伤消毒用品', amount: 39.8, store: '安心乡镇药房' }
]

export const chatMessages = [
  { role: 'ai', text: '您好，我是药店智能服务助手。可以帮您查询药品、营业时间和到店购药注意事项。' },
  { role: 'user', text: '感冒发热可以先看哪些常备药？' },
  { role: 'ai', text: '可先查看感冒发热分类，如复方氨酚烷胺胶囊、布洛芬缓释胶囊等。若持续高热或症状加重，请及时就医或咨询药师。' }
]
