<template>
  <div class="main-container">
    <div class="tag-view-3d">
    <!-- Three.js 3D场景容器 -->
    <div ref="threeContainer" class="three-container"></div>
    
    <!-- 星空粒子背景 -->
    <div class="starfield-background">
      <div class="stars stars-small"></div>
      <div class="stars stars-medium"></div>
      <div class="stars stars-large"></div>
      <div class="nebula"></div>
    </div>

    <!-- HUD界面 -->
    <div class="hud-overlay">
      <!-- 左上角系统面板 -->
      <div class="hud-panel hud-top-left">
        <div class="panel-header">
          <div class="system-icon">🌌</div>
          <div class="system-info">
            <div class="hud-title">知识星图导航系统</div>
            <div class="system-status">STATUS: ONLINE</div>
          </div>
        </div>
        <div class="hud-stats">
          <div class="stat-item">
            <div class="stat-icon">📊</div>
            <div class="stat-content">
              <span class="stat-label">标签总数</span>
              <span class="stat-value">{{ totalTags }}</span>
            </div>
          </div>
          <div class="stat-item">
            <div class="stat-icon">⚡</div>
            <div class="stat-content">
              <span class="stat-label">活跃节点</span>
              <span class="stat-value">{{ activeTags }}</span>
            </div>
          </div>
          <div class="stat-item">
            <div class="stat-icon">🔗</div>
            <div class="stat-content">
              <span class="stat-label">数据链接</span>
              <span class="stat-value">{{ connections }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右上角控制面板 -->
      <div class="hud-panel hud-top-right">
        <div class="control-group">
          <button 
            class="hud-button"
            :class="{ active: viewMode === 'sphere' }"
            @click="setViewMode('sphere')"
          >
            🌐 球体模式
          </button>
          <button 
            class="hud-button"
            :class="{ active: viewMode === 'constellation' }"
            @click="setViewMode('constellation')"
          >
            ⭐ 星座模式
          </button>
          <button 
            class="hud-button"
            :class="{ active: viewMode === 'galaxy' }"
            @click="setViewMode('galaxy')"
          >
            🌌 星系模式
          </button>
        </div>
        
        <div class="render-options">
          <label class="option-item">
            <input type="checkbox" v-model="showParticles" @change="updateRenderOptions">
            <span>粒子效果</span>
            <span class="status-indicator" :class="{ active: showParticles }">
              {{ showParticles ? '开启' : '关闭' }}
            </span>
          </label>
          <label class="option-item">
            <input type="checkbox" v-model="autoRotate" @change="watchAutoRotate">
            <span>自动旋转</span>
            <span class="status-indicator" :class="{ active: autoRotate }">
              {{ autoRotate ? '开启' : '关闭' }}
            </span>
          </label>
        </div>
      </div>

      <!-- 底部详情面板 -->
      <div class="hud-panel hud-bottom" v-if="selectedTag">
        <div class="tag-details-panel">
          <div class="tag-info">
            <div class="tag-name-display">{{ selectedTag.name }}</div>
            <div class="tag-id">ID: {{ selectedTag.id }}</div>
          </div>
          
          <div class="metrics-grid">
            <div class="metric-card">
              <div class="metric-icon">📄</div>
              <div class="metric-details">
                <div class="metric-value">{{ selectedTag.articleCount }}</div>
                <div class="metric-label">相关文章</div>
              </div>
            </div>
            <div class="metric-card">
              <div class="metric-icon">🔥</div>
              <div class="metric-details">
                <div class="metric-value">{{ selectedTag.heat }}</div>
                <div class="metric-label">热度指数</div>
              </div>
            </div>
          </div>
          
          <div class="action-buttons">
            <button class="action-btn primary" @click="showArticles = true">
              查看文章 ({{ filteredArticles.length }})
            </button>
            <button class="action-btn danger" @click="clearSelection">
              清除选择
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 加载动画 -->
    <div v-if="isLoading" class="loading-overlay">
      <div class="loading-animation">
        <div class="cosmic-loader">
          <div class="loader-ring ring-1"></div>
          <div class="loader-ring ring-2"></div>
          <div class="loader-ring ring-3"></div>
          <div class="loader-core">
            <div class="core-pulse"></div>
          </div>
        </div>
        <div class="loading-text">
          <div class="text-main">初始化知识星图</div>
          <div class="text-sub">{{ loadingMessage }}</div>
        </div>
      </div>
    </div>

    <!-- 文章列表模态框 -->
    <div v-if="showArticles && selectedTag" class="articles-modal" @click="closeArticlesModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>标签 "{{ selectedTag.name }}" 的相关文章</h3>
          <button class="close-btn" @click="closeArticlesModal">×</button>
        </div>
        <div class="modal-body">
          <div class="articles-grid" v-if="filteredArticles.length > 0">
            <div
              v-for="article in filteredArticles"
              :key="article.id"
              class="article-card"
              @click="navigateToArticle(article.id!)"
            >
              <h4 class="article-title">{{ article.title }}</h4>
              <p class="article-summary">{{ article.summary || '暂无摘要' }}</p>
              <div class="article-meta">
                <span>👁️ {{ article.views || 0 }} 浏览</span>
                <span>📅 {{ formatDate(article.createdAt) }}</span>
              </div>
            </div>
          </div>
          <div v-else class="no-articles">
            <div class="no-articles-icon">📭</div>
            <div class="no-articles-text">暂无相关文章</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useTagsStore } from '../stores/tags'
