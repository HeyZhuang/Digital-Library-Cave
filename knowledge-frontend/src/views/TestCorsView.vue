<template>
  <div class="cors-test-container">
    <div class="test-panel">
      <h2>🔗 CORS 跨域连接测试</h2>
      <div class="status-info">
        <p><strong>前端地址：</strong>{{ frontendUrl }}</p>
        <p><strong>后端地址：</strong>{{ backendUrl }}</p>
        <p><strong>测试状态：</strong>
          <span :class="testStatus.class">{{ testStatus.text }}</span>
        </p>
      </div>

      <div class="test-buttons">
        <button @click="testBasicConnection" :disabled="testing" class="test-btn primary">
          基础连接测试
        </button>
        <button @click="testPreflight" :disabled="testing" class="test-btn primary">
          预检请求测试
        </button>
        <button @click="testWithCredentials" :disabled="testing" class="test-btn primary">
          凭证请求测试
        </button>
        <button @click="testAllApis" :disabled="testing" class="test-btn success">
          完整API测试
        </button>
        <button @click="clearResults" class="test-btn secondary">
          清除结果
        </button>
      </div>

      <div class="loading" v-if="testing">
        <div class="spinner"></div>
        <span>测试进行中...</span>
      </div>

      <div class="results-container" v-if="testResults.length > 0">
        <h3>测试结果</h3>
        <div class="result-item" v-for="(result, index) in testResults" :key="index">
          <div class="result-header">
            <span :class="result.success ? 'success' : 'error'">
              {{ result.success ? '✅' : '❌' }} {{ result.name }}
            </span>
            <span class="timestamp">{{ result.timestamp }}</span>
          </div>
          <div class="result-details">
            <p><strong>请求：</strong>{{ result.method }} {{ result.url }}</p>
            <p><strong>状态：</strong>{{ result.status }}</p>
            <p><strong>响应：</strong>{{ result.message }}</p>
            <div v-if="result.headers" class="headers">
              <strong>CORS响应头：</strong>
              <pre>{{ JSON.stringify(result.headers, null, 2) }}</pre>
            </div>
            <div v-if="result.error" class="error-detail">
              <strong>错误详情：</strong>
              <pre>{{ result.error }}</pre>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from 'axios'

interface TestResult {
  name: string
  success: boolean
  method: string
  url: string
  status: string
  message: string
  timestamp: string
  headers?: any
  error?: string
}

const frontendUrl = ref(window.location.origin)
const backendUrl = ref('http://localhost:8182')
const testing = ref(false)
const testResults = ref<TestResult[]>([])

const testStatus = ref({
  text: '未测试',
  class: 'status-pending'
})

// 添加测试结果
const addResult = (result: TestResult) => {
  testResults.value.push(result)
  updateTestStatus()
}

// 更新整体测试状态
const updateTestStatus = () => {
  if (testResults.value.length === 0) {
    testStatus.value = { text: '未测试', class: 'status-pending' }
    return
  }
  
  const hasErrors = testResults.value.some(r => !r.success)
  if (hasErrors) {
    testStatus.value = { text: '发现问题', class: 'status-error' }
  } else {
    testStatus.value = { text: '连接正常', class: 'status-success' }
  }
}

// 基础连接测试
const testBasicConnection = async () => {
  testing.value = true
  
  try {
    const response = await axios.get(`${backendUrl.value}/api/articles`, {
      timeout: 5000
    })
    
    addResult({
      name: '基础GET请求',
      success: true,
      method: 'GET',
      url: '/api/articles',
      status: `${response.status} ${response.statusText}`,
      message: '请求成功',
      timestamp: new Date().toLocaleTimeString(),
      headers: extractCorsHeaders(response.headers)
    })
  } catch (error: any) {
    addResult({
      name: '基础GET请求',
      success: false,
      method: 'GET',
      url: '/api/articles',
      status: error.response?.status || 'Network Error',
      message: error.message,
      timestamp: new Date().toLocaleTimeString(),
      error: error.toString()
    })
  }
  
  testing.value = false
}

// 预检请求测试
const testPreflight = async () => {
  testing.value = true
  
  try {
    // 模拟复杂请求，触发预检
    const response = await axios.post(`${backendUrl.value}/api/articles`, {
      title: 'CORS测试文章',
      content: '这是一个CORS连接测试',
      status: 0
    }, {
      timeout: 5000,
      headers: {
        'Content-Type': 'application/json',
        'X-Requested-With': 'XMLHttpRequest'
      }
    })
    
    addResult({
      name: '预检POST请求',
      success: true,
      method: 'POST',
      url: '/api/articles',
      status: `${response.status} ${response.statusText}`,
      message: '预检请求通过',
      timestamp: new Date().toLocaleTimeString(),
      headers: extractCorsHeaders(response.headers)
    })
  } catch (error: any) {
    addResult({
      name: '预检POST请求',
      success: false,
      method: 'POST',
      url: '/api/articles',
      status: error.response?.status || 'Network Error',
      message: error.message,
      timestamp: new Date().toLocaleTimeString(),
      error: error.toString()
    })
  }
  
  testing.value = false
}

