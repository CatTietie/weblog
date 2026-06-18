<template>
  <div>
    <!-- 敏感词管理 -->
    <el-card shadow="never" class="mb-4">
      <div class="flex justify-between items-center mb-4">
        <div class="flex items-center gap-2">
          <el-input v-model="searchKeyword" placeholder="搜索敏感词" clearable style="width: 200px"
            @keyup.enter="fetchWordList" />
          <el-button type="primary" @click="fetchWordList">查询</el-button>
          <el-button @click="searchKeyword = ''; fetchWordList()">重置</el-button>
        </div>
        <div class="flex gap-2">
          <el-button type="primary" @click="showAddDialog = true">添加敏感词</el-button>
          <el-button type="warning" @click="showImportDialog = true">批量导入</el-button>
          <el-button type="danger" @click="handleStartScan">全库扫描</el-button>
        </div>
      </div>

      <el-table :data="wordList" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="word" label="敏感词" />
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button type="danger" size="small" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="mt-4 flex justify-end">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :total="total"
          layout="total, prev, pager, next" @current-change="fetchWordList" />
      </div>
    </el-card>

    <!-- 扫描任务区 -->
    <el-card shadow="never">
      <template #header>
        <span class="font-bold">扫描任务</span>
      </template>

      <el-table :data="scanTasks" border stripe>
        <el-table-column prop="id" label="任务ID" width="80" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'warning' : row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 0 ? '进行中' : row.status === 1 ? '已完成' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="进度" width="200">
          <template #default="{ row }">
            <el-progress :percentage="getProgress(row)" :status="row.status === 1 ? 'success' : ''" />
          </template>
        </el-table-column>
        <el-table-column prop="hitCount" label="命中数" width="80" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column prop="finishTime" label="完成时间" width="180" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="viewResults(row.id)">查看结果</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 扫描结果弹窗 -->
    <el-dialog v-model="showResultDialog" title="扫描结果" width="80%" destroy-on-close>
      <div class="mb-4">
        <el-radio-group v-model="resultFilter" @change="fetchResults">
          <el-radio-button :label="null">全部</el-radio-button>
          <el-radio-button :label="0">未处理</el-radio-button>
          <el-radio-button :label="1">已忽略</el-radio-button>
          <el-radio-button :label="2">已删除</el-radio-button>
        </el-radio-group>
        <el-button type="warning" class="ml-4" :disabled="selectedResults.length === 0"
          @click="batchHandle(1)">批量忽略</el-button>
        <el-button type="danger" class="ml-2" :disabled="selectedResults.length === 0"
          @click="batchHandle(2)">批量删除</el-button>
      </div>

      <el-table :data="scanResults" border stripe @selection-change="onSelectionChange">
        <el-table-column type="selection" width="50" />
        <el-table-column label="类型" width="100">
          <template #default="{ row }">
            {{ row.targetType === 1 ? '文章标题' : row.targetType === 2 ? '文章正文' : '评论' }}
          </template>
        </el-table-column>
        <el-table-column prop="targetTitle" label="内容标识" show-overflow-tooltip />
        <el-table-column prop="hitWords" label="命中词" width="150" />
        <el-table-column prop="context" label="上下文" show-overflow-tooltip />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.handled === 0 ? 'warning' : row.handled === 1 ? 'info' : 'danger'" size="small">
              {{ row.handled === 0 ? '未处理' : row.handled === 1 ? '已忽略' : '已删除' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <template v-if="row.handled === 0">
              <el-button type="info" size="small" @click="batchHandle(1, [row.id])">忽略</el-button>
              <el-button type="danger" size="small" @click="batchHandle(2, [row.id])">删除</el-button>
            </template>
            <span v-else class="text-gray-400">已处理</span>
          </template>
        </el-table-column>
      </el-table>

      <div class="mt-4 flex justify-end">
        <el-pagination v-model:current-page="resultPage" v-model:page-size="resultPageSize" :total="resultTotal"
          layout="total, prev, pager, next" @current-change="fetchResults" />
      </div>
    </el-dialog>

    <!-- 添加敏感词弹窗 -->
    <el-dialog v-model="showAddDialog" title="添加敏感词" width="400px">
      <el-form :model="addForm" label-width="80px">
        <el-form-item label="敏感词">
          <el-input v-model="addForm.word" placeholder="请输入敏感词" />
        </el-form-item>
        <el-form-item label="分类">
          <el-input v-model="addForm.category" placeholder="默认" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="handleAdd">确定</el-button>
      </template>
    </el-dialog>

    <!-- 批量导入弹窗 -->
    <el-dialog v-model="showImportDialog" title="批量导入敏感词" width="500px">
      <el-form :model="importForm" label-width="80px">
        <el-form-item label="分类">
          <el-input v-model="importForm.category" placeholder="默认" />
        </el-form-item>
        <el-form-item label="敏感词">
          <el-input v-model="importForm.text" type="textarea" :rows="8"
            placeholder="请输入敏感词，每行一个" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showImportDialog = false">取消</el-button>
        <el-button type="primary" @click="handleImport">导入</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getSensitiveWordPageList,
  addSensitiveWord,
  deleteSensitiveWord,
  batchImportSensitiveWords,
  startSensitiveScan,
  getScanTasks,
  getScanProgress,
  getScanResults,
  handleScanResult
} from '@/api/admin/sensitiveWord'

const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const wordList = ref([])

const scanTasks = ref([])
const pollingTimer = ref(null)

const showAddDialog = ref(false)
const addForm = ref({ word: '', category: '' })

const showImportDialog = ref(false)
const importForm = ref({ text: '', category: '' })

const showResultDialog = ref(false)
const currentTaskId = ref(null)
const resultFilter = ref(null)
const resultPage = ref(1)
const resultPageSize = ref(10)
const resultTotal = ref(0)
const scanResults = ref([])
const selectedResults = ref([])

onMounted(() => {
  fetchWordList()
  fetchScanTasks()
})

async function fetchWordList() {
  const res = await getSensitiveWordPageList({
    current: currentPage.value,
    size: pageSize.value,
    keyword: searchKeyword.value
  })
  if (res.success) {
    wordList.value = res.data
    total.value = res.total
  }
}

async function handleAdd() {
  if (!addForm.value.word.trim()) {
    ElMessage.warning('敏感词不能为空')
    return
  }
  const res = await addSensitiveWord(addForm.value)
  if (res.success) {
    ElMessage.success('添加成功')
    showAddDialog.value = false
    addForm.value = { word: '', category: '' }
    fetchWordList()
  }
}

async function handleDelete(id) {
  await ElMessageBox.confirm('确定要删除该敏感词吗？', '提示', { type: 'warning' })
  const res = await deleteSensitiveWord(id)
  if (res.success) {
    ElMessage.success('删除成功')
    fetchWordList()
  }
}

async function handleImport() {
  const words = importForm.value.text.split('\n').map(w => w.trim()).filter(w => w)
  if (words.length === 0) {
    ElMessage.warning('请输入至少一个敏感词')
    return
  }
  const res = await batchImportSensitiveWords({
    words,
    category: importForm.value.category || '默认'
  })
  if (res.success) {
    ElMessage.success(`成功导入 ${res.data} 个敏感词`)
    showImportDialog.value = false
    importForm.value = { text: '', category: '' }
    fetchWordList()
  }
}

async function handleStartScan() {
  await ElMessageBox.confirm('确定要启动全库扫描吗？扫描将在后台异步执行。', '全库扫描', { type: 'warning' })
  const res = await startSensitiveScan()
  if (res.success) {
    ElMessage.success('扫描任务已启动')
    fetchScanTasks()
    startPolling(res.data)
  }
}

async function fetchScanTasks() {
  const res = await getScanTasks()
  if (res.success) {
    scanTasks.value = res.data
    const running = res.data.find(t => t.status === 0)
    if (running) {
      startPolling(running.id)
    }
  }
}

function startPolling(taskId) {
  stopPolling()
  pollingTimer.value = setInterval(async () => {
    const res = await getScanProgress(taskId)
    if (res.success) {
      const idx = scanTasks.value.findIndex(t => t.id === taskId)
      if (idx >= 0) {
        scanTasks.value[idx] = res.data
      }
      if (res.data.status !== 0) {
        stopPolling()
        ElMessage.success('扫描任务已完成')
      }
    }
  }, 3000)
}

function stopPolling() {
  if (pollingTimer.value) {
    clearInterval(pollingTimer.value)
    pollingTimer.value = null
  }
}

function getProgress(task) {
  const totalCount = (task.totalArticles || 0) + (task.totalComments || 0)
  if (totalCount === 0) return 0
  return Math.round(((task.scannedCount || 0) / totalCount) * 100)
}

async function viewResults(taskId) {
  currentTaskId.value = taskId
  resultFilter.value = null
  resultPage.value = 1
  showResultDialog.value = true
  await fetchResults()
}

async function fetchResults() {
  const res = await getScanResults({
    current: resultPage.value,
    size: resultPageSize.value,
    taskId: currentTaskId.value,
    handled: resultFilter.value
  })
  if (res.success) {
    scanResults.value = res.data
    resultTotal.value = res.total
  }
}

function onSelectionChange(rows) {
  selectedResults.value = rows.map(r => r.id)
}

async function batchHandle(action, ids) {
  const targetIds = ids || selectedResults.value
  if (targetIds.length === 0) return

  const actionText = action === 1 ? '忽略' : '删除'
  if (!ids) {
    await ElMessageBox.confirm(`确定要${actionText}选中的 ${targetIds.length} 条记录吗？`, '提示', { type: 'warning' })
  }

  const res = await handleScanResult({ ids: targetIds, action })
  if (res.success) {
    ElMessage.success('操作成功')
    fetchResults()
  }
}
</script>