import { useArticlesStore } from '../stores/articles'
import * as THREE from 'three'

interface PositionedTag {
  id: number
  name: string
  articleCount: number
  heat: number
  createdAt: string
  x: number
  y: number
  z: number
  pulse: boolean
  mesh?: THREE.Mesh
  position: THREE.Vector3
}

const router = useRouter()
const tagsStore = useTagsStore()
const articlesStore = useArticlesStore()

// 核心引用
const threeContainer = ref<HTMLElement>()

// Three.js 相关
let scene: THREE.Scene
let camera: THREE.PerspectiveCamera
let renderer: THREE.WebGLRenderer
let animationId: number
let raycaster: THREE.Raycaster
let mouse: THREE.Vector2

// 状态管理
const isLoading = ref(true)
const loadingMessage = ref('正在加载资源...')
const selectedTag = ref<PositionedTag | null>(null)
const showArticles = ref(false)
const viewMode = ref<'sphere' | 'constellation' | 'galaxy'>('sphere')

// 渲染选项
const showParticles = ref(true)
const autoRotate = ref(true)

// 标签对象数组
const tagMeshes: THREE.Mesh[] = []

// 计算属性
const totalTags = computed(() => tagsStore.totalTags)
const activeTags = computed(() => tagsStore.tags.filter(tag => (tag.articleCount || 0) > 0).length)
const connections = computed(() => positionedTags.value.length * 2)

// 初始化默认设置
const initializeSettings = () => {
  // 从localStorage读取用户偏好设置
  const savedShowParticles = localStorage.getItem('showParticles')
  const savedAutoRotate = localStorage.getItem('autoRotate')
  
  if (savedShowParticles !== null) {
    showParticles.value = JSON.parse(savedShowParticles)
  }
  
  if (savedAutoRotate !== null) {
    autoRotate.value = JSON.parse(savedAutoRotate)
  }
}

// 保存用户设置
const saveSettings = () => {
  localStorage.setItem('showParticles', JSON.stringify(showParticles.value))
  localStorage.setItem('autoRotate', JSON.stringify(autoRotate.value))
}

const positionedTags = computed<PositionedTag[]>(() => {
  const tags = tagsStore.tags
  const positions: PositionedTag[] = []
  
  tags.forEach((tag, index) => {
    let x, y, z
    
    switch (viewMode.value) {
      case 'sphere':
        const radius = 300
        const phi = Math.acos(-1 + (2 * index) / tags.length)
        const theta = Math.sqrt(tags.length * Math.PI) * phi
        x = radius * Math.cos(theta) * Math.sin(phi)
        y = radius * Math.sin(theta) * Math.sin(phi)
        z = radius * Math.cos(phi)
        break
        
      case 'constellation':
        const angle = (index / tags.length) * Math.PI * 2
        const distance = 200 + Math.random() * 200
        x = Math.cos(angle) * distance
        y = (Math.random() - 0.5) * 400
        z = Math.sin(angle) * distance
        break
        
      case 'galaxy':
        const spiralAngle = index * 0.5
        const spiralRadius = index * 5
        x = Math.cos(spiralAngle) * spiralRadius
        y = (Math.random() - 0.5) * 100
        z = Math.sin(spiralAngle) * spiralRadius
        break
        
      default:
        x = y = z = 0
    }
    
    positions.push({
      id: tag.id || index,
      name: tag.name,
      articleCount: tag.articleCount || 0,
      heat: tag.heat || 0,
      createdAt: tag.createdAt || new Date().toISOString(),
      x, y, z,
      pulse: (tag.articleCount || 0) > 10,
      position: new THREE.Vector3(x, y, z)
    })
  })
  
  return positions
})

const filteredArticles = computed(() => {
  if (!selectedTag.value) return []
  return articlesStore.articles.filter(article => 
    article.title.toLowerCase().includes(selectedTag.value!.name.toLowerCase()) ||
    (article.summary && article.summary.toLowerCase().includes(selectedTag.value!.name.toLowerCase()))
  )
})

// Three.js 初始化
const initThreeJS = () => {
  if (!threeContainer.value) return

  // 场景
  scene = new THREE.Scene()
  scene.fog = new THREE.FogExp2(0x000011, 0.0002)

  // 相机
  camera = new THREE.PerspectiveCamera(75, window.innerWidth / window.innerHeight, 0.1, 10000)
  camera.position.set(0, 0, 800)

  // 渲染器
  renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true })
  renderer.setSize(window.innerWidth, window.innerHeight)
  renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2))
  threeContainer.value.appendChild(renderer.domElement)

  // 光源
  const ambientLight = new THREE.AmbientLight(0x404040, 0.3)
  scene.add(ambientLight)

  const directionalLight = new THREE.DirectionalLight(0x00ffff, 1)
  directionalLight.position.set(500, 500, 500)
  scene.add(directionalLight)

  // 射线检测
  raycaster = new THREE.Raycaster()
  mouse = new THREE.Vector2()

  // 鼠标事件
  renderer.domElement.addEventListener('click', onMouseClick)
  renderer.domElement.addEventListener('mousemove', onMouseMove)

  // 创建粒子系统（根据当前设置）
  if (showParticles.value) {
    createParticleSystem()
  }
  
  // 创建标签3D对象
  createTagObjects()
  
  // 启动渲染循环
  animate()
}