// 凭证请求测试
const testWithCredentials = async () => {
  testing.value = true
  
  try {
    const response = await axios.get(`${backendUrl.value}/api/auth/me`, {
      timeout: 5000,
      withCredentials: true,
      headers: {
        'Authorization': 'Bearer test-token'
      }
    })
    
    addResult({
      name: '凭证请求测试',
      success: true,
      method: 'GET',
      url: '/api/auth/me',
      status: `${response.status} ${response.statusText}`,
      message: '凭证请求成功',
      timestamp: new Date().toLocaleTimeString(),
      headers: extractCorsHeaders(response.headers)
    })
  } catch (error: any) {
    // 401是预期的，说明CORS工作正常
    if (error.response?.status === 401) {
      addResult({
        name: '凭证请求测试',
        success: true,
        method: 'GET',
        url: '/api/auth/me',
        status: '401 Unauthorized',
        message: 'CORS正常（预期的401错误）',
        timestamp: new Date().toLocaleTimeString(),
        headers: extractCorsHeaders(error.response.headers)
      })
    } else {
      addResult({
        name: '凭证请求测试',
        success: false,
        method: 'GET',
        url: '/api/auth/me',
        status: error.response?.status || 'Network Error',
        message: error.message,
        timestamp: new Date().toLocaleTimeString(),
        error: error.toString()
      })
    }
  }
  
  testing.value = false
}

// 完整API测试
const testAllApis = async () => {
  testing.value = true
  clearResults()
  
  // 依次执行所有测试
  await testBasicConnection()
  await new Promise(resolve => setTimeout(resolve, 1000))
  await testPreflight()
  await new Promise(resolve => setTimeout(resolve, 1000))
  await testWithCredentials()
  
  testing.value = false
}

// 提取CORS相关响应头
const extractCorsHeaders = (headers: any) => {
  const corsHeaders: any = {}
  const corsHeaderNames = [
    'access-control-allow-origin',
    'access-control-allow-methods',
    'access-control-allow-headers',
    'access-control-allow-credentials',
    'access-control-expose-headers',
    'access-control-max-age'
  ]
  
  corsHeaderNames.forEach(name => {
    if (headers[name]) {
      corsHeaders[name] = headers[name]
    }
  })
  
  return Object.keys(corsHeaders).length > 0 ? corsHeaders : null
}

// 清除测试结果
const clearResults = () => {
  testResults.value = []
  testStatus.value = { text: '未测试', class: 'status-pending' }
}

onMounted(() => {
  console.log('CORS测试组件已加载')
})
</script>

<style scoped>
.cors-test-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.test-panel {
  background: white;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.test-panel h2 {
  color: #333;
  margin-bottom: 20px;
  text-align: center;
}

.status-info {
  background: #f5f5f5;
  padding: 15px;
  border-radius: 6px;
  margin-bottom: 20px;
}

.status-info p {
  margin: 5px 0;
}

.status-pending { color: #666; }
.status-success { color: #28a745; font-weight: bold; }
.status-error { color: #dc3545; font-weight: bold; }

.test-buttons {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.test-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.test-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.test-btn.primary {
  background: #007bff;
  color: white;
}

.test-btn.primary:hover:not(:disabled) {
  background: #0056b3;
}

.test-btn.success {
  background: #28a745;
  color: white;
}

.test-btn.success:hover:not(:disabled) {
  background: #1e7e34;
}

.test-btn.secondary {
  background: #6c757d;
  color: white;
}

.test-btn.secondary:hover {
  background: #545b62;
}

.loading {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 20px;
  background: #e7f3ff;
  border-radius: 6px;
  margin-bottom: 20px;
}

.spinner {
  width: 20px;
  height: 20px;
  border: 2px solid #f3f3f3;
  border-top: 2px solid #007bff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.results-container h3 {
  color: #333;
  margin-bottom: 15px;
}

.result-item {
  border: 1px solid #ddd;
  border-radius: 6px;
  margin-bottom: 15px;
  overflow: hidden;
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 15px;
  background: #f8f9fa;
  border-bottom: 1px solid #ddd;
}

.result-header .success {
  color: #28a745;
  font-weight: bold;
}

.result-header .error {
  color: #dc3545;
  font-weight: bold;
}

.timestamp {
  font-size: 12px;
  color: #666;
}

.result-details {
  padding: 15px;
}

.result-details p {
  margin: 5px 0;
}

.headers, .error-detail {
  margin-top: 10px;
}

.headers pre, .error-detail pre {
  background: #f8f9fa;
  padding: 10px;
  border-radius: 4px;
  font-size: 12px;
  overflow-x: auto;
}

.error-detail pre {
  background: #fff5f5;
  color: #dc3545;
  border: 1px solid #f5c6cb;
}
</style> 