// 创建粒子系统
const createParticleSystem = () => {
  if (!showParticles.value) return
  
  const particleCount = 3000 // 增加粒子数量
  const positions = new Float32Array(particleCount * 3)
  const colors = new Float32Array(particleCount * 3)
  const sizes = new Float32Array(particleCount)

  for (let i = 0; i < particleCount; i++) {
    positions[i * 3] = (Math.random() - 0.5) * 3000
    positions[i * 3 + 1] = (Math.random() - 0.5) * 3000
    positions[i * 3 + 2] = (Math.random() - 0.5) * 3000

    const color = new THREE.Color()
    color.setHSL(Math.random() * 0.3 + 0.4, 0.8, Math.random() * 0.6 + 0.4)
    colors[i * 3] = color.r
    colors[i * 3 + 1] = color.g
    colors[i * 3 + 2] = color.b
    
    sizes[i] = Math.random() * 4 + 1 // 增大粒子尺寸
  }

  const geometry = new THREE.BufferGeometry()
  geometry.setAttribute('position', new THREE.BufferAttribute(positions, 3))
  geometry.setAttribute('color', new THREE.BufferAttribute(colors, 3))
  geometry.setAttribute('size', new THREE.BufferAttribute(sizes, 1))

  const material = new THREE.ShaderMaterial({
    uniforms: {
      time: { value: 0 }
    },
    vertexShader: `
      attribute float size;
      attribute vec3 color;
      varying vec3 vColor;
      uniform float time;
      
      void main() {
        vColor = color;
        vec4 mvPosition = modelViewMatrix * vec4(position, 1.0);
        float pulseFactor = 1.0 + sin(time * 2.0 + position.x * 0.01) * 0.3;
        gl_PointSize = size * (300.0 / -mvPosition.z) * pulseFactor;
        gl_Position = projectionMatrix * mvPosition;
      }
    `,
    fragmentShader: `
      varying vec3 vColor;
      
      void main() {
        float distance = length(gl_PointCoord - vec2(0.5));
        if (distance > 0.5) discard;
        float alpha = 1.0 - smoothstep(0.0, 0.5, distance);
        gl_FragColor = vec4(vColor, alpha * 0.8);
      }
    `,
    blending: THREE.AdditiveBlending,
    transparent: true,
    vertexColors: true
  })

  const particles = new THREE.Points(geometry, material)
  particles.userData.isParticleSystem = true
  scene.add(particles)
}

// 清除粒子系统
const clearParticleSystem = () => {
  scene.traverse((child) => {
    if (child.userData?.isParticleSystem) {
      scene.remove(child)
      if (child instanceof THREE.Points) {
        if (child.geometry) child.geometry.dispose()
        if (child.material) child.material.dispose()
      }
    }
  })
}

// 创建标签3D对象
const createTagObjects = () => {
  // 清除现有的标签对象
  tagMeshes.forEach(mesh => scene.remove(mesh))
  tagMeshes.length = 0

  positionedTags.value.forEach((tag) => {
    // 创建发光立方体
    const geometry = new THREE.BoxGeometry(120, 80, 15) // 进一步增大立方体尺寸
    const material = new THREE.MeshPhongMaterial({
      color: new THREE.Color().setHSL((tag.heat || 0) / 100 * 0.3, 0.8, 0.6),
      emissive: new THREE.Color().setHSL((tag.heat || 0) / 100 * 0.3, 0.4, 0.2),
      transparent: true,
      opacity: 0.9
    })

    const mesh = new THREE.Mesh(geometry, material)
    mesh.position.copy(tag.position)
    mesh.userData = { tag }
    
    // 添加文字标签
    const canvas = document.createElement('canvas')
    const context = canvas.getContext('2d')!
    canvas.width = 768 // 进一步增大画布尺寸
    canvas.height = 384
    
    context.fillStyle = 'rgba(0, 0, 0, 0.8)'
    context.fillRect(0, 0, canvas.width, canvas.height)
    context.font = 'bold 56px Arial' // 大幅增大字体尺寸
    context.fillStyle = '#ffffff'
    context.textAlign = 'center'
    context.fillText(tag.name, canvas.width / 2, 180)
    context.font = 'bold 42px Arial' // 大幅增大数字字体
    context.fillStyle = '#00ffff'
    context.fillText(`${tag.articleCount} 篇`, canvas.width / 2, 260)
    
    const texture = new THREE.CanvasTexture(canvas)
    const textMaterial = new THREE.MeshBasicMaterial({ map: texture, transparent: true })
    const textGeometry = new THREE.PlaneGeometry(240, 120) // 进一步增大文字平面尺寸
    const textMesh = new THREE.Mesh(textGeometry, textMaterial)
    textMesh.position.z = 8 // 调整位置
    mesh.add(textMesh)
    
    tag.mesh = mesh
    tagMeshes.push(mesh)
    scene.add(mesh)
  })
}

// 鼠标点击事件
const onMouseClick = (event: MouseEvent) => {
  mouse.x = (event.clientX / window.innerWidth) * 2 - 1
  mouse.y = -(event.clientY / window.innerHeight) * 2 + 1

  raycaster.setFromCamera(mouse, camera)
  const intersects = raycaster.intersectObjects(tagMeshes)

  if (intersects.length > 0) {
    const selectedMesh = intersects[0].object as THREE.Mesh
    const tag = selectedMesh.userData.tag as PositionedTag
    selectTag(tag)
  }
}

// 鼠标移动事件
const onMouseMove = (event: MouseEvent) => {
  mouse.x = (event.clientX / window.innerWidth) * 2 - 1
  mouse.y = -(event.clientY / window.innerHeight) * 2 + 1

  raycaster.setFromCamera(mouse, camera)
  const intersects = raycaster.intersectObjects(tagMeshes)

  // 重置所有标签的缩放
  tagMeshes.forEach(mesh => {
    mesh.scale.setScalar(1)
  })

  // 高亮悬停的标签
  if (intersects.length > 0) {
    const hoveredMesh = intersects[0].object as THREE.Mesh
    hoveredMesh.scale.setScalar(1.2)
    document.body.style.cursor = 'pointer'
  } else {
    document.body.style.cursor = 'default'
  }
}

// 动画循环
const animate = () => {
  animationId = requestAnimationFrame(animate)
  
  const time = Date.now() * 0.001
  
  // 更新粒子动画
  scene.traverse((child) => {
    if (child.userData?.isParticleSystem && child instanceof THREE.Points) {
      const material = child.material as THREE.ShaderMaterial
      if (material.uniforms?.time) {
        material.uniforms.time.value = time
      }
      
      // 粒子旋转动画
      child.rotation.y += 0.002
      child.rotation.x += 0.001
    }
  })
  
  // 自动旋转相机
  if (autoRotate.value && !selectedTag.value) {
    const radius = 800
    camera.position.x = Math.cos(time * 0.15) * radius
    camera.position.z = Math.sin(time * 0.15) * radius
    camera.position.y = Math.sin(time * 0.1) * 200
    camera.lookAt(0, 0, 0)
  }
  
  // 标签动画
  tagMeshes.forEach((mesh, index) => {
    // 标签自身旋转
    mesh.rotation.y += 0.015
    mesh.rotation.x += 0.005
    
    const tag = mesh.userData.tag as PositionedTag
    if (tag.pulse) {
      const scale = 1 + Math.sin(time * 3 + index * 0.5) * 0.15
      if (mesh.scale.x <= 1.2) { // 只在没有悬停高亮时应用脉冲
        mesh.scale.setScalar(scale)
      }
    }
    
    // 添加轻微的浮动效果
    const originalY = tag.position.y
    mesh.position.y = originalY + Math.sin(time * 2 + index) * 10
  })
  
  renderer.render(scene, camera)
}

// 方法实现
const setViewMode = (mode: 'sphere' | 'constellation' | 'galaxy') => {
  viewMode.value = mode
  createTagObjects() // 重新创建标签对象以应用新位置
}

// 更新渲染选项
const updateRenderOptions = () => {
  console.log('更新渲染选项:', { showParticles: showParticles.value })
  
  // 清除现有粒子系统
  clearParticleSystem()
  
  // 重新创建粒子系统（如果启用）
  if (showParticles.value) {
    createParticleSystem()
    console.log('粒子系统已创建')
  } else {
    console.log('粒子系统已清除')
  }
  
  // 保存设置
  saveSettings()
}

// 监听自动旋转状态变化
const watchAutoRotate = () => {
  console.log('自动旋转状态变化:', { autoRotate: autoRotate.value })
  
  // 当自动旋转被禁用时，重置相机位置
  if (!autoRotate.value) {
    camera.position.set(0, 0, 800)
    camera.lookAt(0, 0, 0)
    console.log('相机位置已重置')
  }
  
  // 保存设置
  saveSettings()
}

const selectTag = (tag: PositionedTag) => {
  selectedTag.value = tag
  // 聚焦到选中的标签
  if (tag.mesh) {
    const targetPosition = tag.mesh.position.clone()
    targetPosition.z += 200
    // 这里可以添加相机动画到选中标签
  }
}

const clearSelection = () => {
  selectedTag.value = null
  showArticles.value = false
}

const navigateToArticle = (articleId: number) => {
  router.push(`/articles/${articleId}`)
}

const closeArticlesModal = () => {
  showArticles.value = false
}

const formatDate = (dateStr?: string): string => {
  if (!dateStr) return '未知'
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

// 窗口大小调整
const handleResize = () => {
  if (!camera || !renderer) return
  
  camera.aspect = window.innerWidth / window.innerHeight
  camera.updateProjectionMatrix()
  renderer.setSize(window.innerWidth, window.innerHeight)
}

// 生命周期
onMounted(async () => {
  // 初始化用户设置
  initializeSettings()
  
  // 模拟加载进度
  const loadingSteps = [
    '初始化3D引擎...',
    '加载标签数据...',
    '创建粒子系统...',
    '构建3D模型...',
    '启动导航系统...'
  ]
  
  for (let i = 0; i < loadingSteps.length; i++) {
    loadingMessage.value = loadingSteps[i]
    await new Promise(resolve => setTimeout(resolve, 400))
  }
  
  await tagsStore.initializeData()
  await articlesStore.initializeData()
  
  await nextTick()
  
  initThreeJS()
  
  // 监听自动旋转状态变化
  watch(autoRotate, watchAutoRotate)
  
  setTimeout(() => {
    isLoading.value = false
  }, 1000)
  
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  if (animationId) {
    cancelAnimationFrame(animationId)
  }
  
  // 清理粒子系统
  if (scene) {
    clearParticleSystem()
  }
  
  // 清理标签网格
  if (tagMeshes.length > 0) {
    tagMeshes.forEach(mesh => {
      if (mesh.geometry) mesh.geometry.dispose()
      if (mesh.material) {
        if (Array.isArray(mesh.material)) {
          mesh.material.forEach(mat => mat.dispose())
        } else {
          mesh.material.dispose()
        }
      }
    })
  }
  
  if (renderer) {
    renderer.dispose()
  }
  
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
/* 主容器 - 实现完美居中 */
.main-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  text-align: center;
  width: 100%;
}

.tag-view-3d {
  position: relative;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  background: radial-gradient(ellipse at center, #0a0a23 0%, #000000 100%);
  color: #ffffff;
}

/* 全局文字增强对比度 */
.tag-view-3d * {
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.8);
}

/* Three.js容器 */
.three-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

/* 星空背景 */
.starfield-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
}

.stars {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: 
    radial-gradient(2px 2px at 20px 30px, rgba(255,255,255,0.8), transparent),
    radial-gradient(2px 2px at 40px 70px, rgba(255,255,255,0.6), transparent),
    radial-gradient(1px 1px at 90px 40px, rgba(255,255,255,0.9), transparent);
  background-repeat: repeat;
  animation: sparkle 20s linear infinite;
}

.stars-small {
  background-size: 100px 100px;
  animation-duration: 20s;
}

.stars-medium {
  background-size: 200px 200px;
  animation-duration: 30s;
}

.stars-large {
  background-size: 400px 400px;
  animation-duration: 40s;
}

.nebula {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: 
    radial-gradient(ellipse at 20% 50%, rgba(120, 119, 198, 0.15) 0%, transparent 50%),
    radial-gradient(ellipse at 80% 20%, rgba(255, 119, 198, 0.15) 0%, transparent 50%),
    radial-gradient(ellipse at 40% 80%, rgba(119, 198, 255, 0.15) 0%, transparent 50%);
  animation: nebula-flow 60s ease-in-out infinite;
}

@keyframes sparkle {
  from { transform: translateY(0px); }
  to { transform: translateY(-100vh); }
}

@keyframes nebula-flow {
  0%, 100% { opacity: 0.3; }
  50% { opacity: 0.7; }
}

/* HUD界面 */
.hud-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 10;
  pointer-events: none;
}

.hud-panel {
  position: absolute;
  background: linear-gradient(135deg, 
    rgba(0, 255, 255, 0.1) 0%, 
    rgba(0, 100, 255, 0.1) 50%,
    rgba(120, 0, 255, 0.1) 100%);
  border: 1px solid rgba(0, 255, 255, 0.3);
  border-radius: 12px;
  backdrop-filter: blur(15px);
  padding: 20px;
  pointer-events: auto;
  box-shadow: 
    0 0 30px rgba(0, 255, 255, 0.2),
    inset 0 1px 0 rgba(255, 255, 255, 0.1);
}

.hud-top-left {
  top: 20px;
  left: 20px;
  min-width: 420px; /* 增大面板宽度 */
}

.hud-top-right {
  top: 20px;
  right: 20px;
  min-width: 380px; /* 增大面板宽度 */
}

.hud-bottom {
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  min-width: 800px; /* 增大底部面板宽度 */
  max-width: 1200px;
}

/* 系统面板 */
.panel-header {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid rgba(0, 255, 255, 0.2);
}

.system-icon {
  font-size: 32px;
  animation: rotate 10s linear infinite;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.system-info .hud-title {
  font-size: 1.8rem; /* 稍微减小主标题 */
  font-weight: bold;
  color: #00ffff;
  text-shadow: 
    0 0 15px rgba(0, 255, 255, 0.8),
    2px 2px 8px rgba(0, 0, 0, 1),
    0 0 30px rgba(0, 255, 255, 0.4);
  margin-bottom: 0.5rem;
}

.system-status {
  font-size: 1.2rem; /* 稍微减小副标题 */
  color: #00ff00;
  text-shadow: 
    0 0 12px rgba(0, 255, 0, 0.8),
    2px 2px 6px rgba(0, 0, 0, 1),
    0 0 24px rgba(0, 255, 0, 0.4);
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 0.7; }
  50% { opacity: 1; }
}

/* 统计信息 */
.hud-stats {
  display: flex;
  flex-direction: column;
  gap: 3rem; /* 增加间距 */
  margin-top: 2rem;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px;
  background: rgba(0, 0, 0, 0.3);
  border-radius: 8px;
  border: 1px solid rgba(0, 255, 255, 0.1);
}

.stat-icon {
  font-size: 20px;
  opacity: 0.8;
}

.stat-content {
  display: flex;
  flex-direction: column;
  flex: 1;
}

.stat-label {
  color: #ffffff;
  font-size: 1.1rem; /* 稍微减小标签文字 */
  margin-bottom: 0.5rem;
  text-shadow: 
    2px 2px 4px rgba(0, 0, 0, 1),
    0 0 8px rgba(255, 255, 255, 0.3);
}

.stat-value {
  color: #00ffff;
  font-weight: bold;
  font-size: 1.6rem; /* 稍微减小数字 */
  text-shadow: 
    0 0 12px rgba(0, 255, 255, 0.8),
    2px 2px 6px rgba(0, 0, 0, 1),
    0 0 24px rgba(0, 255, 255, 0.4);
}

/* 控制按钮 */
.control-group {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 20px;
}

.hud-button {
  padding: 1rem 1.5rem; /* 稍微减小按钮内边距 */
  background: linear-gradient(135deg, rgba(0, 255, 255, 0.2) 0%, rgba(0, 100, 255, 0.2) 100%);
  border: 1px solid rgba(0, 255, 255, 0.4);
  border-radius: 12px;
  color: #ffffff;
  font-size: 1.1rem; /* 稍微减小按钮文字 */
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;
  text-shadow: 
    2px 2px 4px rgba(0, 0, 0, 1),
    0 0 12px rgba(255, 255, 255, 0.5);
  display: flex;
  align-items: center;
  gap: 12px;
}

.hud-button:hover {
  background: linear-gradient(135deg, rgba(0, 255, 255, 0.3) 0%, rgba(0, 100, 255, 0.3) 100%);
  box-shadow: 0 0 20px rgba(0, 255, 255, 0.4);
  transform: translateY(-2px);
}

.hud-button.active {
  background: linear-gradient(135deg, rgba(0, 255, 255, 0.5) 0%, rgba(0, 100, 255, 0.5) 100%);
  box-shadow: 0 0 25px rgba(0, 255, 255, 0.6);
}

/* 渲染选项 */
.render-options {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.option-item {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #ffffff;
  font-size: 14px;
  font-weight: 500;
  text-shadow: 
    2px 2px 3px rgba(0, 0, 0, 1),
    0 0 8px rgba(255, 255, 255, 0.3);
}

.option-item input[type="checkbox"] {
  appearance: none;
  width: 16px;
  height: 16px;
  border: 1px solid rgba(0, 255, 255, 0.5);
  border-radius: 3px;
  background: transparent;
  position: relative;
  cursor: pointer;
}

.option-item input[type="checkbox"]:checked {
  background: rgba(0, 255, 255, 0.3);
  border-color: #00ffff;
}

.option-item input[type="checkbox"]:checked::after {
  content: '✓';
  position: absolute;
  top: -2px;
  left: 2px;
  color: #00ffff;
  font-size: 12px;
}

.status-indicator {
  margin-left: auto;
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 4px;
  background: rgba(255, 255, 255, 0.1);
  color: #ffffff;
  transition: all 0.3s ease;
}

.status-indicator.active {
  background: rgba(0, 255, 255, 0.3);
  color: #00ffff;
  text-shadow: 0 0 8px rgba(0, 255, 255, 0.8);
}

/* 标签详情面板 */
.tag-details-panel {
  display: flex;
  align-items: center;
  gap: 30px;
}

.tag-info {
  display: flex;
  flex-direction: column;
}

.tag-name-display {
  font-size: 2.2rem; /* 稍微减小标签名称 */
  font-weight: bold;
  color: #00ffff;
  text-shadow: 
    0 0 20px rgba(0, 255, 255, 1),
    3px 3px 8px rgba(0, 0, 0, 1),
    0 0 40px rgba(0, 255, 255, 0.6);
  margin-bottom: 1rem;
}

.tag-id {
  font-size: 12px;
  color: #ffffff;
  text-shadow: 
    2px 2px 3px rgba(0, 0, 0, 1),
    0 0 6px rgba(255, 255, 255, 0.4);
}

.metrics-grid {
  display: flex;
  gap: 20px;
}

.metric-card {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px;
  background: rgba(0, 0, 0, 0.3);
  border-radius: 8px;
  border: 1px solid rgba(0, 255, 255, 0.2);
}

.metric-icon {
  font-size: 20px;
  opacity: 0.8;
}

.metric-details {
  display: flex;
  flex-direction: column;
}

.metric-value {
  font-size: 1.8rem; /* 稍微减小度量数值 */
  font-weight: bold;
  color: #00ffff;
  text-shadow: 
    0 0 12px rgba(0, 255, 255, 0.8),
    2px 2px 6px rgba(0, 0, 0, 1),
    0 0 24px rgba(0, 255, 255, 0.4);
}

.metric-label {
  font-size: 0.9rem; /* 稍微减小度量标签 */
  color: #ffffff;
  text-shadow: 
    2px 2px 3px rgba(0, 0, 0, 1),
    0 0 6px rgba(255, 255, 255, 0.3);
}

.action-buttons {
  display: flex;
  gap: 10px;
}

.action-btn {
  padding: 10px 16px;
  border-radius: 6px;
  border: none;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.action-btn.primary {
  background: linear-gradient(135deg, #00ffff 0%, #0080ff 100%);
  color: #000;
}

.action-btn.primary:hover {
  box-shadow: 0 0 20px rgba(0, 255, 255, 0.6);
  transform: translateY(-2px);
}

.action-btn.danger {
  background: linear-gradient(135deg, #ff4444 0%, #cc0000 100%);
  color: #fff;
}

.action-btn.danger:hover {
  box-shadow: 0 0 20px rgba(255, 68, 68, 0.6);
  transform: translateY(-2px);
}

/* 加载动画 */
.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 50;
}

.loading-animation {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 30px;
}

.cosmic-loader {
  position: relative;
  width: 120px;
  height: 120px;
}

.loader-ring {
  position: absolute;
  border-radius: 50%;
  border: 2px solid transparent;
}

.ring-1 {
  width: 120px;
  height: 120px;
  border-top: 2px solid #00ffff;
  animation: spin 2s linear infinite;
}

.ring-2 {
  width: 90px;
  height: 90px;
  top: 15px;
  left: 15px;
  border-right: 2px solid #ff00ff;
  animation: spin 1.5s linear infinite reverse;
}

.ring-3 {
  width: 60px;
  height: 60px;
  top: 30px;
  left: 30px;
  border-bottom: 2px solid #ffff00;
  animation: spin 1s linear infinite;
}

.loader-core {
  position: absolute;
  top: 45px;
  left: 45px;
  width: 30px;
  height: 30px;
  background: radial-gradient(circle, #00ffff 0%, #0080ff 100%);
  border-radius: 50%;
}

.core-pulse {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: inherit;
  animation: pulse-core 2s ease-in-out infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

@keyframes pulse-core {
  0%, 100% { transform: scale(1); opacity: 1; }
  50% { transform: scale(1.2); opacity: 0.7; }
}

.loading-text .text-main {
  font-size: 2.2rem; /* 稍微减小加载主文字 */
  font-weight: bold;
  color: #00ffff;
  text-shadow: 
    0 0 25px rgba(0, 255, 255, 1),
    3px 3px 8px rgba(0, 0, 0, 1),
    0 0 50px rgba(0, 255, 255, 0.6);
  margin-bottom: 1.5rem;
  text-align: center;
}

.loading-text .text-sub {
  font-size: 1.3rem; /* 稍微减小加载副文字 */
  color: #ffffff;
  text-align: center;
  text-shadow: 
    2px 2px 4px rgba(0, 0, 0, 1),
    0 0 12px rgba(255, 255, 255, 0.5);
}

/* 文章模态框 */
.articles-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
  backdrop-filter: blur(10px);
}

.modal-content {
  background: linear-gradient(135deg, rgba(0, 20, 40, 0.95) 0%, rgba(0, 40, 80, 0.95) 100%);
  border: 1px solid rgba(0, 255, 255, 0.3);
  border-radius: 16px;
  width: 90%;
  max-width: 900px;
  max-height: 80vh;
  overflow: hidden;
  box-shadow: 0 25px 50px rgba(0, 255, 255, 0.3);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 25px 30px;
  border-bottom: 1px solid rgba(0, 255, 255, 0.2);
  background: rgba(0, 0, 0, 0.3);
}

.modal-header h3 {
  color: #00ffff;
  font-size: 22px;
  font-weight: bold;
  margin: 0;
  text-shadow: 
    0 0 15px rgba(0, 255, 255, 1),
    2px 2px 6px rgba(0, 0, 0, 1),
    0 0 30px rgba(0, 255, 255, 0.5);
}

.close-btn {
  background: none;
  border: none;
  color: #ffffff;
  font-size: 28px;
  cursor: pointer;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.3s ease;
}

.close-btn:hover {
  background: rgba(255, 255, 255, 0.1);
  color: #ff4444;
}

.modal-body {
  padding: 30px;
  max-height: 60vh;
  overflow-y: auto;
}

.articles-grid {
  display: grid;
  gap: 20px;
}

.article-card {
  padding: 20px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.article-card:hover {
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(0, 255, 255, 0.3);
  transform: translateY(-5px);
  box-shadow: 0 10px 25px rgba(0, 255, 255, 0.2);
}

.article-title {
  color: #ffffff;
  margin: 0 0 12px 0;
  font-size: 18px;
  font-weight: 600;
  text-shadow: 
    2px 2px 4px rgba(0, 0, 0, 1),
    0 0 8px rgba(255, 255, 255, 0.3);
}

.article-summary {
  color: #ffffff;
  margin: 0 0 15px 0;
  font-size: 14px;
  line-height: 1.5;
  text-shadow: 
    1px 1px 3px rgba(0, 0, 0, 0.8),
    0 0 6px rgba(255, 255, 255, 0.2);
}

.article-meta {
  display: flex;
  gap: 20px;
  font-size: 12px;
  color: #ffffff;
  text-shadow: 
    1px 1px 2px rgba(0, 0, 0, 0.8),
    0 0 4px rgba(255, 255, 255, 0.2);
}

.no-articles {
  text-align: center;
  padding: 60px 20px;
}

.no-articles-icon {
  font-size: 64px;
  margin-bottom: 20px;
  opacity: 0.5;
}

.no-articles-text {
  font-size: 18px;
  color: #ffffff;
  text-shadow: 
    2px 2px 4px rgba(0, 0, 0, 1),
    0 0 8px rgba(255, 255, 255, 0.3);
}

/* 响应式设计 */
@media (max-width: 768px) {
  /* 移动端字体尺寸调整 */
  .system-info .hud-title {
    font-size: 1.5rem; /* 移动端缩小主标题 */
    text-shadow: 
      0 0 10px rgba(0, 255, 255, 1),
      2px 2px 6px rgba(0, 0, 0, 1),
      0 0 20px rgba(0, 255, 255, 0.4);
  }
  
  .system-status {
    font-size: 1rem; /* 移动端缩小副标题 */
    text-shadow: 
      0 0 8px rgba(0, 255, 0, 1),
      2px 2px 4px rgba(0, 0, 0, 1),
      0 0 16px rgba(0, 255, 0, 0.4);
  }
  
  .stat-label {
    font-size: 0.9rem; /* 移动端标签文字 */
    color: #ffffff;
    text-shadow: 
      1px 1px 3px rgba(0, 0, 0, 1),
      0 0 6px rgba(255, 255, 255, 0.3);
  }
  
  .stat-value {
    font-size: 1.3rem; /* 移动端数字 */
    text-shadow: 
      0 0 8px rgba(0, 255, 255, 1),
      2px 2px 4px rgba(0, 0, 0, 1),
      0 0 16px rgba(0, 255, 255, 0.4);
  }
  
  .hud-button {
    font-size: 0.9rem; /* 移动端按钮文字 */
    padding: 0.8rem 1.2rem; /* 移动端按钮内边距 */
    font-weight: bold;
    text-shadow: 
      1px 1px 3px rgba(0, 0, 0, 1),
      0 0 8px rgba(255, 255, 255, 0.5);
  }
  
  .tag-name-display {
    font-size: 1.8rem; /* 移动端标签名称 */
    text-shadow: 
      0 0 15px rgba(0, 255, 255, 1),
      2px 2px 6px rgba(0, 0, 0, 1),
      0 0 30px rgba(0, 255, 255, 0.6);
  }
  
  .metric-value {
    font-size: 1.4rem; /* 移动端度量数值 */
    text-shadow: 
      0 0 8px rgba(0, 255, 255, 1),
      2px 2px 4px rgba(0, 0, 0, 1),
      0 0 16px rgba(0, 255, 255, 0.4);
  }
  
  .metric-label {
    font-size: 0.8rem; /* 移动端度量标签 */
    color: #ffffff;
    text-shadow: 
      1px 1px 2px rgba(0, 0, 0, 1),
      0 0 4px rgba(255, 255, 255, 0.3);
  }
  
  .loading-text .text-main {
    font-size: 1.8rem; /* 移动端加载主文字 */
    text-shadow: 
      0 0 20px rgba(0, 255, 255, 1),
      2px 2px 6px rgba(0, 0, 0, 1),
      0 0 40px rgba(0, 255, 255, 0.6);
  }
  
  .loading-text .text-sub {
    font-size: 1.1rem; /* 移动端加载副文字 */
    color: #ffffff;
    text-shadow: 
      1px 1px 3px rgba(0, 0, 0, 1),
      0 0 8px rgba(255, 255, 255, 0.5);
  }
  
  /* 布局调整 */
  .hud-stats {
    gap: 1.5rem; /* 移动端减少间距 */
  }
  
  .hud-panel {
    padding: 15px;
    border-radius: 8px;
  }
  
  .hud-top-left,
  .hud-top-right {
    position: relative;
    top: 10px;
    left: 10px;
    right: 10px;
    margin-bottom: 10px;
    min-width: auto;
  }
  
  .hud-bottom {
    bottom: 10px;
    left: 10px;
    right: 10px;
    transform: none;
    min-width: auto;
  }
  
  .tag-details-panel {
    flex-direction: column;
    gap: 15px;
    text-align: center;
  }
  
  .metrics-grid {
    flex-direction: column; /* 移动端垂直排列 */
    align-items: center;
    gap: 1rem;
  }
  
  .modal-content {
    width: 95%;
    margin: 20px;
  }
}

/* 超大屏幕优化 */
@media (min-width: 1920px) {
  .system-info .hud-title {
    font-size: 2.2rem; /* 超大屏幕适度增大 */
    text-shadow: 
      0 0 20px rgba(0, 255, 255, 1),
      3px 3px 10px rgba(0, 0, 0, 1),
      0 0 40px rgba(0, 255, 255, 0.4);
  }
  
  .system-status {
    font-size: 1.5rem;
    text-shadow: 
      0 0 15px rgba(0, 255, 0, 1),
      3px 3px 8px rgba(0, 0, 0, 1),
      0 0 30px rgba(0, 255, 0, 0.4);
  }
  
  .stat-label {
    font-size: 1.3rem;
    color: #ffffff;
    text-shadow: 
      2px 2px 5px rgba(0, 0, 0, 1),
      0 0 10px rgba(255, 255, 255, 0.3);
  }
  
  .stat-value {
    font-size: 2rem;
    text-shadow: 
      0 0 15px rgba(0, 255, 255, 1),
      3px 3px 8px rgba(0, 0, 0, 1),
      0 0 30px rgba(0, 255, 255, 0.4);
  }
  
  .tag-name-display {
    font-size: 2.8rem;
    text-shadow: 
      0 0 25px rgba(0, 255, 255, 1),
      4px 4px 12px rgba(0, 0, 0, 1),
      0 0 50px rgba(0, 255, 255, 0.6);
  }
  
  .metric-value {
    font-size: 2.2rem;
    text-shadow: 
      0 0 15px rgba(0, 255, 255, 1),
      3px 3px 8px rgba(0, 0, 0, 1),
      0 0 30px rgba(0, 255, 255, 0.4);
  }
}
</style> 